const express = require('express');

const placesService = require('../services/placesService.js');
const { PLACES_API_KEY } = require('../constants.js');

const router = express.Router();

// Nearby Search
// {location: {latitude, longitude}, category?}
router.get('/currLocation', async (req, res) => {
  const latitude = req.query.latitude;
  const longitude = req.query.longitude;
  const category = req.query.category;

  //categories: restaurant, tourist_attraction, museum, shopping_mall, night_club
  //UI labels: food, attractions, arts, shopping, nightlife

  try {
    const googleResponse = await placesService.getPlacesNearby(latitude, longitude, category);
    jsonResp = await googleResponse.json();
    console.log(jsonResp);

    if (googleResponse.ok) {
      res.send(jsonResp);
    } else {
      res.status(400).send({ errorMessage: `Failed to find places near location.` });
    }
  } catch (error) {
    res.status(500).send({ errorMessage: `Something went wrong while finding places near location.` });
  }
});

// Text Search
// {searchText}
router.get('/destination', async (req, res) => {
  const textQuery = req.query.textQuery;
  const category = req.query.category;

  try {
    const googleResponse = await placesService.getPlacesByText(textQuery, category);
    jsonResp = await googleResponse.json();
    console.log(jsonResp);

    if (googleResponse.ok) {
      res.send(jsonResp);
    } else {
      res.status(400).send({ errorMessage: `Failed to find places for the given search text.` });
    }
  } catch (error) {
    res.status(500).send({ errorMessage: `Something went wrong while finding places by destination.` });
  }
});

module.exports = router;
