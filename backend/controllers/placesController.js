const express = require('express');

const placesService = require('../services/placesService.js');

const router = express.Router();

// Nearby Search
// {location: {latitude, longitude}, category}
router.get('/currLocation', async (req, res) => {
  const latitude = req.query.latitude;
  const longitude = req.query.longitude;
  const category = req.query.category;

  //categories: restaurant, tourist_attraction, museum, shopping_mall, night_club
  //UI labels: food, attractions, arts, shopping, nightlife

  const googleResponse = await placesService.getPlacesNearby(latitude, longitude, category);
  if (googleResponse.status === 200) {
    res.send(googleResponse.response);
  } else {
    res.status(googleResponse.status).send({ errorMessage: googleResponse.errorMessage });
  }
});

// Text Search
// {textQuery, category}
router.get('/destination', async (req, res) => {
  const textQuery = req.query.textQuery;
  const category = req.query.category;

  const googleResponse = await placesService.getPlacesByText(textQuery, category);
  if (googleResponse.status === 200) {
    res.send(googleResponse.response);
  } else {
    res.status(googleResponse.status).send({ errorMessage: googleResponse.errorMessage });
  }
});

module.exports = router;
