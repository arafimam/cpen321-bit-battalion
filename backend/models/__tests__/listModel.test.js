const listModel = require('../listModel.js');

const mongoose = jest.createMockFromModule('mongoose');
mongoose.model = jest.fn((name, schema) => {
  return {
    create: jest.fn(),
    findById: jest.fn(),
    findByIdAndDelete: jest.fn(),
    updateOne: jest.fn(),
    findOneAndUpdate: jest.fn(),
    findOne: jest.fn()
  };
});

// const mockListSchema = {

// };

// const mockCreate = jest.spyOn(listModel.List, 'create');
// const mockFindById = jest.spyOn(listModel.List, 'findById');
// const mockFindByIdAndDelete = jest.spyOn(listModel.List, 'findByIdAndDelete');
// const mockUpdateOne = jest.spyOn(listModel.List, 'updateOne');
// const mockFindOneAndUpdate = jest.spyOn(listModel.List, 'findOneAndUpdate');
// const mockFindOne = jest.spyOn(listModel.List, 'findOne');

// let mockCreate;
// let mockFindById;
// let mockFindOne;
// let mockFindByIdAndDelete;
// let mockUpdateOne;
// let mockFindOneAndUpdate;

beforeEach(() => {
  jest.clearAllMocks();
});

describe('createList', () => {
  // ChatGPT usage: No
  it('returns list object when createList is successful', async () => {
    const mockListName = 'mock-list-name';
    const mockListData = {
      listName: mockListName,
      places: []
    };

    // mockCreate.mockResolvedValue(mockListData);
    listModel.List.create = jest.fn().mockResolvedValue(mockListData);
    const result = await listModel.createList(mockListName);

    expect(result).toBe(mockListData);
    expect(listModel.List.create).toHaveBeenCalledWith({ listName: mockListName });
  });

  // ChatGPT usage: No
  it('throws error when createList is unsuccessful', async () => {
    const mockListName = 'mock-list-name';

    // mockCreate.mockRejectedValue(new Error('List creation failed'));
    listModel.List.create = jest.fn().mockRejectedValue(new Error('List creation failed'));
    await expect(listModel.createList(mockListName)).rejects.toThrowError();
    expect(listModel.List.create).toHaveBeenCalledWith({ listName: mockListName });
  });
});

describe('deleteList', () => {
  // ChatGPT usage: No
  it('returns list object when deleteList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    listModel.List.findByIdAndDelete = jest.fn().mockResolvedValue(mockListData);
    // mockFindByIdAndDelete.mockResolvedValue(mockListData);
    const result = await listModel.deleteList(mockListId);

    expect(result).toBe(mockListData);
    expect(listModel.List.findByIdAndDelete).toHaveBeenCalledWith(mockListId);
  });

  // ChatGPT usage: No
  it('throws error when deleteList is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    // mockFindByIdAndDelete.mockRejectedValue(new Error('List deletion failed'));
    listModel.List.findByIdAndDelete = jest.fn().mockRejectedValue(new Error('List deletion failed'));

    await expect(listModel.deleteList(mockListId)).rejects.toThrowError();
    expect(listModel.List.findByIdAndDelete).toHaveBeenCalledWith(mockListId);
  });
});

