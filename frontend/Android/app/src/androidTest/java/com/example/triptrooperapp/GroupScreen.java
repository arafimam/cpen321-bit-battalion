package com.example.triptrooperapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public class GroupScreen {

    /**
     * Navigates to the group screen from the bottom navigation bar.
     */
    public static void navigateToGroupScreen() {
        TestFramework.clickWithId(R.id.nav_group);
        // wait for some time for the group screen to load.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    /**
     * Creates a group with name from the group main screen.
     *
     * @param groupName
     */
    public static void createGroupWithName(String groupName) {
        TestFramework.clickWithId(R.id.create_group);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.setTextToTextFieldWithId(R.id.group_name_text_field,
                groupName);
        TestFramework.clickWithId(R.id.create_group);
    }

    /**
     * Clicks on view List.
     */
    public static void clickOnViewList() {
        TestFramework.clickWithId(R.id.list_btn);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    /**
     * Creates a group from group list screen.
     *
     * @param listName
     */
    public static void createGroupList(String listName) {
        TestFramework.clickWithId(R.id.create_list);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                listName);
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    /**
     * Returns a random List Name.
     */
    public static String getRandomGroupName() {
        double randomDouble = Math.random();
        int randomIntInRange = (int) (Math.random() * ((1000) + 1));
        return "Group " + randomIntInRange;
    }

}
