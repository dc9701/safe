package tools.userAccount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Reads a list of user account files and generate a json file
 * describing the user account
 */

public class AccountsFileManager {

    public static AccountsListDescriptor buildAccountsDescriptor(List<File> registerAccountFiles) {
        AccountsListDescriptor accounts = new AccountsListDescriptor();

        for (File registerAccountFile : registerAccountFiles) {
            List<File> accountPropertiesFiles = buildAccountPropertiesFileList(registerAccountFile);
            for (File accountPropertiesFile : accountPropertiesFiles)
                accounts.addAccounts(accountPropertiesFile);
        }
        return accounts;
    }

    private static List<File> buildAccountPropertiesFileList(File registerAccountFile) {
        List<File> accountPropertiesFiles = new ArrayList<File>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(registerAccountFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    StringTokenizer st = new StringTokenizer(line, ",");
                    String file = st.nextToken();
                    accountPropertiesFiles.add(new File(file));
                }
            }
        }
        catch (Exception e) {
            System.err.println(String.format("Error trying to read file: %s", registerAccountFile.getAbsolutePath()));
            e.printStackTrace(System.err);
        }
        return accountPropertiesFiles;
    }

    public static void saveAccountsToFile(AccountsListDescriptor accounts, File file) throws IOException {

        PrintWriter pw = null;
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(accounts);
            pw = new PrintWriter(file);
            pw.println(json);
            System.out.println("Account saved to " + file.getPath().replace("target\\test-classes\\..\\..\\", ""));
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Could not save accounts to file: %s", file));
        }
        finally {
            if (pw != null)
                pw.close();
        }

    }

    public static synchronized AccountsListDescriptor loadAccountsFromFile(File file) {
        BufferedReader br = null;

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.fromJson(new BufferedReader(new FileReader(file)), AccountsListDescriptor.class);
        }
        catch (Exception e) {
            throw new RuntimeException(String.format("Could not get accounts from file: %s", file));
        }
        finally {
            try {
                if (br != null)
                    br.close();
            }
            catch (Exception closeException) {
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        List<File> registerPrinterFiles = new ArrayList<File>();
        for (String arg : args)
            registerPrinterFiles.add(new File(arg));

        AccountsListDescriptor accounts = buildAccountsDescriptor(registerPrinterFiles);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(accounts);
        System.out.println(json);
    }
}
