package tools.userAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;

import tools.commonTools.CommonTools;

/**********************************************************************************************
 * This class provides account management operations via PAM service.
 * 
 * 1. Create new user account by URLConnection.
 * 2. Store the account information in src/test/data/test-accounts-pool.json
 * 
 * Reviewed by Soneye Lv 2012-09-21
 * 
 **********************************************************************************************/

public class AccountManagement {
    private static String                         epcClientId;

    private static String                         server;
    private static String                         basePath;
    private static String                         baseUrl;

    private static String                         loginUrl;
    private static String                         createUrl;
    private static String                         profileUrl;
    private static String                         updateProfileUrl;
    private static String                         updatePasswordUrl;
    private static String                         setPreferenceUrl;
    private static String                         getPreferenceUrl;
    private static String                         deleteUserUrl;
    private static String                         resetPasswordUrl;
    private static String                         forgotPasswordTokenUrl;
    private static String                         forgotPasswordTokenUrlWithExpiry;
    private static String                         userByEmailUrl;
    private static String                         validatePasswordTokenUrl;
    private static String                         userExistenceUrl;
    private static String                         unifyUserUrl;
    private static String                         updatePrimaryEmailUrl;
    private static String                         termsAndConditionsUrl;
    private static String                         authorizeUserUrl;
    private static String                         authLevelsUrl;
    private static String                         verifyEmailUrl;
    private static String                         serviceId;
    private static String                         clientSecret;
    private static String                         pamUserPreferenceKey;
    

    private static JsonNode                       initialConfig;
    private static JsonNode                       sut;

    private static TrustManager[]                 trustAllCerts                = null;
    private static HostnameVerifier               allHostsValid                = null;

    private static Map<String, AccountDescriptor> allAccounts                  = new ConcurrentHashMap<String, AccountDescriptor>();
    private File                                  accountsFile                 = null;
    private static boolean                        storeAccounts                = false;

    public static boolean                         isInitialized;

    private static String                         CONTENT_TYPE_HEADER          = "Content-Type";
    private static String                         CONTENT_TYPE_VALUE           = "application/xml; charset=\"utf-8\"";

