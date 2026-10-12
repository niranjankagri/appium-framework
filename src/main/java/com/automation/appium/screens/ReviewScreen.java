package com.automation.appium.screens;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;

/**
 * Checkout step 3: the order summary with products, address and total.
 */
public class ReviewScreen extends BaseScreen {

	private static final By ITEM_NAMES = id("titleTV");
	private static final By FULL_NAME = id("fullNameTV");
	private static final By ITEM_COUNT = id("itemNumberTV");
	// Total including delivery
	private static final By TOTAL = id("totalAmountTV");
	private static final By PLACE_ORDER = id("paymentBtn");

	/**
	 * Waits for the "Place Order" button, so the summary is shown.
	 */
	public ReviewScreen() {
		visible(PLACE_ORDER);
	}

	/**
	 * @return List the product names in the order.
	 */
	public List<String> itemNames() {
		return textsOf(ITEM_NAMES);
	}

	/**
	 * @return String the name the order ships to.
	 */
	public String shipToName() {
		return textOf(FULL_NAME);
	}

	/**
	 * @return int the item count from the "2 Items" label.
	 */
	public int itemCount() {
		return Integer.parseInt(textOf(ITEM_COUNT).replaceAll("\\D", ""));
	}

	/**
	 * @return BigDecimal the order total including delivery.
	 */
	public BigDecimal total() {
		return Prices.parse(textOf(TOTAL));
	}

	/**
	 * @return CompleteScreen the confirmation.
	 */
	public CompleteScreen placeOrder() {
		tap(PLACE_ORDER);
		return new CompleteScreen();
	}
}
