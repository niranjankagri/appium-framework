package com.automation.appium.data;

import java.math.BigDecimal;

import com.automation.appium.model.Address;
import com.automation.appium.model.Card;

/**
 * Test data in one place. The users are the demo accounts listed on the app's
 * own login screen; the card is the app's placeholder number (nothing is charged).
 */
public final class TestData {

	// Constants only
	private TestData() {
	}

	// Demo user that can log in. Sic: the app spells it "bod", not "bob"
	public static final String USER = "bod@example.com";
	public static final String PASSWORD = "10203040";
	// Demo user the app always refuses
	public static final String LOCKED_USER = "alice@example.com";

	public static final String BACKPACK = "Sauce Labs Backpack";
	// Sixth product: below the first screen, so opening it needs a scroll
	public static final String YELLOW_BACKPACK = "Sauce Labs Backpack (yellow)";
	// Seventh product: opening it crashes app 2.3.0 (see KnownBugTest)
	public static final String BIKE_LIGHT = "Sauce Labs Bike Light";

	// Flat delivery fee the review screen adds to the cart total
	public static final BigDecimal DELIVERY = new BigDecimal("5.99");

	public static final Address ADDRESS = new Address("Niranjan Agri", "Main Street 1", "", "Berlin", "",
			"10115", "Germany");
	public static final Card CARD = new Card("Niranjan Agri", "3258125675687891", "0330", "123");
}
