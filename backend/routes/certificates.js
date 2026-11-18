const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

router.post("/course/:courseId/claim", authMiddleware, async (req, res) => {
  try {
    const studentId = req.user.id;
    const courseId = parseInt(req.params.courseId);

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can claim certificates",
      });
    }

    const [approvedReviews] = await db.query(
      `
                SELECT
                    csr.id AS review_id,
                    m.mentor_id,
                    m.student_id,
                    m.course_id
                FROM case_study_reviews csr
                JOIN mentorings m
                    ON m.id = csr.mentoring_id
                WHERE m.student_id = ?
                AND m.course_id = ?
                AND csr.status = 'approved'
                ORDER BY csr.id DESC
                LIMIT 1
                `,
      [studentId, courseId],
    );

    if (approvedReviews.length === 0) {
      return res.status(403).json({
        message: "Case study has not been approved",
      });
    }

    const approvedReview = approvedReviews[0];

    const [existingCertificates] = await db.query(
      `
                    SELECT
                        id,
                        student_id,
                        mentor_id,
                        course_id,
                        review_id,
                        certificate_id,
                        pdf_url,
                        claimed_at
                    FROM student_certificates
                    WHERE review_id = ?
                    LIMIT 1
                    `,
      [approvedReview.review_id],
    );

    if (existingCertificates.length > 0) {
      return res.status(200).json({
        message: "Certificate already claimed",
        certificate: existingCertificates[0],
      });
    }

    const certificateId = `CL-${courseId}-${studentId}-${Date.now()}`;

    const pdfUrl = "/certificates/codelearn-certificate.pdf";

    const [result] = await db.query(
      `
                INSERT INTO student_certificates
                (
                    student_id,
                    mentor_id,
                    course_id,
                    review_id,
                    certificate_id,
                    pdf_url,
                    claimed_at
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    NOW()
                )
                `,
      [
        studentId,
        approvedReview.mentor_id,
        courseId,
        approvedReview.review_id,
        certificateId,
        pdfUrl,
      ],
    );

    const [certificates] = await db.query(
      `
                    SELECT
                        id,
                        student_id,
                        mentor_id,
                        course_id,
                        review_id,
                        certificate_id,
                        pdf_url,
                        claimed_at
                    FROM student_certificates
                    WHERE id = ?
                    `,
      [result.insertId],
    );

    return res.status(201).json({
      message: "Certificate claimed successfully",
      certificate: certificates[0],
    });
  } catch (error) {
    console.error("Claim certificate error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

router.get("/my-certificates", authMiddleware, async (req, res) => {
  try {
    const studentId = req.user.id;

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can access certificates",
      });
    }

    const [certificates] = await db.query(
      `
                    SELECT
                        sc.id,
                        sc.course_id,
                        c.name AS course_name,
                        sc.certificate_id,
                        sc.pdf_url,
                        sc.claimed_at
                    FROM student_certificates sc
                    JOIN courses c
                        ON c.id = sc.course_id
                    WHERE sc.student_id = ?
                    ORDER BY sc.claimed_at DESC
                    `,
      [studentId],
    );

    return res.status(200).json({
      message: "Certificates retrieved successfully",
      certificates,
    });
  } catch (error) {
    console.error("Get certificates error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

module.exports = router;
