const admin = require('firebase-admin');

const { TRIP_TROOPER } = require('../constants.js');

async function createGroup(userData, groupName, groupCode) {
  console.log(userData);

  const message = {
    notification: {
      title: TRIP_TROOPER,
      body: 'Group Created Successfully'
    },
    data: {
      groupCode: groupCode,
      groupName: groupName
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

module.exports = { createGroup };
