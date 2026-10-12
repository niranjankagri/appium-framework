package com.automation.appium.screens;

import java.math.BigDecimal;

/**
 * Turns the app's price texts ("$ 29.99") into numbers. BigDecimal, not double,
 * so sums like 29.99 + 5.99 compare exactly.
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
