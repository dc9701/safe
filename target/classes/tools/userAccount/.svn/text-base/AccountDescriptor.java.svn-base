package tools.userAccount;

import java.util.HashMap;
import java.util.Map;

import tools.commonTools.CommonTools;

public class AccountDescriptor {

    private Map<String, String> properties;
    transient private String    userAccount;
    transient private String    regDate;
    transient private String    regStack;
    transient private String    password;

    public AccountDescriptor(Map<String, String> properties) {
        this.properties = properties;
    }

    public AccountDescriptor(RegisteringUser user) {
        this(makeAccountPropertiesFromRegisteringUser(user));
    }

    /**
     * Returns all the accounts' properties
     * 
     * @return a map of properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * Returns the account (email)
     * 
     * @return an account
     */

    public String getAccount() {
        if (userAccount == null)
            userAccount = properties.get("userAccount");
        return userAccount;
    }

    /**
     * Returns the date when the account has been registered
     * 
     * @return an date
     */

    public String getRegDate() {
        if (regDate == null)
            regDate = properties.get("regDate");
        return regDate;
    }

    /**
     * Returns the stack of the account.
     * 
     * @return an stack name.
     */

    public String getRegStack() {
        if (regStack == null)
            regStack = properties.get("regStack");
        return regStack;
    }

    /**
     * Returns the password of the account.
     * 
     * @return a password.
     */

    public String getPassword() {
        if (password == null)
            password = properties.get("password");
        return password;
    }

    /**
     * Creates a map of properties for the user account from an RegisteringUser object.
     * 
     * @param user
     *            an RegisteringUser object.
     */

    public static Map<String, String> makeAccountPropertiesFromRegisteringUser(RegisteringUser user) {

        String userAccount = user.getEmail();
        String password = user.getPassword();

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("userAccount", userAccount);
        properties.put("password", password);

        return properties;
    }

    /**
     * Get the stack URL from current sut file.
     * 
     * @return stack URL.
     */

}
