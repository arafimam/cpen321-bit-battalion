const groupService = require('../groupService.js');
const listService = require('../listService.js');
const groupModel = require('../../models/groupModel.js');

jest.mock('../../models/groupModel.js', () => ({
  createGroup: jest.fn(),
  generateUniqueGroupCode: jest.fn(),
  getAllGroups: jest.fn(),
  getGroup: jest.fn(),
  getGroupLists: jest.fn(),
  addUserToGroup: jest.fn(),
  removeUserFromGroup: jest.fn(),
  addListToGroup: jest.fn(),
  removeListFromGroup: jest.fn(),
  deleteGroup: jest.fn()
}));

jest.mock('../../services/listService.js', () => ({
  getListName: jest.fn(),
  createList: jest.fn(),
  getPlacesByListId: jest.fn(),
  deleteListById: jest.fn()
}));

describe('getAllGroups', () => {
  it('returns groups when getAllGroups is successful', async () => {
    const mockUserId = 'mockUserId';
    const mockGroups = [
      { id: '1', name: 'Group 1' },
      { id: '2', name: 'Group 2' }
    ];

    groupModel.getAllGroups.mockResolvedValue(mockGroups);

    const result = await groupService.getAllGroups(mockUserId);

    expect(result).toEqual(mockGroups);
    expect(groupModel.getAllGroups).toHaveBeenCalledWith(mockUserId);
  });

  it('throws an error when getAllGroups fails', async () => {
    const mockUserId = 'mockUserId';

    groupModel.getAllGroups.mockRejectedValue(new Error('error in groups model'));

    await expect(groupService.getAllGroups(mockUserId)).rejects.toThrowError(
      'Error in service while getting group by id: error in groups model'
    );

    expect(groupModel.getAllGroups).toHaveBeenCalledWith(mockUserId);
  });
});

describe('getGroupById', () => {
  it('returns group when getGroupById is successful', async () => {
    const mockGroupId = 'mock-group-id';
    const mockGroup = [{ id: '1', name: 'Group 1' }];

    groupModel.getGroup.mockResolvedValue(mockGroup);

    const result = await groupService.getGroupById(mockGroupId);

    expect(result).toEqual(mockGroup);
  });
});

describe('createGroup', () => {
  it('returns groupCode when createGroup is successful', async () => {
    const mockGroupData = {
      ownerId: 'mock-owner-id',
      ownerName: 'mock-owner-name',
      groupName: 'mock-group-name'
    };
    const mockGroupCode = 'ABC123';

    groupModel.generateUniqueGroupCode.mockResolvedValue(mockGroupCode);
    groupModel.createGroup.mockResolvedValue();

    const result = await groupService.createGroup(mockGroupData);

    expect(result).toBe(mockGroupCode);
  });
});

describe('deleteGroup', () => {
  it('returns true when deleteGroup is successful', async () => {
    const mockGroupId = 'mock-group-id';

    groupModel.deleteGroup.mockResolvedValue(true);

    const result = await groupService.deleteGroup(mockGroupId);

    expect(result).toBe(true);
  });
});

describe('addUserToGroup', () => {
  it('adds a user to the group successfully', async () => {
    const mockGroupCode = 'mock-group-code';
    const mockUserData = {
      userId: 'mock-user-id',
      username: 'mock-username'
    };

    groupModel.addUserToGroup.mockResolvedValue(true);

    const result = await groupService.addUserToGroup(mockGroupCode, mockUserData);

    expect(result).toBe(true);
    expect(groupModel.addUserToGroup).toHaveBeenCalledWith(mockGroupCode, {
      userId: mockUserData.userId,
      username: mockUserData.username
    });
  });
});

describe('removeUserFromGroup', () => {
  it('removes a user from the group successfully', async () => {
    const mockUserId = 'mock-user-id';
    const mockGroupCode = 'mock-group-code';

    groupModel.removeUserFromGroup.mockResolvedValue(true);

    const result = await groupService.removeUserFromGroup(mockUserId, mockGroupCode);

    expect(result).toBe(true);
    expect(groupModel.removeUserFromGroup).toHaveBeenCalledWith(mockUserId, mockGroupCode);
  });
});

describe('getListsforGroup', () => {
  it('returns lists for a group successfully', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListIds = ['list-id1', 'list-id2'];
    const mockLists = [
      { id: 'list-id1', name: 'mock-list-1' },
      { id: 'list-id2', name: 'mock-list-2' }
    ];

    groupModel.getGroupLists.mockResolvedValue({ lists: mockListIds });

    listService.getListName.mockImplementation(async (listId) => {
      const list = mockLists.find((l) => l.id === listId);
      return list;
    });

    const result = await groupService.getListsforGroup(mockGroupId);

    expect(result).toEqual(mockLists);
  });

  it('handles errors when getting lists for a group', async () => {
    const mockGroupId = 'mock-group-id';

    groupModel.getGroupLists.mockRejectedValue(new Error('Failed to get lists for group'));

    await expect(groupService.getListsforGroup(mockGroupId)).rejects.toThrowError(
      'Error in service while getting lists for group: Failed to get lists for group'
    );
  });
});

describe('addListToGroup', () => {
  it('adds a list to the group successfully', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListName = 'mock-list-name';
    const mockListId = 'mock-list-id';

    listService.createList.mockResolvedValue({ _id: mockListId });

    groupModel.addListToGroup.mockResolvedValue(true);

    const result = await groupService.addListToGroup(mockGroupId, mockListName);

    expect(result).toBe(true);
    expect(listService.createList).toHaveBeenCalledWith(mockListName);
    expect(groupModel.addListToGroup).toHaveBeenCalledWith(mockGroupId, mockListId);
  });

  it('throws an error when adding a list to the group fails', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListName = 'mock-list-name';
    const mockListId = 'mock-list-id';

    listService.createList.mockResolvedValue({ _id: mockListId });

    groupModel.addListToGroup.mockRejectedValue(new Error('Failed to add list to group'));

    await expect(groupService.addListToGroup(mockGroupId, mockListName)).rejects.toThrowError(
      'Error in service while adding list to group: Failed to add list to group'
    );
  });
});

describe('removeListFromGroup', () => {
  it('removes a list from the group successfully', async () => {
    const mockGroupId = 'mock-group-id';
    const mockListId = 'mock-list-id';

    groupModel.removeListFromGroup.mockResolvedValue(true);

    const result = await groupService.removeListFromGroup(mockGroupId, mockListId);

    expect(result).toBe(true);
  });
});
