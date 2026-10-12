package com.automation.appium.screens;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

/**
 * Shows the device's GPS position. Opening it asks for the location permission
 * after an app reset.
 */
public class GeoLocationScreen extends BaseScreen {

	private static final By LATITUDE = id("latitudeTV");
	private static final By LONGITUDE = id("longitudeTV");
	private static final By START = id("startBtn");

	/**
	 * Accepts the location permission if asked, then waits for the coordinates.
	 */
	public GeoLocationScreen() {
		PermissionDialog.allowIfShown();
		visible(LATITUDE);
	}

	/**
	 * @return GeoLocationScreen this screen, now following location updates.
	 */
	public GeoLocationScreen startObserving() {
		tap(START);
		return this;
	}

	/**
	 * @return double the latitude shown on screen.
	 */
	public double latitude() {
		return Double.parseDouble(textOf(LATITUDE));
	}

	/**
	 * @return double the longitude shown on screen.
	 */
	public double longitude() {
		return Double.parseDouble(textOf(LONGITUDE));
	}

	/**
	 * Waits until the shown position is within {@code tolerance} degrees of the expected one;
	 * GPS values come back as floats (13.405 is shown as 13.4049983).
	 *
	 * @param latitude  expected latitude.
	 * @param longitude expected longitude.
	 * @param tolerance allowed difference in degrees.
	 * @return boolean  true when the screen showed the position in time.
	 */
	public boolean showsPosition(double latitude, double longitude, double tolerance) {
		try {
			return wait.until(d -> Math.abs(latitude() - latitude) <= tolerance
					&& Math.abs(longitude() - longitude) <= tolerance);
		} catch (TimeoutException e) {
			return false;
		}
	}
}
