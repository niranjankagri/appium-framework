package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.screens.CatalogScreen;

/**
 * First check that the whole chain works: Appium server, emulator, app
 * install and launch.
 */
public class SmokeTest extends BaseTest {

	@Test(groups = "smoke", description = "The app opens on the product catalog")
	public void appOpensOnCatalog() {
		// The constructor waits until the splash screen has handed over
		CatalogScreen catalog = new CatalogScreen();

		assertEquals(catalog.title(), "Products");
		assertFalse(catalog.visibleProductNames().isEmpty(), "No products on the catalog screen");
		// A freshly reset app has an empty cart
		assertEquals(catalog.header().cartCount(), 0, "cart badge");
	}
}
