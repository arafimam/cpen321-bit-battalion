const supertest = require('supertest');

const { app } = require('../../app.js');
const listService = require('../../services/listService.js');

const request = supertest(app);

jest.mock('../../middleware/middleware.js', () => ({
  verifyToken: (req, res, next) => next(),
  getUser: (req, res, next) => {
    res.locals.user = { _id: '1234' };
    next();
  }
}));

jest.mock('../../services/listService.js', () => ({
  getListById: jest.fn(),
  getListName: jest.fn(),
  createList: jest.fn(),
  addPlaceToList: jest.fn(),
  getPlacesByListId: jest.fn(),
  deleteListById: jest.fn(),
  removePlaceFromList: jest.fn(),
  createScheduleForList: jest.fn()
}));

// Interface GET /lists/:id
describe('GET /:id getListById test', () => {
  let listId = 'mock-list-id';

  // Input: valid listId
  // Expected Status Code: 200
  // Expected Behaviour: Returns the list with given id
  // Expected Output: The list object
  // ChatGPT usage: Yes
  it('should return a list', async () => {
    listService.getListById.mockResolvedValue({
      _id: listId,
      listName: 'testList',
      places: []
    });

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(200);
    expect(res.body.list).toStrictEqual({
      _id: listId,
      listName: 'testList',
      places: []
    });
  });

  // Input: invalid listId
  // Expected Status Code: 400
  // Expected Behaviour: Fails to get list
  // Expected Output: Invalid listId Error message
  // ChatGPT usage: Yes
  it('list id not found', async () => {
    listService.getListById.mockResolvedValue(null);

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe(`Failed to find list with ID: ${listId}`);
  });

  // Input: valid listId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: Yes
  it('list service throw error', async () => {
    listService.getListById.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(500);
  });
});

// Interface POST /lists/create
describe('POST /create create a new list', () => {
  const mockListName = 'mock-list-name';
  const mockListId = 'mock-list-id';

  // Input: valid listName
  // Expected Status Code: 200
  // Expected Behaviour: Returns the listId
  // Expected Output: The listId of new list
  // ChatGPT usage: No
  it('should return a 200 status code on successful creation of new list', async () => {
    listService.createList.mockResolvedValue(mockListId);

    const res = await request.post(`/lists/create`).send({ listName: mockListName });
    expect(res.status).toBe(200);
    expect(res.body.listId).toBe(mockListId);
  });

  // Input: invalid/missing listName
  // Expected Status Code: 400
  // Expected Behaviour: Fails to create list
  // Expected Output: Invalid listName error message and 400 status code
  // ChatGPT usage: No
  it('should return a 400 status code when given invalid/missing listName', async () => {
    const res = await request.post(`/lists/create`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Please provide a valid list name');
  });

  // Input: valid listName
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during creation', async () => {
    listService.createList.mockRejectedValue(new Error('mock error'));

    const res = await request.post(`/lists/create`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /lists/:id/add/place
describe('PUT /:id/add/place add place to a list', () => {
  const mockPlace = { displayName: 'mock-display-name' };
  const mockListId = 'mock-list-id';

  // Input: valid place
  // Expected Status Code: 200
  // Expected Behaviour: Adds the place to list with given lisId
  // Expected Output: Success Message
  // ChatGPT usage: No
  it('should return a 200 status code on successful addition of place', async () => {
    listService.addPlaceToList.mockResolvedValue({ placeAlreadyExistsInList: false });

    const res = await request.put(`/lists/${mockListId}/add/place`).send({ place: mockPlace });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('Successfully added place to list');
  });

  // Input: duplicate place (place already exists in list)
  // Expected Status Code: 400
  // Expected Behaviour: Fails to add the place in the list since it's a duplicate
  // Expected Output: Place already exists in the list error message and 400 status code
  // ChatGPT usage: No
  it('should return a 400 status code when given a duplicate place', async () => {
    listService.addPlaceToList.mockResolvedValue({ placeAlreadyExistsInList: true });

    const res = await request.put(`/lists/${mockListId}/add/place`).send({ place: mockPlace });
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Place already exists in list');
  });

  // Input: valid place
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during addition', async () => {
    listService.addPlaceToList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/add/place`).send({ place: mockPlace });
    expect(res.status).toBe(500);
  });
});

// Interface PUT /lists/:id/remove/place
describe('PUT /:id/remove/place remove place from a list', () => {
  const mockPlaceId = 'mock-place-id';
  const mockListId = 'mock-list-id';
  const mockSuccessResult = 'mock-success-result';

  // Input: valid placeId
  // Expected Status Code: 200
  // Expected Behaviour: Removes the place with given placeId from list with given lisId
  // Expected Output: Success Message
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal of place', async () => {
    listService.removePlaceFromList.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/lists/${mockListId}/remove/place`).send({ placeId: mockPlaceId });
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('successfully removed place from list');
  });

  // Input: missing placeId
  // Expected Status Code: 400
  // Expected Behaviour: Fails to remove a place from the list due to missing placeId
  // Expected Output: Missing placeId error message and 400 status code
  // ChatGPT usage: No
  it('should return a 400 status code when placeId input is missing', async () => {
    const res = await request.put(`/lists/${mockListId}/remove/place`);
    expect(res.status).toBe(400);
    expect(res.body.errorMessage).toBe('Please provide a placeId');
  });

  // Input: valid placeId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during removal', async () => {
    listService.removePlaceFromList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/remove/place`).send({ placeId: mockPlaceId });
    expect(res.status).toBe(500);
  });
});

// Interface GET /lists/:id/places
describe('GET /:id/places get all places for a list', () => {
  const mockPlaces = [{ displayName: 'mock-place-1' }, { displayName: 'mock-place-2' }];
  const mockListId = 'mock-list-id';

  // Input: valid listId
  // Expected Status Code: 200
  // Expected Behaviour: Returns the places from list with given lisId
  // Expected Output: places array
  // ChatGPT usage: No
  it('should return a 200 status code on successful removal of place', async () => {
    listService.getPlacesByListId.mockResolvedValue({ places: mockPlaces });

    const res = await request.get(`/lists/${mockListId}/places`);
    expect(res.status).toBe(200);
    expect(res.body.places).toStrictEqual(mockPlaces);
  });

  // Input: valid listId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during removal', async () => {
    listService.getPlacesByListId.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/lists/${mockListId}/places`);
    expect(res.status).toBe(500);
  });
});

