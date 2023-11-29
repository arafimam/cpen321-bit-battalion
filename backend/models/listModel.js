const mongoose = require('mongoose');

const listSchema = new mongoose.Schema({
  listName: {
    type: String,
    required: true
  },
  places: {
    type: [
      {
        placeId: { type: String, required: true },
        shortFormattedAddress: { type: String, required: true },
        displayName: { type: String, required: true },
        location: {
          latitude: { type: Number, required: true },
          longitude: { type: Number, required: true }
        },
        rating: { type: String, required: true },
        websiteUri: { type: String, required: true },
        nationalPhoneNumber: { type: String, required: true },
        regularOpeningHours: {
          periods: [
            {
              open: {
                day: Number,
                hour: Number,
                minute: Number
              },
              close: {
                day: Number,
                hour: Number,
                minute: Number
              }
            }
          ]
        }
      }
    ],
    default: []
  }
});

const List = mongoose.model('List', listSchema);

async function createList(listName) {
  const list = {
    listName
  };

  try {
    return await List.create(list);
  } catch (error) {
    throw new Error('Error in DB while creating list: ' + error.message);
  }
}

async function deleteList(listId) {
  try {
    return await List.findByIdAndDelete(listId);
  } catch (error) {
    throw new Error('Error in DB while deleting list: ' + error.message);
  }
}

async function getListById(listId) {
  try {
    return await List.findById(listId);
  } catch (error) {
    throw new Error('Error in DB while getting list name: ' + error.message);
  }
}

async function getListName(listId) {
  try {
    const list = await List.findById(listId).select({ _id: 1, listName: 1 });
    return list;
  } catch (error) {
    throw new Error('Error in DB while getting list name: ' + error.message);
  }
}

async function getPlaces(listId) {
  try {
    const places = await List.findById(listId).select({ _id: 0, places: 1 });
    return places;
  } catch (error) {
    throw new Error('Error in DB while getting places for list ' + error.message);
  }
}

async function getPlace(listId, placeId) {
  const filter = { _id: listId, places: { placeId } };

  try {
    const list = await List.findOne(filter, '-_id');
    return list;
  } catch (error) {
    throw new Error('Error in DB while getting places for list ' + error.message);
  }
}

async function addPlaceToList(listId, place) {
  const placeExists = await List.findOne({ _id: listId, places: { $elemMatch: { placeId: place.placeId } } });
  if (placeExists) {
    throw new Error('Place already exists in list');
  }

  const filter = { _id: listId };
  const update = { $push: { places: place } };

  try {
    return await List.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while adding place to list: ' + error.message);
  }
}

async function removePlaceFromList(listId, placeId) {
  const filter = { _id: listId };
  const update = { $pull: { places: { placeId } } };

  try {
    return await List.updateOne(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while deleting place from the list: ' + error.message);
  }
}

module.exports = {
  List,
  getListById,
  getListName,
  deleteList,
  addPlaceToList,
  removePlaceFromList,
  createList,
  getPlaces,
  getPlace
};
