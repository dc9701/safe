package tools.userAccount;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import java.util.Random;

/**
 * 
 *
 */
public class RegisteringUser {

    // Required (for signing up) - (had to ask, as the doc is not explicit)
    private String                firstName;
    private String                lastName;
    private String                email;
    private String                password;
    private boolean               snapfishOptIn;
    private boolean               partnerOptIn;
    private String                countryCode;
    private String                languageCode;

    protected final static int    RANDOM_MAX         = 999999;
    protected final static String DEFAULT_FIRST_NAME = "Otto";
    protected final static String DEFAULT_LAST_NAME  = "Tester";
    protected final static String DEFAULT_USER       = "epc2.user.";
    protected final static String DEFAULT_EMAIL      = "@hp.com";
    protected final static String DEFAULT_PASSWORD   = "password123";

    protected final static String DEFAULT_FIRST_NAME_II = "Test";
    protected final static String DEFAULT_LAST_NAME_II  = "InstantInk";
    protected final static String DEFAULT_USER_II       = "test2.ii_user.";
    
    public RegisteringUser(String firstName, String lastName, String email, String password, boolean snapfishOptIn, boolean partnerOptIn,
            String countryCode, String languageCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.snapfishOptIn = snapfishOptIn;
        this.partnerOptIn = partnerOptIn;
        this.countryCode = countryCode;
        this.languageCode = languageCode;
    }

    public RegisteringUser() {
        // Default constructor
    }

    /**
     * Returns an object which can used to SIGN UP with PAPI.  Note that this method does NOT create a
     * user account - it simply creates an object with a random email address and default user first name,
     * last name and password that can be used on the /showsignup page to create a new account.
     * 
     * @return
     *         An object which can used to SIGN UP via the /showsignup page.
     */
    public static RegisteringUser generateNewRegistratingUser(String firstName, String lastName, String emailAddress, String password) {

        String randomId=Long.toString(new Date().getTime());
        
        String first = StringUtils.defaultIfEmpty(firstName, DEFAULT_FIRST_NAME);
        String last = StringUtils.defaultIfEmpty(lastName, DEFAULT_LAST_NAME);
        String email = StringUtils.defaultIfEmpty(emailAddress, (DEFAULT_USER + randomId + DEFAULT_EMAIL));
        String pwd = StringUtils.defaultIfEmpty(password, DEFAULT_PASSWORD);
        
        return new RegisteringUser(first, last, email, pwd, true, true, "US", "EN");
    }

    public static RegisteringUser generateNewRegistratingUser() {
        return generateNewRegistratingUser("", "", "", "");
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSnapfishOptIn() {
        return snapfishOptIn;
    }

    public void setSnapfishOptIn(boolean snapfishOptIn) {
        this.snapfishOptIn = snapfishOptIn;
    }

    public boolean isPartnerOptIn() {
        return partnerOptIn;
    }

    public void setPartnerOptIn(boolean partnerOptIn) {
        this.partnerOptIn = partnerOptIn;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public static RegisteringUser generateNewGmailUser() {

        Random rand = new Random();
        String id = Integer.toString(rand.nextInt(RANDOM_MAX)) + Long.toString(new Date().getTime()).substring(6, 12);
        return new RegisteringUser(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,"epc2.user" +"+"+id + "@gmail.com", DEFAULT_PASSWORD, true, true,
                "US", "EN");
        
    }

	public static RegisteringUser generateNewGmailUser_UK() {

        Random rand = new Random();
        String id = Integer.toString(rand.nextInt(RANDOM_MAX)) + Long.toString(new Date().getTime()).substring(6, 12);
        return new RegisteringUser(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME,"epc2.user" +"+"+id + "@gmail.com", DEFAULT_PASSWORD, true, true,
                "UK", "EN");
        
    }
}
