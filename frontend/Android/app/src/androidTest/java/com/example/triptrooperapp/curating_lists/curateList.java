package com.example.triptrooperapp.curating_lists;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.triptrooperapp.ListActivity;
import com.example.triptrooperapp.ListScreen;
import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.R;
import com.example.triptrooperapp.SignInScreen;
import com.example.triptrooperapp.TestFramework;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

// Use Case-1 List Creation with users and group.
public class curateList {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testCreateUserList() {
        SignInScreen.signInIfNotAlreadySignedIn();
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        // verify if list with name "new List" is created.
        Assert.assertTrue("List not created",
                TestFramework.isViewWithTextDisplayed(listName));

        // 5a. user provides no list name.
        ListScreen.createListWithText("");
        Assert.assertTrue("Dialog closed even when list with empty name " +
                        "created",
                TestFramework.isViewWithIdDisplayed(R.id.create_list_button));
    }

    @Test
    public void testDeleteUserList() {
        SignInScreen.signInIfNotAlreadySignedIn();
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        TestFramework.clickWithId(R.id.action_delete);

        // 6a. the user click on close in the delete list dialog.
        TestFramework.clickViewWithText("Close");

        // verify user stays in the list details screen.
        TestFramework.isViewWithIdDisplayed(R.id.action_delete);

        TestFramework.clickWithId(R.id.action_delete);
        TestFramework.clickViewWithText("Confirm");

        // verify user is navigated to the list screen
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.isViewWithIdDisplayed(R.id.create_list);

        // verify list with listName no longer exists.
        Assert.assertTrue("List still exists in list screen",
                TestFramework.isViewWithTextNotDisplayed(listName));
    }

    @Test
    public void testPopulateUserList() {
        SignInScreen.signInIfNotAlreadySignedIn();
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField, "UBC");
        TestFramework.clickWithId(R.id.create_list_button);

        // add 3 places.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        selectThreePlaces();
        // select on a 4th place but close the dialog.
        TestFramework.clickViewWithTag("place3");
        TestFramework.clickViewWithText("Cancel");

        // verify we are still in the my places screen.
        TestFramework.isViewWithTextDisplayed("Your Places");

        // go to the list details screen
        try (ActivityScenario<ListActivity> scenario =
                     ActivityScenario.launch(ListActivity.class)) {
            onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
            TestFramework.clickViewWithText(listName);
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
            checkIfThreePlacesAdded();
        }
    }

    @Test
    public void testOptimizeUserList() {

    }

    private void selectThreePlaces() {
        for (int i = 0; i < 3; i++) {
            TestFramework.clickViewWithTag("place" + i);
            TestFramework.clickViewWithText("Add");
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        }
    }

    private void checkIfThreePlacesAdded() {
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue("Place not visible",
                    TestFramework.isViewWithTagDisplayed("place" + i));
        }
    }

}
