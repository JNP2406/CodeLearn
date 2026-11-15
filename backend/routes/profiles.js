const express = require("express")
const multer = require("multer")
const path = require("path")
const fs = require("fs")
const db = require("../db")
const authMiddleware = require("../middleware/authMiddleware")

const router = express.Router()

const profileImageDirectory = path.join(
    __dirname,
    "..",
    "uploads",
    "profiles",
    "profile-images"
)

const coverImageDirectory = path.join(
    __dirname,
    "..",
    "uploads",
    "profiles",
    "cover-images"
)

fs.mkdirSync(profileImageDirectory, { recursive: true })
fs.mkdirSync(coverImageDirectory, { recursive: true })

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        if (file.fieldname === "profile_image") {
            cb(null, profileImageDirectory)
            return
        }

        cb(null, coverImageDirectory)
    },

    filename: (req, file, cb) => {
        const extension = path.extname(file.originalname)
        const filename = `${req.user.id}-${Date.now()}${extension}`
        cb(null, filename)
    }
})

const upload = multer({
    storage,
    limits: {
        fileSize: 5 * 1024 * 1024
    },
    fileFilter: (req, file, cb) => {
        if (
            file.mimetype === "image/jpeg" ||
            file.mimetype === "image/png" ||
            file.mimetype === "image/webp"
        ) {
            cb(null, true)
            return
        }

        cb(new Error("Only JPG, PNG, and WEBP images are allowed"))
    }
})

router.get("/me", authMiddleware, async (req, res) => {
    try {
        const [users] = await db.query(
            `
            SELECT
                id,
                email,
                role,
                name,
                campus,
                profile_image,
                cover_image,
                is_demo
            FROM users
            WHERE id = ?
            LIMIT 1
            `,
            [req.user.id]
        )

        if (users.length === 0) {
            return res.status(404).json({
                message: "User not found"
            })
        }

        const user = users[0]

        let specializations = []

        if (user.role === "mentor") {
            const [rows] = await db.query(
                `
                SELECT
                    ms.course_id,
                    c.name AS course_name
                FROM mentor_specializations ms
                JOIN courses c
                    ON c.id = ms.course_id
                WHERE ms.mentor_id = ?
                ORDER BY c.id
                `,
                [req.user.id]
            )

            specializations = rows
        }

        res.json({
            user,
            specializations
        })
    } catch (error) {
        console.error(error)

        res.status(500).json({
            message: "Failed to load profile"
        })
    }
})

router.patch("/me", authMiddleware, async (req, res) => {
    try {
        const { name, campus } = req.body

        if (!name || !name.trim()) {
            return res.status(400).json({
                message: "Name is required"
            })
        }

        await db.query(
            `
            UPDATE users
            SET
                name = ?,
                campus = ?
            WHERE id = ?
            `,
            [
                name.trim(),
                campus ? campus.trim() : null,
                req.user.id
            ]
        )

        res.json({
            message: "Profile updated successfully"
        })
    } catch (error) {
        console.error(error)

        res.status(500).json({
            message: "Failed to update profile"
        })
    }
})

router.post(
    "/me/profile-image",
    authMiddleware,
    upload.single("profile_image"),
    async (req, res) => {
        try {
            if (!req.file) {
                return res.status(400).json({
                    message: "Profile image is required"
                })
            }

            const imageUrl =
                `/uploads/profiles/profile-images/${req.file.filename}`

            await db.query(
                `
                UPDATE users
                SET profile_image = ?
                WHERE id = ?
                `,
                [imageUrl, req.user.id]
            )

            res.json({
                message: "Profile image updated successfully",
                profile_image: imageUrl
            })
        } catch (error) {
            console.error(error)

            res.status(500).json({
                message: "Failed to upload profile image"
            })
        }
    }
)

router.post(
    "/me/cover-image",
    authMiddleware,
    upload.single("cover_image"),
    async (req, res) => {
        try {
            if (!req.file) {
                return res.status(400).json({
                    message: "Cover image is required"
                })
            }

            const imageUrl =
                `/uploads/profiles/cover-images/${req.file.filename}`

            await db.query(
                `
                UPDATE users
                SET cover_image = ?
                WHERE id = ?
                `,
                [imageUrl, req.user.id]
            )

            res.json({
                message: "Cover image updated successfully",
                cover_image: imageUrl
            })
        } catch (error) {
            console.error(error)

            res.status(500).json({
                message: "Failed to upload cover image"
            })
        }
    }
)

router.put("/me/specializations", authMiddleware, async (req, res) => {
    const connection = await db.getConnection()

    try {
        if (req.user.role !== "mentor") {
            connection.release()

            return res.status(403).json({
                message: "Mentor only"
            })
        }

        const { course_ids } = req.body

        if (!Array.isArray(course_ids)) {
            connection.release()

            return res.status(400).json({
                message: "course_ids must be an array"
            })
        }

        await connection.beginTransaction()

        await connection.query(
            `
            DELETE FROM mentor_specializations
            WHERE mentor_id = ?
            `,
            [req.user.id]
        )

        for (const courseId of course_ids) {
            await connection.query(
                `
                INSERT INTO mentor_specializations (
                    mentor_id,
                    course_id
                )
                VALUES (?, ?)
                `,
                [req.user.id, courseId]
            )
        }

        await connection.commit()
        connection.release()

        res.json({
            message: "Specializations updated successfully"
        })
    } catch (error) {
        await connection.rollback()
        connection.release()

        console.error(error)

        res.status(500).json({
            message: "Failed to update specializations"
        })
    }
})

module.exports = router