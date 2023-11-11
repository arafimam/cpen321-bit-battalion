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

describe('/:id getListById test', () => {
  let listId = 'mock-list-id';

  it('should return a list', async () => {
    listService.getListById.mockResolvedValue({
      _id: listId,
      listName: 'testList',
      places: []
    });

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(200);
  });

  it('list id not found', async () => {
    listService.getListById.mockResolvedValue(null);

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(400);
  });

  it('list service throw error', async () => {
    listService.getListById.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/lists/${listId}`);
    expect(res.status).toBe(500);
  });
});

describe('POST /create create a new list', () => {
  const mockListName = 'mock-list-name';
  const mockListId = 'mock-list-id';

  it('should return a 200 status code on successful creation of new list', async () => {
    listService.createList.mockResolvedValue(mockListId);

    const res = await request.post(`/lists/create`).send({ listName: mockListName });
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during creation', async () => {
    listService.createList.mockRejectedValue(new Error('mock error'));

    const res = await request.post(`/lists/create`).send({ listName: mockListName });
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/add/place add place to a list', () => {
  const mockPlace = { displayName: 'mock-display-name' };
  const mockListId = 'mock-list-id';
  const mockSuccessResult = 'mock-success-result';

  it('should return a 200 status code on successful addition of place', async () => {
    listService.addPlaceToList.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/lists/${mockListId}/add/place`).send({ place: mockPlace });
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during addition', async () => {
    listService.addPlaceToList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/add/place`).send({ place: mockPlace });
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/remove/place remove place from a list', () => {
  const mockPlaceId = 'mock-place-id';
  const mockListId = 'mock-list-id';
  const mockSuccessResult = 'mock-success-result';

  it('should return a 200 status code on successful removal of place', async () => {
    listService.removePlaceFromList.mockResolvedValue(mockSuccessResult);

    const res = await request.put(`/lists/${mockListId}/remove/place`).send({ placeId: mockPlaceId });
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during removal', async () => {
    listService.removePlaceFromList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/remove/place`).send({ placeId: mockPlaceId });
    expect(res.status).toBe(500);
  });
});

describe('GET /:id/places get all places for a list', () => {
  const mockPlaces = [{ displayName: 'mock-place-1' }, { displayName: 'mock-place-2' }];
  const mockListId = 'mock-list-id';

  it('should return a 200 status code on successful removal of place', async () => {
    listService.getPlacesByListId.mockResolvedValue(mockPlaces);

    const res = await request.get(`/lists/${mockListId}/places`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during removal', async () => {
    listService.getPlacesByListId.mockRejectedValue(new Error('mock error'));

    const res = await request.get(`/lists/${mockListId}/places`);
    expect(res.status).toBe(500);
  });
});

describe('PUT /:id/add/schedule add schedule for a list', () => {
  const mockSchedule = [{ displayName: 'mock-place-1' }, { displayName: 'mock-place-2' }];
  const mockListId = 'mock-list-id';

  it('should return a 200 status code on successful creation of schedule', async () => {
    listService.createScheduleForList.mockResolvedValue(mockSchedule);

    const res = await request.put(`/lists/${mockListId}/add/schedule`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during schedule creation', async () => {
    listService.createScheduleForList.mockRejectedValue(new Error('mock error'));

    const res = await request.put(`/lists/${mockListId}/add/schedule`);
    expect(res.status).toBe(500);
  });
});

describe('DELETE /:id/delete delete a list by id', () => {
  const mockListId = 'mock-list-id';
  const mockSuccessResult = 'mock-success-result';

  it('should return a 200 status code on successful deletion', async () => {
    listService.deleteListById.mockResolvedValue(mockSuccessResult);

    const res = await request.delete(`/lists/${mockListId}/delete`);
    expect(res.status).toBe(200);
  });

  it('should return a 500 status code if something goes wrong during deletion', async () => {
    listService.deleteListById.mockRejectedValue(new Error('mock error'));

    const res = await request.delete(`/lists/${mockListId}/delete`);
    expect(res.status).toBe(500);
  });
});
