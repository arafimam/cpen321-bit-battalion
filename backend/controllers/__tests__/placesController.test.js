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

// Interface GET /currLocation
describe('GET /currLocation get places by current location test', () => {
  // Input: valid location (latidiude, longitude) and category
  // Expected Status Code: 200
  // Expected Behaviour: Returns places with valid location
  // Expected Output: Places with valid location
  // ChatGPT usage: No
  it('should return places with valid location', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 200, response: [{ name: 'testPlace1' }] });

    const res = await request.get(`/places/currLocation?latitude=1&longitude=1&category=restaurant`);
    expect(res.status).toBe(200);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(1, 1, 'restaurant');
  });

  // Input: valid location (latidiude, longitude) and category
  // Expected Status Code: 400
  // Expected Behaviour: Fails to get places due to failed API call (getPlacesNearby)
  // Expected Output: Returns a 400 status
  // ChatGPT usage: No
  it('should return error if getPlacesNearby fails', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/currLocation?latitude=1&longitude=1&category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(1, 1, 'restaurant');
  });

  // Input: invalid location (latidiude, longitude)
  // Expected Status Code: 400
  // Expected Behaviour: Fails to get places
  // Expected Output: Returns a 400 status
  // ChatGPT usage: No
  it('should return error if latitude and longitude are null or undefined fails', async () => {
    placesService.getPlacesNearby.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/currLocation?category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesNearby).toHaveBeenCalledWith(undefined, undefined, 'restaurant');
  });
});

// Interface GET /destination
describe('GET /destination get places by destination location', () => {
  // Input: valid location (textQuery) and category
  // Expected Status Code: 200
  // Expected Behaviour: Returns places with valid location
  // Expected Output: Places near the location (textQuery)
  // ChatGPT usage: Partial
  it('should return places with valid location', async () => {
    placesService.getPlacesByText.mockResolvedValue({ status: 200, response: [{ name: 'testPlace1' }] });

    const res = await request.get(`/places/destination?textQuery=vancouver&category=restaurant`);
    expect(res.status).toBe(200);
    expect(placesService.getPlacesByText).toHaveBeenCalledWith('vancouver', 'restaurant');
  });

  // Input: valid location (textQuery) and category
  // Expected Status Code: 400
  // Expected Behaviour: Fails to get places due to failed API call (getPlacesByText)
  // Expected Output: Returns a 400 status
  // ChatGPT usage: No
  it('should return error if getPlacesByText fails', async () => {
    placesService.getPlacesByText.mockResolvedValue({ status: 400 });

    const res = await request.get(`/places/destination?textQuery=vancouver&category=restaurant`);
    expect(res.status).toBe(400);
    expect(placesService.getPlacesByText).toHaveBeenCalledWith('vancouver', 'restaurant');
  });
});
