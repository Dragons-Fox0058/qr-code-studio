# Troubleshooting

Try the relevant steps below before opening an issue. Menu names may differ by Android version and device manufacturer.

## APK will not install

- Confirm that the device runs Android 7.0 (API 24) or newer.
- Download the APK from the official [GitHub Releases](https://github.com/Dragons-Fox0058/qr-code-studio/releases) page.
- Check that the download completed and that the file is an APK for this project.
- If Android reports a package signature conflict, do not uninstall an existing app until you have considered whether local data needs to be preserved.

## Android blocks installation

Android may require permission for the browser or file manager to install apps from that source. Grant it only when you intentionally install the official release APK, then revoke it if you no longer need it.

## Camera scanning does not work

- Check that the device has a camera.
- Grant camera permission to QR Code Studio in Android settings.
- Close other apps that may be using the camera.
- Restart the app and try again in adequate lighting.
- If the problem continues, report the device model, Android version, and reproduction steps.

## Gradle sync or build fails

- Open the \`app/\` directory as the Android Studio project.
- Confirm that Android Studio is using JDK 17 or newer.
- Install the SDK components requested by Gradle.
- Check network access to the configured dependency repositories.
- Review local secret and signing configuration without sharing its values publicly.

## Tests cannot run

Instrumentation tests need a compatible emulator or connected device. Some build tasks may also require SDK packages or local configuration. Include the exact command and relevant redacted error output when asking for help.

## Still stuck?

Search [existing issues](https://github.com/Dragons-Fox0058/qr-code-studio/issues) or open a new one using the appropriate template. Remove API keys, passwords, personal information, and private QR contents from logs and screenshots.
