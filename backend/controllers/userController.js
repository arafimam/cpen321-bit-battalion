const express = require('express');

const middleware = require('../middleware/middleware.js');
const userService = require('../services/userService.js');
const listNotification = require('../notifications/listNotification.js');

const router = express.Router();

router.post('/login', async (req, res) => {
  const idToken = req.body.idToken;
  const username = req.body.username;
  const deviceRegistrationToken = req.body.deviceRegistrationToken;

  try {
    var googleId = await userService.verify(idToken);
  } catch (error) {
    console.log(error);
    res.status(500).send('Error while verifying user');
    return;
  }

  try {
    let userData = {
      googleId,
      username,
      deviceRegistrationToken
    };
    const response = await userService.createUser(userData);
    res.send({ response });
  } catch (error) {
    console.log(error);
    res.status(500).send('Error while creating user');
  }
});

router.put('/device-registration-token/update', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userId = res.locals.user.userId;
  const deviceRegistrationToken = req.body.deviceRegistrationToken;

  try {
    await userService.updateDeviceRegistrationToken(userId, deviceRegistrationToken);
    res.send({ message: 'Device registration token updated' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to update device registration token' });
  }
});

router.get('/lists', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userId = res.locals.user.userId;

  try {
    const lists = await userService.getListsforUser(userId);
    res.send({ lists });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to get lists for user' });
  }
});

// Adding list for user
router.put('/add/list', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const userData = res.locals.user;
  const listName = req.body.listName;

  if (listName === null || listName === undefined) {
    res.status(400).send({ errorMessage: 'Please provide a list name' });
    return;
  }

  try {
    await userService.addListForUser(userData.userId, listName);
    res.send({ message: 'New list successfully added for user' });
    try {
      await listNotification.createList(userData.userId, listName);
    } catch (error) {
      console.log('Error while sending notification about a new list being created: ', error.message);
    }
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
