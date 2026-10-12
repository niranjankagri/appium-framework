package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;

import io.appium.java_client.AppiumBy;

/**
 * First check that the whole chain works: Appium server, emulator, app
 * install and launch. Raw locators here on purpose; page objects come next.
 */
public class SmokeTest extends BaseTest {

	@Test(groups = "smoke", description = "The app opens on the product catalog")
	public void appOpensOnCatalog() {
		// Wait for the catalog title: the splash screen comes first
		WebElement title = new WebDriverWait(driver(), Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfElementLocated(
						AppiumBy.id("com.saucelabs.mydemoapp.android:id/productTV")));
		assertEquals(title.getText(), "Products");

		// The catalog shows at least one product card
		List<WebElement> names = driver().findElements(AppiumBy.id("com.saucelabs.mydemoapp.android:id/titleTV"));
		assertFalse(names.isEmpty(), "No products on the catalog screen");
	}
}
