const groupModel = require('../groupModel.js');

let mockCreate = jest.spyOn(groupModel.Group, 'create');
let mockFind = jest.spyOn(groupModel.Group, 'find');
let mockFindById = jest.spyOn(groupModel.Group, 'findById');
let mockFindOne = jest.spyOn(groupModel.Group, 'findOne');
let mockFindByIdAndDelete = jest.spyOn(groupModel.Group, 'findByIdAndDelete');
let mockUpdateOne = jest.spyOn(groupModel.Group, 'updateOne');
let mockFindOneAndUpdate = jest.spyOn(groupModel.Group, 'findOneAndUpdate');

beforeEach(() => {
  jest.clearAllMocks();
});

describe('addUserToGroup', () => {
  // ChatGPT usage: No
  it('returns group when adding a user using addUserToGroup is successful', async () => {
    const mockGroupCode = 'mock-group-code';
    const mockMember = { userId: 'mock-user-id', username: 'mock-username' };
    const mockGroupData = {
      groupCode: mockGroupCode,
      groupId: 'mock-group-id'
    };

    mockFindOne.mockResolvedValue(null);
    mockFindOneAndUpdate.mockResolvedValue(mockGroupData);
    const result = await groupModel.addUserToGroup(mockGroupCode, mockMember);

    expect(result).toBe(mockGroupData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { groupCode: mockGroupCode },
      { $push: { members: mockMember } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when addUserToGroup is unsuccessful', async () => {
    const mockGroupCode = 'mock-group-code';
    const mockMember = { userId: 'mock-user-id', username: 'mock-username' };

    mockFindOneAndUpdate.mockRejectedValue(new Error('Adding user to group failed'));

    await expect(groupModel.addUserToGroup(mockGroupCode, mockMember)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { groupCode: mockGroupCode },
      { $push: { members: mockMember } },
      { new: true }
    );
  });

  it('user already in group in addUserToGroup', async () => {
    const mockGroupCode = 'mock-group-code';
    const mockMember = { userId: 'mock-user-id', username: 'mock-username' };

    mockFindOne.mockResolvedValue(mockMember);
    await expect(groupModel.addUserToGroup(mockGroupCode, mockMember)).rejects.toThrowError();
  });
});

describe('createGroup', () => {
  // ChatGPT usage: No
  it('returns group object when createGroup is successful', async () => {
    const mockGroupData = {
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name',
      groupCode: 'mock-group-code'
    };

    mockCreate.mockResolvedValue(mockGroupData);
    const result = await groupModel.createGroup(mockGroupData);

    expect(result).toBe(mockGroupData);
  });

  // ChatGPT usage: No
  it('throws error when createGroup is unsuccessful', async () => {
    const mockGroupData = {
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name',
      groupCode: 'mock-group-code'
    };

    mockCreate.mockRejectedValue(new Error('Group creation failed'));
    await expect(groupModel.createGroup(mockGroupData)).rejects.toThrowError();
    expect(mockCreate).toHaveBeenCalledWith(mockGroupData);
  });
});

describe('deleteGroup', () => {
  // ChatGPT usage: No
  it('returns group object when deleteGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockGroupData = {
      groupId: mockGroupId,
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name',
      groupCode: 'mock-group-code'
    };

    mockFindByIdAndDelete.mockResolvedValue(mockGroupData);
    const result = await groupModel.deleteGroup(mockGroupId);

    expect(result).toBe(mockGroupData);
  });

  // ChatGPT usage: No
  it('throws error when deleteGroup is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    mockFindByIdAndDelete.mockRejectedValue(new Error('Group deletion failed'));

    await expect(groupModel.deleteGroup(mockGroupId)).rejects.toThrowError();
    expect(mockFindByIdAndDelete).toHaveBeenCalledWith(mockGroupId);
  });
});

describe('getAllGroups', () => {
  // ChatGPT usage: No
  it('returns a list of group when getAllGroups is successful', async () => {
    const mockUserId = 'mock-user-id';
    const mockGroupData = {
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name',
      groupCode: 'mock-group-code',
      members: [{ userId: mockUserId }]
    };

    mockFind.mockReturnValue({
      select: jest.fn().mockResolvedValue(mockGroupData)
    });
    const result = await groupModel.getAllGroups(mockUserId);

    expect(result).toBe(mockGroupData);
    expect(mockFind).toHaveBeenCalledWith({ 'members.userId': mockUserId });
  });

  // ChatGPT usage: No
  it('throws error when getAllGroups is unsuccessful', async () => {
    const mockUserId = 'mock-user-id';
    mockFind.mockReturnValue(new Error('Getting groups failed'));

    await expect(groupModel.getAllGroups(mockUserId)).rejects.toThrowError();
    expect(mockFind).toHaveBeenCalledWith({ 'members.userId': mockUserId });
  });
});

