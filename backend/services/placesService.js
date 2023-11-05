require('dotenv').config();

const MAX_RESULTS = 20;

const REQ_HEADERS = {
  'Content-Type': 'application/json',
  'X-Goog-Api-Key': process.env.PLACES_API_KEY,
  'X-Goog-FieldMask':
    'places.regularOpeningHours,places.displayName,places.name,places.location,places.shortFormattedAddress,places.rating,places.nationalPhoneNumber,places.websiteUri',
  'Accept-Language': 'en'
};

async function getPlacesNearby(latitude, longitude, category) {
  const includedTypes = category ? [category] : [];

  const reqBody = {
    includedTypes,
    maxResultCount: MAX_RESULTS,
    locationRestriction: {
      circle: {
        center: {
          latitude,
          longitude
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

  try {
    const googleResponse = await fetch('https://places.googleapis.com/v1/places:searchNearby', requestOptions);

    if (googleResponse.ok) {
      let jsonResp = await googleResponse.json();
      jsonResp = processPlacesResponse(jsonResp);
      return { status: 200, response: jsonResp };
    } else {
      return {
        status: 400,
        errorMessage: `Failed to find places for the given search text.`
      };
    }
  } catch (error) {
    return {
      status: 500,
      errorMessage: error.message
    };
  }
}

async function getPlacesByText(textQuery, category) {
  const improvedQuery = category ? `${category} near ${textQuery}` : `places near ${textQuery}`;

  const reqBody = {
    textQuery: `${improvedQuery}`,
    maxResultCount: MAX_RESULTS
  };

  const requestOptions = {
    method: 'POST',
    headers: REQ_HEADERS,
    body: JSON.stringify(reqBody)
  };

  try {
    const googleResponse = await fetch('https://places.googleapis.com/v1/places:searchText', requestOptions);

    if (googleResponse.ok) {
      let jsonResp = await googleResponse.json();
      let processedJsonResp = processPlacesResponse(jsonResp);
      return { status: 200, response: processedJsonResp };
    } else {
      return {
        status: 400,
        errorMessage: `Failed to find places for the given search text.`
      };
    }
  } catch (error) {
    return {
      status: 500,
      errorMessage: error.message
    };
  }
}

function processPlacesResponse(googleResponse) {
  for (let place of googleResponse.places) {
    place['placeId'] = place.name;
    delete place.name;
    place['displayName'] = place.displayName?.text;
  }

  return googleResponse;
}

module.exports = { getPlacesNearby, getPlacesByText };
