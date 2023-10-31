const groupModel = require('../models/groupModel');
const listService = require('./listService');

async function getAllGroups(userId) {
  try {
    const groups = await groupModel.getAllGroups(userId);
    console.log(groups);
    return groups;
  } catch (error) {
    throw new Error('Error in service while getting group by id', error.message);
  }
}

async function getGroupById(groupId) {
  return await groupModel.getGroup(groupId);
}

async function createGroup(groupData) {
  const groupCode = await groupModel.generateUniqueGroupCode();
  const group = {
    groupCode: groupCode,
    ownerId: groupData.ownerId,
    ownerName: groupData.ownerName,
    groupName: groupData.groupName,
    members: [
      {
        userId: groupData.ownerId,
        username: groupData.ownerName
      }
    ]
  };

  await groupModel.createGroup(group);
  return groupCode;
}

async function deleteGroup(groupId) {
  return await groupModel.deleteGroup(groupId);
}

async function addUserToGroup(groupCode, userData) {
  const member = {
    userId: userData.userId,
    username: userData.username
  };

  return await groupModel.addUserToGroup(groupCode, member);
}

async function removeUserFromGroup(userId, groupCode) {
  return await groupModel.removeUserFromGroup(userId, groupCode);
}

async function getListsforGroup(groupId) {
  try {
    const output = await groupModel.getGroupLists(groupId);
    const listIds = output.lists;
    console.log('list ids: ', listIds);
    let lists = [];
    for (let listId of listIds) {
      let list = await listService.getListName(listId);
      lists.push(list);
    }
    return lists;
  } catch (error) {
    throw new Error('Error in service while getting lists for group: ' + error.message);
  }
}

async function addListToGroup(groupId, listName) {
  const list = await listService.createList(listName);

  try {
    return await groupModel.addListToGroup(groupId, list._id);
  } catch (error) {
    await listService.deleteListById(list._id);
    throw new Error('Error in service while adding list to group: ' + error.message);
  }
}

async function removeListFromGroup(groupId, listId) {
  await listService.deleteListById(listId);
  return await groupModel.removeListFromGroup(groupId, listId);
}

module.exports = {
  getAllGroups,
  getGroupById,
  createGroup,
  deleteGroup,
  addUserToGroup,
  removeUserFromGroup,
  getListsforGroup,
  addListToGroup,
  removeListFromGroup
};
