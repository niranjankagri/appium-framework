package com.automation.appium.base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import com.automation.appium.driver.DriverManager;

import io.appium.java_client.android.AndroidDriver;

/**
 * Parent of every test class: gives each test a freshly reset app and closes
 * the Appium sessions when the suite ends.
 */
public abstract class BaseTest {

	/**
	 * Starts this thread's session on first use, then resets the app so tests
	 * never depend on what an earlier test left behind.
	 */
	@BeforeMethod(alwaysRun = true)
	public void startCleanApp() {
		DriverManager.resetApp();
	}

	/**
	 * Closes every session opened during the suite.
	 */
	@AfterSuite(alwaysRun = true)
	public void closeSessions() {
		DriverManager.quitAll();
	}

	/**
	 * @return AndroidDriver the session of the current test thread.
	 */
	protected AndroidDriver driver() {
		return DriverManager.get();
	}
}
