// Flight:
// 1. POST /api/flights/flight items {flight_number, flight_departure_date}
// 2. GET /api/flights/flight?flight_id={flight_id}
// 3. GET /api/flights/all_flights

const express = require('express');

const router = express.Router();

const { AVIATION_STACK_API_KEY } = require('../constants');

// Get all the flights that the user has (your trips page)
router.get('/', (req, res) => {
    console.log('hello');
    res.send('hello');
});

// Get a specific flight that the user has
router.get('/:flightId', (req, res) => {});

router.post('/create', async (req, res) => {
    let flight_iata = req.body.flight_iata;
    let flight_departure_date = req.body.flight_departure_date;

    // let endpoint = `http://api.aviationstack.com/v1/flights?access_key=${AVIATION_STACK_API_KEY}&flight_iata=${flight_iata}&flight_date=${flight_departure_date}`;
    let endpoint = `http://api.aviationstack.com/v1/flights?access_key=${AVIATION_STACK_API_KEY}&arr_scheduled_time_dep=${flight_departure_date}`;

    let flightResp = await fetch(endpoint);
    // console.log(endpoint);
    console.log(flightResp);
    res.send(flightResp);
    // res.send('hello');
});

router.delete('/:flightId/delete', (req, res) => {});

module.exports = router;
