const mongoose = require("mongoose");

const activitySchema = new mongoose.Schema({
  // Why do we need to store user id for each activity
  userId: {
    type: String,
    required: true,
    uniqueId: true,
  },
  activityInfo: {
    name: {
      type: String,
      required: true,
    },
    time: {
      type: Date, // change this later if needed
      required: true,
    },
    location: {
      latitude: { type: Number, required: true },
      longitude: { type: Number, required: true },
    },
  },
});

const Activity = mongoose.model("Activity", activitySchema);

module.exports = Activity;
