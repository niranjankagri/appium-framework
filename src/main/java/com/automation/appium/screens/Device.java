package com.automation.appium.screens;

import java.time.Duration;

import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriverException;

import com.automation.appium.config.Config;
import com.automation.appium.driver.DriverManager;
import com.automation.appium.report.Steps;

import io.appium.java_client.android.AndroidDriver;

/**
 * Things a user does to the phone rather than to a screen: the back button,
 * switching apps, restarting the app, rotating, the on-screen keyboard.
 * Kept out of the screen objects because they work the same on every screen.
 */
public final class Device {

	// Session of the current test thread
	private final AndroidDriver driver = DriverManager.get();
	// Package of the app under test
	private final String appPackage = Config.get().value("appPackage");

	/**
	 * Presses the Android back button.
	 */
	public void back() {
		Steps.log("Press the Android back button");
		driver.navigate().back();
	}

	/**
	 * Sends the app to the background (like pressing Home) and brings it back.
	 *
	 * @param time      how long the app stays in the background.
	 */
	public void sendToBackground(Duration time) {
		Steps.log("Send the app to the background for \"" + time.toSeconds() + " s\"");
		driver.runAppInBackground(time);
	}

	/**
	 * Closes the app completely and starts it again (a cold start), keeping its data.
	 */
	public void restartApp() {
		Steps.log("Close the app and start it again");
		driver.terminateApp(appPackage);
		driver.activateApp(appPackage);
	}

	/**
	 * Asks the device to turn to landscape.
	 *
	 * @return boolean false when the device refused, e.g. because the app is locked to portrait.
	 */
	public boolean tryLandscape() {
		Steps.log("Rotate the device to \"landscape\"");
		try {
			driver.rotate(ScreenOrientation.LANDSCAPE);
			return true;
		} catch (WebDriverException e) {
			// UiAutomator2: "Screen rotation cannot be changed ... Is it locked programmatically?"
			return false;
		}
	}

	/**
	 * Turns back to portrait, so the next test starts the usual way round.
	 */
	public void portrait() {
		if (orientation() != ScreenOrientation.PORTRAIT) {
			driver.rotate(ScreenOrientation.PORTRAIT);
		}
	}

	/**
	 * @return ScreenOrientation the current orientation.
	 */
	public ScreenOrientation orientation() {
		return driver.getOrientation();
	}

	/**
	 * @return boolean true while the on-screen keyboard is open.
	 */
	public boolean isKeyboardShown() {
		return driver.isKeyboardShown();
	}

	/**
	 * Closes the on-screen keyboard, e.g. when it covers a button.
	 */
	public void hideKeyboard() {
		Steps.log("Hide the keyboard");
		driver.hideKeyboard();
	}

	/**
	 * Waits up to the configured time for the keyboard to open or close.
	 *
	 * @param shown     the expected state.
	 * @return boolean  true when the keyboard reached that state in time.
	 */
	public boolean waitForKeyboard(boolean shown) {
		long end = System.nanoTime() + Config.get().explicitWait().toNanos();
		while (System.nanoTime() < end) {
			if (isKeyboardShown() == shown) {
				return true;
			}
			// Short poll: keyboard animations take a few hundred milliseconds
			pause(Duration.ofMillis(250));
		}
		return false;
	}

	// Thread.sleep wrapped for the polling loop above
	private static void pause(Duration time) {
		try {
			Thread.sleep(time.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
