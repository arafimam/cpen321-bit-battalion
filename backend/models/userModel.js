const mongoose = require("mongoose");

const userSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true,
  },
  googleId: {
    type: String,
    required: true,
    unique: true,
  },
  userLocation: {
    location: {
      latitude: Number,
      longitude: Number,
    },
    locationName: String,
  },
  // Add other user attributes here
});

const User = mongoose.model("User", userSchema);

module.exports = User;
