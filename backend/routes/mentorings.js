const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

async function createNotificationOnce(
  connection,
  userId,
  type,
  title,
  message,
) {
  const [existing] = await connection.query(
    `
            SELECT id
            FROM notifications
            WHERE user_id = ?
            AND type = ?
            AND title = ?
            AND message = ?
            LIMIT 1
            `,
    [userId, type, title, message],
  );

  if (existing.length > 0) {
    return;
  }

  await connection.query(
    `
        INSERT INTO notifications
        (
            user_id,
            type,
            title,
            message,
            is_read
        )
        VALUES (?, ?, ?, ?, 0)
        `,
    [userId, type, title, message],
  );
}

function mentoringRangeText(
  courseName,
  fromSession,
  toSession,
  includeFinalProject,
) {
  if (includeFinalProject === 1) {
    return `${courseName} · Session ${fromSession} to Final project`;
  }

  return `${courseName} · Sessions ${fromSession}-${toSession}`;
}

async function syncMentoringEndedNotifications(connection = db) {
  const [mentorings] = await connection.query(
    `
            SELECT
                m.id,
                m.student_id,
                m.mentor_id,
                m.from_session,
                COALESCE(
                    m.to_session,
                    m.from_session
                ) AS to_session,
                m.include_final_project,
                m.status,
                c.name AS course_name,
                student.name AS student_name,
                mentor.name AS mentor_name
            FROM mentorings m
            JOIN courses c
                ON c.id = m.course_id
            JOIN users student
                ON student.id = m.student_id
            JOIN users mentor
                ON mentor.id = m.mentor_id
            WHERE m.status IN (
                'completed',
                'expired'
            )
            AND m.ended_at IS NOT NULL
            `,
  );

  for (const mentoring of mentorings) {
    const range = mentoringRangeText(
      mentoring.course_name,
      mentoring.from_session,
      mentoring.to_session,
      mentoring.include_final_project,
    );

    await createNotificationOnce(
      connection,
      mentoring.student_id,
      "mentoring_ended",
      `Mentoring with ${mentoring.mentor_name} ended`,
      `${range} · The chat is now read-only.`,
    );

    await createNotificationOnce(
      connection,
      mentoring.mentor_id,
      "mentoring_ended",
      `Mentoring with ${mentoring.student_name} ended`,
      `${range} · The chat is now read-only.`,
    );
  }
}

async function syncMentoringStatuses(connection = db) {
  await connection.query(
    `
        UPDATE mentorings
        SET status = 'expired',
            ended_at = NOW()
        WHERE status = 'active'
        AND deadline_at < NOW()
        `,
  );

  await connection.query(
    `
        UPDATE mentorings m
        SET
            m.status = 'completed',
            m.ended_at = NOW()
        WHERE m.status = 'active'
        AND COALESCE(
            m.include_final_project,
            0
        ) = 0
        AND EXISTS (
            SELECT 1
            FROM sessions s
            JOIN session_progress sp
                ON sp.session_id = s.id
                AND sp.student_id = m.student_id
            WHERE s.course_id = m.course_id
            AND s.session_number = COALESCE(
                m.to_session,
                m.from_session
            )
            AND sp.status = 'passed'
        )
        `,
  );

  await connection.query(
    `
        UPDATE mentorings m
        SET
            m.status = 'completed',
            m.ended_at = NOW()
        WHERE m.status = 'active'
        AND m.include_final_project = 1
        AND EXISTS (
            SELECT 1
            FROM case_study_reviews csr
            WHERE csr.mentoring_id = m.id
            AND csr.status IN (
                'approved',
                'not_approved'
            )
        )
        `,
  );

  await syncMentoringEndedNotifications(connection);
}

