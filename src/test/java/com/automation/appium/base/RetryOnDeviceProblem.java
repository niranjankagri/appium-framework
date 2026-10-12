package com.automation.appium.base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.automation.appium.driver.DriverManager;
import com.automation.appium.report.Steps;

import io.appium.java_client.android.AndroidDriver;

/**
 * Retries a failed test once, but only when the device itself was the problem:
 * Android is showing its "isn't responding" (ANR) dialog. That happens on a
 * loaded emulator and says nothing about the app's logic or the test. Ordinary
 * failures, such as a wrong value in an assertion, are never retried.
 * <p>
 * The first attempt stays in the report as skipped, with a step saying why,
 * so a retry is always visible.
 */
public class RetryOnDeviceProblem implements IRetryAnalyzer {

	// Android's ANR dialog, e.g. "My Demo App isn't responding"
	private static final String ANR_TEXT = "isn't responding";
	// The dialog's "Close app" button
	private static final By CLOSE_APP = By.id("android:id/aerr_close");

	// One analyzer instance per test method, so this counts per test
	private boolean retried;

	/**
	 * @param result    the failed attempt.
	 * @return boolean  true to run the test again.
	 */
	@Override
	public boolean retry(ITestResult result) {
		if (retried || !DriverManager.hasDriver() || !anrDialogShown()) {
			return false;
		}
		retried = true;
		Steps.log("Device problem: Android showed \"app isn't responding\"; closing it and retrying the test once");
		closeAnrDialog();
		return true;
	}

	// Looks for the dialog in the current screen; any driver error means "no"
	private static boolean anrDialogShown() {
		try {
			return DriverManager.get().getPageSource().contains(ANR_TEXT);
		} catch (WebDriverException e) {
			return false;
		}
	}

	// Taps "Close app"; the next attempt resets and restarts the app anyway
	private static void closeAnrDialog() {
		AndroidDriver driver = DriverManager.get();
		try {
			driver.findElements(CLOSE_APP).stream().findFirst().ifPresent(button -> button.click());
		} catch (WebDriverException e) {
			// The dialog may already be gone
		}
	}
}
