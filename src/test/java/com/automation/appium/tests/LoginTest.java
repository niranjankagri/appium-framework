package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.LoginScreen;
import com.automation.appium.screens.SideMenu;

/**
 * Login and logout from the side menu.
 */
public class LoginTest extends BaseTest {

	@Test(groups = { "smoke", "login" }, description = "[TC-102] A valid user logs in and the menu offers Log Out")
	public void tc102_validUserLogsIn() {
		// Menu > Log In; a successful login returns to the catalog
		CatalogScreen catalog = new CatalogScreen().header().openMenu().logIn()
				.loginToCatalog(TestData.USER, TestData.PASSWORD);

		// Logged in: the menu now offers Log Out instead of Log In
		SideMenu menu = catalog.header().openMenu();
		assertTrue(menu.hasEntry("Log Out"), "Log Out entry after login");
	}

	@Test(groups = "login", description = "[TC-103] A locked-out user is refused with a clear message")
	public void tc103_lockedUserIsRefused() {
		// submit() stays on the login form, so the error can be read
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				.submit(TestData.LOCKED_USER, TestData.PASSWORD);

		// The app shows this message under the password field, not in a dialog
		assertEquals(login.passwordError(), "Sorry this user has been locked out.");
	}

	@Test(groups = "login", description = "[TC-104] Login without a user name asks for one")
	public void tc104_usernameIsRequired() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				// Empty user name, valid password
				.submit("", TestData.PASSWORD);

		assertEquals(login.usernameError(), "Username is required");
	}

	@Test(groups = "login", description = "[TC-105] Login without a password asks for one")
	public void tc105_passwordIsRequired() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				// Valid user name, empty password
				.submit(TestData.USER, "");

		assertEquals(login.passwordError(), "Enter Password");
	}

	@Test(groups = "login", description = "[TC-106] Logging out returns to the login form and the menu offers Log In")
	public void tc106_userLogsOut() {
		// Log in first: after the app reset nobody is logged in
		CatalogScreen catalog = new CatalogScreen().header().openMenu().logIn()
				.loginToCatalog(TestData.USER, TestData.PASSWORD);

		// Log out confirms a dialog and lands on the login form
		LoginScreen login = catalog.header().openMenu().logOut();

		// Logged out: the menu offers Log In again
		assertTrue(login.header().openMenu().hasEntry("Log In"), "Log In entry after logout");
	}
}
