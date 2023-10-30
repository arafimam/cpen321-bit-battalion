const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  username: {
    type: String,
    required: true
  },
  googleId: {
    type: String,
    required: true,
    unique: true
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

module.exports = { User, checkUserExists };
