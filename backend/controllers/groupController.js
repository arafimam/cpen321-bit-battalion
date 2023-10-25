// Group:
// 1. POST /api/groups/create_group items: {group_name}
// 2. GET /api/groups/all_groups
// 3. POST /api/groups/add_to_group items: {user_id, group_code}
// 4. GET /api/groups/group?group_id={group_id}
// 5. DELETE /api/groups/delete_group items: {group_id}
// 6. DELETE /api/groups/leave_group items: {group_id, user_id}

const express = require("express")

const router = express.Router()

// Get all groups
router.get("/", (req, res) => {
	
})

// Get a group by Id
router.get("/:groupId", (req, res) => {

})

// Create a group
router.post("/create", (req, res) => {

})

// Add user to group
router.put("/:groupId/addUser", (req, res) => {
	// Group code as part of request body
})

// Delete group
router.delete("/:groupId/delete", (req, res) => {

})

// Remove user from group
router.delete("/:groupId/removeUser", (req, res) => {

})

export default router