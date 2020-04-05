@echo off
if "%JAVA_HOME%" == "" (
  echo JAVA_HOME not set
  goto :eof
)
if "%CXF_HOME%" == "" (
  echo CXF_HOME not set
  goto :eof
)

set "WSDL_CLIENT=UsiCreateService_CLIENT.wsdl"

set "WSDL_TEST=UsiCreateService_3PT.wsdl"
set "WSDL_PROD=UsiCreateService_PROD.wsdl"
set "WSDL="
if "%1" == "t" set "WSDL=%WSDL_TEST%"
if "%1" == "p" set "WSDL=%WSDL_PROD%"
if "%WSDL%" == "" goto :usage

echo Using %WSDL%
echo copy "%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
copy /y "%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
echo "%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"
"%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"
goto :eof

:usage
echo usage: 
echo   %0 t^|p
echo   generate files for test (UsiCreateService_3PT.wsdl) or prod (UsiCreateService_PROD.wsdl)
goto :eof
