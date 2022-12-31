package test.cases.training;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import base.FrontPanel;

import java.util.Date;

import fwk.AutomationTraining;

/**
 * Sample test for SAFE Automation training.<p><p>
 * 
 * This is the "Phase 1" original test that will be modified during the training workshop.  Exercises
 * OfficeJet 8040 (Rumble) Web App, HP AiO Remote Control (for Android), OJ 8040 front panel and Embedded Web Server (EWS).<p><p>
 * 
 * Phase 1 - Begins with the following basic steps:<p>
 * 
 * <table>
 * <tr><td><h6>Step</h6></td><td><h6>Application</h6></td><td><h6>Activity</h6></td></tr>
 * <tr><td>1.</td><td>OJ 8040 Web App</td><td>Create a new acct and activate a printer</td></tr>
 * <tr><td>2.</td><td>OJ 8040 Front Panel</td><td>Scan-To-Neat one document</td></tr>
 * <tr><td>3.</td><td>Embedded Web Server</td><td>Log in and verify that WebScan is disabled</td></tr>
 * <tr><td>4.</td><td>HP AiO Remote Control (for Android)</td><td>Log in & connect to the printer</td></tr>
 * <tr><td>5.</td><td>OJ 8040 Web App</td><td>Deactivate the printer and log out</td></tr>
 * </table>
 */
public class MyFirstTest {

	/*
	 * AiO device parameters
	 */
	
	// Stage1 device
	
	// private final static String DeviceID ="ABDXMPGXFSJ72DYEQUTG2Z3J5W";    	// TODO: Enter your device ID here.
	// private final static String DeviceIP = "10.6.3.60";						// TODO: Enter your device IP address here.

	// Test1 device
	
	private final static String DeviceID = "L3YAM3PMKNMW9RDR7PMWS7EQW2";    	// TODO: Enter your printer ID.	
	private final static String DeviceIP = "10.6.3.238";						// TODO: Enter your printer IP address.

	private static FrontPanel aioDevice = null;	
	
	/*
	 * Web App parameters.
	 */
	
	private final static String DEFAULT_USER = "ls.autotest+";             	// Variables for creating a new, unique user account.
	private final static String DEFAULT_EMAIL = "@gmail.com";
	private final static String randomId = Long.toString(new Date().getTime());
	private final static String FIRST_NAME = "Otto";
	private final static String LAST_NAME = "Tester";
	private final static String PASSWORD = "lifesaver123";
    private static String newEmail= DEFAULT_USER + randomId + DEFAULT_EMAIL;
	
	private static AutomationTraining webApp = null;

	/**
	 * Set up steps before tests begin - Opens the Web App on the appropriate stack (Stage1).
	 */
	@BeforeClass
	public static void setUp() {

		webApp= new AutomationTraining();
		webApp.openApp();
        webApp.log("New user account email: " + newEmail + "\n");        
	}

	/**
	 * Create a new account and verify that the activation view is shown.
	 */
	@Test
	public void test010_SignUpAsNewUser() {	    
		webApp.log("BEGIN TEST - test010_SignUpAsNewUser");     // Good practice to log when each test begins.

		webApp.clickOn("login");
		webApp.setViewTo("hpConnectedAccount:createAccount");	// Navigates to Create Account page.
		
		webApp.setValueTo("firstName", FIRST_NAME);
		webApp.setValueTo("lastName", LAST_NAME);
        webApp.setValueTo("email", newEmail);
		webApp.setValueTo("password", PASSWORD);
		
        webApp.setValueTo("sendMeEmail", "no");                 // Checkboxes can be set to "yes", "no", "on", "off", "checked", etc.                       
        webApp.setValueTo("yesHaveRead", "yes");
        
        webApp.clickOn("createAccount");                        // Clicking Create Account will navigate to Activate Printer view.
        
        webApp.verifyViewIs("activateYourPrinter");         	// Verify we got to the Activate Printer view.
	}

	/**
	 * Attempt to activate the printer and verify that it is successful.
	 */
	@Test
	public void test020_ActivatePrinter() {
        webApp.log("BEGIN TEST - test020_ActivatePrinter");

        webApp.clickOn("acceptTheNeatCompany");					// Why won't setValueTo() work?  GOOD QUESTION!        
		webApp.clickOn("next");                                 // Accept terms & conditions and go to next view.                                  
		webApp.clickOn("activateYourPrinterManually");
		
		webApp.enterDevID(DeviceID);                            // An easy way to enter the XXXX-XXXX-XXXXXX-XXXXXX-XXXXXX ID.
		webApp.clickOn("next");                                 // Navigates to congratulations view.
		
        webApp.verifyViewIs("congratulations");                	// Verify the congratulations view is shown.        
	}

	/**
	 * Deactivate the printer and sign out.
	 */
	@Test
	public void test030_DeactivatePrinterAndSignOut() {
        webApp.log("BEGIN TEST - test030_DeactivatePrinterAndSignOut");

        // Open dialog to deactivate the printer, then click Yes.
        
		webApp.setViewTo("hpOfficejet8040WithNeat:deactivatePrinter");
		webApp.clickOn("yes");									
		
		// Log out.
		
		webApp.setViewTo("hpOfficejet8040WithNeat:hpOfficejet8040WithNeat");
		webApp.verifyViewIs("hpOfficejet8040WithNeat");
	}

	/**
	 * Closes the Web App. 
	 */
	@AfterClass
    public static void tearDown() {
        webApp.close();
    }
}
