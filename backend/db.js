const mongoose = require('mongoose');
const { MONGO_CONNECTION_STRING } = require('./constants');

const connectDB = async () => {
  await mongoose.connect(MONGO_CONNECTION_STRING, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  });
  console.log('MongoDB connected');
};

module.exports = connectDB;
