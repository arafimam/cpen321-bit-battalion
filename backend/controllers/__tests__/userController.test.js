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

// Interface POST /users/login
describe('POST /login login or create user if user does not exist', () => {
  const mockIdToken = 'mock-id-token';
  const mockGoogleId = 'mock-google-id';
  const mockDeviceRegistrationToken = 'mock-device-registration-token';
  const mockUserName = 'mock-username';

  // Input: valid idToken, username, and deviceRegistrationToken
  // Expected Status Code: 200
  // Expected Behaviour: Successful google verification
  // Expected Output: Returns a 200 status
  // ChatGPT usage: Yes
  it('should return a 200 status code on successful login', async () => {
    userService.verify.mockResolvedValue(mockGoogleId);
    userService.createUser.mockResolvedValue(mockUserName);

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(200);
  });

  // Input: valid idToken, username, and deviceRegistrationToken
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during verification
  // Expected Output: Returns a 500 status
  // ChatGPT usage: Yes
  it('should return a 500 status code if user could not be verified', async () => {
    userService.verify.mockRejectedValue(new Error('mock error'));

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });

  // Input: valid idToken, username, and deviceRegistrationToken
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during new user creation
  // Expected Output: Returns a 500 status
  // ChatGPT usage: Yes
  it('should return a 500 status code if something went wrong while creating new user', async () => {
    userService.verify.mockResolvedValue(mockGoogleId);
    userService.createUser.mockRejectedValue(new Error('mock error'));

    const res = await request
      .post(`/users/login`)
      .send({ idToken: mockIdToken, username: mockUserName, deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /users/device-registration-token/update
describe('PUT /device-registration-token/update update device registration token for existing users', () => {
  const mockDeviceRegistrationToken = 'mock-device-registration-token';

  // Input: valid deviceRegistrationToken
  // Expected Status Code: 200
  // Expected Behaviour: Successful token update
  // Expected Output: Returns a 200 status
  // ChatGPT usage: Yes
  it('should return a 200 status code on successful update', async () => {
    userService.updateDeviceRegistrationToken.mockResolvedValue(mockSuccessResult);

    const res = await request
      .put(`/users/device-registration-token/update`)
      .send({ deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(200);
  });

  // Input: valid deviceRegistrationToken
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during token update
  // Expected Output: Returns a 500 status
  // ChatGPT usage: Yes
  it('should return a 500 status code if something went wrong during update', async () => {
    userService.updateDeviceRegistrationToken.mockRejectedValue(new Error('mock error'));

    const res = await request
      .put(`/users/device-registration-token/update`)
      .send({ deviceRegistrationToken: mockDeviceRegistrationToken });
    expect(res.status).toBe(500);
  });
});

// Interface GET /users/lists
describe('GET /lists get all lists for a user', () => {
  const mockLists = [{ lisName: 'mock-list-1' }, { listName: 'mock-list-2' }];

  // Input: None
  // Expected Status Code: 200
  // Expected Behaviour: Successful retrieval of lists for user
  // Expected Output: Returns the lists array for a user
  // ChatGPT usage: No
  it('should return a 200 status code on successful get', async () => {
    userService.getListsforUser.mockResolvedValue(mockLists);

    const res = await request.get(`/users/lists`);
    expect(res.status).toBe(200);
    expect(res.body.lists).toStrictEqual(mockLists);
  });

  // Input: None
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during list retrieval
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something went wrong while getting lists', async () => {
    userService.getListsforUser.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/users/lists`);
    expect(res.status).toBe(500);
  });
});

// Interface PUT /users/add/list
describe('PUT add/list add list for a user', () => {
  const mockListName = 'mock-list-name';

  // Input: valid listName
  // Expected Status Code: 200
  // Expected Behaviour: Successful addition of list for user
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should return a 200 status code on successful creation', async () => {
    userService.addListForUser.mockResolvedValue(mockSuccessResult);
    listNotification.createList.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('New list successfully added for user');
  });

  // Input: valid listName
  // Expected Status Code: 200
  // Expected Behaviour: Successful addition of list for user despite notification failure
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should return a 200 status code despite notification failure', async () => {
    userService.addListForUser.mockResolvedValue(mockSuccessResult);
    listNotification.createList.mockRejectedValue(new Error('notification failed'));

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(200);
  });

  // Input: missing listName
  // Expected Status Code: 400
  // Expected Behaviour: Failed addition of list for user
  // Expected Output: Missing listName error message
  // ChatGPT usage: No
  it('should return a 400 status code if listName is missing', async () => {
    const res = await request.put(`/users/add/list`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Please provide a list name');
  });

  // Input: valid listName
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during list addition
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during addition', async () => {
    userService.addListForUser.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/users/add/list`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /users/:id/remove/list
describe('PUT /:id/remove/list remove list for a user', () => {
  const mockListId = 'mock-list-id';

  // Input: valid listId
  // Expected Status Code: 200
  // Expected Behaviour: Successful removal of list for user
  // Expected Output: Success message
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal', async () => {
    userService.removeListForUser.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/users/${mockListId}/remove/list`);
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('List successfully removed for user');
  });

  // Input: valid listId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error during list removal
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during removal', async () => {
    userService.removeListForUser.mockRejectedValue(new Error('something went wrong'));

    const res = await request.put(`/users/${mockListId}/remove/list`);
    expect(res.status).toBe(500);
  });
});
