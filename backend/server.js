const express = require("express");
const db = require("./db");

const app = express();
const PORT = 3001;
const authRoutes = require("./routes/auth");

app.use(express.json());
app.use("/api/auth", authRoutes);

app.get("/api/health", async (req, res) => {
    try {
        await db.query("SELECT 1");

        res.status(200).json({
            message: "CodeLearn API is running",
            database: "connected"
        });
    } catch (error) {
        console.error(error);

        res.status(500).json({
            message: "CodeLearn API is running",
            database: "disconnected"
        });
    }
});

app.listen(PORT, () => {
    console.log(`CodeLearn API running on http://localhost:${PORT}`);
});