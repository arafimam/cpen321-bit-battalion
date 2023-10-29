const { List, getAll, getById, addPlace, deleteList } = require('../models/listModel');

async function getAllLists() {
  try {
    const lists = await getAll();
    console.log(lists);
    return lists;
  } catch (error) {
    throw new Error('Error in service while getting lists', error.message);
  }
}

async function getListById(listId) {
  try {
    const list = await getById(listId);
    return list;
  } catch (error) {
    throw new Error('Error in service while getting list by id', error.message);
  }
}

async function createList(listName) {
  try {
    listschemaInput = {
      listName
    };

    console.log(listschemaInput);

    const list = await List.create(listschemaInput);
    console.log(list);
    return list;
  } catch (error) {
    throw new Error('Error in service while creating list: ' + error.message);
  }
}

async function addPlaceToList(placeData, listId) {
  const place = {
    // placeId: placeData.placeId,
    // displayName: placeData.displayName,
    // location: placeData.location,
    // rating: placeData.rating,
    // websiteUri: placeData.websiteUri,
    // phoneNumber: placeData.phoneNumber,
    // regularOpeningHours: placeData.regularOpeningHours,
    ...placeData
  };

  try {
    return await addPlace(place, listId);
  } catch (error) {
    throw error;
  }
}

async function deleteListById(listId) {
  try {
    return await deleteList(listId);
    //call list service to delete list
  } catch (error) {
    throw error;
  }
}

module.exports = {
  getAllLists,
  getListById,
  createList,
  addPlaceToList
};
