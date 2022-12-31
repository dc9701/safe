package base.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.w3c.dom.Document;

import tools.commonTools.CommonTools;
import tools.commonTools.sendLEDCommand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Test class for exercising an application, device or REST interfaces.
 * 
 * Provides methods for inspection of and interaction with application elements,
 * navigation within an application, and verification of expected results.
 */

public abstract class TestUiClass extends TestClass{

	protected String iFrameName = "";
	protected String iFrameAreaName = "";
	private boolean blankAreaLocator = false;

	protected static String browserHost = "";
	protected static String browserPort = "";
	protected static String browserName = "";
	protected static String browserSize = "";
	protected String targetProfile;

	private static ObjectNode content = jsonMapper.createObjectNode();
	private static ObjectNode contentLookup = jsonMapper.createObjectNode();

	protected JsonNode uiMapCurrentPage = jsonMapper.createObjectNode();
	protected JsonNode uiMapCurrentView = jsonMapper.createObjectNode();
	protected JsonNode uiMapCurrentArea = jsonMapper.createObjectNode();
	protected JsonNode uiMapCurrentElement = jsonMapper.createObjectNode();

	protected JsonNode myCurrentPage = jsonMapper.createObjectNode();
	protected JsonNode myCurrentView = jsonMapper.createObjectNode();
	protected JsonNode myCurrentArea = jsonMapper.createObjectNode();
	protected JsonNode myCurrentElement = jsonMapper.createObjectNode();

	/*
	 * Default UI response time thresholds for debugging performance issues and timeout values.
	 */

	protected Integer maxPageTime = 20000;
	protected boolean measureResponseTime = false;

	protected String browserProfilePath = "";
	protected static String FILEDIR = testRoot + "data/";
	protected String timeStamp = CommonTools.getDate().replace("-", "") + "_" + CommonTools.getCurrentTime().replace(":", "").replace(".", "");

	protected final static String PREVIOUS_VIEW = "previousView";
	protected ArrayList<String> uiMapViewList = new ArrayList<String>();
	protected Integer uiMapViewIndex = - 1;

	protected HashMap<String, HashMap<String, String>> uiMapBookmarks = new HashMap<String, HashMap<String, String>>();

	protected ArrayList<String> uiMapAreasAlreadyChecked = new ArrayList<String>();
	protected ArrayList<String> uiMapViewsAlreadyChecked = new ArrayList<String>();

	/*
	 * The below two parameters define whether to capture the screen for failures.
	 */
	protected static boolean enableScreenCapture = false;
	protected static String screenCapturePath = "";

	/*
	 * Selenium session objects.
	 */
	protected WebDriver driver;
	protected AndroidDriver androidDriver;
	protected IOSDriver iosDriver;
	// protected HttpCommandProcessor proc;

	/*
	 * The first window of the application.
	 */
	protected static String main_window;

	/*
	 * UiMap locator for clickOn() and setViewTo().
	 */
	protected String locator = "";
	protected boolean clickableList = false;

	/*
	 * UI Map is used to navigate the application and locate application elements. These
	 * help us do the work. We keep the current page, view, area and element cached.
	 */
	protected JsonNode uiMap;

	protected TestUiClass(){

		this("", "");
	}

	protected TestUiClass(String SUT){

		this(SUT, "");
	}

	protected TestUiClass(String SUT,String profile){

		super(SUT, profile);
		languageSupportInitiate();

	}

	@Override
	protected void languageSupportInitiate(){

		String uiKey = StringUtils.defaultIfEmpty(getInitialProperty("path.conf.ui"), "ui/");
		uiKey = StringUtils.replace(uiKey, "//", "/", - 1);

		projectLevelUiConfigPath = (testConfigRoot + getAppName() + "/" + uiKey).replace("//", "/");

		appLocale = getProperty("application.locale");

		/*
		 * Read the UI response thresholds from InitialConfig.
		 */
		String jsonFiles = getProperty("conf.ui." + getAppType() + ".json");
		if(jsonFiles.contains(",")){
			String jsonFile[] = jsonFiles.split(",");
			uiMap = CommonTools.getDataFromConfigFile(getUiPath() + jsonFile[0]);
			for(int i = 1; i < jsonFile.length; i ++ ){
				JsonNode uiMaps = CommonTools.getDataFromConfigFile(getUiPath() + jsonFile[i].trim());
				uiMap = CommonTools.merge(uiMap, uiMaps);
			}
		} else{
			uiMap = CommonTools.getDataFromConfigFile(getUiFullFileName());
		}

		content = (ObjectNode)CommonTools.getDataFromConfigFile(getMessagesFileFullName());
		JsonNode additionalContent = CommonTools.getDataFromConfigFile(getContentFileFullName());
		CommonTools.mergeProperties(content, additionalContent);

		/*
		 * Repeat the process for the lookup file.
		 */
		contentLookup = (ObjectNode)CommonTools.getDataFromConfigFile(getMessagesLookUpFileFullName());
		JsonNode additionalLookupContent = CommonTools.getDataFromConfigFile(getContentLookUpFileFullName());
		CommonTools.mergeProperties(contentLookup, additionalLookupContent);

		if(Boolean.valueOf(getInitialProperty("measureResponseTime")))
			measureResponseTime = true;

		if( ! StringUtils.isEmpty(getInitialProperty("maxPageTime")))
			maxPageTime = Integer.parseInt(initialConfig.path("maxPageTime").textValue());

		appUrl = getProperty("application.url");
		appApiUrl = getProperty("application.apiurl");

		pageTimeout = StringUtils.defaultIfEmpty(getProperty("test.timeout.page"), pageTimeout);
		viewTimeout = StringUtils.defaultIfEmpty(getProperty("test.timeout.view"), viewTimeout);
		elementTimeout = StringUtils.defaultIfEmpty(getProperty("test.timeout.element"), elementTimeout);
		playbackSpeed = StringUtils.defaultIfEmpty(getProperty("test.speed"), playbackSpeed);

	}

	private String getUiPath(){

		return projectLevelUiConfigPath.replace("[appname]", getAppName());
	}

	private String getUiFullFileName(){

		return getUiPath() + getProperty("conf.ui." + getAppType() + ".json");
	}

	private String getContentFileFullName(){

		String contentFileFullName = getUiPath() + getProperty("conf.ui." + getAppType() + ".content");
		if(appLocale.isEmpty() || appLocale.length() > 2)
			return contentFileFullName;
		else
			contentFileFullName += "_" + appLocale;
		return contentFileFullName;
	}

	private String getMessagesFileFullName(){

		String messagesFileName = getUiPath() + getProperty("conf.ui." + getAppType() + ".messages");
		if(appLocale.isEmpty() || appLocale.length() > 2)
			return messagesFileName;
		else
			messagesFileName += "_" + appLocale;
		return messagesFileName;
	}

	private String getMessagesLookUpFileFullName(){

		String messagesFileName = getUiPath() + getProperty("conf.ui." + getAppType() + ".messages.lookup");
		return messagesFileName;
	}

	private String getContentLookUpFileFullName(){

		String contentFileFullName = getUiPath() + getProperty("conf.ui." + getAppType() + ".content.lookup");
		return contentFileFullName;
	}

	@Override
	protected void prepareTestEnvironment(){

		super.prepareTestEnvironment();

		/*
		 * Get screen capture configurations & Initialize the screenCapturePath.
		 */
		enableScreenCapture = getInitialProperty("screen.capture.forFailures.enable").equalsIgnoreCase("true");
		screenCapturePath = (testRoot.replace("test-classes/", "") + getInitialProperty("screen.capture.forFailures.path"));

		File screenCaptureFolder = new File(screenCapturePath);

		if( ! screenCaptureFolder.exists())
			screenCaptureFolder.mkdirs();

	}

	protected void get(String url){

		driver.get(appUrl + url);
	}

	/**
	 * Opens the specified URL in the current browser window.
	 * 
	 * Parameters:
	 * 
	 * @param newurl
	 *            - The URL you want to open.
	 */
	public void openNewUrl(String newurl){

		driver.get(newurl);
	}

	/**
	 * Open the application to the default view.
	 * 
	 * We open the application URL defined in the SUT config file (and maximize the browser window if set),
	 * then set the default view specified in the UI Map (e.g., the landing page).
	 * 
	 * @return
	 *         true if it successfully opens the application to the default view; otherwise, returns false.
	 */
	public boolean openApp(){

		return openApp("");
	}

	/**
	 * Open the application to the view specified.
	 * 
	 * @param view
	 *            Open the application to the specified view.
	 * 
	 * @return
	 *         true if it successfully opens the application to the specified view; otherwise, return false.
	 */
	public boolean openApp(String view){

		log("<===============COMMENCING THE TEST================>");
		try{

			boolean openApp = uiMapSetView(view);

			if(browserSize.equalsIgnoreCase("maximize"))
				driver.manage().window().maximize();

			if(browserSize.equalsIgnoreCase("restore")){
				Dimension winSize = new Dimension(1024, 768);
				driver.manage().window().setSize(winSize);
			}

			return openApp;

		} catch(Exception e){
		}

		return false;
	}

	/**
	 * Click on an element.
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @return true if successfully finds the specified element,
	 *         clicks on it and (if the view should change according to the UI
	 *         Map) the appropriate view is displayed. Otherwise, returns false.
	 * 
	 **/
	public boolean clickOn(String elementName){

		return clickOn(elementName, "", "", "");
	}

	/**
	 * Click on an item in a collection (list, grid, drop-down menu, etc.) that matches a regex or string.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return true if successfully finds the specified element,
	 *         clicks on it and (if the view should change according to the UI
	 *         Map) the appropriate view is displayed. Otherwise, returns false.
	 * 
	 **/
	public boolean clickOn(String listName, String itemMatching){

		return clickOn(listName, itemMatching, "", "");
	}

	/**
	 * Click on an item in a collection (list, grid, drop-down menu, etc.) by its index.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return true if successfully finds the specified element,
	 *         clicks on it and (if the view should change according to the UI
	 *         Map) the appropriate view is displayed. Otherwise, returns false.
	 * 
	 **/
	public boolean clickOn(String listName, int itemMatching){

		return clickOn(listName, itemMatching, "", "");
	}

	/**
	 * Click on a specific element contained within a list item that matches a regex or string.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked.
	 * Activators are defined in the UI Map using "activator": &lt;value&gt;.
	 * 
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in the current view
	 * for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return true if successfully finds the specified element,
	 *         clicks on it and (if the view should change according to the UI
	 *         Map) the appropriate view is displayed. Otherwise, returns false.
	 * 
	 **/
	public boolean clickOn(String listName, String itemMatching, String elementName){

		return clickOn(listName, itemMatching, elementName, "");
	}

	/**
	 * Click on a specific element contained within a list item specified by index.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked.
	 * Activators are defined in the UI Map using "activator": &lt;value&gt;.
	 * 
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in the current view
	 * for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return true if successfully finds the specified element,
	 *         clicks on it and (if the view should change according to the UI
	 *         Map) the appropriate view is displayed. Otherwise, returns false.
	 * 
	 **/
	public boolean clickOn(String listName, int itemMatching, String elementName){

		return clickOn(listName, itemMatching, elementName, "");
	}

	/**
	 * Click on an element within a list item that matches the specified criteria.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked. Activators
	 * are defined in the UI Map using "activator": &lt;value&gt;.
	 * 
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in
	 * the current view for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @param message
	 *            if message is not null , when you click on element the message is pops up.
	 * 
	 * @return true if successfully finds the specified element
	 *         within a matching list item, clicks on it and (if the view should
	 *         change according to the UI Map) the appropriate view is
	 *         displayed. Otherwise, returns false.
	 * */
	private boolean clickOn(String listName, Object itemMatching, String elementName, String message){

		boolean returnValue = false;

		String activator = getElementAtt("activator", listName);
		if(activator.isEmpty())
			activator = getElementAtt("activator", elementName);
		log("Clicking on '" + (elementName.isEmpty()?listName:elementName) + "'.");

		String view = getTargetView("click", elementName.isEmpty()?listName:elementName, "");
		returnValue = clickElement(listName, itemMatching, elementName, activator, message);

		if(returnValue){
			returnValue = uiMapUpdated(view);
		} else{
			throw new RuntimeException("Element clicking Error.");
		}
		if( ! returnValue){
			throw new RuntimeException("uiMap Update Error.");
		}

		return returnValue;

	}

	/**
	 * Click on an element within a list item that matches the specified criteria.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked. Activators
	 * are defined in the UI Map using "activator": &lt;value&gt;.
	 * 
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in
	 * the current view for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @param message
	 *            if message is not null , when you click on element the message is pops up.
	 * 
	 * @return true if successfully finds the specified element
	 *         within a matching list item, clicks on it and (if the view should
	 *         change according to the UI Map) the appropriate view is
	 *         displayed. Otherwise, returns false.
	 * */
	private boolean clickElement(String listName, Object itemMatching, String elementName, String activator, String message){

		boolean locatorIsVisible = false;

		if( ! activator.isEmpty() && ! isElementShown(elementName.isEmpty()?listName:elementName)){
			log("locator is invisible, clicking activator '" + activator + "'.");

			getActivatorisVisbleAndClick((elementName.isEmpty()?listName:elementName), activator, locatorIsVisible);
		}

		return clickLocator(listName, itemMatching, elementName, message);
	}

	/**
	 * Click on an element within a list item that matches the specified criteria.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked. Activators
	 * are defined in the UI Map using "activator": &lt;value&gt;.
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in
	 * the current view for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param activator
	 *            locator is invisible, try to click activator.
	 * 
	 * @param locatorIsVisible
	 *            if locatorIsVisible is not true , try to click activator.
	 * 
	 * @return true if successfully finds the specified element
	 *         within a matching list item, clicks on it and (if the view should
	 *         change according to the UI Map) the appropriate view is
	 *         displayed. Otherwise, returns false.
	 * */
	private boolean getActivatorisVisbleAndClick(String elementName, String activator, boolean locatorIsVisible){

		if( ! StringUtils.isEmpty(activator) && ! locatorIsVisible){

			if(waitForElement(activator)){
				waitForCondition("selenium.browserbot.getUserWindow().$.active == 0;", pageTimeout);
				getElement(activator).click();

				if( ! (locatorIsVisible = waitForElement(elementName))){
					getElement(activator).click();
					locatorIsVisible = waitForElement(elementName);
				}

			}
		}
		return locatorIsVisible;
	}

	/**
	 * Click on an element within a list item that matches the specified criteria.
	 * 
	 * 1. Some elements (like values in a drop down list) may not be present in the DOM until after their activator is clicked. Activators
	 * are defined in the UI Map using "activator": &lt;value&gt;.
	 * 2. Many clickable items will cause navigation to a new view; this is also specified in the UI Map using "view": &lt;value&gt;.
	 * 
	 * 3. All the heavy lifting is done in the private method uiMapClickOnLocatorAndSetView(). This means checking all areas in
	 * the current view for the element specified, and if it is a list item, locating it by index or by a matching string or regex.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you wish to click (from the uiMap).
	 * 
	 * @param message
	 *            if message is not null , when you click on element the message is pops up.
	 * 
	 * @return true if successfully finds the specified element
	 *         within a matching list item, clicks on it and (if the view should
	 *         change according to the UI Map) the appropriate view is
	 *         displayed. Otherwise, returns false.
	 * */
	private boolean clickLocator(String listName, Object itemMatching, String elementName, String message){

		boolean returnValue = false;
		/*
		 * If itemMatching is NOT empty, then determine the locator for a
		 * matching item (up to this point, we've been searching for ANY item in
		 * the list).
		 */
		if(waitForElement(listName, itemMatching, elementName)){
			try{
				getElement(listName, itemMatching, elementName).click();
			} catch(StaleElementReferenceException e){
				if(waitForElement(listName, itemMatching, elementName))
					getElement(listName, itemMatching, elementName).click();
			}
			returnValue = true;
			if( ! StringUtils.isEmpty(message)){

				returnValue = verifyIsShown(message);

				if( ! returnValue){
					returnValue = verifyIsShown(message);
				}
			}

			return returnValue;
		}
		log("FAIL uiMapClickOnLocatorAndSetView() - Could not find '" + ((elementName.isEmpty())?listName:elementName) + "' to click on"
				+ (StringUtils.isEmpty(elementName)?".":" for an item matching '" + itemMatching + "' in the list named '" + listName + "'."), 2);
		takeFullScreenShot("Click_" + ((elementName.isEmpty())?listName:elementName));
		Assert.fail("FAIL uiMapClickOnLocatorAndSetView() - Could not find '" + ((elementName.isEmpty())?listName:elementName) + "' to click on"
				+ (StringUtils.isEmpty(elementName)?".":" for an item matching '" + itemMatching + "' in the list named '" + listName + "'."));
		return returnValue;
	}

	/**
	 * Update the uiMap to the current view.
	 * 
	 * @param view
	 *            The current view.
	 * 
	 * @return true if the view is changed and it successfully updates the uiMap
	 *         to that view, or if the view is not changed.
	 *         Otherwise, returns false.
	 */
	protected boolean uiMapUpdated(String view){

		boolean returnValue = false;

		if(StringUtils.isEmpty(view)){
			log("View is not changed.");
			returnValue = true;
		} else{
			returnValue = uiMapUpdateView(view);
		}

		return returnValue;
	}

	/**
	 * Get the title of the current page in the browser.
	 * 
	 * @return
	 *         The title of the current page.
	 */
	public String getTitle(){

		return driver.getTitle();
	}

	/**
	 * Click on a specific element within an item in a collection (match by regex or string) and verify that a message is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param message
	 *            The message (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 */
	public boolean verifyClickOnAndMessageIsShown(String listName, String itemMatching, String elementName, String message){

		return clickOn(listName, itemMatching, elementName, message);
	}

	/**
	 * Click on a specific element within an item in a collection (match by index) and verify that a message is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param message
	 *            The message (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 */
	public boolean verifyClickOnAndMessageIsShown(String listName, int itemMatching, String elementName, String message){

		return clickOn(listName, itemMatching, elementName, message);
	}

	/**
	 * Click on an item in a collection (match by regex or string) and verify that a message is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param message
	 *            The message (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 */
	public boolean verifyClickOnAndMessageIsShown(String listName, String itemMatching, String message){

		return clickOn(listName, itemMatching, "", message);
	}

	/**
	 * Click on an item in a collection (match by index) and verify that a message is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param message
	 *            The message (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 */
	public boolean verifyClickOnAndMessageIsShown(String listName, int itemMatching, String message){

		return clickOn(listName, itemMatching, "", message);
	}

	/**
	 * Click on the specified element and verify that a message is shown.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param message
	 *            The message (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 */
	public boolean verifyClickOnAndMessageIsShown(String elementName, String message){

		return clickOn(elementName, "", "", message);
	}

	/**
	 * Click on a specific element within an item in a collection (match by regex or string) and verify that an error is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param errorMessage
	 *            The error (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 * 
	 *         TODO: This method is now identical to verifyClickOnAndMessageIsShown(). There was originally a significant
	 *         difference, in the way dialogs were handled - for messages, the dialog is successfully dismissed and
	 *         the UI map is updated. For errors, the dialog remains, and the UI map is NOT updated. We should
	 *         restore this difference in functionality.
	 */
	public boolean verifyClickOnAndErrorIsShown(String listName, String itemMatching, String elementName, String errorMessage){

		return clickOn(listName, itemMatching, elementName, errorMessage);
	}

	/**
	 * Click on a specific element within an item in a collection (match by index) and verify that an error is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param errorMessage
	 *            The error (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 * 
	 *         TODO: This method is now identical to verifyClickOnAndMessageIsShown(). There was originally a significant
	 *         difference, in the way dialogs were handled - for messages, the dialog is successfully dismissed and
	 *         the UI map is updated. For errors, the dialog remains, and the UI map is NOT updated. We should
	 *         restore this difference in functionality.
	 */
	public boolean verifyClickOnAndErrorIsShown(String listName, int itemMatching, String elementName, String errorMessage){

		return clickOn(listName, itemMatching, elementName, errorMessage);
	}

