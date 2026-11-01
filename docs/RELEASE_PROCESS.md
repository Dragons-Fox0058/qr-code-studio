# Release Process

This is a maintainer checklist, not an automated release workflow. Publishing a release remains a deliberate maintainer action.

## Before a release

- Confirm the intended version name and version code in the Android module configuration.
- Review the complete change set and update [CHANGELOG.md](../CHANGELOG.md).
- Run the relevant unit tests and Android lint checks.
- Build the intended release variant using the project's current signing configuration.
- Verify the APK on a supported Android device or emulator.
- Check that no credentials, debug artifacts, or private configuration are included.

## Signing

Release signing uses values supplied through environment variables and a keystore path configured in the Gradle script. Keep the keystore and passwords private.

Never upload a keystore, signing password, API key, or unredacted environment file to the repository or a public issue.

## Publish

1. Create a Git tag that matches the chosen release version.
2. Create a GitHub Release for that tag.
3. Attach the intended APK artifact.
4. Write concise release notes describing user-visible changes and known limitations.
5. Verify the published asset and release page after publishing.

## After publishing

- Confirm the download link works.
- Check issue reports for installation or upgrade problems.
- Record follow-up work in issues instead of silently promising it in release notes.

See [INSTALL.md](../INSTALL.md) for the end-user installation steps.
