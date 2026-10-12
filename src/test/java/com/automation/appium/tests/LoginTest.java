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

	@Test(groups = { "smoke", "login" }, description = "A valid user logs in and the menu offers Log Out")
	public void validUserLogsIn() {
		CatalogScreen catalog = new CatalogScreen().header().openMenu().logIn()
				.loginToCatalog(TestData.USER, TestData.PASSWORD);

		// Logged in: the menu now offers Log Out instead of Log In
		SideMenu menu = catalog.header().openMenu();
		assertTrue(menu.hasEntry("Log Out"), "Log Out entry after login");
	}

	@Test(groups = "login", description = "A locked-out user is refused with a clear message")
	public void lockedUserIsRefused() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				.submit(TestData.LOCKED_USER, TestData.PASSWORD);

		assertEquals(login.passwordError(), "Sorry this user has been locked out.");
	}

	@Test(groups = "login", description = "Login without a user name asks for one")
	public void usernameIsRequired() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				.submit("", TestData.PASSWORD);

		assertEquals(login.usernameError(), "Username is required");
	}

	@Test(groups = "login", description = "Login without a password asks for one")
	public void passwordIsRequired() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn()
				.submit(TestData.USER, "");

		assertEquals(login.passwordError(), "Enter Password");
	}

	@Test(groups = "login", description = "Logging out returns to the login form and the menu offers Log In")
	public void userLogsOut() {
		CatalogScreen catalog = new CatalogScreen().header().openMenu().logIn()
				.loginToCatalog(TestData.USER, TestData.PASSWORD);

		// Log out confirms a dialog and lands on the login form
		LoginScreen login = catalog.header().openMenu().logOut();

		assertTrue(login.header().openMenu().hasEntry("Log In"), "Log In entry after logout");
	}
}
