package com.automation.appium.screens;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.automation.appium.driver.DriverManager;
import com.automation.appium.report.Steps;

import io.appium.java_client.android.AndroidDriver;

/**
 * Android's runtime permission dialog ("Allow My Demo App to access ...").
 * It belongs to the system (package com.android.permissioncontroller), not
 * to the app. Clearing the app's data before each test also takes back its
 * permissions, so screens that need one call {@link #allowIfShown()}.
 */
public final class PermissionDialog {

	// "While using the app" (location) and "Allow" (photos, notifications, ...)
	private static final By WHILE_USING = By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
	private static final By ALLOW = By.id("com.android.permissioncontroller:id/permission_allow_button");
	// How long to wait for the first dialog: it opens after the screen has loaded
	private static final Duration FIRST = Duration.ofSeconds(6);
	// How long to wait for a further one: some screens ask for two permissions in a row
	private static final Duration NEXT = Duration.ofSeconds(2);

	// Static helpers only
	private PermissionDialog() {
	}

	/**
	 * Accepts every permission dialog that appears (the Drawing screen asks for
	 * two in a row); does nothing when the permissions are already granted.
	 *
	 * @return int how many dialogs were accepted.
	 */
	public static int allowIfShown() {
		int accepted = 0;
		while (accepted < 3 && allowOne(accepted == 0 ? FIRST : NEXT)) {
			accepted++;
		}
		return accepted;
	}

	// Accepts one dialog if it appears within the given time
	private static boolean allowOne(Duration appear) {
		AndroidDriver driver = DriverManager.get();
		try {
			WebElement button = new WebDriverWait(driver, appear).until(d -> {
				for (By locator : List.of(WHILE_USING, ALLOW)) {
					List<WebElement> found = d.findElements(locator);
					if (!found.isEmpty()) {
						return found.get(0);
					}
				}
				return null;
			});
			Steps.log("Allow the system permission dialog: \"" + button.getText() + "\"");
			button.click();
			return true;
		} catch (TimeoutException e) {
			// No dialog: the permission was already granted
			return false;
		}
	}
}
