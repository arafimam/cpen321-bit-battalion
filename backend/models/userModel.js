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
  //get user by google id
  try {
    const user = await User.find({ googleId: googleId });
    console.log(user);
    return user;
  } catch (error) {
    throw new Error('Error while getting user by google id: ' + error.message);
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

  try {
    const update = { $push: { lists: listId } };
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while adding list for the user: ' + error.message);
  }
}

async function removeListForUser(userId, listId) {
  const filter = { _id: userId };

  try {
    const update = { $pull: { lists: listId } };
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while removing list for the user: ' + error.message);
  }
}

module.exports = { User, checkUserExists, getUserByGoogleId, getUserLists, addListForUser, removeListForUser };
