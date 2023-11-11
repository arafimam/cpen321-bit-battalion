const supertest = require('supertest');

const { app } = require('../../app.js');
const placesService = require('../../services/placesService.js');

const request = supertest(app);

jest.mock('../../middleware/middleware.js', () => ({
  verifyToken: (req, res, next) => next(),
  getUser: (req, res, next) => {
    next();
  }
}));

jest.mock('../../services/placesService.js', () => ({
  getPlacesNearby: jest.fn(),
  getPlacesByText: jest.fn()
}));

describe('GET /currLocation get places by current location test', () => {
  it('should return places with valid location', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 200, response: [{ name: 'testPlace1' }] });

    const res = await request.get(`/places/currLocation?latitude=1&longitude=1&category=restaurant`);
    expect(res.status).toBe(200);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(1, 1, 'restaurant');
  });

  it('should return error if getPlacesNearby fails', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/currLocation?latitude=1&longitude=1&category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(1, 1, 'restaurant');
  });

  it('should return error if latitude and longitude are not null or undefined fails', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/currLocation?category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(undefined, undefined, 'restaurant');
  });
});

describe('GET /destination get places by destination location', () => {
  it('should return places with valid location', async () => {
    placesService.getPlacesByText.mockResolvedValue({ status: 200, response: [{ name: 'testPlace1' }] });

    const res = await request.get(`/places/destination?textQuery=vancouver&category=restaurant`);
    expect(res.status).toBe(200);
    expect(placesService.getPlacesByText).toHaveBeenCalledWith('vancouver', 'restaurant');
  });

  it('should return error if getPlacesByText fails', async () => {
    placesService.getPlacesByText.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/destination?textQuery=vancouver&category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesByText).toHaveBeenCalledWith('vancouver', 'restaurant');
  });
});
