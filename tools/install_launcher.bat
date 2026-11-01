@echo off
REM Installs the CleanYourTV launcher APK and makes it the Home screen.
REM Usage: install_launcher.bat path\to\app-debug.apk
REM (or just drag the APK file onto this .bat file)

if "%~1"=="" (
  echo Drag your app-debug.apk onto this file, or run:  install_launcher.bat app-debug.apk
  pause
  exit /b 1
)

set TV=192.168.0.105:5555
adb connect %TV%

echo Installing %~1
adb -s %TV% install -r "%~1"

echo Setting CleanYourTV as the Home screen
adb -s %TV% shell cmd package set-home-activity com.cleanyourtv.launcher/.MainActivity

echo.
echo Press the Home button on your TV remote to see it.
echo If the TV asks which launcher to use, choose CleanYourTV and tap "Always".
pause
