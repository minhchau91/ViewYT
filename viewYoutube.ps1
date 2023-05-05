Write-Host "Installing Apache Maven 3.6.3 ..."
try
{
    Write-Host "Run view youtube"
    .\maven3\bin\mvn test -DretryCount=999 -DthreadCount=3 "-DtestSuite=src/test/resources/viewYoutube.xml"
}
finally
{
    taskkill -im "java.exe" -f
    taskkill /im chromedriver.exe /f
    taskkill /F /IM chrome.exe /T
}