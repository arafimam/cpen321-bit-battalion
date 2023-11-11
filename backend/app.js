require('dotenv').config();
const express = require('express');

const userRouter = require('./controllers/userController.js');
const groupRouter = require('./controllers/groupController.js');
const placesRouter = require('./controllers/placesController.js');
const listRouter = require('./controllers/listController.js');

const app = express();

// Help from chatGPT for routing to different services
// Middleware
app.use(express.json());
app.use('/users', userRouter);
app.use('/groups', groupRouter);
app.use('/places', placesRouter);
app.use('/lists', listRouter);

module.exports = { app };
