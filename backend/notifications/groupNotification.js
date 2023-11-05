const admin = require('firebase-admin');

const { TRIP_TROOPER } = require('../constants.js');
const userService = require('../services/userService.js');

async function createGroup(userData, groupName, groupCode) {
  const message = {
    notification: {
      title: TRIP_TROOPER,
      body: 'Group Created Successfully'
    },
    data: {
      groupCode,
      groupName
    },
    token: userData.deviceRegistrationToken
  };

  admin
    .messaging()
    .send(message)
    .then((response) => {
      console.log('Successfully sent message: ', response);
    })
    .catch((error) => {
      console.log('Error sending message: ', error);
    });
}

async function joinGroup(userData, group) {
  let userDeviceRegistrationTokens = [];

  for (let member of group.members) {
    const user = await userService.getUserById(member.userId);
    userDeviceRegistrationTokens.push(user.deviceRegistrationToken);
  }

  for (let token of userDeviceRegistrationTokens) {
    const message = {
      notification: {
        title: TRIP_TROOPER,
        body: `${userData.username} has joined ${group.groupName}!`
      },
      data: {
        username: userData.username,
        groupName: group.groupName
      },
      token
    };

    admin
      .messaging()
      .send(message)
      .then((response) => {
        console.log('Successfully sent message: ', response);
      })
      .catch((error) => {
        console.log('Error sending message: ', error);
      });
  }
}

module.exports = { createGroup, joinGroup };
