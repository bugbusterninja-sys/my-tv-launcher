@echo off
REM Brings back everything debloat.bat removed, and re-enables the stock launcher.

set TV=192.168.0.105:5555
adb connect %TV%

for %%P in (
  com.homwee.store
  com.homwee.help
  com.homwee.howtocast
  com.homwee.multiscreenshare
  com.google.android.play.games
  com.google.android.videos
  com.google.android.tv.bugreportsender
  com.google.android.feedback
) do (
  echo Restoring %%P
  adb -s %TV% shell cmd package install-existing %%P
)

echo Re-enabling stock launcher
adb -s %TV% shell pm enable com.google.android.tvlauncher
adb -s %TV% shell pm enable com.google.android.leanbacklauncher

echo.
echo Done.
pause
