@echo off
set "proxy=-Dhttps.proxyHost=127.0.0.1 -Dhttps.proxyPort=8080"
if "%1" == "" set "proxy="
echo Running usi.gov.au.USITest ...
C:\pf\java\jdk\bin\javaw.exe %proxy% -Dfile.encoding=Cp1252 -classpath ".\bin;.\lib\abrakm.jar;.\lib\auskey-dep-1.1.jar;.\lib\webservices-api.jar;.\lib\webservices-extra.jar;.\lib\webservices-rt.jar" usi.gov.au.USITest