// Interface PUT /lists/:id/add/schedule
describe('PUT /:id/add/schedule add schedule for a list', () => {
  const mockSchedule = [{ displayName: 'mock-place-1' }, { displayName: 'mock-place-2' }];
  const mockListId = 'mock-list-id';

  // Input: valid listId
  // Expected Status Code: 200
  // Expected Behaviour: Returns the schedule for list with given lisId
  // Expected Output: schedule array
  // ChatGPT usage: No
  it('should return a 200 status code on successful creation of schedule', async () => {
    listService.createScheduleForList.mockResolvedValue(mockSchedule);

    const res = await request.put(`/lists/${mockListId}/add/schedule`);
    expect(res.status).toBe(200);
    expect(res.body.schedule).toStrictEqual(mockSchedule);
  });

  // Input: valid listId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during schedule creation', async () => {
    listService.createScheduleForList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/add/schedule`);
    expect(res.status).toBe(500);
  });
});

// Interface DELETE /lists/:id/delete
describe('DELETE /:id/delete delete a list by id', () => {
  const mockListId = 'mock-list-id';
  const mockSuccessResult = 'mock-success-result';

  // Input: valid listId
  // Expected Status Code: 200
  // Expected Behaviour: Deletes the list with given lisId
  // Expected Output: Success Message
  // ChatGPT usage: No
  it('should return a 200 status code on successful deletion', async () => {
    listService.deleteListById.mockResolvedValue(mockSuccessResult);

    const res = await request.delete(`/lists/${mockListId}/delete`);
    expect(res.status).toBe(200);
    expect(res.body.message).toBe('List successfully deleted');
  });

  // Input: valid listId
  // Expected Status Code: 500
  // Expected Behaviour: Internal server error
  // Expected Output: Returns a 500 status
  // ChatGPT usage: No
  it('should return a 500 status code if something goes wrong during deletion', async () => {
    listService.deleteListById.mockRejectedValue(new Error('mock error'));

    const res = await request.delete(`/lists/${mockListId}/delete`);
    expect(res.status).toBe(500);
  });
});
