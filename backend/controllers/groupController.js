// Group:
// 1. POST /api/groups/create_group items: {group_name}
// 2. GET /api/groups/all_groups
// 3. POST /api/groups/add_to_group items: {userId, group_code}
// 4. GET /api/groups/group?group_id={group_id}
// 5. DELETE /api/groups/delete_group items: {group_id}
// 6. DELETE /api/groups/leave_group items: {group_id, userId}

const express = require('express');
const { OAuth2Client } = require('google-auth-library');

const groupService = require('../services/groupService.js');
const userService = require('../services/userService.js');

const client = new OAuth2Client();
const router = express.Router();

async function verifyToken(req, res, next) {
  const idToken = req.headers.authorization;

  try {
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.WEB_CLIENT_ID_RITAM
    });
    const payload = ticket.getPayload();
    req.googleId = payload['sub'];
    console.log(req.googleId);
    next();
  } catch (error) {
    return res.status(401).json({ message: 'Invalid Google ID token.' });
  }
}

// Get all groups
// router.get('/all', async (req, res) => {
//   try {
//     const groups = await groupService.getAllGroups();
//     res.send({ groups: groups });
//   } catch (error) {
//     res.status(500).send({ errorMessage: 'Something went wrong while getting all groups' });
//   }
// });

// Get a group by Id
router.get('/:id', async (req, res) => {
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
router.post('/create', verifyToken, async (req, res) => {
  let groupName = req.body.groupName;

  const googleId = req.googleId;
  try {
    var user = await userService.getUserByGoogleId(googleId);
  } catch (error) {
    res.status(400).send({ errorMessage: 'Failed to get user by google id' });
    return;
  }

  let groupData = {
    groupName,
    ownerId: user[0]._id,
    ownerName: user[0].username
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
router.delete('/:id/delete', async (req, res) => {
  const groupId = req.params.id;

  try {
    await groupService.deleteGroup(groupId);
    res.send('group successfully deleted');
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to delete the group.' });
  }
});

// Add user to group
router.put('/join', verifyToken, async (req, res) => {
  // Group code as part of request body
  const groupCode = req.body.groupCode;

  // actual user data will come from Google Auth that frontend
  // TODO: change to use middleware to convert token to uid
  // const userData = {
  //   userId: req.headers.userid,
  //   username: req.headers.username
  // };
  const userId = req.userId;
  // const

  try {
    const groupCreated = await groupService.addUserToGroup(groupCode, userData);

    if (!groupCreated) {
      res.status(400).send({ errorMessage: 'Incorrect group code' });
    } else {
      res.send({ message: 'User successfully added to group' });
    }
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to add user to group' });
  }
});

// Remove user from group
router.put('/:id/leave', async (req, res) => {
  const groupId = req.params.id;

  // GET USER ID FROM TOKEN
  const userData = {
    userId: req.headers.userid,
    username: req.headers.username
  };

  try {
    let resp = await groupService.removeUserFromGroup(groupId, userData.userId);
    console.log(resp);
    res.send({ message: 'User successfully removed from group' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to remove user from group' });
  }
});

router.put('/:id/add/list', async (req, res) => {
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

router.put('/:id/remove/list', async (req, res) => {
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