    private static final String                   XML_HEADER                   = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
    private static final String                   LOGIN_REQUEST                = XML_HEADER
                                                                                       + "<credential xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<emailAddress>%s</emailAddress>"
                                                                                       + "<password>%s</password></credential>";
    private static final String                   USER_REGISTER_REQUEST        = XML_HEADER
                                                                                       + "<minimumUserData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<credential>" + "<emailAddress>%s</emailAddress>"
                                                                                       + "<password>%s</password>" + "</credential>"
                                                                                       + "<basicData>" + "<firstName>%s</firstName>"
                                                                                       + "<lastName>%s</lastName>"
                                                                                       + "<snapfishOptIn>%s</snapfishOptIn>"
                                                                                       + "<partnerOptIn>%s</partnerOptIn>"
                                                                                       + "<countryCode>%s</countryCode>"
                                                                                       + "<languageCode>%s</languageCode>"
                                                                                       + "<userName>%s</userName>" + "</basicData>"
                                                                                       + "<termsAndConditions>" + "<title>%s</title>"
                                                                                       + "<version>%s</version>"
                                                                                       + "<accepted>%s</accepted>"
                                                                                       + "</termsAndConditions>" + "</minimumUserData>";
    private static final String					   SNAPFISH_USER_REGISTER_REQUEST = XML_HEADER + "<epcUserRegisterRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
    																					+ "<firstName>%s</firstName>"
    																					+ "<lastName>%s</lastName>"
    																					+ "<email>%s</email>"
    																					+ "<password>%s</password>"
    																					+ "<snapfishOptIn>false</snapfishOptIn>"
    																					+ "<partnerOptIn>false</partnerOptIn>"
    																					+ "<userName></userName>"
    																					+ "<birthMonth>0</birthMonth>"
    																					+ "<birthYear>0</birthYear>"
    																					+ "<securityQuestion></securityQuestion>"
    																					+ "<securityAnswer></securityAnswer>"
    																					+ "<countryCode>%s</countryCode>"
    																					+ "<languageCode>en</languageCode>"
    																					+ "<favoriteTeam></favoriteTeam>"
    																					+ "<tsAgreementTitle></tsAgreementTitle>"
    																					+ "<tsAgreementVersion></tsAgreementVersion>"
    																					+ "<tsAgreementAccepted>false</tsAgreementAccepted>"
    																					+ "<tsResponseDate></tsResponseDate>"
    																					+ "</epcUserRegisterRequest>";
    private static final String					  HPWS_USER_REGISTER_REQUEST   = XML_HEADER
    																				   +"<minimumUserData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
    																				   + "<credential>"	
    																				   + "<emailAddress>%s</emailAddress>" 
    																				   + "<password>%s</password>" 
    																				   + "</credential>" 
    																				   + "<basicData>"
    																				   +"<firstName>%s</firstName>" 
    																				   + "<lastName>%s</lastName>" 
    																				   + "<snapfishOptIn>true</snapfishOptIn>"
    																				   + "<partnerOptIn>true</partnerOptIn>" 
    																				   + "<countryCode>%s</countryCode>" 
    																				   + "<languageCode>en</languageCode>" 
    																				   + "<userName/>"
    																				   + "</basicData>" 
    																				   + "<termsAndConditions>" 
    																				   + "<title/>" 
    																				   + "<version>1.0</version>" 
    																				   + "<accepted>false</accepted>"
    																				   + "</termsAndConditions>" 
    																				   + "</minimumUserData>";
    private static final String                   UPDATE_USER_DATA_REQUEST     = XML_HEADER
                                                                                       + "<basicData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<firstName>%s</firstName><lastName>%s</lastName><snapfishOptIn>%s</snapfishOptIn><partnerOptIn>%s</partnerOptIn>"
                                                                                       + "<countryCode>%s</countryCode><languageCode>%s</languageCode><userName>%s</userName></basicData>";
    private static final String                   UPDATE_PASSWORD_REQUEST      = XML_HEADER
                                                                                       + "<updatePasswordRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<oldPassword>%s</oldPassword>"
                                                                                       + "<newPassword>%s</newPassword>"
                                                                                       + "</updatePasswordRequest>";
    private static final String                   UPDATE_PAYMENT_REQUEST       = XML_HEADER
                                                                                       + "<updatePaymentRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<cardType>%s</cardType>"
                                                                                       + "<cardHolderName>%s</cardHolderName>"
                                                                                       + "<cardNumber>%s</cardNumber>"
                                                                                       + "<expirationMonth>%s</expirationMonth>"
                                                                                       + "<expirationYear>%s</expirationYear>"
                                                                                       + "<street1>%s</street1>" + "<street2>%s</street2>"
                                                                                       + "<street3>%s</street3>" + "<city>%s</city>"
                                                                                       + "<state>%s</state>" + "<zip>%s</zip>"
                                                                                       + "<countryCode>%s</countryCode>"
                                                                                       + "<phoneNumber1>%s</phoneNumber1>"
                                                                                       + "<phoneNumber2>%s</phoneNumber2>"
                                                                                       + "<cvv2>%s</cvv2>" + "</updatePaymentRequest>";
    private static final String                   ADDRESS_REQUEST              = XML_HEADER
                                                                                       + "<addressRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<street1>%s</street1>" + "<street2>%s</street2>"
                                                                                       + "<street3>%s</street3>" + "<city>%s</city>"
                                                                                       + "<state>%s</state>"
                                                                                       + "<postalCode>%s</postalCode>"
                                                                                       + "<countryCode>%s</countryCode>"
                                                                                       + "<county>%s</county>"
                                                                                       + "<addressLabel>%s</addressLabel>"
                                                                                       + "</addressRequest>";
    private static final String                   SET_PREFERENCE_REQUEST       = XML_HEADER
                                                                                       + "<preference xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<key>%s</key>" + "<value>%s</value>"
                                                                                       + "</preference>";
    private static final String                   RESET_PASSWORD_REQUEST       = XML_HEADER
                                                                                       + "<credential xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<password>%s</password></credential>";
    private static final String                   UNIFY_USER_REQUEST           = XML_HEADER
                                                                                       + "<unificationData xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                                                                                       + "<partnerOptIn>%s</partnerOptIn>"
                                                                                       + "<newPassword>%s</newPassword>"
                                                                                       + "<epcPassword>%s</epcPassword>"
                                                                                       + "<sfPassword>%s</sfPassword>"
                                                                                       + "<termsAndConditions>" + "<title>%s</title>"
                                                                                       + "<version>%s</version>"
                                                                                       + "<accepted>%s</accepted>"
                                                                                       + "</termsAndConditions>" + "</unificationData>";

