// User:
// 1. GET /api/users/username
const express = require('express');

const router = express.Router();

router.get('/username', (req, res) => {
  // get username from database or Google Auth
  res.send('Username');
});

module.exports = router;
