package com.automation.appium.screens;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;

/**
 * The product list the app opens on.
 */
public class CatalogScreen extends BaseScreen {

	private static final By TITLE = id("productTV");
	private static final By PRODUCT_NAMES = id("titleTV");
	private static final By PRODUCT_PRICES = id("priceTV");
	private static final By SORT = id("sortIV");

	/** The four choices of the sort dialog, by their accessibility label. */
	public enum Sort {
		NAME_ASC("Ascending order by name"),
		NAME_DESC("Descending order by name"),
		PRICE_ASC("Ascending order by price"),
		PRICE_DESC("Descending order by price");

		private final String label;

		Sort(String label) {
			this.label = label;
		}
	}

	/**
	 * Waits for the catalog title, so the splash screen is gone.
	 */
	public CatalogScreen() {
		visible(TITLE);
	}

	/**
	 * @return Header the top bar.
	 */
	public Header header() {
		return new Header();
	}

	/**
	 * @return String the screen title.
	 */
	public String title() {
		return textOf(TITLE);
	}

	/**
	 * @return List the names of the products currently on screen (top of the list).
	 */
	public List<String> visibleProductNames() {
		visible(PRODUCT_NAMES);
		return textsOf(PRODUCT_NAMES);
	}

	/**
	 * @return List the prices of the products currently on screen, as numbers.
	 */
	public List<BigDecimal> visiblePrices() {
		visible(PRODUCT_PRICES);
		return textsOf(PRODUCT_PRICES).stream().map(Prices::parse).toList();
	}

	/**
	 * Scrolls to a product and opens it.
	 *
	 * @param name      the product name as shown in the list.
	 * @return ProductScreen the product's detail screen.
	 */
	public ProductScreen openProduct(String name) {
		scrollToText(name).click();
		return new ProductScreen();
	}

	/**
	 * @param order     the sort order to choose in the dialog.
	 * @return CatalogScreen this screen, re-sorted.
	 */
	public CatalogScreen sortBy(Sort order) {
		tap(SORT);
		tap(AppiumBy.accessibilityId(order.label));
		return this;
	}
}
