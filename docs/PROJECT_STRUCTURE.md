# Project Structure

This map describes the repository at a high level. It intentionally avoids listing every source file so the guide remains useful as the project evolves.

```text
qr-code-studio/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   ├── PULL_REQUEST_TEMPLATE.md
│   └── workflows/
├── docs/
├── Screenshots/
├── app/
│   ├── gradle/             # Version catalog and wrapper properties
│   └── app/
│       └── src/
├── CHANGELOG.md
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── HELP.md
├── INSTALL.md
├── LICENSE
├── LICENSE_ADDITIONAL.md
├── README.md
├── SECURITY.md
└── SUPPORT.md
```

## Important paths

| Path | Purpose |
| --- | --- |
| `.github/ISSUE_TEMPLATE/` | Issue forms for bug reports and feature requests. |
| `.github/workflows/` | GitHub Actions workflows. |
| `Screenshots/` | Images used to illustrate the project. |
| `app/` | Android Studio project root, Gradle settings, and version catalog. Wrapper scripts are not currently committed. |
| `app/app/` | Android application module. |
| `docs/` | Additional maintainer and contributor guides. |

## Boundaries

Do not commit local build output, IDE state, API keys, signing credentials, or private configuration. See the root `.gitignore` and [DEVELOPMENT.md](DEVELOPMENT.md).
