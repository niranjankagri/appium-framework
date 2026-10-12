package com.automation.appium.screens;

import org.openqa.selenium.By;

/**
 * Checkout done: the confirmation message and "Continue Shopping".
 */
public class CompleteScreen extends BaseScreen {

	private static final By TITLE = id("completeTV");
	private static final By THANK_YOU = id("thankYouTV");
	// Sic: the app's own id has this spelling
	private static final By CONTINUE_SHOPPING = id("shoopingBt");

	/**
	 * Waits for the confirmation title.
	 */
	public CompleteScreen() {
		visible(TITLE);
	}

	/**
	 * @return String "Checkout Complete".
	 */
	public String title() {
		return textOf(TITLE);
	}

	/**
	 * @return String "Thank you for your order".
	 */
	public String thankYou() {
		return textOf(THANK_YOU);
	}

	/**
	 * @return CatalogScreen the product list.
	 */
	public CatalogScreen continueShopping() {
		tap(CONTINUE_SHOPPING);
		return new CatalogScreen();
	}
}