    private static final String                   UPDATE_PRIMARY_EMAIL_REQUEST = XML_HEADER
                                                                                       + "<credential xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"> <emailAddress>%s</emailAddress></credential>";
    protected final static int                    RANDOM_MAX                   = 999999;
    public String                                 PUC;
    public String                                 UID;

    public AccountManagement() {

        initialConfig = CommonTools.getInitialConfig();

        server=CommonTools.getConfigValue(sut, "papi.accountManagement.server");
        serviceId = CommonTools.getConfigValue(sut,"papi.accountManagement.serviceId");
        epcClientId = CommonTools.getConfigValue(sut,"papi.clientId");
        clientSecret = CommonTools.getConfigValue(sut,"papi.clientSecret");
        
        if (CommonTools.getConfigValue(initialConfig,"storeAccount").toLowerCase().matches("true")) {
            storeAccounts = true;
            accountsFile = new File(CommonTools.getTestRoot() + CommonTools.getConfigValue(initialConfig,"account.file"));
        }

        initUrls();
        setTrustManager();
        CommonTools.configProxySettings();
    }

    public void initUrls() {

        if (!isInitialized) {

            basePath = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.basePath");
            baseUrl = String.format("%s/%s", server, basePath);
            loginUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.login.service.2");
            createUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.create.service.2");
            profileUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.profile.service");
            updateProfileUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.profile.update.service");
            updatePasswordUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.password.service");
            setPreferenceUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.setPreference.service");
            getPreferenceUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.getPreference.service");
            deleteUserUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.delete.service");
            resetPasswordUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.password.reset.service");
            forgotPasswordTokenUrl =CommonTools.getConfigValue(initialConfig,"papi.accountManagement.password.forgot.service");
            forgotPasswordTokenUrlWithExpiry = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.password.forgot.serviceWithExpiry");
            userByEmailUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.getUser.byMail");
            validatePasswordTokenUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.validate.password.token.service");
            unifyUserUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.unify.service");
            userExistenceUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.userExistence.service");
            updatePrimaryEmailUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.primaryEmail.update.service");
            termsAndConditionsUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.tnc.service");
            authorizeUserUrl = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.authorizeUser.service");
            authLevelsUrl =CommonTools.getConfigValue(initialConfig,"papi.accountManagement.authLevels.service");
            verifyEmailUrl =CommonTools.getConfigValue(initialConfig,"papi.accountManagement.verifyEmail.service");
            if (pamUserPreferenceKey == null)
                pamUserPreferenceKey = CommonTools.getConfigValue(initialConfig,"papi.accountManagement.userPreference.key");
            isInitialized = true;

        }
    }

    private void setTrustManager() {

        // Create a trust manager that does not validate certificate chains
        trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        // Create all-trusting host name verifier
        allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

    }

    public String getLoginUrl(Boolean emailVerified, Boolean verifyEmail) {
        String fullUrl = makeFullUrl(loginUrl);
        return String.format(fullUrl, epcClientId, String.valueOf(verifyEmail), String.valueOf(emailVerified));
    }

    public String getCreateUrl() {
        String fullUrl = makeFullUrl(createUrl);
        return String.format(fullUrl, epcClientId);
    }

    public String getProfileUrl(String userId) {
        String fullUrl = makeFullUrl(profileUrl);
        return String.format(fullUrl, userId);
    }

    public String getUpdateProfileUrl(String userId) {
        String fullUrl = makeFullUrl(updateProfileUrl);
        return String.format(fullUrl, userId);
    }

    public String getUpdatePasswordUrl(String userId) {
        String fullUrl = makeFullUrl(updatePasswordUrl);
        return String.format(fullUrl, userId);
    }

    public String getDeleteUserUrl(String userId) {
        String fullUrl = makeFullUrl(deleteUserUrl);
        return String.format(fullUrl, userId);
    }

    public String getResetPasswordUrl(String resetId) {
        String fullUrl = makeFullUrl(resetPasswordUrl);
        return String.format(fullUrl, resetId);
    }

