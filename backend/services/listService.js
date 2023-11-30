const listModel = require('../models/listModel');
const { twoOpt } = require('../utils/twoOpt');

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
    ...placeData
  };

  try {
    const list = await listModel.addPlaceToList(listId, place);
    return {
      placeAlreadyExistsInList: false,
      list
    };
  } catch (error) {
    if (error.message.includes('Place already exists in list')) {
      return {
        placeAlreadyExistsInList: true
      };
    } else {
      throw new Error(error.message);
    }
  }
}

async function removePlaceFromList(listId, placeId) {
  return await listModel.removePlaceFromList(listId, placeId);
}

async function createScheduleForList(listId, placeIds) {
  const list = await getPlacesByListId(listId);
  const places = list.places;

  const filteredPlaces = places.filter((place) => {
    for (let placeId of placeIds) {
      if (place.placeId === placeId) {
        return true;
      }
    }
    return false;
  });

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
