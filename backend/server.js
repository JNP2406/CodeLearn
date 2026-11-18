const express = require("express");
const path = require("path");
const db = require("./db");

const authRoutes = require("./routes/auth");
const courseRoutes = require("./routes/courses");
const progressRoutes = require("./routes/progress");
const materialRoutes = require("./routes/materials");
const quizRoutes = require("./routes/quizzes");
const caseStudyRoutes = require("./routes/caseStudies");
const certificateRoutes = require("./routes/certificates");
const profileRoutes = require("./routes/profiles");
const mentoringRoutes = require("./routes/mentorings");
const notificationRoutes = require("./routes/notifications");

const app = express();
const PORT = 3001;

app.use(express.json());

app.use(
  "/materials",
  express.static(path.join(__dirname, "uploads", "materials")),
);

app.use(
  "/certificates",
  express.static(path.join(__dirname, "uploads", "certificates")),
);

app.use("/uploads", express.static(path.join(__dirname, "uploads")));

app.use("/api/auth", authRoutes);
app.use("/api/courses", courseRoutes);
app.use("/api/progress", progressRoutes);
app.use("/api/materials", materialRoutes);
app.use("/api/quiz", quizRoutes);
app.use("/api/case-study", caseStudyRoutes);
app.use("/api/certificates", certificateRoutes);
app.use("/api/profile", profileRoutes);
app.use("/api/mentorings", mentoringRoutes);
app.use("/api/notifications", notificationRoutes);
app.use("/api/notifications", notificationRoutes);

app.get("/api/health", async (req, res) => {
  try {
    await db.query("SELECT 1");

    res.status(200).json({
      message: "CodeLearn API is running",
      database: "connected",
    });
  } catch (error) {
    console.error(error);

    res.status(500).json({
      message: "CodeLearn API is running",
      database: "disconnected",
    });
  }
});

app.listen(PORT, () => {
  console.log(`CodeLearn API running on http://localhost:${PORT}`);
});
