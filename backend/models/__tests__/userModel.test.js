const userModel = require('../userModel.js');

const mockExists = jest.spyOn(userModel.User, 'exists');
const mockFindById = jest.spyOn(userModel.User, 'findById');
const mockFindOne = jest.spyOn(userModel.User, 'findOne');
const mockFindOneAndUpdate = jest.spyOn(userModel.User, 'findOneAndUpdate');

describe('checkUserExists', () => {
  // ChatGPT usage: No
  it.each([true, false])('returns true or false when checkUserExists is successful', async (userExists) => {
    const mockGoogleId = 'mock-google-id';

    mockExists.mockResolvedValue(userExists);
    const result = await userModel.checkUserExists(mockGoogleId);

    expect(result).toBe(userExists);
    expect(mockExists).toHaveBeenCalledWith({ googleId: mockGoogleId });
  });

  // ChatGPT usage: No
  it('throws error when checkUserExists is unsuccessful', async () => {
    const mockGoogleId = 'mock-google-id';

    mockExists.mockRejectedValue(new Error('User exists check failed'));

    await expect(userModel.checkUserExists(mockGoogleId)).rejects.toThrowError();
    expect(mockExists).toHaveBeenCalledWith({ googleId: mockGoogleId });
  });
});

describe('getUserByGoogleId', () => {
  // ChatGPT usage: No
  it('returns a user by googleId when getUserByGoogleId is successful', async () => {
    const mockGoogleId = 'mock-google-id';
    const mockUserData = {
      googleId: mockGoogleId,
      _id: 'mock-user-id',
      username: 'mock-username'
    };

    mockFindOne.mockResolvedValue(mockUserData);
    const result = await userModel.getUserByGoogleId(mockGoogleId);

    expect(result).toBe(mockUserData);
    expect(mockFindOne).toHaveBeenCalledWith({ googleId: mockGoogleId });
  });

  // ChatGPT usage: No
  it('throws error when getUserByGoogleId is unsuccessful', async () => {
    const mockGoogleId = 'mock-google-id';
    mockFindOne.mockRejectedValue(new Error('Getting user by googleId failed'));

    await expect(userModel.getUserByGoogleId(mockGoogleId)).rejects.toThrowError();
    expect(mockFindOne).toHaveBeenCalledWith({ googleId: mockGoogleId });
  });
});

describe('getUserById', () => {
  // ChatGPT usage: No
  it('returns a user by Id when getUserById is successful', async () => {
    const mockUserId = 'mock-user-id';
    const mockUserData = {
      googleId: 'mock-google-id',
      _id: mockUserId,
      username: 'mock-username'
    };

    mockFindById.mockResolvedValue(mockUserData);
    const result = await userModel.getUserById(mockUserId);

    expect(result).toBe(mockUserData);
    expect(mockFindById).toHaveBeenCalledWith(mockUserId);
  });

  // ChatGPT usage: No
  it('throws error when getUserById is unsuccessful', async () => {
    const mockUserId = 'mock-user-id';
    mockFindById.mockRejectedValue(new Error('Getting user by Id failed'));

    await expect(userModel.getUserById(mockUserId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockUserId);
  });
});

describe('updateDeviceRegistrationToken', () => {
  // ChatGPT usage: No
  it('returns a user when updateDeviceRegistrationToken is successful', async () => {
    const mockGoogleId = 'mock-google-id';
    const mockDeviceRegistrationToken = 'mock-device-registration-token';
    const mockUserData = {
      googleId: mockGoogleId,
      _id: 'mock-user-id',
      username: 'mock-username'
    };

    mockFindOneAndUpdate.mockResolvedValue(mockUserData);
    const result = await userModel.updateDeviceRegistrationToken(mockGoogleId, mockDeviceRegistrationToken);

    expect(result).toBe(mockUserData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { googleId: mockGoogleId },
      { deviceRegistrationToken: mockDeviceRegistrationToken },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when updateDeviceRegistrationToken is unsuccessful', async () => {
    const mockGoogleId = 'mock-google-id';
    const mockDeviceRegistrationToken = 'mock-device-registration-token';
    mockFindOneAndUpdate.mockRejectedValue(new Error('Updating device registration token failed'));

    await expect(
      userModel.updateDeviceRegistrationToken(mockGoogleId, mockDeviceRegistrationToken)
    ).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { googleId: mockGoogleId },
      { deviceRegistrationToken: mockDeviceRegistrationToken },
      { new: true }
    );
  });
});

describe('getUserLists', () => {
  // ChatGPT usage: No
  it('returns a list of lists when getUserLists is successful', async () => {
    const mockUserId = 'mock-user-id';
    const mockListsData = [{ listName: 'mock-list-name' }, { listName: 'mock-list-name-2' }];

    mockFindById.mockReturnValue({
      select: jest.fn().mockResolvedValue(mockListsData)
    });
    const result = await userModel.getUserLists(mockUserId);

    expect(result).toBe(mockListsData);
    expect(mockFindById).toHaveBeenCalledWith(mockUserId);
  });

  // ChatGPT usage: No
  it('throws error when getUserLists is unsuccessful', async () => {
    const mockUserId = 'mock-user-id';
    mockFindById.mockReturnValue(new Error('Getting user lists failed'));

    await expect(userModel.getUserLists(mockUserId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockUserId);
  });
});

describe('addListForUser', () => {
  // ChatGPT usage: No
  it('returns a user when adding a list using addListForUser is successful', async () => {
    const mockUserId = 'mock-user-id';
    const mockListId = 'mock-list-id';
    const mockUserData = {
      googleId: 'mock-google-id',
      _id: mockUserId,
      username: 'mock-username',
      lists: [mockListId]
    };

    mockFindOneAndUpdate.mockResolvedValue(mockUserData);
    const result = await userModel.addListForUser(mockUserId, mockListId);

    expect(result).toBe(mockUserData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockUserId },
      { $push: { lists: mockListId } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when addListForUser is unsuccessful', async () => {
    const mockUserId = 'mock-user-id';
    const mockListId = 'mock-list-id';

    mockFindOneAndUpdate.mockRejectedValue(new Error('Adding list for user failed'));

    await expect(userModel.addListForUser(mockUserId, mockListId)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockUserId },
      { $push: { lists: mockListId } },
      { new: true }
    );
  });
});

describe('removeListForUser', () => {
  // ChatGPT usage: No
  it('returns a user when removing a list using removeListForUser is successful', async () => {
    const mockUserId = 'mock-user-id';
    const mockListId = 'mock-list-id';
    const mockUserData = {
      googleId: 'mock-google-id',
      _id: mockUserId,
      username: 'mock-username',
      lists: []
    };

    mockFindOneAndUpdate.mockResolvedValue(mockUserData);
    const result = await userModel.removeListForUser(mockUserId, mockListId);

    expect(result).toBe(mockUserData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockUserId },
      { $pull: { lists: mockListId } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when removeListForUser is unsuccessful', async () => {
    const mockUserId = 'mock-user-id';
    const mockListId = 'mock-list-id';

    mockFindOneAndUpdate.mockRejectedValue(new Error('Removing list for user failed'));

    await expect(userModel.removeListForUser(mockUserId, mockListId)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockUserId },
      { $pull: { lists: mockListId } },
      { new: true }
    );
  });
});
