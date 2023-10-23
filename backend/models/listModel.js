const mongoose = require("mongoose");

const listSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true,
  },
  activities: {
    type: [String],
    default: [],
  }, // Assuming the array contains activity ids
  groupId: {
    type: String,
  },
  groupType: {
    type: Boolean,
    required: true,
  },
});

const List = mongoose.model("List", listSchema);

module.exports = List;
