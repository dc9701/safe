package base;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.core.TestUiClass;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class IOSApp extends TestUiClass{

	protected IOSApp(){

		this("");
	}

	protected IOSApp(String SUT){

		this(SUT, "");
	}

	protected IOSApp(String SUT,String profileName){

		super(SUT, profileName);
		platformSupportInitiate(profileName);

	}

	protected String getAppType(){

		return "IOSApp";
	}

	@Override
	protected void platformSupportInitiate(String profileName){

		startAppiumDriver(profileName);
		prepareTestEnvironment();
	}

	protected void startAppiumDriver(String app_apk){

		log("uninstall app first");
		uninstallApp();
		log("Reinstalling...");
		installApp();

		try{
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(CapabilityType.BROWSER_NAME, getProperty("app.browser.Name"));
			capabilities.setCapability("platformVersion", getProperty("app.device.version"));
			capabilities.setCapability("platform", getProperty("app.os.platform"));
			capabilities.setCapability("deviceName", getProperty("app.device.name"));
			capabilities.setCapability("platformName", getProperty("app.device.platformName"));
			capabilities.setCapability("app", getProperty("app.path"));

			iosDriver = new IOSDriver(new URL("http://" + getProperty("app.appium.serverIP") + "/wd/hub"), capabilities);
			driver = iosDriver;
		} catch(Exception e){
			log("Cannot launch application from the mobile device", 2);
			throw new RuntimeException(e);
		}

		new WebDriverWait(iosDriver, 10);
	}

	public void printScreenContent(){

		// TODO Auto-generated method stub
		log(iosDriver.getPageSource().toString());
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
			iosDriver.tap(1, x, y, pressTime);
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

	private void tap(int fingers, String elementName, int duration){

		tap(fingers, elementName, "", "", duration);

	}

	private void tap(int fingers, String listName, Object itemMatching, int duration){

		tap(fingers, listName, itemMatching, "", duration);
	}

	private void tap(int fingers, String listName, Object itemMatching, String elementName, int duration){

		if(waitForElement(listName, itemMatching, elementName)){
			WebElement element = getElement(listName, itemMatching, elementName);
			iosDriver.tap(fingers, element, duration);
			log("Tap on '" + (elementName.isEmpty()?listName:elementName) + "'.");
		} else{

			log("tap on element failed.");
			throw new RuntimeException("tapOn element failed.");

		}

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

	private void swipe(int startX, int startY, int endX, int endY, int duration){

		iosDriver.swipe(startX, startY, endX, endY, duration);
	}

	private void swipeOfType(String type){

		log("Swiping " + type + ".");
		int windowlenX = iosDriver.manage().window().getSize().getWidth();
		int windowlenY = iosDriver.manage().window().getSize().getHeight();
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

		iosDriver.pinch(element);

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

		iosDriver.pinch(element);

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

		iosDriver.pinch(x, y);
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

		iosDriver.zoom(x, y);
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

		iosDriver.zoom(element);
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

		iosDriver.zoom(element);
	}

	/**
	 * Scroll to the specified text in a list.
	 * 
	 * @param text
	 *            The text you wish to scroll to.
	 */
	public void scrollTo(String text){

		MobileElement element = (IOSElement)iosDriver.findElement(By.className("UIATableView"));
		((IOSElement)element).scrollTo(text);
	}

	/**
	 * Close the current application.
	 */
	public void close(){

		iosDriver.closeApp();
		iosDriver.quit();
	}

	/**
	 * Uninstall the Application being tested.
	 */
	public void uninstallApp(){

		String appbundle = getProperty("app.bundle");
		StringBuffer output = new StringBuffer();
		try{
			Process p;
			p = Runtime.getRuntime().exec(
					"/Applications/Appium.app/Contents/Resources/node_modules/appium/build/fruitstrap/fruitstrap uninstall --bundle " + appbundle);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while((line = reader.readLine()) != null){
				output.append(line + "\n");
			}
			// log(output.toString());
		} catch(IOException e){
			e.printStackTrace();
		} catch(InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Install the Application to be tested.
	 */
	public void installApp(){

		String appPath = getProperty("app.path");
		StringBuffer output = new StringBuffer();
		try{
			Process p;
			String command = "/Applications/Appium.app/Contents/Resources/node_modules/appium/build/fruitstrap/fruitstrap install --bundle " + appPath;
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while((line = reader.readLine()) != null){
				output.append(line + "\n");
			}
			reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while((line = reader.readLine()) != null){
				output.append(line + "\n");
			}
			// log(output.toString());
		} catch(IOException e){
			e.printStackTrace();
		} catch(InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean getIOSText(String className, String expectedText){

		boolean validate = false;
		List<WebElement> listText = iosDriver.findElements(By.className(className));
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
					returnValue = getIOSText("UIATableView", expectedText);
					if(isShown == returnValue){
						break;
					}
					waitByTimeout(500);
				}
			} else{
				waitByTimeout(1000);
				returnValue = getIOSText("UIATableView", expectedText);
			}
		} catch(Exception e){

		}
		return returnValue;
	}

	/**
	 * Convenience method for getting abscissa of device screen
	 * 
	 * @return
	 */
	protected int getCoorinateX(){

		return iosDriver.manage().window().getSize().getWidth();

	}

	/**
	 * Convenience method for getting ordinate of device .
	 * 
	 * @return
	 */
	protected int getCoorinateY(){

		return iosDriver.manage().window().getSize().getHeight();

	}

}
