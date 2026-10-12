package com.automation.appium.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

/**
 * Test settings read from {@code config.properties} on the classpath.
 * A system property with the same key wins, so any setting can be changed
 * from the command line, e.g. {@code -DdeviceName=emulator-5556}.
 */
public final class Config {

	// Name of the settings file on the classpath
	private static final String FILE = "config.properties";
	// Loaded once; the settings do not change during a run
	private static final Config INSTANCE = new Config();

	// Values from the settings file
	private final Properties properties = new Properties();

	// Use get()
	private Config() {
		try (InputStream in = Config.class.getClassLoader().getResourceAsStream(FILE)) {
			if (in == null) {
				throw new IllegalStateException(FILE + " not found on the classpath");
			}
			properties.load(in);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read " + FILE, e);
		}
	}

	/**
	 * @return Config the shared settings.
	 */
	public static Config get() {
		return INSTANCE;
	}

	/**
	 * @param key       the setting name.
	 * @return String   the system property with that name, else the file value.
	 * @throws IllegalStateException if the setting is in neither place.
	 */
	public String value(String key) {
		String value = optional(key);
		if (value.isEmpty()) {
			throw new IllegalStateException("Missing setting '" + key + "' in " + FILE);
		}
		return value;
	}

	/**
	 * @param key       the setting name.
	 * @return String   the trimmed value, or an empty string when it is not set.
	 */
	public String optional(String key) {
		String value = System.getProperty(key, properties.getProperty(key, ""));
		return value.trim();
	}

	/**
	 * @return URL the Appium server address.
	 */
	public URL appiumUrl() {
		try {
			return URI.create(value("appiumUrl")).toURL();
		} catch (Exception e) {
			throw new IllegalStateException("appiumUrl is not a valid URL: " + value("appiumUrl"), e);
		}
	}

	/**
	 * @return Path the absolute path of the app file, so Appium finds it from any working directory.
	 */
	public Path appPath() {
		return Path.of(value("app")).toAbsolutePath().normalize();
	}

	/**
	 * @return Duration how long page objects wait for an element.
	 */
	public Duration explicitWait() {
		return Duration.ofSeconds(Long.parseLong(value("explicitWait")));
	}

	/**
	 * @return Duration how long Appium keeps an idle session.
	 */
	public Duration newCommandTimeout() {
		return Duration.ofSeconds(Long.parseLong(value("newCommandTimeout")));
	}
}
