This source is based on the sample JAVA code available from https://www.usi.gov.au/system-developers/sample-code

It works as with 3PT
* USI Service https://3pt.portal.usi.gov.au/Service/v3/UsiCreateService.svc
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc
* M2M credentials (which replace AUSkey Device credentials)

Alternatively, see below for PROD which uses:
* USI Service https://portal.usi.gov.au/Service/v3/UsiCreateService.svc
* STS Service https://softwareauthorisations.ato.gov.au/R3.0/S007v1.3/service.svc


Dependencies (built and tested with)
============

* Eclipse 2020-03 (4.15.0)
  - java-11-openjdk-11
  - language compliance level: 1.8
* modified metro jars available at https://github.com/DamienJDev/metro-wsit/releases (java1.8MetroSigAlgFix.7z)
  - this allows SHA256 to be specified in the call to the S007v1.3 STS as Metro was defaulting to SHA1 which is not supported
* AUSkey AKM jars - from ATO - copies available in this repo in releases

Structure
=========

* keystore
  - the M2M credentials used in testing 3PT
* src/usi
  - the USI sample app
* src/au
  - the pre-generated output of wsdl2java (see RegenerateClientJava)
  - the files are the same for 3PT or PROD so there is no need to regenerate (unless the service definition changes)
* src/RegenerateClientJava
  - only used to generate WSDL java src - if needed
  - run for usage
  - requires CXF from https://cxf.apache.org/download.html
* src/UsiCreateService_3PT.wsdl and UsiCreateService_PROD.wsdl
* src/META-INF
  - the wsdl definition files
  - contains numerous changes to support *client* side calls

3PT vs PROD
===========

* to use 3PT copy src/UsiCreateService_3PT.wsdl to src/META-INF/wsdl/UsiCreateService_CLIENT.wsdl
* to use PROD copy src/UsiCreateService_PROD.wsdl to src/META-INF/wsdl/UsiCreateService_CLIENT.wsdl

Expected results from run
================

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

Notes
=====

1. UsiServiceChannel.java

This is is set to use a proxy (BURP to capture http traffic).

To switch between local and cloud mode (ActAs/Applies to) see the line
-   private static boolean useActAs = false;

2. wsdl2java.bat

If you see "'f' is not recognized as an internal or external command ...", there is a type in the file.
Line 50: "f %JAVA_VERSION% GTR 8 (" should be if
