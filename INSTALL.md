# Installation Guide

This guide covers installing a published Android APK. It does not describe a new app build or change application behavior.

## Requirements

- Android 7.0 (API 24) or newer.
- Enough free storage for the APK and app data.
- An APK obtained from a release source you trust.

## Install from GitHub Releases

1. Open the [QR Code Studio Releases page](https://github.com/Dragons-Fox0058/qr-code-studio/releases).
2. Open the release you want. Prefer the latest stable release unless you have a reason to install an older version.
3. Download the APK asset provided by that release.
4. Open the downloaded APK on your Android device.
5. If Android asks, allow installation from that source in system settings.
6. Return to the installer and complete installation.
7. Open QR Code Studio.

Android's wording and settings path vary by manufacturer and Android version. Only grant the browser or file manager permission to install unknown apps when you intentionally install an APK from that source. You can revoke that permission afterward.

## Updating

- Prefer installing updates from the project's official [GitHub Releases](https://github.com/Dragons-Fox0058/qr-code-studio/releases) page.
- Android may preserve app data when an update is signed with the same signing key and uses a compatible application ID.
- If Android reports a signature or package conflict, do not uninstall immediately if you need to preserve local data. First confirm that the APK is an authentic release and ask for help with the exact error.

## Verify before installing

- Check that the download came from the repository's Releases page.
- Check the release notes and asset name.
- Do not install APKs sent through unrelated file-sharing links.
- Do not share an APK's signing keys or your own credentials.

## Uninstalling

Uninstalling an Android app can remove app-private data. Export or back up anything you need before uninstalling. The exact backup behavior depends on Android settings and the app's implementation.

## Problems

See [Troubleshooting](docs/TROUBLESHOOTING.md) or open a [support issue](https://github.com/Dragons-Fox0058/qr-code-studio/issues).
