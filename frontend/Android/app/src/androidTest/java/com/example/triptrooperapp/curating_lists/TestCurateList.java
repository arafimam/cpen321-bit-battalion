package com.example.triptrooperapp.curating_lists;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static org.hamcrest.CoreMatchers.is;

import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.triptrooperapp.GroupScreen;
import com.example.triptrooperapp.GroupsActivity;
import com.example.triptrooperapp.ListActivity;
import com.example.triptrooperapp.ListScreen;
import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.R;
import com.example.triptrooperapp.SignInScreen;
import com.example.triptrooperapp.TestFramework;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)

// Use Case-1 List Creation with users and group.
public class TestCurateList {

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

    // function to delete any created user list.

    private void deleteUserList(String listName) {
        ActivityScenario.launch(ListActivity.class);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.clickWithId(R.id.action_delete);
        TestFramework.clickViewWithText("Confirm");
    }

    // Chat GPT usage: No
    @Test
    public void testCreateUserList() {
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

        // clean up.
        deleteUserList(listName);
    }

    // Chat GPT usage: No
    @Test
    public void testDeleteUserList() {
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

    // Chat GPT usage: No
    @Test
    public void testPopulateUserList() {
        List<String> placesAdded;
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        TestFramework.clickWithId(R.id.create_list);
        enterDestinationNameByVerifying("UBC", listName, true);

        // add 3 places.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        placesAdded = selectThreePlaces();
        // select on a 4th place but close the dialog.
        TestFramework.clickViewWithTag("place3");
        TestFramework.clickViewWithText("Cancel");

        // verify we are still in the my places screen.
        TestFramework.isViewWithTextDisplayed("Your Places");

        // go to the list details screen
        ActivityScenario.launch(ListActivity.class);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        checkIfThreePlacesAdded(placesAdded);


        // clean up.
        deleteUserList(listName);
    }

    // Chat GPT usage: No
    @Test
    public void testOptimizeUserList() {
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // click on optimize button without any place added.
        TestFramework.clickWithId(R.id.optimize_button);

        // verify dialog showing schedule unavailable
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.isViewWithTextDisplayed("Schedule Creation Unavailable");
        TestFramework.clickViewWithText("Close");

        // add one place and try optimizing
        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField, "UBC");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        TestFramework.clickViewWithTag("place0");
        TestFramework.clickViewWithText("Add");
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        // verify optimize schedule is still unavailable
        ActivityScenario.launch(ListActivity.class);
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickWithId(R.id.optimize_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.isViewWithTextDisplayed("Schedule Creation Unavailable");
        TestFramework.clickViewWithText("Close");

        // add 3 places
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
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
        TestFramework.isViewWithTextDisplayed("Optimized schedule ready");
        TestFramework.isViewWithTextDisplayed("View in Maps");

        // clean up.
        deleteUserList(listName);
    }

    // Chat GPT usage: No
    @Test
    public void testRemovePlacesFromUserList() {
        ListScreen.navigateToListScreen();

        final String listName = ListScreen.getRandomListName();
        ListScreen.createListWithText(listName);

        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // add few places.
        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField, "UBC");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        selectThreePlaces();

        ActivityScenario.launch(ListActivity.class);
        TestFramework.clickViewWithText(listName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        removeThreePlaces();

        // verify no place exist in the screen
        TestFramework.isViewWithTextNotDisplayed("View Place.");

        //cleanup.
        deleteUserList(listName);
    }

    // Chat GPT usage: No
    @Test
    public void testCreateGroupList() {
        GroupScreen.navigateToGroupScreen();

        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();

        // creating a a group list with name
        final String groupListName = groupName + "list";
        GroupScreen.createGroupList(groupListName);

        // verify group is successfully created.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.isViewWithTextDisplayed(groupListName);

        // create a group with empty name
        GroupScreen.createGroupList("");
        Assert.assertTrue("Dialog closed even when list with empty name " +
                        "created",
                TestFramework.isViewWithIdDisplayed(R.id.create_list_button));

        // clean up
        GroupScreen.deleteGroup(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testDeleteGroupList() {
        GroupScreen.navigateToGroupScreen();

        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);
        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        // creating a a group list with name
        final String groupListName = groupName + "list";
        GroupScreen.createGroupList(groupListName);

        TestFramework.clickViewWithText(groupListName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.clickWithId(R.id.action_delete);

        // click on close and see if we still remain in the group screen
        TestFramework.clickViewWithText("Close");
        TestFramework.isViewWithIdDisplayed(R.id.action_delete);

        // confirm delete
        TestFramework.clickWithId(R.id.action_delete);
        TestFramework.clickViewWithText("Confirm");

        // verify group list is not present
        TestFramework.isViewWithTextNotDisplayed(groupListName);

        // clean up
        GroupScreen.deleteGroup(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testOptimizeGroupList() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        // creating a a group list with name
        final String groupListName = groupName + "list";
        GroupScreen.createGroupList(groupListName);

        TestFramework.clickViewWithText(groupListName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.clickWithId(R.id.optimize_button);
        // verify dialog showing schedule unavailable
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.isViewWithTextDisplayed("Schedule Creation Unavailable");
        TestFramework.clickViewWithText("Close");

        // add one place and try optimizing
        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField, "UBC");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        TestFramework.clickViewWithTag("place0");
        TestFramework.clickViewWithText("Add");
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        TestFramework.clickViewWithText(groupListName);

        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickWithId(R.id.optimize_button);

        // verify dialog showing schedule unavailable
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        TestFramework.isViewWithTextDisplayed("Schedule Creation Unavailable");
        TestFramework.clickViewWithText("Close");

        // now add more places.
        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                "SURREY");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        selectThreePlaces();

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        TestFramework.clickViewWithText(groupListName);

        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickWithId(R.id.optimize_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        // verify optimize schedule is now available
        TestFramework.isViewWithTextDisplayed("Optimized schedule ready");
        TestFramework.isViewWithTextDisplayed("View in Maps");

        // clean up
        GroupScreen.deleteGroup(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testRemovePlacesFromGroupList() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);
        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        // creating a a group list with name
        final String groupListName = groupName + "list";
        GroupScreen.createGroupList(groupListName);

        TestFramework.clickViewWithText(groupListName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                "SURREY");
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        selectThreePlaces();

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        TestFramework.clickViewWithText(groupListName);

        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        removeThreePlaces();
        // verify no place exist in the screen
        TestFramework.isViewWithTextNotDisplayed("View Place.");

        // clean up
        GroupScreen.deleteGroup(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testPopulateGroupList() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        // creating a a group list with name
        final String groupListName = groupName + "list";
        GroupScreen.createGroupList(groupListName);

        TestFramework.clickViewWithText(groupListName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));

        //TestFramework.clickWithId(R.id.create_list);
        enterDestinationNameByVerifying("SURREY", groupListName, false);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(3000));
        List<String> groupSelectedPlaces;
        groupSelectedPlaces = selectThreePlaces();

        ActivityScenario.launch(GroupsActivity.class);
        TestFramework.clickViewWithText(groupName);
        GroupScreen.clickOnViewList();
        TestFramework.clickViewWithText(groupListName);

        checkIfThreePlacesAdded(groupSelectedPlaces);

        // clean up
        GroupScreen.deleteGroup(groupName);
    }

    private void enterDestinationNameByVerifying(String destinationName,
                                                 String listName,
                                                 boolean verify) {
        if (verify) {
            TestFramework.clickViewWithText("Explore places by destination");
            TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                    "");
            TestFramework.clickWithId(R.id.create_list_button);

            // verify not navigated to places screen
            onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
            TestFramework.isViewWithTextDisplayed("Explore places by " +
                    "destination");

            TestFramework.clickViewWithText("Explore places by destination");
            TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                    "lyop hghnbty njghty aswq fghty");
            TestFramework.clickWithId(R.id.create_list_button);
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
            TestFramework.isViewWithTextDisplayed("Something went wrong");

            // now enter the correct destination place
            ActivityScenario.launch(ListActivity.class);
            TestFramework.clickViewWithText(listName);

            onView(isRoot()).perform(TestFramework.waitIdlingResource(1000));
        }

        TestFramework.clickWithId(R.id.create_list);
        TestFramework.clickViewWithText("Explore places by destination");
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                destinationName);
        TestFramework.clickWithId(R.id.create_list_button);
    }

    private List<String> selectThreePlaces() {
        List<String> placeName = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            placeName.add(TestFramework.getText(withTagValue(is("placeName" + i))));
            Log.d("TAG", placeName.get(i));
            TestFramework.clickViewWithTag("place" + i);
            TestFramework.clickViewWithText("Add");
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        }

        return placeName;
    }

    private void removeThreePlaces() {
        for (int i = 0; i < 3; i++) {
            TestFramework.clickViewWithTag("remove" + i);
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
            TestFramework.clickViewWithText("Remove");
            onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        }
    }

    private void checkIfThreePlacesAdded(List<String> placeNames) {
        for (int i = 0; i < 3; i++) {
            TestFramework.isViewWithTextDisplayed(placeNames.get(i));
            Assert.assertTrue("Place not visible",
                    TestFramework.isViewWithTagDisplayed("place" + i));
        }
    }

}
