process.env['NODE_ENV'] = 'TEST';
const placesService = require('../placesService');

global.fetch = jest.fn();

const mockPlacesResponse = [
  {
    name: 'places/ChIJ5UuDwMtyhlQRa2nfU9eAqRQ',
    nationalPhoneNumber: '(604) 822-1953',
    location: {
      latitude: 49.2601989,
      longitude: -123.24837830000001
    },
    rating: 3.2,
    websiteUri: 'https://locations.timhortons.ca/en/bc/vancouver/2424-main-hall/',
    displayName: {
      text: 'Tim Hortons',
      languageCode: 'en'
    },
    shortFormattedAddress: 'Forest Sciences Centre, 2424 Main Mall Forestry, Vancouver'
  },
  {
    name: 'places/ChIJ12vSC8lyhlQRKc0MuP873R4',
    nationalPhoneNumber: '(604) 827-3291',
    location: {
      latitude: 49.2622,
      longitude: -123.24499399999999
    },
    rating: 3.3,
    websiteUri: 'https://food.ubc.ca/places/perugia-italian-caffe/',
    regularOpeningHours: {
      openNow: false,
      periods: [
        {
          open: {
            day: 1,
            hour: 8,
            minute: 0
          },
          close: {
            day: 1,
            hour: 16,
            minute: 0
          }
        },
        {
          open: {
            day: 2,
            hour: 8,
            minute: 0
          },
          close: {
            day: 2,
            hour: 16,
            minute: 0
          }
        },
        {
          open: {
            day: 3,
            hour: 8,
            minute: 0
          },
          close: {
            day: 3,
            hour: 16,
            minute: 0
          }
        },
        {
          open: {
            day: 4,
            hour: 8,
            minute: 0
          },
          close: {
            day: 4,
            hour: 16,
            minute: 0
          }
        },
        {
          open: {
            day: 5,
            hour: 8,
            minute: 0
          },
          close: {
            day: 5,
            hour: 16,
            minute: 0
          }
        }
      ],
      weekdayDescriptions: [
        'Monday: 8:00 AM – 4:00 PM',
        'Tuesday: 8:00 AM – 4:00 PM',
        'Wednesday: 8:00 AM – 4:00 PM',
        'Thursday: 8:00 AM – 4:00 PM',
        'Friday: 8:00 AM – 4:00 PM',
        'Saturday: Closed',
        'Sunday: Closed'
      ]
    },
    displayName: {
      text: 'Perugia Italian Caffè',
      languageCode: 'en'
    },
    shortFormattedAddress: 'Life Sciences Centre, 2350 Health Sciences Mall, Vancouver'
  },
  {
    name: 'places/ChIJmwk7wZtzhlQRdEE8U1LWUqk',
    location: {
      latitude: 49.2622773,
      longitude: -123.24935370000001
    },
    websiteUri: 'https://ubcengineers.ca/eatery',
    displayName: {
      text: 'Eatery',
      languageCode: 'en'
    },
    shortFormattedAddress: 'UBC Engineering Courtyard, 2335 Engineering Rd, Vancouver'
  },
  {
    name: 'places/ChIJNaC9IVNzhlQR0cBcrjxGIzg',
    nationalPhoneNumber: '(604) 827-1525',
    location: {
      latitude: 49.264780099999996,
      longitude: -123.2467318
    },
    rating: 5,
    websiteUri: 'https://campusnutrition.ca/find-food/fooood_woodward',
    displayName: {
      text: 'Fooood 2.0',
      languageCode: 'en'
    },
    shortFormattedAddress: 'Instructional Resources Centre, 2194 Health Sciences Mall, Vancouver'
  },
  {
    name: 'places/ChIJa3gFJ2JzhlQRfo3lxslovKU',
    nationalPhoneNumber: '(604) 822-1992',
    location: {
      latitude: 49.261055899999995,
      longitude: -123.24823850000001
    },
    websiteUri: 'https://food.ubc.ca/places/pho-real/',
    regularOpeningHours: {
      openNow: false,
      periods: [
        {
          open: {
            day: 1,
            hour: 10,
            minute: 0
          },
          close: {
            day: 1,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 2,
            hour: 10,
            minute: 0
          },
          close: {
            day: 2,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 3,
            hour: 10,
            minute: 0
          },
          close: {
            day: 3,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 4,
            hour: 10,
            minute: 0
          },
          close: {
            day: 4,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 5,
            hour: 10,
            minute: 0
          },
          close: {
            day: 5,
            hour: 17,
            minute: 0
          }
        }
      ],
      weekdayDescriptions: [
        'Monday: 10:00 AM – 5:00 PM',
        'Tuesday: 10:00 AM – 5:00 PM',
        'Wednesday: 10:00 AM – 5:00 PM',
        'Thursday: 10:00 AM – 5:00 PM',
        'Friday: 10:00 AM – 5:00 PM',
        'Saturday: Closed',
        'Sunday: Closed'
      ]
    },
    displayName: {
      text: 'Pho Real',
      languageCode: 'en'
    },
    shortFormattedAddress: 'Hugh Dempster Pavilion, ICICS Computer Science, 6245 Agronomy Rd, Vancouver'
  },
  {
    name: 'places/ChIJgSU_Ry1zhlQR4QIXML6Y8iI',
    nationalPhoneNumber: '(604) 822-5805',
    location: {
      latitude: 49.2610081,
      longitude: -123.2491673
    },
    websiteUri: 'https://food.ubc.ca/places/pho-real/',
    regularOpeningHours: {
      openNow: false,
      periods: [
        {
          open: {
            day: 1,
            hour: 10,
            minute: 0
          },
          close: {
            day: 1,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 2,
            hour: 10,
            minute: 0
          },
          close: {
            day: 2,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 3,
            hour: 10,
            minute: 0
          },
          close: {
            day: 3,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 4,
            hour: 10,
            minute: 0
          },
          close: {
            day: 4,
            hour: 17,
            minute: 0
          }
        },
        {
          open: {
            day: 5,
            hour: 10,
            minute: 0
          },
          close: {
            day: 5,
            hour: 17,
            minute: 0
          }
        }
      ],
      weekdayDescriptions: [
        'Monday: 10:00 AM – 5:00 PM',
        'Tuesday: 10:00 AM – 5:00 PM',
        'Wednesday: 10:00 AM – 5:00 PM',
        'Thursday: 10:00 AM – 5:00 PM',
        'Friday: 10:00 AM – 5:00 PM',
        'Saturday: Closed',
        'Sunday: Closed'
      ]
    },
    displayName: {
      text: 'Pho Real',
      languageCode: 'en'
    },
    shortFormattedAddress: 'ICICS Computer Science, 2366 Main Mall, Vancouver'
  }
];

