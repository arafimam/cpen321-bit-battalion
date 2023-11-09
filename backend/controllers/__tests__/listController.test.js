// const jest = require('jest');
const supertest = require('supertest');

const { app } = require('../../app.js');
const { verifyToken } = require('../../middleware/middleware.js');
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
  getListById: jest.fn()
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
