const supertest = require('supertest');

const { app } = require('../../app.js');
const groupService = require('../../services/groupService.js');
const groupNotification = require('../../notifications/groupNotification.js');

const request = supertest(app);

jest.mock('../../middleware/middleware.js', () => ({
  verifyToken: (req, res, next) => next(),
  getUser: (req, res, next) => {
    res.locals.user = { _id: '1234' };
    next();
  }
}));

jest.mock('../../services/groupService.js', () => ({
  getAllGroups: jest.fn(),
  getGroupById: jest.fn(),
  deleteGroup: jest.fn(),
  createGroup: jest.fn(),
  addUserToGroup: jest.fn(),
  removeUserFromGroup: jest.fn(),
  getListsforGroup: jest.fn(),
  addListToGroup: jest.fn(),
  removeListFromGroup: jest.fn()
}));

jest.mock('../../notifications/groupNotification.js', () => ({
  createGroup: jest.fn(),
  joinGroup: jest.fn()
}));

// Interface GET /groups/all
describe('GET /all get all groups test', () => {
  // Input: None
  // Expected Status Code: 200
  // Expected Behaviour: Returns a list of all groups
  // Expected Output: An array of all the groups
  // ChatGPT usage: Yes
  it('should return all groups on success', async () => {
    groupService.getAllGroups.mockResolvedValue([{ groupName: 'testGroup1' }]);

    const res = await request.get(`/groups/all`);
    expect(res.status).toBe(200);
    expect(res.body.groups).toEqual([{ groupName: 'testGroup1' }]);
  });

  // Input: None
  // Expected Status Code: 500
  // Expected Behaviour: Internal Server Error
  // Expected Output: Error
  // ChatGPT usage: Yes
  it('should return a 500 status on internal error', async () => {
    groupService.getAllGroups.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/groups/all`);
    expect(res.status).toBe(500);
  });
});

// Interface GET /groups/:id
describe('GET /:id get group by id test', () => {
  const mockGroupId = 'mock-group-id';
  const invalidId = 'mock-invalid-id';

  // Input: id of the group being retrieved
  // Expected Status Code: 200
  // Expected Behaviour: Returns the group with given id
  // Expected Output: The group object
  // ChatGPT usage: No
  it('should return groups with valid id', async () => {
    groupService.getGroupById.mockResolvedValue({ groupName: 'testGroup1' });

    const res = await request.get(`/groups/${mockGroupId}`);
    expect(res.status).toBe(200);
    expect(res.body.group).toStrictEqual({ groupName: 'testGroup1' });
  });

  // Input: invalid id
  // Expected Status Code: 400
  // Expected Behaviour: Returns a 400 status code
  // Expected Output: Bad request status
  // ChatGPT usage: No
  it('should return a 400 status code with invalid id', async () => {
    groupService.getGroupById.mockResolvedValue(null);

    const res = await request.get(`/groups/${invalidId}`);
    expect(res.status).toBe(400);
  });

  // Input: valid id
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal error
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while getting group by id', async () => {
    groupService.getGroupById.mockRejectedValue(new Error('something went wrong'));

    const res = await request.get(`/groups/${invalidId}`);
    expect(res.status).toBe(500);
  });
});

// Interface POST /groups/create
describe('POST /create create new group', () => {
  const successNotif = 'mock-successful-notification';

  // Input: valid groupName
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code and creates new group
  // Expected Output: Returns the group code for new group
  // ChatGPT usage: No
  it('should return group code on successful creation', async () => {
    const mockGroupCode = 'mock-group-code';
    groupService.createGroup.mockResolvedValue(mockGroupCode);
    groupNotification.createGroup.mockResolvedValue(successNotif);

    const res = await request.post(`/groups/create`).send({ groupName: 'mock-group-name' });
    expect(res.status).toBe(200);
    expect(res.body.groupCode).toBe(mockGroupCode);
  });

  // Input: valid groupName
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code and creates new group
  // Expected Output: Returns the group code for new group despite notif failure
  // ChatGPT usage: No
  it('should still create group despite failed creation notification', async () => {
    const mockGroupCode = 'mock-group-code';
    groupService.createGroup.mockResolvedValue(mockGroupCode);
    groupNotification.createGroup.mockRejectedValue(new Error('notification failed'));

    const res = await request.post(`/groups/create`).send({ groupName: 'mock-group-name' });
    expect(res.status).toBe(200);
  });

  // Input: invalid/missing groupName
  // Expected Status Code: 400
  // Expected Behaviour: fails to create a group due to missing/invalid groupName
  // Expected Output: Returns an error message stating to provide a valid group name
  // ChatGPT usage: No
  it('invalid/missing groupName', async () => {
    const res = await request.post(`/groups/create`);
    expect(res.status).toBe(400);
  });

  // Input: valid groupName
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal error
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong during creation', async () => {
    groupService.createGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.post(`/groups/create`).send({ groupName: 'mock-group-name' });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /groups/join
describe('PUT /join add user to group', () => {
  const successNotif = 'mock-successful-join-notification';

  // Input: valid groupCode
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code and user joins group
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should return a 200 status code on successful join', async () => {
    groupService.addUserToGroup.mockResolvedValue({
      userAlreadyInGroup: false,
      group: { groupName: 'testGroup1' }
    });
    groupNotification.joinGroup.mockResolvedValue(successNotif);

    const res = await request.put(`/groups/join`).send({ groupCode: 'mock-group-code' });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('User successfully added to group');
  });

  // Input: valid groupCode
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code and user joins group despite notif fail
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should join successfully despite a failed notification', async () => {
    groupService.addUserToGroup.mockResolvedValue({
      userAlreadyInGroup: false,
      group: { groupName: 'testGroup1' }
    });
    groupNotification.joinGroup.mockRejectedValue(new Error('notification failed'));

    const res = await request.put(`/groups/join`).send({ groupCode: 'mock-group-code' });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('User successfully added to group');
  });

  // Input: incorrect groupCode
  // Expected Status Code: 400
  // Expected Behaviour: Returns a 400 status code and join fails
  // Expected Output: Incorrect group code message
  // ChatGPT usage: No
  it('should return a 400 status code on joining with incorrect code', async () => {
    groupService.addUserToGroup.mockResolvedValue({
      userAlreadyInGroup: false,
      group: null
    });

    const res = await request.put(`/groups/join`).send({ groupCode: 'mock-group-code' });
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Incorrect group code');
  });

  // Input: invalid/missing groupCode
  // Expected Status Code: 400
  // Expected Behaviour: fails to create a group due to missing/invalid groupCode
  // Expected Output: Returns an error message stating to provide a group code
  // ChatGPT usage: No
  it('invalid/missing groupCode', async () => {
    const res = await request.put(`/groups/join`);
    expect(res.status).toBe(400);
  });

  // Input: valid groupCode
  // Expected Status Code: 400
  // Expected Behaviour: Fails to add user to group since user is already in the group
  // Expected Output: User already in group message
  // ChatGPT usage: No
  it('user already exists in the group', async () => {
    groupService.addUserToGroup.mockResolvedValue({
      userAlreadyInGroup: true,
      group: null
    });

    const res = await request.put(`/groups/join`).send({ groupCode: 'mock-group-code' });
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('User already in group');
  });

  // Input: valid groupCode
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while joining', async () => {
    groupService.addUserToGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/join`).send({ groupCode: 'mock-group-code' });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /groups/:id/leave
describe('PUT /:id/leave remove user from group', () => {
  const successResult = 'mock-successful-removal-result';
  const mockGroupId = 'mock-group-id';

  // Input: valid groupId
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status and user leaves group
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal', async () => {
    groupService.removeUserFromGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/leave`);
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('User successfully removed from group');
  });

  // Input: valid groupId
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while removing', async () => {
    groupService.removeUserFromGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/leave`);
    expect(res.status).toBe(500);
  });
});

