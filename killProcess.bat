@echo off
taskkill -im "java.exe" -f
taskkill /F /IM chrome.exe /T
taskkill /im chromedriver.exe /f
taskkill /im cmd.exe /f