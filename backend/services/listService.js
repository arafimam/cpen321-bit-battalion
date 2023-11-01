const listModel = require('../models/listModel');
const { twoOpt } = require('../utils/twoOpt');

//TODO: check list exists function

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

// async function createScheduleForList(listId, placeIds) {
//   let places = [];

//   for (const placeId of placeIds) {
//     const place = await listModel.getPlace(listId, placeId);
//     if (!place) {
//       throw new Error('Place not found in list');
//     }
//     places.push(place);
//   }

//   console.log('places: ' + places);

//   return twoOpt(places);
// }
async function createScheduleForList(listId, placeIds) {
  const list = await listModel.getPlaces(listId);
  const places = list.places;

  // TODO: make sure place ID exists

  const filteredPlaces = places.filter((place) => {
    for (let placeId of placeIds) {
      if (place.placeId === placeId) {
        return true;
      }
      return false;
    }
  });
  console.log('places: ' + places);

  return twoOpt(filteredPlaces);
}

module.exports = {
  getListById,
  getListName,
  createList,
  addPlaceToList,
  getPlacesByListId,
  deleteListById,
  removePlaceFromList,
  createScheduleForList
};