router.get("/student/mentors", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Student access only",
      });
    }

    await syncMentoringStatuses();

    const [mentors] = await db.query(
      `
                    SELECT
                        u.id,
                        u.name,
                        u.campus,
                        u.profile_image,
                        u.cover_image,
                        COUNT(DISTINCT m.id) AS active_mentees
                    FROM users u
                    LEFT JOIN mentorings m
                        ON m.mentor_id = u.id
                        AND m.status = 'active'
                    WHERE u.role = 'mentor'
                    GROUP BY
                        u.id,
                        u.name,
                        u.campus,
                        u.profile_image,
                        u.cover_image
                    HAVING active_mentees < 3
                    ORDER BY u.name ASC
                    `,
    );

    const mentorIds = mentors.map((mentor) => mentor.id);

    if (mentorIds.length === 0) {
      return res.status(200).json({
        mentors: [],
      });
    }

    const placeholders = mentorIds.map(() => "?").join(",");

    const [specializations] = await db.query(
      `
                    SELECT
                        ms.mentor_id,
                        c.id AS course_id,
                        c.name AS course_name
                    FROM mentor_specializations ms
                    JOIN courses c
                        ON c.id = ms.course_id
                    WHERE ms.mentor_id IN (${placeholders})
                    ORDER BY c.name ASC
                    `,
      mentorIds,
    );

    const result = mentors.map((mentor) => ({
      id: mentor.id,

      name: mentor.name,

      campus: mentor.campus,

      profile_image: mentor.profile_image,

      cover_image: mentor.cover_image,

      active_mentees: Number(mentor.active_mentees),

      specializations: specializations
        .filter((item) => item.mentor_id === mentor.id)
        .map((item) => ({
          course_id: item.course_id,

          course_name: item.course_name,
        })),
    }));

    return res.status(200).json({
      mentors: result,
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to load mentors",
    });
  }
});

router.post("/student/requests", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Student access only",
      });
    }

    await syncMentoringStatuses();

    const studentId = req.user.id;

    const {
      mentor_id,
      course_id,
      from_session,
      to_session,
      include_final_project,
    } = req.body;

    const mentorId = Number(mentor_id);

    const courseId = Number(course_id);

    const fromSession = Number(from_session);

    const toSession = Number(to_session);

    const includeFinalProject = include_final_project ? 1 : 0;

    if (!mentorId || !courseId || !fromSession || !toSession) {
      return res.status(400).json({
        message: "Mentor, course and session range are required",
      });
    }

    if (
      fromSession < 1 ||
      fromSession > 5 ||
      toSession < 1 ||
      toSession > 5 ||
      toSession < fromSession
    ) {
      return res.status(400).json({
        message: "Invalid session range",
      });
    }

    const [mentorRows] = await db.query(
      `
                    SELECT
                        id,
                        name
                    FROM users
                    WHERE id = ?
                    AND role = 'mentor'
                    `,
      [mentorId],
    );

    if (mentorRows.length === 0) {
      return res.status(404).json({
        message: "Mentor not found",
      });
    }

    const [courseRows] = await db.query(
      `
                    SELECT
                        id,
                        name
                    FROM courses
                    WHERE id = ?
                    `,
      [courseId],
    );

    if (courseRows.length === 0) {
      return res.status(404).json({
        message: "Course not found",
      });
    }

    const [specializationRows] = await db.query(
      `
                    SELECT id
                    FROM mentor_specializations
                    WHERE mentor_id = ?
                    AND course_id = ?
                    `,
      [mentorId, courseId],
    );

    if (specializationRows.length === 0) {
      return res.status(400).json({
        message: "Mentor does not specialize in this course",
      });
    }

    const [activeMentorings] = await db.query(
      `
                    SELECT COUNT(*) AS total
                    FROM mentorings
                    WHERE mentor_id = ?
                    AND status = 'active'
                    `,
      [mentorId],
    );

    if (Number(activeMentorings[0].total) >= 3) {
      return res.status(400).json({
        message: "Mentor already has 3 active mentees",
      });
    }

    const [existingPending] = await db.query(
      `
                    SELECT id
                    FROM mentoring_requests
                    WHERE student_id = ?
                    AND mentor_id = ?
                    AND course_id = ?
                    AND status = 'pending'
                    LIMIT 1
                    `,
      [studentId, mentorId, courseId],
    );

    if (existingPending.length > 0) {
      return res.status(400).json({
        message:
          "You already have a pending request for this mentor and course",
      });
    }

    const [existingActive] = await db.query(
      `
                    SELECT id
                    FROM mentorings
                    WHERE student_id = ?
                    AND mentor_id = ?
                    AND course_id = ?
                    AND status = 'active'
                    LIMIT 1
                    `,
      [studentId, mentorId, courseId],
    );

    if (existingActive.length > 0) {
      return res.status(400).json({
        message: "You already have active mentoring with this mentor",
      });
    }

    const [result] = await db.query(
      `
                    INSERT INTO mentoring_requests
                    (
                        student_id,
                        mentor_id,
                        course_id,
                        from_session,
                        to_session,
                        include_final_project,
                        status
                    )
                    VALUES
                    (
                        ?, ?, ?, ?, ?, ?, 'pending'
                    )
                    `,
      [
        studentId,
        mentorId,
        courseId,
        fromSession,
        toSession,
        includeFinalProject,
      ],
    );

    const [studentRows] = await db.query(
      `
                    SELECT name
                    FROM users
                    WHERE id = ?
                    LIMIT 1
                    `,
      [studentId],
    );

    const studentName = studentRows[0]?.name || "Student";

    const courseName = courseRows[0]?.name || "Course";

    const range = mentoringRangeText(
      courseName,
      fromSession,
      toSession,
      includeFinalProject,
    );

    await createNotificationOnce(
      db,
      mentorId,
      "mentoring_request",
      `New request from ${studentName}`,
      range,
    );

    return res.status(201).json({
      message: "Mentoring request sent",

      request_id: result.insertId,
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to send mentoring request",
    });
  }
});

