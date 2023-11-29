package com.example.triptrooperapp.group_management;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static com.example.triptrooperapp.SignInScreen.isSignedIn;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.triptrooperapp.GroupScreen;
import com.example.triptrooperapp.GroupsActivity;
import com.example.triptrooperapp.MainActivity;
import com.example.triptrooperapp.R;
import com.example.triptrooperapp.SignInScreen;
import com.example.triptrooperapp.TestFramework;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TestGroupManagement {


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
    public void testCreateGroup() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);


        ActivityScenario.launch(GroupsActivity.class);

        // verify if name exist
        TestFramework.isViewWithTextDisplayed(groupName);

        // try creating a group with empty name.
        GroupScreen.createGroupWithName("");

        //verify we are still in the create group screen
        TestFramework.isViewWithTextDisplayed("Empty group name or group code");

        //cleanup
        GroupScreen.deleteGroup(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testDeleteGroup() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);


        ActivityScenario.launch(GroupsActivity.class);

        GroupScreen.deleteGroup(groupName);
        TestFramework.isViewWithTextNotDisplayed(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testJoinGroup() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);


        ActivityScenario.launch(GroupsActivity.class);

        // get the group code.
        String fullCode = TestFramework.getText(withTagValue(is("sideTitle0")));
        String code = fullCode.replaceAll("Group code: ", "");

        SignInScreen.signOutAndSignInWithAnotherUser(1);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        GroupScreen.navigateToGroupScreen();
        GroupScreen.joinGroupWithCode(code);


        ActivityScenario.launch(GroupsActivity.class);
        GroupScreen.navigateToGroupScreen();

        // verify successfully joined group.
        TestFramework.isViewWithTextDisplayed(fullCode);

        // test with incorrect group code.
        GroupScreen.joinGroupWithCode("123456");
        // verify alert message is shown.
        TestFramework.isViewWithTextDisplayed("Incorrect group code");

        // test with empty join code.
        ActivityScenario.launch(GroupsActivity.class);
        GroupScreen.joinGroupWithCode("");
        TestFramework.isViewWithTextDisplayed("Empty group name or group code");

        // clean up.
        GroupScreen.deleteGroup(fullCode);
    }

    @Test
    public void testJoinGroupAlreadyJoined() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);

        // get the group code.
        String fullCode = TestFramework.getText(withTagValue(is("sideTitle0")));
        String code = fullCode.replaceAll("Group code: ", "");

        GroupScreen.joinGroupWithCode(code);

        // verify user sees an error message
        TestFramework.isViewWithTextDisplayed("Something went wrong");

    }

    // Chat GPT usage: No
    @Test
    public void testLeaveGroup() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);

        TestFramework.clickViewWithText(groupName);
        TestFramework.clickWithId(R.id.leave_group);

        // close the dialog.
        TestFramework.clickViewWithText("Close");
        TestFramework.isViewWithIdDisplayed(R.id.action_delete);

        TestFramework.clickWithId(R.id.leave_group);
        TestFramework.clickViewWithText("Leave");

        // verify group name doesnt exit.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.isViewWithTextNotDisplayed(groupName);
    }

    // Chat GPT usage: No
    @Test
    public void testGroupMembersOnlyJoin() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);

        // get the group code.
        String fullCode = TestFramework.getText(withTagValue(is("sideTitle0")));
        String code = fullCode.replaceAll("Group code: ", "");

        SignInScreen.signOutAndSignInWithAnotherUser(1);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        GroupScreen.navigateToGroupScreen();
        GroupScreen.joinGroupWithCode(code);


        ActivityScenario.launch(GroupsActivity.class);
        GroupScreen.navigateToGroupScreen();
        TestFramework.clickViewWithText(fullCode);
        TestFramework.clickWithId(R.id.member_btn);

        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        // verify two members
        TestFramework.isViewWithTagDisplayed("member0");
        TestFramework.isViewWithTagDisplayed("member1");
    }

    // Chat GPT usage: No
    @Test
    public void testGroupMembersWithLeave() {
        GroupScreen.navigateToGroupScreen();
        final String groupName = GroupScreen.getRandomGroupName();
        GroupScreen.createGroupWithName(groupName);

        ActivityScenario.launch(GroupsActivity.class);

        // get the group code.
        String fullCode = TestFramework.getText(withTagValue(is("sideTitle0")));
        String code = fullCode.replaceAll("Group code: ", "");

        // verify member count is 1.
        TestFramework.clickViewWithText(groupName);
        TestFramework.clickWithId(R.id.member_btn);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.isViewWithTagDisplayed("member0");

        // Join with another member.
        SignInScreen.signOutAndSignInWithAnotherUser(1);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        GroupScreen.navigateToGroupScreen();
        GroupScreen.joinGroupWithCode(code);


        ActivityScenario.launch(GroupsActivity.class);
        GroupScreen.navigateToGroupScreen();
        TestFramework.clickViewWithText(fullCode);
        TestFramework.clickWithId(R.id.member_btn);

        // verify two members.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        // verify two members
        TestFramework.isViewWithTagDisplayed("member0");
        TestFramework.isViewWithTagDisplayed("member1");

        // close the dialog and leave the group.
        TestFramework.clickViewWithText("Close");
        TestFramework.clickWithId(R.id.leave_group);
        TestFramework.clickViewWithText("Leave");

        // sign back in with the first account and check the count.
        SignInScreen.signOutAndSignInWithAnotherUser(0);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        GroupScreen.navigateToGroupScreen();

        TestFramework.clickViewWithText(groupName);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.clickWithId(R.id.member_btn);

        // check that there is only member.
        TestFramework.isViewWithTagDisplayed("member0");
        TestFramework.isViewWithTagNotDisplayed("member1");

        GroupScreen.deleteGroup(groupName);
    }
}
