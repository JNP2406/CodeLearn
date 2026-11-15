const express = require("express");
const db = require("../db");

const router = express.Router();

/*
|--------------------------------------------------------------------------
| GET ALL COURSES
|--------------------------------------------------------------------------
| GET /api/courses
|
| Mengambil semua course.
*/
router.get("/", async (req, res) => {
    try {
        const [courses] = await db.query(`
            SELECT
                id,
                name,
                category,
                description,
                total_sessions,
                image_url
            FROM courses
            ORDER BY
                CASE
                    WHEN category = 'basic' THEN 1
                    ELSE 2
                END,
                id ASC
        `);

        res.status(200).json({
            message: "Courses retrieved successfully",
            courses: courses
        });

    } catch (error) {
        console.error("GET COURSES ERROR:", error);

        res.status(500).json({
            message: "Failed to retrieve courses"
        });
    }
});


/*
|--------------------------------------------------------------------------
| GET COURSE DETAIL
|--------------------------------------------------------------------------
| GET /api/courses/:id
|
| Mengambil satu course beserta seluruh session-nya.
*/
router.get("/:id", async (req, res) => {
    try {
        const courseId = req.params.id;

        const [courses] = await db.query(
            `
            SELECT
                id,
                name,
                category,
                description,
                total_sessions,
                image_url
            FROM courses
            WHERE id = ?
            `,
            [courseId]
        );

        if (courses.length === 0) {
            return res.status(404).json({
                message: "Course not found"
            });
        }

        const course = courses[0];

        const [sessions] = await db.query(
            `
            SELECT
                id,
                course_id,
                session_number,
                title,
                description
            FROM sessions
            WHERE course_id = ?
            ORDER BY session_number ASC
            `,
            [courseId]
        );

        res.status(200).json({
            message: "Course retrieved successfully",
            course: {
                ...course,
                sessions: sessions
            }
        });

    } catch (error) {
        console.error("GET COURSE DETAIL ERROR:", error);

        res.status(500).json({
            message: "Failed to retrieve course"
        });
    }
});

module.exports = router;