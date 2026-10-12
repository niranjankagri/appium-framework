package com.automation.appium.report;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.testng.Reporter;

/**
 * Records what a test does, step by step, for the HTML report. Screen objects
 * call it on every tap and typed value, so tests need no logging of their own.
 */
public final class Steps {

	// The text inside the first text("...") of a UiAutomator selector
	private static final Pattern UI_TEXT = Pattern.compile("text\\(\\\\?\"(.*?)\\\\?\"\\)");
	// The short name after ":id/" in a resource id
	private static final Pattern RESOURCE_ID = Pattern.compile(":id/(\\w+)");

	// Static helpers only
	private Steps() {
	}

	/**
	 * Adds one step to the current test's result (TestNG's Reporter output).
	 *
	 * @param step      what happened, with values in double quotes so the report highlights them.
	 */
	public static void log(String step) {
		Reporter.log(step);
	}

	/**
	 * @param locator   a locator from a screen object.
	 * @return String   a short readable name: the resource id, the accessibility label or the text.
	 */
	public static String name(By locator) {
		String raw = locator.toString();
		// "new UiSelector().text(\"Log In\")..." -> "Log In"; first, so a product
		// image found next to its name is logged by the name
		Matcher text = UI_TEXT.matcher(raw);
		if (text.find()) {
			return text.group(1);
		}
		// "AppiumBy.id: com.saucelabs.mydemoapp.android:id/cartBt" -> "cartBt"
		Matcher id = RESOURCE_ID.matcher(raw);
		if (id.find()) {
			return id.group(1);
		}
		// "AppiumBy.accessibilityId: View cart" -> "View cart"
		int colon = raw.indexOf(": ");
		return colon >= 0 ? raw.substring(colon + 2) : raw;
	}
}
