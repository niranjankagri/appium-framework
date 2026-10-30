package com.automation.appium.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.math.BigDecimal;
import java.util.List;

import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.CartScreen;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.ProductScreen;

/**
 * Adding, counting and removing cart items.
 */
public class CartTest extends BaseTest {

	@Test(groups = { "smoke", "cart" }, description = "[TC-112] Adding a product shows 1 on the cart badge and in the cart")
	public void tc112_addOneProduct() {
		// Open a product from the catalog and add it with the default quantity 1
		ProductScreen product = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart();

		// The badge on the cart icon updates at once
		assertEquals(product.header().cartCount(), 1, "cart badge");
		// The cart lists the product, and its total equals the price on the product screen
		CartScreen cart = product.header().openCart();
		assertEquals(cart.itemNames(), List.of(TestData.BACKPACK));
		assertEquals(cart.total(), product.price(), "cart total");
	}

	@Test(groups = "cart", description = "[TC-113] Quantity 3 adds three items and triples the total")
	public void tc113_addSeveralOfOneProduct() {
		// Quantity starts at 1, so two taps on plus make 3
		ProductScreen product = new CatalogScreen().openProduct(TestData.BACKPACK).increaseQuantity(2);
		assertEquals(product.quantity(), 3, "quantity before adding");
		// The product screen shows the unit price, not price x quantity
		BigDecimal unitPrice = product.price();

		product.addToCart();

		// Badge, items label and total all count the quantity, not the number of lines
		assertEquals(product.header().cartCount(), 3, "cart badge");
		CartScreen cart = product.header().openCart();
		assertEquals(cart.itemCount(), 3, "items label");
		// BigDecimal keeps money exact (no floating-point rounding noise)
		assertEquals(cart.total(), unitPrice.multiply(BigDecimal.valueOf(3)), "cart total");
	}

	@Test(groups = "cart", description = "[TC-114] Two different products both appear in the cart with the right total")
	public void tc114_addTwoProducts() {
		// First product: add it and remember its price
		CatalogScreen catalog = new CatalogScreen();
		BigDecimal backpack = catalog.openProduct(TestData.BACKPACK).addToCart().price();
		// Back to the catalog; the new CatalogScreen below waits until the list is shown again
		driver().navigate().back();
		// The second product sits below the first screen, so openProduct scrolls to it
		ProductScreen yellow = new CatalogScreen().openProduct(TestData.YELLOW_BACKPACK).addToCart();

		// Both lines in the order they were added; the total is the sum of both prices
		CartScreen cart = yellow.header().openCart();
		assertEquals(cart.itemNames(), List.of(TestData.BACKPACK, TestData.YELLOW_BACKPACK));
		assertEquals(cart.itemCount(), 2, "items label");
		assertEquals(cart.total(), backpack.add(yellow.price()), "cart total");
	}

	@Test(groups = "cart", description = "[TC-115] Removing the only item empties the cart")
	public void tc115_removeItem() {
		// Add one product, open the cart and tap Remove on its only line
		CartScreen cart = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart()
				.removeFirstItem();

		// Empty cart: the badge shows nothing and there is nothing to check out
		assertEquals(cart.header().cartCount(), 0, "cart badge");
		assertFalse(cart.hasCheckout(), "checkout button on an empty cart");
	}
}
