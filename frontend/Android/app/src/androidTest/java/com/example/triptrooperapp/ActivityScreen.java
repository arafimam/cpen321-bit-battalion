package com.example.triptrooperapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

public class ActivityScreen {

    public static void navigateToActivityScreen() {
        TestFramework.clickWithId(R.id.nav_activity);
        // wait for some time for the activity screen to load.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    public static void clickOnPlacesNearby() {
        TestFramework.clickWithId(R.id.activity_location);
    }

    public static void clickOnPlacesByDestination() {
        TestFramework.clickWithId(R.id.activity_destination);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    public static void enterDestination(String destinationName) {
        TestFramework.clickWithId(R.id.list_name_textField);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
        TestFramework.setTextToTextFieldWithId(R.id.list_name_textField,
                destinationName);
        TestFramework.clickWithId(R.id.create_list_button);
        onView(isRoot()).perform(TestFramework.waitIdlingResource(2000));
    }

    public static void denyLocationPermissionFirstTime() {
        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiSelector selector =
                new UiSelector().textContains("DENY");
        UiObject object = device.findObject(selector);

        try {
            if (object.exists()) {
                object.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void denyLocationPermissionMoreThanOneTime() {
        TestFramework.clickViewWithText("Cancel");
    }

    public static void acceptLocationFirstTime() {
        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        UiSelector selector =
                new UiSelector().textContains("ALLOW ONLY WHILE USING THE APP");
        UiObject object = device.findObject(selector);

        try {
            if (object.exists()) {
                object.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void acceptLocationSecondTime() {
        TestFramework.clickViewWithText("Confirm");
        acceptLocationFirstTime();
    }

}
