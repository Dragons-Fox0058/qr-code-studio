# Testing Guide

The Android module includes dependencies for local unit tests, Android instrumentation tests, Robolectric, and screenshot testing. The presence of test dependencies does not mean every test category is currently implemented or passing.

## Local checks

The repository currently does not include the Gradle wrapper scripts. Install Gradle 9.3.1 locally or use Android Studio's configured Gradle installation. Run these commands from the repository's `app/` directory:

```bash
# Unit tests for the debug variant
gradle :app:testDebugUnitTest

# Android lint for the debug variant
gradle :app:lintDebug

# Assemble a debug APK
gradle :app:assembleDebug
```

Use the installed `gradle` command from PowerShell or Command Prompt on Windows.

## Instrumentation tests

Instrumentation tests require an Android emulator or connected device with a compatible API level:

```bash
./gradlew :app:connectedDebugAndroidTest
```

## Review checklist

- Test the workflow changed by the pull request.
- Check permission-denied and permission-granted paths when relevant.
- Check both light and dark appearance when UI styling changes.
- Test on more than one Android API level when practical.
- Do not include personal QR contents, API keys, or private logs in test artifacts.

## Reporting results

In the pull request description, list the exact commands run and whether they passed. If a check could not run because of missing SDK components, emulator availability, credentials, or signing configuration, say so rather than implying it passed.
