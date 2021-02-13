#!/usr/bin/env bash

function usage {
  echo "usage:";
  echo   "$0 3PT|PROD [12|13 [local|cloud]]";
  echo   "  generate files for 3PT or PROD";
  echo   "  using STS 1.2 (sha1) (default) or STS 1.3 (sha256)";
  echo   "  for local (default) or cloud transactions";
  exit 1;
}

if [ "$JAVA_HOME" == "" ]; then
  echo "JAVA_HOME not set";
  exit 1;
fi
if [ "$CXF_HOME" == "" ]; then
  echo "CXF_HOME not set to location of wsdl2java";
  exit 1;
fi

[[ "$1" == "3PT" ]] || [[ "$1" == "PROD" ]] || usage
[[ "$2" == "" ]] || [[ "$2" == "12" ]] || [[ "$2" == "13" ]] || usage
[[ "$3" == "" ]] || [[ "$3" == "local" ]] || [[ "$3" == "cloud" ]] || usage
env="3PT"
if [ "$1" == "PROD" ]; then env="$1"; fi;
sha="12"
if [ "$2" == "13" ]; then sha="$2"; fi;
mode="local"
if [ "$3" == "cloud" ]; then mode="$3"; fi;
skipgensrc="$4" # any value

srcroot="wsdls/usiv3"
WSDL="UsiService_${env}_sts${sha}.wsdl"
WSDL_CLIENT="UsiCreateService_CLIENT.wsdl"

echo "Using $srcroot/$WSDL"
echo "cp $srcroot/$WSDL" "META-INF/wsdl/$WSDL_CLIENT"
cp "$srcroot/$WSDL" "META-INF/wsdl/$WSDL_CLIENT"
if [ "${skipgensrc}" == "" ]; then
  echo "$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
  "$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
fi

echo > ../application.properties
echo "# sts_version is generated using src/RegenerateClientJava.sh - do not modify" >> ../application.properties
if [ "${sha}" == "13" ]; then
  echo "sts_version=3" >> ../application.properties
else
  echo "sts_version=2" >> ../application.properties
fi
echo >> ../application.properties
echo "# sts_mode can be 'local' or 'cloud'" >> ../application.properties
echo "sts_mode=${mode}" >> ../application.properties
