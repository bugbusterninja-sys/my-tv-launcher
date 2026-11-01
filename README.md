# CleanYourTV

A clean, ad-free Android TV home screen (launcher) with an Apple TV-style look, plus helper scripts to remove bloatware.

Built for a Realme "AI PONT" TV (Android 9 / API 28), should work on most Android TV devices.

## What it does
- Big Favourites row (Netflix, YouTube, Prime Video, Hotstar if installed)
- "All apps" row with every installed TV app
- Focused app gets a glowing border, zoom, and a soft backdrop
- Greeting, date and clock; background colour changes through the day
- No ads, no recommendation rows

## Get the APK
1. Open the **Actions** tab on GitHub.
2. Click the latest **Build APK** run.
3. Download `cleanyourtv-debug-apk` from **Artifacts** and unzip it to get `app-debug.apk`.

## Install on the TV
1. Connect with `adb connect <tv-ip>:5555`.
2. Drag `app-debug.apk` onto `tools/install_launcher.bat`.
3. Press Home on the remote.

## Tools
| Script | What it does |
|---|---|
| `tools/debloat.bat` | Removes safe bloatware (reversible) |
| `tools/restore.bat` | Brings everything back |
| `tools/install_launcher.bat` | Installs the APK and sets it as Home |
| `tools/disable_stock_launcher.bat` | Turns off Google's stock launcher (do this last) |

The scripts use the TV address `192.168.0.105:5555`. Edit the `TV=` line at the top if yours changes.

## Roadmap
- Long-press to pin / unpin favourites
- Real wallpapers and blurred backdrops
- Settings and input shortcuts in the header