    public String getForgotPasswordTokenUrl(String emailId, Integer expiry) {
        if (expiry == null) {
            String fullUrl = makeFullUrl(forgotPasswordTokenUrl);
            return String.format(fullUrl, emailId);
        }
        else {
            String fullUrl = makeFullUrl(forgotPasswordTokenUrlWithExpiry);
            return String.format(fullUrl, emailId, expiry);
        }
    }

    public String getUserByEmailUrl(String emailId) {
        String fullUrl = makeFullUrl(userByEmailUrl);
        return String.format(fullUrl, emailId);
    }

    public String getValidatePasswordTokenUrl(String token) {
        String fullUrl = makeFullUrl(validatePasswordTokenUrl);
        return String.format(fullUrl, token);
    }

    public String getUnifyUserUrl(String emailId, String emailVerified) {
        String fullUrl = makeFullUrl(unifyUserUrl);
        return String.format(fullUrl, emailId, epcClientId, emailVerified);
    }

    public String getUserExistenceUrl(String email) {
        String fullUrl = makeFullUrl(userExistenceUrl);
        return String.format(fullUrl, email, epcClientId);
    }

    public String getUpdatePrimaryEmailUrl(String userId) {
        String fullUrl = makeFullUrl(updatePrimaryEmailUrl);
        return String.format(fullUrl, userId);
    }

    public String getTermsAndConditionsUrl(String userId) {
        String fullUrl = makeFullUrl(termsAndConditionsUrl);
        return String.format(fullUrl, userId);
    }

    public String getAuthorizeUserUrl(String userId) {
        String fullUrl = makeFullUrl(authorizeUserUrl);
        return String.format(fullUrl, epcClientId, userId);
    }

    public String getAuthLevelsByEmailUrl(String emailAddress) {
        String fullUrl = makeFullUrl(authLevelsUrl);
        return String.format(fullUrl, emailAddress);
    }

    public String getVerifyEmailUrl(String userId, String authCode, Boolean verifyEmail) {
        String fullUrl = makeFullUrl(verifyEmailUrl);
        return String.format(fullUrl, authCode, epcClientId, userId, verifyEmail);
    }

    /**
     * Builds a full URL given a service path
     * 
     * @param servicePath
     *            the service path (i.e: login)
     * @return the full url, i.e:
     *         http://ukas-dev-app06.usa.hp.com:8080/accountweb/services/v1.0/account/login
     */

    protected String makeFullUrl(String servicePath) {
        return String.format("%s/%s", baseUrl, servicePath);
    }

