package tools.userAccount;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class AccountsListDescriptor {

    public List<AccountDescriptor> accounts;

    public AccountsListDescriptor() {
        accounts = new ArrayList<AccountDescriptor>();
    }

    public AccountsListDescriptor(List<AccountDescriptor> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(AccountDescriptor account) {
        accounts.add(account);
    }

    public void addAccounts(File accountPropertiesFile) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(accountPropertiesFile));

            HashMap<String, String> account = new HashMap<String, String>();
            for (String key : properties.stringPropertyNames())
                account.put(key, properties.getProperty(key));

            accounts.add(new AccountDescriptor(account));
        }
        catch (Exception e) {
            System.err.println(String.format("Error trying to read file: %s", accountPropertiesFile.getAbsolutePath()));
            e.printStackTrace(System.err);
        }
    }

}
