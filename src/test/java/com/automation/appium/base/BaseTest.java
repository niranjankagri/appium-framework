package com.automation.appium.base;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import com.automation.appium.config.Config;
import com.automation.appium.driver.DriverManager;
import com.automation.appium.report.HtmlReportListener;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;

/**
 * Parent of every test class: gives each test a freshly reset app, records the
 * screen while it runs, keeps a screenshot and the video when it fails, and
 * closes the Appium sessions when the suite ends.
 */
public abstract class BaseTest {

	// Where failure videos are written; linked from the HTML report
	private static final Path ARTIFACTS = Path.of("target", "artifacts");

	/**
	 * Starts this thread's session on first use, resets the app so tests never
	 * depend on what an earlier test left behind, then starts the screen recording.
	 */
	@BeforeMethod(alwaysRun = true)
	public void startCleanApp() {
		DriverManager.resetApp();
		if (recordVideo()) {
			// adb screenrecord on the device; no ffmpeg needed. Half size keeps files small
			driver().startRecordingScreen(new AndroidStartScreenRecordingOptions()
					.withVideoSize("540x1200")
					// 1 Mbit/s instead of the default 4: less load on the emulator, still readable
					.withBitRate(1_000_000)
					.withTimeLimit(Duration.ofMinutes(3)));
		}
	}

	/**
	 * On failure: attaches a screenshot and saves the recording for the report.
	 * On success the recording is stopped and thrown away.
	 *
	 * @param result    the test that just ran.
	 */
	@AfterMethod(alwaysRun = true)
	public void keepEvidence(ITestResult result) {
		if (!DriverManager.hasDriver()) {
			return;
		}
		boolean failed = result.getStatus() == ITestResult.FAILURE;
		if (failed) {
			try {
				result.setAttribute(HtmlReportListener.SCREENSHOT, driver().getScreenshotAs(OutputType.BASE64));
			} catch (WebDriverException e) {
				// The app may have crashed; the report still shows the error and the video
			}
		}
		if (recordVideo()) {
			String video = stopRecording();
			if (failed && video != null && !video.isEmpty()) {
				result.setAttribute(HtmlReportListener.VIDEO, saveVideo(result, video));
			}
		}
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

	// recordVideo=false (config or -D) switches recording off, e.g. on a slow CI emulator
	private static boolean recordVideo() {
		return Boolean.parseBoolean(Config.get().value("recordVideo"));
	}

	// Base64 MP4 of the recording, or null when there was none to stop
	private String stopRecording() {
		try {
			return driver().stopRecordingScreen();
		} catch (WebDriverException e) {
			return null;
		}
	}

	// Writes target/artifacts/<Class>.<method>.mp4 and returns its path
	private static Path saveVideo(ITestResult result, String base64) {
		Path file = ARTIFACTS.resolve(result.getTestClass().getRealClass().getSimpleName()
				+ "." + result.getMethod().getMethodName() + ".mp4").toAbsolutePath();
		try {
			Files.createDirectories(file.getParent());
			Files.write(file, Base64.getDecoder().decode(base64));
			return file;
		} catch (IOException e) {
			throw new UncheckedIOException("Could not save " + file, e);
		}
	}
}
