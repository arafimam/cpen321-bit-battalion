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
        user_id: { type: String, required: true },
        username: { type: String, required: true }
      }
    ]
  },
  lists: {
    type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'List' }],
    default: []
  }

  // groupDestination: {
  //   location: {
  //     latitude: { type: Number, required: true },
  //     longitude: { type: Number, required: true }
  //   },
  //   locationName: { type: String, required: true }
  // }
});

const Group = mongoose.model('Group', groupSchema);

async function getAll() {
  // try {
  //   const groups = await Group.find({});
  //   return groups;
  // } catch (error) {
  //   throw new Error('Error in DB while getting all groups ' + error.message);
  // }
  return await Group.find({});
}

async function getById(groupId) {
  return await Group.findById(groupId);
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

async function addUser(member, groupCode) {
  const filter = { groupCode: groupCode };
  const update = { $push: { members: member } };

  try {
    return await Group.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while adding user to the group: ' + error.message);
  }
}

async function addList(groupId) {
  const filter = { _id: groupId };

  try {
    //create the list first
    const update = { $push: { lists: listId } };
    return await Group.findOneAndUpdate(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while adding list to the group: ' + error.message);
  }
}

async function deleteList(listId, groupId) {
  const filter = { _id: groupId };
  const update = { $pull: { lists: listId } };

  try {
    // delete list first
    return await Group.updateOne(filter, update, { new: true });
  } catch (error) {
    throw new Error('Error in DB while deleting list from the group: ' + error.message);
  }
}

module.exports = { Group, getAll, getById, generateUniqueGroupCode, addUser, addList, deleteList };