router.get("/student/dashboard", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Student access only",
      });
    }

    await syncMentoringStatuses();

    const studentId = req.user.id;

    const [mentorings] = await db.query(
      `
                    SELECT
                        m.id AS mentoring_id,
                        m.mentor_id,
                        u.name AS mentor_name,
                        u.campus AS mentor_campus,
                        u.profile_image AS mentor_profile_image,
                        m.course_id,
                        c.name AS course_name,
                        m.from_session AS start_session,
                        COALESCE(
                            m.to_session,
                            m.from_session
                        ) AS end_session,
                        m.include_final_project,
                        m.start_at,
                        m.deadline_at,
                        m.ended_at,
                        m.status
                    FROM mentorings m
                    JOIN users u
                        ON u.id = m.mentor_id
                    JOIN courses c
                        ON c.id = m.course_id
                    WHERE m.student_id = ?
                    ORDER BY
                        CASE
                            WHEN m.status = 'active'
                                THEN 0
                            WHEN m.status = 'completed'
                                THEN 1
                            ELSE 2
                        END,
                        m.start_at DESC
                    `,
      [studentId],
    );

    return res.status(200).json({
      mentorings,
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to load student mentorings",
    });
  }
});

router.get("/mentor/dashboard", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "mentor") {
      return res.status(403).json({
        message: "Mentor access only",
      });
    }

    await syncMentoringStatuses();

    const mentorId = req.user.id;

    const [activeMentorings] = await db.query(
      `
                    SELECT
                        m.id AS mentoring_id,
                        m.student_id,
                        u.name AS student_name,
                        u.campus AS student_campus,
                        u.profile_image AS student_profile_image,
                        m.course_id,
                        c.name AS course_name,
                        m.from_session AS start_session,
                        COALESCE(
                            m.to_session,
                            m.from_session
                        ) AS end_session,
                        m.include_final_project,
                        m.start_at AS start_date,
                        m.deadline_at,
                        m.ended_at,
                        m.status
                    FROM mentorings m
                    JOIN users u
                        ON u.id = m.student_id
                    JOIN courses c
                        ON c.id = m.course_id
                    WHERE m.mentor_id = ?
                    AND m.status = 'active'
                    ORDER BY
                        m.deadline_at ASC
                    `,
      [mentorId],
    );

    const [pendingRequests] = await db.query(
      `
                    SELECT
                        mr.id AS request_id,
                        mr.student_id,
                        u.name AS student_name,
                        u.campus AS student_campus,
                        u.profile_image AS student_profile_image,
                        mr.course_id,
                        c.name AS course_name,
                        mr.from_session AS start_session,
                        COALESCE(
                            mr.to_session,
                            mr.from_session
                        ) AS end_session,
                        mr.include_final_project,
                        mr.status,
                        mr.created_at AS requested_at
                    FROM mentoring_requests mr
                    JOIN users u
                        ON u.id = mr.student_id
                    JOIN courses c
                        ON c.id = mr.course_id
                    WHERE mr.mentor_id = ?
                    AND mr.status = 'pending'
                    ORDER BY
                        mr.created_at DESC
                    `,
      [mentorId],
    );

    return res.status(200).json({
      active_count: activeMentorings.length,

      max_active: 3,

      active_mentorings: activeMentorings,

      pending_requests: pendingRequests,
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to load mentor dashboard",
    });
  }
});

