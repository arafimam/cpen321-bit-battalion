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

describe('/all get all groups test', () => {
  it('should return all groups', async () => {
    groupService.getAllGroups.mockResolvedValue([{ groupName: 'testGroup1' }]);

    const res = await request.get(`/groups/all`);
    expect(res.status).toBe(200);
  });

  it('should return a list', async () => {
    groupService.getAllGroups.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/groups/all`);
    expect(res.status).toBe(500);
  });
});

describe('GET /:id get group by id test', () => {
  const mockGroupId = 'mock-group-id';
  const invalidId = 'mock-invalid-id';
  it('should return groups with valid id', async () => {
    groupService.getGroupById.mockResolvedValue([{ groupName: 'testGroup1' }]);

    const res = await request.get(`/groups/${mockGroupId}`);
    expect(res.status).toBe(200);
  });

  it('should return a 400 status code with invalid id', async () => {
    groupService.getGroupById.mockResolvedValue(null);

    const res = await request.get(`/groups/${invalidId}`);
    expect(res.status).toBe(400);
  });

  it('should return a 500 status code if something went wrong while getting group by id', async () => {
    groupService.getGroupById.mockRejectedValue(new Error('something went wrong'));

    const res = await request.get(`/groups/${invalidId}`);
    expect(res.status).toBe(500);
  });
});

describe('POST /create create new group', () => {
  const successNotif = 'mock-successful-notification';
  it('should return group code on successful creation', async () => {
    const mockGroupCode = 'mock-group-code';
    groupService.createGroup.mockResolvedValue(mockGroupCode);
    groupNotification.createGroup.mockResolvedValue(successNotif);

    const res = await request.post(`/groups/create`);
    expect(res.status).toBe(200);
  });

  it('should still create group despite failed creation notification', async () => {
    const mockGroupCode = 'mock-group-code';
    groupService.createGroup.mockResolvedValue(mockGroupCode);
    groupNotification.createGroup.mockRejectedValue(new Error('notification failed'));

    const res = await request.post(`/groups/create`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong during creation', async () => {
    groupService.createGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.post(`/groups/create`);
    expect(res.status).toBe(500);
  });
});

describe('PUT /join add user to group', () => {
  const successNotif = 'mock-successful-join-notification';
  const successResult = 'mock-successful-join-result';
  it('should return a 200 status code on successful join', async () => {
    groupService.addUserToGroup.mockResolvedValue(successResult);
    groupNotification.joinGroup.mockResolvedValue(successNotif);

    const res = await request.put(`/groups/join`);
    expect(res.status).toBe(200);
  });

  it('should join successfully despite a failed notification', async () => {
    groupService.addUserToGroup.mockResolvedValue(successResult);
    groupNotification.joinGroup.mockRejectedValue(new Error('notification failed'));

    const res = await request.put(`/groups/join`);
    expect(res.status).toBe(200);
  });

  it('should return a 400 status code on joining with invalid code', async () => {
    groupService.addUserToGroup.mockResolvedValue(null);

    const res = await request.put(`/groups/join`);
    expect(res.status).toBe(400);
  });

  it('should return a 500 status code if something went wrong while joining', async () => {
    groupService.addUserToGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/join`);
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/leave remove user from group', () => {
  const successResult = 'mock-successful-removal-result';
  const mockGroupId = 'mock-group-id';
  it('should return a 200 status code on successful removal', async () => {
    groupService.removeUserFromGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/leave`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong while removing', async () => {
    groupService.removeUserFromGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/leave`);
    expect(res.status).toBe(500);
  });
});

describe('GET /:id/lists get lists for group of id', () => {
  const successResult = 'mock-successful-removal-result';
  const mockGroupId = 'mock-group-id';
  it('should return a 200 status code on successful removal', async () => {
    groupService.getListsforGroup.mockResolvedValue(successResult);

    const res = await request.get(`/groups/${mockGroupId}/lists`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong while getting', async () => {
    groupService.getListsforGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.get(`/groups/${mockGroupId}/lists`);
    expect(res.status).toBe(500);
  });
});

describe('PUT :id/add/list add list to group', () => {
  const successResult = 'mock-successful-add-result';
  const mockGroupId = 'mock-group-id';
  const mockListName = 'mock-list-name';
  it('should return a 200 status code on successful addition', async () => {
    groupService.addListToGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
  });

  it('should return a 400 status code if missing list name', async () => {
    const res = await request.put(`/groups/${mockGroupId}/add/list`);
    expect(res.status).toBe(400);
  });

  it('should return a 500 status code if something went wrong while adding list', async () => {
    groupService.addListToGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/remove/list remove list from group', () => {
  const successResult = 'mock-successful-add-result';
  const mockGroupId = 'mock-group-id';
  const mockListId = 'mock-list-id';
  it('should return a 200 status code on successful removal', async () => {
    groupService.removeListFromGroup.mockResolvedValue(successResult);

    const res = await request.put(`/groups/${mockGroupId}/remove/list`).send({ listId: mockListId });
    expect(res.status).toBe(200);
  });

  it('should return a 400 status code if missing list id', async () => {
    const res = await request.put(`/groups/${mockGroupId}/remove/list`);
    expect(res.status).toBe(400);
  });

  it('should return a 500 status code if something went wrong while adding list', async () => {
    groupService.removeListFromGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/groups/${mockGroupId}/remove/list`).send({ listId: mockListId });
    expect(res.status).toBe(500);
  });
});

describe('DELETE /:id delete group by id test', () => {
  const mockGroupId = 'mock-group-id';
  it('should delete group with valid id', async () => {
    groupService.deleteGroup.mockResolvedValue([{ message: 'mock deletion successful' }]);

    const res = await request.delete(`/groups/${mockGroupId}/delete`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong during deletion', async () => {
    groupService.deleteGroup.mockRejectedValue(new Error('something went wrong'));

    const res = await request.delete(`/groups/${mockGroupId}/delete`);
    expect(res.status).toBe(500);
  });
});
