package usi.gov.au;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.FileReader;
import java.io.IOException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.BindingProvider;

//import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.sun.xml.ws.addressing.W3CAddressingConstants;
import com.sun.xml.ws.api.security.trust.client.STSIssuedTokenConfiguration;
import com.sun.xml.ws.api.security.trust.client.SecondaryIssuedTokenParameters;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.security.Token;
import com.sun.xml.ws.security.trust.GenericToken;
import com.sun.xml.ws.security.trust.STSIssuedTokenFeature;
import com.sun.xml.ws.security.trust.WSTrustVersion;
import com.sun.xml.ws.security.trust.impl.client.DefaultSTSIssuedTokenConfiguration;
import com.sun.xml.ws.security.trust.impl.client.SecondaryIssuedTokenParametersImpl;
import com.sun.xml.wss.XWSSConstants;

import au.gov.abr.akm.credential.store.ABRProperties;
import au.gov.abr.akm.credential.store.ABRCredential;
import au.gov.abr.akm.credential.store.ABRKeyStore;
import au.gov.usi._2022.ws.servicepolicy.IUSIService;
import au.gov.usi._2022.ws.servicepolicy.USIService;
//import au.gov.usi._2018.ws.servicepolicy.IUSIService;
//import au.gov.usi._2018.ws.servicepolicy.USIService;
import com.sun.xml.wss.XWSSecurityException;
import com.sun.xml.wss.impl.MessageConstants;
import com.sun.xml.wss.saml.util.SAMLUtil;
import org.w3c.dom.Element;

import java.util.Properties;


public class UsiServiceChannel {
    public static String strOrganisation = "USI";
    public static String strProduct = "UsiSampleCode";
    public static String strVersion = "0.1";
    public static String strSoftwareTimeStamp = "27 Mar 2020 01:00:00";

    private static final int CONNECT_TIMEOUT = 60000;
    private static final int REQUEST_TIMEOUT = 60000;

    //private static String M2M_KEYSTORE = "keystore/KeyStore.xml";
    //private static String M2M_ALIAS = "ABRD:12300000059_TestDevice03";
    private static String M2M_PASSWORD = "Password1!";
    private static String M2M_KEYSTORE = "keystore/keystore-usi.xml";
    private static String M2M_ALIAS_CLOUD= "ABRD:11000002568_INGLETON153"; // orgcode VA1802 - use with ActAs
    private static String M2M_ALIAS_LOCAL = "ABRD:27809366375_USIMachine"; // orgcode VA1803 - do not use with ActAs
    private static String ORGCODE_CLOUD = "VA1802";
    private static String ORGCODE_LOCAL = "VA1803";

    private static boolean useSts13 = false;
    private static boolean useCloud = false;
    private static String M2M_ALIAS = useCloud ? M2M_ALIAS_CLOUD : M2M_ALIAS_LOCAL;
    private static String ORGCODE = useCloud ? ORGCODE_CLOUD : ORGCODE_LOCAL;

    static Properties getPropFile(String file) {
        Properties p = new Properties();
        try {
            FileReader reader = new FileReader(file);
            p.load(reader);
            reader.close();
            System.out.println("Loaded " + file);
        } catch (FileNotFoundException e) {
            System.out.println("Skipped" + file + " - not found");
            p = null;
        } catch (IOException e) {
            p = null;
        }
        return p;
    }
    static {
        String v = null;
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Properties p = getPropFile("appgen.properties");
        if (p != null) {
            v = p.getProperty("sts_version");
            if (v != null) { useSts13 = v.equals("13"); }
        }

        p = getPropFile("application.properties");
        if (p != null) {
            v = p.getProperty("keystore");
            if (v != null) { M2M_KEYSTORE = v; }
        
            v = p.getProperty("alias_local");
            if (v != null) { M2M_ALIAS_LOCAL = v; }
            v = p.getProperty("alias_cloud");
            if (v != null) { M2M_ALIAS_CLOUD = v; }
        
            v = p.getProperty("sts_mode");
            if (v != null) { useCloud = v.equals("cloud"); }
            M2M_ALIAS = useCloud ? M2M_ALIAS_CLOUD : M2M_ALIAS_LOCAL;
            ORGCODE = useCloud ? ORGCODE_CLOUD : ORGCODE_LOCAL;
        }
        
        System.out.println("Using sts " + (useSts13 ? "3" : "2") + " mode " + (useCloud ? "cloud" : "local"));
        System.out.println("keystore = " + M2M_KEYSTORE + "; " + "alias_local = " + M2M_ALIAS_LOCAL + "; " + "alias_cloud = " + M2M_ALIAS_CLOUD + "; ");
    }

