package com.automation.appium.screens;

import java.util.Set;

import org.openqa.selenium.By;

/**
 * A native form that opens a web page inside the app (a WebView). Such hybrid
 * screens give Appium a second context, WEBVIEW_..., next to NATIVE_APP.
 */
public class WebViewScreen extends BaseScreen {

	private static final By URL = id("urlET");
	private static final By GO = id("goBtn");
	private static final By WEB_VIEW = id("webView");

	/**
	 * Waits for the URL field.
	 */
	public WebViewScreen() {
		visible(URL);
	}

	/**
	 * Opens an HTTPS page in the app's WebView.
	 *
	 * @param url       the page address.
	 * @return WebViewScreen this screen, now showing the page.
	 */
	public WebViewScreen open(String url) {
		type(URL, url);
		tap(GO);
		visible(WEB_VIEW);
		return this;
	}

	/**
	 * Waits until the WebView context is offered: it appears once the page has started loading.
	 *
	 * @return Set the context names, e.g. NATIVE_APP and WEBVIEW_com.saucelabs.mydemoapp.android.
	 */
	public Set<String> contexts() {
		return wait.until(d -> {
			Set<String> names = driver.getContextHandles();
			return names.stream().anyMatch(name -> name.startsWith("WEBVIEW")) ? names : null;
		});
	}
}
