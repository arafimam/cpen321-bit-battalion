const express = require('express');
const https = require('https');
const fs = require('fs');

const connectDB = require('./db.js');
const { PORT } = require('./constants.js');
const userRouter = require('./controllers/userController.js');
const groupRouter = require('./controllers/groupController.js');
const placesRouter = require('./controllers/placesController.js');
const listRouter = require('./controllers/listController.js');

const app = express();

const httpsOptions = {
  key: fs.readFileSync('./certs/key.pem'),
  cert: fs.readFileSync('./certs/cert.pem')
};

// Middleware
app.use(express.json());
app.use('/users', userRouter);
app.use('/groups', groupRouter);
app.use('/places', placesRouter);
app.use('/lists', listRouter);

// Simple GET endpoint
app.get('/', (req, res) => {
  res.send('Hello, World!');
});

const startServer = async () => {
  await connectDB();

  const server = https.createServer(httpsOptions, app);
  server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
  });
};

startServer();
