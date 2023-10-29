const mongoose = require('mongoose');
const { MONGO_CONNECTION_STRING } = require('./constants');

const connectDB = async () => {
  try {
    await mongoose.connect(MONGO_CONNECTION_STRING, {
      useNewUrlParser: true,
      useUnifiedTopology: true
    });
    console.log('MongoDB connected');
  } catch (error) {
    console.error('Error connecting to database: ', error.message);
    process.exit(1); // Exit process with failure
  }
};

module.exports = connectDB;