    private URLConnection createRequest(String tempUrl) {

        try {

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL url = new URL(tempUrl);
            URLConnection conn = url.openConnection();
            URLConnection http = (URLConnection) conn;
            conn.setDefaultUseCaches(false);
            conn.setRequestProperty(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE);
            if (!StringUtils.isEmpty(PUC)) {
                conn.setRequestProperty("Authorization", PUC);
            }
            else {
                conn.setRequestProperty("Authorization", "Basic ZXBjc2lwczplUEM0ZXZhaA==");
            }
            return http;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, String> executePostRequest(URLConnection conn, String xmlPayload) {

        String data = "";

        Map<String, String> response = new HashMap<String, String>();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        System.out.println(String.format("PAM Request - url: %s - payload: %s", conn.getURL(), xmlPayload));
        try {
            conn.connect();
            OutputStream os = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(xmlPayload.getBytes());
            dos.close();
            dos.flush();
            InputStream is = conn.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte d[] = new byte[dis.available()];
            dis.read(d);
            dis.close();
            data = new String(d);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        HttpURLConnection httpCon = (HttpURLConnection) conn;
        try {
            response.put("responseBody", data);
            response.put("responseStatus", String.valueOf(httpCon.getResponseCode()));
            response.put("responseMessage", httpCon.getResponseMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }

    private void updateAccountsFile(RegisteringUser user) {

        Map<String, String> properties = AccountDescriptor.makeAccountPropertiesFromRegisteringUser(user);
        AccountDescriptor account = new AccountDescriptor(properties);

        try {
            List<AccountDescriptor> accountList = AccountsFileManager.loadAccountsFromFile(accountsFile).accounts;

            for (AccountDescriptor tempAccount : accountList)
                allAccounts.put(tempAccount.getAccount(), tempAccount);
        }
        catch (Exception e) {
            // TODO: handle exception
        }

        allAccounts.put(user.getEmail(), account);

        try {
            AccountsFileManager.saveAccountsToFile(new AccountsListDescriptor(new ArrayList<AccountDescriptor>(allAccounts.values())),
                    accountsFile);
        }
        catch (Exception e) {
            System.out.println("Could not update account list file - See exception");
            e.printStackTrace();
        }
    }

    /**
     * Create a new user account via API.
     * 
     * @return The user object (populated) if successful; otherwise, a user object with blank email, name & password.
     */
    public static RegisteringUser createNewUserAccount(RegisteringUser user) {
        
        AccountManagement AM = new AccountManagement();
        
        try {
            if (AM.create(user, true).get("responseStatus").startsWith("20")) 
                return user;
        }
        catch (Exception e) {}

        /*
         * Clear out the user email, name and password if we failed to create the account and log an error.
         */
        
        user.setEmail("");
        user.setFirstName("");
        user.setLastName("");
        user.setPassword("");
        
        System.out.println("ERROR: createNewUserAccount() - Could not create new user account for " + user.getEmail() + ".");
        return user;
    }    

    /**
     * Create default new user for HPC.
     */
    public static RegisteringUser createNewUserAccount() {
    	
    	RegisteringUser user = RegisteringUser.generateNewRegistratingUser();
        return createNewUserAccount(user);
    }
    
    /**
     * Create new UK user for HPC.
     */
    public static RegisteringUser createNewUserAccount_UK() {

    	RegisteringUser user = RegisteringUser.generateNewRegistratingUser();
        user.setCountryCode("UK");
    	return createNewUserAccount(user);
    }
    
    /**
     * The instant ink account name should start with "test2" and first name should also start with "Test". So we should 
     * add a method for II user to distinguish between HPC and Instant Ink.
     */
    public static RegisteringUser createNewUserAccountForII() {
    	
    	String randomId=Long.toString(new Date().getTime());
    	
    	RegisteringUser user = RegisteringUser.generateNewRegistratingUser();
    	user.setFirstName("Test");
    	user.setLastName("InstantInk");
    	user.setEmail(("test2.ii_user." + randomId + "@hp.com"));
        return createNewUserAccount(user);
    }
    
	public static boolean deactivateUser(RegisteringUser user) {

		AccountManagement ac = new AccountManagement();
		ac.login(user);
		HttpsURLConnection conn = (HttpsURLConnection) ac.createRequest(ac.getDeleteUserUrl(ac.UID));
		try {
			conn.setRequestMethod("DELETE");
		}
		catch (ProtocolException e) {
			e.printStackTrace();
		}

		System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running deactivateUser Request ================");

		HttpURLConnection http = (HttpURLConnection) conn;
		try {
			if (String.valueOf(http.getResponseCode()).startsWith("204"))

				System.out.println("successfully deactivated new user");
			return true;
		}
		catch (Exception e) {

			System.out.println("ERROR: deactivateNewUser() - Could not deactivate new user account");
		}

		return false;

	}
    
    /**
     * Generate a unique user account email address with name "Otto Tester" and password "password123".
     * 
     * @return User information that can be used to Sign Up.
     * 
     * NOTE:  Does NOT actually create the user account; use createNewUserAccount() for that.
     */
    public static RegisteringUser getSignUpInfo() {
        
        RegisteringUser signUpInfo = RegisteringUser.generateNewRegistratingUser(); 
        return signUpInfo;
    }
    
    public Map<String, String> create(RegisteringUser newRegUser, Boolean termsAndConditions) {
        String xmlPayload = String.format(USER_REGISTER_REQUEST, newRegUser.getEmail(), newRegUser.getPassword(),
                newRegUser.getFirstName(), newRegUser.getLastName(), newRegUser.isSnapfishOptIn(), newRegUser.isPartnerOptIn(),
                newRegUser.getCountryCode(), newRegUser.getLanguageCode(), "",
                CommonTools.getConfigValue(initialConfig,"papi.accountManagement.terms.and.conditions.title"),
                CommonTools.getConfigValue(initialConfig,"papi.accountManagement.terms.and.conditions.version"), String.valueOf(termsAndConditions));
        URLConnection conn = createRequest(getCreateUrl());

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Create Account Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Account Successfully Generated: <" + newRegUser.getEmail() + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");
            if (storeAccounts) {
                updateAccountsFile(newRegUser);
            }
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }

    public Map<String, String> createSnapfishUser(RegisteringUser newRegUser) {
        String xmlPayload = String.format(SNAPFISH_USER_REGISTER_REQUEST, newRegUser.getFirstName(), newRegUser.getLastName(), newRegUser.getEmail(), newRegUser.getPassword(), newRegUser.getCountryCode());
        URLConnection conn = createRequest("https://accountweb-dev2.hpconnecteddev.com/accountweb/services/v1.0/account/create");

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Create Account Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Account Successfully Generated: <" + newRegUser.getEmail() + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");
            if (storeAccounts) {
                updateAccountsFile(newRegUser);
            }
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }
    
 
    public Map<String, String> createHPWSUser(RegisteringUser newRegUser) {
        String xmlPayload = String.format(HPWS_USER_REGISTER_REQUEST, newRegUser.getEmail(), newRegUser.getPassword(),newRegUser.getFirstName(), newRegUser.getLastName(), newRegUser.getCountryCode());
        URLConnection conn = createRequest("https://pam-dev2.hpconnecteddev.com/pam/services/pam/users?clientId=dev02test");

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Create Account Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Account Successfully Generated: <" + newRegUser.getEmail() + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");
            if (storeAccounts) {
                updateAccountsFile(newRegUser);
            }
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }
    
	//Create Snapfish Account
  	public static RegisteringUser createSnapfishAccount() {

  		RegisteringUser user = RegisteringUser.generateNewGmailUser();
  		AccountManagement AM = new AccountManagement();

  		try {
  			if (AM.createSnapfishUser(user).get("responseStatus").startsWith("20"))
  				return user;
  		}
  		catch (Exception e) {
  		}
  		return user;
  	}
	//Create UK Snapfish Account
  	public static RegisteringUser createSnapfishAccount_UK(){
  		
  		RegisteringUser user = RegisteringUser.generateNewGmailUser_UK();
  		AccountManagement AM = new AccountManagement();

  		try {
  			if (AM.createSnapfishUser(user).get("responseStatus").startsWith("20"))
  				return user;
  		}
  		catch (Exception e) {
  		}
  		return user;
  	}
    

	//create EPC1 user
	public static RegisteringUser createHPWSUserAccount() {

		RegisteringUser user = RegisteringUser.generateNewGmailUser();
		AccountManagement AM = new AccountManagement();

		try {
			if (AM.createHPWSUser(user).get("responseStatus").startsWith("20"))
				user.setEmail(user.getEmail().replace("%2B", "+"));
				return user;
		}
		catch (Exception e) {
		}
		return user;
	}
    
	//create EPC1 UK user
	public static RegisteringUser createHPWSUserAccount_UK() {

		RegisteringUser user = RegisteringUser.generateNewGmailUser_UK();
		AccountManagement AM = new AccountManagement();

		try {
			if (AM.createHPWSUser(user).get("responseStatus").startsWith("20"))
				user.setEmail(user.getEmail().replace("%2B", "+"));
				return user;
		}
		catch (Exception e) {
		}
		return user;
	}
	
    public Map<String, String> login(RegisteringUser newRegUser) {
        String xmlPayload = String.format(LOGIN_REQUEST, newRegUser.getEmail(), newRegUser.getPassword());
        URLConnection conn = createRequest(getLoginUrl(true, true));

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Login Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Sign In Account: <" + newRegUser.getEmail() + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");

            /* If login succeed, get the UID and PUC for this user */

            String tempStr = response.get("responseBody");
            tempStr = tempStr.substring(tempStr.indexOf("<userId>") + 8);
            UID = tempStr.substring(0, tempStr.indexOf("</userId>"));

            tempStr = tempStr.substring(tempStr.indexOf("<accessToken>") + 13);
            tempStr = tempStr.substring(0, tempStr.indexOf("</accessToken>"));
            PUC = "Bearer " + tempStr;
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }

    public Map<String, String> login(String userEmail, String userPassword) {
        String xmlPayload = String.format(LOGIN_REQUEST, userEmail, userPassword);
        URLConnection conn = createRequest(getLoginUrl(true, true));

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Login Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Sign In Account: <" + userEmail + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");

            /* If login succeed, get the UID and PUC for this user */

            String tempStr = response.get("responseBody");
            tempStr = tempStr.substring(tempStr.indexOf("<userId>") + 8);
            UID = tempStr.substring(0, tempStr.indexOf("</userId>"));

            tempStr = tempStr.substring(tempStr.indexOf("<accessToken>") + 13);
            tempStr = tempStr.substring(0, tempStr.indexOf("</accessToken>"));
            PUC = "Bearer " + tempStr;
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }

