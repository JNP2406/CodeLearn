const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

router.get("/session/:sessionId", authMiddleware, async (req, res) => {
  try {
    const sessionId = parseInt(req.params.sessionId);

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can access quizzes",
      });
    }

    const [sessions] = await db.query(
      `
                SELECT
                    id,
                    course_id,
                    session_number,
                    title
                FROM sessions
                WHERE id = ?
                `,
      [sessionId],
    );

    if (sessions.length === 0) {
      return res.status(404).json({
        message: "Session not found",
      });
    }

    const session = sessions[0];

    const [users] = await db.query(
      `
                SELECT id, is_demo
                FROM users
                WHERE id = ?
                `,
      [req.user.id],
    );

    if (users.length === 0) {
      return res.status(404).json({
        message: "User not found",
      });
    }

    const isDemo = Boolean(users[0].is_demo);

    const [progress] = await db.query(
      `
                SELECT
                    id,
                    status,
                    deadline_at,
                    completed_at
                FROM session_progress
                WHERE student_id = ?
                AND session_id = ?
                `,
      [req.user.id, sessionId],
    );

    if (!isDemo && progress.length === 0) {
      return res.status(403).json({
        message: "Session is still locked",
      });
    }

    if (progress.length > 0 && progress[0].status === "passed") {
      return res.status(409).json({
        message: "Session already passed",
      });
    }

    const [questions] = await db.query(
      `
                SELECT
                    id,
                    question
                FROM quiz_questions
                WHERE session_id = ?
                ORDER BY id ASC
                `,
      [sessionId],
    );

    const quiz = [];

    for (const question of questions) {
      const [options] = await db.query(
        `
                    SELECT
                        id,
                        option_text
                    FROM quiz_options
                    WHERE question_id = ?
                    ORDER BY id ASC
                    `,
        [question.id],
      );

      quiz.push({
        id: question.id,
        question: question.question,
        options,
      });
    }

    return res.status(200).json({
      message: "Quiz retrieved successfully",
      session: {
        id: session.id,
        course_id: session.course_id,
        session_number: session.session_number,
        title: session.title,
      },
      total_questions: quiz.length,
      questions: quiz,
    });
  } catch (error) {
    console.error("Get quiz error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

router.get("/session/:sessionId/result", authMiddleware, async (req, res) => {
  try {
    const studentId = req.user.id;
    const sessionId = parseInt(req.params.sessionId);

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can access quiz results",
      });
    }

    const [progressRows] = await db.query(
      `
                SELECT
                    status,
                    quiz_score,
                    completed_at
                FROM session_progress
                WHERE student_id = ?
                AND session_id = ?
                `,
      [studentId, sessionId],
    );

    if (progressRows.length === 0) {
      return res.status(404).json({
        message: "Quiz result not found",
      });
    }

    const progress = progressRows[0];

    if (progress.status !== "passed") {
      return res.status(403).json({
        message: "Quiz has not been passed yet",
      });
    }

    const [sessions] = await db.query(
      `
                SELECT
                    id,
                    course_id,
                    session_number,
                    title
                FROM sessions
                WHERE id = ?
                `,
      [sessionId],
    );

    if (sessions.length === 0) {
      return res.status(404).json({
        message: "Session not found",
      });
    }

    const [questions] = await db.query(
      `
                SELECT
                    id,
                    question
                FROM quiz_questions
                WHERE session_id = ?
                ORDER BY id ASC
                `,
      [sessionId],
    );

    const reviewQuestions = [];
    let correctAnswers = 0;

    for (const question of questions) {
      const [options] = await db.query(
        `
                    SELECT
                        id,
                        option_text,
                        is_correct
                    FROM quiz_options
                    WHERE question_id = ?
                    ORDER BY id ASC
                    `,
        [question.id],
      );

      const [answerRows] = await db.query(
        `
                    SELECT option_id
                    FROM quiz_answers
                    WHERE student_id = ?
                    AND session_id = ?
                    AND question_id = ?
                    LIMIT 1
                    `,
        [studentId, sessionId, question.id],
      );

      const selectedOptionId =
        answerRows.length > 0 ? answerRows[0].option_id : null;

      const correctOption = options.find((option) =>
        Boolean(option.is_correct),
      );

      if (correctOption && selectedOptionId === correctOption.id) {
        correctAnswers++;
      }

      reviewQuestions.push({
        id: question.id,
        question: question.question,
        selected_option_id: selectedOptionId,
        correct_option_id: correctOption ? correctOption.id : null,
        options: options.map((option) => ({
          id: option.id,
          option_text: option.option_text,
        })),
      });
    }

    return res.status(200).json({
      message: "Quiz result retrieved successfully",
      session: sessions[0],
      score: progress.quiz_score,
      completed_at: progress.completed_at,
      total_questions: questions.length,
      correct_answers: correctAnswers,
      passed: true,
      questions: reviewQuestions,
    });
  } catch (error) {
    console.error("Get quiz result error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

router.post("/session/:sessionId/submit", authMiddleware, async (req, res) => {
  const connection = await db.getConnection();

  try {
    const studentId = req.user.id;
    const sessionId = parseInt(req.params.sessionId);
    const answers = req.body.answers;

    if (req.user.role !== "student") {
      return res.status(403).json({
        message: "Only students can submit quizzes",
      });
    }

    if (!Array.isArray(answers)) {
      return res.status(400).json({
        message: "Answers are required",
      });
    }

    const [users] = await connection.query(
      `
                SELECT
                    id,
                    is_demo
                FROM users
                WHERE id = ?
                `,
      [studentId],
    );

    if (users.length === 0) {
      return res.status(404).json({
        message: "User not found",
      });
    }

    const isDemo = Boolean(users[0].is_demo);

    const [sessions] = await connection.query(
      `
                SELECT
                    id,
                    course_id,
                    session_number,
                    title
                FROM sessions
                WHERE id = ?
                `,
      [sessionId],
    );

    if (sessions.length === 0) {
      return res.status(404).json({
        message: "Session not found",
      });
    }

    const session = sessions[0];

    const [questions] = await connection.query(
      `
                SELECT id
                FROM quiz_questions
                WHERE session_id = ?
                ORDER BY id ASC
                `,
      [sessionId],
    );

    if (questions.length === 0) {
      return res.status(404).json({
        message: "Quiz questions not found",
      });
    }

    const [progressRows] = await connection.query(
      `
                SELECT
                    id,
                    status,
                    deadline_at,
                    completed_at
                FROM session_progress
                WHERE student_id = ?
                AND session_id = ?
                `,
      [studentId, sessionId],
    );

    if (progressRows.length > 0 && progressRows[0].status === "passed") {
      return res.status(409).json({
        message: "Session already passed",
      });
    }

    if (!isDemo && progressRows.length === 0) {
      return res.status(403).json({
        message: "Session is still locked",
      });
    }

    let realCorrectAnswers = 0;

    for (const question of questions) {
      const answer = answers.find((item) => item.question_id === question.id);

      if (!answer) {
        continue;
      }

      const [validOption] = await connection.query(
        `
                    SELECT
                        id,
                        is_correct
                    FROM quiz_options
                    WHERE id = ?
                    AND question_id = ?
                    LIMIT 1
                    `,
        [answer.option_id, question.id],
      );

      if (validOption.length === 0) {
        continue;
      }

      await connection.query(
        `
                    INSERT INTO quiz_answers
                    (
                        student_id,
                        session_id,
                        question_id,
                        option_id
                    )
                    VALUES (?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        option_id = VALUES(option_id)
                    `,
        [studentId, sessionId, question.id, answer.option_id],
      );

      if (Boolean(validOption[0].is_correct)) {
        realCorrectAnswers++;
      }
    }

    const totalQuestions = questions.length;

    const correctAnswers = isDemo ? totalQuestions : realCorrectAnswers;

    const score = Math.round((correctAnswers / totalQuestions) * 100);

    const passed = isDemo || correctAnswers >= 8;

    if (!passed) {
      await connection.query(
        `
                    UPDATE session_progress
                    SET quiz_score = ?
                    WHERE student_id = ?
                    AND session_id = ?
                    `,
        [score, studentId, sessionId],
      );

      return res.status(200).json({
        message: "Quiz completed",
        total_questions: totalQuestions,
        correct_answers: correctAnswers,
        score,
        passed: false,
        next_session_unlocked: false,
        next_session: null,
      });
    }

    await connection.beginTransaction();

    const [existingProgress] = await connection.query(
      `
                    SELECT id
                    FROM session_progress
                    WHERE student_id = ?
                    AND session_id = ?
                    `,
      [studentId, sessionId],
    );

    if (existingProgress.length === 0) {
      await connection.query(
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
                        NOW(),
                        'passed',
                        ?
                    )
                    `,
        [studentId, sessionId, score],
      );
    } else {
      await connection.query(
        `
                    UPDATE session_progress
                    SET
                        status = 'passed',
                        quiz_score = ?,
                        completed_at = NOW()
                    WHERE student_id = ?
                    AND session_id = ?
                    `,
        [score, studentId, sessionId],
      );
    }

    const [nextSessions] = await connection.query(
      `
                    SELECT
                        id,
                        session_number,
                        title
                    FROM sessions
                    WHERE course_id = ?
                    AND session_number = ?
                    LIMIT 1
                    `,
      [session.course_id, session.session_number + 1],
    );

    let nextSession = null;
    let nextSessionUnlocked = false;

    if (nextSessions.length > 0) {
      nextSession = nextSessions[0];

      const [existingNext] = await connection.query(
        `
                        SELECT id
                        FROM session_progress
                        WHERE student_id = ?
                        AND session_id = ?
                        `,
        [studentId, nextSession.id],
      );

      if (existingNext.length === 0) {
        await connection.query(
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
          [studentId, nextSession.id],
        );
      }

      await connection.query(
        `
                    UPDATE student_course_progress
                    SET current_session = ?
                    WHERE student_id = ?
                    AND course_id = ?
                    `,
        [nextSession.session_number, studentId, session.course_id],
      );

      nextSessionUnlocked = true;
    }

    await connection.commit();

    return res.status(200).json({
      message: "Quiz passed",
      total_questions: totalQuestions,
      correct_answers: correctAnswers,
      score,
      passed: true,
      next_session_unlocked: nextSessionUnlocked,
      next_session: nextSession,
    });
  } catch (error) {
    await connection.rollback();

    console.error("Submit quiz error:", error);

    return res.status(500).json({
      message: "Internal server error",
    });
  } finally {
    connection.release();
  }
});

module.exports = router;
