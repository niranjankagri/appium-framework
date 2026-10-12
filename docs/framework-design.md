# Framework design

How the framework is built and why. Each section names the class that does it.

## Layers

```text
tests (TestNG)            what is checked             tests/*Test.java, data/TestData.java
   │ uses
screens (page objects)    what a user can do          screens/*Screen.java, Header, SideMenu, Device
   │ use
BaseScreen                how to wait, tap, type,     screens/BaseScreen.java
                          scroll, and log each step
   │ uses
DriverManager             one Appium session per      driver/DriverManager.java, CapabilitiesFactory.java
                          thread, app reset
   │ talks to
Appium server + UiAutomator2 driver  →  Android emulator or phone
```

Tests never hold a locator or call the driver for UI work. A test reads like the user story it checks:

```java
ReviewScreen review = cart.checkoutAsGuest()
        .loginToCheckout(TestData.USER, TestData.PASSWORD)
        .fill(TestData.ADDRESS)
        .toPayment()
        .fill(TestData.CARD)
        .reviewOrder();
```

## One session, reset per test

*`DriverManager`, `BaseTest`*

Starting an Appium session takes 5-10 seconds (UiAutomator2 server install and start, app launch).
Opening one per test would double the run time. Instead each thread keeps one session, and before
every test `DriverManager.resetApp()` does what a fresh install would:

1. `terminateApp` stops the app.
2. `mobile: clearApp` wipes its data, like *Settings > Storage > Clear storage*: cart and login are gone.
3. `activateApp` starts it again on the catalog.

That takes about 2 seconds and gives every test the same starting point, so tests can run in any order.
`noReset=true` in the capabilities keeps Appium from reinstalling the app; the reset is ours.

The session lives in a `ThreadLocal`, so the design also works with parallel tests, one emulator per
thread (`-Dudid=` per run). With one emulator the suite runs on one thread.

## Screens: waiting is built in

*`BaseScreen`*

Every action waits first: `tap` waits until the element is clickable, `type` and `textOf` wait until it is
visible. Constructors wait for one element that proves the screen is shown (`CatalogScreen` waits for the
"Products" title), so `new CatalogScreen()` doubles as "the app has finished the splash screen".
There is no `Thread.sleep` in tests or screens; the only fixed pause is a 250 ms poll in
`Device.waitForKeyboard`, where Appium has no wait condition.

**Navigation returns the next screen.** `CartScreen.checkout()` returns an `AddressScreen`. Where the next
screen depends on state, there are two methods instead of a guess: `checkoutAsGuest()` returns the
`LoginScreen`, `checkout()` (logged in) the `AddressScreen`. A test that takes the wrong one fails in the
constructor with a clear timeout instead of somewhere later.

**Header is a component**, not a screen: menu, cart icon and badge exist on every screen, so screens
expose it through `header()` instead of repeating the locators.

## Locators

In this order, matching the advice in [setup.md](setup.md#33-finding-a-locator):

| Locator | Used for | Why |
|---|---|---|
| resource id (`id("cartBt")`) | almost everything | set by the app's developers, stable, fastest |
| accessibility id | the sort options | stable, readable, and a check that the app is accessible |
| UiAutomator `text(...)` | side menu entries | they have no id; text is the only handle |
| UiAutomator `fromParent` | product image next to its name | only the image opens a product |
| XPath | never | slow on Android and breaks when the layout changes |

`BaseScreen.id("cartBt")` adds the package prefix, so screens use the short names that Appium Inspector shows.

## Scrolling

*`BaseScreen.scrollTo`*

Android only creates views for what is on screen, so an element further down a list does not exist until
it is scrolled into view. `UiScrollable.scrollIntoView` is the usual answer, but this app's catalog does not
report itself as scrollable, so UiScrollable finds nothing. `scrollTo` swipes half a screen at a time
(`mobile: swipeGesture` at a moderate speed, so the list does not fling past the element) and checks after
each swipe. It searches down first, then back up, because a list keeps its scroll position when the user
returns to it (back from a product). The end of the list is detected when a swipe no longer changes the
page source. After the element appears, the tap goes through the normal wait again, because the list can
still be gliding.

The first version only swiped down, at the default speed. A full run caught it: after "back" the catalog
was already scrolled, the swipes flung past the product and ended at the bottom of the list. The failure
screenshot in the report showed exactly that, which is why it is kept for every failure.

## System dialogs and permissions

*`PermissionDialog`, `DrawingScreen`, `GeoLocationScreen`*

`autoGrantPermissions` grants runtime permissions when the app is installed, but clearing the app's data
before each test takes them back. Screens that need a permission therefore accept Android's dialog
themselves: `PermissionDialog.allowIfShown()` waits briefly for the system dialog
(`com.android.permissioncontroller`) and accepts every one that appears, because the Drawing screen asks
for two in a row. Screens without a permission never wait for it.

## Prices as BigDecimal

*`Prices`*

`$ 29.99` becomes `new BigDecimal("29.99")`. With `double`, a $49.99 jacket plus $5.99 delivery is
55.980000000000004, and an exact comparison with the $55.98 on screen fails.

## Reporting and evidence

*`report/Steps`, `report/HtmlReportListener`, `BaseTest.keepEvidence`*

- Every tap, typed value, swipe and device action is logged by `BaseScreen` and `Device` through
  `Steps.log` (TestNG `Reporter`). Tests log nothing themselves. Passwords are written as `******`.
- The screen is recorded during every test (`adb screenrecord` through Appium, 540x1200 at 1 Mbit/s).
  On success the recording is thrown away; on failure it is saved to `target/artifacts/` together with a
  screenshot taken at the moment of failure.
- `HtmlReportListener` writes `target/surefire-reports/appium-test-report.html`: summary, device and app,
  tests grouped by class, steps per test, and for failures the error, the screenshot in a phone frame and
  the video. It is registered through `META-INF/services`, so it runs from Maven and from the IDE.
- `recordVideo=false` turns recording off.

## Retries: only for device problems

*`RetryOnDeviceProblem`, `RetryTransformer`*

A loaded emulator sometimes freezes the app long enough for Android to show "My Demo App isn't
responding" (ANR). That says nothing about the app's logic, so the test is run once more, after closing the
dialog. Everything else, including every assertion failure, is never retried: blanket retries hide real
bugs and flaky tests. The first attempt stays in the report as skipped with a step explaining the retry.

## Known app bugs

*`KnownBugTest`, `known-bugs.xml`*

Writing the tests turned up three defects in My Demo App 2.3.0 (details in the class Javadoc). Each has a
test that asserts the **correct** behaviour, so it fails today and starts passing when the bug is fixed:
nobody has to remember to come back to it. They are excluded from `testng.xml` to keep the regular run a
clear signal, and the regular tests avoid the crash by using the first six products.

## Configuration

*`Config`, `config.properties`*

One properties file; any key can be overridden with `-Dkey=value` (system properties win). CI changes the
device or turns recording off without code changes.

## What is deliberately not here

- **iOS.** The demo app exists for iOS too, but running it needs a Mac with Xcode. The screen layer is the
  place to add it (`AppiumBy.iOSClassChain` or accessibility ids, which both platforms share).
- **Opening the menu with an edge swipe.** On Android 15 a swipe from the screen edge is the system back
  gesture, so it leaves the app instead.
- **Deep links and WebView context switching.** The app has no deep links in its manifest, and the WebView
  screen would need a matching Chromedriver on every machine.
