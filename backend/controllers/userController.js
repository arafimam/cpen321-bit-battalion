// User:
// 1. GET /api/users/username
const express = require('express');
const { OAuth2Client } = require('google-auth-library');

const router = express.Router();
const client = new OAuth2Client();

router.get('/username', (req, res) => {
  // get username from database or Google Auth
  res.send('Username');
});

router.post('/login', async (req, res) => {
  const idToken = req.body.idToken;
  console.log(idToken);

  async function verify() {
    const ticket = await client.verifyIdToken({
      idToken: idToken,
      audience: CLIENT_ID // Specify the CLIENT_ID of the app that accesses the backend
      // Or, if multiple clients access the backend:
      //[CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3]
    });
    const payload = ticket.getPayload();
    const userid = payload['sub'];
    // If request specified a G Suite domain:
    // const domain = payload['hd'];
  }
  verify()
    .then(() => {
      console.log('Sucess');
      res.status(200).send('success');
    })
    .catch(console.error);
});

module.exports = router;
