const {
  Group,
  getAll,
  getById,
  generateUniqueGroupCode,
  addUser,
  addList,
  deleteList
} = require('../models/groupModel');

async function getAllGroups() {
  try {
    const groups = await getAll();
    console.log(groups);
    return groups;
  } catch (error) {
    throw new Error('Error in service while getting group by id', error.message);
  }
}

async function getGroupById(groupId) {
  try {
    const group = await getById(groupId);
    return group;
  } catch (error) {
    throw new Error('Error while getting group by id', error.message);
  }
}

async function createGroup(groupData) {
  try {
    const groupCode = await generateUniqueGroupCode();
    groupSchemaInput = {
      groupCode: groupCode,
      ownerId: groupData.ownerId,
      ownerName: groupData.ownerName,
      groupName: groupData.groupName,
      members: [
        {
          user_id: groupData.ownerId,
          username: groupData.ownerName
        }
      ]
    };

    await Group.create(groupSchemaInput);
    return groupCode;
  } catch (error) {
    throw new Error('Error while creating group: ' + error.message);
  }
}

async function addUserToGroup(userData, groupCode) {
  const member = {
    user_id: userData.user_id,
    username: userData.username
  };

  try {
    return await addUser(member, groupCode);
  } catch (error) {
    throw error;
  }
}

async function addListToGroup(groupId) {
  try {
    return await addList(groupId);
  } catch (error) {
    throw error;
  }
}

async function deleteListFromGroup(listId, groupId) {
  try {
    return await deleteList(listId, groupId);
    //call list service to delete list
  } catch (error) {
    throw error;
  }
}

module.exports = {
  getAllGroups,
  getGroupById,
  createGroup,
  addUserToGroup,
  addListToGroup,
  deleteListFromGroup
};
