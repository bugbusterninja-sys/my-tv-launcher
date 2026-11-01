@echo off
REM Removes safe bloatware from the Realme "AI PONT" TV for the current user.
REM Reversible: run restore.bat, or factory reset.
REM Needs adb.exe in PATH, or put this file inside the platform-tools folder.

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
  echo Removing %%P
  adb -s %TV% shell pm uninstall --user 0 %%P
)

echo.
echo Done. Each line above should say Success.
pause
