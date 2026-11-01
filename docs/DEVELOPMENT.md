# Development Guide

## Prerequisites

The Android project is under the `app/` directory.

- Android Studio (a recent stable release).
- JDK 17 or newer for the Gradle toolchain.
- Android SDK components required by the project (compile SDK 36).
- An Android emulator or physical device for manual verification.

## Open the project

1. Clone the repository.
2. Open the repository's `app/` directory in Android Studio.
3. Allow Gradle to sync.
4. Resolve SDK or JDK prompts using compatible official Android Studio components.

## Configuration and secrets

The project includes `app/.env.example` as a placeholder template. The Gradle Secrets plugin is configured to read `.env` and use `.env.example` as its defaults file.

- Review the example before configuring local development.
- Add only credentials you are authorized to use.
- Keep real `.env` files, API keys, Firebase configuration secrets, and signing credentials out of Git.
- Never paste secrets into issues, pull requests, screenshots, or logs.
- A placeholder value is not a working API key.

Some services may require separate Firebase or provider-side configuration. A successful Gradle sync alone does not confirm those services are configured.

## Build commands

The repository currently contains Gradle configuration files but does not include the Gradle wrapper scripts (`gradlew` and `gradlew.bat`). Install Gradle 9.3.1 locally, or use Android Studio's configured Gradle installation. Run commands from the repository's `app/` directory:

```bash
# Build a debug APK
gradle :app:assembleDebug

# Run local unit tests
gradle :app:testDebugUnitTest

# Run Android lint checks
gradle :app:lintDebug
```

On Windows, use the installed `gradle` command from PowerShell or Command Prompt. Do not assume `./gradlew` is available until wrapper scripts are added in a separately reviewed change.

The debug build can depend on local signing/configuration files referenced by the Gradle scripts. Do not commit private signing material to work around a local configuration issue.

## Before opening a pull request

- Review `git diff` and confirm that only intended files changed.
- Run the checks relevant to your change.
- Describe checks that could not be run.
- Include screenshots for meaningful UI changes, without exposing private data.
- Follow [CONTRIBUTING.md](../CONTRIBUTING.md).

## Related guides

- [Architecture overview](ARCHITECTURE.md)
- [Testing guide](TESTING.md)
- [Release process](RELEASE_PROCESS.md)
