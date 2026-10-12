#!/usr/bin/env bash
# Downloads the app under test (Sauce Labs My Demo App for Android) into apps/.
# The APK is not committed: it is a binary from the app's own GitHub release.
set -euo pipefail

VERSION="2.3.0"
FILE="mda-${VERSION}-27.apk"
URL="https://github.com/saucelabs/my-demo-app-android/releases/download/${VERSION}/${FILE}"

# Run from the repository root, wherever the script is called from
cd "$(dirname "$0")/.."
mkdir -p apps

# Skip the download when the file is already there
if [ -f "apps/${FILE}" ]; then
	echo "apps/${FILE} already present"
	exit 0
fi

echo "Downloading ${URL}"
curl -fsSL -o "apps/${FILE}" "${URL}"
echo "Saved apps/${FILE}"
