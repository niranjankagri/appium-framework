package com.automation.appium.model;

/**
 * A shipping address as entered on the checkout form.
 *
 * @param fullName  required.
 * @param line1     required.
 * @param line2     optional, may be empty.
 * @param city      required.
 * @param state     optional, may be empty.
 * @param zip       required.
 * @param country   required.
 */
public record Address(String fullName, String line1, String line2, String city, String state, String zip,
		String country) {
}
