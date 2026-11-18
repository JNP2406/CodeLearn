const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

router.get("/course/:courseId", authMiddleware, async (req, res) => {
  try {
    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Student access only",
      });
    }

    const studentId = req.user.id;
    const courseId = Number(req.params.courseId);

    const [caseStudies] = await db.query(
      `
                SELECT
                    id,
                    course_id,
                    title,
                    scenario,
                    task_description
                FROM case_studies
                WHERE course_id = ?
                LIMIT 1
                `,
      [courseId],
    );

    if (caseStudies.length === 0) {
      return res.status(404).json({
        message: "Case study not found",
      });
    }

    const [mentorings] = await db.query(
      `
                SELECT
                    m.id,
                    m.mentor_id,
                    u.name AS mentor_name,
                    m.course_id,
                    m.from_session,
                    COALESCE(
                        m.to_session,
                        m.from_session
                    ) AS to_session,
                    m.include_final_project,
                    m.start_at,
                    m.deadline_at,
                    m.ended_at,
                    m.status
                FROM mentorings m
                JOIN users u
                    ON u.id = m.mentor_id
                WHERE m.student_id = ?
                AND m.course_id = ?
                AND m.include_final_project = 1
                ORDER BY m.start_at DESC
                LIMIT 1
                `,
      [studentId, courseId],
    );

    let mentoring = null;
    let review = null;

    if (mentorings.length > 0) {
      mentoring = mentorings[0];

      const [reviews] = await db.query(
        `
                    SELECT
                        id,
                        mentoring_id,
                        case_study_id,
                        status,
                        reviewed_at
                    FROM case_study_reviews
                    WHERE mentoring_id = ?
                    LIMIT 1
                    `,
        [mentoring.id],
      );

      if (reviews.length > 0) {
        review = reviews[0];
      } else if (mentoring.status === "active") {
        const [result] = await db.query(
          `
                        INSERT INTO case_study_reviews
                        (
                            mentoring_id,
                            case_study_id,
                            status
                        )
                        VALUES (
                            ?, ?, 'under_review'
                        )
                        `,
          [mentoring.id, caseStudies[0].id],
        );

        review = {
          id: result.insertId,
          mentoring_id: mentoring.id,
          case_study_id: caseStudies[0].id,
          status: "under_review",
          reviewed_at: null,
        };
      }
    }

    return res.status(200).json({
      case_study: caseStudies[0],
      mentoring,
      review,
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Failed to load case study",
    });
  }
});

router.get(
  "/mentor/mentoring/:mentoringId",
  authMiddleware,
  async (req, res) => {
    try {
      if (req.user.role !== "mentor") {
        return res.status(403).json({
          message: "Mentor access only",
        });
      }

      const mentorId = req.user.id;
      const mentoringId = Number(req.params.mentoringId);

      const [mentorings] = await db.query(
        `
                SELECT
                    m.id AS mentoring_id,
                    m.student_id,
                    u.name AS student_name,
                    u.campus AS student_campus,
                    u.profile_image AS student_profile_image,
                    m.mentor_id,
                    m.course_id,
                    c.name AS course_name,
                    m.from_session,
                    COALESCE(
                        m.to_session,
                        m.from_session
                    ) AS to_session,
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
                WHERE m.id = ?
                AND m.mentor_id = ?
                LIMIT 1
                `,
        [mentoringId, mentorId],
      );

      if (mentorings.length === 0) {
        return res.status(404).json({
          message: "Mentoring not found",
        });
      }

      const mentoring = mentorings[0];

      if (mentoring.include_final_project !== 1) {
        return res.status(400).json({
          message: "This mentoring does not include a final project",
        });
      }

      const [caseStudies] = await db.query(
        `
                SELECT
                    id,
                    course_id,
                    title,
                    scenario,
                    task_description
                FROM case_studies
                WHERE course_id = ?
                LIMIT 1
                `,
        [mentoring.course_id],
      );

      if (caseStudies.length === 0) {
        return res.status(404).json({
          message: "Case study not found",
        });
      }

      const caseStudy = caseStudies[0];

      const [reviews] = await db.query(
        `
                SELECT
                    id,
                    mentoring_id,
                    case_study_id,
                    status,
                    reviewed_at
                FROM case_study_reviews
                WHERE mentoring_id = ?
                LIMIT 1
                `,
        [mentoringId],
      );

      let review;

      if (reviews.length > 0) {
        review = reviews[0];
      } else {
        const [result] = await db.query(
          `
                    INSERT INTO case_study_reviews
                    (
                        mentoring_id,
                        case_study_id,
                        status
                    )
                    VALUES (
                        ?, ?, 'under_review'
                    )
                    `,
          [mentoringId, caseStudy.id],
        );

        review = {
          id: result.insertId,
          mentoring_id: mentoringId,
          case_study_id: caseStudy.id,
          status: "under_review",
          reviewed_at: null,
        };
      }

      return res.status(200).json({
        mentoring,
        case_study: caseStudy,
        review,
      });
    } catch (error) {
      console.error(error);

      return res.status(500).json({
        message: "Failed to load case study review",
      });
    }
  },
);

