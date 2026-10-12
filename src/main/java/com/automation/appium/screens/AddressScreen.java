package com.automation.appium.screens;

import java.util.List;

import org.openqa.selenium.By;

import com.automation.appium.model.Address;

import io.appium.java_client.AppiumBy;

/**
 * Checkout step 1: the shipping address. Empty fields show hint texts
 * ("Rebecca Winter"), which UiAutomator reports as the field's text.
 */
public class AddressScreen extends BaseScreen {

	private static final By FULL_NAME = id("fullNameET");
	private static final By LINE1 = id("address1ET");
	private static final By LINE2 = id("address2ET");
	private static final By CITY = id("cityET");
	private static final By STATE = id("stateET");
	private static final By ZIP = id("zipET");
	private static final By COUNTRY = id("countryET");
	private static final By TO_PAYMENT = id("paymentBtn");
	// Each required field has its own message view: fullNameErrorTV, cityErrorTV, ...
	private static final By ERRORS = AppiumBy
			.androidUIAutomator("new UiSelector().resourceIdMatches(\".*ErrorTV\")");

	/**
	 * Waits for the first field, so the form is shown.
	 */
	public AddressScreen() {
		visible(FULL_NAME);
	}

	/**
	 * Types every non-empty value of the address.
	 *
	 * @param address   the address to enter.
	 * @return AddressScreen this screen.
	 */
	public AddressScreen fill(Address address) {
		typeIfGiven(FULL_NAME, address.fullName());
		typeIfGiven(LINE1, address.line1());
		typeIfGiven(LINE2, address.line2());
		typeIfGiven(CITY, address.city());
		typeIfGiven(STATE, address.state());
		typeIfGiven(ZIP, address.zip());
		typeIfGiven(COUNTRY, address.country());
		return this;
	}

	/**
	 * @return PaymentScreen the payment form (only when the address is valid).
	 */
	public PaymentScreen toPayment() {
		tap(TO_PAYMENT);
		return new PaymentScreen();
	}

	/**
	 * Taps "To Payment" expecting the form to refuse.
	 *
	 * @return AddressScreen this screen with its error messages.
	 */
	public AddressScreen submitExpectingErrors() {
		tap(TO_PAYMENT);
		return this;
	}

	/**
	 * @return List the validation messages currently shown.
	 */
	public List<String> errors() {
		visible(ERRORS);
		return textsOf(ERRORS);
	}

	// Empty values mean "leave the field alone", so tests can model missing input
	private void typeIfGiven(By field, String value) {
		if (value != null && !value.isEmpty()) {
			type(field, value);
		}
	}
}
