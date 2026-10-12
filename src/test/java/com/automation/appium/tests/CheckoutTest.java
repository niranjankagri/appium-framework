package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.model.Address;
import com.automation.appium.screens.AddressScreen;
import com.automation.appium.screens.CartScreen;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.CompleteScreen;
import com.automation.appium.screens.ReviewScreen;

/**
 * The checkout flow: login on the way, address, payment, review, confirmation.
 */
public class CheckoutTest extends BaseTest {

	@Test(groups = { "smoke", "checkout" }, description = "A guest logs in during checkout and completes the order")
	public void guestCompletesCheckout() {
		CartScreen cart = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart();
		BigDecimal cartTotal = cart.total();

		// Nobody is logged in after the reset, so checkout asks for a login first
		ReviewScreen review = cart.checkoutAsGuest()
				.loginToCheckout(TestData.USER, TestData.PASSWORD)
				.fill(TestData.ADDRESS)
				.toPayment()
				.fill(TestData.CARD)
				.reviewOrder();

		assertEquals(review.itemNames(), List.of(TestData.BACKPACK));
		assertEquals(review.shipToName(), TestData.ADDRESS.fullName());
		assertEquals(review.itemCount(), 1, "items");
		// The review total is the cart total plus the flat delivery fee
		assertEquals(review.total(), cartTotal.add(TestData.DELIVERY), "order total");

		CompleteScreen complete = review.placeOrder();
		assertEquals(complete.title(), "Checkout Complete");
		assertEquals(complete.thankYou(), "Thank you for your order");

		// "Continue Shopping" returns to an emptied cart
		assertEquals(complete.continueShopping().header().cartCount(), 0, "cart badge after the order");
	}

	@Test(groups = "checkout", description = "The address form names every missing required field")
	public void addressRequiresAllMandatoryFields() {
		AddressScreen address = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart()
				.checkoutAsGuest()
				.loginToCheckout(TestData.USER, TestData.PASSWORD);

		List<String> errors = address.submitExpectingErrors().errors();

		// One message per required field, in form order
		assertEquals(errors.size(), 5, "error messages: " + errors);
		assertEquals(errors.subList(0, 4), List.of(
				"Please provide your full name.",
				"Please provide your address.",
				"Please provide your city.",
				"Please provide your zip"));
		// The country message's wording is a known bug (see KnownBugTest), so only its presence is checked here
		assertTrue(errors.get(4).startsWith("Please provide your"), "country message: " + errors.get(4));
	}

	@Test(groups = "checkout", description = "The optional address fields can stay empty")
	public void optionalAddressFieldsCanBeEmpty() {
		// Line 2 and state are optional; the test data leaves them empty on purpose
		Address address = TestData.ADDRESS;
		assertEquals(address.line2(), "");
		assertEquals(address.state(), "");

		new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart()
				.checkoutAsGuest()
				.loginToCheckout(TestData.USER, TestData.PASSWORD)
				.fill(address)
				// toPayment() waits for the payment form, so reaching it is the check
				.toPayment();
	}
}
