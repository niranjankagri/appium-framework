package com.automation.appium.driver;

import java.nio.file.Files;

import com.automation.appium.config.Config;

import io.appium.java_client.android.options.UiAutomator2Options;

/**
 * Builds the Appium capabilities for an Android session from {@link Config}.
 * Kept apart from the driver code so the settings are easy to read and test.
 */
public final class CapabilitiesFactory {

	// Static helpers only
	private CapabilitiesFactory() {
	}

	/**
	 * @return UiAutomator2Options the options for a session on the configured device and app.
	 * @throws IllegalStateException if the app file has not been downloaded yet.
	 */
	public static UiAutomator2Options android() {
		Config config = Config.get();

		// Fail early with a clear hint instead of a long Appium error
		if (!Files.isRegularFile(config.appPath())) {
			throw new IllegalStateException("App not found at " + config.appPath()
					+ ". Run scripts/get-app.sh (or scripts\\get-app.ps1) first.");
		}

		UiAutomator2Options options = new UiAutomator2Options()
				.setDeviceName(config.value("deviceName"))
				.setPlatformVersion(config.value("platformVersion"))
				// Installs the app when it is missing or older than this file
				.setApp(config.appPath().toString())
				.setAppPackage(config.value("appPackage"))
				.setAppActivity(config.value("appActivity"))
				// The splash screen hands over to the main activity; accept any of them
				.setAppWaitActivity("*")
				// Grant runtime permissions up front so no system dialog blocks a test
				.setAutoGrantPermissions(true)
				// Keep the installed app between sessions; tests reset its data themselves
				.setNoReset(true)
				.setNewCommandTimeout(config.newCommandTimeout());

		// Pin a specific device when several are connected
		String udid = config.optional("udid");
		if (!udid.isEmpty()) {
			options.setUdid(udid);
		}
		return options;
	}
}
