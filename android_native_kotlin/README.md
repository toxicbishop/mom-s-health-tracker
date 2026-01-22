# Mom Health Tracker - Native Android (Kotlin)

This folder contains the Kotlin source code to replicate the Mom Health Tracker app functionality using Native Android and Jetpack Compose.

## How to Run

1. **Install Android Studio**.
2. **Create a New Project**:
   - Select "Empty Activity" (ensuring it uses Jetpack Compose).
   - Name it "MomHealthTracker".
3. **Add Dependencies**:
   Open `app/build.gradle.kts` and ensure you have these dependencies (versions may vary):
   ```kotlin
   implementation("androidx.compose.ui:ui:1.6.0")
   implementation("androidx.compose.material3:material3:1.2.0")
   implementation("androidx.navigation:navigation-compose:2.7.6")
   implementation("com.squareup.retrofit2:retrofit:2.9.0")
   implementation("com.squareup.retrofit2:converter-gson:2.9.0")
   implementation("androidx.datastore:datastore-preferences:1.0.0")
   // For charts (optional, or use a library like Vico or MPAndroidChart)
   // implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") 
   ```
4. **Copy Code**:
   - Copy the content of `MainActivity.kt` from this folder into your project's `app/src/main/java/com/example/momhealthtracker/MainActivity.kt`.
   - *Note*: You may need to adjust the `package` declaration at the top of the file to match your project.

## Architecture Differences

- **UI**: Replaces React Native Views/Text with Jetpack Compose `Box`, `Column`, `Text`.
- **State**: Uses `ViewModel` and `StateFlow` instead of `useState` and `Context`.
- **Navigation**: Uses `NavHost` instead of `React Navigation`.
- **Networking**: Uses `Retrofit` instead of `fetch`.
- **Local Storage**: Uses `DataStore` instead of `AsyncStorage`.
