# Contributing to QR Code Studio

Thank you for considering a contribution. This document describes how to
propose changes to this repository.

## Before you start

- Check existing issues and pull requests to avoid duplicate work.
- For significant changes, open an issue first to discuss the approach.
- Keep pull requests focused: one logical change per pull request.
- Do not commit credentials, signing keys, private data, generated build
  outputs, or personal information.

## Development workflow

1. Fork the repository.
2. Create a topic branch from the default branch.

   ```bash
   git checkout -b type/short-description
   ```

3. Make your changes and review the complete diff.
4. Run the relevant checks available for your change.
5. Commit with a clear message.

   ```bash
   git commit -m "docs: clarify contribution process"
   ```

6. Push your branch and open a pull request against the default branch.

## Commit message suggestions

Use a short, imperative subject. The following prefixes are recommended:

- `feat:` new functionality
- `fix:` bug fix
- `docs:` documentation changes
- `build:` build or dependency configuration
- `ci:` continuous integration changes
- `refactor:` restructuring without behavior changes
- `chore:` maintenance tasks

## Pull request checklist

- [ ] The pull request has a clear title and explains the reason for the change.
- [ ] The change is limited to its stated purpose.
- [ ] Documentation is updated when necessary.
- [ ] No secrets, credentials, generated artifacts, or unrelated files are included.
- [ ] Relevant checks have been run, and their results are described.
- [ ] Screenshots or logs are included when they help reviewers understand the
      change.

## Scope and review

Be respectful, constructive, and open to feedback. Maintainers may ask for
changes, additional context, or tests before merging.

By submitting a contribution, you agree that it is provided under the
repository's MIT License, to the extent permitted by applicable law.
