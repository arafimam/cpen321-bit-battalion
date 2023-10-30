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

async function addList(userId) {
  const filter = { _id: userId };

  try {
    //create the list first

    const update = { $push: { lists: listId } };
    return await User.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while adding list to the group: ' + error.message);
  }
}

module.exports = { User, checkUserExists, getUserByGoogleId };
