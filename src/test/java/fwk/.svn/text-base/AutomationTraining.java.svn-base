/*
 * History
 * Date Ver Author Change Description
 * ----------- --- ------------- ----------------------------------------
 * 24 Feb 2014 001 Karl Life saver web application API class.
 * Life saver web have its own set of configuration files.
 * Such as sut property file, ui json file, message property file, etc.
 * It needs a identifier to point to this set of configuration. We now call
 * it "appName". Different app will have different app names.
 * This name is important, since framework need this name to search for
 * correct configuration file set.
 * Some ink web application specific method could be put here.
 * Such as Signin(), signUp(), signOut() etc.
 */
package fwk;

import java.util.Date;

import org.openqa.selenium.By;

import base.WebApp;

public class AutomationTraining extends WebApp{

	public String getAppName(){

		return "Training";
	}

	public AutomationTraining(){

		this("");
	}

	public AutomationTraining(String SUT){

		this(SUT, "");
	}

	public AutomationTraining(String SUT,String profileName){

		super(SUT, profileName);
	}

	/**
	 * Get the DeviceID for device registration. Also parse the DeviceID
	 * according to the inputbox layout. <br>
	 * <b>TIP</b>: if you don't want to pass your own DeviceID, use this for
	 * short.
	 * 
	 * @return Parsed DeviceID. <br>
	 *         For Lifesaver, it is like xxxxxx-xxxxxx-xx-xxxxxx-xxxxxx. <br>
	 *         For Rumble, TBD.
	 */
	public String getDeviceID(){

		return getDeviceID("");
	}

	/**
	 * Get the DeviceID for device registration. Also parse the DeviceID
	 * according to the inputbox layout.
	 * 
	 * @param DevID
	 *            You can pass your own DeviceID via this parameter, otherwise,
	 *            it will read from System properties and device property file.
	 * @return Parsed DeviceID. <br>
	 *         For Lifesaver, it is like xxxxxx-xxxxxx-xx-xxxxxx-xxxxxx. <br>
	 *         For Rumble, TBD.
	 */
	private String getDeviceID(String DevID){

		if( ! DevID.isEmpty()){
			return getDeviceID(DevID, "");
		} else{
			return getDeviceID("", "");
		}
	}

	public String getNewDeviceID(String NewDevID){

		return getDeviceID(NewDevID, "newdeviceid");
	}

	/**
	 * NOTES: Priority,If DevID is not empty,use it/if DevID and deviceType are empty ,use default
	 * If DevID is empty , deviceType is not empty ,the diviceId decideId by deviceType
	 * 
	 * @param deviceType
	 *            -"" device.aio.+'application.stackName'+.deviceid -newdeviceid
	 *            device.aio.+'application.stackName'+.newdeviceid
	 *            -thirddeviceid
	 *            device.aio.+'application.stackName'+.thirddeviceid
	 * @return deviceid
	 */
	public String getDeviceID(String DevID, String deviceType){

		if(deviceType.isEmpty() && DevID.isEmpty()){

			DevID = getProperty("device.aio." + getProperty("application.stackName") + ".deviceid");
		}

		if( ! deviceType.isEmpty() && DevID.isEmpty()){

			DevID = getProperty("device.aio." + getProperty("application.stackName") + "." + deviceType);
		}

		log("Current deviceID is " + DevID);

		if( ! DevID.isEmpty()){
			DevID = parseDevid(DevID);
			if( ! deviceType.isEmpty()){
				log("Using " + deviceType + ": " + DevID);
			} else{
				log("Using default deviceId or which you specified:" + DevID);
			}

		} else{
			log("DeviceID Not Found!");
			throw new RuntimeException("DeviceID Not Found!");
		}
		return DevID;
	}

	/**
	 * Parse the DeviceID based on Lifesaver's layout,
	 * xxxxxx-xxxxxx-xx-xxxxxx-xxxxxx.
	 * 
	 * @param DevID
	 *            DeviceID that is going to parse.
	 * @return Parsed DeviceID.
	 */
	private String parseDevid(String DevID){

		DevID = DevID.replace("-", "").trim();
		if(DevID.length() != 26)
			throw new RuntimeException("DeviceID Error!");
		return DevID;

	}

	/**
	 * Enter the DeviceID to Device Registration page.
	 * 
	 * @param DeviceID
	 */
	public void enterDevID(String DeviceID){

		DeviceID = getDeviceID(DeviceID);
		verifyIsShown("input1");
		int inputLen = 0;
		int usedLen = 0;
		String DevIdToInput[] = {"","","","",""};

		for(int i = 4; i > 0; i -- ){
			try{
				inputLen = Integer.parseInt(getElement("input" + (i + 1)).getAttribute("maxlength"));
			} catch(Exception e){
				throw new RuntimeException("DeviceID Inputbox " + (i + 1) + " is not found!");
			}

			DevIdToInput[i] = DeviceID.substring(26 - usedLen - inputLen, 26 - usedLen);

			usedLen += inputLen;
		}
		DevIdToInput[0] = DeviceID.substring(0, 26 - usedLen);

		for(int i = 0; i < 5; i ++ ){
			setValueTo("input" + (i + 1), DevIdToInput[i]);
		}
	}

