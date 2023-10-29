const mongoose = require("mongoose");

const listSchema = new mongoose.Schema({
  listName: {
    type: String,
    required: true,
  },
  places: {
    type: [
      {
        placeId: { type: String, required: true },
        displayName: { type: String, required: true },
        location: {
          latitude: { type: Number, required: true },
          longitude: { type: Number, required: true },
        },
        rating: { type: String, required: true },
        websiteUri: { type: String, required: true },
        phoneNumber: { type: String, required: true },
        regularOpeningHours: {
          periods: [
            {
              open: {
                day: Number,
                hour: Number,
                minute: Number,
              },
              close: {
                day: Number,
                hour: Number,
                minute: Number,
              },
            },
          ],
        },
      },
    ],
    default: [],
  },
});

const List = mongoose.model("List", listSchema);

async function getAll() {
  return await List.find({});
}

async function getById(listId) {
  return await List.findById(listId);
}

async function addPlace(place, listId) {
  const filter = { _id: listId };
  const update = { $push: { places: place } };

  try {
    return await List.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error("Error in DB while adding place to list: " + error.message);
  }
}

async function deleteList(listId) {
  try {
    return await List.findByIdAndDelete(listId);
  } catch (error) {
    throw new Error("Error in DB while deleting list: " + error.message);
  }
}

module.exports = List;
