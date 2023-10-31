// Group:
// 1. POST /api/groups/create_group items: {group_name}
// 2. GET /api/groups/all_groups
// 3. POST /api/groups/add_to_group items: {userId, group_code}
// 4. GET /api/groups/group?group_id={group_id}
// 5. DELETE /api/groups/delete_group items: {group_id}
// 6. DELETE /api/groups/leave_group items: {group_id, userId}

const express = require('express');

const groupService = require('../services/groupService.js');
const middleware = require('../middleware/middleware.js');

const router = express.Router();

// Get all groups for a user
router.get('/all', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const user = res.locals.user;

  try {
    const groups = await groupService.getAllGroups(user.userId);
    res.send({ groups: groups });
  } catch (error) {
    console.log(error.message);
    return res.status(401).json({ message: 'Invalid Google ID token.' });
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
      res.send({ group: group });
    }
  } catch (error) {
    res.status(500).send({
      errorMessage: `Something went wrong while finding group with ID.`
    });
  }
});

// Create a group
router.post('/create', middleware.verifyToken, middleware.getUser, async (req, res) => {
  let groupName = req.body.groupName;

  const googleId = req.googleId;
  console.log(googleId);
  // try {
  //   var user = await userService.getUserByGoogleId(googleId);
  // } catch (error) {
  //   res.status(400).send({ errorMessage: 'Failed to get user by google id' });
  //   return;
  // }

  let groupData = {
    groupName,
    ownerId: res.locals.user.userId,
    ownerName: res.locals.user.username
  };

  console.log(groupData);

  try {
    const groupCode = await groupService.createGroup(groupData);
    res.send({ groupCode: groupCode });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to create a group.' });
  }
});

// Delete group
router.delete('/:id/delete', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;

  try {
    // TODO: check if user is owner of group
    await groupService.deleteGroup(groupId);
    res.send({ message: 'group successfully deleted' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to delete the group.' });
  }
});

// Add user to group
router.put('/join', middleware.verifyToken, middleware.getUser, async (req, res) => {
  // Group code as part of request body
  const groupCode = req.body.groupCode;
  const user = res.locals.user;
  console.log(user);
  try {
    const group = await groupService.addUserToGroup(groupCode, user);

    if (!group) {
      res.status(400).send({ errorMessage: 'Incorrect group code' });
    } else {
      res.send({ message: 'User successfully added to group' });
    }
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to add user to group' });
  }
});

// Remove user from group
router.put('/:id/leave', middleware.verifyToken, middleware.getUser, async (req, res) => {
  const groupId = req.params.id;
  const user = res.locals.user;

  try {
    let resp = await groupService.removeUserFromGroup(groupId, user.userId);
    console.log(resp);
    res.send({ message: 'User successfully removed from group' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to remove user from group' });
  }
});

router.get('/:id/lists', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;

  try {
    const lists = await groupService.getListsforGroup(groupId);
    res.send({ lists: lists });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to get lists for group' });
  }
});

// Adding a list to the group
router.put('/:id/add/list', middleware.verifyToken, async (req, res) => {
  const groupId = req.params.id;
  const listName = req.body.listName;

  if (listName === null || listName === undefined) {
    res.status(400).send({ errorMessage: 'Please provide a list name' });
    return;
  }

  try {
    await groupService.addListToGroup(groupId, listName);
    res.send({ message: 'New list successfully added to group' });
  } catch (error) {
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
    res.status(500).send({ errorMessage: 'Failed to remove list to group' });
  }
});

module.exports = router;
