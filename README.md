# Appium Framework

Android mobile test automation with **Appium 3**, **Java 17** and **TestNG**, written against the
[Sauce Labs My Demo App](https://github.com/saucelabs/my-demo-app-android).

Work in progress: the framework is built step by step, one commit per part.

## Quick start

Full guide: **[docs/setup.md](docs/setup.md)** (installation, starting the device, Appium Inspector,
troubleshooting). With the Android SDK, the `Pixel_API_35` emulator and Appium installed:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\get-app.ps1   # or: scripts/get-app.sh
emulator -avd Pixel_API_35 -no-snapshot-save                   # terminal 1
appium --use-plugins=inspector --allow-cors                    # terminal 2
mvn test                                                       # terminal 3
```

Inspect the app's screens at [http://localhost:4723/inspector](http://localhost:4723/inspector)
(capabilities in [docs/setup.md](docs/setup.md#32-open-the-inspector)).