router.patch("/reviews/:reviewId", authMiddleware, async (req, res) => {
  const connection = await db.getConnection();

  try {
    if (req.user.role !== "mentor") {
      return res.status(403).json({
        message: "Mentor access only",
      });
    }

    const mentorId = req.user.id;
    const reviewId = Number(req.params.reviewId);

    const { status } = req.body;

    if (!["approved", "not_approved"].includes(status)) {
      return res.status(400).json({
        message: "Status must be approved or not_approved",
      });
    }

    await connection.beginTransaction();

    const [reviews] = await connection.query(
      `
                SELECT
                    csr.id,
                    csr.status,
                    csr.mentoring_id,
                    m.mentor_id,
                    m.student_id,
                    c.name AS course_name
                FROM case_study_reviews csr
                JOIN mentorings m
                    ON m.id =
                    csr.mentoring_id
                JOIN courses c
                    ON c.id =
                    m.course_id
                WHERE csr.id = ?
                AND m.mentor_id = ?
                LIMIT 1
                FOR UPDATE
                `,
      [reviewId, mentorId],
    );

    if (reviews.length === 0) {
      await connection.rollback();

      return res.status(404).json({
        message: "Review not found",
      });
    }

    const review = reviews[0];

    if (review.status === "approved" || review.status === "not_approved") {
      await connection.rollback();

      return res.status(400).json({
        message: "This review has already been finalized",
      });
    }

    await connection.query(
      `
                UPDATE case_study_reviews
                SET
                    status = ?,
                    reviewed_at = NOW()
                WHERE id = ?
                `,
      [status, reviewId],
    );

    await connection.query(
      `
                UPDATE mentorings
                SET
                    status = 'completed',
                    ended_at = NOW()
                WHERE id = ?
                AND status = 'active'
                `,
      [review.mentoring_id],
    );

    const notificationTitle =
      status === "approved"
        ? "Final project approved"
        : "Final project not approved";

    const notificationMessage =
      status === "approved"
        ? `${review.course_name} · Your case study was approved. You can claim your certificate now.`
        : `${review.course_name} · You can request a new mentor if you want to retry.`;

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
      [
        review.student_id,
        status === "approved" ? "case_approved" : "case_not_approved",
        notificationTitle,
        notificationMessage,
      ],
    );

    await connection.commit();

    return res.status(200).json({
      message:
        status === "approved"
          ? "Case study approved"
          : "Case study not approved",
      status,
    });
  } catch (error) {
    await connection.rollback();

    console.error(error);

    return res.status(500).json({
      message: "Failed to update case study review",
    });
  } finally {
    connection.release();
  }
});

module.exports = router;
