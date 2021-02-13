@echo off
if "%JAVA_HOME%" == "" (
  echo JAVA_HOME not set
  goto :eof
)
if "%CXF_HOME%" == "" (
  echo CXF_HOME not set
  goto :eof
)


set "WSDL="
if "%1" == "3PT" set "WSDLenv=3PT"
if "%1" == "PROD" set "WSDLenv=PROD"
set "WSDLsts=12"
if "%2" == "13" set "WSDLsts=13"
set "WSDLmode=local"
if "%3" == "cloud" set "WSDLmode=cloud"
if "%WSDLenv%" == "" goto :usage

set "srcroot=wsdls/usiv4"
set "WSDL=UsiService_%WSDLenv%_sts%WSDLsts%.wsdl"
set "WSDL_CLIENT=UsiCreateService_CLIENT.wsdl"

echo Using %srcroot%\%WSDL%
echo copy "%srcroot%\%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
copy /y "%srcroot%\%WSDL%" "META-INF\wsdl\%WSDL_CLIENT%"
if not "%4" == "" goto :eof
echo "%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"
"%CXF_HOME%\bin\wsdl2java.bat" -V -b "META-INF\wsdl\cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/%WSDL_CLIENT%" "META-INF\wsdl\%WSDL_CLIENT%"

echo # > ../application.properties
echo # sts_version is generated using src/RegenerateClientJava.sh - do not modify >> ../application.properties
if "%WSDLsts%" == "13" ]; then
  echo sts_version=3 >> ../application.properties
else
  echo sts_version=2 >> ../application.properties
fi
echo # >> ../application.properties
echo # sts_mode can be 'local' or 'cloud' >> ../application.properties
echo sts_mode=%WSDLmode% >> ../application.properties

goto :eof
:usage
echo usage:
echo   $0 3PT^|PROD [12^|13 [local^|cloud]]
echo     generate files for 3PT or PROD
echo     using STS 1.2 (sha1) (default) or STS 1.3 (sha256)
echo     for local (default) or cloud transactions
goto :eof
