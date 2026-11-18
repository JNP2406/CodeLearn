const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const db = require("../db");

const router = express.Router();

router.post("/register", async (req, res) => {
  try {
    const { email, password, role, name, campus } = req.body;

    if (!email || !password || !role || !name) {
      return res.status(400).json({
        message: "Email, password, role, and name are required",
      });
    }

    if (!["student", "mentor"].includes(role)) {
      return res.status(400).json({
        message: "Role must be student or mentor",
      });
    }

    const [existingUsers] = await db.query(
      `
                SELECT id
                FROM users
                WHERE email = ?
                `,
      [email],
    );

    if (existingUsers.length > 0) {
      return res.status(409).json({
        message: "Email already registered",
      });
    }

    const passwordHash = await bcrypt.hash(password, 10);

    const [result] = await db.query(
      `
                INSERT INTO users
                (
                    email,
                    password_hash,
                    role,
                    name,
                    campus
                )
                VALUES (?, ?, ?, ?, ?)
                `,
      [email, passwordHash, role, name, campus || null],
    );

    const token = jwt.sign(
      {
        id: result.insertId,
        role,
      },
      process.env.JWT_SECRET,
      {
        expiresIn: "7d",
      },
    );

    return res.status(201).json({
      message: "Registration successful",

      token,

      user: {
        id: result.insertId,

        email,

        role,

        name,

        campus: campus || null,

        is_demo: false,
      },
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        message: "Email and password are required",
      });
    }

    const [users] = await db.query(
      `
                SELECT *
                FROM users
                WHERE email = ?
                `,
      [email],
    );

    if (users.length === 0) {
      return res.status(401).json({
        message: "Invalid email or password",
      });
    }

    const user = users[0];

    const passwordMatch = await bcrypt.compare(password, user.password_hash);

    if (!passwordMatch) {
      return res.status(401).json({
        message: "Invalid email or password",
      });
    }

    const token = jwt.sign(
      {
        id: user.id,
        role: user.role,
      },
      process.env.JWT_SECRET,
      {
        expiresIn: "7d",
      },
    );

    return res.status(200).json({
      message: "Login successful",

      token,

      user: {
        id: user.id,

        email: user.email,

        role: user.role,

        name: user.name,

        campus: user.campus,

        is_demo: Boolean(user.is_demo),
      },
    });
  } catch (error) {
    console.error(error);

    return res.status(500).json({
      message: "Internal server error",
    });
  }
});

module.exports = router;
