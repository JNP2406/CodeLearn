const express = require("express");
const db = require("../db");
const authMiddleware = require("../middleware/authMiddleware");

const router = express.Router();

async function createNotificationOnce(userId, type, title, message) {
  const [existing] = await db.query(
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

  await db.query(
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

async function syncStudentSessionReminders(userId) {
  const [sessions] = await db.query(
    `
        SELECT
            s.session_number,
            s.title AS session_title,
            c.name AS course_name,
            DATE_FORMAT(
                sp.deadline_at,
                '%d %b %Y'
            ) AS deadline_label
        FROM session_progress sp
        JOIN sessions s
            ON s.id = sp.session_id
        JOIN courses c
            ON c.id = s.course_id
        WHERE sp.student_id = ?
        AND sp.status = 'active'
        AND DATE(sp.deadline_at) =
            DATE_ADD(
                CURDATE(),
                INTERVAL 1 DAY
            )
        `,
    [userId],
  );

  for (const session of sessions) {
    const title = `Session ${session.session_number} of ${session.course_name} ends tomorrow`;

    const message = `${session.session_title} · Pass the test by ${session.deadline_label} or your progress resets.`;

    await createNotificationOnce(userId, "session_deadline", title, message);
  }
}

async function syncUserNotifications(user) {
  if (user.role === "student") {
    await syncStudentSessionReminders(user.id);
  }
}

router.get("/", authMiddleware, async (req, res) => {
  try {
    await syncUserNotifications(req.user);

    const [notifications] = await db.query(
      `
                    SELECT
                        id,
                        user_id,
                        type,
                        title,
                        message,
                        is_read,
                        created_at
                    FROM notifications
                    WHERE user_id = ?
                    ORDER BY created_at DESC
                    `,
      [req.user.id],
    );

    return res.status(200).json({
      notifications,
    });
  } catch (error) {
    console.error("Get notifications error:", error);

    return res.status(500).json({
      message: "Failed to load notifications",
    });
  }
});

router.get("/unread-count", authMiddleware, async (req, res) => {
  try {
    await syncUserNotifications(req.user);

    const [rows] = await db.query(
      `
                    SELECT
                        COUNT(*) AS unread_count
                    FROM notifications
                    WHERE user_id = ?
                    AND is_read = 0
                    `,
      [req.user.id],
    );

    return res.status(200).json({
      unread_count: Number(rows[0].unread_count),
    });
  } catch (error) {
    console.error("Unread count error:", error);

    return res.status(500).json({
      message: "Failed to load unread count",
    });
  }
});

router.patch("/read-all", authMiddleware, async (req, res) => {
  try {
    await db.query(
      `
                UPDATE notifications
                SET is_read = 1
                WHERE user_id = ?
                AND is_read = 0
                `,
      [req.user.id],
    );

    return res.status(200).json({
      message: "All notifications marked as read",
    });
  } catch (error) {
    console.error("Read all notifications error:", error);

    return res.status(500).json({
      message: "Failed to update notifications",
    });
  }
});

router.patch("/:notificationId/read", authMiddleware, async (req, res) => {
  try {
    const notificationId = Number(req.params.notificationId);

    const [rows] = await db.query(
      `
                    SELECT id
                    FROM notifications
                    WHERE id = ?
                    AND user_id = ?
                    LIMIT 1
                    `,
      [notificationId, req.user.id],
    );

    if (rows.length === 0) {
      return res.status(404).json({
        message: "Notification not found",
      });
    }

    await db.query(
      `
                UPDATE notifications
                SET is_read = 1
                WHERE id = ?
                AND user_id = ?
                `,
      [notificationId, req.user.id],
    );

    return res.status(200).json({
      message: "Notification marked as read",
    });
  } catch (error) {
    console.error("Read notification error:", error);

    return res.status(500).json({
      message: "Failed to update notification",
    });
  }
});

module.exports = router;
