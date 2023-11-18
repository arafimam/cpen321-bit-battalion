const admin = require('firebase-admin');
const { TRIP_TROOPER } = require('../../constants');
const { createList } = require('../listNotification'); // Replace with the correct path

jest.mock('firebase-admin', () => {
  const messagingSendMock = jest.fn();
  const messagingMock = jest.fn(() => ({ send: messagingSendMock }));

  return {
    messaging: messagingMock
  };
});

describe('createList', () => {
  // ChatGPT usage: No
  it('should send a message when creating a list', async () => {
    const userData = {
      deviceRegistrationToken: 'mockDeviceToken'
    };
    admin.messaging().send.mockResolvedValue('message sent successfully');

    await createList(userData, 'TestList');

    expect(admin.messaging().send).toHaveBeenCalledWith({
      notification: {
        title: TRIP_TROOPER,
        body: 'List Created Successfully'
      },
      data: {
        listName: 'TestList'
      },
      token: 'mockDeviceToken'
    });

    expect(admin.messaging().send).toHaveBeenCalledTimes(1);
  });

  // ChatGPT usage: No
  it('should catch error if sending notification when creating a list was unsuccessful', async () => {
    const userData = {
      deviceRegistrationToken: 'mockDeviceToken'
    };
    admin.messaging().send.mockRejectedValue('message was not sent successfully');

    await createList(userData, 'TestList');

    expect(admin.messaging().send).toHaveBeenCalledWith({
      notification: {
        title: TRIP_TROOPER,
        body: 'List Created Successfully'
      },
      data: {
        listName: 'TestList'
      },
      token: 'mockDeviceToken'
    });

    expect(admin.messaging().send).toHaveBeenCalledTimes(1);
  });
});
