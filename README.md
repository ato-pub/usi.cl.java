27 March 2020
=============

This draft release of the sample java app for USI updates the app to work with the 
* USI Service https://3pt.portal.usi.gov.au/Service/v3/UsiCreateService.svc
* STS Service https://softwareauthorisations.acc.ato.gov.au/R3.0/S007v1.3/service.svc
* M2M credentials (which replace AUSkey Device credentials)

This app is configured to use 3PT not PROD.

It is based on the sample code available from https://www.usi.gov.au/system-developers/sample-code

Important Notes
===============

Java
----
Tested with Java 8 and 11
It will build with Intellij or Eclipse

Dependency - AUSkey ADK jars
----------
* abrakm.jar (2.0.0)
* auskey-dep-1.1.jar (if the file is auskey-dep.jar rename it - required by Eclipse)

Dependency - Metro jars
----------
* webservices-rt.jar
* webservices-api.jar
* webservices-extra.jar
* Available at https://github.com/DamienJDev/metro-wsit/releases (java1.8MetroSigAlgFix.7z
 or java11MetroSigAlgFix.7z)
This allows SHA256 to be specified in the call to the S007v1.3 STS as Metro was defaulting to SHA1 which is not supported.

WSDL
----
* contains numerous changes to support *client* side calls
* Uses the client WSDL src\META-INF\wsdl\UsiCreateService_CLIENT.wsdl
* pre-generated files for the USI service are already included in src\au\gov\usi\_2018\ws\servicepolicy
* files are generated using apache-cxf-3.3.5 - see src\RegenerateClientJava.bat
* If not using src\RegenerateClientJava.bat
  - For prod, simply replace the file src\META-INF\wsdl\UsiCreateService_CLIENT.wsdl with src\UsiCreateService_PROD.wsdl
  - For 3pt, simply replace the file src\META-INF\wsdl\UsiCreateService_CLIENT.wsdl with src\UsiCreateService_3PT.wsdl

UsiServiceChannel.java
-----------------
To switch between local and cloud mode (ActAs/Applies to) see the line
- 	private static boolean useActAs = false;

Expected result (TEST)
---------------
The USI service will return failure due to incorrect data but it does pass all authentication

<<<<<<<<<<<<<<<<
----------Printing Create USI Request Result and USI
Failure
null
----------Printing Error Messages
Failed to create USI record, multiple existing records were found.
----------Cannot call Verify, due to errors from Create.

Process finished with exit code 0
>>>>>>>>>>>>>>>>