    public static String getOrgCode() {
        return ORGCODE;
    }
    public static IUSIService GetNewClient(String orgCode){
        try {
        skipSSLVerification();
        soapTracing();
        //EnableProxy_FOR_DEBUG_ONLY();

        PrivateKey privateKey = GetAUSkey_PrivateKey();
        X509Certificate certificate = GetAUSkey_Cert();

        USIService service = new USIService();

        DefaultSTSIssuedTokenConfiguration config = new DefaultSTSIssuedTokenConfiguration();
        Map<String, Object> otherOptions = config.getOtherOptions();

        if (useSts13) {
            config.setSignatureAlgorithm("SHA256withRSA");
        }

        //config.setKeySize(256);
        if (useCloud) {
            // can put the ActAs token here or below
            Token actAs = getActAs();
            otherOptions.put(STSIssuedTokenConfiguration.ACT_AS, actAs);
        }
            STSIssuedTokenFeature feature = new STSIssuedTokenFeature(config);
        IUSIService endpoint = service.getWS2007FederationHttpBindingIUSIService(feature);

        SetupRequestContext(endpoint, certificate, privateKey);
        return endpoint;

        } catch (Exception ex)  {
            ex.printStackTrace();
            return null;
        }
    }

    // DO NOT USE FOR PRODUCTION CODE
    private static void skipSSLVerification() throws NoSuchAlgorithmException, KeyManagementException {
            // cert and url dont always say the same thing, ergo you gotta be able tell it to ignore it
        final HostnameVerifier hv = new HostnameVerifier()
            {
                public boolean verify(String urlHostName, SSLSession session)
                {
                    if(urlHostName!=null && session.getPeerHost()!=null)
                    {
                        if(!(urlHostName.equals(session.getPeerHost())))
                        {
                            //if they didn't match log it.
                            //LoggingUtilities.log(LoggingUtilities.Level.WARNING, m_logger, "SSL certificate and given URL host name do not match. URL Host: " + urlHostName + "  vs " + session.getPeerHost());
                        }
                    }
                    //we're ignoring the verification if it failed anyway so return true.
                    return true;

                }
            };

            // Create a trust manager that does not validate certificate chains like the default TrustManager
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            //No need to implement.
                        }

                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                            //No need to implement.
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory factory = sc.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(factory);
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        }

    private static X509Certificate GetAUSkey_Cert() {
        try
        {
            File keystorefile = new File(M2M_KEYSTORE).getAbsoluteFile();

            if (!keystorefile.exists()) {
                throw new FileNotFoundException(keystorefile.getCanonicalPath());
            }

            FileInputStream fileInputStreamKeystore = new FileInputStream(keystorefile);
            ABRKeyStore keyStore = new ABRKeyStore(fileInputStreamKeystore);
    
            ABRCredential abrCredential = keyStore.getCredential(M2M_ALIAS);
            X509Certificate[] certificate = abrCredential.getCertificateChain();
            return certificate[0];

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static PrivateKey GetAUSkey_PrivateKey() {
        try
        {
            File keystorefile = new File(M2M_KEYSTORE).getAbsoluteFile();

            if (!keystorefile.exists()) {
                throw new FileNotFoundException(keystorefile.getCanonicalPath());
            }

            FileInputStream fileInputStreamKeystore = new FileInputStream(keystorefile);

            ABRProperties.setSoftwareInfo(strOrganisation, strProduct, strVersion, strSoftwareTimeStamp);
            ABRProperties keystoreProperties = new ABRProperties();
            ABRKeyStore keyStore = new ABRKeyStore(fileInputStreamKeystore, keystoreProperties);
            ABRCredential abrCredential = keyStore.getCredential(M2M_ALIAS);

            if (abrCredential.isReadyForRenewal()) {
                System.out.println("credential is ready for renewal");
            }

            return abrCredential.getPrivateKey(M2M_PASSWORD.toCharArray());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static void SetupRequestContext(IUSIService endpoint, X509Certificate certificate, PrivateKey privateKey) throws XMLStreamException, XWSSecurityException {

        Map<String, Object> requestContext = ((BindingProvider)endpoint).getRequestContext();

        requestContext.put(XWSSConstants.CERTIFICATE_PROPERTY, certificate);
        requestContext.put(XWSSConstants.PRIVATEKEY_PROPERTY, privateKey);

        if (useCloud) {
            // or can do above
            //Token actAs = getActAs();
            //requestContext.put(STSIssuedTokenConfiguration.ACT_AS, actAs);
            requestContext.put(STSIssuedTokenConfiguration.SHARE_TOKEN, false); // Prevents caching (sharing) token in multi tenanted applications.
        }


        requestContext.put(STSIssuedTokenConfiguration.LIFE_TIME, 20*60*1000); // minutes*60*1000 (milliseconds). This will override the WSDL
        requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, REQUEST_TIMEOUT);
        requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
    }
    private static Token getActAs() throws XMLStreamException, XWSSecurityException {
        return getActAs("11000002568","96312011219","0000123400");
    }
    private static Token getActAs(String FirstParty, String SecondParty, String SSID) throws XMLStreamException, XWSSecurityException {
        // Sure we COULD build a DOM to construct the ActAs. Or not.
        String actAs =  "" +
                "        <v13:RelationshipToken xmlns:v13=\"http://vanguard.business.gov.au/2016/03\" ID=\"35e6d176-bcf0-c7ac-c98d-5eae177e414d\">\n" +
                "          <v13:Relationship v13:Type=\"OSPfor\">\n" +
                "            <v13:Attribute v13:Name=\"SSID\" v13:Value=\"${SSID}\"/>\n" +
                "          </v13:Relationship>\n" +
                "          <v13:FirstParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"${FirstParty}\"/>\n" +
                "          <v13:SecondParty v13:Scheme=\"uri://abr.gov.au/ABN\" v13:Value=\"${SecondParty}\"/>\n" +
                "        </v13:RelationshipToken>\n" +
                "";
        actAs = actAs.replace("${FirstParty}", FirstParty);
        actAs = actAs.replace("${SecondParty}", SecondParty);
        actAs = actAs.replace("${SSID}", SSID);
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(actAs));
        Element actAsElt = SAMLUtil.createSAMLAssertion(reader);
        return new GenericToken(actAsElt);
    }
    
    public static void soapTracing() {
        // Suggested by pwillia6  (https://github.com/bartland-usi/sample-java/issues/4)
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true"); 
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true"); 
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true"); 
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true"); 
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dumpTreshold", "999999");
    }
    
    public static void EnableProxy_FOR_DEBUG_ONLY(){
        String MY_PROXY_HOST = "localhost";
        String MY_PROXY_PORT = "8080";
        //This method assumes:
        //  * you're running the proxy on localhost:8080 and your've added its SSL inspection cert to the java keystore
        System.out.println("***********************************************************");
        System.out.println("WARNING: ***** Using proxy [" + MY_PROXY_HOST + ":" + MY_PROXY_PORT + "] *****");
        System.out.println("***********************************************************");
        System.setProperty("https.proxyHost", MY_PROXY_HOST);
        System.setProperty("https.proxyPort", MY_PROXY_PORT);
        System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
        //System.setProperty("javax.net.ssl.trustStore", "E:\\pf\\Java\\jdk\\jre\\lib\\security\\cacerts");
        //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }
    
}
