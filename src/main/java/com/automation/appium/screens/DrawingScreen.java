package com.automation.appium.screens;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.PointerInput.Origin;
import org.openqa.selenium.interactions.Sequence;

import com.automation.appium.report.Steps;

/**
 * A signature pad: a canvas with no child elements, so the only way to check
 * a drawing is to compare pictures of the pad.
 */
public class DrawingScreen extends BaseScreen {

	private static final By PAD = id("signature_pad");
	private static final By CLEAR = id("clearBtn");

	/**
	 * Waits for the pad. Opening the screen may first ask for the photos permission.
	 */
	public DrawingScreen() {
		PermissionDialog.allowIfShown();
		visible(PAD);
	}

	/**
	 * Draws a "V" across the pad with one finger, as a W3C pointer action:
	 * press, move, move, release. The points are relative to the pad, in percent.
	 *
	 * @return DrawingScreen this screen.
	 */
	public DrawingScreen drawCheckMark() {
		Rectangle pad = visible(PAD).getRect();
		List<Point> path = List.of(
				point(pad, 20, 40), point(pad, 45, 70), point(pad, 80, 25));
		Steps.log("Draw a check mark on \"signature_pad\" with a W3C pointer action");

		PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
		Sequence stroke = new Sequence(finger, 0);
		stroke.addAction(finger.createPointerMove(Duration.ZERO, Origin.viewport(), path.get(0)));
		stroke.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
		for (Point next : path.subList(1, path.size())) {
			stroke.addAction(finger.createPointerMove(Duration.ofMillis(300), Origin.viewport(), next));
		}
		stroke.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
		driver.perform(List.of(stroke));
		return this;
	}

	/**
	 * @return DrawingScreen this screen with an empty pad.
	 */
	public DrawingScreen clear() {
		tap(CLEAR);
		return this;
	}

	/**
	 * @return byte[] a PNG of the pad only (an element screenshot, not the whole screen).
	 */
	public byte[] padImage() {
		return visible(PAD).getScreenshotAs(OutputType.BYTES);
	}

	// A point inside the pad, given in percent of its width and height
	private static Point point(Rectangle pad, int xPercent, int yPercent) {
		return new Point(pad.getX() + pad.getWidth() * xPercent / 100, pad.getY() + pad.getHeight() * yPercent / 100);
	}
}
