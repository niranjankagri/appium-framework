# Manual test cases

The test cases this framework automates on Sauce Labs' **My Demo App** (Android), written as a tester
would run them by hand on a phone or emulator. Every case has an ID; the TestNG method that covers it
starts with the same ID (`tc116_guestCompletesCheckout`) and so does its description
(`[TC-116] A guest logs in during checkout ...`), so a failure in the report leads straight to the
case below.

Not listed: the interview tests that show an Appium technique or a device command without testing the
app (five locator strategies, a tap at coordinates, element screenshot, page source, FluentWait,
Android key events, app state, device info, clipboard, push/pull a file, logcat).

## Setup

- My Demo App 2.3.0 (`apps/mda-2.3.0-27.apk`, see `docs/setup.md`) on Android 15 (Pixel, API 35).
  Before each case: clear the app's data (Settings › Apps › My Demo App › Storage › Clear storage) or
  reinstall: no one is logged in and the cart is empty.
- Login: `bod@example.com` / `10203040`; locked user `alice@example.com`.
- Products: Sauce Labs Backpack, Sauce Labs Backpack (yellow) `$29.99` (below the first screen), Sauce
  Labs Bike Light (7th product).
- Address: Niranjan Agri, Main Street 1, Berlin, 10115, Germany (line 2 and state empty).
  Card: Niranjan Agri, `3258 1256 7568 7891`, expiry `03/30`, security code `123`. Delivery fee `$5.99`.

## Summary

| ID | Title | Type | Priority |
|---|---|---|---|
| TC-101 | The app opens on the product catalog | Smoke | High |
| TC-102 | A valid user logs in; the menu offers Log Out | Smoke | High |
| TC-103 | A locked-out user is refused with a clear message | Negative | High |
| TC-104 | Login without a user name asks for one | Negative | Medium |
| TC-105 | Login without a password asks for one | Negative | Medium |
| TC-106 | Logging out returns to the login form | Regression | High |
| TC-107 | Sort by name A-Z | Regression | Low |
| TC-108 | Sort by name Z-A | Regression | Low |
| TC-109 | Sort by price low-high | Regression | Medium |
| TC-110 | Sort by price high-low | Regression | Medium |
| TC-111 | A product further down the list opens with its own name and price | Smoke | High |
| TC-112 | Adding a product shows 1 on the badge and in the cart | Smoke | High |
| TC-113 | Quantity 3 adds three items and triples the total | Regression | High |
| TC-114 | Two different products: both in the cart, right total | Regression | High |
| TC-115 | Removing the only item empties the cart | Regression | Medium |
| TC-116 | A guest logs in during checkout and completes the order | Smoke / E2E | High |
| TC-117 | The address form names every missing required field | Negative | High |
| TC-118 | The optional address fields can stay empty | Regression | Medium |
| TC-119 | Android back button returns from a product to the catalog | Mobile | High |
| TC-120 | Screen and cart survive the app going to the background | Mobile / Smoke | High |
| TC-121 | A cold start opens on the catalog | Mobile | Medium |
| TC-122 | The app stays in portrait | Mobile | Low |
| TC-123 | The keyboard opens for a text field and can be closed | Mobile | Medium |
| TC-124 | KNOWN BUG: opening the 7th product (Bike Light) | Known bug | High |
| TC-125 | KNOWN BUG: product quantity must not go below 1 | Known bug | Medium |
| TC-126 | KNOWN BUG: the missing-country message must be complete | Known bug | Low |
| TC-127 | Drawing on the signature pad, then clearing it | Regression | Low |
| TC-128 | Add to cart button: text, accessibility label, fully on screen | Accessibility | Medium |
| TC-129 | A revoked location permission is asked for again | Mobile / Permissions | Medium |
| TC-130 | The WebView screen opens a website inside the app | Regression | Low |
| TC-131 | A mocked GPS position appears on the Geo Location screen | Mobile | Low |
| TC-132 | The app is usable again after the notification shade | Mobile / Interruption | Low |

TC-124 to TC-126 describe the **correct** behaviour. The app currently fails them (bugs found on
2026-10-12); they run in their own suite (`known-bugs.xml`, group `known-bug`) and stay red until the
bugs are fixed.

