@echo off
if "%JAVA_HOME%" == "" (
  echo JAVA_HOME not set
  goto :eof
)
if "%CXF_HOME%" == "" (
  echo CXF_HOME not set
  goto :eof
)


set "WSDL_TEST=UsiCreateService_3PT.wsdl"
set "WSDL_PROD=UsiCreateService_PROD.wsdl"
set "WSDL="
if "%1" == "3PT" set "WSDLenv=3PT"
if "%1" == "PROD" set "WSDLenv=PROD"
if "%2" == "12" set "WSDLsts=12"
if "%2" == "13" set "WSDLsts=13"
if "%WSDLenv%" == "" goto :usage
if "%WSDLsts%" == "" goto :usage

set "WSDL_CLIENT=UsiCreateService_CLIENT.wsdl"
set "WSDL=UsiCreateService_%WSDLenv%_sts%WSDLsts%.wsdl"

echo Using %WSDL%
echo copy "%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
copy /y "%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
if not "%3" == "" goto :eof
echo "%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"
"%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"
goto :eof

:usage
echo usage:
echo   $0 3PT^|PROD 12^|13
echo     generate files for 3PT or PROD
echo     using STS 1.2 (sha1) or STS 1.3 (sha256)
goto :eof
