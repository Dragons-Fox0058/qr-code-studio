# Architecture

This document describes the repository structure and the architectural boundaries visible in the current project configuration. It intentionally avoids claiming implementation details that are not established by the checked-in configuration.

## Repository layout

- `app/` — Android Studio/Gradle project.
- `app/app/` — Android application module.
- `app/gradle/libs.versions.toml` — centralized dependency and plugin versions.
- `Screenshots/` — project screenshots and visual references.
- `docs/` — contributor and project-maintenance guides.

## Android application

The Android project uses Kotlin and Jetpack Compose. The application module is configured with:

- Android Gradle Plugin and Kotlin Compose plugin.
- Android SDK compile/target API 36 and minimum API 24.
- Java source and target compatibility 11.
- Jetpack Compose UI and Material 3.
- CameraX libraries for camera integration.
- ZXing Core for QR/barcode-related functionality.
- Room, Retrofit, Moshi, and Kotlin coroutines dependencies.
- Firebase libraries, including Firebase AI and App Check.

The presence of a dependency does not necessarily mean every feature is currently active. Consult the module source and Gradle configuration before relying on a specific runtime data flow.

## Build configuration

The Gradle project is rooted inside `app/`. The root build script and settings file are therefore located at:

- `app/build.gradle.kts`
- `app/settings.gradle.kts`
- `app/gradle/libs.versions.toml`
- `app/gradle/wrapper/gradle-wrapper.properties`

The application module is `app/app/`, with its own `build.gradle.kts`.

## Android entry points and permissions

The manifest declares the launcher activity, text and image sharing intents, a non-exported FileProvider, and camera, vibration, notification, internet, and network-state permissions. The camera hardware feature is optional.

Review the Android manifest and runtime permission handling together when changing permission-related behavior. A manifest declaration alone does not establish when or why the app requests a permission.

## Tests

The module configuration includes JUnit, AndroidX test libraries, Robolectric, Compose UI testing, and Roborazzi dependencies. Keep tests close to the behavior they verify and run the relevant Gradle test tasks before proposing a release.

## Architectural change guidelines

1. Keep UI, state, persistence, networking, and platform integration responsibilities clearly separated.
2. Avoid introducing credentials or private keys into source control.
3. Centralize dependency versions in the version catalog.
4. Treat permissions and external data processing as security-sensitive.
5. Update this document when the actual module boundaries or major integrations change.
