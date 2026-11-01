# Frequently Asked Questions

## Where can I download the app?

Use the project's [GitHub Releases](https://github.com/Dragons-Fox0058/qr-code-studio/releases) page. Avoid APKs from unofficial mirrors.

## Which Android versions are supported?

The Android module currently declares a minimum SDK of API 24 (Android 7.0). A particular release may have additional requirements.

## Is the project open source?

The repository includes a top-level MIT License. Third-party dependencies and assets can have separate terms; see [LICENSE_ADDITIONAL.md](../LICENSE_ADDITIONAL.md).

## Why does installation show an unknown-app warning?

Android applies this warning to APKs installed outside an app store. Only proceed when you intentionally downloaded the APK from the official repository release page and trust the source.

## Why does scanning need camera access?

The Android manifest declares camera permission, and the camera hardware feature is marked optional at the package level. Android may ask for permission when a camera feature is used. Denying it can prevent camera scanning from working.

## Does the app need internet access?

The manifest declares internet and network-state permissions, and the project includes network and Firebase-related dependencies. This alone does not describe exactly what information is transmitted in every app workflow. See [PRIVACY.md](PRIVACY.md) and review the app's current implementation.

## Where can I report a bug?

Use the [Bug Report issue template](https://github.com/Dragons-Fox0058/qr-code-studio/issues/new/choose). Include reproducible steps and redact personal information.

## How can I contribute?

Read [CONTRIBUTING.md](../CONTRIBUTING.md) and open a pull request with a focused change.

## I found a security vulnerability. Should I open an issue?

No. Do not publish vulnerability details in a public issue. Follow [SECURITY.md](../SECURITY.md).
