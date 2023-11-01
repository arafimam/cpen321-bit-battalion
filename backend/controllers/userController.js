// User:
// 1. GET /api/users/username

require('dotenv').config();
const express = require('express');

const middleware = require('../middleware/middleware.js');
const userService = require('../services/userService.js');

const router = express.Router();

// router.get('/username', (req, res) => {
//   // get username from database or Google Auth
//   res.send('Username');
// });

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

router.get('/lists', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userId = res.locals.user.userId;

  try {
    const lists = await userService.getListsforUser(userId);
    res.send({ lists: lists });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to get lists for user' });
  }
});

// Adding list for user
router.put('/add/list', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userId = res.locals.user.userId;
  const listName = req.body.listName;

  if (listName === null || listName === undefined) {
    res.status(400).send({ errorMessage: 'Please provide a list name' });
    return;
  }

  try {
    await userService.addListForUser(userId, listName);
    res.send({ message: 'New list successfully added for user' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to add list for user' });
  }
});

// Remove list for user
router.put('/:id/remove/list', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userId = res.locals.user.userId;
  const listId = req.params.id;

  try {
    await userService.removeListForUser(userId, listId);
    res.send({ message: 'List successfully removed for user' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to remove list for user' });
  }
});

module.exports = router;
