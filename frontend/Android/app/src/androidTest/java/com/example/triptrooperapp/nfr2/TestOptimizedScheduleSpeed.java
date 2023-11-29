package com.example.triptrooperapp.nfr2;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static com.example.triptrooperapp.SignInScreen.isSignedIn;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.triptrooperapp.ListActivity;
import com.example.triptrooperapp.ListScreen;
import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.R;
import com.example.triptrooperapp.SignInScreen;
import com.example.triptrooperapp.TestFramework;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestOptimizedScheduleSpeed {


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
    public void testSpeedOfOptimizedSchedule() {
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
        selectFivePlaces();

        ActivityScenario.launch(ListActivity.class);
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        long startTime = System.currentTimeMillis();
        TestFramework.clickWithId(R.id.optimize_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // verify optimize schedule is now available
        TestFramework.clickViewWithText("View in Maps");

        long endTime = System.currentTimeMillis();

        long timePassed = endTime - startTime;
        assertTrue("Optimization is slower than the non functional requirement",
                timePassed <= 4000);
    }

    private void selectFivePlaces() {
        for (int i = 0; i < 5; i++) {
            TestFramework.clickViewWithTag("place" + i);
            TestFramework.clickViewWithText("Add");
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        }
    }
}
