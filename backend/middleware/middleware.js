const { OAuth2Client } = require('google-auth-library');

const userService = require('../services/userService.js');

const client = new OAuth2Client();

async function verifyToken(req, res, next) {
  const idToken = req.headers.authorization;

  try {
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.WEB_CLIENT_ID_RITAM
    });
    const payload = ticket.getPayload();
    res.locals.googleId = payload['sub'];
    next();
  } catch (error) {
    return res.status(401).json({ message: 'Invalid Google ID token.' });
  }
}

async function getUser(req, res, next) {
  const googleId = res.locals.googleId;
  console.log(googleId);
  try {
    const user = await userService.getUserByGoogleId(googleId);
    res.locals.user = user;
    console.log(user);
    next();
  } catch (error) {
    res.status(400).send({ errorMessage: 'Failed to get user by google id' });
  }
}

module.exports = { verifyToken, getUser };
