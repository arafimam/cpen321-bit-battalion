const userService = require('../userService.js');
const listService = require('../listService.js');
const userModel = require('../../models/userModel.js');

jest.mock('google-auth-library', () => ({
  OAuth2Client: jest.fn(() => ({
    verifyIdToken: jest.fn(() => Promise.resolve({ getPayload: jest.fn().mockReturnValue({ sub: 'mock-google-id' }) }))
  }))
}));

jest.mock('../../models/userModel.js', () => ({
  User: {
    create: jest.fn(),
    updateDeviceRegistrationToken: jest.fn()
  },
  checkUserExists: jest.fn(),
  updateDeviceRegistrationToken: jest.fn(),
  getUserById: jest.fn(),
  getUserByGoogleId: jest.fn(),
  getUserLists: jest.fn(),
  addListForUser: jest.fn(),
  removeListForUser: jest.fn()
}));

jest.mock('../listService.js', () => ({
  createList: jest.fn(),
  deleteListById: jest.fn(),
  getListName: jest.fn()
}));

const mockUser = {
  _id: 'mock-user-id',
  username: 'mock-username',
  deviceRegistrationToken: 'mock-device-registration-token'
};
const mockGoogleId = 'mock-google-id';
const mockUserId = 'mockUserId';
const mockDeviceRegistrationToken = 'mockDeviceRegistrationToken';

describe('verify function', () => {
  // ChatGPT usage: Yes
  it('verifies the ID token and returns the Google ID', async () => {
    const mockIdToken = 'mock-id-token';

    const result = await userService.verify(mockIdToken);

    expect(result).toBe(mockGoogleId);
  });
});

describe('createUser function', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  // ChatGPT usage: Yes
  it('should create a new user if user does not exist', async () => {
    const userData = {
      username: 'testUser',
      googleId: 'testGoogleId',
      deviceRegistrationToken: 'testToken'
    };

    // Mock userExists check
    userModel.checkUserExists.mockResolvedValue(false);

    // Mock user creation
    userModel.User.create.mockResolvedValue({ username: 'testUser' });

    // Call the createUser function
    const result = await userService.createUser(userData);

    // Assertions
    expect(result).toBe('testUser');
  });

  // ChatGPT usage: Yes
  it('should handle error during user creation', async () => {
    const userData = {
      username: 'testUser',
      googleId: 'testGoogleId',
      deviceRegistrationToken: 'testToken'
    };

    // Mock userExists check
    userModel.checkUserExists.mockResolvedValue(false);

    // Mock user creation
    userModel.User.create.mockRejectedValue(new Error('failed to create user'));

    // Assertions
    await expect(userService.createUser(userData)).rejects.toThrow(
      'Error in service while creating user: failed to create user'
    );
  });

  // ChatGPT usage: Yes
  it('should update device registration token if user exists', async () => {
    // Mock user data
    const userData = {
      username: 'existingUser',
      googleId: 'testGoogleId',
      deviceRegistrationToken: 'newTestToken'
    };

    // Mock userExists check
    userModel.checkUserExists.mockResolvedValue(true);

    // Mock device token update
    userModel.updateDeviceRegistrationToken.mockResolvedValue();

    // Call the createUser function
    const result = await userService.createUser(userData);

    // Assertions
    expect(result).toBe('existingUser');
  });

  // ChatGPT usage: Yes
  it('should handle error if update device registration token fails', async () => {
    // Mock user data
    const userData = {
      username: 'existingUser',
      googleId: 'testGoogleId',
      deviceRegistrationToken: 'newTestToken'
    };

    // Mock userExists check
    userModel.checkUserExists.mockResolvedValue(true);

    // Mock device token update
    userModel.updateDeviceRegistrationToken.mockRejectedValue(new Error('Error'));

    // Assertions
    await expect(userService.createUser(userData)).rejects.toThrow(
      'Error in service while updating device registration token: Error'
    );
  });

  // ChatGPT usage: Yes
  it('should handle errors if user exists check fails', async () => {
    // Mock user data
    const userData = {
      username: 'testUser',
      googleId: 'testGoogleId',
      deviceRegistrationToken: 'testToken'
    };

    // Mock userExists check with an error
    userModel.checkUserExists.mockRejectedValue(new Error('User exists check failed'));

    // Call the createUser function and expect it to throw an error
    await expect(userService.createUser(userData)).rejects.toThrow(
      'Error while checking if user exists: User exists check failed'
    );
  });
});

describe('getUserByGoogleId', () => {
  // ChatGPT usage: No
  it('returns user data for a valid Google ID', async () => {
    userModel.getUserByGoogleId.mockResolvedValue(mockUser);

    const result = await userService.getUserByGoogleId(mockGoogleId);

    expect(result).toEqual({
      userId: mockUser._id,
      username: mockUser.username,
      deviceRegistrationToken: mockUser.deviceRegistrationToken
    });
  });

  // ChatGPT usage: No
  it('throws an error for an invalid Google ID', async () => {
    userModel.getUserByGoogleId.mockResolvedValue(null);

    await expect(userService.getUserByGoogleId(mockGoogleId)).rejects.toThrowError(
      'Could not find user with the given google id'
    );
  });
});