    public Map<String, String> updatePrimaryEmail(RegisteringUser user, String newEmail) {
        String xmlPayload = String.format(UPDATE_PRIMARY_EMAIL_REQUEST, newEmail);
        login(user);
        HttpsURLConnection conn = (HttpsURLConnection) createRequest(getUpdatePrimaryEmailUrl(UID));
        try {
            conn.setRequestMethod("PUT");
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }

        System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running UpdatePrimaryEmail Request ================");
        Map<String, String> response = executePostRequest(conn, xmlPayload);
        if (response.get("responseStatus").startsWith("20")) {
            System.out.println("[Status: " + response.get("responseStatus") + "], [Message: " + response.get("responseMessage")
                    + "], [Updated Primary Email from: <" + user.getEmail() + "> to <" + newEmail + ">]");
            System.out.println("[ResponseBody: " + StringUtils.defaultString(response.get("responseBody"), "There is no response body.")
                    + "]");
        }
        else {
            System.out.println(CommonTools.getCurrentTime() + " Status: " + response.get("responseStatus") + ", Message: "
                    + response.get("responseMessage") + "\n" + "ResponseBody: "
                    + StringUtils.defaultString(response.get("responseBody"), "There is no response body."));
        }

        return response;
    }

    public AccountDescriptor getAccount(String emailId, String stackName) {
        List<AccountDescriptor> accounts = AccountsFileManager.loadAccountsFromFile(accountsFile).accounts;
        for (AccountDescriptor account : accounts)
            if (account.getProperties().get("userAccount").equals(emailId) && account.getProperties().get("regStack").equals(stackName))
                return account;
        System.out.println("No such account: <" + emailId + "> in the pool!");
        return null;
    }