---

## Start and login (`SmokeTest`, `LoginTest`)

### TC-101 The app opens on the product catalog

| # | Step | Expected result |
|---|---|---|
| 1 | Start the app | After the splash screen: title "Products", products listed, cart badge empty |

### TC-102 A valid user logs in; the menu offers Log Out

| # | Step | Expected result |
|---|---|---|
| 1 | Menu (☰) › **Log In**; enter `bod@example.com` / `10203040`, tap **Login** | Back on the catalog |
| 2 | Open the menu | It offers **Log Out** |

### TC-103 A locked-out user is refused with a clear message

| # | Step | Expected result |
|---|---|---|
| 1 | Log in as `alice@example.com` / `10203040` | Under the password field: "Sorry this user has been locked out." |

### TC-104 Login without a user name asks for one

| # | Step | Expected result |
|---|---|---|
| 1 | Empty user name, password `10203040`, tap **Login** | "Username is required" |

### TC-105 Login without a password asks for one

| # | Step | Expected result |
|---|---|---|
| 1 | User `bod@example.com`, empty password, tap **Login** | "Enter Password" |

### TC-106 Logging out returns to the login form

| # | Step | Expected result |
|---|---|---|
| 1 | Log in (TC-102); menu › **Log Out**, confirm the dialog | Login form |
| 2 | Open the menu | It offers **Log In** again |

## Catalog (`CatalogTest`)

### TC-107 to TC-110 Sort the catalog

Tap the sort button, choose the option, read the products on the screen.

| ID | Option | Expected result |
|---|---|---|
| TC-107 | Name - Ascending | Names A-Z |
| TC-108 | Name - Descending | Names Z-A |
| TC-109 | Price - Ascending | Prices low to high (compared as numbers) |
| TC-110 | Price - Descending | Prices high to low |

### TC-111 A product further down the list opens with its own name and price

| # | Step | Expected result |
|---|---|---|
| 1 | Scroll down, tap "Sauce Labs Backpack (yellow)" | Product screen with that name and `$29.99` |

## Cart (`CartTest`)

### TC-112 Adding a product shows 1 on the badge and in the cart

| # | Step | Expected result |
|---|---|---|
| 1 | Open Sauce Labs Backpack, tap **Add to cart** | Cart badge `1` |
| 2 | Open the cart | One line, Sauce Labs Backpack; total = the product's price |

### TC-113 Quantity 3 adds three items and triples the total

| # | Step | Expected result |
|---|---|---|
| 1 | Open the Backpack, tap **+** twice | Quantity `3`; the screen still shows the unit price |
| 2 | **Add to cart** | Badge `3` |
| 3 | Open the cart | "3 items"; total = 3 × unit price, to the cent |

### TC-114 Two different products: both in the cart, right total

| # | Step | Expected result |
|---|---|---|
| 1 | Add the Backpack; back; scroll to the yellow Backpack, add it | |
| 2 | Open the cart | Backpack, then yellow Backpack; "2 items"; total = sum of both prices |

### TC-115 Removing the only item empties the cart

| # | Step | Expected result |
|---|---|---|
| 1 | Add the Backpack, open the cart, tap **Remove item** | Badge empty; no **Proceed To Checkout** button |

## Checkout (`CheckoutTest`)

### TC-116 A guest logs in during checkout and completes the order

| # | Step | Expected result |
|---|---|---|
| 1 | Add the Backpack, open the cart, note the total, **Proceed To Checkout** | The app asks for a login |
| 2 | Log in as `bod@example.com` | Address form |
| 3 | Enter the address, **To Payment**; enter the card, **Review Order** | Review screen: the Backpack, ship to "Niranjan Agri", 1 item |
| 4 | Check the total | Cart total + `$5.99` delivery |
| 5 | **Place Order** | "Checkout Complete", "Thank you for your order" |
| 6 | **Continue Shopping** | Cart badge empty |

### TC-117 The address form names every missing required field

| # | Step | Expected result |
|---|---|---|
| 1 | Go to the address form as in TC-116, submit it empty | Still on the form, five messages in form order: "Please provide your full name.", "Please provide your address.", "Please provide your city.", "Please provide your zip", and one starting "Please provide your" for the country (see TC-126) |