describe('getUserById', () => {
  // ChatGPT usage: No
  it('returns user data for a valid user ID', async () => {
    userModel.getUserById.mockResolvedValue(mockUser);

    const result = await userService.getUserById(mockUserId);

    expect(userModel.getUserById).toHaveBeenCalledWith(mockUserId);
    expect(result).toEqual({
      userId: mockUser._id,
      username: mockUser.username,
      deviceRegistrationToken: mockUser.deviceRegistrationToken
    });
  });

  // ChatGPT usage: No
  it('throws an error for an invalid user ID', async () => {
    userModel.getUserById.mockResolvedValue(null);

    await expect(userService.getUserById(mockUserId)).rejects.toThrowError(
      'Could not find user with the given user id'
    );
  });
});

describe('updateDeviceRegistrationToken function', () => {
  // ChatGPT usage: No
  it('updates the device registration token for a valid Google ID', async () => {
    userModel.updateDeviceRegistrationToken.mockResolvedValue({
      _id: 'mockUserId',
      username: 'mockUsername',
      deviceRegistrationToken: mockDeviceRegistrationToken
    });

    const result = await userService.updateDeviceRegistrationToken(mockGoogleId, mockDeviceRegistrationToken);

    expect(result).toEqual({
      _id: 'mockUserId',
      username: 'mockUsername',
      deviceRegistrationToken: mockDeviceRegistrationToken
    });
  });

  // ChatGPT usage: No
  it('throws an error for an invalid Google ID', async () => {
    userModel.updateDeviceRegistrationToken.mockRejectedValue(new Error('Failed to update device registration token'));

    await expect(
      userService.updateDeviceRegistrationToken(mockGoogleId, mockDeviceRegistrationToken)
    ).rejects.toThrowError('Error in service while updating device registration token');
  });
});

describe('addListForUser', () => {
  // ChatGPT usage: No
  it('adds a list for a valid user ID and list name', async () => {
    const mockListName = 'mockListName';
    const mockListId = 'mockListId';

    listService.createList.mockResolvedValue({
      _id: mockListId,
      name: mockListName
    });

    userModel.addListForUser.mockResolvedValue({
      _id: 'mockUserId',
      username: 'mockUsername',
      lists: [mockListId]
    });

    const result = await userService.addListForUser(mockUserId, mockListName);

    expect(result).toEqual({
      _id: 'mockUserId',
      username: 'mockUsername',
      lists: [mockListId]
    });
  });

  // ChatGPT usage: No
  it('throws an error for an invalid user ID', async () => {
    const mockUserId = 'invalidUserId';
    const mockListName = 'mockListName';

    listService.createList.mockResolvedValue({
      _id: 'mockListId',
      name: mockListName
    });

    userModel.addListForUser.mockRejectedValue(new Error('Failed to add list for user'));

    await expect(userService.addListForUser(mockUserId, mockListName)).rejects.toThrowError(
      'Error in service while adding list for user'
    );
  });
});

describe('removeListForUser', () => {
  // ChatGPT usage: No
  it('removes a list for a valid user ID and list ID', async () => {
    const mockListId = 'mockListId';

    userModel.removeListForUser.mockResolvedValue({
      _id: 'mockUserId',
      username: 'mockUsername',
      lists: []
    });

    const result = await userService.removeListForUser(mockUserId, mockListId);

    expect(result).toEqual({
      _id: 'mockUserId',
      username: 'mockUsername',
      lists: []
    });
  });

  // ChatGPT usage: No
  it('throws an error for an invalid user ID', async () => {
    const mockUserId = 'invalidUserId';
    const mockListId = 'mockListId';

    userModel.removeListForUser.mockRejectedValue(new Error('Failed to remove list for user'));

    await expect(userService.removeListForUser(mockUserId, mockListId)).rejects.toThrowError(
      'Error in service while removing list for user'
    );
  });
});

describe('getListsforUser', () => {
  // ChatGPT usage: No
  it('returns lists for a valid user ID', async () => {
    const mockListIds = ['mockListId1', 'mockListId2'];
    const mockLists = [
      { _id: 'mockListId1', name: 'mockListName1' },
      { _id: 'mockListId2', name: 'mockListName2' }
    ];

    userModel.getUserLists.mockResolvedValue({ lists: mockListIds });

    listService.getListName.mockImplementation(async (listId) => {
      const foundList = mockLists.find((list) => list._id === listId);
      return foundList ? foundList.name : null;
    });

    const result = await userService.getListsforUser(mockUserId);

    expect(result).toEqual(['mockListName1', 'mockListName2']);
  });

  // ChatGPT usage: No
  it('throws an error for an invalid user ID', async () => {
    const mockUserId = 'invalidUserId';

    userModel.getUserLists.mockRejectedValue(new Error('Failed to get lists for user'));

    await expect(userService.getListsforUser(mockUserId)).rejects.toThrowError(
      'Error in service while getting lists for user'
    );
  });
});
