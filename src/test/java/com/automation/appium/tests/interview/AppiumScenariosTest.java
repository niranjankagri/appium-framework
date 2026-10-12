package com.automation.appium.tests.interview;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.data.TestData;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.DrawingScreen;
import com.automation.appium.screens.Header;
import com.automation.appium.screens.LoginScreen;
import com.automation.appium.screens.ProductScreen;
import com.automation.appium.screens.WebViewScreen;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

/**
 * Common Appium interview tasks, solved on the demo app: locator strategies,
 * W3C gestures, element attributes and screenshots, the page source, custom
 * waits, Android key events, runtime permissions and hybrid contexts.
 * <p>
 * Unlike the functional tests, these call the driver directly where the API
 * call is the answer being shown. Each test is short enough to explain in an
 * interview; docs/interview-questions.pdf links back to them.
 */
@Test(groups = "interview")
public class AppiumScenariosTest extends BaseTest {

	/**
	 * The cart icon found five ways. Resource id and accessibility id are the
	 * fast, stable choices; UiAutomator covers what has neither; XPath works
	 * but walks the whole XML tree, so it is the slowest.
	 */
	@Test(description = "Five locator strategies find the same element")
	public void locatorStrategiesFindTheSameElement() {
		new CatalogScreen();
		List<By> locators = List.of(
				AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartRL"),
				AppiumBy.accessibilityId("View cart"),
				AppiumBy.androidUIAutomator("new UiSelector().description(\"View cart\")"),
				AppiumBy.androidUIAutomator("new UiSelector().resourceIdMatches(\".*:id/cartRL\")"),
				AppiumBy.xpath("//*[@content-desc='View cart']"));

		Rectangle first = driver().findElement(locators.get(0)).getRect();
		for (By locator : locators) {
			// Same position and size means the same element on screen
			assertEquals(driver().findElement(locator).getRect(), first, locator.toString());
		}
	}

