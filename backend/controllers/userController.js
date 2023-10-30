// User:
// 1. GET /api/users/username
require('dotenv').config();
const express = require('express');

const userService = require('../services/userService.js');

const router = express.Router();

router.get('/username', (req, res) => {
  // get username from database or Google Auth
  res.send('Username');
});

router.post('/login', async (req, res) => {
  const idToken = req.body.idToken;
  const username = req.body.username;
  console.log(idToken);

  try {
    var userId = await userService.verify(idToken);
  } catch (error) {
    console.log(error);
    res.status(500).send('Error while verifying user');
    return;
  }

  try {
    let userData = {
      userId,
      username
    };
    response = await userService.createUser(userData);
    res.send({ response: response });
  } catch (error) {
    console.log(error);
    res.status(500).send('Error while creating user');
  }
});

module.exports = router;