// Interface GET /groups/:id/lists
describe('GET /:id/lists get lists for group of id', () => {
  const successResult = 'mock-successful-removal-result';
  const mockGroupId = 'mock-group-id';

  // Input: valid groupId
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code and returns lists
  // Expected Output: Lists for group with given groupId
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal', async () => {
    groupService.getListsforGroup.mockResolvedValue(successResult);

    const res = await request.get(`/groups/${mockGroupId}/lists`);
    expect(res.status).toBe(200);
  });

  // Input: valid groupCode
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while getting', async () => {
    groupService.getListsforGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.get(`/groups/${mockGroupId}/lists`);
    expect(res.status).toBe(500);
  });
});

// Interface PUT /groups/:id/add/list
describe('PUT /:id/add/list add list to group', () => {
  const successResult = 'mock-successful-add-result';
  const mockGroupId = 'mock-group-id';
  const mockListName = 'mock-list-name';

  // Input: valid groupId, listName
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code on successful addition
  // Expected Output: Success Message
  // ChatGPT usage: No
  it('should return a 200 status code on successful addition', async () => {
    groupService.addListToGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('New list successfully added to group');
  });

  // Input: valid groupId, missing listName
  // Expected Status Code: 400
  // Expected Behaviour: Returns a 400 status code and failed addition
  // Expected Output: Missing listName error message
  // ChatGPT usage: No
  it('should return a 400 status code if missing list name', async () => {
    const res = await request.put(`/groups/${mockGroupId}/add/list`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Please provide a list name');
  });

  // Input: valid groupId, listName
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while adding list', async () => {
    groupService.addListToGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /groups/:id/remove/list
describe('PUT /:id/remove/list remove list from group', () => {
  const successResult = 'mock-successful-add-result';
  const mockGroupId = 'mock-group-id';
  const mockListId = 'mock-list-id';

  // Input: valid groupId, listId
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code on successful removal
  // Expected Output: Success Message
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal', async () => {
    groupService.removeListFromGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/remove/list`).send({ listId: mockListId });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('List successfully removed to group');
  });

  // Input: valid groupId, missing listId
  // Expected Status Code: 400
  // Expected Behaviour: Returns a 400 status code and fails to remove
  // Expected Output: Missing listId error message
  // ChatGPT usage: No
  it('should return a 400 status code if missing list id', async () => {
    const res = await request.put(`/groups/${mockGroupId}/remove/list`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Please provide a listId');
  });

  // Input: valid groupId, listId
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while removing list', async () => {
    groupService.removeListFromGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/remove/list`).send({ listId: mockListId });
    expect(res.status).toBe(500);
  });
});

// Interface DELETE /groups/:id
describe('DELETE /:id delete group by id test', () => {
  const mockGroupId = 'mock-group-id';

  // Input: valid groupId
  // Expected Status Code: 200
  // Expected Behaviour: Returns a 200 status code on successful deletion
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should delete group with valid id', async () => {
    groupService.deleteGroup.mockResolvedValue([{ message: 'mock deletion successful' }]);

    const res = await request.delete(`/groups/${mockGroupId}/delete`);
    expect(res.status).toBe(200);
  });

  // Input: valid groupId
  // Expected Status Code: 500
  // Expected Behaviour: Returns a 500 status code on internal failure
  // Expected Output: Internal Server Error
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong during deletion', async () => {
    groupService.deleteGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.delete(`/groups/${mockGroupId}/delete`);
    expect(res.status).toBe(500);
  });
});