describe('getGroup', () => {
  // ChatGPT usage: No
  it('returns a group by Id when getGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockGroupData = {
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name',
      groupCode: 'mock-group-code',
      groupId: mockGroupId
    };

    mockFindById.mockResolvedValue(mockGroupData);
    const result = await groupModel.getGroup(mockGroupId);

    expect(result).toBe(mockGroupData);
    expect(mockFindById).toHaveBeenCalledWith(mockGroupId);
  });

  // ChatGPT usage: No
  it('throws error when getGroup is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    mockFindById.mockRejectedValue(new Error('Getting group by id failed'));

    await expect(groupModel.getGroup(mockGroupId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockGroupId);
  });
});

describe('getGroupLists', () => {
  // ChatGPT usage: No
  it('returns lists within a group when getGroupLists is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockGroupData = {
      lists: ['list-id-1', 'list-id-2']
    };

    mockFindById.mockReturnValue({
      select: jest.fn().mockResolvedValue(mockGroupData)
    });
    const result = await groupModel.getGroupLists(mockGroupId);

    expect(result).toBe(mockGroupData);
    expect(mockFindById).toHaveBeenCalledWith(mockGroupId);
  });

  // ChatGPT usage: No
  it('throws error when getGroupLists is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    mockFindById.mockReturnValue(new Error('Getting group by id failed'));

    await expect(groupModel.getGroupLists(mockGroupId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockGroupId);
  });
});

describe('generateUniqueGroupCode', () => {
  // ChatGPT usage: No
  it.each([false, true])('generateUniqueGroupCode generates a code', async (groupAlreadyExists) => {
    mockFindOne.mockResolvedValueOnce(groupAlreadyExists).mockReturnValueOnce(false);
    const result = await groupModel.generateUniqueGroupCode();
    expect(result.length).toBe(6);
    expect(mockFindOne).toHaveBeenCalled();
  });
});

describe('removeUserFromGroup', () => {
  // ChatGPT usage: No
  it('returns group when removing a user using removeUserFromGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockUserId = 'mock-user-id';
    const mockGroupData = {
      groupId: mockGroupId,
      members: [mockUserId]
    };

    mockFindOneAndUpdate.mockResolvedValue(mockGroupData);
    const result = await groupModel.removeUserFromGroup(mockGroupId, mockUserId);

    expect(result).toBe(mockGroupData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockGroupId },
      { $pull: { members: { userId: mockUserId } } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when removeUserFromGroup is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockUserId = 'mock-user-id';

    mockFindOneAndUpdate.mockRejectedValue(new Error('Removing user from group failed'));

    await expect(groupModel.removeUserFromGroup(mockGroupId, mockUserId)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockGroupId },
      { $pull: { members: { userId: mockUserId } } },
      { new: true }
    );
  });
});

describe('addListToGroup', () => {
  // ChatGPT usage: No
  it('returns group when adding a list using addListToGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListId = 'mock-list-id';
    const mockGroupData = {
      groupId: mockGroupId,
      lists: []
    };

    mockFindOneAndUpdate.mockResolvedValue(mockGroupData);
    const result = await groupModel.addListToGroup(mockGroupId, mockListId);

    expect(result).toBe(mockGroupData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockGroupId },
      { $push: { lists: mockListId } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when addListToGroup is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListId = 'mock-list-id';

    mockFindOneAndUpdate.mockRejectedValue(new Error('Adding list to group failed'));

    await expect(groupModel.addListToGroup(mockGroupId, mockListId)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockGroupId },
      { $push: { lists: mockListId } },
      { new: true }
    );
  });
});

describe('removeListFromGroup', () => {
  // ChatGPT usage: No
  it('returns group when removing a list using removeListFromGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListId = 'mock-list-id';
    const mockGroupData = {
      groupId: mockGroupId,
      lists: [mockListId]
    };

    mockUpdateOne.mockResolvedValue(mockGroupData);
    const result = await groupModel.removeListFromGroup(mockGroupId, mockListId);

    expect(result).toBe(mockGroupData);
    expect(mockUpdateOne).toHaveBeenCalledWith({ _id: mockGroupId }, { $pull: { lists: mockListId } }, { new: true });
  });

  // ChatGPT usage: No
  it('throws error when addListToGroup is unsuccessful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListId = 'mock-list-id';

    mockUpdateOne.mockRejectedValue(new Error('Removing list from group failed'));

    await expect(groupModel.removeListFromGroup(mockGroupId, mockListId)).rejects.toThrowError();
    expect(mockUpdateOne).toHaveBeenCalledWith({ _id: mockGroupId }, { $pull: { lists: mockListId } }, { new: true });
  });
});
