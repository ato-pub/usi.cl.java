package usi.gov.au;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import au.gov.usi._2022.ws.ApplicationResponseType;
import au.gov.usi._2022.ws.ApplicationType;
import au.gov.usi._2022.ws.BirthCertificateDocumentType;
import au.gov.usi._2022.ws.ContactDetailsType;
import au.gov.usi._2022.ws.CreateUSIResponseType;
import au.gov.usi._2022.ws.CreateUSIType;
import au.gov.usi._2022.ws.DVSDocumentType;
import au.gov.usi._2022.ws.ErrorType;
import au.gov.usi._2022.ws.MatchResultType;
import au.gov.usi._2022.ws.NationalAddressType;
import au.gov.usi._2022.ws.PersonalDetailsType;
import au.gov.usi._2022.ws.PhoneType;
import au.gov.usi._2022.ws.StateListType;
import au.gov.usi._2022.ws.VerifyUSIResponseType;
import au.gov.usi._2022.ws.VerifyUSIType;
import au.gov.usi._2022.ws.servicepolicy.IUSIService;

public class USITest {
    
    static {
        System.setProperty("java.util.logging.config.file",
                "config/logging.properties");

    }

    private static Random RANDOM = new Random();
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String OrgCode = UsiServiceChannel.getOrgCode();

