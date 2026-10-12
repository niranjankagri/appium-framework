package com.automation.appium.screens;

import java.math.BigDecimal;

import org.openqa.selenium.By;

/**
 * Detail screen of one product: name, price, quantity and "Add to cart".
 */
public class ProductScreen extends BaseScreen {

	private static final By NAME = id("productTV");
	private static final By PRICE = id("priceTV");
	private static final By PLUS = id("plusIV");
	private static final By MINUS = id("minusIV");
	private static final By QUANTITY = id("noTV");
	private static final By ADD_TO_CART = id("cartBt");

	/**
	 * Waits for the "Add to cart" button, so the screen is fully shown.
	 */
	public ProductScreen() {
		visible(ADD_TO_CART);
	}

	/**
	 * @return Header the top bar.
	 */
	public Header header() {
		return new Header();
	}

	/**
	 * @return String the product name.
	 */
	public String name() {
		return textOf(NAME);
	}

	/**
	 * @return BigDecimal the price of one item.
	 */
	public BigDecimal price() {
		return Prices.parse(textOf(PRICE));
	}

	/**
	 * @return int the quantity that "Add to cart" will add.
	 */
	public int quantity() {
		return Integer.parseInt(textOf(QUANTITY));
	}

	/**
	 * @param times     how often to tap "+".
	 * @return ProductScreen this screen.
	 */
	public ProductScreen increaseQuantity(int times) {
		for (int i = 0; i < times; i++) {
			tap(PLUS);
		}
		return this;
	}

	/**
	 * @return ProductScreen this screen, with the quantity one lower.
	 */
	public ProductScreen decreaseQuantity() {
		tap(MINUS);
		return this;
	}

	/**
	 * @return ProductScreen this screen; the cart badge updates.
	 */
	public ProductScreen addToCart() {
		tap(ADD_TO_CART);
		return this;
	}
}
