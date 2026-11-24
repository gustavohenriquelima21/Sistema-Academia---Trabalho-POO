@echo off
cd /d "%~dp0"
if not exist out mkdir out
powershell -NoProfile -Command "$sources = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }; javac -encoding UTF-8 -d out $sources; if ($LASTEXITCODE -eq 0) { java -cp out academia.app.Main }"
pause
