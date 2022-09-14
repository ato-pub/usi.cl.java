# Overview

This source is based on the sample JAVA code previously available from https://www.usi.gov.au/system-developers/sample-code

It works as is with USI v5 3PT using the STS 1.2 SHA1 service, using build5 script (see also build4 script).

* USI v4 Service - https://3pt.portal.usi.gov.au/service/usiservice.svc
* USI v5 Service - https://3pt.portal.usi.gov.au/service/v5/usiservice.svc
* STS Service - https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service - https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc
* keystore-usi.xml - 3PT/EVTE M2M credentials (which replace AUSkey Device credentials)

Alternatively, see below for PROD (support for v5 pending) for  which uses:

* USI v4 Service - https://portal.usi.gov.au/service/usiservice.svc
* USI v5 Service - https://portal.usi.gov.au/service/v5/usiservice.svc
* STS Service - https://softwareauthorisations.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service - https://softwareauthorisations.ato.gov.au/R3.0/S007v1.3/service.svc
* Provide your own keystore.xml file (with "machine" credentials) from https://authorisationmanager.gov.au/

**Important note:** The v4 service is deprecated and is due to be removed around August 2023.

# WSDLs

The WSDLs used are NOT those hosted on the USI site. Instead local modified copies are used which:

* remove the MEX call as this is not supported by the STS
* configure the STS including claims
* fully specify use of sha256 (otherwise some aspects fall back to sha1)

See srcv4/wsdls and srcv5/wsdls for the modified versions using the STS v1.2 or v1.3 service

* USI v4:
    - 3PT: https://3pt.portal.usi.gov.au/service/usiservice.wsdl
    - PROD: https://portal.usi.gov.au/service/usiservice.wsdl
* USI v5:
    - 3PT: https://3pt.portal.usi.gov.au/service/v5/usiservice.wsdl
    - PROD: https://portal.usi.gov.au/service/v5/usiservice.wsdl

# Building

Either use:

* IDE - use gh to fetch the jars (see comment in build files)
* ANT - see build.xml

Tested platforms: Windows 10, MacOS, Linux

Notes on contributions:

* binaries are banned (use gh)

# Using ANT

See the input vars in build.xml.

e.g. the following do the same 

* ant
* ant buildall
* ant -Dusiver=5 -Dstsver=12 -Denv=3PT dumpvars getjars wsdl jar runUSITest

or to skip tests

* ant build

or seperately run targets

* ant getjars
* ant -Dusiver=5 -Denv=3PT wsdl
* ant -Dusiver=5 jar
* ant -Dusiver=5 runUSITest

will generate files for USI v5 for 3PT use (PROD requires a valid production keystore file to run).

The wsdl target will setup the appgen.properties file and
copy the application.<env>.properties to application.properties where <env> is 3PT or PROD.

Variables exist, in build.xml, to set:

* USI v4 or v5 service
* 3PT or PROD
* STS v1.2 or v1.3

In application.properties there are some values that can be set at runtime:

* local or cloud (ActAs/Applies)
* keystore=keystore/keystore-usi.xml
* alias_local=ABRD:27809366375_USIMachine
* alias_cloud=ABRD:11000002568_INGLETON153

Note: application.properties is overwritten at build time (wsdlpatch) by either of application.3PT.properties or application.PROD.properties. Any values not set here, will use predefined values in the code.

# Build for

In a terminal window:

## 3PT

    ant

will build and test as above.

## PROD

* Put your production keystore file somewhere e.g. keystore/my-prod-keystore.xml
* Modify the application.PROD.properties file, e.g.:
    - keystore=keystore/prod.xml
    - alias_local=ABRD:99999999990_CredentialName

    ant -Dusiver=4 -Dstsver=12 -Denv=PROD dumpvars getjars wsdl jar runUSITest

will build and test for PROD.

# Dependencies

Built and tested with:

* Eclipse 2020-03 (4.15.0)
    - java-11-openjdk-11
    - language compliance level: 1.8
* AUSkey AKM jars
    - These are from the Java SDK of https://www.sbr.gov.au/digital-service-providers
    - copies used to be hosted by USI Office (as non-SBR providers)
    - for convenience copies are in this repo in Releases
* If using STS v1.3
    - modified metro jars available at https://github.com/DamienJDev/metro-wsit/releases (java1.8MetroSigAlgFix.7z)
    - this allows SHA256 to be specified in the call to the S007v1.3 STS as Metro was defaulting to SHA1 which is not supported
    - copies in this repository for convenience - supports v1.2 and v1.3
* If using STS v1.2
    - standard jars can be used https://github.com/eclipse-ee4j/metro-wsit/releases
    - however the copies in this repository supports v1.2 and v1.3 as above
* gh from https://github.com/cli/cli
    - used in the build script - see build files
    - used to fetch the dependent jars fromm the repository Releases
* wsdl2java
    - requires CXF from https://cxf.apache.org/download.html

Ensure environment variables are set properly

* JAVA_HOME
* CXF_HOME (must be set to bin subfolder)

The build3/4 scripts will fetch the dependent libs into the lib subfolder:

* abrakm.jar
* auskey-dep-1.1.jar
* webservices-api.jar
* webservices-extra.jar
* webservices-rt.jar

# Structure

where N = 4 or 5

* build.xml
    - see Building
* keystore/
    - the M2M credentials used in testing 3PT
* application.properties, etc
    - used by the sample apps. See content for more info.
* srcvN/usi
    - the USI sample app for USI vN
* srcvN/wsdls
    - UsiService_EEE_stsSS.wsdl (where EEE=3PT or PROD; SS=12 (sha1) or 13 (sha256))
    - contains numerous changes to support *client* side calls
* srcvN/META-INF
    - the wsdl definition file consumed by the generated source
* lib
    - where downloaded jars are put

# Expected results

Sample of expected results from run:

USI reports an error as the data already exists. However, the transaction did verify successfully.

```
***********************************************************
WARNING: ***** Using proxy [localhost:8080] *****
***********************************************************
[main] INFO au.gov.abr.akm.credential.store.ABRKeyStoreSerializer - XML keystore loading
[main] INFO au.gov.abr.akm.credential.store.ABRKeyStoreSerializer - 2 credentials loaded in 125 milliseconds.
[main] INFO au.gov.abr.akm.credential.store.ABRCredential - Initialising X509Delegate
[main] INFO au.gov.abr.akm.credential.store.ABRCredential - checked to see if credential is due to be renewed - is not due yet
[main] INFO au.gov.abr.akm.credential.store.ABRKeyStoreSerializer - XML keystore loading
[main] INFO au.gov.abr.akm.credential.store.ABRKeyStoreSerializer - 2 credentials loaded in 17 milliseconds.
----------Printing Create USI Request Result and USI
Failure
null
----------Printing Error Messages
Failed to create USI record, multiple existing records were found.
----------Cannot call Verify, due to errors from Create.
```

# Notes

## Proxy and Tracing

See EnableProxy_FOR_DEBUG_ONLY() to use a proxy such as BURP to capture http/s traffic.

soapTracing() is generally sufficient.

