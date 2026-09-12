<div align="center">

<!-- LOGO -->
<img src="logo.png" width="120" alt="Logo"/>

<h1>QR CODE STUDİO</h1>

<!-- BADGES -->
[![Download](https://img.shields.io/badge/Android-Download-0D47A1?style=for-the-badge&logo=android&logoColor=white&labelColor=3DDC84)](https://github.com/Dragons-Fox0058/qr-code-studio/releases/latest)
-
[![License MIT](https://img.shields.io/badge/License(project)-MIT-A31F34?style=for-the-badge&labelColor=A8A9AD)](https://github.com/Dragons-Fox0058/qr-code-studio?tab=MIT-1-ov-file)

<p>This is a QR code scanning application created using artificial intelligence (Google AI Studio).</p>

<!-- BADGES -->
[![API 24+](https://img.shields.io/badge/API-24%2B-brightgreen?style=for-the-badge&logo=android&logoColor=white&labelColor=00838F)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material_3-6750A4?style=for-the-badge&logo=material-design&logoColor=white)](https://m3.material.io)
[![License MIT](https://img.shields.io/badge/License-MIT-A31F34?style=for-the-badge&labelColor=A8A9AD)](https://opensource.org/licenses/MIT)

</div>

---

## ![](https://img.shields.io/badge/Table_of_Contents-0D2048?style=flat-square&logo=googledocs&logoColor=4285F4) Table of Contents

- [Screenshots](#screenshots)
- [Features](#features)
- [Supported Languages](#supported-languages)
- [Technologies Used](#technologies-used)
- [Installation](#installation)
- [How to Build](#how-to-build)
- [Permissions](#permissions)
- [Contributing](#contributing)
- [Licence](#licence)

---

## ![](https://img.shields.io/badge/Screenshots-1B3A2D?style=flat-square&logo=android&logoColor=3DDC84) Screenshots

| Create | Scan | History |
|--------|------|---------|
| ![Create](Screenshots/Create.png) | ![Scan](Screenshots/Scan.png) | ![History](Screenshots/History.png) |

---

## ![](https://img.shields.io/badge/Features-2D1B4E?style=flat-square&logo=materialdesign&logoColor=B39DDB) Features

- 20 Language support
- Free QR code generation
- Scanning the QR code
- The ability to look back at the past
- Material 3 design language
- Dark mode support

---

## ![](https://img.shields.io/badge/Supported_Languages-0D2048?style=flat-square&logo=googletranslate&logoColor=4285F4) Supported Languages

The application supports **20 languages**:

| # | Flag | Language | Code |
|---|------|----------|------|
| 1 | 🇹🇷 | Türkçe | `tr` |
| 2 | 🇬🇧 | English | `en` |
| 3 | 🇪🇸 | Español | `es` |
| 4 | 🇩🇪 | Deutsch | `de` |
| 5 | 🇫🇷 | Français | `fr` |
| 6 | 🇮🇹 | Italiano | `it` |
| 7 | 🇵🇹 | Português | `pt` |
| 8 | 🇷🇺 | Русский | `ru` |
| 9 | 🇨🇳 | 中文 | `zh` |
| 10 | 🇯🇵 | 日本語 | `ja` |
| 11 | 🇰🇷 | 한국어 | `ko` |
| 12 | 🇸🇦 | العربية | `ar` |
| 13 | 🇮🇳 | हिन्दी | `hi` |
| 14 | 🇦🇿 | Azərbaycanca | `az` |
| 15 | 🇳🇱 | Nederlands | `nl` |
| 16 | 🇵🇱 | Polski | `pl` |
| 17 | 🇺🇦 | Українська | `uk` |
| 18 | 🇮🇩 | Bahasa Indonesia | `id` |
| 19 | 🇻🇳 | Tiếng Việt | `vi` |
| 20 | 🇬🇷 | Ελληνικά | `el` |

---

## ![](https://img.shields.io/badge/Technologies_Used-1A0F3A?style=flat-square&logo=kotlin&logoColor=7F52FF) Technologies Used

| Technology | Explanation |
|-----------|-------------|
| [Kotlin](https://kotlinlang.org/) | Main programming language |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Modern user interface toolkit |
| [Material 3](https://m3.material.io/) | Design system |
| [AndroidX CameraX](https://developer.android.com/jetpack/androidx/releases/camera?hl=en) | Camera connection |
| [ZXing Core](https://zxing.github.io/zxing/) | Generating a QR code |
| [AndroidX Room](https://developer.android.com/jetpack/androidx/releases/room?hl=en) | For storage |
| [Android Photo Picker](https://developer.android.com/training/data-storage/shared/photo-picker?hl=en) | Photo selection |

---

## ![](https://img.shields.io/badge/Installation-1B3A2D?style=flat-square&logo=android&logoColor=3DDC84) Installation

### Requirements
- Android **7.0 (API 24)** or higher
- ~10 MB free storage

### Steps

1. Go to the [**Releases**](https://github.com/Dragons-Fox0058/qr-code-studio/releases/latest) page
2. Download the latest **`.apk`** file
3. On your Android device, open the downloaded file
4. If prompted, enable **"Install from unknown sources"** in your settings
5. Tap **Install** and open the app

> **Tip:** After installation, you can disable "Install from unknown sources" again for security.

---

## ![](https://img.shields.io/badge/How_to_Build-1A0F3A?style=flat-square&logo=kotlin&logoColor=7F52FF) How to Build

### Requirements
- [Android Studio](https://developer.android.com/studio) Hedgehog or newer
- JDK 17+
- Android SDK API 24+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/Dragons-Fox0058/qr-code-studio.git

# 2. Open the project in Android Studio
# File → Open → select the cloned folder

# 3. Let Gradle sync finish

# 4. Run on emulator or physical device
# Press the ▶ Run button or use:
./gradlew assembleDebug
```

> The generated APK will be at `app/build/outputs/apk/debug/app-debug.apk`

---

## ![](https://img.shields.io/badge/Permissions-3B0A14?style=flat-square&logo=gnuprivacyguard&logoColor=EF5350) Permissions

The app is designed with **privacy in mind** and uses the minimum number of permissions possible.

### ✅ Runtime Permission (User Approval Required)

| Permission | Reason | When Asked |
|---|---|---|
| `CAMERA` | To scan QR codes live via camera | Only when switching to the **Scan** tab |

### ⚙️ Normal Permission (Granted Automatically at Install)

| Permission | Reason | When Asked |
|---|---|---|
| `VIBRATE` | Haptic feedback when a QR code is successfully scanned | Never — granted automatically |

### ⭐ Permissions We Do NOT Request (Privacy Highlights)

| Permission | Why It's Not Needed |
|---|---|
| ❌ `READ_EXTERNAL_STORAGE` | Uses Android's modern **Photo Picker** — no broad gallery access needed |
| ❌ `INTERNET` | Everything works **100% offline** — QR generation, scanning, and history are all on-device. No data is ever sent externally |

---

## ![](https://img.shields.io/badge/Contributing-2D1B4E?style=flat-square&logo=materialdesign&logoColor=B39DDB) Contributing

Contributions are welcome! Here's how to get started:

1. **Fork** this repository
2. **Create** a new branch
```bash
   git checkout -b feature/your-feature-name
```
3. **Make** your changes and commit
```bash
   git commit -m "feat: add your feature description"
```
4. **Push** to your branch
```bash
   git push origin feature/your-feature-name
```
5. **Open** a Pull Request on GitHub

### Guidelines
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use Material 3 components for any UI changes
- Test on both light and dark mode before submitting

---

## ![](https://img.shields.io/badge/Licence-2D2D30?style=flat-square&logo=opensourceinitiative&logoColor=A8A9AD) Licence

This project is licensed under the **MIT License** — see the [![License MIT](https://img.shields.io/badge/License-MIT-A31F34?style=flat-square&labelColor=A8A9AD)](https://opensource.org/licenses/MIT) for details.
