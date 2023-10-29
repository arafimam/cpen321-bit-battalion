const express = require('express');

const placesService = require('../services/placesService.js');
const { PLACES_API_KEY } = require('../constants.js');

const router = express.Router();

// Nearby Search
// {location: {latitude, longitude}, category?}
router.get('/nearbyPlaces', async (req, res) => {
  const latitude = req.query.latitude;
  const longitude = req.query.longitude;
  const category = req.query.category;

  //categories: restaurant, tourist_attraction, museums, shopping_mall, night_club
  //UI labels: food, attractions, arts, shopping, nightlife

  const reqHeaders = {
    'Content-Type': 'application/json',
    'X-Goog-Api-Key': PLACES_API_KEY,
    'X-Goog-FieldMask': 'places.displayName,places.name,places.location',
    'Accept-Language': 'en'
  };

  const reqBody = {
    includedTypes: [category],
    maxResultCount: 10,
    locationRestriction: {
      circle: {
        center: {
          latitude: latitude,
          longitude: longitude
        },
        radius: 1000.0
      }
    }
  };

  const requestOptions = {
    method: 'POST',
    headers: reqHeaders,
    body: JSON.stringify(reqBody)
  };

  try {
    const googleResponse = await fetch('https://places.googleapis.com/v1/places:searchNearby', requestOptions);

    if (googleResponse.ok) {
      jsonResp = await googleResponse.json();
      res.send(jsonResp);
    } else {
      res.status(400).send({ errorMessage: `Failed to find places near location.` });
    }
  } catch (error) {
    console.log(error);
    res.status(500).send({ errorMessage: `Something went wrong while finding places near location.` });
  }
});

// Text Search
// {searchText}
// router.get(async (req, res) => {});

module.exports = router;
