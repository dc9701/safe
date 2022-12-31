package base;

import java.io.File;

import junit.framework.AssertionFailedError;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebElement;

import tools.commonTools.SmartRobot;
import base.core.TestUiClass;

public abstract class WebApp extends TestUiClass{

	protected String getAppName(){

		return "";
	}

	protected String getAppType(){

		return "WebApp";
	}

	protected WebApp(){

		this("");
	}

	protected WebApp(String SUT){

		this(SUT, "");
	}

	protected WebApp(String SUT,String profileName){

		super(SUT, profileName);
		platformSupportInitiate(profileName);

	}

	public boolean signIn(){

		// TODO Auto-generated method stub
		return false;
	}

	public boolean signOut(){

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void platformSupportInitiate(String profileName){

		setProfilePath(profileName);

		startSeleniumServerAndBrowser();

		prepareTestEnvironment();
	}

	private void setProfilePath(String profileName){

		browserName = getProperty("browser.name");
		browserSize = getProperty("browser.size").toLowerCase();
		browserProfilePath = getProfilePath(profileName);
	}

	private String getProfilePath(String profileName){

		/*
		 * Define the browserProfilePath according to the browser we are going
		 * to test.
		 */
		if(browserName.matches(".*firefox.*")){

			return getBrowserProfileProperty("profile.firefox", profileName);

		} else if(browserName.matches(".*iexplore.*")){
			return null;
		} else if(browserName.matches(".*chrome.*")){
			return getBrowserProfileProperty("profile.chrome");
		}
		throw new RuntimeException("Can't find any profile property for browser [" + browserName + "] and profile [" + profileName + "]. ");
	}

	protected void startSeleniumServerAndBrowser(){

		targetProfile = "";
		log("Test is running on " + System.getProperty("os.name"));
		if(browserName.matches(".*firefox.*")){
			log("Loading Firefox Profile and open Firefox...");
			File profileFile = new File(browserProfilePath);
			if(profileFile.exists()){
				FirefoxProfile profile = new FirefoxProfile(new File(browserProfilePath));
				profile.setEnableNativeEvents(true);
				driver = new FirefoxDriver(profile);
			} else{
				driver = new FirefoxDriver();
			}
		}
		if(browserName.matches(".*chrome.*")){
			
			log("Loading Chrome Profile and open Chrome...");
			String userProfile="";
			String execString="";
			
			if(System.getProperty("os.name").toLowerCase().contains("windows")){
				System.setProperty("webdriver.chrome.driver", testDataRoot + "common\\browserProfiles\\drivers\\chromedriver.exe");
				userProfile = browserProfilePath.replace("/", "\\");
				targetProfile = (testRoot.replace("test-classes/", "profiles/") + "b" + System.currentTimeMillis() + "/").replace("/", "\\");
				execString = "xcopy " + userProfile + " " + targetProfile + " /e /y";
			}
			else{
				System.setProperty("webdriver.chrome.driver", testDataRoot + "common/browserProfiles/drivers/chromedriver");
				userProfile = browserProfilePath;
				targetProfile = (testRoot.replace("test-classes/", "profiles/") + "b" + System.currentTimeMillis() + "/");
				execString = "cp -R " + userProfile + " " + targetProfile;
			}
			
			try{
				Runtime.getRuntime().exec(execString);
				log("Browser Profile copied to " + targetProfile);
			} catch(Exception e){
				log("Error copying profile to target folder! Chrome may start in wrong condition.");
			}
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--user-data-dir=" + targetProfile, "--disable-prerender-local-predictor");// , "--incognito");
			options.addArguments("--start-maximized");
			driver = new ChromeDriver(options);
		}
		if(browserName.matches(".*iexplore.*")){
			if( ! System.getProperty("os.name").toLowerCase().contains("windows")){
				log("It appears you are not running IE related test on Windows OS, please double check!");
				throw new RuntimeException("IE test can only be performed on Windows!");
			}
			log("Open IExplore...");
			System.setProperty("webdriver.ie.driver", testDataRoot + "common\\browserProfiles\\drivers\\IEDriverServer.exe");
			DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			driver = new InternetExplorerDriver(capabilities);
		}

		main_window = driver.getWindowHandle();
		driver.manage().deleteAllCookies();

	}

	private String getBrowserProfileProperty(String profileType){

		return getBrowserProfileProperty(profileType, "");
	}

	private String getBrowserProfileProperty(String profileType, String folderName){

		String returnStr = "";

		if(folderName.isEmpty())
			folderName = getProperty("browser." + getProperty("application.stackName") + ".profile");
		returnStr = testDataRoot + "common" + getInitialProperty(profileType) + "/" + folderName;
		File profileFolder = new File(returnStr);

		if( ! profileFolder.exists() || folderName.isEmpty()){
			returnStr = returnStr.substring(0, returnStr.length() - folderName.length()) + "default";
			File defaultProfile = new File(returnStr);
			if(defaultProfile.exists()){
				log("Browser Profile not found for " + appUrl + ", use default profile instead.", 3);
				log("Browser Profile used: " + returnStr);
			} else{
				log("Browser Profile not found for " + appUrl + ", use local profile instead.", 3);
				log("Browser Profile used: Local profile!");
			}
		} else{
			log("Browser Profile used: " + returnStr);
		}

		return returnStr;
	}

	/**
	 * Click on an element using java script.
	 * 
	 * @param jsScript
	 *            - the java java script that you want to execute .
	 */
	public void clickElement(String jsScript){

		JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
		String js = null;
		if( ! jsScript.contains("=")){
			throw new AssertionFailedError("The format of the navigate element is incorrect.");
		}
		if(jsScript.contains("id")){

			jsScript = jsScript.split("=")[1];
			js = "document.getElementById('" + jsScript + "').click()";
			jsExecutor.executeScript(js);
		}

		else if(jsScript.contains("tagname")){

			jsScript = jsScript.split("=")[1];
			js = "document.getElementsByTagName('" + jsScript + "').click()";
			jsExecutor.executeScript(js);
		} else{
			throw new AssertionFailedError("The parameter jsScript should only be 'id' or 'tagname'.");
		}
	}

	/**
	 * Move the mouse over the specified element. 
	 * 
	 * NOTE: Situation of this method using:Element will be displayed when the mouse moves up
	 * 
	 * @param elementName
	 *            - the name of valid Selenium locator string in uiMap .
	 */
	public void hover(String elementName){

		waitForArea(getElementLocator(elementName), Long.parseLong(elementTimeout));
		RemoteWebElement element = (RemoteWebElement)getElement(elementName);
		log("Moving mouse to element: " + elementName + ".");

		Actions actions = new Actions(driver);
		actions.moveToElement(element).build().perform();
	}

	/**
	 * Close the current browser session and clear the
	 * framework history. Then start a new session.
	 */
	public void restartBrowser(){

		log("Restarting the browser session.");
		close();
		clearHistory();

		startSeleniumServerAndBrowser();
		log("Restarted.");
	}

	/**
	 * Clear the data structure of uiMap.
	 * 
	 * NOTE: Objects' data will be clear which are existed
	 * in'uiMapViewList','uiMapAreasAlreadyChecked','uiMapViewIndex'.
	 */
	public void clearHistory(){

		uiMapViewList.clear();
		uiMapAreasAlreadyChecked.clear();
		uiMapViewIndex = - 1;
	}

	/**
	 * Press the Esc key.
	 */
	public void pressEscKey(){

		SmartRobot robot = new SmartRobot();
		robot.pressESC();
	}

	/**
	 * Return the URL of the current page.
	 * 
	 * @return URL of the current page.
	 */
	public String getCurrentUrl(){

		return driver.getCurrentUrl();
	}

	/**
	 * Get the value of a the given attribute of the element. Will return the
	 * current value, even if this has been modified after the page has been
	 * loaded. More exactly, this method will return the value of the given
	 * attribute, unless that attribute is not present, in which case the value
	 * of the property with the same name is returned (for example for the
	 * "value" property of a textarea element). If neither value is set, null is
	 * returned. The "style" attribute is converted as best can be to a text
	 * representation with a trailing semi-colon. The following are deemed to be
	 * "boolean" attributes, and will return either "true" or null:
	 * 
	 * async, autofocus, autoplay, checked, compact, complete, controls,
	 * declare, defaultchecked, defaultselected, defer, disabled, draggable,
	 * ended, formnovalidate, hidden, indeterminate, iscontenteditable, ismap,
	 * itemscope, loop, multiple, muted, nohref, noresize, noshade, novalidate,
	 * nowrap, open, paused, pubdate, readonly, required, reversed, scoped,
	 * seamless, seeking, selected, spellcheck, truespeed, willvalidate
	 * 
	 * Finally, the following commonly mis-capitalized attribute/property names
	 * are evaluated as expected:
	 * 
	 * <ul>
	 * <li>"class"
	 * <li>"readonly"
	 * </ul>
	 * 
	 * @param name
	 *            The name of the attribute.
	 *            
	 * @return The attribute/property's current value or null if the value is
	 *         not set.
	 */
	public String getAttributeFromElementLocator(String elementName, String attribute){

		WebElement element = getElement(elementName);
		String attributeValue = element.getAttribute(attribute);
		return attributeValue;
	}

	/**
	 * Switch to a specified iframe.
	 * 
	 * @param frameElement
	 *          Name of the iframe you want to switch to.
	 */
	public void switchToIframe(String frameElement){

		WebElement element = getElement(frameElement);
		driver.switchTo().frame(element);
		log("switch to iframe...");
	}

	/**
	 * Click an element in an iframe.
	 * 
	 * @param elementLocator
	 *            - the valid Selenium locator string in uiMap
	 */
	public void clickElementIframe(String elementLocator){

		boolean verify = driver.findElement(By.xpath(elementLocator)).isDisplayed();
		log("verify element if display=" + verify);
		if(verify){
			driver.findElement(By.xpath(elementLocator)).click();
		}
	}

	/**
	 * Get the number of elements's of same style. 
	 * 
	 * For example, the number of photos on the photos page.
	 * 
	 * @param elementName
	 *            - The name of element(s) to count.
	 *            
	 * @return -Number of elements.
	 */
	public int getAllElements(String elementName){

		waitForElement(elementName);

		return getElements(elementName).size();
	}

	/**
	 * Scroll to a particular element on the page.
	 * 
	 * @param elementLocator
	 *            The locator of the element you want to scroll to.
	 */
	public void scrollTo(String elementName){

		waitForElementShown(elementName);
		// ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView()", getElement(elementName));
		WebElement element = getElement(elementName);
		int elementPosition = element.getLocation().getY();
		String js = String.format("window.scroll(0, %s-200)", elementPosition);
		((JavascriptExecutor)driver).executeScript(js);
	}

	/**
	 * Scroll to a particular location near the top of the page.
	 * 
	 * @param height
	 *            The offset in pixels from the top of the page to which you wish to scroll.
	 */
	public void scrollTo(int height){

		String setscroll = "document.documentElement.scrollTop=" + height;
		((JavascriptExecutor)driver).executeScript(setscroll);
	}

	/**
	 * Scroll to a particular element on the page.
	 * 
	 * @param element
	 *            The element you want to scroll to.
	 */
	public void scrollTo(WebElement element){

		((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView()", element);
	}

	/**
	 * Click on an element using javascript.
	 * 
	 * @param elementName
	 * 			The name of the element you wish to click on.
	 */
	public void clickOnUseJS(String elementName){

		// String locator = uiMapElementLocator(elementName);
		WebElement element = getElement(elementName);
		((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
	}

	/**
	 * Scroll to the top of the page.
	 */
	public void scrollToTop(){

		String Js = "var q=document.documentElement.scrollTop=0";
		JavascriptExecutor jsExecutor = (JavascriptExecutor)driver;
		jsExecutor.executeScript(Js);

	}
}
