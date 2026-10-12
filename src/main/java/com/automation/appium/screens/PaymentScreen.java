package com.automation.appium.screens;

import org.openqa.selenium.By;

import com.automation.appium.model.Card;

/**
 * Checkout step 2: card details. Billing address = shipping address is ticked by default.
 */
public class PaymentScreen extends BaseScreen {

	private static final By HOLDER = id("nameET");
	private static final By NUMBER = id("cardNumberET");
	private static final By EXPIRY = id("expirationDateET");
	private static final By SECURITY_CODE = id("securityCodeET");
	private static final By REVIEW_ORDER = id("paymentBtn");

	/**
	 * Waits for the card number field, so the form is shown.
	 */
	public PaymentScreen() {
		visible(NUMBER);
	}

	/**
	 * @param card      the card to enter.
	 * @return PaymentScreen this screen.
	 */
	public PaymentScreen fill(Card card) {
		type(HOLDER, card.holder());
		type(NUMBER, card.number());
		type(EXPIRY, card.expiry());
		type(SECURITY_CODE, card.securityCode());
		return this;
	}

	/**
	 * @return ReviewScreen the order summary.
	 */
	public ReviewScreen reviewOrder() {
		tap(REVIEW_ORDER);
		return new ReviewScreen();
	}
}
