package test.cases.training;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import fwk.AutomationTraining;

import java.util.Date;

/**
 * Quick test to create many users and activate a printer amongst them.
 */
public class CreateManyUsers {

    private static String deviceId = "";
    static String emailBase = "";
	private final static String EMAIL_DOMAIN = "@gmail.com";
	
	private final static String FIRST_NAME = "Many";
	private final static String LAST_NAME = "Users";
	private final static String PASSWORD = "lifesaver123";

	private static Integer userNumber = 0;
	private static Integer maxUsers = 1;	

	private static AutomationTraining webApp = null;

	@BeforeClass
	public static void setUp() {

		webApp= new AutomationTraining();
		webApp.openApp();
		
        emailBase = webApp.getProperty("user.email");
        deviceId = webApp.getProperty("device.id");
        
        Integer configMaxUsers = Integer.parseInt(webApp.getProperty("max.users"));
        
        maxUsers = Math.max(configMaxUsers, maxUsers);
	}
	
	/**
	 * Create a new account and verify that the activation view is shown.
	 */
	@Test
	public void testCreateManyUsers() {
		
		while (userNumber < maxUsers) {
			
			// Log in with a new user account.
			
			String newEmail= emailBase + Long.toString(new Date().getTime()) + EMAIL_DOMAIN;		   
	        webApp.log("New user #" + ++userNumber + " account email: " + newEmail + "\n");        
	
			webApp.clickOn("login");
			webApp.setViewTo("hpConnectedAccount:createAccount");
			
			webApp.setValueTo("firstName", FIRST_NAME);
			webApp.setValueTo("lastName", LAST_NAME);
	        webApp.setValueTo("email", newEmail);
			webApp.setValueTo("password", PASSWORD);
			
	        webApp.setValueTo("sendMeEmail", "no");                                        
	        webApp.setValueTo("yesHaveRead", "yes");	        
	        webApp.clickOn("createAccount");                        
	        
	        // Activate a printer.
	        
	        webApp.clickOn("acceptTheNeatCompany");					        
			webApp.clickOn("next");                                 // Accept terms & conditions and go to next view.                                  
			webApp.clickOn("activateYourPrinterManually");
			
			webApp.enterDevID(deviceId);                            
			webApp.clickOn("next");                                 // Navigates to congratulations view.
			webApp.waitByTimeout(10000);							// Wait 10 secs...
	
			/*
			 * If we successfully activated the device, de-activate it.  If not, log an error.
			 */
					
			if (StringUtils.containsIgnoreCase(webApp.getCurrentUrl(), "success")) {
	
				webApp.setViewTo("hpOfficejet8040WithNeat:deactivatePrinter");
				webApp.clickOn("yes");															
			}
			else {			
		        webApp.log("ACTIVATION FAILURE:  New user #" + userNumber + " (" + newEmail + ") failed to activate device " + deviceId + "\n");
		        webApp.setViewToData("hpOfficejet8040WithNeat:sorryWeNeedLittle");
			}
			
			// Finally, log out.
			
			webApp.setViewTo("hpOfficejet8040WithNeat:hpOfficejet8040WithNeat");
			// webApp.verifyViewIs("hpOfficejet8040WithNeat");
		}
	}
	
	@AfterClass
    public static void tearDown() {
        webApp.close();
    }
	
}
