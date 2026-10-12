package com.automation.appium.model;

/**
 * Payment card data for the demo app's checkout. Test values only: the app
 * charges nothing and accepts its own placeholder numbers.
 *
 * @param holder        name on the card.
 * @param number        digits only; the app adds the spaces.
 * @param expiry        MMYY; the app adds the slash.
 * @param securityCode  three digits.
 */
public record Card(String holder, String number, String expiry, String securityCode) {
}
