const mongoose = require("mongoose");

const groupSchema = new mongoose.Schema({
  groupCode: {
    type: String,
    required: true,
    unique: true,
  },
  ownerId: {
    type: String,
    required: true,
  },
  members: {
    type: [
      {
        user_id: { type: String, required: true },
        username: { type: String, required: true },
      },
    ],
    default: [],
  },
  groupDestination: {
    location: {
      latitude: { type: Number, required: true },
      longitude: { type: Number, required: true },
    },
    locationName: { type: String, required: true },
  },
});

const Group = mongoose.model("Group", groupSchema);

module.exports = Group;
