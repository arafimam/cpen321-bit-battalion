// Group:
// 1. POST /api/groups/create_group items: {group_name}
// 2. GET /api/groups/all_groups
// 3. POST /api/groups/add_to_group items: {user_id, group_code}
// 4. GET /api/groups/group?group_id={group_id}
// 5. DELETE /api/groups/delete_group items: {group_id}
// 6. DELETE /api/groups/leave_group items: {group_id, user_id}

const express = require('express');

const groupService = require('../services/groupService.js');

const router = express.Router();

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
router.get('/:groupId', async (req, res) => {
  try {
    const groupId = req.params.groupId;
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
router.post('/create', async (req, res) => {
  let groupName = req.body.groupName;

  let groupData = {
    groupName,
    ownerId: 'mock-owner-id',
    ownerName: 'mock-owner-name'
  };

  try {
    const groupCode = await groupService.createGroup(groupData);
    res.send({ groupCode: groupCode });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to create a group.' });
  }
});

// Add user to group
router.put('/addUser', async (req, res) => {
  // Group code as part of request body
  const groupCode = req.body.groupCode;

  // actual user data will come from Google Auth
  const userData = {
    user_id: req.body.user_id,
    username: req.body.username
  };

  let groupObj = await groupService.addUserToGroup(userData, groupCode);

  res.send({ group: groupObj });
});

// Delete group
router.delete('/:groupId/delete', (req, res) => {});

// Remove user from group
router.delete('/:groupId/removeUser', (req, res) => {});

module.exports = router;
