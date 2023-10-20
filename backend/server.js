const express = require("express");
const app = express();

// Middleware
app.use(express.json());

// Routes
// app.use('/api/users', userRoutes);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
