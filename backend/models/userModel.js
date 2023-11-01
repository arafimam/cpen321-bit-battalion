const mongoose = require('mongoose');
const { List } = require('./listModel.js');

const userSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true
  },
  googleId: {
    type: String,
    required: true,
    unique: true
  },
  deviceRegistrationToken: {
    type: String,
    required: true
  },
  lists: {
    type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'List' }],
    default: []
  }
});

const User = mongoose.model('User', userSchema);

async function checkUserExists(googleId) {
  try {
    return await User.exists({ googleId: googleId });
  } catch (error) {
    throw new Error('Error while checking if user exists: ' + error.message);
  }
}

async function getUserByGoogleId(googleId) {
  try {
    const user = await User.find({ googleId: googleId });
    return user;
  } catch (error) {
    throw new Error('Error while getting user by google id: ' + error.message);
  }
}

async function getUserById(userId) {
  try {
    const user = await User.findById(userId);
    return user;
  } catch (error) {
    throw new Error('Error while getting user by google id: ' + error.message);
  }
}

async function updateDeviceRegistrationToken(googleId, deviceRegistrationToken) {
  const filter = { googleId: googleId };
  const update = { deviceRegistrationToken: deviceRegistrationToken };

  try {
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while updating device registration token: ' + error.message);
  }
}

async function getUserLists(userId) {
  try {
    return await User.findById(userId).select({ _id: 1, lists: 1 });
  } catch (error) {
    throw new Error('Error in DB while getting lists for a user' + error.message);
  }
}

async function addListForUser(userId, listId) {
  const filter = { _id: userId };
  const update = { $push: { lists: listId } };

  try {
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while adding list for the user: ' + error.message);
  }
}

async function removeListForUser(userId, listId) {
  const filter = { _id: userId };
  const update = { $pull: { lists: listId } };

  try {
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while removing list for the user: ' + error.message);
  }
}

module.exports = {
  User,
  checkUserExists,
  updateDeviceRegistrationToken,
  getUserById,
  getUserByGoogleId,
  getUserLists,
  addListForUser,
  removeListForUser
};
