package com.automation.appium.tests.interview;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.automation.appium.base.BaseTest;
import com.automation.appium.config.Config;
import com.automation.appium.screens.CatalogScreen;
import com.automation.appium.screens.Device;
import com.automation.appium.screens.GeoLocationScreen;

import io.appium.java_client.appmanagement.ApplicationState;

/**
 * Appium's device APIs, as asked in interviews: app states, device facts, a
 * mocked GPS position, the clipboard, files, logcat and the notification
 * shade. The API call is the answer, so the tests call the driver directly.
 */
@Test(groups = "interview")
public class DeviceApiTest extends BaseTest {

	// Package of the app under test
	private final String app = Config.get().value("appPackage");

	/**
	 * Installed, in the foreground, stopped, started again: the states Appium
	 * reports, e.g. to check that a test really left the app.
	 */
	@Test(description = "The app's state follows terminate and activate")
	public void appStateLifecycle() {
		new CatalogScreen();
		assertTrue(driver().isAppInstalled(app), "installed");
		assertEquals(driver().queryAppState(app), ApplicationState.RUNNING_IN_FOREGROUND);

		driver().terminateApp(app);
		assertEquals(driver().queryAppState(app), ApplicationState.NOT_RUNNING);

		driver().activateApp(app);
		// activateApp returns while the splash screen starts; wait for the foreground state
		ApplicationState state = new WebDriverWait(driver(), Duration.ofSeconds(10)).until(
				d -> driver().queryAppState(app) == ApplicationState.RUNNING_IN_FOREGROUND ? ApplicationState.RUNNING_IN_FOREGROUND : null);
		assertEquals(state, ApplicationState.RUNNING_IN_FOREGROUND);
	}

	/**
	 * What the device says about itself; handy for logs and for skipping tests
	 * on unsupported Android versions.
	 */
	@Test(description = "Device info matches the configured Android version")
	@SuppressWarnings("unchecked")
	public void deviceInfo() {
		new CatalogScreen();
		Map<String, Object> info = (Map<String, Object>) driver().executeScript("mobile: deviceInfo");

		assertEquals(info.get("platformVersion"), Config.get().value("platformVersion"), "Android version");
		assertTrue(Integer.parseInt(String.valueOf(info.get("apiVersion"))) >= 30, "API level: " + info.get("apiVersion"));
		assertFalse(String.valueOf(info.get("model")).isBlank(), "model");
		assertFalse(driver().getDeviceTime().isBlank(), "device time");
	}

	/**
	 * A mocked GPS position: set Berlin, the app's location screen shows it.
	 * GPS values come back as floats, so the check allows 0.001 degrees.
	 */
	@Test(description = "A mocked GPS position appears in the app")
	public void mockGpsLocation() {
		driver().executeScript("mobile: setGeolocation", Map.of("latitude", 52.52, "longitude", 13.405, "altitude", 34));

		GeoLocationScreen geo = new CatalogScreen().header().openMenu().geoLocation().startObserving();

		assertTrue(geo.showsPosition(52.52, 13.405, 0.001),
				"position shown: " + geo.latitude() + ", " + geo.longitude());
	}

	/**
	 * Setting and reading the device clipboard, e.g. to test paste handling or
	 * to read a code an app copies.
	 */
	@Test(description = "Text survives a round trip through the clipboard")
	public void clipboardRoundTrip() {
		new CatalogScreen();

		driver().setClipboardText("bod@example.com");

		assertEquals(driver().getClipboardText(), "bod@example.com");
	}

	/**
	 * Files on the device: push test data (an image to upload, a config file),
	 * pull results back. Data travels Base64-encoded.
	 */
	@Test(description = "A file pushed to the device can be pulled back")
	public void pushAndPullAFile() {
		new CatalogScreen();
		String path = "/sdcard/Download/appium-framework-check.txt";
		byte[] content = "pushed by DeviceApiTest".getBytes(StandardCharsets.UTF_8);

		driver().pushFile(path, Base64.getEncoder().encode(content));
		try {
			assertEquals(driver().pullFile(path), content);
		} finally {
			// Leave the device as it was
			driver().executeScript("mobile: deleteFile", Map.of("remotePath", path));
		}
	}

	/**
	 * The device log (logcat) through Appium: the first place to look when an
	 * app crashes, as with the known 7th-product crash.
	 */
	@Test(description = "The device log (logcat) can be read")
	public void readLogcat() {
		new CatalogScreen();

		List<LogEntry> entries = driver().manage().logs().get("logcat").getAll();

		assertFalse(entries.isEmpty(), "logcat entries since the last read");
	}

	/**
	 * Opening Android's notification shade (system UI, outside the app) and
	 * coming back with the back button.
	 */
	@Test(description = "The notification shade opens and closes")
	public void notificationShade() {
		new CatalogScreen();

		driver().openNotifications();
		try {
			// The shade slides in; wait for its panel (system UI, package com.android.systemui)
			new WebDriverWait(driver(), Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfElementLocated(
					By.id("com.android.systemui:id/notification_panel")));
		} finally {
			// Close it whatever happens: an open shade would hide the app from the next test
			new Device().back();
		}
		assertEquals(new CatalogScreen().title(), "Products", "back in the app");
	}
}
