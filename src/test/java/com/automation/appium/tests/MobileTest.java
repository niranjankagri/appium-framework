package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Duration;

import org.openqa.selenium.ScreenOrientation;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.Device;
import com.automation.appium.screens.LoginScreen;
import com.automation.appium.screens.ProductScreen;

/**
 * Behaviour that only exists on a phone: the back button, switching apps,
 * cold starts, rotation and the on-screen keyboard.
 */
public class MobileTest extends BaseTest {

	@Test(groups = "mobile", description = "The Android back button returns from a product to the catalog")
	public void backButtonReturnsToCatalog() {
		new CatalogScreen().openProduct(TestData.BACKPACK);

		new Device().back();

		// The constructor waits for the catalog title
		assertEquals(new CatalogScreen().title(), "Products");
	}

	@Test(groups = { "smoke", "mobile" }, description = "Screen and cart survive the app going to the background")
	public void stateSurvivesBackground() {
		new CatalogScreen().openProduct(TestData.BACKPACK).addToCart();

		// Like pressing Home, waiting, and switching back
		new Device().sendToBackground(Duration.ofSeconds(3));

		ProductScreen product = new ProductScreen();
		assertEquals(product.name(), TestData.BACKPACK, "still on the same product");
		assertEquals(product.header().cartCount(), 1, "cart badge");
	}

	@Test(groups = "mobile", description = "A cold start opens on the catalog")
	public void coldStartOpensCatalog() {
		new CatalogScreen().openProduct(TestData.BACKPACK);

		new Device().restartApp();

		assertEquals(new CatalogScreen().title(), "Products");
	}

	@Test(groups = "mobile", description = "The app stays in portrait: the main screen is locked to it")
	public void screenStaysPortrait() {
		new CatalogScreen();
		Device device = new Device();

		// The app's manifest sets screenOrientation=portrait, so the device must refuse
		assertFalse(device.tryLandscape(), "rotation to landscape was accepted");
		assertEquals(device.orientation(), ScreenOrientation.PORTRAIT);
		// And the screen still works afterwards
		assertEquals(new CatalogScreen().title(), "Products");
	}

	@Test(groups = "mobile", description = "The keyboard opens for a text field and can be closed")
	public void keyboardOpensAndCloses() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn().focusUsername();
		Device device = new Device();

		assertTrue(device.waitForKeyboard(true), "keyboard opens on the user name field");

		device.hideKeyboard();

		assertTrue(device.waitForKeyboard(false), "keyboard closes");
		assertTrue(login.isLoginButtonVisible(), "Login button visible after closing the keyboard");
	}

	/**
	 * Puts the device back to portrait whatever happened, so a failed rotation
	 * test cannot leave the next test sideways.
	 */
	@AfterMethod(alwaysRun = true)
	public void backToPortrait() {
		new Device().portrait();
	}
}
