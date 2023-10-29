const { PLACES_API_KEY } = require('../constants.js');

const MAX_RESULTS = 20;

const REQ_HEADERS = {
  'Content-Type': 'application/json',
  'X-Goog-Api-Key': PLACES_API_KEY,
  'X-Goog-FieldMask':
    'places.regularOpeningHours,places.displayName,places.name,places.location,places.shortFormattedAddress,places.rating,places.nationalPhoneNumber,places.websiteUri',
  'Accept-Language': 'en'
};

async function getPlacesNearby(latitude, longitude, category) {
  const includedTypes = category ? [category] : [];

  const reqBody = {
    includedTypes: includedTypes,
    maxResultCount: MAX_RESULTS,
    locationRestriction: {
      circle: {
        center: {
          latitude: latitude,
          longitude: longitude
        },
        radius: 5000.0
      }
    }
  };

  const requestOptions = {
    method: 'POST',
    headers: REQ_HEADERS,
    body: JSON.stringify(reqBody)
  };

  return await fetch('https://places.googleapis.com/v1/places:searchNearby', requestOptions);
}

async function getPlacesByText(textQuery, category) {
  improvedQuery = category ? `${category} near ${textQuery}` : `places near ${textQuery}`;

  const reqBody = {
    textQuery: `${improvedQuery}`,
    maxResultCount: MAX_RESULTS
  };

  const requestOptions = {
    method: 'POST',
    headers: REQ_HEADERS,
    body: JSON.stringify(reqBody)
  };

  return await fetch('https://places.googleapis.com/v1/places:searchText', requestOptions);
}

function processPhotos(googleResponse) {
  for (place of googleResponse.places) {
    if (place.photos) {
      for (photo of place.photos) {
        delete photo.authorAttributions;
      }
    }
  }
}

module.exports = { getPlacesNearby, getPlacesByText, processPhotos };
