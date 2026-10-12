# Downloads the app under test (Sauce Labs My Demo App for Android) into apps\.
# Windows version of get-app.sh; the APK is not committed.
$ErrorActionPreference = 'Stop'

$version = '2.3.0'
$file = "mda-$version-27.apk"
$url = "https://github.com/saucelabs/my-demo-app-android/releases/download/$version/$file"

# Run from the repository root, wherever the script is called from
$root = Split-Path -Parent $PSScriptRoot
$target = Join-Path $root "apps\$file"
New-Item -ItemType Directory -Force (Join-Path $root 'apps') | Out-Null

# Skip the download when the file is already there
if (Test-Path $target) {
	Write-Output "apps\$file already present"
	exit 0
}

Write-Output "Downloading $url"
Invoke-WebRequest -Uri $url -OutFile $target
Write-Output "Saved apps\$file"
