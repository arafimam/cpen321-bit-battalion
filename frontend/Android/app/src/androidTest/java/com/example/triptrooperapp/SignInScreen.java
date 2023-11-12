package com.example.triptrooperapp;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

public class SignInScreen {

    private static final String googleAccountType = "@gmail.com";

    /**
     * Signs in the user.
     * Pre-requisite: The device should already have a google account registerd.
     * If any other account type other than @gmail.com is used change the
     * googleAccountType above.
     */
    public static void signInIfNotAlreadySignedIn() {

        // click on the sign in button if it is visible.
        TestFramework.clickWithId(R.id.sign_in_google);

        // using Thread to sleep because we are launching another 3rd party
        // activity.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // using UI automator to control the google account picker.
        UiDevice device =
                UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiSelector selector = new UiSelector().textContains(googleAccountType);
        UiObject object = device.findObject(selector);

        try {
            if (object.exists()) {
                object.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        // wait for 5 seconds.
        onView(isRoot()).perform(TestFramework.waitIdlingResource(5000));
    }

}