        try {
            IUSIService endpoint = UsiServiceChannel.GetNewClient(OrgCode);
            
            VerifyUSIType verifyUSI = new VerifyUSIType();
            GregorianCalendar cal = new GregorianCalendar();

            Date dob = null;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            dob = df.parse("13/06/1983");
            cal.setTime(dob);
            XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarDate(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);
            //BNGH7C75FN    Maryam  Fredrick    25/05/1966  Active

            // Create USI Request Start
            ApplicationType appType = getApplicationType("Maryam",
                    "Fredrick", "25/05/1966", "F",
                    "usi.sample.code+javasample@gmail.com", "0800000006",
                    "123 Fake Street", "2612", StateListType.ACT, "Turner",
                    getBirthCertDvs(), "Australia", "Australia", "Email",
                    "Australia");

            CreateUSIType createUsiRequest = new CreateUSIType();
            createUsiRequest.setApplication(appType);
            createUsiRequest.setOrgCode(OrgCode);
            createUsiRequest.setRequestId(String.valueOf(RANDOM.nextInt(999999999)));

            CreateUSIResponseType createUsiResponse = endpoint.createUSI(createUsiRequest);
            ApplicationResponseType appResponse = createUsiResponse.getApplication();

            System.out.println("----------Printing Create USI Request Result and USI");
            System.out.println(appResponse.getResult());
            System.out.println(appResponse.getUSI());

            String usi = null;
            if (appResponse.getResult().equals("MatchFound") || appResponse.getResult().equals("Success"))
                usi = appResponse.getUSI();

            if (appResponse.getErrors() != null
                    && appResponse.getErrors().getError().size() > 0) {
                System.out.println("----------Printing Error Messages");
                for (ErrorType err : appResponse.getErrors().getError()) {
                    System.out.println(err.getMessage());
                }
            }

            /* Create USI Request Finish */

            if (usi == null)
            {
                System.out.println("----------Cannot call Verify, due to errors from Create.");
                return;
            }
            
            /* Verify USI Request Begin */

            //BNGH7C75FN    Maryam  Fredrick    25/05/1966  Active
            verifyUSI.setDateOfBirth(xmlDate);
            verifyUSI.setFamilyName("Fredrick");
            verifyUSI.setFirstName("Maryam");
            verifyUSI.setOrgCode(OrgCode);
            verifyUSI.setUSI(usi);

            VerifyUSIResponseType response = endpoint.verifyUSI(verifyUSI);

            System.out.println("----------Printing Verify USI Request Result");

            System.out.println(response.getUSIStatus());
            System.out.println(response.getDateOfBirth().value());
            System.out.println(response.getFamilyName().value());
            System.out.println(response.getFirstName().value());

            /* Verify USI Request Finish */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ApplicationType getApplicationType(String firstName,
            String lastName, String dateOfBirth, String gender,
            String emailAddress, String phoneNumber, String address,
            String postCode, StateListType state, String suburbTownCity,
            DVSDocumentType document, String birthCountry, String countryStudy,
            String preferredMethod, String residentCountry) {

        ApplicationType appType = new ApplicationType();

        PersonalDetailsType personDetail = getPeronDetailsType(birthCountry,
                countryStudy, dateOfBirth, gender, firstName, null, lastName,
                null, suburbTownCity);
        NationalAddressType nationAddress = getNationalAddressType(address,
                null, suburbTownCity, StateListType.ACT, postCode);
        PhoneType phone = new PhoneType();
        phone.setHome(phoneNumber);

        ContactDetailsType contactDetail = getContactDetailsType(
                preferredMethod, residentCountry, emailAddress, phone, null,
                nationAddress);

        appType.setApplicationId(String.valueOf(RANDOM.nextInt(999999)));
        appType.setContactDetails(contactDetail);
        appType.setDVSCheckRequired(true);
        appType.setDVSDocument(document);
        appType.setPersonalDetails(personDetail);
        appType.setUserReference("CalledBySample");

        return appType;

    }

    private static ContactDetailsType getContactDetailsType(
            String preferredMethod, String countryOfResidence,
            String emailAddress, PhoneType phone, String internationalAddress,
            NationalAddressType nationalAddress) {
        ContactDetailsType contactDetail = new ContactDetailsType();

        contactDetail.setCountryOfResidenceCode("1101");
        contactDetail.setEmailAddress(emailAddress);
        contactDetail.setInternationalAddress(internationalAddress);
        contactDetail.setNationalAddress(nationalAddress);
        contactDetail.setPhone(phone);
        //contactDetail.setPreferredMethod(preferredMethod);

        return contactDetail;
    }

    private static NationalAddressType getNationalAddressType(String address1,
            String address2, String suburb, StateListType state, String postCode) {

        NationalAddressType addressType = new NationalAddressType();

        addressType.setAddress1(address1);
        addressType.setAddress2(address2);
        addressType.setPostCode(postCode);
        addressType.setState(state);
        addressType.setSuburbTownCity(suburb);

        return addressType;

    }

    private static PersonalDetailsType getPeronDetailsType(String birthCountry,
            String countryStudy, String dob, String gender, String firstName,
            String middleName, String familyName, String singleName,
            String townCityOfBirth) {
        GregorianCalendar cal = new GregorianCalendar();

        Date birthDate = null;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            birthDate = df.parse(dob);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cal.setTime(birthDate);
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarDate(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        PersonalDetailsType personDetail = new PersonalDetailsType();

        personDetail.setCountryOfBirthCode("1101");
        //personDetail.setCountryStudyingIn(countryStudy);
        personDetail.setDateOfBirth(xmlDate);
        personDetail.setFamilyName(familyName);
        personDetail.setFirstName(firstName);
        personDetail.setGender(gender);
        personDetail.setMiddleName(middleName);
        personDetail.setSingleName(singleName);
        personDetail.setTownCityOfBirth(townCityOfBirth);

        return personDetail;
    }

    private static DVSDocumentType getBirthCertDvs() {

        GregorianCalendar cal = new GregorianCalendar();

        Date datePrinted = new Date();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            datePrinted = df.parse(df.format(datePrinted));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        cal.setTime(datePrinted);
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendarDate(cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BirthCertificateDocumentType birthCert = new BirthCertificateDocumentType();

        birthCert.setCertificateNumber("1111111");
        birthCert.setDatePrinted(xmlDate);
        birthCert.setRegistrationDate(xmlDate);
        birthCert.setRegistrationNumber("1111111");
        birthCert.setRegistrationState(StateListType.NSW);
        //birthCert.setRegistrationYear(String.valueOf(cal.get(Calendar.YEAR)));

        return birthCert;

    }

    public static void copyFile(File fileToCopy, File destinationFile) {
        if (fileToCopy.isDirectory() || destinationFile.isDirectory()) {
            throw new IllegalArgumentException(
                    "Cannot copy directories, only files");
        }

        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        FileInputStream origFile = null;
        FileOutputStream copiedFile = null;
        try {
            if (!destinationFile.exists()) {
                destinationFile.getParentFile().mkdirs();
                destinationFile.createNewFile();
            }

            origFile = new FileInputStream(fileToCopy);
            copiedFile = new FileOutputStream(destinationFile);

            fromChannel = origFile.getChannel();
            toChannel = copiedFile.getChannel();

            fromChannel.transferTo(0, origFile.available(), toChannel);
        } catch (Exception e) {
            System.out.println("Couldn't create copy of file");
            e.printStackTrace();
            return;
        } finally {
            try {
                if (fromChannel != null)
                    fromChannel.close();
                if (toChannel != null)
                    toChannel.close();
                if (origFile != null)
                    origFile.close();
                if (copiedFile != null)
                    copiedFile.close();
            } catch (IOException e) {
                System.out.println("Couldn't close Input Streams");
                e.printStackTrace();
            }

        }
    }

}
