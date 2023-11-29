const express = require('express');

const groupService = require('../services/groupService.js');
const middleware = require('../middleware/middleware.js');
const groupNotification = require('../notifications/groupNotification.js');

const router = express.Router();

// Get all groups for a user
router.get('/all', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const user = res.locals.user;

  try {
    const groups = await groupService.getAllGroups(user.userId);
    res.send({ groups });
  } catch (error) {
    console.log(error.message);
    return res.status(500).json({ message: 'Failed to find groups for the user' });
  }
});

// Get a group by Id
router.get('/:id', middleware.verifyToken, async (req, res) => {
  try {
    const groupId = req.params.id;
    const group = await groupService.getGroupById(groupId);
    if (group === null || group === undefined) {
      res.status(400).send({ errorMessage: `Failed to find group with ID: ${groupId}` });
    } else {
      res.send({ group });
    }
  } catch (error) {
    console.log(error.message);
    res.status(500).send({
      errorMessage: `Something went wrong while finding group with ID.`
    });
  }
});

// Create a group
router.post('/create', middleware.verifyToken, middleware.getUser, async (req, res) => {
  let groupName = req.body.groupName;

  if (groupName === null || groupName === undefined || groupName === '') {
    res.status(400).send({ errorMessage: 'Please provide a group name' });
    return;
  }

  let groupData = {
    groupName,
    ownerId: res.locals.user.userId,
    ownerName: res.locals.user.username
  };

  try {
    const groupCode = await groupService.createGroup(groupData);
    res.send({ groupCode });

    try {
      await groupNotification.createGroup(res.locals.user, groupName, groupCode);
    } catch (error) {
      console.log('Error while notifying about a new group created: ', error.message);
    }
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to create a group.' });
  }
});

// Delete group
router.delete('/:id/delete', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;

  try {
    await groupService.deleteGroup(groupId);
    res.send({ message: 'group successfully deleted' });
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to delete the group.' });
  }
});

// Help from chatGPT
// Add user to group
router.put('/join', middleware.verifyToken, middleware.getUser, async (req, res) => {
  // Group code as part of request body
  const groupCode = req.body.groupCode;

  if (groupCode === null || groupCode === undefined || groupCode === '') {
    res.status(400).send({ errorMessage: 'Please provide a group code' });
    return;
  }

  const user = res.locals.user;
  try {
    const retval = await groupService.addUserToGroup(groupCode, user);

    if (!retval.userAlreadyInGroup) {
      if (!retval.group) {
        res.status(400).send({ errorMessage: 'Incorrect group code' });
      } else {
        res.status(200).send({ message: 'User successfully added to group' });
        try {
          await groupNotification.joinGroup(user, retval.group);
        } catch (error) {
          console.log('Error while notifying group members about a new member joining the group: ', error.message);
        }
      }
    } else {
      res.status(400).send({ errorMessage: 'User already in group' });
    }
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to add user to group' });
  }
});

// Remove user from group
router.put('/:id/leave', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const groupId = req.params.id;
  const user = res.locals.user;

  try {
    await groupService.removeUserFromGroup(groupId, user.userId);
    res.send({ message: 'User successfully removed from group' });
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to remove user from group' });
  }
});

router.get('/:id/lists', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;

  try {
    const lists = await groupService.getListsforGroup(groupId);
    res.send({ lists });
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to get lists for group' });
  }
});

// Adding a list to the group
router.put('/:id/add/list', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;
  const listName = req.body.listName;

  if (listName === null || listName === undefined || listName === '') {
    res.status(400).send({ errorMessage: 'Please provide a list name' });
    return;
  }

  try {
    await groupService.addListToGroup(groupId, listName);
    res.send({ message: 'New list successfully added to group' });
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to add list to group' });
  }
});

// Removing a list from the group
router.put('/:id/remove/list', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;
  const listId = req.body.listId;

  if (listId === null || listId === undefined) {
    res.status(400).send({ errorMessage: 'Please provide a listId' });
    return;
  }

  try {
    await groupService.removeListFromGroup(groupId, listId);
    res.send({ message: 'List successfully removed to group' });
  } catch (error) {
    console.log(error.message);
    res.status(500).send({ errorMessage: 'Failed to remove list to group' });
  }
});

module.exports = router;
