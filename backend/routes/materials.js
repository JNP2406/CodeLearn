const express = require("express");
const db = require("../db");

const router = express.Router();

router.get("/session/:sessionId", async (req, res) => {
    try {
        const sessionId = req.params.sessionId;

        const [sessions] = await db.query(
            `
            SELECT id, course_id, session_number, title
            FROM sessions
            WHERE id = ?
            `,
            [sessionId]
        );

        if (sessions.length === 0) {
            return res.status(404).json({
                message: "Session not found"
            });
        }

        const [materials] = await db.query(
            `
            SELECT
                id,
                session_id,
                title,
                type,
                url
            FROM materials
            WHERE session_id = ?
            ORDER BY id ASC
            `,
            [sessionId]
        );

        return res.status(200).json({
            message: "Materials retrieved successfully",
            session: sessions[0],
            materials
        });

    } catch (error) {
        console.error("Get materials error:", error);

        return res.status(500).json({
            message: "Internal server error"
        });
    }
});

module.exports = router;