	/**
	 * Click on an item in a collection (match by regex or string) and verify that an error is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param errorMessage
	 *            The error (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 * 
	 *         TODO: This method is now identical to verifyClickOnAndMessageIsShown(). There was originally a significant
	 *         difference, in the way dialogs were handled - for messages, the dialog is successfully dismissed and
	 *         the UI map is updated. For errors, the dialog remains, and the UI map is NOT updated. We should
	 *         restore this difference in functionality.
	 */
	public boolean verifyClickOnAndErrorIsShown(String listName, String itemMatching, String errorMessage){

		return clickOn(listName, itemMatching, "", errorMessage);
	}

	/**
	 * Click on an item in a collection (match by index) and verify that an error is shown.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param errorMessage
	 *            The error (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 * 
	 *         TODO: This method is now identical to verifyClickOnAndMessageIsShown(). There was originally a significant
	 *         difference, in the way dialogs were handled - for messages, the dialog is successfully dismissed and
	 *         the UI map is updated. For errors, the dialog remains, and the UI map is NOT updated. We should
	 *         restore this difference in functionality.
	 */
	public boolean verifyClickOnAndErrorIsShown(String listName, int itemMatching, String errorMessage){

		return clickOn(listName, itemMatching, "", errorMessage);
	}

	/**
	 * Click on an element and verify that an error is shown.
	 * 
	 * @param elementName
	 *            The name of the element you want to click on.
	 * 
	 * @param errorMessage
	 *            The error (literal text, regex or a message id) you expect displayed.
	 * 
	 * @return true if the element was clicked and the message was displayed; otherwise, returns false.
	 * 
	 *         TODO: This method is now identical to verifyClickOnAndMessageIsShown(). There was originally a significant
	 *         difference, in the way dialogs were handled - for messages, the dialog is successfully dismissed and
	 *         the UI map is updated. For errors, the dialog remains, and the UI map is NOT updated. We should
	 *         restore this difference in functionality.
	 */
	public boolean verifyClickOnAndErrorIsShown(String elementName, String errorMessage){

		return clickOn(elementName, "", "", errorMessage);
	}

	/**
	 * Go back to the previous view.
	 * 
	 * Returns to the previous view by simulating a click on the browser Back button, then updates
	 * the UI Map in a way that retains forward breadcrumbs so that forward() works as expected.
	 * 
	 * TODO: Restore functionality of updating the UI Map to back().
	 */
	public void back(){

		log("Clicking 'Back' button");
		driver.navigate().back();
		waitByTimeout(Long.parseLong(pageTimeout));
		return;
	}

	/**
	 * Go forward to the next page (after having previously used back() at least once).
	 * 
	 * Advances to a prior view by simulating a click on the browser Forward button, then updates
	 * the UI Map in a way that enables back() works as expected.
	 * 
	 * @return true if successfully navigates forward (or there is
	 *         no forward view to go to); otherwise, returns false.
	 * 
	 *         TODO: Restore functionality of updating the UI Map to forward().
	 */
	public boolean forward(){

		/*
		 * If we are already at the end of uiMapViewList, do nothing and return
		 * true; otherwise, increment uiMapViewIndex and update the UI Map with
		 * the new view.
		 */
		driver.navigate().forward();
		return true;
	}

	/**
	 * Refresh the current page. The UI Map is not changed.
	 * 
	 * @return Always returns true.
	 */

	public boolean refresh(){

		driver.navigate().refresh();
		driver.manage().timeouts().implicitlyWait(Integer.parseInt(pageTimeout), TimeUnit.SECONDS);
		return true;
	}

	/**
	 * Opens the requested view.
	 * 
	 * Uses the UI Map to determine how to navigate to the requested view from the current view.
	 * 
	 * @param requestedView
	 *            The "Page:View" you wish to display. If blank (""), it will
	 *            open the default view of the default page of the application
	 *            as specified in the UI Map.
	 * 
	 * @return true if successfully opens the specified view; otherwise, returns false.
	 */

	public boolean setViewTo(String requestedView){

		boolean setViewTo = uiMapSetView(requestedView);
		return setViewTo;
	}

	/**********************************************************************************************
	 * The following public methods are used in Test Cases for high-level
	 * Create/Read/Update/Delete.
	 * 
	 * get() - Gets the value of an element or element attribute.
	 * setValueTo() - Sets the value of an element.
	 **********************************************************************************************/

	/**
	 * Gets the value of the specified element (text box, select list, checkbox, etc.).
	 * 
	 * Gets the value of inputs (text boxes), the label of selects, the "on" or "off" value for checkboxes,
	 * and the text of other elements.
	 * 
	 * @param elementName
	 *            The name of the element you want to get the value of.
	 * 
	 * @return Returns the value of the specified element or returns an empty
	 *         string ("") if the element was not found.
	 */
	public String getValueOf(String elementName){

		return getValueOf(elementName, "", "", "");
	}

	/**
	 * Gets the value of an item in a collection (list, grid, drop-down, etc.), matching by string or regex.
	 * 
	 * Gets the value of all text in the item.
	 * 
	 * @param listName
	 *            The name of the list containing the item.
	 * 
	 * @param itemMatching
	 *            A message id, string or regex to locate a specific item in the collection.
	 * 
	 * @return Returns the value of all the text in the item or returns an empty
	 *         string ("") if the item was not found.
	 */
	public String getValueOf(String listName, String itemMatching){

		return getValueOf(listName, itemMatching, "", "");
	}

	/**
	 * Gets the value of an item in a collection (list, grid, drop-down, etc.), matching by index.
	 * 
	 * Gets the value of all text in the item.
	 * 
	 * @param listName
	 *            The name of the list containing the item.
	 * 
	 * @param itemMatching
	 *            A message id, string or regex to locate a specific item in the collection.
	 * 
	 * @return Returns the value of all the text in the item or returns an empty
	 *         string ("") if the item was not found.
	 */
	public String getValueOf(String listName, int itemMatching){

		return getValueOf(listName, itemMatching, "", "");
	}

	/**
	 * Gets the value of an element in a list item that matches the specified string or regex.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to get the value of.
	 * 
	 * @return Returns the value of the specified element or returns an empty
	 *         string ("") if the element or a matching list item were not
	 *         found.
	 */
	public String getValueOf(String listName, String itemMatching, String elementName){

		return getValueOf(listName, itemMatching, elementName, "");
	}

	/**
	 * Gets the value of an element in a list item, matching by index.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to get the value of.
	 * 
	 * @return Returns the value of the specified element or returns an empty
	 *         string ("") if the element or a matching list item were not
	 *         found.
	 */
	public String getValueOf(String listName, int itemMatching, String elementName){

		return getValueOf(listName, itemMatching, elementName, "");
	}

	/**
	 * Gets the value of an attribute of an element in a list item that matches
	 * a literal string or regex expression.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 */
	public String getValueOf(String listName, String itemMatching, String elementName, String attribute){

		return getValueOf(listName, itemMatching, elementName, attribute, "");
	}

	/**
	 * Gets the value of an attribute of an element in a list item, matching by index.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 */
	public String getValueOf(String listName, int itemMatching, String elementName, String attribute){

		return getValueOf(listName, itemMatching, elementName, attribute, "");
	}