describe('getListById', () => {
  // ChatGPT usage: No
  it('returns a list by Id when getListById is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    // mockFindById.mockResolvedValue(mockListData);
    listModel.List.findById = jest.fn().mockResolvedValue(mockListData);
    const result = await listModel.getListById(mockListId);

    expect(result).toBe(mockListData);
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });

  // ChatGPT usage: No
  it('throws error when getListById is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    // mockFindById.mockRejectedValue(new Error('Getting list by id failed'));
    listModel.List.findById = jest.fn().mockRejectedValue(new Error('Getting list by id failed'));

    await expect(listModel.getListById(mockListId)).rejects.toThrowError();
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getListName', () => {
  // ChatGPT usage: No
  it('returns a listName by Id when getListName is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    // mockFindById.mockReturnValue({
    //   select: jest.fn().mockResolvedValue(mockListData)
    // });
    listModel.List.findById = jest.fn().mockReturnValue({
      select: jest.fn().mockResolvedValue(mockListData)
    });
    const result = await listModel.getListName(mockListId);

    expect(result).toBe(mockListData);
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });

  // ChatGPT usage: No
  it('throws error when getListName is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    // mockFindById.mockReturnValue(new Error('Getting listName failed'));
    listModel.List.findById = jest.fn().mockReturnValue(new Error('Getting listName failed'));
    // listModel.List.findById = jest.fn().mockRejectedValue(new Error('Getting listName failed'));

    await expect(listModel.getListName(mockListId)).rejects.toThrowError();
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getPlaces', () => {
  // ChatGPT usage: No
  it('returns a list of places by listId when getPlaces is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlacesData = [{ placeId: 'mock-place-id' }, { placeId: 'mock-place-id-2' }];

    listModel.List.findById = jest.fn().mockReturnValue({
      select: jest.fn().mockResolvedValue(mockPlacesData)
    });
    const result = await listModel.getPlaces(mockListId);

    expect(result).toBe(mockPlacesData);
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });

  // ChatGPT usage: No
  it('throws error when getPlaces is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    listModel.List.findById = jest.fn().mockReturnValue(new Error('Getting places failed'));

    await expect(listModel.getPlaces(mockListId)).rejects.toThrowError();
    expect(listModel.List.findById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getPlace', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  // ChatGPT usage: No
  it('returns a place by placeId when getPlace is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';
    const mockPlaceData = { placeId: 'mock-place-id' };

    listModel.List.findOne = jest.fn().mockResolvedValue(mockPlaceData);
    const result = await listModel.getPlace(mockListId, mockPlaceId);

    expect(result).toBe(mockPlaceData);
    expect(listModel.List.findOne).toHaveBeenCalledWith({ _id: mockListId, places: { placeId: mockPlaceId } }, '-_id');
  });

  // ChatGPT usage: No
  it('throws error when getPlace is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';

    listModel.List.findOne = jest.fn().mockRejectedValue(new Error('Getting place failed'));
    await expect(listModel.getPlace(mockListId, mockPlaceId)).rejects.toThrowError();
    expect(listModel.List.findOne).toHaveBeenCalledWith({ _id: mockListId, places: { placeId: mockPlaceId } }, '-_id');
  });
});

describe('addPlaceToList', () => {
  // ChatGPT usage: No
  it('returns a list when adding a place using addPlaceToList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceData = { placeId: 'mock-place-id' };
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: [mockPlaceData]
    };

    listModel.List.findOne = jest.fn().mockResolvedValue(null);
    listModel.List.findOneAndUpdate = jest.fn().mockResolvedValue(mockListData);
    const result = await listModel.addPlaceToList(mockListId, mockPlaceData);

    expect(result).toBe(mockListData);
    expect(listModel.List.findOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockListId },
      { $push: { places: mockPlaceData } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when addPlaceToList is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceData = { placeId: 'mock-place-id' };

    listModel.List.findOne = jest.fn().mockResolvedValue(null);
    listModel.List.findOneAndUpdate = jest.fn().mockRejectedValue(new Error('Adding place to list failed'));

    await expect(listModel.addPlaceToList(mockListId, mockPlaceData)).rejects.toThrowError();
  });

  it('throws error when place already exists in the list: addPlaceToList', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceData = { placeId: 'mock-place-id' };

    listModel.List.findOne = jest.fn().mockResolvedValue(mockPlaceData);

    await expect(listModel.addPlaceToList(mockListId, mockPlaceData)).rejects.toThrowError();
  });
});

describe('removePlaceFromList', () => {
  // ChatGPT usage: No
  it('returns a list when removing a place using removePlaceFromList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    listModel.List.updateOne = jest.fn().mockResolvedValue(mockListData);
    const result = await listModel.removePlaceFromList(mockListId, mockPlaceId);

    expect(result).toBe(mockListData);
    expect(listModel.List.updateOne).toHaveBeenCalledWith(
      { _id: mockListId },
      { $pull: { places: { placeId: mockPlaceId } } },
      { new: true }
    );
  });

  // ChatGPT usage: No
  it('throws error when removePlaceFromList is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';

    listModel.List.updateOne = jest.fn().mockRejectedValue(new Error('Removing place from list failed'));

    await expect(listModel.removePlaceFromList(mockListId, mockPlaceId)).rejects.toThrowError();
    expect(listModel.List.updateOne).toHaveBeenCalledWith(
      { _id: mockListId },
      { $pull: { places: { placeId: mockPlaceId } } },
      { new: true }
    );
  });
});
