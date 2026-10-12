package com.automation.appium.screens;

import org.openqa.selenium.By;

/**
 * The bar at the top of every screen: menu button, cart icon and the badge
 * with the number of items in the cart. A component, not a screen: screens
 * expose it through {@code header()}.
 */
public class Header extends BaseScreen {

	private static final By MENU = id("menuIV");
	private static final By CART = id("cartRL");
	// The badge only exists while the cart has items
	private static final By CART_BADGE = id("cartTV");

	/**
	 * @return SideMenu the opened navigation drawer.
	 */
	public SideMenu openMenu() {
		tap(MENU);
		return new SideMenu();
	}

	/**
	 * @return CartScreen the cart.
	 */
	public CartScreen openCart() {
		tap(CART);
		return new CartScreen();
	}

	/**
	 * @return int the number on the cart badge, 0 when there is no badge.
	 */
	public int cartCount() {
		return isPresent(CART_BADGE) ? Integer.parseInt(textOf(CART_BADGE)) : 0;
	}
}
