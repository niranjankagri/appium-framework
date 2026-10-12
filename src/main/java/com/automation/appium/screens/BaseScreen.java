package com.automation.appium.screens;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.appium.config.Config;
import com.automation.appium.driver.DriverManager;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

/**
 * Parent of every screen object. Holds the waits and the small set of actions
 * screens need, so no screen talks to the driver without waiting first.
 */
public abstract class BaseScreen {

	// Every resource id in the app starts with the package name
	private static final String ID_PREFIX = "com.saucelabs.mydemoapp.android:id/";

	// Session of the current test thread
	protected final AndroidDriver driver;
	// Waits up to explicitWait seconds (config) for a condition
	protected final WebDriverWait wait;

	protected BaseScreen() {
		this.driver = DriverManager.get();
		this.wait = new WebDriverWait(driver, Config.get().explicitWait());
	}

	/**
	 * @param name      the short resource id as shown in Appium Inspector, e.g. {@code cartBt}.
	 * @return By       the full resource-id locator.
	 */
	protected static By id(String name) {
		return AppiumBy.id(ID_PREFIX + name);
	}

	/**
	 * @param text      the exact text on screen.
	 * @return By       a UiAutomator locator for that text (for items without an id).
	 */
	protected static By text(String text) {
		return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")");
	}

	/**
	 * @param locator   where the element is.
	 * @return WebElement the element once it is visible.
	 */
	protected WebElement visible(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Taps an element once it can be tapped.
	 *
	 * @param locator   where the element is.
	 */
	protected void tap(By locator) {
		wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
	}

	/**
	 * Replaces the text of an input field.
	 *
	 * @param locator   where the field is.
	 * @param value     the text to type.
	 */
	protected void type(By locator, String value) {
		WebElement field = visible(locator);
		field.clear();
		field.sendKeys(value);
	}

	/**
	 * @param locator   where the element is.
	 * @return String   its text once it is visible.
	 */
	protected String textOf(By locator) {
		return visible(locator).getText();
	}

	/**
	 * Checks without waiting: for things that may legitimately be absent.
	 *
	 * @param locator   where the element would be.
	 * @return boolean  true when at least one matching element is on screen now.
	 */
	protected boolean isPresent(By locator) {
		return !driver.findElements(locator).isEmpty();
	}

	/**
	 * @param locator   where the elements are.
	 * @return List     the texts of every matching element currently on screen.
	 */
	protected List<String> textsOf(By locator) {
		return driver.findElements(locator).stream().map(WebElement::getText).toList();
	}

	/**
	 * Scrolls the screen's scrollable view until the text is visible. Android only
	 * renders what is on screen, so elements further down are not found before this.
	 *
	 * @param text      the exact text to bring into view.
	 * @return WebElement the element with that text.
	 */
	protected WebElement scrollToText(String text) {
		return driver.findElement(AppiumBy.androidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true))"
						+ ".scrollIntoView(new UiSelector().text(\"" + text + "\"))"));
	}
}
