const express = require('express');
const https = require('https');
const fs = require('fs');
const admin = require('firebase-admin');

const connectDB = require('./db.js');
const { PORT } = require('./constants.js');
const userRouter = require('./controllers/userController.js');
const groupRouter = require('./controllers/groupController.js');
const placesRouter = require('./controllers/placesController.js');
const listRouter = require('./controllers/listController.js');

const serviceAccount = require('./service-account.json');

const app = express();
admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

const httpsOptions = {
  key: fs.readFileSync('./certs/key.pem'),
  cert: fs.readFileSync('./certs/cert.pem')
};

// Help from chatGPT for routing to different services
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

async function startServer() {
  await connectDB();

  const server = https.createServer(httpsOptions, app);
  server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
  });
}

startServer();
