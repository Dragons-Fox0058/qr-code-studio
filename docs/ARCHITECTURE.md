# Architecture Overview

This is a repository-level orientation document, not a guarantee that every implementation detail is represented here. The application source remains the authoritative description of current behavior.

## Platform and UI

The Android application is built with Kotlin and Jetpack Compose. The module configuration also includes AndroidX lifecycle components, CameraX, Room, Kotlin coroutines, and Material 3.

## Data and integrations

The app module declares dependencies for local persistence (Room), QR processing (ZXing), image loading (Coil), HTTP clients (OkHttp and Retrofit), JSON handling (Moshi), and Firebase services.

A declared dependency does not prove that every feature is active or that a particular data flow occurs. Consult the current source and runtime configuration before making claims about behavior.

## Repository layout

See [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) for a high-level directory map.

## Change boundaries

- Keep application behavior changes isolated and reviewable.
- Update documentation when a user-visible workflow changes.
- Never commit API keys, signing credentials, local configuration, or generated build output.
- Do not infer privacy guarantees from the presence or absence of a library alone.

## Keeping this document accurate

When the architecture changes, update this overview in the same pull request. Prefer describing stable boundaries and actual source structure over speculative future designs.
