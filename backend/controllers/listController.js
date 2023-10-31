// List:
// 1. POST /api/lists/create_list items: {list_name}
// 2. GET /api/lists/all_lists
// 3. PUT /api/lists/add_to_list items: {list_id, activity}
// 4. DELETE /api/lists/delete_from_list items: {list_id, activity_id}
// 5. DELETE /api/lists/delete_list items: {list_id}

const express = require('express');

const listService = require('../services/listService.js');
const middleware = require('../middleware/middleware.js');

const router = express.Router();

router.get('/:id', middleware.verifyToken, async (req, res) => {
  try {
    const listId = req.params.id;
    console.log(listId);
    const list = await listService.getListById(listId);
    if (list === null || list === undefined) {
      res.status(400).send({ errorMessage: `Failed to find list with ID: ${listId}` });
    } else {
      res.send({ list: list });
    }
  } catch (error) {
    res.status(500).send({
      errorMessage: `Something went wrong while finding list with ID.`
    });
  }
});

router.post('/create', middleware.verifyToken, async (req, res) => {
  let listName = req.body.listName;

  try {
    const listId = await listService.createList(listName);
    res.send({ listId: listId });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to create a list.' });
  }
});

router.put('/:id/add/place', middleware.verifyToken, async (req, res) => {
  let listId = req.params.id;
  let place = req.body.place;

  try {
    const placeResp = await listService.addPlaceToList(listId, place);
    console.log(placeResp);
    res.send({ message: 'Successfully added place to list' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to add a place to list ' });
  }
});

router.put('/:id/remove/place', middleware.verifyToken, async (req, res) => {
  const listId = req.params.id;
  const placeId = req.body.placeId;

  try {
    const resp = await listService.removePlaceFromList(listId, placeId);
    console.log(resp);
    res.send({ message: 'successfully removed place from list' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to remove place from list' });
  }
});

router.get('/:id/places', middleware.verifyToken, async (req, res) => {
  const listId = req.params.id;

  try {
    const places = await listService.getPlacesByListId(listId);
    console.log(places);
    res.send({ places: places.places });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to get places in the list' });
  }
});

router.put('/:id/add/schedule', middleware.verifyToken, async (req, res) => {
  const placeIds = req.body.placeIds;
  const listId = req.params.id;

  try {
    const resp = await listService.createScheduleForList(listId, placeIds);
    // console.log(resp);
    res.send({ schedule: resp });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to create schedule for given list and places' });
  }
});

router.delete('/:id/delete', middleware.verifyToken, async (req, res) => {
  const listId = req.params.id;

  try {
    await listService.deleteListById(listId);
    res.send({ message: 'List successfully deleted' });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to delete the list.' });
  }
});

module.exports = router;
