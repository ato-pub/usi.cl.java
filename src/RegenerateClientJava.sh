#!/usr/bin/env bash
[[ "$JAVA_HOME" == "" ]] && {
  echo "JAVA_HOME not set";
  exit 1;
}
[[ "$CXF_HOME" == "" ]] && {
  echo "CXF_HOME not set to location of wsdl2java";
  exit 1;
}

WSDL_CLIENT="UsiCreateService_CLIENT.wsdl"

WSDL_TEST="UsiCreateService_3PT.wsdl"
WSDL_PROD="UsiCreateService_PROD.wsdl"
WSDL=
[[ "$1" == "t" ]] && { WSDL="$WSDL_TEST"; }
[[ "$1" == "p" ]] && { WSDL="$WSDL_PROD"; }
[[ "$WSDL" == "" ]] && {
  echo "usage:"; 
  echo   "$0 t|p";
  echo   "generate files for test (UsiCreateService_3PT.wsdl) or prod (UsiCreateService_PROD.wsdl)";
  exit 1;
}

echo "Using $WSDL"
echo "cp $WSDL" "META-INF/wsdl/$WSDL_CLIENT"
cp "$WSDL" "META-INF/wsdl/$WSDL_CLIENT"
echo "$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
"$CXF_HOME/wsdl2java" -V -b "META-INF/wsdl/cxf_bindings.config" -client -wsdlLocation "src/META-INF/wsdl/$WSDL_CLIENT" "META-INF/wsdl/$WSDL_CLIENT"
