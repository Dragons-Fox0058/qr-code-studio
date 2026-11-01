# Privacy

This document is a repository-level privacy notice for contributors and users. It is not a substitute for a legally reviewed privacy policy, and it must not be interpreted as a guarantee about runtime data handling that has not been verified.

## Permissions declared by the Android app

The current Android manifest declares:

- Camera
- Vibration
- Post notifications
- Internet access
- Network state access

It also declares camera hardware as optional. A permission declaration does not, by itself, establish whether data is collected, transmitted, retained, or shared.

## Third-party services and network communication

The Gradle configuration includes Firebase libraries, including Firebase AI and Firebase App Check, as well as networking-related libraries. Actual service usage, transmitted data, retention, regional processing, and third-party terms depend on the application code and service configuration.

Before publishing a definitive user-facing privacy policy, the maintainers should audit:

- What information is processed on-device.
- Whether QR contents, images, or scan history leave the device.
- Which Firebase or other external services are contacted and for what purpose.
- Whether analytics, diagnostics, crash reporting, or advertising SDKs are enabled.
- How long data is retained and how users can request deletion.
- Whether children or other protected audiences are in scope.

## Secrets and credentials

Never commit API keys intended to be private, service-account files, signing credentials, keystores, access tokens, or production configuration. Use the repository's documented local configuration and GitHub Actions secrets where appropriate. Rotate any credential that has been exposed.

## Contact and updates

For security-sensitive reports, follow [SECURITY.md](SECURITY.md). For general support, see [SUPPORT.md](SUPPORT.md).

Maintainers should update this document whenever data processing, permissions, third-party services, or retention practices change. Do not publish claims until they have been checked against the shipped application and backend configuration.
