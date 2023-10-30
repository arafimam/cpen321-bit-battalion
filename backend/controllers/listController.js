// List:
// 1. POST /api/lists/create_list items: {list_name}
// 2. GET /api/lists/all_lists
// 3. PUT /api/lists/add_to_list items: {list_id, activity}
// 4. DELETE /api/lists/delete_from_list items: {list_id, activity_id}
// 5. DELETE /api/lists/delete_list items: {list_id}

const express = require('express');

const listService = require('../services/listService.js');

const router = express.Router();

// router.get("/", (req, res) => {}); // Implement after user module is ready

router.get('/:id', async (req, res) => {
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

router.post('/create', async (req, res) => {
  let listName = req.body.listName;

  try {
    const listId = await listService.createList(listName);
    res.send({ listId: listId });
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to create a list.' });
  }
});

router.put('/add/place', async (req, res) => {
  let listId = req.body.listId;
  let place = req.body.place;

  try {
    await listService.addPlaceToList(listId, place);
    res.send('success');
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to add a place to list ' });
  }
});

router.put('/remove/place', async (req, res) => {
  const listId = req.body.listId;
  const placeId = req.body.placeId;

  try {
    const resp = await listService.removePlaceFromList(listId, placeId);
    console.log(resp);
    res.send('successfully removed place from list');
  } catch (error) {}
});

// router.delete('/:listId/deleteFromList/:activityId', (req, res) => {});
router.delete('/:id/delete', async (req, res) => {
  const listId = req.params.id;

  try {
    await listService.deleteListById(listId);
    res.send('list successfully deleted');
  } catch (error) {
    res.status(500).send({ errorMessage: 'Failed to delete the list.' });
  }
});

module.exports = router;
