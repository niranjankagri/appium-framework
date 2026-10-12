package com.automation.appium.screens;

import org.openqa.selenium.By;

/**
 * The navigation drawer opened from the header. Its entries have no
 * resource ids, so they are found by their text.
 */
public class SideMenu extends BaseScreen {

	// Confirmation dialog shown by "Log Out" (standard Android alert buttons)
	private static final By CONFIRM_LOGOUT = By.id("android:id/button1");

	/**
	 * @return CatalogScreen the product list.
	 */
	public CatalogScreen catalog() {
		tap(text("Catalog"));
		return new CatalogScreen();
	}

	/**
	 * @return LoginScreen the login form (the entry shows only while logged out).
	 */
	public LoginScreen logIn() {
		tap(text("Log In"));
		return new LoginScreen();
	}

	/**
	 * Logs out and confirms the dialog; the app then shows the login form.
	 *
	 * @return LoginScreen the login form.
	 */
	public LoginScreen logOut() {
		tap(text("Log Out"));
		tap(CONFIRM_LOGOUT);
		return new LoginScreen();
	}

	/**
	 * @param entry     the menu text, e.g. "Log In".
	 * @return boolean  true when that entry is in the menu.
	 */
	public boolean hasEntry(String entry) {
		visible(id("menuRV"));
		return isPresent(text(entry));
	}
}