	/**
	 * Click on the specified item of a group of elements located by
	 * elementName.
	 * 
	 * @param elementName
	 *            elementName used in uimap.
	 * @param itemNo
	 *            position of the element operate with.
	 * @return <code>true:</code> element click succeed.<br>
	 *         <code>false:</code> element click failed.
	 */
	/*
	 * public boolean clickOn(String elementName, int itemNo) {
	 * // String elementLocator = getElementLocator(elementName);
	 * if(waitForElement(elementName)) {
	 * List<WebElement> element = getElements(elementName);
	 * if(itemNo > element.size())
	 * itemNo = itemNo % element.size();
	 * try {
	 * element.get(itemNo - 1).click();
	 * log("Clicking on item #" + itemNo + " of " + element.size() + " elements " + elementName + ".");
	 * }
	 * catch(Exception e) {
	 * throw new RuntimeException("Unknown error: " + e);
	 * }
	 * }
	 * else {
	 * return false;
	 * }
	 * return true;
	 * }
	 */

	public boolean randomClickOn(String elementName){

		boolean returnValue = clickOn(elementName, (int)(Math.random() * 30000) + "");
		return returnValue;
	}

	public boolean randomClickOn(String elementName, int num){

		boolean returnValue = clickOn(elementName, num + "");
		return returnValue;
	}

	public boolean signIn(){

		verifyIsShown("login");
		clickOn("login");
		return signIn("", "");
	}

	public boolean signIn(String email, String password){

		if(email.isEmpty()){
			email = getProperty("application.email").trim();
			password = getProperty("application.password").trim();
		}
		if(isElementPresent("confirmOK")){
			clickOn("confirmOK");
		}
		if(isElementPresent("email")){
			setValueTo("email", email);
			setValueTo("password", password);
			clickOn("signIn");
			return true;
		} else
			return false;
	}

	protected String generateRandomAccount(){

		String randomId = Long.toString(new Date().getTime());
		String returnValue = "ls.autotest+" + randomId + "@gmail.com";
		log("New email generated: " + returnValue);
		return returnValue;
	}

	public String signUpOnWeb(){

		log("Start creating a new Random Account.");
		String account = generateRandomAccount();

		verifyIsShown("signupSubmit");
		setValueTo("signupEmail", account);
		setValueTo("firstName", "lifesaver");
		setValueTo("lastName", "autotest");
		setValueTo("signupPassword", "lifesaver123");
		driver.findElement(By.id("notifyOptin")).click();
		driver.findElement(By.id("termsOptin")).click();
		clickOn("signupSubmit");
		log("Account Created Successfull.");
		return account;
	}

	public String signUp(){

		verifyIsShown("signUp");
		clickOn("signUp");

		String account = signUpOnWeb();
		return account;
	}

	public String signUpNoRegDevice(){

		verifyIsShown("signUp");
		clickOn("signUp");
		log("Start creating a new Random Account.");
		String account = generateRandomAccount();
		verifyIsShown("snapfishSignupSubmit");
		setValueTo("signupEmail", account);
		setValueTo("firstName", "lifesaver");
		setValueTo("lastName", "autotest");
		setValueTo("signupPassword", "lifesaver123");
		setValueTo("passwordConfirm", "lifesaver123");

		driver.findElement(By.id("notifyOptin")).click();
		driver.findElement(By.id("termsOptin")).click();

		clickOn("snapfishSignupSubmit");
		log("Account Created Successfull.");
		return account;
	}

	public void RegDevice(String DeviceID){

		verifyIsShown("connectYes");
		clickOn("connectYes");
		waitForElementNotShown("findingImg");
		waitByTimeout(1000);
		if(isElementShown("manualNext")){
			verifyIsShown("manualNext");
			enterDevID(DeviceID);
			clickOn("manualNext");
			waitByTimeout(2000);
			waitForPrintRegister();
		} else{
			setViewToData("activate:activeSuccess");
			verifyIsShown("startNow");
			verifyIsShown("successPic");
			clickOn("startNow");
		}

	}

	private boolean waitForPrintRegister(){

		for(int i = 30; i > 0; i -- ){
			if(isElementShown("startNow")){
				verifyIsShown("startNow");
				verifyIsShown("successPic");
				log("Device Activation Completed.");
				return clickOn("startNow");
			} else if(isElementShown("registeringError")){
				close();
				throw new RuntimeException("There was an error registering the printer with your account.");
			}

			waitByTimeout(2000);
		}
		throw new RuntimeException("The Finding Process is timeout!");

	}

	/**
	 * Get the link of an element
	 * 
	 * @param elementName
	 * @return link
	 */
	public String getLinkUrl(String elementName){

		if(waitForElement(elementName)){
			return getElement(getElementLocator(elementName)).getAttribute("href").toString();
		} else
			return "";
	}

}