### TC-118 The optional address fields can stay empty

| # | Step | Expected result |
|---|---|---|
| 1 | Fill the address form without line 2 and state, **To Payment** | The payment form opens |

## Device behaviour (`MobileTest`)

### TC-119 Android back button returns from a product to the catalog

| # | Step | Expected result |
|---|---|---|
| 1 | Open the Backpack, press the system **Back** button | "Products" |

### TC-120 Screen and cart survive the app going to the background

| # | Step | Expected result |
|---|---|---|
| 1 | Open the Backpack, add it to the cart | Badge `1` |
| 2 | Press **Home**, wait 3 seconds, switch back to the app | Same product screen; badge still `1` |

### TC-121 A cold start opens on the catalog

| # | Step | Expected result |
|---|---|---|
| 1 | Open a product; force-stop the app (Settings or recent apps), start it again | "Products" (the last screen is not restored) |

### TC-122 The app stays in portrait

| # | Step | Expected result |
|---|---|---|
| 1 | Turn auto-rotate on, turn the device to landscape | The app stays in portrait and keeps working |

### TC-123 The keyboard opens for a text field and can be closed

| # | Step | Expected result |
|---|---|---|
| 1 | Menu › **Log In**, tap the user name field | The keyboard opens |
| 2 | Close it (Back) | Keyboard gone; the **Login** button is visible |

## Known bugs (`KnownBugTest`, suite `known-bugs.xml`)

### TC-124 Opening the 7th product (Bike Light)

| # | Step | Expected result | Actual (bug) |
|---|---|---|---|
| 1 | Scroll to Sauce Labs Bike Light, tap it | Its product screen | The app closes (`ArrayIndexOutOfBoundsException: length=6; index=6` in logcat) |

### TC-125 Product quantity must not go below 1

| # | Step | Expected result | Actual (bug) |
|---|---|---|---|
| 1 | Open the Backpack (quantity 1), tap **−** | Quantity stays `1` | It drops to `0` |

### TC-126 The missing-country message must be complete

| # | Step | Expected result | Actual (bug) |
|---|---|---|---|
| 1 | Submit the empty address form (TC-117) | Last message "Please provide your country." | "Please provide your" (cut off) |

## More app features (`interview/AppiumScenariosTest`, `interview/DeviceApiTest`)

### TC-127 Drawing on the signature pad, then clearing it

| # | Step | Expected result |
|---|---|---|
| 1 | Menu › **Drawing**, draw a check mark with a finger | The line appears on the pad |
| 2 | Tap **Clear** | The pad changes back (line removed) |

### TC-128 Add to cart button: text, accessibility label, fully on screen

| # | Step | Expected result |
|---|---|---|
| 1 | Open the Backpack; inspect **Add to cart** (Appium Inspector or TalkBack) | Text "Add to cart"; TalkBack reads "Tap to add product to cart"; enabled, clickable, completely inside the screen |

### TC-129 A revoked location permission is asked for again

| # | Step | Expected result |
|---|---|---|
| 1 | Settings › Apps › My Demo App › Permissions › Location › **Don't allow** | Permission revoked (the app restarts) |
| 2 | Start the app, menu › **Geo Location** | Android's permission dialog appears |
| 3 | Choose **While using the app** | The permission is granted again |

### TC-130 The WebView screen opens a website inside the app

| # | Step | Expected result |
|---|---|---|
| 1 | Menu › **WebView**, enter `https://www.saucedemo.com`, **Go To Site** | The site loads inside the app (a web view in the native app) |

### TC-131 A mocked GPS position appears on the Geo Location screen

| # | Step | Expected result |
|---|---|---|
| 1 | Emulator › Extended controls › Location: set 52.52, 13.405 (Berlin), **Set location** | |
| 2 | Menu › **Geo Location**, **Start Observing** | Latitude 52.52, longitude 13.405 (± 0.001) |

### TC-132 The app is usable again after the notification shade

| # | Step | Expected result |
|---|---|---|
| 1 | On the catalog, pull down the notification shade | The shade opens |
| 2 | Press **Back** | The shade closes; the app shows "Products" and works |
