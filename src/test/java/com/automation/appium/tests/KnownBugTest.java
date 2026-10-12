package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.AddressScreen;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.ProductScreen;

/**
 * Defects found in the app under test. Each test describes the correct
 * behaviour, so it fails while the bug exists and passes once it is fixed.
 * The group is excluded from testng.xml to keep the regular run green;
 * run them with {@code mvn test -DsuiteXmlFile=known-bugs.xml}.
 */
public class KnownBugTest extends BaseTest {

	/**
	 * Found 2026-10-12 on My Demo App 2.3.0 (Android 15 emulator).
	 * Tapping the 7th or a later product closes the app; logcat shows
	 * {@code ArrayIndexOutOfBoundsException: length=6; index=6} in
	 * {@code ProductCatalogFragment.java:156}. Products 1-6 open normally.
	 */
	@Test(groups = "known-bug", description = "BUG: opening the 7th product (Bike Light) crashes the app")
	public void seventhProductOpens() {
		ProductScreen product = new CatalogScreen().openProduct(TestData.BIKE_LIGHT);

		assertEquals(product.name(), TestData.BIKE_LIGHT);
	}

	/**
	 * Found 2026-10-12 on My Demo App 2.3.0. On the product screen the minus
	 * button lowers the quantity from 1 to 0, so "Add to cart" can be tapped
	 * with nothing to add. The quantity should stop at 1.
	 */
	@Test(groups = "known-bug", description = "BUG: the product quantity can be lowered to 0")
	public void quantityStopsAtOne() {
		ProductScreen product = new CatalogScreen().openProduct(TestData.BACKPACK).decreaseQuantity();

		assertEquals(product.quantity(), 1, "quantity after tapping minus at 1");
	}

	/**
	 * Found 2026-10-12 on My Demo App 2.3.0. Submitting the address form
	 * without a country shows "Please provide your" with the last word
	 * missing; the other required fields have complete messages.
	 */
	@Test(groups = "known-bug", description = "BUG: the missing-country message is cut off")
	public void countryMessageIsComplete() {
		AddressScreen address = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart()
				.checkoutAsGuest()
				.loginToCheckout(TestData.USER, TestData.PASSWORD);

		List<String> errors = address.submitExpectingErrors().errors();

		assertEquals(errors.get(errors.size() - 1), "Please provide your country.");
	}
}
