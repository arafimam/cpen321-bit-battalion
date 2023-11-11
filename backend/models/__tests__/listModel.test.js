const listModel = require('../listModel.js');

const mockCreate = jest.spyOn(listModel.List, 'create');
const mockFindById = jest.spyOn(listModel.List, 'findById');
const mockFindOne = jest.spyOn(listModel.List, 'findOne');
const mockFindByIdAndDelete = jest.spyOn(listModel.List, 'findByIdAndDelete');
const mockUpdateOne = jest.spyOn(listModel.List, 'updateOne');
const mockFindOneAndUpdate = jest.spyOn(listModel.List, 'findOneAndUpdate');

describe('createList', () => {
  it('returns list object when createList is successful', async () => {
    const mockListName = 'mock-list-name';
    const mockListData = {
      listName: mockListName,
      places: []
    };

    mockCreate.mockResolvedValue(mockListData);
    const result = await listModel.createList(mockListName);

    expect(result).toBe(mockListData);
    expect(mockCreate).toHaveBeenCalledWith({ listName: mockListName });
  });

  it('throws error when createList is unsuccessful', async () => {
    const mockListName = 'mock-list-name';

    mockCreate.mockRejectedValue(new Error('List creation failed'));
    await expect(listModel.createList(mockListName)).rejects.toThrowError();
    expect(mockCreate).toHaveBeenCalledWith({ listName: mockListName });
  });
});

describe('deleteList', () => {
  it('returns list object when deleteList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    mockFindByIdAndDelete.mockResolvedValue(mockListData);
    const result = await listModel.deleteList(mockListId);

    expect(result).toBe(mockListData);
    expect(mockFindByIdAndDelete).toHaveBeenCalledWith(mockListId);
  });

  it('throws error when deleteList is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    mockFindByIdAndDelete.mockRejectedValue(new Error('List deletion failed'));

    await expect(listModel.deleteList(mockListId)).rejects.toThrowError();
    expect(mockFindByIdAndDelete).toHaveBeenCalledWith(mockListId);
  });
});

describe('getListById', () => {
  it('returns a list by Id when getListById is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    mockFindById.mockResolvedValue(mockListData);
    const result = await listModel.getListById(mockListId);

    expect(result).toBe(mockListData);
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });

  it('throws error when getListById is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    mockFindById.mockRejectedValue(new Error('Getting list by id failed'));

    await expect(listModel.getListById(mockListId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getListName', () => {
  it('returns a listName by Id when getListName is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    mockFindById.mockReturnValue({
      select: jest.fn().mockResolvedValue(mockListData)
    });
    const result = await listModel.getListName(mockListId);

    expect(result).toBe(mockListData);
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });

  it('throws error when getListName is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    mockFindById.mockReturnValue(new Error('Getting listName failed'));

    await expect(listModel.getListName(mockListId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getPlaces', () => {
  it('returns a list of places by listId when getPlaces is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlacesData = [{ placeId: 'mock-place-id' }, { placeId: 'mock-place-id-2' }];

    mockFindById.mockReturnValue({
      select: jest.fn().mockResolvedValue(mockPlacesData)
    });
    const result = await listModel.getPlaces(mockListId);

    expect(result).toBe(mockPlacesData);
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });

  it('throws error when getPlaces is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    mockFindById.mockReturnValue(new Error('Getting places failed'));

    await expect(listModel.getPlaces(mockListId)).rejects.toThrowError();
    expect(mockFindById).toHaveBeenCalledWith(mockListId);
  });
});

describe('getPlace', () => {
  it('returns a place by placeId when getPlace is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';
    const mockPlaceData = { placeId: 'mock-place-id' };

    mockFindOne.mockResolvedValue(mockPlaceData);
    const result = await listModel.getPlace(mockListId, mockPlaceId);

    expect(result).toBe(mockPlaceData);
    expect(mockFindOne).toHaveBeenCalledWith({ _id: mockListId, places: { placeId: mockPlaceId } }, '-_id');
  });

  it('throws error when getPlace is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';
    mockFindOne.mockRejectedValue(new Error('Getting place failed'));

    await expect(listModel.getPlace(mockListId, mockPlaceId)).rejects.toThrowError();
    expect(mockFindOne).toHaveBeenCalledWith({ _id: mockListId, places: { placeId: mockPlaceId } }, '-_id');
  });
});

describe('addPlaceToList', () => {
  it('returns a list when adding a place using addPlaceToList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceData = { placeId: 'mock-place-id' };
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: [mockPlaceData]
    };

    mockFindOneAndUpdate.mockResolvedValue(mockListData);
    const result = await listModel.addPlaceToList(mockListId, mockPlaceData);

    expect(result).toBe(mockListData);
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockListId },
      { $push: { places: mockPlaceData } },
      { new: true }
    );
  });

  it('throws error when addPlaceToLisst is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceData = { placeId: 'mock-place-id' };

    mockFindOneAndUpdate.mockRejectedValue(new Error('Adding place to list failed'));

    await expect(listModel.addPlaceToList(mockListId, mockPlaceData)).rejects.toThrowError();
    expect(mockFindOneAndUpdate).toHaveBeenCalledWith(
      { _id: mockListId },
      { $push: { places: mockPlaceData } },
      { new: true }
    );
  });
});

describe('removePlaceFromList', () => {
  it('returns a list when removing a place using removePlaceFromList is successful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';
    const mockListData = {
      _id: mockListId,
      listName: 'mock-list-name',
      places: []
    };

    mockUpdateOne.mockResolvedValue(mockListData);
    const result = await listModel.removePlaceFromList(mockListId, mockPlaceId);

    expect(result).toBe(mockListData);
    expect(mockUpdateOne).toHaveBeenCalledWith(
      { _id: mockListId },
      { $pull: { places: { placeId: mockPlaceId } } },
      { new: true }
    );
  });

  it('throws error when removePlaceFromList is unsuccessful', async () => {
    const mockListId = 'mock-list-id';
    const mockPlaceId = 'mock-place-id';

    mockUpdateOne.mockRejectedValue(new Error('Removing place from list failed'));

    await expect(listModel.removePlaceFromList(mockListId, mockPlaceId)).rejects.toThrowError();
    expect(mockUpdateOne).toHaveBeenCalledWith(
      { _id: mockListId },
      { $pull: { places: { placeId: mockPlaceId } } },
      { new: true }
    );
  });
});