	/**
	 * A tap at screen coordinates with a W3C pointer action: the low-level way
	 * when an element cannot be tapped normally (custom views, games, maps).
	 */
	@Test(description = "A W3C tap at coordinates opens the first product")
	public void tapAtCoordinates() {
		new CatalogScreen();
		Rectangle image = driver().findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/productIV")).getRect();
		Point centre = new Point(image.getX() + image.getWidth() / 2, image.getY() + image.getHeight() / 2);

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence tap = new Sequence(finger, 0)
				.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), centre))
				.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
				.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver().perform(List.of(tap));

		assertEquals(new ProductScreen().name(), TestData.BACKPACK);
	}

	/**
	 * A multi-point drag on a canvas. The pad has no child elements to check,
	 * so the proof is visual: element screenshots before and after differ.
	 */
	@Test(description = "A W3C drag draws on the signature pad")
	public void drawWithAPointerAction() {
		DrawingScreen pad = new CatalogScreen().header().openMenu().drawing();
		byte[] empty = pad.padImage();

		byte[] drawn = pad.drawCheckMark().padImage();
		assertFalse(Arrays.equals(drawn, empty), "the pad looks the same after drawing");

		byte[] cleared = pad.clear().padImage();
		assertFalse(Arrays.equals(cleared, drawn), "Clear did not change the pad");
	}

	/**
	 * What an element reports about itself: text, accessibility label, state
	 * and position. getAttribute takes UiAutomator2's attribute names.
	 */
	@Test(description = "Element attributes: text, label, state and position")
	public void elementAttributes() {
		new CatalogScreen().openProduct(TestData.BACKPACK);
		WebElement button = driver().findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/cartBt"));

		assertEquals(button.getText(), "Add to cart");
		assertEquals(button.getAttribute("content-desc"), "Tap to add product to cart");
		assertEquals(button.getAttribute("clickable"), "true");
		assertEquals(button.getAttribute("class"), "android.widget.Button");
		assertTrue(button.isEnabled() && button.isDisplayed(), "enabled and displayed");
		// Fully inside the screen
		Dimension screen = driver().manage().window().getSize();
		Rectangle rect = button.getRect();
		assertTrue(rect.getX() >= 0 && rect.getY() >= 0
				&& rect.getX() + rect.getWidth() <= screen.getWidth()
				&& rect.getY() + rect.getHeight() <= screen.getHeight(), "button inside the screen: " + rect);
	}

	/**
	 * A screenshot of one element instead of the whole screen, e.g. for visual
	 * checks of a single image or chart.
	 */
	@Test(description = "An element screenshot is a PNG")
	public void elementScreenshotIsAPng() {
		new CatalogScreen();
		byte[] png = driver().findElement(AppiumBy.id("com.saucelabs.mydemoapp.android:id/productIV"))
				.getScreenshotAs(OutputType.BYTES);

		// Every PNG starts with these 8 bytes
		byte[] signature = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n' };
		assertEquals(Arrays.copyOf(png, 8), signature, "PNG signature");
	}

	/**
	 * The page source is the XML tree Appium Inspector shows. Useful for
	 * debugging a locator, and the reason XPath works at all.
	 */
	@Test(description = "The page source is the screen's XML tree")
	public void pageSourceIsTheScreenTree() {
		new CatalogScreen();
		String source = driver().getPageSource();

		assertTrue(source.startsWith("<?xml"), "XML document");
		assertTrue(source.contains("resource-id=\"com.saucelabs.mydemoapp.android:id/productTV\""), "catalog title");
		assertTrue(source.contains("text=\"" + TestData.BACKPACK + "\""), "first product");
	}

	/**
	 * A FluentWait with a custom condition: poll every 200 ms for up to 10 s,
	 * ignoring "not found" while the badge is not there yet.
	 */
	@Test(description = "A FluentWait waits for the cart badge to reach 3")
	public void fluentWaitForACustomCondition() {
		new CatalogScreen().openProduct(TestData.BACKPACK).increaseQuantity(2).addToCart();
		Header header = new Header();

		int count = new FluentWait<>(header)
				.withTimeout(Duration.ofSeconds(10))
				.pollingEvery(Duration.ofMillis(200))
				.ignoring(NoSuchElementException.class)
				.until(h -> h.cartCount() == 3 ? h.cartCount() : null);

		assertEquals(count, 3);
	}

	/**
	 * Typing with Android key events instead of sendKeys: how a real keyboard
	 * would type, e.g. for fields that react to each key.
	 */
	@Test(description = "Android key events type into a field")
	public void typeWithAndroidKeyEvents() {
		LoginScreen login = new CatalogScreen().header().openMenu().logIn().focusUsername();

		for (AndroidKey key : List.of(AndroidKey.B, AndroidKey.O, AndroidKey.D)) {
			driver().pressKey(new KeyEvent(key));
		}

		assertEquals(login.usernameText(), "bod");
	}

	/**
	 * The runtime permission dialog belongs to Android, not the app. Revoke the
	 * permission, open a screen that needs it, accept the system dialog.
	 */
	@Test(description = "A revoked permission is asked for again and accepted")
	public void runtimePermissionDialog() {
		new CatalogScreen();
		String appPackage = "com.saucelabs.mydemoapp.android";
		driver().executeScript("mobile: changePermissions", Map.of(
				"permissions", List.of("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"),
				"appPackage", appPackage, "action", "revoke"));
		assertFalse(grantedPermissions(appPackage).contains("android.permission.ACCESS_FINE_LOCATION"), "revoked");
		// Revoking a permission stops the app; start it again
		driver().activateApp(appPackage);

		// GeoLocationScreen accepts the dialog ("While using the app") through PermissionDialog
		new CatalogScreen().header().openMenu().geoLocation();

		assertTrue(grantedPermissions(appPackage).contains("android.permission.ACCESS_FINE_LOCATION"),
				"granted again through the dialog");
	}

	/**
	 * A hybrid screen: a WebView inside the native app. Appium lists a WEBVIEW
	 * context next to NATIVE_APP; switching into it (driver.context(...)) needs
	 * a Chromedriver that matches the device's WebView, so this test only checks
	 * that the context is offered and stays native.
	 */
	@Test(description = "A WebView screen offers a WEBVIEW context")
	public void hybridScreenOffersAWebViewContext() {
		WebViewScreen web = new CatalogScreen().header().openMenu().webView().open("https://www.saucedemo.com");

		Set<String> contexts = web.contexts();

		assertTrue(contexts.contains("NATIVE_APP"), "native context: " + contexts);
		assertTrue(contexts.contains("WEBVIEW_com.saucelabs.mydemoapp.android"), "WebView context: " + contexts);
		assertEquals(driver().getContext(), "NATIVE_APP", "still in the native context");
	}

	@SuppressWarnings("unchecked")
	private List<String> grantedPermissions(String appPackage) {
		return (List<String>) driver().executeScript("mobile: getPermissions",
				Map.of("type", "granted", "appPackage", appPackage));
	}
}
