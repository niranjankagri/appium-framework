# Appium Framework

Android mobile test automation with **Appium 3**, **Java 17** and **TestNG**, written against the
[Sauce Labs My Demo App](https://github.com/saucelabs/my-demo-app-android).

Work in progress: the framework is built step by step, one commit per part.

## Setup

1. Android SDK with an emulator (`Pixel_API_35`, Android 15), `ANDROID_HOME` set
2. `npm install -g appium` and `appium driver install uiautomator2`
3. Get the app: `scripts/get-app.sh` (or `scripts\get-app.ps1` on Windows)
4. Start the emulator and `appium`, then `mvn test`
