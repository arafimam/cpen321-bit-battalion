const listService = require('../listService.js');
const listModel = require('../../models/listModel.js');

jest.mock('../../models/listModel.js', () => ({
  getListById: jest.fn(),
  getListName: jest.fn(),
  deleteList: jest.fn(),
  addPlaceToList: jest.fn(),
  removePlaceFromList: jest.fn(),
  createList: jest.fn(),
  getPlaces: jest.fn(),
  getPlace: jest.fn()
}));

const mockListName = 'mock-list-name';
const mockListId = 'mock-list-id';
const mockPlaceId = 'mock-place-id';

describe('createList', () => {
  // ChatGPT usage: No
  it('creates a list successfully and returns id of new list', async () => {
    listModel.createList.mockResolvedValue({ _id: mockListId });

    const result = await listService.createList(mockListName);

    expect(result).toEqual({ _id: mockListId });
  });

  // ChatGPT usage: No
  it('handles errors when creating a list', async () => {
    listModel.createList.mockRejectedValue(new Error('Failed to create list'));

    await expect(listService.createList(mockListName)).rejects.toThrowError(
      'Error in service while creating list: Failed to create list'
    );
  });
});

describe('deleteListById', () => {
  // ChatGPT usage: No
  it('deletes a list by ID successfully', async () => {
    listModel.deleteList.mockResolvedValue(true);

    const result = await listService.deleteListById(mockListId);

    expect(result).toBe(true);
  });
});

describe('getListById', () => {
  // ChatGPT usage: No
  it('gets a list by ID successfully', async () => {
    const mockList = { id: mockListId, name: 'Mock List' };

    listModel.getListById.mockResolvedValue(mockList);

    const result = await listService.getListById(mockListId);

    expect(result).toEqual(mockList);
  });
});

describe('getListName', () => {
  // ChatGPT usage: No
  it('gets a list name by ID successfully', async () => {
    listModel.getListName.mockResolvedValue(mockListName);

    const result = await listService.getListName(mockListId);

    expect(result).toBe(mockListName);
  });
});

describe('getPlacesByListId', () => {
  // ChatGPT usage: No
  it('gets places by list ID successfully', async () => {
    const mockPlaces = [
      { id: 'place-id1', name: 'place1' },
      { id: 'place-id2', name: 'place2' }
    ];

    listModel.getPlaces.mockResolvedValue(mockPlaces);

    const result = await listService.getPlacesByListId(mockListId);

    expect(result).toEqual(mockPlaces);
  });
});

describe('addPlaceToList', () => {
  // ChatGPT usage: No
  it('adds a place to the list successfully', async () => {
    const mockPlaceData = {
      placeId: 'mock-place-id',
      displayName: 'mock-display-name',
      location: 'mock-location'
    };
    const mockList = { ...mockPlaceData };

    listModel.addPlaceToList.mockResolvedValue(mockList);

    const result = await listService.addPlaceToList(mockListId, mockPlaceData);

    expect(result).toEqual({
      placeAlreadyExistsInList: false,
      list: mockList
    });
  });

  it('place already exists in the list', async () => {
    const mockPlaceData = {
      placeId: 'mock-place-id',
      displayName: 'mock-display-name',
      location: 'mock-location'
    };

    listModel.addPlaceToList.mockRejectedValue(new Error('Place already exists in list'));

    const result = await listService.addPlaceToList(mockListId, mockPlaceData);

    expect(result).toEqual({
      placeAlreadyExistsInList: true
    });
  });

  it('adding place to list is unsuccessful', async () => {
    const mockPlaceData = {
      placeId: 'mock-place-id',
      displayName: 'mock-display-name',
      location: 'mock-location'
    };

    listModel.addPlaceToList.mockRejectedValue(new Error('Failed to add place to list'));

    await expect(listService.addPlaceToList(mockListId, mockPlaceData)).rejects.toThrowError();
  });
});

describe('removePlaceFromList', () => {
  // ChatGPT usage: No
  it('removes a place from the list successfully', async () => {
    listModel.removePlaceFromList.mockResolvedValue(true);

    const result = await listService.removePlaceFromList(mockListId, mockPlaceId);

    expect(result).toBe(true);
  });
});

describe('createScheduleForList', () => {
  // ChatGPT usage: No
  it('creates a schedule for the list successfully', async () => {
    const mockPlaceIds = ['mock-place-id1', 'mock-place-id2'];
    const mockPlaces = [
      { placeId: 'mock-place-id1', name: 'place1' },
      { placeId: 'mock-place-id2', name: 'place2' },
      { placeId: 'mock-place-id3', name: 'place3' }
    ];
    const mockSchedule = [
      { placeId: 'mock-place-id1', name: 'place1' },
      { placeId: 'mock-place-id2', name: 'place2' }
    ];

    listModel.getPlaces.mockResolvedValue({ places: mockPlaces });

    jest.mock('../../utils/twoOpt.js', () => ({
      twoOpt: jest.fn(() => mockSchedule)
    }));

    const result = await listService.createScheduleForList(mockListId, mockPlaceIds);

    expect(result).toStrictEqual(mockSchedule);
  });
});
