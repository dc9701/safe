package base;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import tools.commonTools.CommonTools;
import base.core.TestUiClass;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class AndroidApp extends TestUiClass{

	protected String getAppName(){

		return "";
	}

	protected String getAppType(){

		return "AndroidApp";
	}

	protected AndroidApp(){

		this("");
	}

	protected AndroidApp(String SUT){

		this(SUT, "");
	}

	protected AndroidApp(String sut,String profileName){

		super(sut, profileName);
		platformSupportInitiate(profileName);

	}

	@Override
	protected void platformSupportInitiate(String profileName){

		startAppiumDriver(profileName);
		prepareTestEnvironment();
	}

	/**
	 * Lists the source of the currently loaded page in the console log.
	 * 
	 */
	public void getPageSource(){

		log(androidDriver.getPageSource());
	}

	/**
	 * Returns a WebElement based upon the specified element locator.
	 * 
	 * @param elementName
	 *            The locator of the element you want.
	 * 
	 * @param text
	 *            Text of the element, if needed.
	 * 
	 * @return
	 *         The WebElement you were asking for.
	 */
	public WebElement getAndroidElementByClass(String elementName, String text){

		String elementLocator = getElementLocator(elementName);
		String uiCommand = "new UiSelector().className(\"" + elementLocator + "\").text(\"" + text + "\")";
		WebElement element = androidDriver.findElementByAndroidUIAutomator(uiCommand);

		return element;
	}

	/**
	 * Taps on the specified element.
	 * 
	 * @param elementName
	 *            The locator of the element you want.
	 * 
	 * @param text
	 *            Text of the element, if needed.
	 */
	public void getElementAndTap(String elementName, String text){

		WebElement element = getAndroidElementByClass(elementName, text);
		androidDriver.tap(1, element, 0);
	}

	/**
	 * Returns a list of WebElements that match the specified class.
	 * 
	 * @param className
	 *            The class of the element(s) you want.
	 * 
	 * @return
	 *         A list of WebElements that match the requested class.
	 */
	public List<WebElement> getAndroidElementsByClass(String className){

		className = "new UiSelector().className(\"" + className + "\")";
		List<WebElement> listElement = androidDriver.findElementsByAndroidUIAutomator(className);

		return listElement;
	}

	protected void startAppiumDriver(String app_apk){

		try{
			if(app_apk.isEmpty())
				app_apk = "c:/default.apk";
			File app = new File(app_apk);
			if( ! app.exists()){
				File classpathRoot = new File(testDataRoot);
				File appDir = new File(classpathRoot, getProperty("app.path"));
				app = new File(appDir, getProperty("app.name"));
				System.out.println("Launch the application from the path " + app);
			} else{
				System.out.println("Launch the application from " + app_apk);
			}

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(CapabilityType.BROWSER_NAME, getProperty("app.browser.Name"));
			capabilities.setCapability("platformVersion", getProperty("app.device.version"));
			capabilities.setCapability("platform", getProperty("app.os.platform"));
			capabilities.setCapability("deviceName", getProperty("app.device.name"));
			capabilities.setCapability("platformName", getProperty("app.device.platformName"));
			capabilities.setCapability("newCommandTimeout", getProperty("app.command.timeout"));
			capabilities.setCapability("app", app.getAbsolutePath());
			capabilities.setCapability("appPackage", getProperty("app.package"));
			capabilities.setCapability("appActivity", getProperty("app.activity"));
			capabilities.setCapability("appWaitActivity", getProperty("app.wait.activity"));

			androidDriver = new AndroidDriver(new URL("http://" + getProperty("app.appium.serverIP") + "/wd/hub"), capabilities);
			driver = androidDriver;
		} catch(Exception e){
			log("Cannot install application into the mobile device", 2);
			throw new RuntimeException(e);
		}

		new WebDriverWait(androidDriver, 10);
	}

	/**
	 * Wait (up to one minute) for the specified element to be displayed.
	 * 
	 * @elementName: element on the page
	 * 
	 * @return Returns true if the element is found; otherwise, fails the current test.
	 */
	public boolean waitForElementToPresent(String elementName){

		if( ! waitForElement(elementName, "", "", 60)){
			Assert.assertTrue(false, ("wait for elementName '" + elementName + "' time out 60s"));
		}
		return true;
	}

	/**
	 * Returns the current Android OS version.
	 * 
	 * @return Android OS version.
	 */
	public String getAndroidVersion(){

		try{
			Process process = Runtime.getRuntime().exec("adb shell getprop ro.build.version.release");
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String version = br.readLine();
			log("device version " + version);
			int points = 0;

			for(int i = 0; i < version.length(); i ++ ){
				if(version.substring(i, i + 1).equals(".")){
					points ++ ;
					if(points == 2){
						return version.substring(0, i);
					}
				}
			}
			return version;

		} catch(Exception e){
			log("adb shell getprop ro.build.version.release", 2);
			throw new NullPointerException("get device version is null.");
		}
	}

	/**
	 * Uninstall the Application being tested.
	 */
	public void uninstallApp(){

		String apkPackage = getProperty("app_package");
		try{
			Runtime.getRuntime().exec("adb uninstall " + apkPackage);

		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Swipe across the screen
	 * 
	 * @param startx
	 *            starting x coordinate
	 * @param starty
	 *            starting y coordinate
	 * @param endx
	 *            ending x coordinate
	 * @param endy
	 *            ending y coordinate
	 * @param duration
	 *            amount of time in milliseconds for the entire swipe action to
	 *            take
	 */

	private void swipe(int startX, int startY, int endX, int endY, int duration){

		androidDriver.swipe(startX, startY, endX, endY, duration);
	}

	/**
	 * Swipe the screen and update the uiMap.
	 *
	 * @param type
	 *            - left Sliding screen to the left
	 *            - leftSide From the left of screen to began to slip
	 *            - right Sliding screen to the right
	 *            - rightSide From the right of screen to began to slip
	 *            - up Screen upward sliding
	 *            - top From the top of screen to began to slip
	 *            - down Slide down the screen
	 *            - bottom From the bottom of screen to began to slip
	 */

	private void swipeOfType(String type){

		log("Swiping " + type + ".");
		int windowlenX = getCoorinateX();
		int windowlenY = getCoorinateY();
		String swipeLeft = "left";
		String swipeLeftSide = "leftSide";
		String swipeRight = "right";
		String swipeRightSide = "rightSide";
		String swipeUp = "up";
		String swipeTop = "top";
		String swipeDown = "down";
		String swipeBottom = "bottom";
		// Sliding screen to the left
		if(type.equalsIgnoreCase(swipeLeft)){
			log("SWIPE : Sliding screen to the left.");
			swipe((int)(windowlenX * 0.9), (int)(windowlenY * 0.5), (int)(windowlenX * 0.2), (int)(windowlenY * 0.5), 1000);
		}
		// From the left of screen to began to slip
		if(type.equalsIgnoreCase(swipeLeftSide)){
			log("SWIPE : From the LeftSide of screen to right.");
			swipe(1, (int)(windowlenY * 0.5), (int)(windowlenX * 0.9), (int)(windowlenY * 0.5), 1000);
		}
		// Sliding screen to the right
		if(type.equalsIgnoreCase(swipeRight)){
			log("SWIPE : Sliding screen to the right.");
			swipe((int)(windowlenX * 0.2), (int)(windowlenY * 0.5), (int)(windowlenX * 0.9), (int)(windowlenY * 0.5), 1000);
		}
		// From the right of screen to began to slip
		if(type.equalsIgnoreCase(swipeRightSide)){
			log("SWIPE : From the RightSide of screen to left.");
			swipe((int)(windowlenX * 0.9), (int)(windowlenY * 0.5), (int)(windowlenX * 0.2), (int)(windowlenY * 0.5), 1000);
		}
		// Screen upward sliding
		if(type.equalsIgnoreCase(swipeUp)){
			log("SWIPE : Screen upward sliding.");
			swipe((int)(windowlenX * 0.5), (int)(windowlenY * 0.9), (int)(windowlenX * 0.5), (int)(windowlenY * 0.4), 1000);
		}
		// From the top of screen to began to slip
		if(type.equalsIgnoreCase(swipeTop)){
			log("SWIPE : From the top of screen to bottom.");
			swipe((int)(windowlenX * 0.5), 0, (int)(windowlenX * 0.5), (int)(windowlenY * 0.8), 1000);
		}
		// Slide down the screen
		if(type.equalsIgnoreCase(swipeDown)){
			log("SWIPE : Slide down the screen.");
			swipe((int)(windowlenX * 0.5), (int)(windowlenY * 0.4), (int)(windowlenX * 0.5), (int)(windowlenY * 0.9), 1000);
		}
		// From the bottom of screen to began to slip
		if(type.equalsIgnoreCase(swipeBottom)){
			log("SWIPE :From the bottom of screen to top.");
			swipe((int)(windowlenX * 0.5), windowlenY, (int)(windowlenX * 0.5), (int)(windowlenY * 0.2), 1000);
		}
	}

	/**
	 * Tap the center of an element on the screen.
	 * 
	 * @param fingers
	 *            - number of fingers/appendages to tap with
	 * 
	 * @param elementName
	 *            - elementName to tap
	 * 
	 * @param duration
	 *            -how long between pressing down, and lifting
	 *            fingers/appendages
	 */
	private void tap(int fingers, String elementName, int duration){

		tap(fingers, elementName, "", "", duration);
	}

	private void tap(int fingers, String listName, Object itemMatching, int duration){

		tap(fingers, listName, itemMatching, "", duration);
	}

	private void tap(int fingers, String listName, Object itemMatching, String elementName, int duration){

		if(waitForElement(listName, itemMatching, elementName, 5)){
			WebElement element = getElement(listName, itemMatching, elementName);
			androidDriver.tap(fingers, element, duration);
			log("Tap on '" + (elementName.isEmpty()?listName:elementName) + "'.");
		} else{

			log("tap on element failed.");
			throw new RuntimeException("tapOn element failed.");

		}

	}

	/**
	 * Double-tap the screen.
	 */
	public void doubleTap(){

		TouchAction action = new TouchAction(androidDriver);
		action.press(500, 500).release().waitAction(100).press(500, 500).release().perform();
		log("Double tap the screen.");
	}

	/**
	 * Zoom in on the screen by x,y coordinates.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param x
	 *            - x coordinate to start zoom on
	 * @param y
	 *            - y coordinate to start zoom on
	 */
	public void zoom(int x, int y){

		androidDriver.zoom(x, y);
	}

	/**
	 * Zoom in on an element on the screen.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param elementName
	 *            - The elementName to zoom in upon
	 */
	public void zoom(String elementName){

		zoom(elementName, "");
	}

	/**
	 * Zoom in on a list item on the screen.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to zoom in upon.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 */
	public void zoom(String listName, String itemMatching){

		zoom(listName, itemMatching, "");
	}

	/**
	 * Zoom in on a list item on the screen.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to zoom in upon.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 */
	public void zoom(String listName, int itemMatching){

		zoom(listName, itemMatching, "");
	}

	/**
	 * Zoom in on an element contained within a list item on the screen.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to zoom in upon.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            The name of the element you wish to zoom in upon.
	 */
	public void zoom(String listName, String itemMatching, String elementName){

		WebElement element = getElement(listName, itemMatching, elementName);

		androidDriver.zoom(element);
	}

	/**
	 * Zoom in on an element contained within a list item on the screen.
	 * 
	 * "Zooming in" refers to the action of two appendages pressing the screen
	 * and sliding away from each other. NOTE: This convenience method slides
	 * touches away from the element, if this would happen to place one of them
	 * off the screen, appium will return an outOfBounds error. In this case,
	 * revert to using the MultiTouchAction api instead of this method.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to zoom in upon.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            The name of the element you wish to zoom in upon.
	 */
	public void zoom(String listName, int itemMatching, String elementName){

		WebElement element = getElement(listName, itemMatching, elementName);

		androidDriver.zoom(element);
	}

	/**
	 * Pinch an element on the screen.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method Parameter:
	 * 
	 * @param elementName
	 *            - The element to pinch
	 */
	public void pinch(String elementName){

		pinch(elementName, "");

	}

	/**
	 * Pinch a list item on the screen.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method Parameter:
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to pinch.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 */
	public void pinch(String listName, String itemMatching){

		pinch(listName, itemMatching, "");

	}

	/**
	 * Pinch a list item on the screen.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method Parameter:
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to pinch.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 */
	public void pinch(String listName, int itemMatching){

		pinch(listName, itemMatching, "");

	}

	/**
	 * Pinch an element contained within a list item on the screen.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method Parameter:
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to pinch.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            The element to pinch
	 */
	public void pinch(String listName, String itemMatching, String elementName){

		WebElement element = getElement(listName, itemMatching, elementName);

		androidDriver.pinch(element);

	}

	/**
	 * Pinch an element contained within a list item on the screen.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method Parameter:
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to pinch.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            The element to pinch
	 */
	public void pinch(String listName, int itemMatching, String elementName){

		WebElement element = getElement(listName, itemMatching, elementName);

		androidDriver.pinch(element);

	}

	/**
	 * Pinch the screen by x,y coordinates.
	 * 
	 * "Pinching" refers to the action of two appendages pressing the screen and sliding
	 * towards each other. NOTE: This convenience method places the initial
	 * touches around the element, if this would happen to place one of them off
	 * the screen, appium with return an outOfBounds error. In this case, revert
	 * to using the MultiTouchAction api instead of this method
	 * 
	 * @param x
	 *            - x coordinate to terminate the pinch on
	 * @param y
	 *            - y coordinate to terminate the pinch on
	 */
	public void pinch(int x, int y){

		androidDriver.pinch(x, y);
	}

	/**
	 * Send a "down" key event.
	 */
	public void down(){

		androidDriver.sendKeyEvent(20);

	}

	/**
	 * Click the back button.
	 */
	public void back(){

		androidDriver.sendKeyEvent(4);

	}

	/**
	 * Click the home page button.
	 */
	public void goHome(){

		androidDriver.sendKeyEvent(3);
	}

	private boolean tap(String elementName, int pressTime, String type){

		return tap(elementName, "", "", pressTime, type);

	}

	private boolean tap(String listName, Object itemMatching, int pressTime, String type){

		return tap(listName, itemMatching, "", pressTime, type);

	}

	private boolean tap(String listName, Object itemMatching, String elementName, int pressTime, String type){

		boolean returnValue = false;
		String locator = getElementLocator(listName);
		String view = getTargetView(type, listName, "");
		if(StringUtils.isEmpty(locator)){
			JsonNode currentCoordinate = getCoordinateJsonNode((elementName.isEmpty())?listName:elementName);

			// x , y ,0< x,y< 1.
			float getPercentX = Float.parseFloat(currentCoordinate.path("x").textValue());
			float getPercentY = Float.parseFloat(currentCoordinate.path("y").textValue());

			int x = (int)(getCoorinateX() * getPercentX);
			int y = (int)(getCoorinateY() * getPercentY);
			androidDriver.tap(1, x, y, pressTime);
		} else{
			boolean emptyItemMatching = true;
			if(itemMatching instanceof Integer){
				if((Integer)itemMatching > 0)
					emptyItemMatching = false;
			}
			if(itemMatching instanceof String){
				if( ! ((String)itemMatching).isEmpty())
					emptyItemMatching = false;
			}
			if(emptyItemMatching){
				tap(1, listName, pressTime);
			} else{
				tap(1, listName, itemMatching, pressTime);
			}
		}
		returnValue = uiMapUpdated(view);
		if( ! returnValue){
			throw new RuntimeException("uiMap Update Error.");
		}

		return returnValue;

	}

	/**
	 * Swipe the screen in the specified direction.
	 * 
	 * @param direction
	 * 
	 * @return true if swipe is successful.
	 */

	public boolean swipe(String direction){

		return SwipeAndUpdateView(direction);
	}

	/**
	 * Convenience method for swipe and update uiMap
	 * 
	 * @param direction
	 *            - left Sliding screen to the left
	 *            - leftSide From the left side of screen to right .
	 *            - right Sliding screen to the right
	 *            - rightSide From the right of screen to right.
	 *            - up Screen upward sliding
	 *            - top From the top of screen to bottom .
	 *            - down Slide down the screen
	 *            - bottom From the bottom of screen to top .
	 */

	private boolean SwipeAndUpdateView(String direction){

		String swipeToView = getTargetView("swipe", "", direction);
		swipeOfType(direction);
		log("swipe to view :" + swipeToView, 1);
		return uiMapUpdated(swipeToView);
	}

	/**
	 * Click (tap) on an item.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean clickOn(String elementName){

		return tapOn(elementName, "", "");
	}

	/**
	 * Click (tap) on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean clickOn(String listName, String itemMatching){

		return tapOn(listName, itemMatching, "");
	}

	/**
	 * Click (tap) on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean clickOn(String listName, int itemMatching){

		return tapOn(listName, itemMatching, "");
	}

	/**
	 * Click (tap) on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Regex or string to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean clickOn(String listName, String itemMatching, String elementName){

		return tapOn(listName, itemMatching, elementName);
	}

	/**
	 * Click (tap) on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean clickOn(String listName, int itemMatching, String elementName){

		return tapOn(listName, itemMatching, elementName);
	}

	/**
	 * Tap on an item.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean tapOn(String elementName){

		return tap(elementName, 500, "tap");
	}

	/**
	 * Tap on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean tapOn(String listName, String itemMatching){

		return tap(listName, itemMatching, 500, "tap");
	}

	/**
	 * Tap on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean tapOn(String listName, int itemMatching){

		return tap(listName, itemMatching, 500, "tap");
	}

	/**
	 * Tap on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean tapOn(String listName, String itemMatching, String elementName){

		return tap(listName, itemMatching, elementName, 500, "tap");
	}

	/**
	 * Tap on an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to tap on.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to tap on.
	 * 
	 * @return true if the tap is successful.
	 */
	public boolean tapOn(String listName, int itemMatching, String elementName){

		return tap(listName, itemMatching, elementName, 500, "tap");
	}

	/**
	 * Flick an item.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @return true if the flick is successful.
	 */
	public boolean flickOn(String elementName){

		return tap(elementName, "", "", 0, "tap");
	}

	/**
	 * Flick an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @return true if the flick is successful.
	 */
	public boolean flickOn(String listName, String itemMatching){

		return tap(listName, itemMatching, "", 0, "tap");
	}

	/**
	 * Flick an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @return true if the flick is successful.
	 */
	public boolean flickOn(String listName, int itemMatching){

		return tap(listName, itemMatching, "", 0, "tap");
	}

	/**
	 * Flick an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @return true if the flick is successful.
	 */
	public boolean flickOn(String listName, String itemMatching, String elementName){

		return tap(listName, itemMatching, elementName, 0, "tap");
	}

	/**
	 * Flick an item.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @return true if the flick is successful.
	 */
	public boolean flickOn(String listName, int itemMatching, String elementName){

		return tap(listName, itemMatching, elementName, 0, "tap");
	}

	/**
	 * Press an item for a specified duration.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @param pressTime
	 *            Duration of the press, in millisecs.
	 * 
	 * @return true if the press is successful.
	 */
	public boolean pressOn(String elementName, int pressTime){

		return tap(elementName, pressTime, "press");
	}

	/**
	 * Press an item for a specified duration.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @param pressTime
	 *            Duration of the press, in millisecs.
	 * 
	 * @return true if the press is successful.
	 */
	public boolean pressOn(String listName, String itemMatching, int pressTime){

		return tap(listName, itemMatching, pressTime, "press");
	}

	/**
	 * Press an item for a specified duration.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @param pressTime
	 *            Duration of the press, in millisecs.
	 * 
	 * @return true if the press is successful.
	 */
	public boolean pressOn(String listName, int itemMatching, int pressTime){

		return tap(listName, itemMatching, pressTime, "press");
	}

	/**
	 * Press an item for a specified duration.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            String or regex to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @param pressTime
	 *            Duration of the press, in millisecs.
	 * 
	 * @return true if the press is successful.
	 */
	public boolean pressOn(String listName, String itemMatching, String elementName, int pressTime){

		return tap(elementName, pressTime, "press");
	}

	/**
	 * Press an item for a specified duration.
	 * 
	 * @param listName
	 *            The name of the list containing the item you wish to flick.
	 * 
	 * @param itemMatching
	 *            Index to locate a specific item within the list.
	 * 
	 * @param elementName
	 *            Name of the element to flick.
	 * 
	 * @param pressTime
	 *            Duration of the press, in millisecs.
	 * 
	 * @return true if the press is successful.
	 */
	public boolean pressOn(String listName, int itemMatching, String elementName, int pressTime){

		return tap(elementName, pressTime, "press");
	}

	/**
	 * Get the abscissa (x coordinate) of device screen
	 * 
	 * @return
	 */
	protected int getCoorinateX(){

		return androidDriver.manage().window().getSize().getWidth();

	}

	/**
	 * Get the ordinate (y coordinate) of device screen.
	 * 
	 * @return
	 */
	protected int getCoorinateY(){

		return androidDriver.manage().window().getSize().getHeight();

	}

	/**
	 * Reset the current application.
	 */
	public void reset(){

		androidDriver.resetApp();
	}

	/**
	 * Close the current application.
	 */
	public void close(){

		androidDriver.closeApp();
	}

	/**
	 * Remove the current application.
	 */
	public void removeApp(String packageName){

		androidDriver.removeApp(packageName);
	}

	/**
	 * Launch the current application.
	 */
	public void launchApp(){

		androidDriver.launchApp();
	}

	/**
	 * @param appPackage
	 *            The package containing the activity. [Required]
	 * @param appActivity
	 *            The activity to start. [Required]
	 * @example
	 *          *.startActivity("com.foo.bar", ".MyActivity");
	 * @see StartsActivity#startActivity(String, String)
	 */
	protected void switchToActivity(String packageApp, String activityPage){

		androidDriver.startActivity(packageApp, activityPage);
	}

	protected boolean takeFullScreenShot(String failure){

		boolean returnValue = false;
		String timeStamp = CommonTools.getDate().replace("-", "") + "_" + CommonTools.getCurrentTime().replace(":", "").replace(".", "");
		failure = timeStamp + "_" + CommonTools.replaceIllegalFileName(failure, "_");
		if(StringUtils.endsWith(failure, "_"))
			failure = timeStamp;

		if(enableScreenCapture){
			String fileName = screenCapturePath + "/" + failure + ".png";

			getScreenShot(fileName);

			returnValue = true;
		}

		return returnValue;
	}

	protected void getScreenShot(String fileName){

		File screenshot = androidDriver.getScreenshotAs(OutputType.FILE);
		try{
			copyScreenShot(screenshot, new File(fileName));
		} catch(IOException e){
			log("Exception happen when getting screen shot, detail is : [" + e.getMessage() + "]. " + "The screen shot operatioin was ignored. ", 3);
		}

	}

	private boolean getAndroidText(String className, String expectedText){

		boolean validate = false;
		List<WebElement> listText = androidDriver.findElements(By.className(className));
		int textSize = listText.size();
		for(int i = 0; i < textSize; i ++ ){
			String bodyText = listText.get(i).getText();
			if(bodyText.contains(expectedText) || bodyText.matches(expectedText)){
				validate = true;
				break;
			}
		}

		return validate;
	}

	@Override
	protected boolean verifyBodyTextContainsExpectedText(String expectedText, boolean isShown, boolean needWait){

		boolean returnValue = false;
		Long currentTimeMillis = System.currentTimeMillis();
		try{
			if(needWait){
				while((System.currentTimeMillis() - currentTimeMillis) < Long.parseLong(elementTimeout)){
					returnValue = getAndroidText("android.widget.TextView", expectedText);
					if(isShown == returnValue){
						break;
					}
					waitByTimeout(500);
				}
			} else{
				waitByTimeout(1000);
				returnValue = getAndroidText("android.widget.TextView", expectedText);
			}
		} catch(Exception e){

		}
		return returnValue;
	}
}
