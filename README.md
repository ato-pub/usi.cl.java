This source is based on the sample JAVA code previously available from https://www.usi.gov.au/system-developers/sample-code

It works as is with 3PT using the STS 1.2 SHA1 service.

Note that USI v3 has been deprecated, so support for it has been removed.

* USI v4 Service https://3pt.portal.usi.gov.au/Service/UsiService.svc
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc
* M2M credentials (which replace AUSkey Device credentials)

Alternatively, see below for PROD which uses:

* USI v4 Service https://portal.usi.gov.au/Service/UsiService.svc
* STS Service https://softwareauthorisations.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service https://softwareauthorisations.ato.gov.au/R3.0/S007v1.3/service.svc

WSDLs
=====

The WSDLs used are NOT those hosted on the USI site. Instead local modified copies are used which:

* remove the MEX call as this is not supported by the STS
* configure the STS including claims
* full specify use of sha256 (otherwise some aspects fall back to sha1)

Files:

* USI v4: See srcv4/wsdls for the modified versions using the STS v1.2 or v1.3 service
    - 3PT: https://3pt.portal.usi.gov.au/Service/v3/UsiCreateService.wsdl
    - PROD: https://portal.usi.gov.au/Service/v3/UsiCreateService.wsdl

Building
========

Either use:

* IDE - use gh to fetch the jars (see comment in build)
* ANT - see build4 (USI v4)

Tested platforms: Windows 10, MacOS, Linux

Notes on contributions:

1. binaries are banned (use gh)

Using ANT
=========

See the input vars in build.xml.

e.g.

  * ant -Dusiver=4 -Denv=PROD getjars wsdl jar runUSITest

or seperately run targets
  * ant getjars
  * ant -Dusiver=4 -Denv=PROD wsdl
  * ant -Dusiver=4 jar
  * ant -Dusiver=4 runUSITest

will generate files for USI v4 for production use (requires a valid production keystore file to run).
The wsdl target will setup the appgen.properties file and
copy the application.<env>.properties to application.properties where <env> is 3PT or PROD.

Variables exist to set:
  * USI v3 or v4 service (v3 no longer supported)
  * 3PT or PROD
  * STS v1.2 or v1.3

In application.properties there are some values that can be set at runtime:
  * local or cloud (ActAs/Applies)
  * keystore=keystore/keystore-usi.xml
  * alias_local=ABRD:27809366375_USIMachine
  * alias_cloud=ABRD:11000002568_INGLETON153


Dependencies (built and tested with)
============

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

Structure
=========

* build.xml
    - see Building
* keystore/
    - the M2M credentials used in testing 3PT
* application.properties, etc
    - used by the sample apps. See content for more info.
* srcv4/usi
    - the USI sample app for USI v4
* srcv4/au
    - the pre-generated output of wsdl2java (see wsdl target in build.xml)
    - the files are the same for 3PT or PROD so there is no need to regenerate (unless the service definition changes)
* srcv4/wsdls
    - UsiService_EEE_stsSS.wsdl (where EEE=3PT or PROD; SS=12 (sha1) or 13 (sha256))
    - contains numerous changes to support *client* side calls
* srcv4/META-INF
    - the wsdl definition file consumed by the generated source
* lib
    - where downloaded jars are put

Sample of expected results from run
================

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

Notes
=====

Proxy and Tracing
-----

See EnableProxy_FOR_DEBUG_ONLY() to use a proxy such as BURP to capture http/s traffic.

soapTracing() is generally sufficient.

