package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.CatalogScreen.Sort;
import com.automation.appium.screens.ProductScreen;

/**
 * Product list: sorting and opening a product. Sort checks look at the
 * products on screen, which are the top of the sorted list.
 */
public class CatalogTest extends BaseTest {

	@Test(groups = "catalog", description = "[TC-107] Sorting by name A-Z orders the products alphabetically")
	public void tc107_sortByNameAscending() {
		// Pick the option in the sort dialog, then read the names on screen
		List<String> names = new CatalogScreen().sortBy(Sort.NAME_ASC).visibleProductNames();

		// Sorting the list that was read must not change it
		assertEquals(names, names.stream().sorted().toList(), "names A-Z");
	}

	@Test(groups = "catalog", description = "[TC-108] Sorting by name Z-A orders the products in reverse")
	public void tc108_sortByNameDescending() {
		List<String> names = new CatalogScreen().sortBy(Sort.NAME_DESC).visibleProductNames();

		// Same check in reverse order
		assertEquals(names, names.stream().sorted(Comparator.reverseOrder()).toList(), "names Z-A");
	}

	@Test(groups = "catalog", description = "[TC-109] Sorting by price low-high puts the cheapest first")
	public void tc109_sortByPriceAscending() {
		// Prices are compared as numbers: as text, "$9.99" would sort after "$15.99"
		List<BigDecimal> prices = new CatalogScreen().sortBy(Sort.PRICE_ASC).visiblePrices();

		assertEquals(prices, prices.stream().sorted().toList(), "prices low-high");
	}

	@Test(groups = "catalog", description = "[TC-110] Sorting by price high-low puts the most expensive first")
	public void tc110_sortByPriceDescending() {
		List<BigDecimal> prices = new CatalogScreen().sortBy(Sort.PRICE_DESC).visiblePrices();

		assertEquals(prices, prices.stream().sorted(Comparator.reverseOrder()).toList(), "prices high-low");
	}

	@Test(groups = { "smoke", "catalog" }, description = "[TC-111] A product further down the list opens with its own name and price")
	public void tc111_openProductBelowTheFold() {
		// The yellow backpack is not on the first screen: the page object has to scroll to it
		ProductScreen product = new CatalogScreen().openProduct(TestData.YELLOW_BACKPACK);

		// The product screen belongs to the product that was tapped
		assertEquals(product.name(), TestData.YELLOW_BACKPACK);
		assertEquals(product.price(), new BigDecimal("29.99"));
	}
}
