const admin = require('firebase-admin');
const { TRIP_TROOPER } = require('../../constants');
const { createGroup } = require('../groupNotification'); // Replace with the correct path

jest.mock('firebase-admin', () => {
  const messagingSendMock = jest.fn();
  const messagingMock = jest.fn(() => ({ send: messagingSendMock }));

  return {
    messaging: messagingMock
  };
});

describe('createGroup', () => {
  it('should send a message when creating a group', async () => {
    const userData = {
      deviceRegistrationToken: 'mockDeviceToken'
    };
    admin.messaging().send.mockResolvedValue('message sent successfully');

    await createGroup(userData, 'TestGroup', '123');

    expect(admin.messaging().send).toHaveBeenCalledWith({
      notification: {
        title: TRIP_TROOPER,
        body: 'Group Created Successfully'
      },
      data: {
        groupCode: '123',
        groupName: 'TestGroup'
      },
      token: 'mockDeviceToken'
    });

    expect(admin.messaging().send).toHaveBeenCalledTimes(1);
  });

  it('should catch error if sending notification when creating a group was unsuccessful', async () => {
    const userData = {
      deviceRegistrationToken: 'mockDeviceToken'
    };
    admin.messaging().send.mockRejectedValue('message was not sent successfully');

    await createGroup(userData, 'TestGroup', '123');

    expect(admin.messaging().send).toHaveBeenCalledWith({
      notification: {
        title: TRIP_TROOPER,
        body: 'Group Created Successfully'
      },
      data: {
        groupCode: '123',
        groupName: 'TestGroup'
      },
      token: 'mockDeviceToken'
    });

    expect(admin.messaging().send).toHaveBeenCalledTimes(1);
  });
});
