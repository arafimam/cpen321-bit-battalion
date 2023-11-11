const supertest = require('supertest');

const { app } = require('../../app.js');
const userService = require('../../services/userService.js');
const listNotification = require('../../notifications/listNotification.js');

const request = supertest(app);

jest.mock('../../middleware/middleware.js', () => ({
  verifyToken: (req, res, next) => next(),
  getUser: (req, res, next) => {
    res.locals.user = { _id: '1234' };
    next();
  }
}));

jest.mock('../../services/userService.js', () => ({
  verify: jest.fn(),
  createUser: jest.fn(),
  getUserById: jest.fn(),
  getUserByGoogleId: jest.fn(),
  updateDeviceRegistrationToken: jest.fn(),
  getListsforUser: jest.fn(),
  addListForUser: jest.fn(),
  removeListForUser: jest.fn()
}));

jest.mock('../../notifications/listNotification.js', () => ({
  createList: jest.fn()
}));

const mockSuccessResult = 'mock-success-result';

describe('POST /login login or create user if user does not exist', () => {
  const mockIdToken = 'mock-id-token';
  const mockGoogleId = 'mock-google-id';
  const mockDeviceRegistrationToken = 'mock-device-registration-token';
  const mockUserName = 'mock-username';

  it('should return a 200 status code on successful login', async () => {
    userService.verify.mockResolvedValue(mockGoogleId);
    userService.createUser.mockResolvedValue(mockUserName);

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if user could not be verified', async () => {
    userService.verify.mockRejectedValue(new Error('mock error'));

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });

  it('should return a 500 status code if something went wrong while creating new user', async () => {
    userService.verify.mockResolvedValue(mockGoogleId);
    userService.createUser.mockRejectedValue(new Error('mock error'));

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });
});

describe('PUT /device-registration-token/update update device registration token for existing users', () => {
  const mockDeviceRegistrationToken = 'mock-device-registration-token';

  it('should return a 200 status code on successful update', async () => {
    userService.updateDeviceRegistrationToken.mockResolvedValue(mockSuccessResult);

    const res = await request
      .put(`/users/device-registration-token/update`)
      .send({ deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong during update', async () => {
    userService.updateDeviceRegistrationToken.mockRejectedValue(new Error('mock error'));

    const res = await request
      .put(`/users/device-registration-token/update`)
      .send({ deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });
});

describe('GET /lists get all lists for a user', () => {
  const mockLists = [{ lisName: 'mock-list-1' }, { listName: 'mock-list-2' }];

  it('should return a 200 status code on successful get', async () => {
    userService.getListsforUser.mockResolvedValue(mockLists);

    const res = await request.get(`/users/lists`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something went wrong while getting lists', async () => {
    userService.getListsforUser.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/users/lists`);
    expect(res.status).toBe(500);
  });
});

describe('PUT add/list add list for a user', () => {
  const mockListName = 'mock-list-name';

  it('should return a 200 status code on successful creation', async () => {
    userService.addListForUser.mockResolvedValue(mockSuccessResult);
    listNotification.createList.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
  });

  it('should return a 200 status code despite notification failure', async () => {
    userService.addListForUser.mockResolvedValue(mockSuccessResult);
    listNotification.createList.mockRejectedValue(new Error('notification failed'));

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
  });

  it('should return a 400 status code if listName is missing', async () => {
    const res = await request.put(`/users/add/list`);
    expect(res.status).toBe(400);
  });

  it('should return a 500 status code if something goes wrong during addition', async () => {
    userService.addListForUser.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/remove/list remove list for a user', () => {
  const mockListId = 'mock-list-id';

  it('should return a 200 status code on successful removal', async () => {
    userService.removeListForUser.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/users/${mockListId}/remove/list`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during removal', async () => {
    userService.removeListForUser.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/users/${mockListId}/remove/list`);
    expect(res.status).toBe(500);
  });
});
