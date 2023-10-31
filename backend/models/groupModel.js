const mongoose = require('mongoose');
const randomstring = require('randomstring');
const { List } = require('./listModel');

const groupSchema = new mongoose.Schema({
  groupName: {
    type: String,
    required: true
  },
  groupCode: {
    type: String,
    required: true,
    unique: true
  },
  ownerId: {
    type: String,
    required: true
  },
  members: {
    type: [
      {
        userId: { type: String, required: true },
        username: { type: String, required: true }
      }
    ]
  },
  lists: {
    type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'List' }],
    default: []
  }
});

const Group = mongoose.model('Group', groupSchema);

async function createGroup(group) {
  try {
    return await Group.create(group);
  } catch (error) {
    throw new Error('Error while creating group: ' + error.message);
  }
}

async function deleteGroup(groupId) {
  try {
    return await Group.findByIdAndDelete(groupId);
  } catch (error) {
    throw new Error('Error in DB while deleting groups: ' + error.message);
  }
}

async function getAllGroups(userId) {
  try {
    const groups = await Group.find({ 'members.userId': userId }).select('groupName');
    return groups;
  } catch (error) {
    throw new Error('Error in DB while getting all groups for a user' + error.message);
  }
}

async function getGroup(groupId) {
  try {
    return Group.findById(groupId);
  } catch (error) {
    throw new Error('Error in DB while getting group by id' + error.message);
  }
}

async function getGroupLists(groupId) {
  try {
    return await Group.findById(groupId).select({ _id: 1, lists: 1 });
  } catch (error) {
    throw new Error('Error in DB while getting lists for a group' + error.message);
  }
}

// Function to generate a unique group code
async function generateUniqueGroupCode() {
  let isNotUnique = true;
  while (true) {
    const groupCode = randomstring.generate(6).toUpperCase(); // Generate a 6-character alphanumeric code
    const existingGroup = await Group.findOne({ groupCode: groupCode });
    isNotUnique = !!existingGroup; // Check if the code already exists

    if (!isNotUnique) return groupCode;
  }
}

async function addUserToGroup(groupCode, member) {
  //TODO: check if user already in group
  const filter = { groupCode: groupCode };
  const update = { $push: { members: member } };

  try {
    return await Group.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while adding user to the group: ' + error.message);
  }
}

async function removeUserFromGroup(groupId, userId) {
  //TODO: check if user already in group
  //TODO: if group members becomes empty after removing user, then delete group
  const filter = { _id: groupId };
  const update = { $pull: { members: { userId: userId } } };

  try {
    return await Group.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while removing user from the group: ' + error.message);
  }
}

async function addListToGroup(groupId, listId) {
  const filter = { _id: groupId };

  try {
    const update = { $push: { lists: listId } };
    return await Group.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error occured while adding list to the group: ' + error.message);
  }
}

async function removeListFromGroup(groupId, listId) {
  const filter = { _id: groupId };
  const update = { $pull: { lists: listId } };

  try {
    return await Group.updateOne(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while deleting list from the group: ' + error.message);
  }
}

module.exports = {
  Group,
  createGroup,
  generateUniqueGroupCode,
  getAllGroups,
  getGroup,
  getGroupLists,
  addUserToGroup,
  removeUserFromGroup,
  addListToGroup,
  removeListFromGroup,
  deleteGroup
};
