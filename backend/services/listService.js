const {
  List,
  getAll,
  getById,
  addList,
  deleteList,
} = require("../models/listModel");

async function getAllLists() {
  try {
    const lists = await getAll();
    console.log(lists);
    return lists;
  } catch (error) {
    throw new Error("Error in service while getting lists", error.message);
  }
}

async function getListById(listId) {
  try {
    const list = await getById(listId);
    return list;
  } catch (error) {
    throw new Error("Error while getting list by id", error.message);
  }
}

async function createList(listName) {
  try {
    listschemaInput = {
      listName: listName,
    };

    return await List.create(listschemaInput);
  } catch (error) {
    throw new Error("Error while creating list: " + error.message);
  }
}

async function addPlaceToList(userData, groupCode) {
  const member = {
    user_id: userData.user_id,
    username: userData.username,
  };

  try {
    return await addUser(member, groupCode);
  } catch (error) {
    throw error;
  }
}

async function addListToGroup(listId) {
  try {
    return await addList(listId);
  } catch (error) {
    throw error;
  }
}

async function deleteListById(listId) {
  try {
    return await deleteList(listId, listId);
    //call list service to delete list
  } catch (error) {
    throw error;
  }
}

module.exports = {
  getAllLists,
  getListById,
  createList,
  addPlaceToList,
  addListToGroup,
};
