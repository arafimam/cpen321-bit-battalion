const admin = require('firebase-admin');
const userService = require('../../services/userService');
const { TRIP_TROOPER } = require('../../constants');
const { createGroup, joinGroup } = require('../groupNotification'); // Replace with the correct path

jest.mock('firebase-admin', () => {
  const messagingSendMock = jest.fn();
  const messagingMock = jest.fn(() => ({ send: messagingSendMock }));

  return {
    messaging: messagingMock
  };
});

jest.mock('../../services/userService', () => ({
  getUserById: jest.fn()
}));

describe('createGroup', () => {
  // ChatGPT usage: No
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

  // ChatGPT usage: No
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

describe('joinGroup', () => {
  // ChatGPT usage: No
  it('should send a message to all users in a group when a new user joins', async () => {
    const mockUsers = [
      { deviceRegistrationToken: 'mockDeviceToken1' },
      { deviceRegistrationToken: 'mockDeviceToken2' },
      { deviceRegistrationToken: 'mockDeviceToken3' }
    ];
    const mockGroupData = {
      groupName: 'TestGroup',
      members: [{ userId: 1 }, { userId: 2 }, { userId: 3 }]
    };
    const mockNewUser = { username: 'mockUsername' };

    userService.getUserById
      .mockResolvedValueOnce(mockUsers[0])
      .mockResolvedValueOnce(mockUsers[1])
      .mockResolvedValueOnce(mockUsers[2]);
    admin.messaging().send.mockResolvedValue('message sent successfully');

    await joinGroup(mockNewUser, mockGroupData);

    expect(admin.messaging().send).toHaveBeenCalledTimes(3);
  });

  // ChatGPT usage: No
  it('should catch error if sending notification when creating a group was unsuccessful', async () => {
    const mockUsers = [
      { deviceRegistrationToken: 'mockDeviceToken1' },
      { deviceRegistrationToken: 'mockDeviceToken2' },
      { deviceRegistrationToken: 'mockDeviceToken3' }
    ];
    const mockGroupData = {
      groupName: 'TestGroup',
      members: [{ userId: 1 }, { userId: 2 }, { userId: 3 }]
    };
    const mockNewUser = { username: 'mockUsername' };

    userService.getUserById
      .mockResolvedValueOnce(mockUsers[0])
      .mockResolvedValueOnce(mockUsers[1])
      .mockResolvedValueOnce(mockUsers[2]);
    admin.messaging().send.mockRejectedValue('message was not sent successfully');

    await joinGroup(mockNewUser, mockGroupData);

    expect(admin.messaging().send).toHaveBeenCalledTimes(3);
  });
});
