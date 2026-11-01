@echo off
REM Turns OFF Google's stock TV launcher (the one with ads / recommendation rows).
REM ONLY run this after CleanYourTV works as your Home screen.
REM Undo with restore.bat.

set TV=192.168.0.105:5555
adb connect %TV%

adb -s %TV% shell pm disable-user --user 0 com.google.android.tvlauncher
adb -s %TV% shell pm disable-user --user 0 com.google.android.leanbacklauncher

echo.
echo Done. Press Home on the remote. If it ever misbehaves, run restore.bat.
pause
