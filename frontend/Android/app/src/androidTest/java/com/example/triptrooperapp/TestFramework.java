package com.example.triptrooperapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;

import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import org.hamcrest.Matcher;

public class TestFramework {

    /**
     * Method for idling resources.
     *
     * @param millis
     * @return
     */
    public static ViewAction waitIdlingResource(long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    /**
     * Checks if the element to click with Id is displayed.
     * If it is displayed one click is performed.
     *
     * @param id
     */
    public static void clickWithId(int id) {
        onView(withId(id)).check(matches(isDisplayed()))
                .perform(click());
    }

    /**
     * Checks if view with text is visible.
     * If it is displayed one click is performed.
     *
     * @param text
     */
    public static void clickViewWithText(String text) {
        onView(withText(text)).check(matches(isDisplayed()))
                .perform(click());
    }

    public static void clickViewWithTag(String tag) {
        onView(withTagValue(is(tag))).perform(click());
    }

    /**
     * Types the text textToType in the textField with Id "id"
     *
     * @param id
     * @param textToType
     */
    public static void setTextToTextFieldWithId(int id, String textToType) {
        onView(withId(id)).perform(typeText(textToType));
    }

    /**
     * Checks if view contains element with passed in text.
     *
     * @param text
     * @return
     */
    public static boolean isViewWithTextDisplayed(String text) {
        onView(withText(text)).check(matches(isDisplayed()));
        return true;
    }

    public static boolean isViewWithTextNotDisplayed(String text) {
        onView(withText(text)).check(doesNotExist());
        return true;
    }

    public static boolean isViewWithTagDisplayed(String tag) {
        onView(withTagValue(is(tag))).check(matches(isDisplayed()));
        return true;
    }

    public static boolean isViewWithTagNotDisplayed(String tag) {
        onView(withTagValue(is(tag))).check(doesNotExist());
        return true;
    }

    /**
     * Checks if view contains element with passed in id.
     *
     * @param id
     * @return
     */
    public static boolean isViewWithIdDisplayed(int id) {
        onView(withId(id)).check(matches(isDisplayed()));
        return true;
    }

    // https://stackoverflow.com/questions/23381459/how-to-get-text-from-textview-using-espresso
    public static String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view; //Save, because of check in
                // getConstraints()
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }


}
