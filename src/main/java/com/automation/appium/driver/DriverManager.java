package com.automation.appium.driver;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.automation.appium.config.Config;

import io.appium.java_client.android.AndroidDriver;

/**
 * One Appium session per test thread, created on first use and reused by the
 * following tests on that thread. Starting a session takes several seconds, so
 * tests reset the app's data instead of opening a new session each time.
 */
public final class DriverManager {

	// The session of the current test thread
	private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();
	// Every open session, so all of them can be closed at the end of the run
	private static final List<AndroidDriver> ALL = new CopyOnWriteArrayList<>();

	// Static helpers only
	private DriverManager() {
	}

	/**
	 * @return AndroidDriver the session of this thread, started if there is none yet.
	 */
	public static AndroidDriver get() {
		AndroidDriver driver = DRIVER.get();
		if (driver == null) {
			driver = new AndroidDriver(Config.get().appiumUrl(), CapabilitiesFactory.android());
			DRIVER.set(driver);
			ALL.add(driver);
		}
		return driver;
	}

	/**
	 * @return boolean true when this thread already has a session.
	 */
	public static boolean hasDriver() {
		return DRIVER.get() != null;
	}

	/**
	 * Closes every session opened during the run.
	 */
	public static void quitAll() {
		for (AndroidDriver driver : ALL) {
			try {
				driver.quit();
			} catch (RuntimeException e) {
				// The session may already be gone (e.g. timed out); nothing left to close
			}
		}
		ALL.clear();
		DRIVER.remove();
	}

	/**
	 * Stops the app, wipes its data and starts it again, so each test begins
	 * on the catalog screen with an empty cart and no logged-in user.
	 */
	public static void resetApp() {
		AndroidDriver driver = get();
		String appPackage = Config.get().value("appPackage");
		driver.terminateApp(appPackage);
		// Same as "Clear storage" in Android settings: cart and login are gone
		driver.executeScript("mobile: clearApp", Map.of("appId", appPackage));
		driver.activateApp(appPackage);
	}
}
