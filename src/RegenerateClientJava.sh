#!/usr/bin/env bash

function usage {
  echo "usage:";
  echo   "$0 3PT|PROD 12|13";
  echo   "  generate files for 3PT or PROD";
  echo   "  using STS 1.2 (sha1) or STS 1.3 (sha256)";
  exit 1;
}

[[ "$JAVA_HOME" == "" ]] && {
  echo "JAVA_HOME not set";
  exit 1;
}
[[ "$CXF_HOME" == "" ]] && {
  echo "CXF_HOME not set to location of wsdl2java";
  exit 1;
}

[[ "$1" == "3PT" ]] || [[ "$1" == "PROD" ]] || usage
[[ "$2" == "12" ]] || [[ "$2" == "13" ]] || usage

WSDL="UsiCreateService_$1_sts$2.wsdl"
WSDL_CLIENT="UsiCreateService_CLIENT.wsdl"

echo "Using $WSDL"
echo "cp $WSDL" "META-INF/wsdl/$WSDL_CLIENT"
cp "$WSDL" "META-INF/wsdl/$WSDL_CLIENT"
[[ "$3" == "" ]] && {
  echo "$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
  "$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
}