const mongoose = require('mongoose');

const flightSchema = new mongoose.Schema({
  userId: {
    type: String,
    required: true
  },
  flightNumber: {
    type: String,
    required: true
  },
  departureDate: {
    type: Date,
    required: true
  },
  destination: {
    type: String,
    required: true
  }
  // Add other flight attributes here
});

const Flight = mongoose.model('Flight', flightSchema);

module.exports = Flight;
