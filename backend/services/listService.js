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
    const list = await listModel.createList(listName);
    return list;
  } catch (error) {
    throw new Error('Error in service while creating list: ' + error.message);
  }
}

async function deleteListById(listId) {
  try {
    return await listModel.deleteList(listId);
  } catch (error) {
    throw error;
  }
}

async function getListById(listId) {
  try {
    const list = await listModel.getListById(listId);
    return list;
  } catch (error) {
    throw new Error('Error in service while getting list by id. ', error.message);
  }
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
  createList,
  addPlaceToList,
  deleteListById,
  removePlaceFromList
};
