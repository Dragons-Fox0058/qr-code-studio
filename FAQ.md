# Frequently Asked Questions

## Where is the Android project?

The Gradle project is under `app/`. The application module is `app/app/`.

## Which Android versions are configured?

The current module configuration sets minimum SDK 24 and target SDK 36. Device compatibility should still be verified with tests and real devices.

## Which technologies does the project use?

The build configuration includes Kotlin, Jetpack Compose, Material 3, CameraX, ZXing, Room, Retrofit, Moshi, coroutines, and Firebase libraries. A declared dependency is not proof that every related feature is enabled in the running application.

## Where are dependency versions managed?

In `app/gradle/libs.versions.toml`.

## How do I report a bug?

Open a GitHub issue and use the Bug Report template. Include reproducible steps, expected and actual results, and relevant Android/device details. Do not include secrets or private user data.

## How do I request a feature?

Use the Feature Request issue template. Describe the problem to solve and the benefit, rather than prescribing only an implementation.

## How should I report a security vulnerability?

Do not publish exploitable details in a public issue. Follow the private reporting instructions in [SECURITY.md](SECURITY.md).

## Where can I find support?

See [SUPPORT.md](SUPPORT.md) for support and issue-reporting guidance.
