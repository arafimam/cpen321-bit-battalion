const { EARTH_RADIUS } = require('../constants.js');

// Helper function that uses the Haversine law to calculate the distance between 2 points on Earth
function haversineDistance(point1, point2) {
  const dLat = degToRad(point2.latitude - point1.latitude);
  const dLon = degToRad(point2.longitude - point1.longitude);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(degToRad(point1.latitude)) * Math.cos(degToRad(point2.latitude)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distance = EARTH_RADIUS * c;
  return distance;
}

function degToRad(degrees) {
  return degrees * (Math.PI / 180);
}

function calculateTotalDistance(path, places) {
  let totalDistance = 0;
  for (let i = 0; i < path.length - 1; i++) {
    totalDistance += haversineDistance(places[path[i]].location, places[path[i + 1]].location);
  }
  return totalDistance;
}

// 2-Opt Algorithm using Haversine distance
function twoOpt(places) {
  let bestPath = [...places.keys()];

  for (let i = 1; i < places.length - 1; i++) {
    for (let j = i + 1; j < places.length; j++) {
      const newPath = bestPath.slice();
      newPath[i] = bestPath[j];
      newPath[j] = bestPath[i];
      if (calculateTotalDistance(newPath, places) < calculateTotalDistance(bestPath, places)) {
        bestPath = newPath;
      }
    }
  }

  return bestPath.map((index) => places[index]);
}

module.exports = { twoOpt };

if (process.env['NODE_ENV'] == 'TEST') {
  module.exports.haversineDistance = haversineDistance;
  module.exports.degToRad = degToRad;
  module.exports.calculateTotalDistance = calculateTotalDistance;
}
