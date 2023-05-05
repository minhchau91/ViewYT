#View ytb

### Precondition
Download java 1.8 from: https://drive.google.com/drive/folders/1NgIX9e8jCpAfDGcBriWZ79gGeiBzo7WK
Open terminal with Administrator and run:
`setx JAVA_HOME "path\to\folder\jdk1.8.0_202"`

### To add email:
`src/main/resources/email.txt`

### To add proxy:
`src/main/resources/proxy/proxylist.txt`

### To Turn on data captive: 
- Run until the end: `.\maven3\bin\mvn clean test -DretryCount=1 -DthreadCount=3 -DrerunFailed=false -DtestSuite=src/test/resources/turnOnDataCaptive.xml`
- Re-run failed emails: `.\maven3\bin\mvn clean test -DrerunFailed=true -DtestSuite=src/test/resources/turnOnDataCaptive.xml`

### View Youtube
- Run until the end: `.\maven3\bin\mvn test -DretryCount=999 -DthreadCount=20 -DvpsId=Pre01 -DenableActivity=true -DtestSuite=src/test/resources/viewYoutube.xml`

Parameters:  
`-DvpsId`: vps id in the matrix
`-DretryCount`: number of times to re-run a test if failed
`-DthreadCount`: number of thread to run
`-DrerunFailed`: re-run previous failed test 
`-DenableActivity`: enable/disable activity before running

### Run properties order:
If debugMode = false: 
`command line parameters > google sheet properties > xml parameters > local properties`
If debugMode = true: like above but ignore google sheet properties