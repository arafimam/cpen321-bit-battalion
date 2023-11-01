const { OAuth2Client } = require('google-auth-library');

const userModel = require('../models/userModel.js');
const listService = require('./listService');

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
    var userExists = await userModel.checkUserExists(userData.userId);
  } catch (error) {
    throw new Error('Error while checking if user exists: ' + error.message);
  }

  if (!userExists) {
    const user = {
      username: userData.username,
      googleId: userData.userId,
      deviceRegistrationToken: userData.deviceRegistrationToken
    };
    try {
      const createdUser = await userModel.User.create(user);
      return createdUser.username;
    } catch (error) {
      throw new Error('Error in service while creating user: ' + error.message);
    }
  } else {
    try {
      await userModel.updateDeviceRegistrationToken(userData.userId, userData.deviceRegistrationToken);
      return userData.username;
    } catch (error) {
      throw new Error('Error in service while updating device registration token: ' + error.message);
    }
  }
}

async function getUserByGoogleId(googleId) {
  const user = await userModel.getUserByGoogleId(googleId);
  if (user === null || user === undefined) {
    throw new Error('Could not find user with the given google id');
  }

  return {
    userId: user[0]._id,
    username: user[0].username,
    deviceRegistrationToken: user[0].deviceRegistrationToken
  };
}

async function getUserById(userId) {
  const user = await userModel.getUserById(userId);
  if (user === null || user === undefined) {
    throw new Error('Could not find user with the given user id');
  }

  return {
    userId: user._id,
    username: user.username,
    deviceRegistrationToken: user.deviceRegistrationToken
  };
}

async function updateDeviceRegistrationToken(userId, deviceRegistrationToken) {
  try {
    return await userModel.updateDeviceRegistrationToken(userId, deviceRegistrationToken);
  } catch (error) {
    throw new Error('Error in service while updating device registration token: ' + error.message);
  }
}

async function addListForUser(userId, listName) {
  const list = await listService.createList(listName);

  try {
    return await userModel.addListForUser(userId, list._id);
  } catch (error) {
    await listService.deleteListById(list._id);
    throw new Error('Error in service while adding list for user: ' + error.message);
  }
}
async function removeListForUser(userId, listId) {
  await listService.deleteListById(listId);
  try {
    return await userModel.removeListForUser(userId, listId);
  } catch (error) {
    throw new Error('Error in service while removing list for user: ' + error.message);
  }
}

async function getListsforUser(userId) {
  try {
    const output = await userModel.getUserLists(userId);
    const listIds = output.lists;

    let lists = [];
    for (let listId of listIds) {
      let list = await listService.getListName(listId);
      lists.push(list);
    }

    return lists;
  } catch (error) {
    throw new Error('Error in service while getting lists for user: ' + error.message);
  }
}

module.exports = {
  verify,
  createUser,
  getUserById,
  getUserByGoogleId,
  updateDeviceRegistrationToken,
  getListsforUser,
  addListForUser,
  removeListForUser
};
