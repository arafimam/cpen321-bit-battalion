// Endpoints
// User:
// 1. GET /api/users/username

// Flight:
// 1. POST /api/flights/flight items {flight_number, flight_departure_date}
// 2. GET /api/flights/flight?flight_id={flight_id}
// 3. GET /api/flights/all_flights

// Acitivity:
// 1. GET /api/activities?latitude={latitude}&longitude={longitude}?filters={filters}

// List:
// 1. POST /api/lists/create_list items: {list_name}
// 2. GET /api/lists/all_lists
// 3. POST /api/lists/add_to_list items: {list_id, activity}
// 4. DELETE /api/lists/delete_from_list items: {list_id, activity_id}
// 5. DELETE /api/lists/delete_list items: {list_id}

// Group:
// 1. POST /api/groups/create_group items: {group_name}
// 2. GET /api/groups/all_groups
// 3. POST /api/groups/add_to_group items: {user_id, group_code}
// 4. GET /api/groups/group?group_id={group_id}
// 5. DELETE /api/groups/delete_group items: {group_id}
// 6. DELETE /api/groups/leave_group items: {group_id, user_id}