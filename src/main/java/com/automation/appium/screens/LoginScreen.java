package com.automation.appium.screens;

import org.openqa.selenium.By;

/**
 * Login form. Opened from the side menu, or by checkout when nobody is logged in.
 */
public class LoginScreen extends BaseScreen {

	private static final By USERNAME = id("nameET");
	private static final By PASSWORD = id("passwordET");
	private static final By LOGIN = id("loginBtn");
	private static final By USERNAME_ERROR = id("nameErrorTV");
	// The app shows both "Enter Password" and the locked-out message here
	private static final By PASSWORD_ERROR = id("passwordErrorTV");

	/**
	 * Waits for the login button, so the form is shown.
	 */
	public LoginScreen() {
		visible(LOGIN);
	}

	/**
	 * @return Header the top bar.
	 */
	public Header header() {
		return new Header();
	}

	/**
	 * Taps into the user name field, which opens the on-screen keyboard.
	 *
	 * @return LoginScreen this screen.
	 */
	public LoginScreen focusUsername() {
		tap(USERNAME);
		return this;
	}

	/**
	 * @return boolean true when the Login button is on screen (not hidden by the keyboard).
	 */
	public boolean isLoginButtonVisible() {
		return isPresent(LOGIN);
	}

	/**
	 * Fills both fields and taps Login. The caller decides which screen comes next,
	 * because that depends on how the login was reached.
	 *
	 * @param username  the user name (e-mail).
	 * @param password  the password.
	 * @return LoginScreen this screen, for checking errors after a failed login.
	 */
	public LoginScreen submit(String username, String password) {
		type(USERNAME, username);
		type(PASSWORD, password);
		tap(LOGIN);
		return this;
	}

	/**
	 * Login reached from the side menu: the app returns to the catalog.
	 *
	 * @param username  the user name.
	 * @param password  the password.
	 * @return CatalogScreen the product list.
	 */
	public CatalogScreen loginToCatalog(String username, String password) {
		submit(username, password);
		return new CatalogScreen();
	}

	/**
	 * Login reached from checkout: the app continues to the address form.
	 *
	 * @param username  the user name.
	 * @param password  the password.
	 * @return AddressScreen the shipping address form.
	 */
	public AddressScreen loginToCheckout(String username, String password) {
		submit(username, password);
		return new AddressScreen();
	}

	/**
	 * @return String the message under the user name field.
	 */
	public String usernameError() {
		return textOf(USERNAME_ERROR);
	}

	/**
	 * @return String the message under the password field.
	 */
	public String passwordError() {
		return textOf(PASSWORD_ERROR);
	}
}
