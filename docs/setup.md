# Setup guide: installation, device, Appium Inspector

Everything needed to run this framework on a Windows machine **without Android Studio**, from an
empty machine to a green test run. Each step ends with a check, so you know it worked before moving on.
Commands are for **PowerShell** unless marked *bash*.

Versions used when this guide was written (October 2026):

| Tool | Version |
|---|---|
| JDK | 17 or newer (tested on 27) |
| Node.js / npm | 24 / 11 |
| Android SDK command-line tools | 23.0 |
| Android platform-tools (`adb`) / emulator | 37.0.1 / 37.2.12 |
| Android image | Android 15 (API 35), Google APIs, x86_64 |
| Appium / UiAutomator2 driver | 3.8.0 / 8.7.0 |
| Appium Inspector (plugin) | 2026.9.2 |

---

## 1. Installation

### 1.1 Java and Node.js

- **JDK 17+**: any distribution (Temurin, Corretto, OpenJDK). IntelliJ can download one under
  *Project Structure > SDKs*.
- **Node.js 24 LTS** from [nodejs.org](https://nodejs.org) (Appium is a Node application).
- **Maven**: IntelliJ's bundled Maven is enough; on the command line use `mvn` if installed.

Check:

```powershell
java -version
node -v
npm -v
```

### 1.2 Android SDK (command-line tools only)

1. Download *Command line tools only* for Windows from
   [developer.android.com/studio](https://developer.android.com/studio#command-line-tools-only).
2. Unzip it so the tools end up in this exact folder (the `latest` level matters):

   ```text
   %LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat
   ```

3. Accept the Android SDK licenses (read them; answer `y`):

   ```powershell
   $sdk = "$env:LOCALAPPDATA\Android\Sdk"
   & "$sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root=$sdk --licenses
   ```

4. Install the packages:

   ```powershell
   & "$sdk\cmdline-tools\latest\bin\sdkmanager.bat" --sdk_root=$sdk `
       "platform-tools" "emulator" "platforms;android-35" "build-tools;35.0.1" `
       "system-images;android-35;google_apis;x86_64"
   ```

   About 3 GB. In *bash*, write the package names with `/` instead of `;`
   (`platforms/android-35`): bash passes `;` to the `.bat` file, which splits the name.

### 1.3 Environment variables

Appium finds the SDK and Java through these. Set them for your user (no admin rights needed):

```powershell
$sdk = "$env:LOCALAPPDATA\Android\Sdk"
$jdk = "C:\path\to\your\jdk"          # e.g. C:\Users\<you>\.jdks\openjdk-27
[Environment]::SetEnvironmentVariable('JAVA_HOME', $jdk, 'User')
[Environment]::SetEnvironmentVariable('ANDROID_HOME', $sdk, 'User')
$path = [Environment]::GetEnvironmentVariable('Path', 'User')
[Environment]::SetEnvironmentVariable('Path', "$path;$jdk\bin;$sdk\platform-tools;$sdk\emulator;$sdk\cmdline-tools\latest\bin", 'User')
```

**Close and reopen every terminal and IntelliJ** afterwards; running programs keep the old values.

Check (in a new terminal):

```powershell
echo $env:ANDROID_HOME
adb version
emulator -version
```

### 1.4 Appium, the Android driver and the Inspector

```powershell
npm install -g appium
appium driver install uiautomator2
appium plugin install inspector
```

Check:

```powershell
appium --version
appium driver list --installed     # uiautomator2 [installed]
appium plugin list --installed     # inspector [installed]
appium driver doctor uiautomator2  # "0 required fixes needed"
```

The doctor also lists optional fixes (`bundletool.jar`, `ffmpeg`, GStreamer). They are only needed for
`.aab` bundles, screen recording and audio/video streaming; this framework uses none of them.

### 1.5 The app under test

```powershell
powershell -ExecutionPolicy Bypass -File scripts\get-app.ps1
```

or in *bash*: `scripts/get-app.sh`. The APK goes to `apps/` (not committed).
`-ExecutionPolicy Bypass` applies to this one command only; Windows blocks unsigned scripts by default.

---

## 2. The device

### 2.1 Create the emulator (once)

```powershell
avdmanager create avd -n Pixel_API_35 -k "system-images;android-35;google_apis;x86_64" -d pixel_7
emulator -list-avds     # Pixel_API_35
```

If `avdmanager` says *"Java version 17 or higher is required"* although you have a newer JDK, it
misreads the version number. Run `$env:SKIP_JDK_VERSION_CHECK = '1'` first.

### 2.2 Start it

```powershell
emulator -avd Pixel_API_35 -no-snapshot-save -no-boot-anim
```

Leave that window open. `-no-snapshot-save` starts from the same clean state every time.
Without a screen (CI, or to save resources) add `-no-window`.

Check that Android has finished booting (prints `1`; the first boot takes about a minute):

```powershell
adb devices                            # emulator-5554   device
adb shell getprop sys.boot_completed   # 1
```

Useful while working:

| Command | What it does |
|---|---|
| `adb shell cmd statusbar collapse` | closes the notification shade if it covers the app after the first boot |
| `adb install -r apps\mda-2.3.0-27.apk` | installs or updates the app by hand |
| `adb shell pm clear com.saucelabs.mydemoapp.android` | wipes the app's data (what the tests do before each test) |
| `adb emu kill` | shuts the emulator down |

The emulator window is resizable: drag a corner if it is taller than your screen.

### 2.3 A real phone instead (optional)

1. On the phone: *Settings > About phone*, tap *Build number* 7 times, then
   *Developer options > USB debugging* on.
2. Connect it by USB and accept the "Allow USB debugging?" prompt on the phone.
3. `adb devices` shows its serial number. Run the tests with that serial and the phone's Android version:

   ```powershell
   mvn test "-Dudid=<serial>" "-DplatformVersion=14"
   ```

---

## 3. Appium server and Appium Inspector

### 3.1 Start the server

```powershell
appium --use-plugins=inspector --allow-cors
```

Leave it running. It listens on `http://127.0.0.1:4723`, which is `appiumUrl` in
`src/main/resources/config.properties`. For the tests alone, plain `appium` is enough.

Check: [http://127.0.0.1:4723/status](http://127.0.0.1:4723/status) answers `"ready": true`.

### 3.2 Open the Inspector

Appium Inspector replaces the old *Appium Desktop* app. With the plugin it runs in the browser:

1. Open [http://localhost:4723/inspector](http://localhost:4723/inspector).
2. Leave *Remote Host* `127.0.0.1`, *Port* `4723`, *Path* `/`.
3. In *JSON Representation* click the pencil, paste and save:

   ```json
   {
     "platformName": "Android",
     "appium:automationName": "UiAutomator2",
     "appium:deviceName": "Pixel_API_35",
     "appium:appPackage": "com.saucelabs.mydemoapp.android",
     "appium:appActivity": "com.saucelabs.mydemoapp.android.view.activities.SplashActivity",
     "appium:appWaitActivity": "*",
     "appium:noReset": true
   }
   ```

4. Click **Start Session**. The left side shows the phone screen, the middle the element tree,
   the right the selected element's attributes and suggested locators.

(Prefer a separate window? The same Inspector exists as a desktop app on its
[GitHub releases page](https://github.com/appium/appium-inspector/releases).)

### 3.3 Finding a locator

Click an element on the screenshot, then read the attributes on the right. Pick in this order:

| Order | Attribute | In code | Why |
|---|---|---|---|
| 1 | `resource-id` | `AppiumBy.id("com.saucelabs.mydemoapp.android:id/productTV")` | set by the developers, stable, fast |
| 2 | `content-desc` | `AppiumBy.accessibilityId("View cart")` | stable, works on iOS too, and checks accessibility |
| 3 | text via UiAutomator | `AppiumBy.androidUIAutomator("new UiSelector().text(\"Products\")")` | for items with no id; changes with the language |
| 4 | XPath | `AppiumBy.xpath("//...")` | last resort: slow on mobile and breaks when the layout changes |

The *Search for element* button (magnifier) tries a locator on the live screen before you put it in code.
Elements below the fold are not in the tree until you scroll: use the swipe tool, then *Refresh*.

**Close the Inspector session (*Quit Session*) before running the tests**: an open Inspector
session holds the UiAutomator2 server on the device and the tests' session would replace it.

---

## 4. Run the tests

With the emulator and Appium running:

```powershell
mvn test                       # the whole suite (testng.xml)
mvn test "-Dgroups=smoke"      # only tests in the smoke group
```

In IntelliJ: right-click `testng.xml` > *Run*, or the green arrow next to a test.

---

## 5. Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `Neither ANDROID_HOME nor ANDROID_SDK_ROOT environment variable was exported` | Appium was started from a terminal opened before the variables were set. Close it, open a new one, start `appium` again. |
| `listen EADDRINUSE ... 4723` | An older Appium is still running. Find it with `netstat -ano \| findstr :4723` and stop that process (or use `--port 4724` and `-DappiumUrl=http://127.0.0.1:4724`). |
| `App not found at ...apps\mda-...apk` | Run `scripts\get-app.ps1` (section 1.5). |
| A test cannot find the first element and the screenshot shows the notification shade | First boot left it open: `adb shell cmd statusbar collapse`. |
| `adb devices` lists nothing | The emulator is still booting, or it was closed. Start it (2.2) and wait for `sys.boot_completed` = 1. |
| `running scripts is disabled on this system` | Use `powershell -ExecutionPolicy Bypass -File scripts\get-app.ps1`. |
| `avdmanager`: `Java version 17 or higher is required` | `$env:SKIP_JDK_VERSION_CHECK = '1'` (see 2.1). |
| `sdkmanager`: *"Package system-images not found"* in bash | Use `/` instead of `;` in package names (see 1.2). |
