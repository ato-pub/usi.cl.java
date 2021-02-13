This source is based on the sample JAVA code previously available from https://www.usi.gov.au/system-developers/sample-code

It works as is with 3PT using the STS 1.2 SHA1 service.
* USI Service https://3pt.portal.usi.gov.au/Service/v3/UsiCreateService.svc
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc
* M2M credentials (which replace AUSkey Device credentials)

Alternatively, see below for PROD which uses:
* USI Service https://portal.usi.gov.au/Service/v3/UsiCreateService.svc
* STS Service https://softwareauthorisations.ato.gov.au/R3.0/S007v1.2/service.svc OR
* STS Service https://softwareauthorisations.ato.gov.au/R3.0/S007v1.3/service.svc

Building
========

Either use
* IDE - use gh to fetch the jars (see comment in build)
* ANT - see build

Tested platforms: Windows 10, MacOS, Linux

Notes on contributions:
1. binaries are banned (use gh)

Dependencies (built and tested with)
============

* Eclipse 2020-03 (4.15.0)
    - java-11-openjdk-11
    - language compliance level: 1.8
* If using STS v1.2
    - https://github.com/eclipse-ee4j/metro-wsit/releases
* If using STS v1.3
    - modified metro jars available at https://github.com/DamienJDev/metro-wsit/releases (java1.8MetroSigAlgFix.7z)
    - this allows SHA256 to be specified in the call to the S007v1.3 STS as Metro was defaulting to SHA1 which is not supported
* AUSkey AKM jars
    - These are from the Java SDK of https://www.sbr.gov.au/digital-service-providers
    - copies used to be hosted by USI Office (as non-SBR providers)
    - for convenience copies are in this repo in Releases
* gh from https://github.com/cli/cli
    - used in the build script - see build.sh/build.bat
    - used to fetch the dependent jars fromm the repository Releases

Structure
=========

* keystore/
    - the M2M credentials used in testing 3PT
* src/usi
    - the USI sample app
* application.properties
    - used by the sample app. See content for more info.
* src/au
    - the pre-generated output of wsdl2java (see RegenerateClientJava)
    - the files are the same for 3PT or PROD so there is no need to regenerate (unless the service definition changes)
* src/RegenerateClientJava
    - only used to generate WSDL java src - if needed
    - also generates a matching application.properties file
    - run for usage
    - requires CXF from https://cxf.apache.org/download.html
* src/UsiCreateService_EEE_stsSS.wsdl (where EEE=3PT or PROD; SS=12 (sha1) or 13 (sha256))
* src/META-INF
    - the wsdl definition files
    - contains numerous changes to support *client* side calls

3PT vs PROD
===========

e.g.

* to use 3PT sts12, copy src/UsiCreateService_3PT_sts12.wsdl to src/META-INF/wsdl/UsiCreateService_CLIENT.wsdl
* to use PROD sts13, copy src/UsiCreateService_PROD_sts13.wsdl to src/META-INF/wsdl/UsiCreateService_CLIENT.wsdl

The RegenerateClientJava script will do this and set the application.properties file appropriately.

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

See EnableProxy_FOR_DEBUG_ONLY() to use a proxy such as BURP to capture http traffic.

soapTracing() is generally sufficient.


Cloud or Local
------------

To switch between local and cloud mode (ActAs/Applies to) see the file UsiServiceChannel.java and line
*   private static boolean useCloud = false;

STS
---

The STS service is set in UsiCreateService_CLIENT.wsdl. The code must match this to indicate use of the v1.2 or v1.3 service.

See the file UsiServiceChannel.java and the line
* 	private static boolean useSts13 = false;

wsdl2java.bat
-------------

If you see the error
  * "'f' is not recognized as an internal or external command ..."
there is a typo in the batch file.
  * Line 50: "f %JAVA_VERSION% GTR 8 ("
Amend the "f" to be "if"
