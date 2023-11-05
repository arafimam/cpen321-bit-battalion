const admin = require('firebase-admin');

const { TRIP_TROOPER } = require('../constants.js');

async function createList(userData, listName) {
  const message = {
    notification: {
      title: TRIP_TROOPER,
      body: 'List Created Successfully'
    },
    data: {
      listName
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

module.exports = { createList };
