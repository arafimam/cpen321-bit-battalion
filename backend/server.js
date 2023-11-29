require('dotenv').config();
const https = require('https');
const fs = require('fs');
const admin = require('firebase-admin');

const { app } = require('./app.js');
const connectDB = require('./db.js');
const { PORT } = require('./constants.js');
const serviceAccount = require('./service-account.json');

admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

const httpsOptions = {
  key: fs.readFileSync(process.env.CERTS_KEY),
  cert: fs.readFileSync(process.env.CERTS_CERT)
};

async function startServer() {
  await connectDB();

  const server = https.createServer(httpsOptions, app);
  server.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
  });
}

startServer();
