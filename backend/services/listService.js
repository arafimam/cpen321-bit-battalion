const listModel = require('../models/listModel');

// async function getAllLists() {
//   try {
//     const lists = await listModel.getAllLists();
//     console.log(lists);
//     return lists;
//   } catch (error) {
//     throw new Error('Error in service while getting lists', error.message);
//   }
// }

async function createList(listName) {
  try {
    return await listModel.createList(listName);
  } catch (error) {
    throw new Error('Error in service while creating list: ' + error.message);
  }
}

async function deleteListById(listId) {
  return await listModel.deleteList(listId);
}

async function getListById(listId) {
  return await listModel.getListById(listId);
}

async function getListName(listId) {
  return await listModel.getListName(listId);
}

async function getPlacesByListId(listId) {
  return await listModel.getPlaces(listId);
}

async function addPlaceToList(listId, placeData) {
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

  return await listModel.addPlaceToList(listId, place);
}

async function removePlaceFromList(listId, placeId) {
  return await listModel.removePlaceFromList(listId, placeId);
}

module.exports = {
  getListById,
  getListName,
  createList,
  addPlaceToList,
  getPlacesByListId,
  deleteListById,
  removePlaceFromList
};
