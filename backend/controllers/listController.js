// List:
// 1. POST /api/lists/create_list items: {list_name}
// 2. GET /api/lists/all_lists
// 3. PUT /api/lists/add_to_list items: {list_id, activity}
// 4. DELETE /api/lists/delete_from_list items: {list_id, activity_id}
// 5. DELETE /api/lists/delete_list items: {list_id}

const express = require('express');

const router = express.Router();

router.get('/', (req, res) => {});
router.post('/create', (req, res) => {});
router.put('/:listId/addToList', (req, res) => {});
router.delete('/:listId/deleteFromList/:activityId', (req, res) => {});
router.delete('/:listId/delete', (req, res) => {});

export default router;