router.patch(
  "/requests/:requestId/respond",
  authMiddleware,
  async (req, res) => {
    const connection = await db.getConnection();

    try {
      if (req.user.role !== "mentor") {
        return res.status(403).json({
          message: "Mentor access only",
        });
      }

      await syncMentoringStatuses(connection);

      const mentorId = req.user.id;

      const requestId = Number(req.params.requestId);

      const { action } = req.body;

      if (!["accept", "reject"].includes(action)) {
        return res.status(400).json({
          message: "Action must be accept or reject",
        });
      }

      await connection.beginTransaction();

      const [requests] = await connection.query(
        `
                    SELECT *
                    FROM mentoring_requests
                    WHERE id = ?
                    AND mentor_id = ?
                    FOR UPDATE
                    `,
        [requestId, mentorId],
      );

      if (requests.length === 0) {
        await connection.rollback();

        return res.status(404).json({
          message: "Mentoring request not found",
        });
      }

      const request = requests[0];

      if (request.status !== "pending") {
        await connection.rollback();

        return res.status(400).json({
          message: "This request has already been responded to",
        });
      }

      const [mentorRows] = await connection.query(
        `
                    SELECT name
                    FROM users
                    WHERE id = ?
                    LIMIT 1
                    `,
        [mentorId],
      );

      const [courseRows] = await connection.query(
        `
                    SELECT name
                    FROM courses
                    WHERE id = ?
                    LIMIT 1
                    `,
        [request.course_id],
      );

      const mentorName = mentorRows[0]?.name || "Mentor";

      const courseName = courseRows[0]?.name || "Course";

      const requestToSession = request.to_session ?? request.from_session;

      const requestRange = mentoringRangeText(
        courseName,
        request.from_session,
        requestToSession,
        request.include_final_project ?? 0,
      );

      if (action === "reject") {
        await connection.query(
          `
                    UPDATE mentoring_requests
                    SET status = 'declined'
                    WHERE id = ?
                    `,
          [requestId],
        );

        await createNotificationOnce(
          connection,
          request.student_id,
          "request_declined",
          `${mentorName} declined your request`,
          `${requestRange} · You can request another mentor.`,
        );

        await connection.commit();

        return res.status(200).json({
          message: "Mentoring request declined",
        });
      }

      const [activeRows] = await connection.query(
        `
                    SELECT COUNT(*) AS total
                    FROM mentorings
                    WHERE mentor_id = ?
                    AND status = 'active'
                    `,
        [mentorId],
      );

      if (Number(activeRows[0].total) >= 3) {
        await connection.rollback();

        return res.status(400).json({
          message: "You already have 3 active mentees",
        });
      }

      const fromSession = request.from_session;

      const toSession = request.to_session ?? request.from_session;

      const sessionCount = toSession - fromSession + 1;

      let mentoringDays = sessionCount * 7;

      if (request.include_final_project === 1) {
        mentoringDays += 7;
      }

      const deadline = new Date();

      deadline.setDate(deadline.getDate() + mentoringDays);

      await connection.query(
        `
                UPDATE mentoring_requests
                SET status = 'accepted'
                WHERE id = ?
                `,
        [requestId],
      );

      const [result] = await connection.query(
        `
                    INSERT INTO mentorings
                    (
                        request_id,
                        student_id,
                        mentor_id,
                        course_id,
                        from_session,
                        to_session,
                        include_final_project,
                        start_at,
                        deadline_at,
                        status
                    )
                    VALUES
                    (
                        ?, ?, ?, ?, ?, ?, ?,
                        NOW(),
                        ?,
                        'active'
                    )
                    `,
        [
          request.id,
          request.student_id,
          mentorId,
          request.course_id,
          fromSession,
          toSession,
          request.include_final_project ?? 0,
          deadline,
        ],
      );

      await createNotificationOnce(
        connection,
        request.student_id,
        "request_accepted",
        `${mentorName} accepted your request`,
        `${requestRange} · You can chat now.`,
      );

      await connection.commit();

      return res.status(200).json({
        message: "Mentoring request accepted",

        mentoring_id: result.insertId,

        deadline_at: deadline,
      });
    } catch (error) {
      await connection.rollback();

      console.error(error);

      return res.status(500).json({
        message: "Failed to respond to mentoring request",
      });
    } finally {
      connection.release();
    }
  },
);

