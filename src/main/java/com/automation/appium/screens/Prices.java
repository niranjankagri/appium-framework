package com.automation.appium.screens;

import java.math.BigDecimal;

/**
 * Turns the app's price texts ("$ 29.99") into numbers. BigDecimal, not double:
 * as doubles, 49.99 + 5.99 is 55.980000000000004, not 55.98.
 */
public final class Prices {

	// Static helpers only
	private Prices() {
	}

	/**
	 * @param text          a price as shown in the app, e.g. "$ 29.99".
	 * @return BigDecimal   the amount, e.g. 29.99.
	 */
	public static BigDecimal parse(String text) {
		return new BigDecimal(text.replace("$", "").replace(",", "").trim());
	}
}
