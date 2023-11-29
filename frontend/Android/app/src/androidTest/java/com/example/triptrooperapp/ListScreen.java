package com.example.triptrooperapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public class ListScreen {

    /**
     * Navigates to the user list screen via the bottom navigation bar.
     */
    public static void navigateToListScreen() {
        TestFramework.clickWithId(R.id.nav_list);
        // wait for some time for the list screen to load.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    public static void createListWithText(String listName) {
        // click on the floating action button
        TestFramework.clickWithId(R.id.create_list);

        // wait for dialog to appear.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));

        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                listName);
        TestFramework.clickWithId(R.id.create_list_button);

        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    /**
     * Returns a random List Name.
     */
    public static String getRandomListName() {
        int randomIntInRange = (int) (Math.random() * ((1000) + 1));
        return "List " + randomIntInRange;
    }
}