    public static void main(String[] args) {
        AccountManagement am = new AccountManagement();
        System.out.println("getAuthLevelsByEmailUrl: " + am.getAuthLevelsByEmailUrl("epc2.user.autotest_ab@hp.com"));
        System.out.println("getAuthorizeUserUrl: "
                + am.getAuthorizeUserUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getCreateUrl: " + am.getCreateUrl());
        System.out.println("getDeleteUserUrl: " + am.getDeleteUserUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getForgotPasswordTokenUrl: " + am.getForgotPasswordTokenUrl("epc2.user.autotest_ab@hp.com", 3600));
        System.out.println("getLoginUrl: " + am.getLoginUrl(true, true));
        System.out.println("getProfileUrl: " + am.getProfileUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getResetPasswordUrl: "
                + am.getResetPasswordUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getTermsAndConditionsUrl: "
                + am.getTermsAndConditionsUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getUnifyUserUrl: " + am.getUnifyUserUrl("epc2.user.autotest_ab@hp.com", "emailVerified"));
        System.out.println("getUpdatePasswordUrl: "
                + am.getUpdatePasswordUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getUpdatePrimaryEmailUrl: "
                + am.getUpdatePrimaryEmailUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getUpdateProfileUrl: "
                + am.getUpdateProfileUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303"));
        System.out.println("getUserByEmailUrl: " + am.getUserByEmailUrl("epc2.user.autotest_ab@hp.com"));
        System.out.println("getUserExistenceUrl: " + am.getUserExistenceUrl("epc2.user.autotest_ab@hp.com"));
        System.out.println("getVerifyEmailUrl: "
                + am.getVerifyEmailUrl("87d542d2b78274c3358446b24c469eb328b79a9fcabd3adf962896aa7fa3c303", "MASL0M", true));
        RegisteringUser user = RegisteringUser.generateNewRegistratingUser();

        am.create(user, true); // test create method

        am.updatePrimaryEmail(user, user.getEmail().replace("@hp.com", "_new@hp.com")); // test login and updatePrimaryEmail methods

    }
    public static RegisteringUser getGmailSignUpInfo() {
        
        RegisteringUser signUpInfo = RegisteringUser.generateNewGmailUser(); 
        return signUpInfo;
    }
}