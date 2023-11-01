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
const fbapp = admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

const httpsOptions = {
  key: fs.readFileSync('./certs/key.pem'),
  cert: fs.readFileSync('./certs/cert.pem')
};

// This registration token comes from the client FCM SDKs.
// const registrationToken =
//   'f5CsLJZKRyqPVciNlRLK:APA91bEK2Q1BkdCqtH6D0JNJsEDFCt_uyNJTe_Se6fp5kOndoQyU5YtSQYNp1eKFwj_pFgw5WHuBpPaltuNfTGThkIB1_VNnDomekPhFg99-3SQfkyFbYiFzBlG3e47s7djfdzc5wq-a';

// const registrationToken =
//   'fcva2yNuSgefy2CbECf3c7:APA91bHMbmfg7hLXSgk-2sLbflenfXklgMlSoT2D8SRJ9PxX56rVZo8rBPlSfVaINITbShOl1MDV4OrX7p3WV1Hetb8zgSw6gu57lEoUbgX5Qm75nLVe1ay7jhnv9ci000ekPYJbxXuC';

// const message = {
//   notification: {
//     title: 'title',
//     body: 'body text'
//   },
//   data: {
//     score: '850',
//     time: '2:45'
//   },
//   token: registrationToken
// };

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

  // Send a message to the device corresponding to the provided
  // registration token.
  // admin
  //   .messaging()
  //   .send(message)
  //   .then((response) => {
  //     // Response is a message ID string.
  //     console.log('Successfully sent message:', response);
  //   })
  //   .catch((error) => {
  //     console.log('Error sending message:', error);
  //   });
}

startServer();
