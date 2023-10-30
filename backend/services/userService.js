const { OAuth2Client } = require('google-auth-library');
const { User, checkUserExists } = require('../models/userModel.js');

const client = new OAuth2Client();

async function verify(idToken) {
  const ticket = await client.verifyIdToken({
    idToken,
    audience: process.env.WEB_CLIENT_ID_RITAM
  });
  const payload = ticket.getPayload();
  const userId = payload['sub'];
  console.log(userId);
  return userId;
}

async function createUser(userData) {
  try {
    var userExists = await checkUserExists(userData.userId);
  } catch (error) {
    throw new Error('Error while checking if user exists: ' + error.message);
  }

  if (!userExists) {
    const user = {
      username: userData.username,
      googleId: userData.userId
    };
    try {
      const createdUser = await User.create(user);
      return createdUser.username;
    } catch (error) {
      throw new Error('Error while creating user: ' + error.message);
    }
  } else {
    return userData.username;
  }
}

module.exports = { verify, createUser };
