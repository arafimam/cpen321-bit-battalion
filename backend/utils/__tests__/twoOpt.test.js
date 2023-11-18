process.env['NODE_ENV'] = 'TEST';
const twoOpt = require('../twoOpt.js');

describe('degToRad', () => {
  // ChatGPT usage: No
  it.each([
    [180, Math.PI],
    [90, Math.PI / 2],
    [270, (3 * Math.PI) / 2]
  ])('converts degrees to radians', (degs, rads) => {
    expect(twoOpt.degToRad(degs)).toBe(rads);
    expect(twoOpt.degToRad(degs)).toBe(rads);
    expect(twoOpt.degToRad(degs)).toBe(rads);
  });
});

describe('haversineDistance', () => {
  // ChatGPT usage: No
  it.each([
    [{ latitude: 0, longitude: 0 }, { latitude: 0, longitude: 0 }, 0],
    [{ latitude: 49.2827, longitude: -123.1207 }, { latitude: 34.0549, longitude: -118.2426 }, 1740.1]
  ])(`returns the haversince distance between 2 points`, (point1, point2, distance) => {
    expect(twoOpt.haversineDistance(point1, point2)).toBeCloseTo(distance, 1);
  });
});

describe('calculateTotalDistance', () => {
  // ChatGPT usage: No
  it('returns the total distance of a path', () => {
    const mockPlaces = [
      { location: { latitude: 0, longitude: 0 } },
      { location: { latitude: 1, longitude: 1 } },
      { location: { latitude: 2, longitude: 2 } }
    ];
    const mockPath = [0, 1, 2];

    const result = twoOpt.calculateTotalDistance(mockPath, mockPlaces);
    expect(result).toBeCloseTo(314.4, 0);
  });
});

describe('twoOpt', () => {
  // ChatGPT usage: No
  it('returns the best path', () => {
    const mockPlaces = [
      { location: { latitude: 40.7128, longitude: -74.006 } }, // New York
      { location: { latitude: 29.7604, longitude: -95.3698 } }, // Houston
      { location: { latitude: 41.8781, longitude: -87.6298 } }, // Chicago
      { location: { latitude: 34.0522, longitude: -118.2437 } } // Los Angeles
    ];

    const expectedResult = [
      { location: { latitude: 40.7128, longitude: -74.006 } }, // New York
      { location: { latitude: 41.8781, longitude: -87.6298 } }, // Chicago
      { location: { latitude: 29.7604, longitude: -95.3698 } }, // Houston
      { location: { latitude: 34.0522, longitude: -118.2437 } } // Los Angeles
    ];

    const result = twoOpt.twoOpt(mockPlaces);
    expect(result).toEqual(expectedResult);
  });
});
