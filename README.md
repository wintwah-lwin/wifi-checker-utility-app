# Wi-Fi Checker

**Wi-Fi Checker tells you whether your connection is good enough for what you are doing right now.**

---

## Overview & Purpose
Wi-Fi Checker is an Android utility application built with **Jetpack Compose** and **Material Design 3**. It tests real-time network speeds, evaluates connection stability, diagnoses bottlenecks across your local network and the internet, and provides personalized smart tips for 11 different online activities (such as video calls, 4K streaming, and competitive gaming).

---

## Core Features

| Feature | What it does | Implementation (File / Class) |
|---|---|---|
| **Speed Gauge** | 270-degree animated ring marking required vs measured speed | [`SpeedGauge.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/check/components/SpeedGauge.kt) |
| **11 Activities** | Switch between video calls, gaming, 4K streaming, downloads, etc. | [`Usage.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/model/Usage.kt) |
| **Diagnosis Path** | Traces connection health across Phone ➔ Router ➔ Internet | [`PathCard.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/check/components/PathCard.kt) |
| **Smart Tips** | Actionable troubleshooting advice based on bottleneck metrics | [`TipEngine.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/logic/TipEngine.kt) |
| **Activity Estimates** | Calculates download time, file upload time, and max video tier | [`EstimateEngine.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/logic/EstimateEngine.kt) |
| **Data Saver** | Restricts heavy data tests on metered cellular connections | [`Settings.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/model/Settings.kt) |
| **Stale-Result Detection** | Dims results and warns if the phone connects to a different network | [`UtilityViewModel.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/check/UtilityViewModel.kt) |
| **Permission Flow** | Requests fine location permission dynamically to read Wi-Fi SSID | [`UtilityScreen.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/check/UtilityScreen.kt) |
| **Haptics** | Tactile feedback vibration on test completion | [`UtilityScreen.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/check/UtilityScreen.kt) |
| **Settings** | Customizes picky scale, test depth, data saver, and speed units | [`SettingsScreen.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/ui/settings/SettingsScreen.kt) |

---

## Architecture & Design

1. **UI Layer (Presentation)**:
   - Built entirely with **Jetpack Compose** and **Material Design 3**.
   - Composables (`UtilityScreen`, `SettingsScreen`) observe reactive `StateFlow` streams emitted by ViewModels (`UtilityViewModel`, `SettingsViewModel`).
   - ViewModels survive configuration changes (such as screen rotation) and manage business logic execution.

2. **Domain Layer (Business Logic)**:
   - Contains pure Kotlin business rules, models (`Measurement`, `LinkInfo`, `Verdict`, `Settings`), and specialized calculation engines:
     - [`VerdictCalculator.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/logic/VerdictCalculator.kt): Determines connection grade (`GOOD`, `STRUGGLES`, `POOR`) using a ratio rule against activity requirements.
     - [`DiagnosisEngine.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/logic/DiagnosisEngine.kt): Analyzes signal strength and latency to pinpoint network bottlenecks (Phone vs Router vs Internet).
     - [`TipEngine.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/domain/logic/TipEngine.kt): Generates targeted optimization advice.

3. **Data Layer**:
   - **Repositories**: `ConnectionRepository` and `SettingsRepository` act as single sources of truth for network data and user preferences.
   - **Remote Testing**: [`RetrofitNetworkTester.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/data/remote/RetrofitNetworkTester.kt) uses **Retrofit** and **OkHttp** to stream download/upload payloads against Cloudflare's public speed test endpoints (`speed.cloudflare.com`).
   - **Device Integration**: [`ConnectionInfoProvider.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/data/device/ConnectionInfoProvider.kt) utilizes Android's `ConnectivityManager.NetworkCallback` and `WifiManager` to track Wi-Fi/cellular state and RSSI signal strength.

4. **Manual Dependency Injection**:
   - [`AppContainer.kt`](app/src/main/java/au/edu/jcu/assessment/utilityapp/di/AppContainer.kt) instantiates and wires singletons and repositories across the app without requiring heavy third-party dependency injection frameworks.

---

## How It Works

The verdict evaluates network performance against the chosen activity's minimum requirements using a **ratio rule**:
1. Ratios are calculated for Download, Upload, Latency, and Stability against required thresholds scaled by the user's **Picky** preference (`RELAXED = 0.7`, `NORMAL = 1.0`, `STRICT = 1.5`).
2. The worst ratio dictates the overall grade and identifies the limiting metric:
   - **`>= 1.0`**: **Good** (All requirements met with room to spare)
   - **`0.6 – 0.99`**: **Struggles** (Near the threshold limit)
   - **`< 0.6`**: **Poor** (Insufficient bandwidth or high latency)

---

## How to Install

1. **Download the APK**:
   - Obtain the pre-built `app-debug.apk` file (located in `app/build/outputs/apk/debug/app-debug.apk` after building, or from project releases).
2. **Transfer to Your Phone**:
   - Send the `.apk` file to your Android phone via USB cable, Google Drive, email, or messaging app.
3. **Allow Installation from Unknown Sources**:
   - Tap on the downloaded APK file on your phone.
   - If prompted, go to **Settings > Security / Install Unknown Apps** and allow your file manager (e.g., Files by Google) to install apps.
4. **Install and Launch**:
   - Tap **Install**. Once installed, tap **Open** to launch **Wi-Fi Checker** directly on your device.

---

## For Developers: How to Run in Android Studio

1. Open **Android Studio**.
2. Clone or download this project and open the root folder.
3. Let Gradle sync dependencies.
4. Run the app on an emulator or physical device running **API 26+** (Android 8.0 or higher).

---

## APIs & Libraries

- [Retrofit & OkHttp](https://square.github.io/retrofit/): HTTP client for streaming download and upload payloads against Cloudflare public speed test endpoints (`speed.cloudflare.com/__down` & `__up`).
- [ConnectivityManager.NetworkCallback](https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback): Real-time network state observation.
- [WifiManager](https://developer.android.com/reference/android/net/wifi/WifiManager): Reads RSSI signal strength and SSID.
- [Activity Result API](https://developer.android.com/training/permissions/requesting): Modern permission request flow for fine location access.
- [Compose Haptics](https://developer.android.com/reference/kotlin/androidx/compose/ui/hapticfeedback/HapticFeedback): Tactile vibration feedback.

---
