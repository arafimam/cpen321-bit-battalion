package com.example.triptrooperapp.recommend_places;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static com.example.triptrooperapp.SignInScreen.isSignedIn;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.triptrooperapp.ActivityScreen;
import com.example.triptrooperapp.ListActivity;
import com.example.triptrooperapp.ListScreen;
import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.R;
import com.example.triptrooperapp.SignInScreen;
import com.example.triptrooperapp.TestFramework;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestRecommendPlaces {

    private static int deniedLocation = 0;
    private static int acceptLocation = 0;

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

    // Chat GPT usage: No
    @Test
    public void testDoNotGivePermissionToViewPlacesNearby() {
        ActivityScreen.navigateToActivityScreen();
        ActivityScreen.clickOnPlacesNearby();
        if (deniedLocation == 0 && acceptLocation == 0) {
            ActivityScreen.denyLocationPermissionFirstTime();
            deniedLocation++;
        } else if (deniedLocation == 1 || acceptLocation == 1) {
            ActivityScreen.denyLocationPermissionMoreThanOneTime();
            deniedLocation++;
        }

        // verify we are still in the activity screen
        TestFramework.isViewWithIdDisplayed(R.id.activity_location);

        // deny again
        ActivityScreen.clickOnPlacesNearby();
        if (deniedLocation == 0) {
            ActivityScreen.denyLocationPermissionFirstTime();
            deniedLocation++;
        } else if (deniedLocation == 1) {
            ActivityScreen.denyLocationPermissionMoreThanOneTime();
            deniedLocation++;
        }

        // verify we are still in the activity screen
        TestFramework.isViewWithIdDisplayed(R.id.activity_location);

        ActivityScreen.navigateToActivityScreen();
        ActivityScreen.clickOnPlacesNearby();
        if (acceptLocation == 0 && deniedLocation == 0) {
            ActivityScreen.acceptLocationFirstTime();
            acceptLocation++;
        } else if (acceptLocation == 1 || deniedLocation == 2) {
            ActivityScreen.acceptLocationSecondTime();
            acceptLocation++;
        }

        // verify we are in the nearby place screen and there are at least 1
        // place
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.isViewWithTextDisplayed("Local Gems near you");
        TestFramework.isViewWithTagDisplayed("viewPlace0");
    }

    // Chat GPT usage: No
    @Test
    public void testGetPlacesByDestination() {
        ActivityScreen.navigateToActivityScreen();
        ActivityScreen.clickOnPlacesByDestination();

        // trying with an empty destination
        ActivityScreen.enterDestination("");
        // verify we are still in the activity screen
        TestFramework.isViewWithIdDisplayed(R.id.activity_location);

        ActivityScreen.clickOnPlacesByDestination();
        ActivityScreen.enterDestination("UBC");

        // verify there is at least one place
        TestFramework.isViewWithTagDisplayed("viewPlace0");
    }

    // Chat GPT usage: No
    @Test
    public void testViewPlaceDetailsForDestination() {
        ActivityScreen.navigateToActivityScreen();
        ActivityScreen.clickOnPlacesByDestination();

        // trying with an empty destination
        ActivityScreen.enterDestination("");
        // verify we are still in the activity screen
        TestFramework.isViewWithIdDisplayed(R.id.activity_location);

        ActivityScreen.clickOnPlacesByDestination();
        ActivityScreen.enterDestination("UBC");

        TestFramework.clickViewWithTag("viewPlace0");

        // verify we are in the place details screen
        TestFramework.isViewWithIdDisplayed(R.id.view_in_map);
    }

    @Test
    public void testViewPlaceWithNonExistentDestination() {
        ActivityScreen.navigateToActivityScreen();
        ActivityScreen.clickOnPlacesByDestination();

        // trying with an empty destination
        ActivityScreen.enterDestination("lyop hghnbty njghty aswq fghty");
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        TestFramework.isViewWithTextDisplayed("Something went wrong");
    }

    // Chat GPT usage: No
    @Test
    public void testOptimizedScheduleRecommendation() {
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // add  places
        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                "SURREY");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        selectThreePlaces();

        ActivityScenario.launch(ListActivity.class);
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickWithId(R.id.optimize_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // verify optimize schedule is now available
        TestFramework.clickViewWithText("View in Maps");
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        TestFramework.clickWithId(R.id.summary);

        // verify summary page.
        TestFramework.isViewWithTextDisplayed("Our recommendation");

    }

    private void selectThreePlaces() {
        for (int i = 0; i < 3; i++) {
            TestFramework.clickViewWithTag("place" + i);
            TestFramework.clickViewWithText("Add");
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        }
    }
}
