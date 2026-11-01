# Privacy and Permissions Notes

This document records permission and configuration facts visible in the repository. It is not a complete privacy policy, a legal statement, or an audit of runtime network traffic.

## Permissions declared by the Android app

The current Android manifest declares:

| Permission or feature | Why Android exposes it |
| --- | --- |
| \`CAMERA\` | Allows camera-based scanning when that feature is used. |
| \`VIBRATE\` | Allows vibration feedback. |
| \`POST_NOTIFICATIONS\` | Allows notifications on Android versions that require this permission. |
| \`INTERNET\` | Allows network communication. |
| \`ACCESS_NETWORK_STATE\` | Allows the app to inspect network connectivity state. |
| Camera hardware feature | Declared as optional at package level. |

Actual prompts and behavior depend on Android version, device capabilities, and the feature being used.

## Network and service configuration

The app module declares Firebase AI and App Check dependencies, along with HTTP and JSON libraries. The manifest also declares internet access.

Dependency declarations and permissions do not, by themselves, establish:

- Which exact data is sent for each operation.
- Whether diagnostics or analytics are enabled in a given build.
- How long a service provider retains information.
- Whether a particular workflow can operate offline.
- Whether user-generated QR content is ever transmitted.

Those questions require review of the relevant source code, Firebase/provider configuration, and runtime behavior. Do not interpret this file as a promise that the app is fully offline or that no data leaves the device.

## User guidance

- Grant permissions only when you intend to use the related feature.
- Avoid scanning or sharing QR codes that contain credentials or other sensitive information unless you understand where the content will go.
- Do not post API keys, Firebase tokens, signing keys, or private QR contents in public support channels.
- Obtain APKs from the official [Releases page](https://github.com/Dragons-Fox0058/qr-code-studio/releases).

## Reporting a privacy concern

Open a repository issue without including personal or confidential data. For a security vulnerability, use the private reporting instructions in [SECURITY.md](../SECURITY.md).
