const express = require("express");
const connectDB = require("./db.js");
const { PORT } = require("./constants.js");
const app = express();

// Middleware
app.use(express.json());

// Routes
// app.use('/api/users', userRoutes);

// Simple GET endpoint
app.get("/", (req, res) => {
  res.send("Hello, World!");
});

const startServer = async () => {
  await connectDB();
  app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
  });
};

startServer();
