// const jest = require('jest');
const supertest = require('supertest');

const { app } = require('../../app.js');
const groupService = require('../../services/groupService.js');

const request = supertest(app);

jest.mock('../../middleware/middleware.js', () => ({
  verifyToken: (req, res, next) => next(),
  getUser: (req, res, next) => {
    res.locals.user = { _id: '1234' };
    next();
  }
}));

jest.mock('../../services/groupService.js', () => ({
  getAllGroups: jest.fn()
}));

describe('/all get all groups test', () => {
  let listId = 'mock-list-id';

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
