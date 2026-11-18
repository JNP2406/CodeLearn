const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

async function resetExpiredSessions(connection, studentId, courseId) {
  const [expiredSessions] = await connection.query(
    `
            SELECT
                sp.id,
                sp.session_id
            FROM session_progress sp
            JOIN sessions s
                ON s.id = sp.session_id
            WHERE sp.student_id = ?
            AND s.course_id = ?
            AND sp.status = 'active'
            AND sp.deadline_at < NOW()
            `,
    [studentId, courseId],
  );

  for (const session of expiredSessions) {
    await connection.query(
      `
            DELETE FROM quiz_answers
            WHERE student_id = ?
            AND session_id = ?
            `,
      [studentId, session.session_id],
    );

    await connection.query(
      `
            UPDATE session_progress
            SET
                started_at = NOW(),
                deadline_at = DATE_ADD(
                    NOW(),
                    INTERVAL 7 DAY
                ),
                completed_at = NULL,
                status = 'active',
                quiz_score = NULL
            WHERE id = ?
            `,
      [session.id],
    );
  }
}

router.post("/take-course/:courseId", authMiddleware, async (req, res) => {
  const connection = await db.getConnection();

  try {
    const studentId = req.user.id;

    const courseId = parseInt(req.params.courseId);

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can take courses",
      });
    }

    const [courses] = await connection.query(
      `
                    SELECT
                        id,
                        name,
                        total_sessions
                    FROM courses
                    WHERE id = ?
                    `,
      [courseId],
    );

    if (courses.length === 0) {
      return res.status(404).json({
        message: "Course not found",
      });
    }

    const [existingProgress] = await connection.query(
      `
                    SELECT
                        id,
                        current_session,
                        started_at
                    FROM student_course_progress
                    WHERE student_id = ?
                    AND course_id = ?
                    `,
      [studentId, courseId],
    );

    if (existingProgress.length > 0) {
      return res.status(409).json({
        message: "Course already taken",
        progress: existingProgress[0],
      });
    }

    const [sessions] = await connection.query(
      `
                    SELECT
                        id,
                        session_number,
                        title
                    FROM sessions
                    WHERE course_id = ?
                    AND session_number = 1
                    LIMIT 1
                    `,
      [courseId],
    );

    if (sessions.length === 0) {
      return res.status(404).json({
        message: "Session 1 not found",
      });
    }

    const firstSession = sessions[0];

    await connection.beginTransaction();

    const [courseProgressResult] = await connection.query(
      `
                    INSERT INTO student_course_progress
                    (
                        student_id,
                        course_id,
                        current_session,
                        started_at
                    )
                    VALUES
                    (
                        ?,
                        ?,
                        1,
                        NOW()
                    )
                    `,
      [studentId, courseId],
    );

    const [sessionProgressResult] = await connection.query(
      `
                    INSERT INTO session_progress
                    (
                        student_id,
                        session_id,
                        started_at,
                        deadline_at,
                        completed_at,
                        status,
                        quiz_score
                    )
                    VALUES
                    (
                        ?,
                        ?,
                        NOW(),
                        DATE_ADD(
                            NOW(),
                            INTERVAL 7 DAY
                        ),
                        NULL,
                        'active',
                        NULL
                    )
                    `,
      [studentId, firstSession.id],
    );

    await connection.commit();

    const [createdProgress] = await connection.query(
      `
                    SELECT
                        sp.id,
                        sp.student_id,
                        sp.session_id,
                        sp.started_at,
                        sp.deadline_at,
                        sp.completed_at,
                        sp.status,
                        sp.quiz_score,
                        s.session_number,
                        s.title
                    FROM session_progress sp
                    JOIN sessions s
                        ON s.id =
                        sp.session_id
                    WHERE sp.id = ?
                    `,
      [sessionProgressResult.insertId],
    );

    return res.status(201).json({
      message: "Course started successfully",
      course_progress_id: courseProgressResult.insertId,
      course: {
        id: courses[0].id,
        name: courses[0].name,
      },
      current_session: createdProgress[0],
    });
  } catch (error) {
    await connection.rollback();

    console.error("Take course error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  } finally {
    connection.release();
  }
});

router.get("/course/:courseId", authMiddleware, async (req, res) => {
  const connection = await db.getConnection();

  try {
    const studentId = req.user.id;

    const courseId = parseInt(req.params.courseId);

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can access course progress",
      });
    }

    const [courseProgress] = await connection.query(
      `
                    SELECT
                        scp.id,
                        scp.student_id,
                        scp.course_id,
                        scp.current_session,
                        scp.started_at,
                        c.name AS course_name,
                        c.total_sessions
                    FROM student_course_progress scp
                    JOIN courses c
                        ON c.id =
                        scp.course_id
                    WHERE scp.student_id = ?
                    AND scp.course_id = ?
                    `,
      [studentId, courseId],
    );

    if (courseProgress.length === 0) {
      return res.status(404).json({
        message: "Course progress not found",
      });
    }

    await connection.beginTransaction();

    await resetExpiredSessions(connection, studentId, courseId);

    await connection.commit();

    const [sessionProgress] = await connection.query(
      `
                    SELECT
                        sp.id,
                        sp.session_id,
                        s.session_number,
                        s.title,
                        sp.started_at,
                        sp.deadline_at,
                        sp.completed_at,
                        sp.status,
                        sp.quiz_score
                    FROM session_progress sp
                    JOIN sessions s
                        ON s.id =
                        sp.session_id
                    WHERE sp.student_id = ?
                    AND s.course_id = ?
                    ORDER BY
                        s.session_number ASC
                    `,
      [studentId, courseId],
    );

    return res.status(200).json({
      message: "Course progress retrieved successfully",
      course_progress: courseProgress[0],
      sessions: sessionProgress,
    });
  } catch (error) {
    await connection.rollback();

    console.error("Get course progress error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  } finally {
    connection.release();
  }
});

module.exports = router;