router.get("/:mentoringId/status", authMiddleware, async (req, res) => {
  try {
    await syncMentoringStatuses();

    const mentoringId = Number(req.params.mentoringId);

    const [rows] = await db.query(
      `
                    SELECT
                        id,
                        student_id,
                        mentor_id,
                        course_id,
                        from_session,
                        COALESCE(
                            to_session,
                            from_session
                        ) AS to_session,
                        include_final_project,
                        start_at,
                        deadline_at,
                        ended_at,
                        status
                    FROM mentorings
                    WHERE id = ?
                    AND (
                        student_id = ?
                        OR mentor_id = ?
                    )
                    LIMIT 1
                    `,
      [mentoringId, req.user.id, req.user.id],
    );

    if (rows.length === 0) {
      return res.status(404).json({
        message: "Mentoring not found",
      });
    }

    const mentoring = rows[0];

    return res.status(200).json({
      mentoring,
      can_chat: mentoring.status === "active",
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to load mentoring status",
    });
  }
});

router.get("/mentor/calendar", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "mentor") {
      return res.status(403).json({
        message: "Only mentors can access mentor calendar",
      });
    }

    await syncMentoringStatuses();

    const [mentorings] = await db.query(
      `
                    SELECT
                        m.id AS mentoring_id,
                        m.student_id,
                        u.name AS student_name,
                        u.campus AS student_campus,
                        u.profile_image AS student_profile_image,
                        m.course_id,
                        c.name AS course_name,
                        m.from_session AS start_session,
                        COALESCE(
                            m.to_session,
                            m.from_session
                        ) AS end_session,
                        m.include_final_project,
                        m.start_at,
                        m.deadline_at,
                        m.ended_at,
                        m.status
                    FROM mentorings m
                    JOIN users u
                        ON u.id = m.student_id
                    JOIN courses c
                        ON c.id = m.course_id
                    WHERE m.mentor_id = ?
                    ORDER BY
                        CASE
                            WHEN m.status = 'active'
                                THEN 0
                            WHEN m.status = 'completed'
                                THEN 1
                            ELSE 2
                        END,
                        m.deadline_at ASC
                    `,
      [req.user.id],
    );

    return res.status(200).json({
      mentorings,
    });
  } catch (error) {
    console.error("Mentor calendar error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

module.exports = router;
