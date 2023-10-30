// Acitivity:
// 1. GET /api/activities?latitude={latitude}&longitude={longitude}?filters={filters}

const express = require('express');

const router = express.Router();

router.get('/', (req, res) => {
  let latitude = req.query.latitude;
  let longitude = req.query.longitude;
  let filters = req.query.filters; //filters might actually be more query parameters
});

export default router;
