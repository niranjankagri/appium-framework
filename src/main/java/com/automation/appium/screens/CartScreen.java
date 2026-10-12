package com.automation.appium.screens;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;

/**
 * "My Cart": the chosen products, the item count, the total and checkout.
 * An empty cart shows a different layout without these elements.
 */
public class CartScreen extends BaseScreen {

	private static final By ITEM_NAMES = id("titleTV");
	private static final By ITEM_COUNT = id("itemsTV");
	private static final By TOTAL = id("totalPriceTV");
	private static final By REMOVE = id("removeBt");
	private static final By CHECKOUT = id("cartBt");

	/**
	 * @return Header the top bar.
	 */
	public Header header() {
		return new Header();
	}

	/**
	 * @return List the product names in the cart.
	 */
	public List<String> itemNames() {
		visible(ITEM_NAMES);
		return textsOf(ITEM_NAMES);
	}

	/**
	 * @return int the item count from the "3 Items" label.
	 */
	public int itemCount() {
		return Integer.parseInt(textOf(ITEM_COUNT).replaceAll("\\D", ""));
	}

	/**
	 * @return BigDecimal the cart total (without delivery).
	 */
	public BigDecimal total() {
		return Prices.parse(textOf(TOTAL));
	}

	/**
	 * @return boolean true when the checkout button is shown (cart has items).
	 */
	public boolean hasCheckout() {
		return isPresent(CHECKOUT);
	}

	/**
	 * Removes the first product in the list.
	 *
	 * @return CartScreen this screen.
	 */
	public CartScreen removeFirstItem() {
		tap(REMOVE);
		return this;
	}

	/**
	 * Use when nobody is logged in: the app asks for a login first.
	 *
	 * @return LoginScreen the login form.
	 */
	public LoginScreen checkoutAsGuest() {
		tap(CHECKOUT);
		return new LoginScreen();
	}

	/**
	 * Use when a user is logged in: the app goes straight to the address form.
	 *
	 * @return AddressScreen the shipping address form.
	 */
	public AddressScreen checkout() {
		tap(CHECKOUT);
		return new AddressScreen();
	}
}
