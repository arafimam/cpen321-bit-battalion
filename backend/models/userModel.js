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
  // userLocation: {
  //   location: {
  //     latitude: { type: Number, required: true },
  //     longitude: { type: Number, required: true },
  //   },
  //   locationName: { type: String, required: true },
  // },
});

userSchema.statics.createUser = async function (username, googleId) {
  this.create({ username, googleId });
};

userSchema.statics.deleteUser = async function (googleId) {
  this.findOneAndDelete({ googleId });
};

const User = mongoose.model("User", userSchema);

module.exports = User;