describe('getPlacesNearby', () => {
  placesService.processPlacesResponse = jest.fn();
  placesService.processPlacesResponse.mockReturnValue(mockPlacesResponse);

  // ChatGPT usage: No
  it.each(['restaurant', undefined])('return a list of places nearby', async (category) => {
    global.fetch.mockResolvedValue({
      ok: true,
      json: () => {
        return { places: mockPlacesResponse };
      }
    });

    const result = await placesService.getPlacesNearby(0, 0, category);

    expect(result.status).toBe(200);
  });

  // ChatGPT usage: No
  it('returns a 400 status when Google response is not ok', async () => {
    global.fetch.mockResolvedValue({
      ok: false
    });

    const result = await placesService.getPlacesNearby(0, 0, 'restaurant');

    expect(result.status).toBe(400);
  });

  // ChatGPT usage: No
  it('returns a 500 status when fetch fails', async () => {
    global.fetch.mockRejectedValue({
      ok: false
    });

    const result = await placesService.getPlacesNearby(0, 0, 'restaurant');

    expect(result.status).toBe(500);
  });
});

describe('getPlacesByText', () => {
  placesService.processPlacesResponse = jest.fn();
  placesService.processPlacesResponse.mockReturnValue(mockPlacesResponse);

  // ChatGPT usage: No
  it.each(['restaurant', undefined])('return a list of places by textQuery', async (category) => {
    global.fetch.mockResolvedValue({
      ok: true,
      json: () => {
        return { places: mockPlacesResponse };
      }
    });

    const result = await placesService.getPlacesByText('mock place', category);

    expect(result.status).toBe(200);
  });

  // ChatGPT usage: No
  it('returns a 400 status when Google response is not ok', async () => {
    global.fetch.mockResolvedValue({
      ok: false
    });

    const result = await placesService.getPlacesByText('mock place', 'restaurant');

    expect(result.status).toBe(400);
  });

  // ChatGPT usage: No
  it('returns a 500 status when fetch fails', async () => {
    global.fetch.mockRejectedValue({
      ok: false
    });

    const result = await placesService.getPlacesByText('mock place', 'restaurant');

    expect(result.status).toBe(500);
  });
});