	/**
	 * Gets the value of an attribute of an element in a list item that matches
	 * a literal string or regex expression.
	 * 
	 * NOTE: Used by verifyValueOf().
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 */
	private String getValueOf(String listName, Object itemMatching, String elementName, String attribute, String flag){

		String actualValue = "";

		String elementLocator = "";
		if(waitForElement(listName, itemMatching, elementName)){

			/*
			 * Get the value based upon the input type and specified attribute
			 * (if any).
			 */

			if(attribute == ""){

				/*
				 * Get the value of a text input area or text box.
				 */
				String elementType = getElementType(listName, itemMatching, elementName);
				if(StringUtils.contains(elementType, "input")){
					actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute("value"));
				}

				if(StringUtils.contains(elementType, "text")){
					actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getText());
				}

				/*
				 * Get the value of a checkbox.
				 */

				if(StringUtils.contains(elementType, "checkbox")){

					String checkboxClass = getElement(listName, itemMatching, elementName).getAttribute("class");

					actualValue = StringUtils.contains(checkboxClass, "checked")?"checked":"unchecked";
				}

				/*
				 * Get the value of a select drop-down list.
				 * TODO: Develop a better solution that supports L10n of select
				 * labels (the visible text). Using ui.getSelectedValue() isn't
				 * useful, because it requires test developers to search the
				 * DOM. Both getValueOf() and setValueTo() need to be consistent
				 * in how they handle selects.
				 */

				if(StringUtils.contains(elementType, "select")){

					WebElement element = getElement(listName, itemMatching, elementName);
					for(int i = 0; i < getElementsSize(elementName.isEmpty()?listName:elementName); i ++ ){
						WebElement selectItem = element.findElements(By.tagName("option")).get(i);
						if(selectItem.isSelected())
							actualValue = StringUtils.defaultString(selectItem.getText());
					}

				}

				/*
				 * Get the text value of any other element type.
				 */

				if(StringUtils.isBlank(actualValue)){
					elementLocator = getElement(listName, itemMatching, elementName).getText();
					actualValue = StringUtils.defaultString(elementLocator);
				}
				if( ! actualValue.isEmpty())
					log("get '" + (elementName.isEmpty()?listName:elementName) + "' tagName is '" + actualValue + "'.");
				else
					log("getValueOf() '" + (elementName.isEmpty()?listName:elementName) + "' is Empty!");
			} else{

				/*
				 * Get the value of a specific attribute of the element.
				 */

				actualValue = StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute(attribute));
				if( ! actualValue.isEmpty())
					log("get '" + (elementName.isEmpty()?listName:elementName) + "' tagName is '" + actualValue + "'.");
				else
					log("getValueOf() '" + (elementName.isEmpty()?listName:elementName) + "' is Empty!");
			}

			/*
			 * Strip any newlines out of the actual value. Actual value is
			 * rendered html, so we don't have to worry about html tags or
			 * entities.
			 */

			actualValue = actualValue.replaceAll("\n", "");

			return actualValue;
		}
		/*
		 * If unable to locate the specified element, log an error and return an
		 * empty string.
		 */

		if(StringUtils.isEmpty(actualValue))
			log("getValueOf() - Unable to locate element '" + (elementName.isEmpty()?listName:elementName) + "' " + elementLocator + "in current view "
					+ uiMapCurrentView.path("viewName") + ".", 2);
		return actualValue;

	}

	/**
	 * Removes the 'css=' from an element locator.
	 * 
	 * @param strName
	 *            The strName is element locator.
	 * 
	 * @return is element locator.
	 */
	protected String splitString(String strName){

		return strName.replace("css=", "");
	}

	/**
	 * Sets the value of an element (e.g., text input) by clearing it and sending a series of keystrokes.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to set the value of.
	 * 
	 * @param value
	 *            The value you want it set to.
	 */
	private void clearAndSendkeys(String listName, Object itemMatching, String elementName, String value){

		WebElement element = getElement(listName, itemMatching, elementName);
		element.clear();
		element.sendKeys(value);
	}

	/**
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 */
	protected void click(String elementName){

		click(elementName, "", "");
	}

	/**
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 */
	protected void click(String listName, String itemMatching){

		click(listName, itemMatching, "");
	}

	protected void click(String listName, int itemMatching){

		click(listName, itemMatching, "");
	}

	/**
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 */
	protected void click(String listName, Object itemMatching, String elementName){

		waitForElementShown(listName, itemMatching, elementName);
		getElement(listName, itemMatching, elementName).click();
	}

	/**
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * @return String
	 *         The String is element type.
	 */
	private String getElementType(String listName, Object itemMatching, String elementName){

		WebElement element = getElement(listName, itemMatching, elementName);

		if( ! element.isDisplayed()){
			log("Element is not displayed '" + elementName + "'.", 2);
			takeFullScreenShot(elementName);
			Assert.fail("Element is not displayed '" + elementName + "'.");
			throw new NullPointerException();
		}

		String elementType = element.getTagName();
		try{
			elementType += " " + element.getAttribute("type");
		} catch(Exception e){
			// Ignoring type attribute if "type" does not exist.
		}
		if(StringUtils.contains(elementType, "select")){
			return elementType = "select";
		} else if(StringUtils.contains(elementType, "checkbox")){
			return elementType = "checkbox";
		} else if(StringUtils.contains(elementType, "radio")){
			return elementType = "radio";
		} else if(StringUtils.contains(elementType, "text")){
			return elementType = "text";
		} else if(StringUtils.contains(elementType, "file")){
			return elementType = "file";
		} else{
			// Default type, in case we can't tell.
			return elementType = "input";
		}
	}

	/**
	 * Gets the value of an attribute of an element in a list item that matches
	 * a literal string or regex expression.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 * 
	 *         TODO: Are the getElementAttribute() methods redundant to the getValueOf() methods?
	 *         If so, we should remove them.
	 */
	public String getElementAttribute(String listName, String itemMatching, String elementName, String attribute){

		waitForElement(listName, itemMatching, elementName);
		String returnValue = getElement(listName, itemMatching, elementName).getAttribute(attribute);
		log("The attribute '" + attribute + "' of the element '" + (elementName.isEmpty()?listName:elementName) + "' is '"
				+ (returnValue.isEmpty()?"null":returnValue) + "'.");
		return returnValue;
	}

	/**
	 * Gets the value of an attribute of an element in a list item, matching by index.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 * 
	 *         TODO: Are the getElementAttribute() methods redundant to the getValueOf() methods?
	 *         If so, we should remove them.
	 */
	public String getElementAttribute(String listName, int itemMatching, String elementName, String attribute){

		waitForElement(listName, itemMatching, elementName);
		String returnValue = getElement(listName, itemMatching, elementName).getAttribute(attribute);
		log("The attribute '" + attribute + "' of the element '" + (elementName.isEmpty()?listName:elementName) + "' is '"
				+ (returnValue.isEmpty()?"null":returnValue) + "'.");
		return returnValue;
	}

	/**
	 * Gets the value of an attribute of a list item that matches
	 * a literal string or regex expression.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the list.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 * 
	 *         TODO: Are the getElementAttribute() methods redundant to the getValueOf() methods?
	 *         If so, we should remove them. Also, I'm not sure it is possible to get an
	 *         attribute for an entire list item - I thought this was only applicable to elements.
	 */
	public String getElementAttribute(String listName, String itemMatching, String attribute){

		return getElementAttribute(listName, itemMatching, "", attribute);
	}

	/**
	 * Gets the value of an attribute of a list item, matching by index.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the list.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 * 
	 *         TODO: Are the getElementAttribute() methods redundant to the getValueOf() methods?
	 *         If so, we should remove them. Also, I'm not sure it is possible to get an
	 *         attribute for an entire list item - I thought this was only applicable to elements.
	 */
	public String getElementAttribute(String listName, int itemMatching, String attribute){

		return getElementAttribute(listName, itemMatching, "", attribute);
	}

	/**
	 * Gets the value of an attribute of an element.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute you want to get the value of.
	 * 
	 * @return Returns the value of the specified element's attribute or returns
	 *         an empty string ("") if the element, attribute or a matching list
	 *         item were not found.
	 * 
	 *         TODO: Are the getElementAttribute() methods redundant to the getValueOf() methods?
	 *         If so, we should remove them.
	 */
	public String getElementAttribute(String elementName, String attribute){

		return getElementAttribute(elementName, "", "", attribute);
	}

	/**
	 * Sets the value of an element, based upon its type.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate a specific item in the
	 *            list.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param elementType
	 *            Is element type.
	 * 
	 * @param value
	 *            The value you want it set to. CheckBoxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 */
	private boolean operateOnEveryElementType(String listName, Object itemMatching, String elementName, String elementType, String value){

		boolean returnValue = false;
		/*
		 * Text and password inputs - set to value using type(). Also checks
		 * resource files to get appropriate localized string, if available.
		 */
		if(StringUtils.equals(elementType, "input") || StringUtils.equals(elementType, "text") || StringUtils.equals(elementType, "password")
				|| StringUtils.equals(elementType, "email")){

			value = StringUtils.defaultString(getLocalizedText(value), value);
			clearAndSendkeys(listName, itemMatching, elementName, value);
			returnValue = true;
		}

		/*
		 * Checkboxes - click() to toggle state, depending upon value ("true" or
		 * "false") and whether or not the checkbox is already set to the
		 * desired value. If we toggled the value or if the current value
		 * matched what we wanted, set returnValue = true.
		 */

		if(StringUtils.equals(elementType, "checkbox")){

			ArrayList<String> positiveValues = new ArrayList<String>();
			ArrayList<String> negativeValues = new ArrayList<String>();

			positiveValues.addAll(Arrays.asList("y", "yes", "true", "on", "checked"));
			negativeValues.addAll(Arrays.asList("n", "no", "false", "off", "unchecked"));

			if(positiveValues.contains(value.toLowerCase())){
				returnValue = true;
				if( ! StringUtils.containsIgnoreCase(StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute("class")),
						"checked"))
					click(listName, itemMatching, elementName);
			} else{
				if(negativeValues.contains(value.toLowerCase())){
					returnValue = true;
					if( ! StringUtils.containsIgnoreCase(StringUtils.defaultString(getElement(listName, itemMatching, elementName).getAttribute("class")),
							"checked"))
						click(listName, itemMatching, elementName);
				}
			}
		}

		/*
		 * Selects - Uses the default "label=" optionLocator to set the value.
		 * Also checks resource files to get appropriate localized string, if
		 * available.
		 */

		if(StringUtils.equals(elementType, "select")){

			value = StringUtils.defaultString(getLocalizedText(value), value);
			Select select = new Select(getElement(listName, itemMatching, elementName));
			select.selectByVisibleText(value);
			returnValue = true;
		}

		if(StringUtils.equals(elementType, "radio")){

			elementName = elementName.isEmpty()?listName:elementName;

			value = StringUtils.defaultString(getLocalizedText(value), value);
			String areaLocator = getElementAtt("areaLocator", elementName);
			String groupLocator = getElementAtt("groupLocator", elementName);
			String elementLocator = getElementAtt("locator", elementName);
			log("Trying to select radio button '" + value + "'.");
			try{
				getElements(areaLocator, groupLocator, value, elementLocator).get(0).click();
			} catch(IndexOutOfBoundsException e){
				log("Possible UiMap error! Cannot locate the target Radio Value!", 2);
				throw new RuntimeException("Possible UiMap error! Cannot locate the target Radio Value!");
			}
			returnValue = true;
		}

		if(StringUtils.equals(elementType, "file")){

			value = StringUtils.defaultString(getLocalizedText(value), value);
			WebElement element = getElement(listName, itemMatching, elementName);
			element.sendKeys(value);
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * Sets an element (text, checkbox, radio button, list) to the specified
	 * value.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            if element is not in a list.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, a regex used to find a list item with matching text,
	 *            or empty string if element is not in a list.
	 * 
	 * @param elementName
	 *            The name of the element you want to set.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 */
	private boolean setValueTo(String listName, Object itemMatching, String elementName, String value, String flag){

		boolean returnValue = false;
		if(waitForElement(listName, itemMatching, elementName)){
			String elementType = getElementType(listName, itemMatching, elementName);
			returnValue = operateOnEveryElementType(listName, itemMatching, elementName, elementType, value);
		}

		if( ! returnValue){
			log("setValueTo() - Could not set '" + (elementName.isEmpty()?listName:elementName) + "' " + value + "' in current view '"
					+ uiMapCurrentView.path("viewName") + "'.", 2);
			throw new RuntimeException("setValueTo() - Could not set '" + (elementName.isEmpty()?listName:elementName) + "' '" + locator + "' to '" + value
					+ "' in current view " + uiMapCurrentView.path("viewName") + ".");
		} else
			log("Value '" + value + "' is set for '" + (elementName.isEmpty()?listName:elementName) + "'.");
		return returnValue;
	}

	/**
	 * Sets an element (text, checkbox, radio button, list) within a list item that
	 * matches a string or regex to the specified value.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            if element is not in a list.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, a regex used to find a list item with matching text,
	 *            or empty string if element is not in a list.
	 * 
	 * @param elementName
	 *            The name of the element you want to set.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 */
	public boolean setValueTo(String listName, String itemMatching, String elementName, String value){

		return setValueTo(listName, itemMatching, elementName, value, "");
	}

	/**
	 * Sets an element (text, checkbox, radio button, list) within a list item (match by index)
	 * to the specified value.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            if element is not in a list.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, a regex used to find a list item with matching text,
	 *            or empty string if element is not in a list.
	 * 
	 * @param elementName
	 *            The name of the element you want to set.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 */
	public boolean setValueTo(String listName, int itemMatching, String elementName, String value){

		return setValueTo(listName, itemMatching, elementName, value, "");
	}

	/**
	 * Sets an element (text, checkbox, radio button, list) to the specified
	 * value.
	 * 
	 * Simply calls setValueTo(listName, itemMatching, elementName, value),
	 * which does all the work. Returns "" if elementName could not be found in
	 * the current page.
	 * 
	 * @param elementName
	 *            The name of the element you want to set.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 */
	public boolean setValueTo(String elementName, String value){

		return setValueTo(elementName, "", "", value);
	}

	/**
	 * Sets a list item that matches a string or regex to the specified value.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            if element is not in a list.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, a regex used to find a list item with matching text,
	 *            or empty string if element is not in a list.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 * 
	 *         TODO: Not sure that we can set the value of an entire list item;
	 *         I think this only applies to individual elements and elements within a list item.
	 */
	public boolean setValueTo(String listName, String itemMatching, String value){

		return setValueTo(listName, itemMatching, "", value);
	}

	/**
	 * Sets a list item (match by index) to the specified value.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            if element is not in a list.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, a regex used to find a list item with matching text,
	 *            or empty string if element is not in a list.
	 * 
	 * @param value
	 *            The value you want it set to. Checkboxes take "true" or "on"
	 *            to check, "false" or "off" to uncheck.
	 * 
	 * @return true if the element was found and set successfully;
	 *         otherwise, returns false.
	 * 
	 *         TODO: Not sure that we can set the value of an entire list item;
	 *         I think this only applies to individual elements and elements within a list item.
	 */
	public boolean setValueTo(String listName, int itemMatching, String value){

		return setValueTo(listName, itemMatching, "", value);
	}

	/**********************************************************************************************
	 * The following public methods are used in Test Cases for high-level verifications.
	 * 
	 * verifyClickOnAndErrorIsShown() - Verify clicking on an element displays an error message.
	 * 
	 * verifyClickOnAndMessageIsShown() - Verify clicking on an element displays a message.
	 * 
	 * verifyIsNotShown() - Verify an element or text is NOT visible on the page.
	 * 
	 * verifyIsShown() - Verify an element or text is visible on the page.
	 * 
	 * verifyValueOf() - Verify the value of an element matches a string or regex.
	 * 
	 * verifyViewIs() - Verify the current view is what we expect.
	 **********************************************************************************************/

	/**
	 * Verify that an element (optionally within a list item that matches a
	 * regex), a message (by ID) or literal text is either visible or NOT
	 * visible, based upon the value of isShown. For messages or literal text,
	 * will check all open windows. For elements, will only search the main
	 * application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementNameOrMessage
	 *            The name of an element, a message ID, or literal text that you
	 *            want to check is (or is not) shown. If you want the entire
	 *            matching item in a list, use an empty string.
	 * 
	 * @param isShown
	 *            If true, verify the requested item IS visible; if false,
	 *            verify it is NOT visible.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 * 
	 *         NOTE: Used by public verifyIsShown() and verifyIsNotShown()
	 *         methods.
	 */
	private boolean verifyIsShown(String listName, Object itemMatching, String elementNameOrMessage, boolean isShown, boolean wait){

		/*
		 * The two booleans returnValue and isNotShown start true so that our
		 * multiple-window lookup works correctly for both isShown==true and
		 * isShown==false.
		 */

		String errorMessage = "";
		String elementLocator = "";
		boolean returnValue = true;
		String expectedText = "";

		/*
		 * Determine whether we are checking visiblity of an element or a
		 * message/literal text.
		 */
		if( ! elementNameOrMessage.isEmpty()){
			log("Looking for '" + elementNameOrMessage + "'.");
			elementLocator = getElementLocator(elementNameOrMessage);
		} else{
			elementLocator = getElementLocator(listName);
			log("Looking for '" + listName + "'.");
		}

		if( ! StringUtils.isEmpty(elementLocator)){

			if(isShown && wait){
				returnValue = waitForElement(listName, itemMatching, elementNameOrMessage);
			} else if( ! isShown && wait){
				returnValue = ! waitForElementNotShown(listName, itemMatching, elementNameOrMessage);
			}
			// is not shown
			else{
				waitByTimeout(1000);
				returnValue = isElementShown(listName, itemMatching, elementNameOrMessage, "");
			}
			expectedText = elementLocator;

		}

		/*
		 * Otherwise, check for a message or literal text in any open window.
		 * Iterate through all open windows, looking for a match. First does a
		 * check to see whether we are using a message ID; if not, use the
		 * literal text.
		 */

		else{
			expectedText = getLocalizedText(elementNameOrMessage.isEmpty()?listName:elementNameOrMessage);
			returnValue = isTextShown(expectedText, isShown, wait);
		}

		/*
		 * Build the error messages for verifyIsTrue().
		 */
		elementNameOrMessage = elementNameOrMessage.isEmpty()?listName:elementNameOrMessage;
		if(isShown){

			if(elementNameOrMessage.isEmpty())
				errorMessage = "FAIL verifyIsShown() - Could not find '" + expectedText + "' ";
			else
				errorMessage = "FAIL verifyIsShown() - Could not find '" + elementNameOrMessage + "' ";
		} else{
			if(elementNameOrMessage.isEmpty())
				errorMessage = "FAIL verifyIsNotShown() - Found '" + expectedText + "' ";
			else
				errorMessage = "FAIL verifyIsNotShown() - Found '" + elementNameOrMessage + "' ";
			returnValue = ! returnValue;
		}

		errorMessage = errorMessage + "in current window.";

		return verifyIsTrue(returnValue, errorMessage);
	}

	/**
	 * Verify than an element, message or regex is NOT visible on the page.
	 * 
	 * NOTES:
	 * 
	 * 1. Supports verifying that a message or text is NOT shown in another (pop-up) window than the main application.
	 * However, elements can only be checked in the main application window.
	 * 
	 * 2. The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param elementName
	 *            The element name, literal text, or regex to be checked for
	 *            visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, returns false.
	 */
	public boolean verifyIsNotShown(String elementName){

		return verifyIsShown(elementName, "", "", false, true);
	}

	/**
	 * Verify than an element, message or regex is NOT visible on the page WITHOUT waiting.
	 * 
	 * NOTES:
	 * 
	 * 1. Supports verifying that a message or text is NOT shown in another (pop-up) window than the main application.
	 * However, elements can only be checked in the main application window.
	 * 
	 * 2. The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param elementName
	 *            The element name, literal text, or regex to be checked for
	 *            visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, returns false.
	 */
	public boolean verifyIsNotShowNoWait(String elementName){

		return verifyIsShown(elementName, "", "", false, false);
	}

	/**
	 * Verify than a list item matching a specified string or regex is NOT
	 * visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShown(String listName, String itemMatching){

		return verifyIsShown(listName, itemMatching, "", false, true);
	}

	/**
	 * Verify than a list item (match by index) is NOT visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShown(String listName, int itemMatching){

		return verifyIsShown(listName, itemMatching, "", false, true);
	}

	/**
	 * Verify than a list item matching a specified string or regex is NOT
	 * visible on the page WITHOUT waiting.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShowNoWait(String listName, String itemMatching){

		return verifyIsShown(listName, itemMatching, "", false, false);
	}

	/**
	 * Verify than a list item (match by index) is NOT visible on the page WITHOUT waiting.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShowNoWait(String listName, int itemMatching){

		return verifyIsShown(listName, itemMatching, "", false, false);
	}

	/**
	 * Verify than an element within a list item matching a specified string or
	 * regex is NOT visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShown(String listName, String itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, false, true);
	}

	/**
	 * Verify than an element within a list item (match by index) is NOT visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShown(String listName, int itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, false, true);
	}

	/**
	 * Verify than an element within a list item matching a specified string or
	 * regex is NOT visible on the page WITHOUT waiting.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShowNoWait(String listName, String itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, false, false);
	}

	/**
	 * Verify than an element within a list item (match by index) is NOT visible on the page WITHOUT waiting.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item is NOT visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShowNoWait(String listName, int itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, false, false);
	}

	/**
	 * Verify than an element, message or regex IS visible on the page.
	 * 
	 * NOTES:
	 * 
	 * 1. Supports verifying that a message or text is shown in another (pop-up) window than the main application.
	 * However, elements can only be checked in the main application window.
	 * 
	 * 2. The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param elementNameOrMessage
	 *            The element name, literal text, or regex to be checked for
	 *            visibility on the page.
	 * 
	 * @return true if the item IS visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String elementNameOrMessage){

		return verifyIsShown(elementNameOrMessage, "", "", true, true);
	}

	/**
	 * Verify than a list item matching a specified string or regex IS visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item IS visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String listName, String itemMatching){

		return verifyIsShown(listName, itemMatching, "", true, true);
	}

	/**
	 * Verify than a list item (match by index) IS visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @return true if the item IS visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String listName, int itemMatching){

		return verifyIsShown(listName, itemMatching, "", true, true);
	}

	/**
	 * Verify than an element within a list item matching a specified string or
	 * regex IS visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item IS visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String listName, String itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, true, true);
	}

	/**
	 * Verify than an element within a list item (match by index) IS visible on the page.
	 * 
	 * NOTES: The private method verifyIsShown() does all the heavy lifting.
	 * 
	 * @param listName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            The string or regex used to locate the matching list item.
	 * 
	 * @param elementName
	 *            The element name to be checked for visibility on the page.
	 * 
	 * @return true if the item IS visible; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String listName, int itemMatching, String elementName){

		return verifyIsShown(listName, itemMatching, elementName, true, true);
	}

	/**
	 * Verifies that the value of the specified element matches a literal string or regex.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the element
	 *            value to match.
	 * 
	 * @return true if the element exists and the value matches the
	 *         expected value; otherwise, logs a failure and returns false.
	 */
	public boolean verifyValueOf(String elementName, String expectedValue){

		return verifyValueOf(elementName, "", "", "", expectedValue);
	}

	/**
	 * Verifies that the value of a list item (match by regex or string) matches a
	 * literal string or regex.
	 * 
	 * @param listName
	 *            The list containing the element to be checked.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate the item in the list.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the list item to match.
	 * 
	 * @return true if the list item exists and the attribute value matches
	 *         the expected value; otherwise, logs a failure and returns false.
	 */
	public boolean verifyValueOf(String listOrElementName, String itemMatchingOrAttribute, String expectedValue){

		String attributeNames = "class id name style title type";

		/*
		 * If itemMatchingOrAttribute looks like an attribute, then check the element's attribute
		 * value. Otherwise, check the value of a matching list item.
		 */

		if(StringUtils.containsIgnoreCase(attributeNames, itemMatchingOrAttribute))
			return verifyValueOf("", "", listOrElementName, itemMatchingOrAttribute, expectedValue);
		else
			return verifyValueOf(listOrElementName, itemMatchingOrAttribute, "", "", expectedValue);
	}

	/**
	 * Verifies that the value of a list item (match by index) matches a
	 * literal string or regex.
	 * 
	 * @param listName
	 *            The list containing the element to be checked.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate the item in the list.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the list item to match.
	 * 
	 * @return true if the list item exists and the attribute value matches
	 *         the expected value; otherwise, logs a failure and returns false.
	 */
	public boolean verifyValueOf(String listName, int itemMatching, String expectedValue){

		return verifyValueOf(listName, itemMatching, "", expectedValue);
	}

	/**
	 * Verifies that the value of the specified element in a list item (match by string or regex) matches a
	 * literal or regex expression.
	 * 
	 * @param listName
	 *            The list containing the element to be checked.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate the item in the list
	 *            than contains the element.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the element
	 *            text or attribute to match.
	 * 
	 * @return true if the element exists and the value matches the
	 *         expected value; otherwise, logs a failure and returns false.
	 */
	public boolean verifyValueOf(String listName, String itemMatching, String elementName, String expectedValue){

		return verifyValueOf(listName, itemMatching, elementName, "", expectedValue);
	}

	/**
	 * Verifies that the value of the specified element in a list item (match by index) matches a
	 * literal or regex expression.
	 * 
	 * @param listName
	 *            The list containing the element to be checked.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate the item in the list
	 *            than contains the element.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the element
	 *            text or attribute to match.
	 * 
	 * @return true if the element exists and the value matches the
	 *         expected value; otherwise, logs a failure and returns false.
	 */
	public boolean verifyValueOf(String listName, int itemMatching, String elementName, String expectedValue){

		return verifyValueOf(listName, itemMatching, elementName, "", expectedValue);
	}

	/**
	 * Verifies that the value of the attribute of a specified element in a list
	 * item matches a literal or regex expression.
	 * 
	 * @param listName
	 *            The list containing the element to be checked.
	 * 
	 * @param itemMatching
	 *            An index, message id or regex to locate the item in the list
	 *            than contains the element.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param attribute
	 *            The name of the attribute of the element that you want to
	 *            check.
	 * 
	 * @param expectedValue
	 *            A message ID, literal string, or regex you expect the element
	 *            text or attribute to match.
	 * 
	 * @return true if the element exists and the value matches the
	 *         expected value; otherwise, logs a failure and returns false.
	 */
	private boolean verifyValueOf(String listName, Object itemMatching, String elementName, String attribute, String expectedValue){

		boolean returnValue = false;

		String actualValue = getValueOf(listName, itemMatching, elementName, attribute, "");

		ArrayList<String> positiveValues = new ArrayList<String>();
		ArrayList<String> negativeValues = new ArrayList<String>();

		/*
		 * If the expectedValue is represented in the contents file, get the
		 * actual contents. Then, check whether the actual value matches the
		 * expected value via contains or regex match.
		 */

		expectedValue = getLocalizedText(expectedValue);

		/*
		 * First check if we're expecting a "boolean" string value; the
		 * following are equivalent:
		 * positiveValues : y = yes = true = on = checked negativeValues : n =
		 * no = false = off = unchecked
		 * NOTE: We can't do a simple StringUtils.containsIgnoreCase() to
		 * compare expected and actual values, because the because the
		 * positiveValues string would match a negative value ("n") and the
		 * negativeValues string would match a positive value ("checked"),
		 * resulting in potential false positive results.
		 * Therefore, we do a case-insensitive comparison of each exact string
		 * in the ArrayList.
		 * If not checking a "boolean" string value, try a simple contains; if
		 * that doesn't match, try a regex match.
		 */

		positiveValues.addAll(Arrays.asList("y", "yes", "true", "on", "checked"));
		negativeValues.addAll(Arrays.asList("n", "no", "false", "off", "unchecked"));

		if((positiveValues.contains(expectedValue.toLowerCase()) && positiveValues.contains(actualValue.toLowerCase()))
				|| (negativeValues.contains(expectedValue.toLowerCase()) && negativeValues.contains(actualValue.toLowerCase()))
				|| actualValue.contains(expectedValue) || actualValue.matches(expectedValue))
			returnValue = true;

		/*
		 * Build an informative error message based on the arguments passed to
		 * verifyValueOf(). Error will be logged by verifyIsTrue() if
		 * returnValue is false.
		 */

		String errorMessage = "FAIL verifyValueOf() - ";

		if(StringUtils.isEmpty(listName)){

			if(StringUtils.isEmpty(attribute))
				errorMessage = errorMessage + "Value of element " + elementName;

			else
				errorMessage = errorMessage + "Attribute '" + attribute + "' of element " + elementName;
		} else{

			if(StringUtils.isEmpty(elementName))
				errorMessage = errorMessage + "Value of item matching '" + itemMatching + "' in list named '" + listName;
			else
				errorMessage = errorMessage + "Value of element '" + elementName + "' in item matching '" + itemMatching + "' in list named '" + listName + "'";
		}

		errorMessage = errorMessage + " did not match:  expected '" + expectedValue + "', but found '" + actualValue + "'.";
		/*
		 * If verify value failed, then take a screen capture.
		 */
		if( ! returnValue)
			takeFullScreenShot(elementName);

		return verifyIsTrue(returnValue, errorMessage);
	}

	/**
	 * Verify the current view.
	 * 
	 * Verifies the current view by checking the uiMap current view name and also by verifying the default area is ready. If the expectedView matches a page
	 * name, also checks the current page name and URL. If the view has a specified path element, it will also check for that in the current URL. In the case of
	 * both the page and view path value, we search for contains or regex match. Finding a matching page OR view is sufficient.
	 * 
	 * @param expectedView
	 *            The expected view name (just the Page OR the View, not Page:View).
	 * 
	 * @return true if the current Page and/or View matches expectedView; otherwise, logs a failure and returns false.
	 */

	public boolean verifyViewIs(String expectedView){

		boolean verifyPage = false;
		/*
		 * Don't fail if we don't find a matching
		 * page but do find a matching view.
		 */
		boolean verifyView = false;
		/*
		 * DO FAIL if we find neither a matching
		 * page NOR a matching view.
		 */

		String errorMessage = "";

		/*
		 * If the expectedView matches the name of a page in the application,
		 * then check that the currentPage name and URL matches the name and URL
		 * (if any) of the expectedView.
		 */

		for(JsonNode currentPage:uiMap.path("application").path("pages")){

			if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), expectedView)){

				String actualPage = StringUtils.defaultString(uiMapCurrentPage.path("properties").path("pageName").textValue());
				String actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());
				String expectedPageUrl = StringUtils.defaultString(currentPage.path("properties").path("path").textValue());

				if( ! StringUtils.isEmpty(expectedPageUrl))
					verifyPage = (StringUtils.containsIgnoreCase(actualPageUrl, expectedPageUrl) || actualPageUrl.matches(expectedPageUrl));

				if(verifyPage)
					verifyView = true; // Not required to find a matching view
				else
					errorMessage = "FAIL verifyViewIs() - expected Page='" + expectedView + "' '" + expectedPageUrl + "', but actual Page='" + actualPage
							+ "' '" + actualPageUrl + "'.";

				break;
			}
		}

		/*
		 * If the expectedView matches a view name in the current page, make
		 * sure the current view's name and URL (path) match the expected
		 * values. We only check if we haven't already found a matching page.
		 */

		if( ! verifyView){

			for(JsonNode currentView:uiMapCurrentPage.path("views")){

				if(StringUtils.equalsIgnoreCase(currentView.path("viewName").textValue(), expectedView)){

					String actualView = StringUtils.defaultString(uiMapCurrentView.path("viewName").textValue());
					String actualViewUrl = StringUtils.defaultString(driver.getCurrentUrl());
					String expectedViewUrl = StringUtils.defaultString(currentView.path("path").textValue());

					verifyView = (StringUtils.containsIgnoreCase(actualView, expectedView) || actualView.matches(expectedView));

					/*
					 * If we believe we're on the proper view, check if the
					 * current URL matches the expected view URL (if any), and
					 * make sure the defaultArea's locator is ready.
					 */

					if(verifyView){

						if( ! StringUtils.isEmpty(expectedViewUrl))
							verifyView = (StringUtils.containsIgnoreCase(actualViewUrl, expectedViewUrl) || actualViewUrl.matches(expectedViewUrl));

						if(verifyView)
							verifyView = waitForArea(
									getJsonNodeMatching(uiMapCurrentPage.path("areas"), "areaName", uiMapCurrentView.path("defaultArea").textValue()).path(
											"locator").textValue(), Long.parseLong(pageTimeout));
					}

					if( ! verifyView)
						errorMessage = "FAIL verifyViewIs() - expected View='" + expectedView
								+ (StringUtils.isEmpty(expectedViewUrl)?"'":"' '" + expectedViewUrl + "'") + ", but actual View='" + actualView
								+ (StringUtils.isEmpty(expectedViewUrl)?"'.":"' '" + actualViewUrl + "'.");

					break;
				}
			}
		}

		if( ! verifyView)
			takeFullScreenShot(expectedView);

		/*
		 * Return true if we found either a matching Page, a matching View, or
		 * both.
		 */
		return verifyIsTrue(verifyPage || verifyView, errorMessage);
	}

	/**
	 * Verifying the URL or the current page matches the specified string or regex.
	 * 
	 * Verifies the URL matches expectedPageURL (literal string or regex)
	 * 
	 * @param expectedPageURL
	 *            The expected URL.
	 * 
	 * @return true if the URL matches expected; otherwise, logs a failure and returns false.
	 */
	public boolean verifyURLIs(String expectedPageURL){

		expectedPageURL = getLocalizedText(expectedPageURL);
		boolean verifyURL = false; // DO FAIL if we don't find a matching URL.
		String errorMessage = "";
		Set<String> allWindowNames = driver.getWindowHandles();
		/*
		 * Build a list of open windows; will be at least one (our application
		 * window).
		 */

		Set<String> currentWindowNamesList = new HashSet<String>();

		if(allWindowNames.size() > 1){
			currentWindowNamesList = getCurrentValidWindowNamesListInAll(allWindowNames);

			for(String windowName:currentWindowNamesList){
				boolean mainApplicationWindow = StringUtils.equalsIgnoreCase(windowName, main_window);
				if( ! mainApplicationWindow){

					driver.switchTo().window(windowName);
					waitForCondition("(selenium.browserbot.getCurrentWindow().document.readyState=='interactive') || "
							+ "(selenium.browserbot.getCurrentWindow().document.readyState=='complete');", pageTimeout);
					String actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());

					if(StringUtils.equalsIgnoreCase(actualPageUrl, expectedPageURL) || actualPageUrl.matches(expectedPageURL)
							|| actualPageUrl.contains(expectedPageURL))
						verifyURL = true;
					else
						errorMessage = "FAIL verifyURLIs() - expected URL='" + expectedPageURL + ", but actual Page='" + actualPageUrl + ".";

					/*
					 * Close the current window and return to the main
					 * application window.
					 */

					driver.close();
					driver.switchTo().window(main_window);
				}
			}
		} else{
			String actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());

			if(StringUtils.equalsIgnoreCase(actualPageUrl, expectedPageURL) || actualPageUrl.matches(expectedPageURL)
					|| actualPageUrl.contains(expectedPageURL)){
				verifyURL = true;
			} else

				errorMessage = "FAIL verifyURLIs() - expected URL='" + expectedPageURL + ", but actual Page='" + actualPageUrl + ".";
		}

		/*
		 * Return true if we found a matching URL.
		 */

		if( ! verifyURL)
			takeFullScreenShot("verifyURLFailed");

		return verifyIsTrue(verifyURL, errorMessage);
	}

	/**
	 * Sets any specified defaults for Page, View, Area & Element that weren't
	 * specified. Any Page, View, Area or Element located in the UI Map is
	 * cached.
	 * 
	 * @param requestedView
	 *            -A complete or partial view for which defaults may need to be
	 *            added.
	 * @param updateUiMap
	 *            - Whether or not to update the uiMap.
	 * @return - The complete view including any defaults specified in uiMap.
	 */

	protected String uiMapGetDefaults(String requestedView, boolean updateUiMap){

		String[] viewArray = new String[4];

		Integer i = 0;
		for(String item:requestedView.split(UIMAP_DELIM, 4)){
			viewArray[i ++ ] = item;
		}

		// Get the current page properties

		viewArray = getUiMapWithCurrentView(viewArray);

		/*
		 * Update the uiMap, if specified. If the UI Map Page, View, Area or Element are blank (not
		 * specified, no default), then clear the old value of the associated UI Map object by
		 * replacing it with a blank one.
		 */

		if(updateUiMap){

			uiMapCurrentPage = (StringUtils.isEmpty(viewArray[0]))?jsonMapper.createObjectNode():myCurrentPage;
			uiMapCurrentView = (StringUtils.isEmpty(viewArray[1]))?jsonMapper.createObjectNode():myCurrentView;
			uiMapCurrentArea = (StringUtils.isEmpty(viewArray[2]))?jsonMapper.createObjectNode():myCurrentArea;
			uiMapCurrentElement = (StringUtils.isEmpty(viewArray[3]))?jsonMapper.createObjectNode():myCurrentElement;
		}

		return String.format("%1$s:%2$s:%3$s:%4$s", (Object[])viewArray);
	}

	/**
	 * By iterating over the json file and save the attributes to jsonNode
	 * object
	 * 
	 * @param viewArray
	 *            A collection of the String object
	 * @return The complete view including any defaults specified in uiMap
	 * */
	private String[] getUiMapWithCurrentView(String[] viewArray){

		// Build up any unspecified default values. "" is used if no default is
		// specified in uiMap.
		// Add default Page, if needed.
		if(StringUtils.isEmpty(viewArray[0])){

			viewArray[0] = StringUtils.defaultString(uiMap.path("application").path("properties").path("defaultPage").textValue());
		}
		// If we have a valid Page name; cache the JsonObject for it and
		// continue to search for
		// a default View, if needed.

		if( ! StringUtils.isEmpty(viewArray[0])){

			for(JsonNode currentPage:uiMap.path("application").path("pages")){

				if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), viewArray[0])){

					myCurrentPage = currentPage;
					break;
				}
			}

			// We have a Page; add a default View, if needed.

			if(StringUtils.isEmpty(viewArray[1])){

				viewArray[1] = StringUtils.defaultString(myCurrentPage.path("properties").path("defaultView").textValue());
			}

			// If we have a valid View name; cache the JsonObject for it and
			// continue to search for
			// a default Area, if needed.

			if( ! StringUtils.isEmpty(viewArray[1])){

				myCurrentView = getJsonNodeMatching(myCurrentPage.path("views"), "viewName", viewArray[1]);

				// We have a View; add a default Area, if needed.

				if(StringUtils.isEmpty(viewArray[2])){

					viewArray[2] = StringUtils.defaultString(myCurrentView.path("defaultArea").textValue());
				}

				// If we have a valid Area name; cache the JsonObject for it and
				// continue to search
				// for a default Element, if needed.

				if( ! StringUtils.isEmpty(viewArray[2])){

					myCurrentArea = getJsonNodeMatching(myCurrentPage.path("areas"), "areaName", viewArray[2]);

					// Add default Element, if any. If we have a valid Element
					// name, cache the
					// JsonObject for it.

					if(StringUtils.isEmpty(viewArray[3])){

						viewArray[3] = StringUtils.defaultString(myCurrentArea.path("defaultElement").textValue());

						if( ! StringUtils.isEmpty(viewArray[3])){

							myCurrentElement = getJsonNodeMatching(myCurrentArea.path("elements"), "elementName", viewArray[3]);
						}
					}
				}
			}
		}
		return viewArray;
	}

	/**
	 * Find a successful path to the requestedView by first searching for a link
	 * to it in our current view. If not found, traverse all links until a
	 * successful path is determined.
	 * 
	 * @param virtualCurrentView
	 *            Our current view (or virtual view when traversing virtually).
	 * 
	 * @param requestedView
	 *            The view we want to reach.
	 * 
	 * @param pathSoFar
	 *            Our path so far, when traversing virtually.
	 * 
	 * @return A list of locators that, if walked, will get us to our
	 *         requestedView (if succussful); otherwise, an empty ArrayList.
	 */
	private ArrayList<String> uiMapGetPathToRequestedView(String virtualCurrentView, String requestedView, ArrayList<String> pathSoFar, int viewIndex){

		if( ! (uiMapViewsAlreadyChecked.contains(StringUtils.defaultString(virtualCurrentView))))
			uiMapViewsAlreadyChecked.add(StringUtils.defaultString(virtualCurrentView));

		ArrayList<JsonNode> areasToCheck = new ArrayList<JsonNode>();
		ArrayList<String[]> otherViewsToCheck = new ArrayList<String[]>();

		JsonNode myCurrentPage = jsonMapper.createObjectNode();
		JsonNode myCurrentView = jsonMapper.createObjectNode();

		String[] viewArray = new String[2];

		int virtualViewIndex = viewIndex;

		boolean foundMatchingElement = false;
		boolean walkNoFurther = false;

		ArrayList<String> pathToRequestedView = new ArrayList<String>();
		pathToRequestedView.addAll(pathSoFar);

		/*
		 * Get the Page and View of the virtualCurrentView.
		 */

		int i = 0;
		for(String item:virtualCurrentView.split(UIMAP_DELIM, 2)){
			viewArray[i ++ ] = StringUtils.defaultString(item);
		}

		for(JsonNode currentPage:uiMap.path("application").path("pages")){

			if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), viewArray[0])){

				myCurrentPage = currentPage;
				break;
			}
		}

		myCurrentView = getJsonNodeMatching(myCurrentPage.path("views"), "viewName", viewArray[1]);

		// areasToCheck.add(myCurrentView);

		/*
		 * Generate a list of all target views specified in views.
		 * e.g. swipe element in view, led element in view.
		 */
		if(myCurrentView.has("gestures")){
			String directions[] = myCurrentView.path("gestures").toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "")
					.replace("\"", "").split(",");
			String dir;
			String targetView;
			for(String direction:directions){
				dir = direction.split(":")[0].trim();
				targetView = direction.substring(dir.length() + 1).trim();
				if(StringUtils.equalsIgnoreCase(targetView, requestedView)){
					pathToRequestedView.add("gestures:" + dir);
					foundMatchingElement = true;
					break;
				} else{
					if( ! (uiMapViewsAlreadyChecked.contains(StringUtils.defaultString(targetView)))){
						String[] directionAndView = {"gestures:" + dir,targetView};
						otherViewsToCheck.add(directionAndView);
					}
				}
			}
		}
		/*
		 * Build up a list of all areas in the virtualCurrentView that haven't
		 * already been checked, starting with the default area.
		 */
		if( ! foundMatchingElement){
			if( ! (uiMapAreasAlreadyChecked.contains(StringUtils.defaultString(myCurrentView.path("defaultArea").textValue()))))
				areasToCheck.add(getJsonNodeMatching(myCurrentPage.path("areas"), "areaName", myCurrentView.path("defaultArea").textValue()));

			for(JsonNode currentArea:myCurrentView.path("activeAreas")){

				JsonNode currentAreaNode = getJsonNodeMatching(myCurrentPage.path("areas"), "areaName", StringUtils.defaultString(currentArea.textValue()));

				if( ! uiMapAreasAlreadyChecked.contains(StringUtils.defaultString(currentArea.textValue())) && ! areasToCheck.contains(currentAreaNode))
					areasToCheck.add(currentAreaNode);
			}

			/*
			 * For each area to be checked, search all elements for one with a link
			 * to the requestedArea.
			 */

			for(JsonNode currentArea:areasToCheck){

				String areaName = StringUtils.defaultString(currentArea.path("areaName").textValue());

				for(JsonNode currentElement:currentArea.path("elements")){

					String elementName = StringUtils.defaultString(currentElement.path("elementName").textValue());

					String elementView = StringUtils.defaultIfEmpty(currentElement.path("view").textValue(),
							StringUtils.defaultString(currentElement.path("view").toString()));

					String dontNavigate = StringUtils.defaultString(currentElement.path("dontNavigate").textValue());

					// Build elementName as areaName:elementName if areaName isn't blank.

					if( ! StringUtils.isBlank(areaName))
						elementName = (areaName + UIMAP_DELIM + elementName);

					/*
					 * If the current element has multiple views, choose the view
					 * which equals requestedView.
					 */
					if(StringUtils.contains(elementView, "\",\"")){

						String[] elementViewArray = StringUtils.split(elementView, "[\",]");

						for(String currentView:elementViewArray){

							if(StringUtils.equalsIgnoreCase(currentView, requestedView)){
								elementView = currentView;
							}
						}
					}

					/*
					 * If the current element has a defined view attribute, decide
					 * whether or not to traverse it. If it does not have a view
					 * element, try the next element in the currentArea.
					 */

					if( ! elementView.isEmpty()){

						walkNoFurther = false;

						/*
						 * SUCCESS - If we found an element that will take us to the
						 * requestedArea, add its areaName:elementName to the pathToRequestedView,
						 * set foundMatchingElement=true, and exit.
						 */

						if(StringUtils.equalsIgnoreCase(elementView, requestedView)){

							pathToRequestedView.add(elementName);
							foundMatchingElement = true;
							break;
						} else{

							/*
							 * Terminate walking the path if we have already tried
							 * this element or the view that it points to in order
							 * to avoid infinite recursion.
							 * Modal dialogs will have at least one element that
							 * returns to PREVIOUS_VIEW. Normally, we would consider
							 * a dialog the end of the traversal path and
							 * walkNoFurther; however, there is one important
							 * exception:
							 * If we are BEGINNING our walk from an open dialog, we
							 * have to dismiss it in order to look in other areas
							 * (like the Navigation Bar). We do so in a
							 * non-destructive manner by making sure we don't click
							 * on an element which requires field validation. In
							 * layman's terms - we click on Cancel, rather than OK,
							 * Delete, Remove, etc.
							 */

							walkNoFurther = (pathSoFar.contains(elementName) || StringUtils.equalsIgnoreCase(elementView, virtualCurrentView));

							if( ! walkNoFurther){

								/*
								 * If we are starting our walk from an open dialog,
								 * click on the proper element (i.e., does NOT have
								 * a validation attribute = 'true') to dismiss it
								 * and indicate the current real previous view (from
								 * uiMapViewList) as its destination.
								 * We maintain a virtualViewIndex to handle the case
								 * where we have nested dialogs and need to exit
								 * them and properly locate the N-th previous view.
								 * So we'll check either that we're starting our
								 * search from an open dialog
								 * (pathToRequestedView.size() == 0) or that we're
								 * still traversing backwards (virtualViewIndex <
								 * uiMapViewIndex).
								 */

								if(StringUtils.equalsIgnoreCase(elementView, PREVIOUS_VIEW)){

									if( ! StringUtils.equalsIgnoreCase(dontNavigate, "true")
											&& ((pathToRequestedView.size() == 0) || (virtualViewIndex < uiMapViewIndex)) && (virtualViewIndex > 0)){
										elementView = uiMapViewList.get( -- virtualViewIndex);
									} else{
										walkNoFurther = true;

									}
								}

								/*
								 * If the afore-mentioned criteria are met, go ahead
								 * and add the element's view to traverse later.
								 */

								if( ! walkNoFurther){

									String[] elementNameAndView = {elementName,elementView};
									otherViewsToCheck.add(elementNameAndView);
								}
							}
						}
					}
				}

				if(foundMatchingElement)
					break;

				/*
				 * If we didn't find a matching element in this area, add it to
				 * areasAlreadyChecked so we don't needlessly search it again in
				 * recursive calls to uiMapGetPathToRequestedView().
				 * Then, try the next area in the current view.
				 */

				uiMapAreasAlreadyChecked.add(currentArea.path("areaName").textValue());
			}
		}

		/*
		 * If we did not find a matching area in our current view, but found
		 * other views to check via elements with links in the current view,
		 * then traverse them recursively.
		 */

		if( ! (foundMatchingElement || otherViewsToCheck.isEmpty())){

			ArrayList<String> possiblePathToRequestedView = new ArrayList<String>();
			ArrayList<ArrayList<String>> successfulPaths = new ArrayList<ArrayList<String>>();

			for(String[] elementNameAndView:otherViewsToCheck){

				ArrayList<String> pathToTry = new ArrayList<String>();

				String elementName = elementNameAndView[0];
				String elementView = elementNameAndView[1];

				pathToTry.addAll(pathSoFar);
				pathToTry.add(elementName);

				/*
				 * If our currentElement didn't locate the requestedView, but it
				 * does link to a view, recursively traverse that view and any
				 * additional links, looking for a path to the matching element.
				 * If we find a matching path, save it and continue traversing
				 * the remaining views. We keep searching so we aren't stuck
				 * with the first matching path, which may be convoluted. Once
				 * we've found one or more matching paths, we will choose the
				 * shortest one.
				 */

				possiblePathToRequestedView = uiMapGetPathToRequestedView(elementView, requestedView, pathToTry, virtualViewIndex);
				foundMatchingElement = ( ! possiblePathToRequestedView.equals(pathSoFar));

				if(foundMatchingElement)
					successfulPaths.add(possiblePathToRequestedView);
			}

			/*
			 * Once all links have been traversed, determine the shortest
			 * successful path.
			 */

			if( ! successfulPaths.isEmpty()){

				int indexOfShortestPath = - 1;

				for(int index = 0; (index < successfulPaths.size()); index ++ ){

					if((indexOfShortestPath == - 1) || (successfulPaths.get(index).size() < successfulPaths.get(indexOfShortestPath).size()))
						indexOfShortestPath = index;
				}

				/*
				 * In locating a successful path, set foundMatchingElement and
				 * clear walkNoFurther.
				 */

				pathToRequestedView = successfulPaths.get(indexOfShortestPath);
				foundMatchingElement = true;
				walkNoFurther = false;
			}

			/*
			 * TODO: Cache successful paths from virtualCurrentView =>
			 * requestedView so we can re-use them later.
			 */
		}

		/*
		 * If after searching all links in all areas, we didn't find a
		 * successful path to the requestedView then pop the last locator off
		 * the path in order to have recursive calls walk back and try another
		 * path.
		 */

		if(walkNoFurther || ! foundMatchingElement){

			if( ! pathToRequestedView.isEmpty())
				pathToRequestedView.remove(pathToRequestedView.size() - 1);
		}

		return pathToRequestedView;
	}

	private ArrayList<String> splitrequestedViewToCorrectFormat(boolean loadInitialPage, String requestedView){

		ArrayList<String> viewArray = new ArrayList<String>();
		/*
		 * Split our requestedView into [Page, View, Area, Element]. If the
		 * uiMap is not yet defined (uiMapViewIndex < 0), define it.
		 */
		String requestedViewWithDefaults = uiMapGetDefaults(requestedView, loadInitialPage);

		for(String item:requestedViewWithDefaults.split(UIMAP_DELIM, 4)){
			viewArray.add(item);
		}

		return viewArray;
	}

	/*
	 * Get path to request view, If we found a successful
	 * path to the requestedView, walk it by clicking the links in the
	 * path,return loadNewPage=false. If we didn't find a way to walk there, but
	 * we are loading a new or the initial page, then do the page load, return
	 * loadNewPage = true.
	 */
	private boolean getToRequestedViewWay(boolean loadInitialPage, String requestedView){

		boolean returnValue = false;
		ArrayList<String> pathToRequestedView = new ArrayList<String>();
		String currentView = String.format("%1$s:%2$s", uiMapCurrentPage.path("properties").path("pageName").textValue(), uiMapCurrentView.path("viewName")
				.textValue());
		/*
		 * If we found a successful path to the requestedView, walk it by
		 * clicking the links in the path. If we didn't find a way to walk
		 * there, but we are loading a new or the initial page, then do the page
		 * load. In both cases, update the uiMap objects with the current
		 * Page.View.Area.Element and return true.
		 */

		if( ! loadInitialPage)
			pathToRequestedView = uiMapGetPathToRequestedView(currentView, requestedView, pathToRequestedView, uiMapViewIndex);

		if( ! pathToRequestedView.isEmpty()){

			log("Using setViewTo() to navigate to '" + requestedView + "'.");

			uiMapWalkToRequestedView(pathToRequestedView);
			returnValue = true;
		}
		return returnValue;
	}

	/**
	 * Convenience method for waiting for request view to load.
	 * 
	 * @param currentPage
	 *            our current page.
	 */
	private void waitForRequestViewToLoad(JsonNode currentPage){

		if(getAppType().equals("WebApp")){
			String uimapPath = currentPage.path("properties").path("path").textValue();
			if(uimapPath.contains("://")){
				driver.get(uimapPath);
				log("Navigated to path:" + uimapPath);
			} else{
				String appPath[] = uimapPath.split("/", 2);
				String actualPath = "";
				String domain = getAppUrl();
				if(appPath.length > 1){
					actualPath = appPath[1];
					if(appPath[0].contains("<") && appPath[0].contains(">"))
						domain = getProperty("application." + appPath[0].replace("<", "").replace(">", ""));

				} else{
					if(uimapPath.contains("<") && uimapPath.contains(">"))
						domain = getProperty("application." + uimapPath.replace("<", "").replace(">", ""));
					if(domain.isEmpty())
						domain = getAppUrl();
				}
				driver.get(domain + actualPath);
				log("Navigated to path:" + domain + actualPath);
			}

		}
	}

	/**
	 * Convenience method for waiting for request area to load.
	 * 
	 */
	protected void waitForArea(){

		String locator = StringUtils.defaultString(uiMapCurrentArea.path("locator").textValue());

		if( ! (locator.isEmpty() || waitForArea(locator, Long.parseLong(pageTimeout) / 1000))){

			String message = "Area Load TimeOut:" + uiMapCurrentPage.path("pageName").textValue() + "_" + uiMapCurrentView.path("viewName").textValue() + "_"
					+ uiMapCurrentArea.path("areaName").textValue();
			log(message, 2);
			takeFullScreenShot(message);
			Assert.assertTrue(false, message);
		}
	}

	protected boolean waitForArea(String areaLocator, Long timeoutInSec){

		return waitForArea(areaLocator, timeoutInSec, false);
	}

	/**
	 * Wait for current Area to load by checking the presence of area locator.
	 * 
	 * @param areaLocator
	 *            our current area Locator.
	 * 
	 * @param timeout
	 *            The limit time of load area.
	 * @return
	 *         true wait for aread to load success.
	 */
	protected boolean waitForArea(String areaLocator, Long timeoutInSec, boolean quiet){

		if(timeoutInSec == 0)
			timeoutInSec = Long.parseLong(viewTimeout) / 1000;
		final String locator = areaLocator;
		if(locator.isEmpty()){
			if( ! blankAreaLocator){
				log("AreaLocator is empty, skip area checking.", 3);
				blankAreaLocator = true;
			}
			return true;
		}

		WebDriverWait driverWait = (WebDriverWait)new WebDriverWait(driver, timeoutInSec, 500).ignoring(StaleElementReferenceException.class).withMessage(
				"waitForAreaToLoad() timed out after " + timeoutInSec + " milliseconds.");
		try{

			driverWait.until(new ExpectedCondition<Boolean>(){

				public Boolean apply(WebDriver driver){

					return getElements("", locator, "", "").size() > 0;
				}

			});

		} catch(TimeoutException e){
			if( ! quiet)
				log("Area is not found!");
			return false;
		}
		return true;
	}

	/*
	 * Convenience method for loading new page.
	 */
	private boolean loadNewPage(boolean loadNewPage, ArrayList<String> viewArray){

		/*
		 * Open an initial or new page, and wait for it to load.
		 */
		boolean returnValue = false;
		if(loadNewPage){

			for(JsonNode currentPage:uiMap.path("application").path("pages")){

				if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), viewArray.get(0))){

					long startTime = System.currentTimeMillis();

					waitForRequestViewToLoad(currentPage);

					// preConfigInternetExplorer("IE8");

					/*
					 * Update the UI Map with the requested view.
					 */

					returnValue = uiMapUpdateView(String.format("%1$s:%2$s", viewArray.get(0), viewArray.get(1)), true);

					/*
					 * If we opened a new page but the default area is not yet
					 * visible, then wait for the page to load.
					 */
					waitForArea();
					// waitForDefaultAreaToLoad();
					// waitForElementReady(uiMapCurrentArea.path("locator").textValue(), Integer.parseInt(pageTimeout));

					measureResponseTime(startTime, viewArray);

					break;
				}
			}
		}

		return returnValue;
	}

	/*
	 * Convenience method for measuring response time.
	 */
	private void measureResponseTime(long startTime, ArrayList<String> viewArray){

		float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

		if(measureResponseTime){

			String viewType = "page";
			System.out.printf(CommonTools.getCurrentTime() + " INFO - <UI_RESPONSE_TIME> - Opened " + viewType + " (" + viewArray.get(0)
					+ ") in %.3f seconds.\n", elapsedTime);

			Assert.assertTrue(elapsedTime < maxPageTime);
		}

	}

	/**
	 * View the specified view, or the default if requestedView is blank.
	 * 
	 * Uses the UI Map to determine the default page.view.area.element, then walks through the uiMap to determine how to get to the requested view by following
	 * links or buttons which change view in a recursive fashion.
	 * 
	 * Here is an overview of the process:
	 * 
	 * - Check whether we're already there; if so, just return true
	 * - Check if we can get there directly from any area in the current view
	 * - If not, recursively walk the links in the UI Map until we find a successful path to the requestedView
	 * - Keep track of areas already checked, so we don't needlessly check them multiple times
	 * - If no successful path is found, return false *
	 * - If a successful path was found, click through it until the requested view's default area locator is ready
	 * - Update all uiMapCurrent* objects, then return true
	 * 
	 * NOTES
	 * 
	 * - uiMap elements that change the view (usually buttons or links) have a setView attribute
	 * - Buttons that exit modal dialogs have setView=previousView and use the UI Map to determine the proper view to return to.
	 * 
	 * @param requestedView
	 *            The view to view. Can leave blank to let view determine the
	 *            default from uiMap or use the following syntax:
	 *            [Page[:View[:Area[:Element]]]], where these represent:
	 * 
	 *            Page - A web page that requires a page load from the server. A Page has 1 or more Views.
	 * 
	 *            View - A specific view within the Page (like Print History. Each View has 1 or more Areas.
	 * 
	 *            Area - A logical area (div, iframe) in the View that contains 1 or more Elements.
	 * 
	 *            Element - A DOM element like text, input, button, table, etc. that might have focus initially.
	 */
	protected boolean uiMapSetView(String requestedView){

		ArrayList<String> viewArray = new ArrayList<String>();
		boolean loadNewPage = true;
		boolean loadInitialPage = (uiMapViewIndex < 0);
		boolean returnValue = false;

		// If only a view is specified (e.g., no ":", then append uiMapCurrentPage: to the beginning (if it is not blank).

		String currentPageName = uiMapCurrentPage.path("properties").path("pageName").textValue();

		if( ! StringUtils.contains(requestedView, UIMAP_DELIM) && ! StringUtils.isBlank(currentPageName))
			requestedView = (currentPageName + UIMAP_DELIM + requestedView);

		/*
		 * Split our requestedView into [Page, View, Area, Element]. If the
		 * uiMap is not yet defined (uiMapViewIndex < 0), define it.
		 */
		viewArray = splitrequestedViewToCorrectFormat(loadInitialPage, requestedView);

		/*
		 * If we're already in the requested Page:View (according to the UI
		 * Map), do nothing but return true. If we're on the requested page (but
		 * a different view), don't do a page load.
		 */

		if( ! loadInitialPage && StringUtils.equalsIgnoreCase(uiMapCurrentPage.path("properties").path("pageName").textValue(), viewArray.get(0))){

			if(StringUtils.equalsIgnoreCase(uiMapCurrentView.path("viewName").textValue(), viewArray.get(1)))
				return true;

			loadNewPage = false; // On the correct Page, but requested a different View.
		}

		/*
		 * Starting a new search for a successful path to the requestedView, so
		 * clear out areasAlreadyChecked.
		 */
		uiMapAreasAlreadyChecked.clear();
		uiMapViewsAlreadyChecked.clear();

		/*
		 * If we found a successful path to the requestedView, walk it by
		 * clicking the links in the path. If we didn't find a way to walk
		 * there, but we are loading a new or the initial page, then do the page
		 * load. In both cases, update the uiMap objects with the current
		 * Page.View.Area.Element and return true.
		 */
		returnValue = getToRequestedViewWay(loadInitialPage, requestedView);

		/*
		 * Open an initial or new page, and wait for it to load.
		 */

		if(( ! returnValue) && loadNewPage)

			returnValue = loadNewPage(loadNewPage, viewArray);

		/*
		 * TODO: DCC - Need to add logic to update the uiMapViewList & Index
		 * when transition between signed-in=>signed-out states, and
		 * conversely. back(), forward() and openBookmark() need to
		 * understand how to deal with these transitions, which should be
		 * represented in ui.json.
		 */

		/*
		 * Wait for area to load and log success or failure.
		 */

		waitForArea();
		String defaultView = uiMapViewList.get(uiMapViewIndex);

		if(returnValue)
			log("Successfully set view to '" + StringUtils.defaultIfBlank(requestedView, defaultView) + "'.");

		return returnValue;
	}

	/**
	 * Convenience method for updating the UI Map objects with the current view. Single-arg wrapper for
	 * uiMapUpdateView() that always adds a new view and deletes any forward
	 * bread crumbs.
	 * 
	 * Parameters:
	 * 
	 * @param view
	 *            -The view which you want switch to .
	 * @return true if view was updated successfully.
	 */
	public boolean uiMapUpdateView(String view){

		return uiMapUpdateView(view, true);
	}

	/**
	 * Update the UI Map objects with the current view.
	 * 
	 * @param view
	 *            - The current view being set.
	 * 
	 *            NOTE: This can be a comma-delimited string of multiple views -
	 *            some clickOn elements can take one to more than one view,
	 *            depending upon circumstances. For instances, adding an App
	 *            takes one to different views dependin gupon the number of
	 *            devices installed of the type of app. At this point, we've
	 *            already clicked on the element, so we check to see what view
	 *            is visible if more than one is specified.
	 * 
	 * @return - true if successful.
	 * 
	 *         NOTE: Going to a new view removes any forward breadcrumbs so
	 *         goBack & goForward work correctly.
	 */
	private boolean uiMapUpdateView(String view, boolean addNewView){

		boolean returnValue = true;
		boolean updateUiMap = true;

		if(StringUtils.containsIgnoreCase(view, PREVIOUS_VIEW)){

			if(uiMapViewIndex > 0){
				uiMapViewIndex -- ;
				view = uiMapViewList.get(uiMapViewIndex);
				log("Setting view to PREVIOUS VIEW: " + view);
			} else
				updateUiMap = false;
		} else{

			/*
			 * Check whether the clicked-on element could take us to more than
			 * one view, and if so, (because we've already clicked by this
			 * point), see which one is visible. That's the view we want to
			 * update the UI Map with.
			 * We check the Page and View to determine the right default area
			 * locator.
			 */

			if(StringUtils.contains(view, ",")){
				view = getCurrentAreaisDisplayed(view, 0);

			}

			/*
			 * Add the requested view and increment uiViewIndex unless we are
			 * already on the view.
			 */

			if(addNewView && ((uiMapViewIndex < 0) || ! StringUtils.equalsIgnoreCase(uiMapViewList.get(uiMapViewIndex), view))){

				/*
				 * Setting a view will remove any forward history breadcrumbs
				 * from uiMapViewList.
				 * NOTE: uiMapViewIndex starts at -1; uiMapViewListSize starts
				 * at 0, hence the +/-1. Using uiMapViewIndex + 1 ensures we
				 * don't remove the current view from uiMapViewList; only
				 * forward history.
				 */

				while(uiMapViewList.size() > (uiMapViewIndex + 1)){
					uiMapViewList.remove(uiMapViewList.size() - 1);
				}

				/*
				 * Add the new view and increment the viewIndex.
				 */

				uiMapViewList.add(view);
				uiMapViewIndex ++ ;
			}
		}

		/*
		 * Update the uiMap if needed, then wait for for the rendering to be
		 * completed and for the current area's locator to be ready.
		 */

		if(updateUiMap){

			/*
			 * Before update the defaults for the new view, find out whether the
			 * previous area has 'iFrame', record it if it has. Then, get
			 * defaults for the new view.
			 */
			if(view.isEmpty()){
				log("Cannot Determine which view is loaded!");
				takeFullScreenShot("updateViewError");
				throw new RuntimeException("Cannot Determine which view is loaded!");
			}
			log("Setting view to " + view);
			uiMapGetDefaults(view, true);

			/*
			 * If there was a problem opening the requested view , then roll
			 * back the changes to uimapViewList and refresh the UI Map.
			 * returnValue = rollBackAndRefreshUiMap(view,returnValue);
			 */
		}

		return returnValue;
	}

	/**
	 * Check whether the clicked-on element could take us to more than one view,
	 * and if so, (because we've already clicked by this point), see which one
	 * is visible. That's the view we want to update the UI Map with.
	 * 
	 * We check the Page and View to determine the right default area locator.
	 * 
	 * @param view
	 *            .
	 */

	protected String getCurrentAreaisDisplayed(String view, long timeOut){

		if(timeOut == 0)
			timeOut = Long.parseLong(viewTimeout);
		view = view.replace("[", "").replace("]", "").replace("\"", "");
		log("Checking Views: " + view);
		String[] viewArray = StringUtils.split(view, ",");
		String areaLocator = "";
		view = "";
		long startTime = System.currentTimeMillis();

		do{
			for(String thisPageAndView:viewArray){

				String thisPage = StringUtils.split(thisPageAndView, ":")[0];
				String thisView = StringUtils.split(thisPageAndView, ":")[1];

				/*
				 * Seach through the pages in the UI Map tp find the one we've
				 * requested (thisPage).
				 */

				for(JsonNode currentPage:uiMap.path("application").path("pages")){

					if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), thisPage)){

						/*
						 * Now locate the view in the page that we've requestd
						 * (thisView).
						 */

						for(JsonNode currentView:currentPage.path("views")){
							String actualView = StringUtils.defaultString(currentView.path("viewName").textValue());
							if(StringUtils.equalsIgnoreCase(actualView, thisView)){
								/*
								 * Finally, get the locator for the default area in
								 * the view and make sure it is visible. If it is
								 * not visibile, check the other views in
								 * viewArray[].
								 */
								String defaultArea = StringUtils.defaultString(currentView.path("defaultArea").textValue());
								for(JsonNode currentArea:currentPage.path("areas")){
									if(StringUtils.equals(currentArea.path("areaName").textValue(), defaultArea)){
										areaLocator = StringUtils.defaultString(currentArea.path("locator").textValue());
										if(waitForArea(areaLocator, (long)1, true)){
											// get elementLocator.
											JsonNode currentElements = currentArea.path("elements");
											String defaultAreaElementLocator = currentElements.path(0).path("locator").textValue();
											if(waitForElementReadyWithElementLocator(areaLocator, defaultAreaElementLocator, "", "", (long)1, true)){
												view = thisPageAndView;
												break; // Exit currentArea loop.
											}
										}
									}
								}

								if( ! StringUtils.isEmpty(view))

									break; // Exit currentView loop.
							}
						}

						if( ! StringUtils.isEmpty(view)) // Exit currentPage loop.
							break;
					}
				}

				if( ! StringUtils.isEmpty(view)) // Exit thisPageAndView loop.
					break;
			}
		} while(((System.currentTimeMillis() - startTime) < timeOut) && (view.isEmpty()));
		return view;
	}

	/**
	 * Set focus to the current area's iFrame, if it is within one.
	 * 
	 */
	public void switchToIFrame(){

		switchToIFrame("");
	}

	/**
	 * Set focus to the specified area's iFrame, if it is within one.
	 * 
	 * @param areaName
	 */
	public void switchToIFrame(String areaName){

		JsonNode myNode;
		if(areaName.isEmpty())
			myNode = uiMapCurrentArea;
		else{
			myNode = getJsonNodeMatching(uiMapCurrentPage.path("areas"), "areaName", areaName);
		}

		String currentIFrame = "";
		/*
		 * If we are at another iframe, then select back to the top;
		 * otherwise, do nothing.
		 */
		if(myNode.has("iFrame")){
			currentIFrame = myNode.path("iFrame").textValue();
			if( ! iFrameName.isEmpty()){
				if(currentIFrame.equalsIgnoreCase(iFrameName) && iFrameAreaName.equalsIgnoreCase(areaName)){
					return;
				}
				driver.switchTo().defaultContent();
			}
			if(currentIFrame.matches("<\\S*>=.*") && currentIFrame.startsWith("<"))
				driver.switchTo().frame(driver.findElement(getByObject(currentIFrame)));
			else
				driver.switchTo().frame(currentIFrame);
			iFrameName = currentIFrame;
			iFrameAreaName = areaName;
		} else{
			if( ! iFrameName.isEmpty()){
				driver.switchTo().defaultContent();
				iFrameName = "";
			}
		}

	}

	/**
	 * Walk the list of element names to get to a desired view.
	 * 
	 * @param pathToRequestedView
	 */
	private void uiMapWalkToRequestedView(ArrayList<String> pathToRequestedView){

		for(String clickMe:pathToRequestedView){

			long startTime = System.currentTimeMillis();

			// Strip the "areaName:" from clickMe, if it exists.

			if(StringUtils.contains(clickMe, UIMAP_DELIM) && ! StringUtils.contains(clickMe, "gestures")){
				clickMe = clickMe.split(UIMAP_DELIM)[1];
				if( ! clickOn(clickMe))
					break;
			} else{
				clickMe = clickMe.split(UIMAP_DELIM)[1];
				if(StringUtils.contains(clickMe, "home") || StringUtils.contains(clickMe, "back")){
					if( ! clickOn(clickMe))
						break;
				} else{
					if( ! swipe(clickMe))
						break;
				}
			}
			if(measureResponseTime){

				float elapsedTime = (System.currentTimeMillis() - startTime) / 1000f;

				String requestedView = getElementAtt("view", clickMe);

				String viewType = (uiMapCurrentView.path("activeAreas").size() == 1)?"dialog":"view";
				System.out.printf(CommonTools.getCurrentTime() + " INFO - <UI_RESPONSE_TIME> - Opened " + viewType + " '" + requestedView
						+ "' in %.3f seconds.\n", elapsedTime);

				Assert.assertTrue(elapsedTime < maxPageTime);
			}
		}
	}

	/**********************************************************************************************
	 * The following private methods are used in HpcTest for low-level
	 * Create/Read/Update/Delete.
	 **********************************************************************************************/
	/**
	 * Convenience method for getting current valid window names list.
	 * 
	 * @param allWindowNames
	 *            - a list of open windows that bulid.
	 * 
	 * @return
	 *         current window names list.
	 */
	private Set<String> getCurrentValidWindowNamesListInAll(Set<String> allWindowNames){

		Set<String> currentWindowNamesList = new HashSet<String>();

		/*
		 * Build a list of open windows; will be at least one (our application
		 * window).
		 */
		for(String window:allWindowNames){

			/*
			 * In Firefox, the window name of the window which has been closed
			 * is null, but in Chrome is undefined.
			 */

			if( ! (window.equalsIgnoreCase("null") || window.equalsIgnoreCase("undefined") || window.isEmpty()))
				currentWindowNamesList.add(window);
		}
		return currentWindowNamesList;
	}

	protected boolean verifyBodyTextContainsExpectedText(String expectedText, boolean isShown, boolean needWait){

		boolean returnValue = false;
		String bodyText = "";
		Long currentTimeMillis = System.currentTimeMillis();
		if(needWait){
			while((System.currentTimeMillis() - currentTimeMillis) < Long.parseLong(elementTimeout)){
				bodyText = driver.findElement(By.tagName("body")).getText();
				returnValue = bodyText.contains(expectedText) || bodyText.matches(expectedText);
				if(isShown == returnValue){
					break;
				}
				waitByTimeout(500);

			}
		} else{
			waitByTimeout(1000);
			returnValue = bodyText.contains(expectedText) || bodyText.matches(expectedText);
		}

		return returnValue;
	}

	/**********************************************************************************************
	 * The following private methods are used in HpcTest for low-level
	 * verifications.
	 **********************************************************************************************/

	/**
	 * Check whether an element (optionally within a list item that matches a
	 * regex or index) is visible in the main application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param flag
	 *            Simply used to differentiate the private method from the public one.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	private boolean isElementShown(String listName, Object itemMatching, String elementName, String flag){

		boolean returnValue = false;
		try{
			returnValue = getElement(listName, itemMatching, elementName).isDisplayed();
		} catch(Exception e){
			log("The element is not visible!", 3);
		}
		return returnValue;
	}

	/**
	 * Check whether an element within a list item (matched by regex)
	 * is visible in the main application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementShown(String listName, String itemMatching, String elementName){

		return isElementShown(listName, itemMatching, elementName, "");
	}

	/**
	 * Check whether an element within a list item (matched by index)
	 * is visible in the main application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementShown(String listName, int itemMatching, String elementName){

		return isElementShown(listName, itemMatching, elementName, "");
	}

	/**
	 * Check whether a list item (matched by regex)
	 * is visible in the main application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementShown(String listName, String itemMatching){

		return isElementShown(listName, itemMatching, "");
	}

	/**
	 * Check whether a list item (matched by index)
	 * is visible in the main application window.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementShown(String listName, int itemMatching){

		return isElementShown(listName, itemMatching, "");
	}

	/**
	 * Check whether an element is visible in the main application window.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementShown(String elementName){

		/*
		 * Two two booleans returnValue and isNotShown start true so that our
		 * multiple-window lookup works correctly for both isShown==true and
		 * isShown==false.
		 */

		boolean returnValue = false;

		/*
		 * Determine whether we are checking visiblity of an element or a
		 * message/literal text.
		 */

		/*
		 * Elements are only looked for in the main application window; check
		 * whether or not it is visible, based upon the value of isShown.
		 * Visible means present in the DOM and displayed (visible). NOT visible
		 * means either not present in the DOM or not visible.
		 */

		try{
			returnValue = getElement(elementName).isDisplayed();
		} catch(Exception e){
			log("The element is not visible!", 3);
		}
		/*
		 * Otherwise, check for a message or literal text in any open window.
		 * Iterate through all open windows, looking for a match. First does a
		 * check to see whether we are using a message ID; if not, use the
		 * literal text.
		 */
		return returnValue;
	}

	/**
	 * Check whether the specified text is visible in the main application window.
	 * 
	 * @param elementNameOrMessage
	 *            The text or message id you want to check. Will use localized value, if applicable.
	 * 
	 * @param isShown
	 *            If true, check that the text is visible; if false, check that it is not.
	 *
	 * @param needWait
	 *            If true, will wait briefly before checking whether the text is shown (or not).
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	private boolean isTextShown(String elementNameOrMessage, boolean isShown, boolean needWait){

		boolean returnValue = false;
		boolean singleWindow = true;

		try{
			singleWindow = (driver.getWindowHandles().size() == 1);
		} catch(Exception e){
		}

		if(singleWindow){
			returnValue = verifyBodyTextContainsExpectedText(elementNameOrMessage, isShown, needWait);
		} else{
			String currentWindowName = driver.getWindowHandle();
			Set<String> allWindowNames = driver.getWindowHandles();
			Set<String> currentWindowNamesList = getCurrentValidWindowNamesListInAll(allWindowNames);

			for(String windowName:currentWindowNamesList){
				driver.switchTo().window(windowName);
				waitForCondition("(selenium.browserbot.getCurrentWindow().document.readyState=='interactive') || "
						+ "(selenium.browserbot.getCurrentWindow().document.readyState=='complete');", pageTimeout);
				returnValue = verifyBodyTextContainsExpectedText(elementNameOrMessage, isShown, needWait);

				if(returnValue)
					break;
			}
			driver.switchTo().window(currentWindowName);
		}

		return returnValue;
	}

	/**********************************************************************************************
	 * The following private methods are used in HpcTest to log results.
	 **********************************************************************************************/

	/**
	 * Provides a common method for asserting verifications, logging errors, etc.
	 * 
	 * If only two args are specified, then set failOnError = true.
	 * 
	 * @param expression
	 *            A boolean expression that should evaluate to true.
	 * 
	 * @param errorMessage
	 *            The error message to display if the expression is NOT true.
	 * 
	 * @return true if expression is true; otherwise,
	 *         log.
	 */
	private boolean verifyIsTrue(boolean expression, String errorMessage){

		return verifyIsTrue(expression, errorMessage, true);
	}

	/**
	 * Method for providing a common method for asserting verifications, logging errors,
	 * etc.
	 * 
	 * @param expression
	 *            - A boolean expression that should evaluate to true.
	 * @param errorMessage
	 *            - The error message to log if the expression is NOT true.
	 * @param failOnError
	 *            - Fail the test if true and expression is false; otherwise, just log the error.
	 * 
	 * @return true if expression is true; otherwise, false.
	 */
	private boolean verifyIsTrue(boolean expression, String errorMessage, boolean failOnError){

		if( ! expression)
			if(failOnError){
				String shotName = errorMessage.split("'", 3)[1];
				takeFullScreenShot(shotName);
				log(errorMessage, 2);
				Assert.fail(errorMessage);

			} else
				log(errorMessage, 2);
		return expression;
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @param flag
	 *            Simply used to differentiate the private method from the public one.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	private boolean isElementPresent(String listName, Object itemMatching, String elementName, String flag){

		boolean returnValue = false;
		for(int i = 0; i < 3; i ++ ){
			if(isElementShown(listName, itemMatching, elementName, flag)){
				returnValue = true;
				break;
			}
			waitByTimeout(1000);
		}
		return returnValue;
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementPresent(String listName, String itemMatching, String elementName){

		return isElementPresent(listName, itemMatching, elementName, "");
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementPresent(String listName, int itemMatching, String elementName){

		return isElementPresent(listName, itemMatching, elementName, "");
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementPresent(String listName, String itemMatching){

		return isElementPresent(listName, itemMatching, "");
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param listName
	 *            The name of the list containing your element, or empty string
	 *            ("") if checking for a single element, a message ID or literal
	 *            text.
	 * 
	 * @param itemMatching
	 *            The index (starting at 1) of the list item containing your
	 *            element, or a regex used to find a list item with matching
	 *            text. Use an empty string ("") if checking for a single
	 *            element, a message ID or literal text.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementPresent(String listName, int itemMatching){

		return isElementPresent(listName, itemMatching, "");
	}

	/**
	 * Same as isElementShown(), except that it waits up to 3 secs for the element to be displayed.
	 * 
	 * @param elementName
	 *            The name of the element you want to check.
	 * 
	 * @return true if successful; otherwise, log a failure and return false.
	 */
	public boolean isElementPresent(String elementName){

		return isElementPresent(elementName, "");
	}

	/**
	 * Convenience method for waiting for element to load by js.
	 * 
	 * Parameters:
	 * 
	 * @param js
	 *            -javascript
	 * @param timeout
	 *            -Limit time
	 */
	protected void waitForCondition(String js, String timeout){

		((JavascriptExecutor)driver).executeScript("try {" + js + "} catch(err){false}", timeout);
	}

	/**
	 * Wait for specified amount of time (in milliseconds).
	 * 
	 * @param waitFor
	 *            The time to wait in milliseconds.
	 */
	public void waitByTimeout(long waitFor){

		try{
			Thread.sleep(waitFor);
		} catch(Exception e){
		}
	}

	/**
	 * Method for triggering a keypress event..
	 * 
	 * Parameters:
	 * 
	 * @param key
	 *            The java constant for the key to press
	 *            (e.g.,java.awt.event.KeyEvent.VK_ENTER).
	 */
	public void pressKey(Keys key){

		Actions action = new Actions(driver);
		action.sendKeys(key).perform();
	}

	/**
	 * Return the current page url.
	 * 
	 * @return
	 *         return the current page url.
	 */
	public String getCurrentUrl(){

		String actualPageUrl = "";
		Set<String> allWindowNames = driver.getWindowHandles();

		/*
		 * Build a list of open windows; will be at least one (our application
		 * window).
		 */

		Set<String> currentWindowNamesList = new HashSet<String>();

		if(allWindowNames.size() > 1){

			for(String window:allWindowNames){

				/*
				 * In Firefox, the window name of the window which has been
				 * closed is null, but in Chrome is undefined.
				 */

				if( ! (window.equalsIgnoreCase("null") || window.equalsIgnoreCase("undefined") || window.isEmpty()))
					currentWindowNamesList.add(window);
			}

			for(String windowName:currentWindowNamesList){

				boolean mainApplicationWindow = StringUtils.equalsIgnoreCase(windowName, main_window);

				if( ! mainApplicationWindow){

					driver.switchTo().window(windowName);
					waitForCondition("(selenium.browserbot.getCurrentWindow().document.readyState=='interactive') || "
							+ "(selenium.browserbot.getCurrentWindow().document.readyState=='complete');", pageTimeout);

					actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());

					driver.close();
					returnToMain();
				}
			}
		} else{
			actualPageUrl = StringUtils.defaultString(driver.getCurrentUrl());
		}

		return actualPageUrl;
	}

	/**
	 * Method for closing the specified window or tab.
	 * 
	 * Parameters:
	 * 
	 * @param subtabname
	 *            name of the window or tab you want to close
	 * 
	 * @return true if successfully close the page; otherwise, returns false.
	 */
	public boolean closeSubTab(String subtabname){

		boolean closeSubTab = false;

		if(switchWindow(subtabname)){
			try{
				driver.close();
				closeSubTab = true;
			} catch(Exception e){
				log("Failed to close window '" + subtabname + "'.", 3);
			}
		}
		returnToMain();
		return closeSubTab;
	}

	/**
	 * Delete all the visible browser cookies that are stored.
	 */
	public void deleteCookies(){

		driver.manage().deleteAllCookies();
	}

	/**
	 * Get the values of all stored cookies.
	 */
	public String getCookies(){

		String cookies = "";
		Set<Cookie> cookiesSet = driver.manage().getCookies();
		for(Cookie c:cookiesSet){
			cookies += c.getName() + "=" + c.getValue() + ";";
		}
		return cookies;
	}

	/**
	 * Get the value of the specified cookie.
	 * 
	 * @param cookieName
	 *            - Specified cookie name
	 */
	public String getCookies(String cookieName){

		String cookies = "";
		Set<Cookie> cookiesSet = driver.manage().getCookies();
		for(Cookie c:cookiesSet){
			if(c.getName().equals(cookieName)){
				cookies += c.getName() + "=" + c.getValue() + ";";
			}
		}
		return cookies;
	}

	/**
	 * Capture a snapshot of the current page to the specified filename.
	 * 
	 * @param fileName
	 *            - String messages to describe the fileName and it will be a
	 *            part of the png file name.
	 * 
	 * @return - return true if the capture completes, otherwise return false.
	 */
	public void getScreen(String fileName){

		takeFullScreenShot(fileName);

	}

	/**
	 * Capture a snapshot of the current page to the default "timestamp" filename.
	 * 
	 * @param fileName
	 *            - String messages to describe the fileName and it will be a
	 *            part of the png file name.
	 * 
	 * @return - return true if the capture completes, otherwise return false.
	 */
	public void getScreen(){

		getScreen("");
	}

	/**
	 * Capture a snapshot of the current page to the specified filename.
	 * 
	 * @param fileName
	 *            - String messages to describe the fileName and it will be a
	 *            part of the png file name.
	 * 
	 * @return - return true if the capture completes, otherwise return false.
	 */
	protected boolean takeFullScreenShot(String fileName){

		boolean returnValue = false;

		String timeStamp = CommonTools.getDate().replace("-", "") + "_" + CommonTools.getCurrentTime().replace(":", "").replace(".", "");
		fileName = timeStamp + "_" + CommonTools.replaceIllegalFileName(fileName, "_");

		if(StringUtils.endsWith(fileName, "_"))
			fileName = timeStamp;

		if(enableScreenCapture){
			fileName = screenCapturePath + "/" + fileName + ".png";
			getScreenShot(fileName);
			returnValue = true;
		}

		return returnValue;
	}

	/**
	 * Create a file containing a screenshot of the current page.
	 * 
	 * @return - File object containing the screenshot contents.
	 */
	private File getScreenShotFile(){

		File screenshot = null;

		try{
			if( ! (driver instanceof TakesScreenshot)){
				WebDriver augmentDriver = new Augmenter().augment(driver);
				screenshot = ((TakesScreenshot)augmentDriver).getScreenshotAs(OutputType.FILE);
			} else
				screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

		} catch(WebDriverException e){
			log("Failed to take screenshot, please let us know following trace! Test may continue without screenshot.", 2);
			enableScreenCapture = false;
			e.printStackTrace();
		}
		return screenshot;
	}

	/**
	 * Capture a snapshot of the current page to the specified filename.
	 * 
	 * @param fileName
	 *            - String messages to describe the fileName and it will be a
	 *            part of the png file name.
	 * 
	 * @return - return true if the capture completes, otherwise return false.
	 */
	private void getScreenShot(String fileName){

		File screenshot = getScreenShotFile();

		if(screenshot == null)
			return;
		try{
			copyScreenShot(screenshot, new File(fileName));
		} catch(IOException e){
			log("Exception happened when getting screen shot, detail is : '" + e.getMessage() + "'. " + "The screen shot operation was ignored. ", 3);
		}
	}

	/**
	 * Copyies a screen shot file to the specified output file.
	 * 
	 * @param screenShotFile
	 *            the screen shot file.
	 * 
	 * @param outputFile
	 *            the file that we want to put the screen shot file.
	 * 
	 * @throws IOException
	 */
	protected static void copyScreenShot(File screenShotFile, File outputFile) throws IOException{

		FileInputStream imgIs = new FileInputStream(screenShotFile);
		FileOutputStream imageOs = new FileOutputStream(outputFile);
		FileChannel imgCin = imgIs.getChannel();
		FileChannel imgCout = imageOs.getChannel();
		imgCin.transferTo(0, imgCin.size(), imgCout);
		imgCin.close();
		imgCout.close();
		imgIs.close();
		imageOs.close();
	}

	/**
	 * Close the current browser session. Call this method before ending a test.
	 */
	public void close(){

		if(driver != null){

			driver.quit();
		}

		if(System.getProperty("os.name").toLowerCase().contains("windows")){
			try{
				Runtime.getRuntime().exec("taskkill /f /im IEDriverServer.exe");
				Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe");
				waitByTimeout(2000);
			} catch(Exception e){
				e.printStackTrace();
			}
			if( ! targetProfile.isEmpty())
				try{
					Runtime.getRuntime().exec("cmd /c rd /s /q " + targetProfile);
				} catch(Exception e){
					log("Cannot delete folder " + targetProfile);
				}
		} else{
			try{
				Runtime.getRuntime().exec("killall chromedriver");
				// Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe");
				waitByTimeout(2000);
			} catch(Exception e){
				e.printStackTrace();
			}
			if( ! targetProfile.isEmpty())
				try{
					Runtime.getRuntime().exec("rm -R " + targetProfile);
				} catch(Exception e){
					log("Cannot delete folder " + targetProfile);
				}
		}
		log("<===================TEST TERMINATED================>");
	}

	/**
	 * This enum type contains all kinds of Browser languages for Firefox,
	 * currently we only add one language to it non_en-us. We will support
	 * different language later,like UK , French......
	 */
	public static enum FirfoxProfile{
		Nonenus,NonUSCountry,
	}

	/**
	 * Switch to the specified window.
	 * 
	 * @param windowNames
	 *            - Specified WindowName
	 */
	public boolean switchWindow(String windowNames){

		boolean flag = false;
		try{
			String currentHandle = driver.getWindowHandle();
			Set<String> handles = driver.getWindowHandles();

			for(String str:handles){

				if(str.equals(currentHandle))
					continue;
				else{
					driver.switchTo().window(str);
					if(driver.getTitle().contains(windowNames)){
						flag = true;
						log("Switch to window: '" + windowNames + "' successfully!");
						break;
					} else
						continue;
				}
			}

		} catch(NoSuchWindowException e){
			log("Window: '" + windowNames + "' cound not found!", 3);
			flag = false;
		}
		if( ! flag)
			log("Window: '" + windowNames + "' title is not found!", 2);
		Assert.assertTrue(flag, "Window: '" + windowNames + "' title is not found!");
		return flag;
	}

	/**
	 * Switch back to the main window.
	 */
	public void returnToMain(){

		driver.switchTo().window(main_window);
	}

	/**
	 * Close the current window and return to main window.
	 */
	public void closeCurrentWindow(){

		if( ! driver.getWindowHandle().equals(main_window)){
			driver.close();
			returnToMain();
		}
	}

	/**
	 * Close all open windows other than the main window.
	 */
	public void closeOtherWindows(){

		for(String windows:driver.getWindowHandles())
			if( ! windows.equals(main_window)){
				driver.switchTo().window(windows);
				driver.close();
			}
		returnToMain();
	}

	/**
	 * Switch to the open alert, if it exists.
	 */
	public boolean isAlertPresent(){

		try{
			driver.switchTo().alert();
			return true;
		} catch(NoAlertPresentException Ex){
			return false;
		}
	}

	/**
	 * Accepts an alert dialog by pressing OK.
	 * 
	 * @return
	 *         Returns the text of the alert. If no alert is displayed, returns "".
	 */
	public String acceptAlert(){

		String alertText = "";
		try{
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			alert.accept();
		} catch(NoAlertPresentException e){

		}
		if(alertText.isEmpty())
			log("There is no alert in current page");
		else
			log("The alert is '" + alertText + "'.");

		return alertText;

	}

	/**
	 * Dismisses an alert dialog by pressing Cancel.
	 * 
	 * @return
	 *         Returns the text of the alert. If no alert is displayed, returns "".
	 */
	public String dismissAlert(){

		String alertText = "";
		try{
			Alert alert = driver.switchTo().alert();
			alertText = alert.getText();
			alert.dismiss();
		} catch(NoAlertPresentException e){

		}
		if(alertText.isEmpty())
			log("There is no alert in current page");
		else
			log("The alert is '" + alertText + "'.");

		return alertText;
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @return WebElement Object.
	 */
	public WebElement getElement(String itemName, String itemMatching){

		return getElement(itemName, itemMatching, "");
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @return WebElement Object.
	 */
	public WebElement getElement(String itemName, int itemMatching){

		return getElement(itemName, itemMatching, "");
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @return WebElement Object.
	 */
	public WebElement getElement(String elementName){

		return getElement(elementName, "", "");
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @return WebElement Object.
	 */
	protected WebElement getElement(String itemName, Object itemMatching, String elementName){

		return getElement(itemName, itemMatching, elementName, 0);
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 *
	 * @return WebElement Object.
	 */
	public WebElement getElement(String itemName, String itemMatching, String elementName){

		return getElement(itemName, itemMatching, elementName, 0);
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @return WebElement Object.
	 */
	public WebElement getElement(String itemName, int itemMatching, String elementName){

		return getElement(itemName, itemMatching, elementName, 0);
	}

	/**
	 * Method for getting getting a WebElement object according to the locator type, by default, if no
	 * sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 *
	 * @param timeout
	 *            Timeout value for which to look for the element, in millisecs.
	 * 
	 * @return WebElement Object.
	 */
	private WebElement getElement(String itemName, Object itemMatching, String elementName, int timeout){

		String areaLocator = getAreaLocator(elementName.isEmpty()?itemName:elementName);
		String elementLocator = getElementLocator(elementName);
		String listLocator = getElementLocator(itemName);
		WebElement returnValue = null;
		String errorMsg = "";

		List<WebElement> elements;
		boolean emptyItemMatching = true;

		if(itemMatching instanceof Integer)
			if((Integer)itemMatching > 0)
				emptyItemMatching = false;

		if(itemMatching instanceof String)
			if( ! ((String)itemMatching).isEmpty())
				emptyItemMatching = false;

		if(emptyItemMatching){
			if(listLocator.isEmpty()){
				elements = getElements("", areaLocator, "", "");
				if(elements.size() > 0)
					returnValue = elements.get(0);
				else
					errorMsg = itemName;
			} else{
				elements = getElements(areaLocator, listLocator, "", "");
				if(elements.size() > 0)
					returnValue = elements.get(0);
				else
					errorMsg = itemName;

			}

		} else{
			if(elementLocator.isEmpty()){
				List<WebElement> listElements = waitForElementList(areaLocator, listLocator, timeout);

				int matchingIndex = getMatchingIndex(listElements, itemMatching);
				if(matchingIndex >= 0)
					returnValue = listElements.get(matchingIndex);

			} else{
				elements = getElements(areaLocator, listLocator, itemMatching, elementLocator, timeout);
				if(elements.size() > 0)
					returnValue = elements.get(0);
				else
					errorMsg = elementName + " in " + itemName;
			}
		}

		if(returnValue == null){
			throw new IndexOutOfBoundsException("Element " + errorMsg + " Not Found!");
		} else
			return returnValue;
	}

	/**
	 * 
	 * @param areaLocator
	 * @param listLocator
	 * @param elementLocator
	 * @param itemMatching
	 * @return WebElement Object.
	 */
	private List<WebElement> getElements(String areaLocator, String itemLocator, Object itemMatching, String elementLocator){

		return getElements(areaLocator, itemLocator, itemMatching, elementLocator, 0);
	}

	/**
	 * Convenience method for getting a list of WebElement objects according to the locator specified. by
	 * default, if no sign is indicated, cssSelector will be used. Otherwise,
	 * specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator, parentLocator with css locator must be specified to make it works.
	 * Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param elementLocator
	 * @param areaLocator
	 * @param listLocator
	 * @param elementLocator
	 * @param itemMatching
	 * @return WebElement Object.
	 */
	private List<WebElement> getElements(String areaLocator, String itemLocator, Object itemMatching, String elementLocator, int timeout){

		if(areaLocator.isEmpty() && itemLocator.isEmpty() && elementLocator.isEmpty()){
			throw new NoSuchElementException("Element Locator Error!");
		}
		String arealocatorType = getLocatorType(areaLocator);
		String arealocatorStr = getLocatorStr(areaLocator);

		String locator = "";
		WebElement parentElement = null;
		boolean emptyItemMatching = true;
		if(itemMatching instanceof Integer)
			if((Integer)itemMatching > 0)
				emptyItemMatching = false;
		if(itemMatching instanceof String)
			if( ! ((String)itemMatching).isEmpty())
				emptyItemMatching = false;
		if(emptyItemMatching){
			if( ! arealocatorStr.isEmpty())
				if(waitForArea(areaLocator, (long)timeout, true)){
					parentElement = driver.findElement(getByObjectByType(arealocatorStr, arealocatorType));
				} else
					throw new NoSuchElementException("Area Not Found!");
			locator = itemLocator;
		} else{
			// If calling getElements(item, itemMatching,""), throw an exception because with only these 2 parameters, only one element but not a list of
			// element will be got.
			if(elementLocator.isEmpty())
				throw new NoSuchElementException("Please use getElement(item,itemMatching) instead!");
			if(elementLocator.startsWith("<xpath>=")){
				String locatorStr = elementLocator.split("=", 2)[1];
				if( ! locatorStr.startsWith("."))
					log("It appears you are using absolute <xpath> as element locator to find an element in a list, it may not find what you exactly want. Please change the locator for elementLocator '"
							+ elementLocator + "'. Test may continue, but may result in a failure.", 3);
			}
			List<WebElement> listElements = waitForElementList(areaLocator, itemLocator, timeout);

			int index = getMatchingIndex(listElements, itemMatching);

			if(index >= 0)
				parentElement = listElements.get(index);
			locator = elementLocator;
		}

		// get elementName webElement.
		String elementLocatorType = getLocatorType(locator);
		String elementLocatorStr = getLocatorStr(locator);

		if(parentElement == null)
			return driver.findElements(getByObjectByType(elementLocatorStr, elementLocatorType));
		else{
			return parentElement.findElements(getByObjectByType(elementLocatorStr, elementLocatorType));
		}
	}

	/**
	 * Get the locator type from locater specified.
	 * 
	 * @param locator
	 * @return locatorType
	 */
	private String getLocatorType(String locator){

		if(locator.isEmpty())
			return "";

		if(locator.matches("<\\S*>=.*") && locator.startsWith("<"))
			return locator.split("=")[0];

		return "css";
	}

	/**
	 * Get the locator String from locater specified.
	 * 
	 * @param locator
	 * @return locatorString
	 */
	private String getLocatorStr(String locator){

		if(locator.isEmpty())
			return "";

		String locatorType = getLocatorType(locator);

		if(locator.startsWith(locatorType))
			return locator.substring(locatorType.length() + 1);
		else
			return locator;
	}

	/**
	 * /**
	 * Convenience method for getting a list of WebElement objects according to the locator specified. by
	 * default, if no sign is indicated, cssSelector will be used. Otherwise,
	 * specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator, parentLocator with css locator must be specified to make it works.
	 * Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param elementLocator
	 * @param areaLocator
	 * @param listLocator
	 * @param timeInSec
	 * @return WebElement Object.
	 */
	private List<WebElement> waitForElementList(final String areaLocator, final String listLocator, final int timeInSec){

		WebDriverWait driverWait = (WebDriverWait)new WebDriverWait(driver, timeInSec, 500).ignoring(StaleElementReferenceException.class).withMessage(
				"Refersh the element failure to time out " + timeInSec);
		try{

			driverWait.until(new ExpectedCondition<Boolean>(){

				public Boolean apply(WebDriver driver){

					return(getElements(areaLocator, listLocator, "", "").size() > 0);
				}
			});
		} catch(TimeoutException e){
			takeFullScreenShot(listLocator);
			log("Element List is not loaded in " + timeInSec + " seconds.");
		}
		return getElements(areaLocator, listLocator, "", "");

	}

	private int getMatchingIndex(List<WebElement> listElements, Object itemMatching){

		int index = - 1;

		if(itemMatching instanceof Integer)
			index = (Integer)itemMatching - 1;
		if(itemMatching instanceof String){
			String matchingString = (String)itemMatching;
			if(StringUtils.isNumeric(matchingString)){
				index = Integer.valueOf(matchingString) - 1;
			} else{
				itemMatching = getLocalizedText(matchingString);
				String getAttText = "";

				for(int i = 0; i < listElements.size(); i ++ ){
					getAttText = listElements.get(i).getText();
					if(StringUtils.containsIgnoreCase(getAttText, matchingString)){
						index = i;
						break;
					}
				}
			}
		}

		return index;
	}

	/**
	 * Method for getting a list of WebElement objects according to the locator specified. by
	 * default, if no sign is indicated, cssSelector will be used. Otherwise, specified selector will be used.
	 * 
	 * WARNING: if a css locator is used within an area that is not located by css locator,
	 * parentLocator with css locator must be specified to make it works. Or, you may put "<css>=" sign in front of it.
	 * 
	 * @param elementName
	 *            Name of the element you want to get.
	 * 
	 * @return WebElement Object.
	 */
	public List<WebElement> getElements(String elementName){

		String areaLocator = getAreaLocator(elementName);

		String elementLocator = getElementLocator(elementName);
		return getElements(areaLocator, elementLocator, "", "");

	}

	/**
	 * Convenience method for getting by object by element locator.
	 * 
	 * @param elementLocator
	 *            the element locator string from uimap.
	 * @return
	 *         the by object.
	 */
	private By getByObject(String elementLocator){

		if( ! elementLocator.contains("="))
			throw new RuntimeException("Incorrect elementlocator Format. type=locator expected.");
		String locatorType = elementLocator.split("=")[0];
		String locatorStr = elementLocator.substring(locatorType.length() + 1);
		String subLocatorType = getSubSelectorType(locatorStr);
		String subLocatorStr = null;
		By returnObject;
		if(subLocatorType != null){
			subLocatorStr = locatorStr.split(subLocatorType + "=")[1];
			locatorStr = locatorStr.split(subLocatorType + "=")[0];
			returnObject = getByObjectByType(subLocatorStr, subLocatorType);
		} else
			returnObject = getByObjectByType(locatorStr, locatorType);

		return returnObject;
	}

	private By getByObjectByType(String locatorStr, String locatorType){

		if(locatorType.equals("<id>")){
			return By.id(locatorStr);
		}

		if(locatorType.equals("<class>")){
			return By.className(locatorStr);
		}

		if(locatorType.equals("<name>")){
			return By.name(locatorStr);
		}

		if(locatorType.equals("<xpath>")){
			return By.xpath(locatorStr);
		}

		if(locatorType.equals("<tagName>")){
			return By.tagName(locatorStr);
		}

		if(locatorType.equals("<linkText>")){
			return By.linkText(locatorStr);
		}

		if(locatorType.equals("<partialLinkText>")){
			return By.partialLinkText(locatorStr);
		}

		return By.cssSelector(locatorStr.replace("css=", "").replace("<css>=", ""));
	}

	/**
	 * Convenience method for getting by object by locator type.
	 * 
	 * @param locatorStr
	 *            the locator string from uimap.
	 * 
	 * @param locatorType
	 *            the locator type.
	 * 
	 * @return
	 *         the by object.
	 */
	private String getSubSelectorType(String locatorStr){

		String returnValue = null;

		if(locatorStr.contains("<id>=")){
			returnValue = "<id>";
		}

		if(locatorStr.contains("<class>=")){
			returnValue = "<class>";
		}

		if(locatorStr.contains("<name>=")){
			returnValue = "<name>";
		}

		if(locatorStr.contains("<xpath>=")){
			returnValue = "<xpath>";
		}

		if(locatorStr.contains("<tagName>=")){
			returnValue = "<tagName>";
		}

		if(locatorStr.contains("<linkText>=")){
			returnValue = "<linkText>";
		}

		if(locatorStr.contains("<partialLinkText>=")){
			returnValue = "<partialLinkText>";
		}

		if(locatorStr.contains("<css>=")){
			returnValue = "<css>";
		}

		return returnValue;
	}

	/**
	 * Get the locator for the specified element.
	 * 
	 * Parameters:
	 * 
	 * @param elementName
	 * 
	 * @returns Locator string for the specified element.
	 */
	public String getElementLocator(String elementName){

		return getElementAtt("locator", elementName);
	}

	/**
	 * if locator contains "{{" and "}}" , go to connect file get value
	 * */
	protected String getConnectValueAndReplace(String elementLocator){

		while(elementLocator.contains("{{") && elementLocator.contains("}}")){

			String embeddedPropertyName = StringUtils.substringBetween(elementLocator, "{{", "}}");
			String connectText = getLocalizedText(embeddedPropertyName);

			if(connectText.isEmpty() && connectText.equals("")){
				log("Cannot find string for '" + embeddedPropertyName + "'.");
				throw new NullPointerException("The '" + embeddedPropertyName + "' is error, '" + connectText + "' is not null.");
			} else
				log("Replaced '" + embeddedPropertyName + "' with '" + connectText + "'.");
			elementLocator = StringUtils.replace(elementLocator, ("{{" + embeddedPropertyName + "}}"), connectText);
		}
		// elementLocator = elementLocator.trim();
		return elementLocator;
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @param timeInSec
	 *            Timeout value, in secs.
	 *
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	private boolean waitForElementNotShown(final String listName, final Object itemMatching, final String elementName, Long timeInSec){

		long timeOut = timeInSec;

		if(timeInSec == 0){
			timeOut = Long.parseLong(elementTimeout) / 1000;
		}

		try{
			WebDriverWait driverWait = (WebDriverWait)new WebDriverWait(driver, timeOut, 500).ignoring(StaleElementReferenceException.class).withMessage(
					"Refersh the element failure to time out " + timeOut);
			return driverWait.until(new ExpectedCondition<Boolean>(){

				public Boolean apply(WebDriver driver){

					try{
						if(getElement(listName, itemMatching, elementName, 1).isDisplayed()){
							return false;
						}
					} catch(IndexOutOfBoundsException e){
					} catch(NoSuchElementException e){
					}

					log("Element '" + (elementName.isEmpty()?listName:elementName) + "' is not present on this page.");
					return true;
				}
			});

		} catch(TimeoutException e){
			log("Element '" + (elementName.isEmpty()?listName:elementName) + "' still present after " + timeOut + " secs.");
			takeFullScreenShot(elementName.isEmpty()?listName:elementName);
			return false;
		}
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 *
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	public boolean waitForElementNotShown(String elementName){

		return waitForElementNotShown(elementName, "", "", (long)0);
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 *
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	public boolean waitForElementNotShown(String listName, String itemMatching){

		return waitForElementNotShown(listName, itemMatching, "", (long)0);
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 *
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	public boolean waitForElementNotShown(String listName, int itemMatching){

		return waitForElementNotShown(listName, itemMatching, "", (long)0);
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	private boolean waitForElementNotShown(String listName, Object itemMatching, String elementName){

		return waitForElementNotShown(listName, itemMatching, elementName, (long)0);
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 * 
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	public boolean waitForElementNotShown(String listName, String itemMatching, String elementName){

		return waitForElementNotShown(listName, itemMatching, elementName, (long)0);
	}

	/**
	 * Wait for the specified element to not be displayed.
	 * 
	 * @param itemName
	 *            The name of the list containing the element.
	 * 
	 * @param itemMatching
	 *            Regex or index of the list item you are looking for.
	 * 
	 * @param elementName
	 *            The name of the element you want to get.
	 *
	 * @return - If the element disappears, return true , otherwise return false.
	 */
	public boolean waitForElementNotShown(String listName, int itemMatching, String elementName){

		return waitForElementNotShown(listName, itemMatching, elementName, (long)0);
	}

	/**
	 * Convenience method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param areaLocator
	 * @param listLocator
	 * @param elementLocator
	 * @param itemMatching
	 * @param timeInSec
	 * @return True if the element is present / visible.
	 */

	private boolean waitForElementReadyWithElementLocator(String areaLocator, String listLocator, Object itemMatching, String elementLocator, Long timeInSec){

		return waitForElementReadyWithElementLocator(areaLocator, listLocator, itemMatching, elementLocator, timeInSec, false);
	}

	private boolean waitForElementReadyWithElementLocator(final String areaLocator, final String listLocator, final Object itemMatching,
			final String elementLocator, Long timeInSec, boolean quiet){

		long timeOut = (timeInSec == 0)?Long.parseLong(elementTimeout) / 1000:timeInSec;
		try{

			WebDriverWait driverWait = (WebDriverWait)new WebDriverWait(driver, timeOut).ignoring(StaleElementReferenceException.class).withMessage(
					"Refersh the element failure to time out " + timeOut);

			return driverWait.until(new ExpectedCondition<Boolean>(){

				public Boolean apply(WebDriver driver){

					List<WebElement> elements = getElements(areaLocator, listLocator, itemMatching, elementLocator);
					return elements.size() > 0 && elements.get(0).isDisplayed();
				}
			});
		} catch(Exception e){
			if( ! quiet){
				log("The Element is not found <areaLocator> = '" + areaLocator + "': <elementLocator> = '"
						+ ((elementLocator.isEmpty())?listLocator:elementLocator) + "' in " + timeOut + " seconds.", 2);

				takeFullScreenShot("ChangeViewFailed");
			}
			return false;
		}

	}

	/**
	 * Convenience method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 * @param elementName
	 * @param itemMatching
	 * @param timeInSec
	 * @return True if the element is present / visible.
	 */
	private boolean waitForElementReadyWithElementName(final String listName, final Object itemMatching, final String elementName, Long timeInSec){

		long timeOut = timeInSec;
		log("Waiting for element: " + (elementName.isEmpty()?listName:elementName));
		try{

			WebDriverWait driverWait = (WebDriverWait)new WebDriverWait(driver, timeOut, 500).ignoring(StaleElementReferenceException.class).withMessage(
					"Refresh the element failure to time out " + timeOut);
			return driverWait.until(new ExpectedCondition<Boolean>(){

				public Boolean apply(WebDriver driver){

					boolean returnValue = false;
					try{

						returnValue = getElement(listName, itemMatching, elementName).isDisplayed();
					} catch(IndexOutOfBoundsException e){

					}

					return returnValue;
				}
			});
		} catch(TimeoutException e){
			log("The Element is not found elementName '" + (elementName.isEmpty()?listName:elementName) + "' in " + timeOut + " seconds.");
			takeFullScreenShot(elementName.isEmpty()?listName:elementName);
			return false;
		}

	}

	protected boolean waitForElement(String listName, Object itemMatching, String elementName, long timeOutInSec){

		return waitForElementReadyWithElementName(listName, itemMatching, elementName, timeOutInSec);
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	protected boolean waitForElement(String listName, Object itemMatching, String elementName){

		return waitForElementReadyWithElementName(listName, itemMatching, elementName, Long.parseLong(elementTimeout) / 1000);
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	private boolean waitForElementShown(String listName, Object itemMatching, String elementName){

		return waitForElement(listName, itemMatching, elementName);
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElementShown(String listName, String itemMatching, String elementName){

		return waitForElement(listName, itemMatching, elementName);
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElementShown(String listName, int itemMatching, String elementName){

		return waitForElement(listName, itemMatching, elementName);
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElementShown(String elementName){

		return waitForElementShown(elementName, "", "");
	}

	/**
	 * Convenience method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * Parameters:
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElementShown(String listName, String itemMatching){

		return waitForElementShown(listName, itemMatching, "");
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElementShown(String listName, int itemMatching){

		return waitForElementShown(listName, itemMatching, "");
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElement(String elementName){

		return waitForElement(elementName, "", "");
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is present / visible.
	 */
	public boolean waitForElement(String listName, String itemMatching){

		return waitForElement(listName, itemMatching, "");
	}

	/**
	 * Method for waiting for the specified elementLocator to be ready for get/set interactions.
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is present / visible.
	 */

	public boolean waitForElement(String listName, int itemMatching){

		return waitForElement(listName, itemMatching, "");
	}

	/**
	 * Return the localized version of a msg id, regex or literal string.
	 * 
	 * In a test case, one can use either the msg id or the English text of the
	 * string. This method will do the lookup across content config files and
	 * return the proper localized value. If the lookup fails, then the literal
	 * value of textString is returned.
	 * 
	 * Four files are used to specify the content used to test expected results
	 * for page text, element default values, confirmation or error
	 * notifications, etc. These four files are specified in the
	 * <SUT>.properties file:
	 * 
	 * conf.messages - Localized version of conf/messages from epc2-web (e.g.,
	 * messages.en_UK). Do NOT edit. conf.content - Localized resources (text,
	 * URLs) not in the conf.messages file (e.g., HPC_content.en_UK).
	 * conf.messages.lookup - US English version of conf/messages used for
	 * looking up the msg id. Do NOT edit. conf.content.lookup - US English
	 * version of resources not in conf/messages used for for looking up the msg
	 * id.
	 * 
	 * NOTE: The conf.messages and conf.content files are combined into a single
	 * content object. Similarly, conf.messages.lookup and conf.content.lookup
	 * are combined into a single contentLookup object.
	 * 
	 * The lookup process goes like this:
	 * 
	 * 1. Search content for textString as a msg id; if found, return the
	 * localized value. 2. Search contentLookup for textString as literal text;
	 * if not found, try as a regex. NOTE: In both cases, if textString begins
	 * with ".", limit the nodes searched to those with a matching msg id. 3. If
	 * either are found, use the corresponding msg id to lookup the localized
	 * string in content. 4. If steps 1-3 don't find a match, return textString.
	 * 
	 * For example, if our locale is the UK:
	 * 
	 * getLocalizedText("msg.settings.updatePwd.doesnotmatch") - Returns
	 * "Passwords do not match UK" getLocalizedText(".updatePwd doesnotmatch") -
	 * Returns "Passwords do not match UK" getLocalizedText("Letters only") -
	 * Returns "Letters only UK" getLocalizedText("6 characters") - Returns
	 * "Must contain 6 characters UK" (this is the first of multiple matches)
	 * getLocalizedText("quarZelplex") - Returns "quarZelplex"
	 * 
	 * @param textString
	 *            The string to locate, either a message id, literal string or
	 *            regex. If the textString begins with ".", then the string up
	 *            to the first whitespace char is used to specify a subset of
	 *            nodes to search.
	 * 
	 * @return The localized version of the specified string.
	 * 
	 *         NOTE: Some strings in messages use "%s" as a replacable parameter
	 *         (such as device email); this is replaced by the regexp ".*?" so
	 *         that matches will work as expected.
	 */
	protected String getLocalizedText(String textString){

		/*
		 * If textString happens to be empty, just return it.
		 */
		if(StringUtils.isBlank(textString) || StringUtils.isNumeric(textString))
			return textString;

		/*
		 * 1. Search content for textString as a msg id; if found, return the
		 * localized value.
		 */
		String returnValue = CommonTools.getConfigValue(content, textString).trim();

		if(StringUtils.isBlank(returnValue)){
			/*
			 * 2. Search contentLookup for textString as literal text; if not
			 * found, try as a regex.
			 * NOTE: In both cases, if textString begins with ".", limit the
			 * nodes searched to those with a matching msg id.
			 */

			String filterString = ""; // A filter to limit the contentLookup
			// nodes searched (if any).
			String textOrRegexp = ""; // The literal text or rexexp to check.

			if(StringUtils.startsWith(textString, ".")){
				filterString = StringUtils.substringBefore(StringUtils.substringAfter(textString, "."), " ");
			}

			textOrRegexp = StringUtils.strip(StringUtils.substringAfter(textString, filterString));

			/*
			 * Here we iterate through every field in contentLookup, searching
			 * for a value that matches textString either literally or as a
			 * regexp.
			 * If textString began with ".", it is giving us a clue about what
			 * group of msg ids we should be looking at, so we ignore any mgs
			 * ids (field names) that don't first match this criteria. We use
			 * filterString to filter the msg ids (if needed) and textOrRegexp
			 * to locate a matching field value.
			 * We'll build an array of string arrays, split across '","' with
			 * the key first, and the value following a '":"', with leading and
			 * trailing '"' chars removed.
			 * TODO: Tried all the JSON iterators (elements(), fields(),
			 * fieldNames(), iterator()), but none of them seem to work
			 * properly, so we may want to try upgrading the Jackson JSON
			 * version for a better solution.
			 */

			boolean matchingField = false;

			Iterator<String> Keys = contentLookup.fieldNames();
			while(Keys.hasNext()){

				boolean skipThisField = false;

				String fieldName = Keys.next();
				String fieldValue = contentLookup.path(fieldName).textValue();

				if( ! StringUtils.isBlank(filterString))
					skipThisField = ! (StringUtils.contains(fieldName, filterString) || fieldName.matches(filterString));
				/*
				 * If the msg id is ok, check the string value for a match.
				 */

				if( ! skipThisField){

					try{
						matchingField = StringUtils.equalsIgnoreCase(fieldValue, textOrRegexp) || fieldValue.matches(textOrRegexp);

					} catch(PatternSyntaxException e){
						matchingField = false;
					}
					/*
					 * If we found a matching field value, get the field name
					 * (msg id), then use it to lookup the appropriate localized
					 * string in content.
					 */

					if(matchingField){

						returnValue = CommonTools.getConfigValue(content, fieldName);
						matchingField = StringUtils.isNotEmpty(returnValue);
					}
				}

				/*
				 * If we found a matching field, exit the for loop.
				 */

				if(matchingField)
					break;

			}

			/*
			 * Didn't find any matching fields on the string value, so just
			 * return textString as a literal.
			 */

			if( ! matchingField)
				returnValue = textString;
		}

		/*
		 * Substitute any "%s" replacable parameters with ".*?" regexp and
		 * return it.
		 */

		returnValue = StringUtils.replace(returnValue, "%s", ".*?");
		return returnValue;
	}

	/**
	 * Get the visible (i.e. not hidden by CSS) innerText of this element, including sub-elements,
	 * without any leading or trailing whitespace.
	 * 
	 * @param - elementName The name of element
	 * 
	 * @return The innerText of this element.
	 */
	public String getElementText(String elementName){

		waitForElement(elementName);
		String elementText = getElement(elementName).getText();
		log("The text of the element '" + elementName + "' is '" + elementText + "'.");
		return elementText;

	}

	/**
	 * Get an elements's full text value, including multiple elements.
	 * Example, get all albums' description on the album page.
	 * 
	 * @param - elementName
	 *        The name of element in json file.
	 * 
	 * @return The complete text of all elements matching the specified elementName.
	 */
	public String getElementAllText(String elementName){

		String allText = "";
		List<WebElement> allElement = getElements(elementName);

		for(WebElement element:allElement){
			allText += element.getText();
		}
		return allText;
	}

	/**
	 * Getting an elements's size of same style. Example, get all photos's size on the photos page.
	 * 
	 * @param elementName
	 *            The name of element in json file.
	 * 
	 * @return Size of elements.
	 */
	public int getElementsSize(String elementName){

		List<WebElement> elements = getElements(elementName);
		return elements.size();
	}

	/**
	 * Update the data structure of uiMap.
	 * If you want switch to specified view of specified page, This method can help you .
	 * 
	 * @param requestedView
	 *            -Request view which you want.
	 * 
	 * @return If update is successful, return true; if not, return false .
	 * 
	 *         NOTE: In general, should use setViewTo(), which also handles navigation.
	 * 
	 * */
	public boolean setViewToData(String requestedView){

		String[] viewArray = null;
		log("Setting view to '" + requestedView + "'.");
		viewArray = requestedView.split(":");

		for(JsonNode currentPage:uiMap.path("application").path("pages")){

			if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), viewArray[0])){
				log("currentPage : " + viewArray[0]);

				for(JsonNode currentView:currentPage.path("views")){
					if(StringUtils.equalsIgnoreCase(currentView.path("viewName").textValue(), viewArray[1])){
						log("currentView : '" + currentView.path("viewName").textValue() + "'.");
						uiMapGetDefaults(requestedView, true);
						return true;
					}
				}
				break;
			}
		}
		throw new RuntimeException(requestedView + " cannot be found in uiMap.");

	}

	/**
	 * Switch to the new popup window.
	 * 
	 * NOTES:
	 * 
	 * When we click on a link on the current page, it will open another new
	 * window or new sub tab , we need to swich to the new window or tab.
	 * 
	 */
	public void switchPopupNewWindow(){

		String currentHandle = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> it = handles.iterator();
		while(it.hasNext()){
			if(currentHandle == it.next())
				continue;
			driver.switchTo().window(it.next());
		}
	}

	/**
	 * Switch to prompt window.
	 * 
	 * NOTE:
	 * 
	 * When we click on a link on the current page, it will open another new
	 * window or new sub tab , we need to swich to the new window or tab .
	 * Using the method need to combine with method of "getWindowHandles()" .
	 * Example: At first,you need to get before data by "getWindowHandles()"
	 * Second step ,Click on a link .
	 * Third ,Get all imformation for current windows by "getWindowHandles()" .
	 * Fourth ,Calls 'switchToPromptWindow' and switch to prompted window
	 * Parameters:
	 * 
	 * @param before
	 *            - before click on an element ,all infomation for current windows
	 * 
	 * @param after
	 *            - after click on an element ,all infomation for current windows
	 */
	public void switchToPromptWindow(Set<String> before, Set<String> after){

		List<String> whs = new ArrayList<String>(after);
		whs.removeAll(before);
		/*
		 * This is important, it is found that if a modal window is opened by
		 * AutoIt tool,
		 * some handles might be empty at the beginning, and change to a valid
		 * value later
		 */
		whs.remove("");
		if(whs.size() > 0){
			driver.switchTo().window(whs.get(0));
		} else{
			throw new WebDriverException("No new window prompted out.");
		}
	}

	/**
	 * Getting all information of current open windows.
	 * 
	 * @return all information of current open windows
	 */
	public Set<String> getWindowHandles(){

		return driver.getWindowHandles();
	}

	/**
	 * Get element coordinate
	 * 
	 * @param elementName
	 * @return
	 *         the element coordinate
	 */
	protected JsonNode getCoordinateJsonNode(String elementName){

		JsonNode uimapElements = uiMapCurrentArea.path("elements");
		for(JsonNode currentElement:uimapElements){
			if(currentElement.path("elementName").textValue().equalsIgnoreCase(elementName)){
				if(currentElement.path("coordinate").equals(null) || currentElement.path("coordinate").equals("")){
					takeFullScreenShot(elementName);
					throw new NullPointerException("The coordinate is null.");
				}
				return currentElement.path("coordinate");
			}
		}
		log("The element: '" + elementName + "' is not found.", 2);
		return null;
	}

	/**
	 * Get element current view when type(click,gestures,tap,press and so on) element
	 * 
	 * @param type
	 *            operate the element's type
	 * @param elementName
	 * @param direction
	 *            the direction of the gestures and swipe
	 * @return
	 *         the current view
	 */
	protected String getTargetView(String type, String elementName, String direction){

		if( ! StringUtils.isEmpty(type)){
			if(type.equalsIgnoreCase("click")){
				return getElementAtt("view", elementName);
			}
			if(type.equalsIgnoreCase("gestures")){
				if( ! StringUtils.isEmpty(direction)){
					try{
						JsonNode Currentview = uiMapCurrentView.path(type);
						String targetView = Currentview.path(direction).textValue();

						if(targetView.contains(":")){
							return targetView;
						} else{
							throw new NumberFormatException("The format is error");
						}
					} catch(NullPointerException e){

						throw new NullPointerException("Current view cann't find " + type + " [ " + direction
								+ " ] view , Please check whether it is available .");
					}

				} else{
					throw new NullPointerException("The " + type + " is null.");
				}
			}

			if(type.equalsIgnoreCase("tap")){
				return getElementAtt("view", elementName);
			}

			if(type.equalsIgnoreCase("press")){
				return getElementAtt("pressView", elementName);
			}
		} else{
			throw new RuntimeException("The type is null");
		}
		return "";

	}

	/**
	 * Determine whether the specified element is NOT enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is not enabled.
	 */
	public boolean isElementNotEnabled(String listName, String itemMatching, String elementName){

		return ! isElementEnabled(listName, itemMatching, elementName);
	}

	/**
	 * Determine whether the specified element is NOT enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is not enabled.
	 */
	public boolean isElementNotEnabled(String listName, int itemMatching){

		return ! isElementEnabled(listName, itemMatching);
	}

	/**
	 * Determine whether the specified element is NOT enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is not enabled.
	 */
	public boolean isElementNotEnabled(String listName, int itemMatching, String elementName){

		return ! isElementEnabled(listName, itemMatching, elementName);
	}

	/**
	 * Determine whether the specified element is NOT enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @return - True if the element is not enabled.
	 */
	public boolean isElementNotEnabled(String listName, String itemMatching){

		return ! isElementEnabled(listName, itemMatching);
	}

	/**
	 * Determine whether the specified element is NOT enabled for interaction..
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return - True if the element is not enabled.
	 */
	public boolean isElementNotEnabled(String elementName){

		return ! isElementEnabled(elementName);
	}

	private void waitForElementEnabled(String listName, Object itemMatching, String elementName, int a){

		boolean returnValue = false;
		for(int i = 0; i < Long.parseLong(elementTimeout) / 1000; i ++ ){
			returnValue = isElementEnabled(listName, itemMatching, elementName);
			waitByTimeout(1000);
			if(returnValue)
				break;
		}
		if( ! returnValue){
			throw new RuntimeException("Wait for <" + (elementName.isEmpty()?listName:elementName) + "> enable timeout " + Long.parseLong(elementTimeout)
					/ 1000);
		}

	}

	/**
	 * Wait for the specified element to be enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public void waitForElementEnabled(String listName, int itemMatching){

		waitForElementEnabled(listName, itemMatching, "", 0);
	}

	/**
	 * Wait for the specified element to be enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 */
	public void waitForElementEnabled(String listName, String itemMatching){

		waitForElementEnabled(listName, itemMatching, "", 0);
	}

	/**
	 * Wait for the specified element to be enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public void waitForElementEnabled(String listName, int itemMatching, String elementName){

		waitForElementEnabled(listName, itemMatching, elementName, 0);
	}

	/**
	 * Wait for the specified element to be enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public void waitForElementEnabled(String listName, String itemMatching, String elementName){

		waitForElementEnabled(listName, itemMatching, elementName, 0);
	}

	/**
	 * Wait for the specified element to be enabled for interaction..
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public void waitForElementEnabled(String elementName){

		waitForElementEnabled(elementName, "", "", 0);
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	private boolean isElementEnabled(String listName, Object itemMatching, String elementName){

		return getElement(listName, itemMatching, elementName).isEnabled();
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public boolean isElementEnabled(String listName, int itemMatching, String elementName){

		return getElement(listName, itemMatching, elementName).isEnabled();
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public boolean isElementEnabled(String listName, String itemMatching, String elementName){

		return getElement(listName, itemMatching, elementName).isEnabled();
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 */
	public boolean isElementEnabled(String listName, int itemMatching){

		return getElement(listName, itemMatching, "").isEnabled();
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param listName
	 *            The name of the list you wish to use (from the uiMap).
	 * 
	 * @param itemMatching
	 *            The item in the list you wish to select. You can specify an
	 *            index ("1", "2", ...) or a literal string/regex to match.
	 */
	public boolean isElementEnabled(String listName, String itemMatching){

		return getElement(listName, itemMatching, "").isEnabled();
	}

	/**
	 * Check whether the specified element is enabled for interaction..
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 */
	public boolean isElementEnabled(String elementName){

		return getElement(elementName, "", "").isEnabled();
	}

	/**
	 * Get the locator of current Area.
	 * 
	 * @param elementName
	 *            - Any valid Selenium locator string.
	 * 
	 * @return locator of current area.
	 */
	public String getAreaLocator(String elementName){

		return getElementAtt("areaLocator", elementName);
	}

	/**
	 * Get the specified attribute for element in current Area.
	 * 
	 * @param attributeName
	 *            Attribute Name to retrive.
	 * 
	 * @param elementName
	 *            Name of the element to operate with.
	 * 
	 * @return Specified element attribute.
	 */
	protected String getElementAtt(String attributeName, String elementName){

		String elementAttributeValue = ""; // Locator or other attribute for a
		String areaLocator = "";
		String areaName = "";

		/*
		 * If we are looking for an element in a list, our uiMap search will be
		 * for the list locator, followed by a DOM search for the element within
		 * a matching list item. We use these two variables to search either for
		 * the list or for the element within the areas.
		 */

		ArrayList<JsonNode> areas = new ArrayList<JsonNode>();

		/*
		 * If the elementName includes an area, search it first, then the
		 * current Area (if any), and finally we'll search the remaining areas
		 * in the view, trying not to check them twice.
		 * JsonNode.findValuesAsText() returns an empty string, so we have to
		 * parse a string.
		 */

		if(uiMapCurrentArea.has("areaName"))
			areas.add(uiMapCurrentArea);

		/*
		 * The values of activeAreas are just like this:"activeAreas": [
		 * "header", "welcome", "toolbox", "cookieHeader", "footer" ], we remove
		 * the Start and End , then split the String by ",".
		 */

		String activeAreas = uiMapCurrentView.path("activeAreas").toString();

		activeAreas = StringUtils.removeStart(activeAreas, "[\"");
		activeAreas = StringUtils.removeEnd(activeAreas, "\"]");

		String[] activeAreasArray = activeAreas.split("\\W,\\W");

		for(String currentArea:activeAreasArray){

			if( ! StringUtils.isEmpty(currentArea)){

				for(JsonNode item:uiMapCurrentPage.path("areas")){

					String itemArea = item.path("areaName").textValue();

					if(StringUtils.equals(itemArea, currentArea) && ! areas.contains(item))
						areas.add(item);
				}
			}
		}
		ArrayList<JsonNode> elementMatches = new ArrayList<JsonNode>();
		JsonNode matchedElement = jsonMapper.createObjectNode();;
		for(JsonNode currentArea:areas){

			for(JsonNode currentElement:currentArea.path("elements")){

				if(StringUtils.equalsIgnoreCase(currentElement.path("elementName").textValue(), elementName)){
					matchedElement = currentElement.deepCopy();
					((ObjectNode)matchedElement).put("areaLocator", StringUtils.defaultString(currentArea.path("locator").textValue()));
					((ObjectNode)matchedElement).put("areaName", StringUtils.defaultString(currentArea.path("areaName").textValue()));
					elementMatches.add(matchedElement);
				}
			}
		}
		for(JsonNode currentElement:elementMatches){

			/*
			 * Clear these before checking the next currentElement so we
			 * don't retain values from former potentially-matching
			 * elements.
			 */

			elementAttributeValue = "";

			/*
			 * If there are more than one element, so we should get the view
			 * of the element which is visible.
			 */

			elementAttributeValue = currentElement.path(attributeName).toString().replace("\"", "");
			areaLocator = StringUtils.defaultString(currentElement.path("areaLocator").textValue());
			areaName = StringUtils.defaultString(currentElement.path("areaName").textValue());

			switchToIFrame(areaName);

			if(elementMatches.size() > 1){
				if(waitForElementReadyWithElementLocator(areaLocator, "", "", StringUtils.defaultString(currentElement.path("locator").textValue()), (long)3)){
					break;
				}
			}
		}

		if(attributeName.equalsIgnoreCase("areaLocator")){
			return getConnectValueAndReplace(areaLocator);
		}
		if(attributeName.equalsIgnoreCase("areaName")){
			return getConnectValueAndReplace(areaName);
		} else
			return getConnectValueAndReplace(elementAttributeValue);

	}

	/**
	 * Get view attribute.
	 * 
	 * @param type
	 *            the attribute type which want to get for uimap view.
	 * @return
	 *         the type attribute
	 * @throws NullPointerException
	 */
	protected JsonNode getViewAttribute(String type) throws NullPointerException{

		JsonNode target = jsonMapper.createObjectNode();
		if(uiMapCurrentView.has(type)){
			target = uiMapCurrentView.path(type);
		} else{
			throw new NullPointerException("Specified attribute \"" + type + "\" cannot be found in current view \""
					+ uiMapCurrentView.get("viewName").textValue() + "\".");
		}
		return target;
	}

	protected boolean swipe(String direction){

		return false;
	}

	protected boolean checkElementInView(String type, String elementName){

		boolean returnValue = false;
		if(uiMapCurrentView.has(type)){
			JsonNode currentView = uiMapCurrentView.path(type);
			returnValue = currentView.has(elementName);
		}
		return returnValue;
	}

	/**
	 * Get the XML document and parse it.
	 * 
	 * @return return the XML document.
	 */
	protected Document parseXmlFile(File xmlFile){

		Document doc = null;
		try{
			// File xmlFile = getXmlFile();
			DocumentBuilderFactory buf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = buf.newDocumentBuilder();
			doc = db.parse(xmlFile);
		} catch(Exception e){

		}
		return doc;
	}

	/**
	 * Get the xml file for the current uri.
	 * 
	 * @param uri
	 * @return the xml file.
	 */
	protected File getXML(String uri){

		sendLEDCommand request;
		File xmlFile = null;
		request = new sendLEDCommand(uri, "GET");
		request.execute();
		String xmlString = request.loadResponseBody().replace("\"x", "\" x").replaceAll("<Type>([\\s\\S]{20,50})</Type>", "<Type>placeholder</Type>");
		try{
			xmlFile = new File(FILEDIR + "FrontPanel_" + timeStamp + ".xml");

			xmlFile.createNewFile();
			FileWriter fw = new FileWriter(xmlFile);
			fw.write(xmlString);
			fw.flush();
			fw.close();

			Thread.sleep(250);
		} catch(Exception e){
		}
		if(xmlString.isEmpty()){
			throw new RuntimeException("Screen info not found!");
		}
		return xmlFile;
	}

	/**
	 * Get the name of the current view.
	 * 
	 * @return The name of the current view.
	 */
	public String getCurrentView(){

		return uiMapCurrentPage.path("properties").path("pageName").textValue() + ":" + uiMapCurrentView.path("viewName").textValue();
	}

}
