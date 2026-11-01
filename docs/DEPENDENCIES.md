# Dependency Overview

This is a short orientation to dependency groups declared by the Android module. The exact resolved dependency graph can differ by build variant and transitive dependencies.

| Area | Examples declared by the project |
| --- | --- |
| Android UI | Jetpack Compose, Material 3, AndroidX Activity and Lifecycle |
| Camera | CameraX camera2, core, lifecycle, and view |
| Local persistence | Room runtime and KTX |
| QR processing | ZXing core |
| Images | Coil Compose |
| Networking | OkHttp and Retrofit |
| JSON | Moshi and Retrofit's Moshi converter |
| Async work | Kotlin Coroutines |
| Backend services | Firebase AI and App Check |
| Testing | JUnit, AndroidX test libraries, Robolectric, and Roborazzi |

## Dependency maintenance

- Prefer stable, compatible versions.
- Review release notes and breaking changes before upgrades.
- Avoid adding a library for functionality already available in the platform or existing dependencies without a clear reason.
- Run relevant build and test tasks after a dependency change.
- Review license obligations for the exact resolved artifacts.

For licensing notes, see [LICENSE_ADDITIONAL.md](../LICENSE_ADDITIONAL.md). This overview is not a complete software bill of materials (SBOM).
