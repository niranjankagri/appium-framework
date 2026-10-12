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

	@Test(groups = { "smoke", "cart" }, description = "Adding a product shows 1 on the cart badge and in the cart")
	public void addOneProduct() {
		ProductScreen product = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart();

		assertEquals(product.header().cartCount(), 1, "cart badge");
		CartScreen cart = product.header().openCart();
		assertEquals(cart.itemNames(), List.of(TestData.BACKPACK));
		assertEquals(cart.total(), product.price(), "cart total");
	}

	@Test(groups = "cart", description = "Quantity 3 adds three items and triples the total")
	public void addSeveralOfOneProduct() {
		ProductScreen product = new CatalogScreen().openProduct(TestData.BACKPACK).increaseQuantity(2);
		assertEquals(product.quantity(), 3, "quantity before adding");
		BigDecimal unitPrice = product.price();

		product.addToCart();

		assertEquals(product.header().cartCount(), 3, "cart badge");
		CartScreen cart = product.header().openCart();
		assertEquals(cart.itemCount(), 3, "items label");
		assertEquals(cart.total(), unitPrice.multiply(BigDecimal.valueOf(3)), "cart total");
	}

	@Test(groups = "cart", description = "Two different products both appear in the cart with the right total")
	public void addTwoProducts() {
		CatalogScreen catalog = new CatalogScreen();
		BigDecimal backpack = catalog.openProduct(TestData.BACKPACK).addToCart().price();
		driver().navigate().back();
		ProductScreen yellow = new CatalogScreen().openProduct(TestData.YELLOW_BACKPACK).addToCart();

		CartScreen cart = yellow.header().openCart();
		assertEquals(cart.itemNames(), List.of(TestData.BACKPACK, TestData.YELLOW_BACKPACK));
		assertEquals(cart.itemCount(), 2, "items label");
		assertEquals(cart.total(), backpack.add(yellow.price()), "cart total");
	}

	@Test(groups = "cart", description = "Removing the only item empties the cart")
	public void removeItem() {
		CartScreen cart = new CatalogScreen().openProduct(TestData.BACKPACK).addToCart()
				.header().openCart()
				.removeFirstItem();

		assertEquals(cart.header().cartCount(), 0, "cart badge");
		assertFalse(cart.hasCheckout(), "checkout button on an empty cart");
	}
}
