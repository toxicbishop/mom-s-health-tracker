# Mom Health Tracker - Native Android (Kotlin)

A professional health tracking application designed for expecting mothers, built with **Android Native (Kotlin)** and **Jetpack Compose**.

## Features

### 🔐 Authentication & Security
- **Secure Sign In/Up**: Dark-themed login screens with modern aesthetics.
- **PIN Protection**: Quick access via PIN verification.
- **Dark/Light Mode**: Full theme support with a toggle in Profile settings.

### 📊 Health Dashboard
- **Vitals Tracking**: Log Weight and Blood Pressure with ease.
- **Progress Indicators**: Visual trackers for health goals.
- **Quick Logging**: Easy access buttons for daily logs.

### 💊 Medication Management
- **Meds Tab**: dedicated screen for managing prescriptions (e.g., Metformin).
- **Adherence Tracking**: "Today's Progress" bars and completion stats.
- **Reminders**: Custom schedules (Daily/Weekly) with alarm toggles.

### 📈 Analytics & History
- **Trends**: Visual weight graphs and blood pressure analysis.
- **Insights**: Auto-generated health insights based on your data patterns.
- **Calendar/History**: Detailed log history grouped by date (Today/Yesterday).

### ⚙️ Profile
- **Personal Settings**: manage account info.
- **Theme Toggle**: Switch between Professional Light and Slate Dark modes.

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material3)
- **Navigation**: Jetpack Navigation Compose
- **Networking**: Retrofit + Gson
- **Architecture**: MVVM (Model-View-ViewModel)

## How to Run

1. **Prerequisites**: Android Studio Hedgehog or newer.
2. **Open Project**: Open the android project folder in Android Studio.
3. **Sync Gradle**: Ensure all dependencies (`retrofit`, `navigation-compose`, etc.) are downloaded.
4. **Run**: Select an emulator or physical device and click **Run**.

## Project Structure
- `MainActivity.kt`: Contains the entry point and all Composable screens (Login, Home, Trends, Meds, Profile).
- `Theme Engine`: Custom `AppTheme` with swappable `LightPalette` and `DarkPalette`.
