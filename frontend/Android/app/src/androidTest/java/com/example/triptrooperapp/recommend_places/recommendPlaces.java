package com.example.triptrooperapp.recommend_places;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.SignInScreen;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class recommendPlaces {

    private static boolean isSignedIn = false;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void signInOnce() {
        if (!isSignedIn) {
            SignInScreen.signInIfNotAlreadySignedIn();
            isSignedIn = true;
        }
    }

    @Test
    public void testGetPlacesNearby() {

    }

    @Test
    public void testGetPlacesByDestination() {

    }

    @Test
    public void testViewPlaceDetails() {

    }

    @Test
    public void testViewPlaceMap() {

    }
}
