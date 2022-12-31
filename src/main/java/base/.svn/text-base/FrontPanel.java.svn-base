package base;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import tools.commonTools.CommonTools;
import tools.commonTools.sendLEDCommand;
import base.core.TestUiClass;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("deprecation")
public abstract class FrontPanel extends TestUiClass{

	private sendLEDCommand request;
	private static String contentType = "text/xml";

	private String deviceip;
	private String uriDomain;

	protected FrontPanel(){

		this("");

	}

	protected FrontPanel(String SUT){

		this(SUT, "");
	}

	protected FrontPanel(String SUT,String profileName){

		super(SUT, profileName);
		platformSupportInitiate(profileName);

	}

	protected String getAppName(){

		return "";
	}

	protected String getAppType(){

		return "FrontPanel";
	}

	@Override
	protected void platformSupportInitiate(String profileName){

		prepareTestEnvironment();
	}

	/**
	 * Open a connection with the device and initialize the UI map.
	 * 
	 * @return true if the home screen is found; otherwise, return false.
	 */
	@Override
	public boolean openApp(){

		sendUDWCommand("test_service.security_off");
		boolean validate = uiMapSetView("");

		if( ! validate)
			throw new RuntimeException("Load initial UI map failed!");

		return validate;
	}

	/**
	 * Send an "underware" (UDW) command to the device.
	 * 
	 * @param UDWcommand
	 * 			The UDW command to be sent to the device.
	 */
	@SuppressWarnings("resource")
	public void sendUDWCommand(String UDWcommand){

		String command = getProperty(UDWcommand);
		command = (command.isEmpty()?UDWcommand:command) + ";";
		try{
			HttpClient client = new DefaultHttpClient();
			String uri = "http://" + deviceip + "/UDW";
			HttpPost post = new HttpPost(uri);
			HttpResponse getSession = client.execute(post);
			int getResponseCode = getSession.getStatusLine().getStatusCode();
			if(getResponseCode != 200 && getResponseCode != 201)
				log("Unable to open underware session!", 2);
			post.releaseConnection();

			StringEntity entity = new StringEntity(command);
			String location = getSession.getLastHeader("Location").getValue().replace(":80", "");
			if(location.isEmpty())
				throw new RuntimeException("Underware session POST response did not contain 'location' in response headers");
			HttpPut put = new HttpPut(location);
			put.addHeader("Content-Type", "application/octet-stream");
			put.setEntity(entity);
			log("Send underware command '" + command + "'.");
			HttpResponse sendUDW = client.execute(put);

			int putResponseCode = sendUDW.getStatusLine().getStatusCode();
			if(putResponseCode != 200)
				log("Send underware command '" + command + "' failed!", 2);
			put.releaseConnection();

		} catch(Exception e){
			throw new RuntimeException("Send Underware command '" + command + "' failed!");
		}
	}

	@Override
	protected void waitForArea(){

		String defaultAreaElement = (uiMapCurrentArea.path("elements").path(0).path("elementName").textValue());
		if( ! checkElement(defaultAreaElement)){
			takeFullScreenShot(defaultAreaElement);
			throw new RuntimeException("Set the device to home page and then try again.");
		}

	}

	@Override
	protected String getCurrentAreaisDisplayed(String view, long timeOut){

		String[] viewArray = StringUtils.split(view, "[\",]");

		for(String thisPageAndView:viewArray){

			String thisPage = StringUtils.split(thisPageAndView, ":")[0];
			String thisView = StringUtils.split(thisPageAndView, ":")[1];

			for(JsonNode currentPage:uiMap.path("application").path("pages")){

				if(StringUtils.equalsIgnoreCase(currentPage.path("properties").path("pageName").textValue(), thisPage)){

					for(JsonNode currentView:currentPage.path("views")){
						String actualView = StringUtils.defaultString(currentView.path("viewName").textValue());
						if(StringUtils.equalsIgnoreCase(actualView, thisView)){
							String defaultArea = StringUtils.defaultString(currentView.path("defaultArea").textValue());
							for(JsonNode currentArea:currentPage.path("areas")){
								if(StringUtils.equals(currentArea.path("areaName").textValue(), defaultArea)){
									view = thisPageAndView;
									JsonNode currentElements = currentArea.path("elements");
									String defaultAreaElement = currentElements.path(0).path("elementName").textValue();
									uiMapCurrentPage = currentPage;
									uiMapCurrentView = currentView;
									uiMapCurrentArea = currentArea;
									uiMapCurrentElement = currentElements;
									if(checkElement(defaultAreaElement)){
										break;
									} else{
										log("The Element is not found: '" + defaultAreaElement + "' in the view " + view);
										return "";
									}
								}
							}

							if( ! StringUtils.isEmpty(view))
								break;
						}
					}

					if( ! StringUtils.isEmpty(view))
						break;
				}
			}

			if( ! StringUtils.isEmpty(view))
				break;
		}
		return view;
	}

	/**
	 * Get the ID of the device being tested.  This is obtained from the device config file,
	 * not the device iteself.
	 * 
	 * @param deviceID
	 * 			A specific device ID.
	 * 			
	 * @return The device ID.
	 */
	public String getDeviceID(String deviceID){

		return deviceID;
	}

	/**
	 * Get the ID of the device being tested.  This is obtained from the device config file,
	 * not the device itself.
	 * 
	 * @return The device ID.
	 */
	public String getDeviceID(){

		String deviceID = getProperty("device.aio.id");
		if(deviceID.isEmpty())
			throw new RuntimeException("Please set device ID for test");
		else
			return deviceID;
	}

	/**
	 * Get the IP address of the device being tested.  This is obtained from the device config file,
	 * not the device itself.
	 * 
	 * @param deviceip
	 * 			A specific device IP address.
	 * 			
	 * @return The device IP address.
	 */
	public String getDeviceIP(String deviceip){

		this.deviceip = deviceip;
		uriDomain = "http://" + deviceip;
		log("The device IP is '" + this.deviceip + "'.");

		return deviceip;

	}

	/**
	 * Get the IP address of the device being tested.  This is obtained from the device config file,
	 * not the device itself.
	 * 
	 * @return The device IP address.
	 */
	public String getDeviceIP(){

		String deviceip = getProperty("device.aio.ip");
		if(deviceip.isEmpty())
			throw new RuntimeException("Please set device IP for test");
		else
			this.deviceip = deviceip;

		return getDeviceIP(deviceip);
	}

	/**
	 * Get the screen info and save it into XML file
	 * 
	 * @return Screen contents as an XML file
	 */
	private File getScreenInfo(){

		String screenInfoUri = uriDomain + "/TestService/UI/ScreenInfo";
		return getXML(screenInfoUri);

	}

	/**
	 * Get the current device status.
	 * 
	 * @return Current device status as an XML file.
	 */
	private File getPrinterStatus(){

		String printerStatus = uriDomain + "/DevMgmt/ProductConfigDyn.xml";
		return getXML(printerStatus);
	}

	/**
	 * Get the device serial number.
	 * 
	 * @return The device serial number.
	 */
	public String getSerialNumber(){

		Document doc = parseXmlFile(getPrinterStatus());

		NodeList nameList = doc.getElementsByTagName("dd:SerialNumber");
		String serialNumber = nameList.item(0).getTextContent();
		log("The device Serial Number is '" + serialNumber + "'.");
		return serialNumber;
	}

	/**
	 * Get LED elements from the device.
	 * 
	 * @param elementName
	 * 			The name of the item you want.
	 * 
	 * @return
	 *         return the element locator
	 */
	private String getLEDLocatorFromXml(String elementName){

		String namelLocator = null;
		Document doc = parseXmlFile(getScreenInfo());

		NodeList nameList = doc.getElementsByTagName("LEDName");
		for(int i = 0; i < nameList.getLength(); i ++ ){
			namelLocator = nameList.item(i).getTextContent();
			if(elementName.equalsIgnoreCase(namelLocator))
				break;
		}
		return namelLocator;
	}

	/**
	 * Get the widget elements from the device
	 * 
	 * @param elementName
	 * 			The name of the item you want.
	 * 
	 * @return
	 *         return the element locator
	 */
	private String getWidgetLocatorFromXml(String elementName){

		String namelLocator = null;
		Document doc = parseXmlFile(getScreenInfo());
		String elementLocator = getElementLocator(elementName);
		try{
			NodeList nameList = doc.getElementsByTagName("Id");
			for(int i = 0; i < nameList.getLength(); i ++ ){
				namelLocator = nameList.item(i).getTextContent();
				if( ! elementLocator.equalsIgnoreCase(namelLocator))
					continue;
				else
					break;
			}
		} finally{

		}
		return namelLocator;
	}

	/**
	 * Press the back button.
	 */
	public void back(){

		String currentView = uiMapCurrentView.path("viewName").textValue();
		
		if(uiMapViewIndex > 0){
			String viewBeforeBack = uiMapViewList.get(uiMapViewList.size() - 2);
			
			if( ! viewBeforeBack.contains(currentView)){
				
				if(pressLED("back"))
					uiMapUpdated(PREVIOUS_VIEW);
			}
		}

	}

	/**
	 * Tap on the device front panel with coordinates
	 * 
	 * @param x
	 * 
	 * @param y
	 */
	public void tapOn(int x, int y){

		String uri = "http://" + deviceip + "/TestService/UI/ScreenPress";
		request = new sendLEDCommand(uri, "POST");
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test:ScreenPress xmlns:test=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28 ../../schemas/TestService.xsd\"><test:X-Coordinate>"
				+ x + "</test:X-Coordinate><test:Y-Coordinate>" + y + "</test:Y-Coordinate></test:ScreenPress>\n";
		request.body(body).execute();

	}

	/**
	 * Press the specified button on the front panel
	 * 
	 * @param elementName
	 * 			Name of the button to be pressed.
	 * 
	 * @return true if the button is found and pressed successfully.
	 */
	private boolean pressScreen(String elementName){

		verifyIsShown(elementName);
		boolean validate = false;
		log("Click On '" + elementName + "'.");
		String widgetLocator = getElementLocator(elementName);
		String xmlLocator = getWidgetLocatorFromXml(elementName);
		String uri = "http://" + deviceip + "/TestService/UI/ScreenInfo";
		request = new sendLEDCommand(uri, "PUT");
		request.type(contentType);
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test:ScreenInfo xmlns:test=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28 ../../schemas/TestService.xsd\"><test:Widgets><test:Widget><test:Id>"
				+ widgetLocator
				+ "</test:Id><test:Actions><test:Action><test:Type>select</test:Type></test:Action></test:Actions></test:Widget></test:Widgets></test:ScreenInfo>\n";
		
		if(widgetLocator.equalsIgnoreCase(xmlLocator))
			validate = true;
		else
			throw new RuntimeException("Click button failed! : " + elementName);
		
		request.body(body).execute();
		return validate;
	}

	/**
	 * Press the physical key from the front panel.
	 * 
	 * @param LEDName
	 * 			Name of the button to press
	 * 
	 * @return true if the button is found and pressed successfully.
	 */
	private boolean pressLED(String LEDName){

		boolean validate = checkElementInView("gestures", LEDName);
		if(validate){
			String elementName = LEDName;
			String xmlElementName = getLEDLocatorFromXml(LEDName);
			if(elementName.equalsIgnoreCase(xmlElementName))
				validate = true;
			else
				throw new RuntimeException("Press LED button failed! : " + LEDName);

			log("Press '" + LEDName + "' on the Front Panel!");
			String uri = "http://" + deviceip + "/TestService/UI/KeyPress";
			request = new sendLEDCommand(uri, "POST");
			request.type(contentType);
			String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test:KeyPress xmlns:test=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28 ../../schemas/TestService.xsd\"><test:Key>"
					+ elementName + "</test:Key></test:KeyPress>\n";
			request.body(body).execute();
		} else{
			log("LED '" + LEDName + "' seems not available on this page, please check your uiMap.", 2);
		}
		return validate;
	}

	/**
	 * Tap on the element on CGD or LED button around CGD.
	 * 
	 * @param elementName
	 * 			Name of the item to be pressed.
	 * 
	 * @return true if the item is found and pressed successfully.
	 */
	public boolean tapOn(String elementName){

		boolean validate = false;
		// String LEDView = getLEDView(elementName);
		String view = "";
		if(validateLED(elementName)){
			if(validate = pressLED(elementName))
				view = getTargetView("gestures", "", elementName);

		} else{
			if(validate = pressScreen(elementName))
				view = getTargetView("tap", elementName, "");
		}
		if(validate)
			validate = uiMapUpdated(view);
		return validate;
	}

	/**
	 * Tap on the element on CGD or LED button around CGD.
	 * 
	 * @param elementName
	 * 			Name of the item to be pressed.
	 * 
	 * @return true if the item is found and pressed successfully.
	 */
	public boolean clickOn(String elementName){

		return tapOn(elementName);
	}

	/**
	 * Scroll to the next screen on front panel
	 */
	private void swipeLeft(){

		String uri = "http://" + deviceip + "/TestService/UI/ScreenInfo";
		request = new sendLEDCommand(uri, "PUT");
		request.type(contentType);
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test:ScreenInfo xmlns:test=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28 ../../schemas/TestService.xsd\"><test:Widgets><test:Widget><test:Id>home_grid</test:Id><test:Actions><test:Action><test:Type>scrollNext</test:Type></test:Action></test:Actions></test:Widget></test:Widgets></test:ScreenInfo>\n";
		request.body(body).execute();
	}

	/**
	 * Scroll to the prev screen on front panel
	 */
	private void swipeRight(){

		String uri = "http://" + deviceip + "/TestService/UI/ScreenInfo";
		request = new sendLEDCommand(uri, "PUT");
		request.type(contentType);
		String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test:ScreenInfo xmlns:test=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.hp.com/schemas/imaging/con/ledm/testservice/2011/11/28 ../../schemas/TestService.xsd\"><test:Widgets><test:Widget><test:Id>home_grid</test:Id><test:Actions><test:Action><test:Type>scrollPrev</test:Type></test:Action></test:Actions></test:Widget></test:Widgets></test:ScreenInfo>\n";
		request.body(body).execute();
	}

	/**
	 * Swipe the screen in the specified direction.
	 * 
	 * @param direction
	 * 			The direction to swipe (right or left).
	 * 
	 * @return true if the swipe is successful. otherwise, false
	 */
	public boolean swipe(String direction){

		boolean validate = checkElementInView("gestures", direction);
		if(validate){
			if( ! StringUtils.isEmpty(direction)){
				if(direction.equals("left"))
					swipeLeft();
				if(direction.equals("right"))
					swipeRight();
			}
			String view = getTargetView("gestures", "", direction);
			validate = uiMapUpdated(view);
		} else{
			log("It seems we cannot perform swipe action with direction '" + direction + "' on this screen, please check your uiMap.");
			Assert.assertTrue(validate, "It seems we cannot perform swipe action with direction " + direction + " on this screen, please check your uiMap.");
		}
		return validate;

	}

	/**
	 * Looks for content in the xml of the current page.
	 * Will retry after a second, if not found
	 * 
	 * @param contentName
	 * 			The content you want to look for.
	 * 
	 * @return true if the content was found
	 */
	private boolean checkContent(String contentName){

		boolean validate = false;
		int retry = 3;

		String nameLocator = null;
		String targetContent = null;

		while( ! validate && retry > 0){

			Document doc = parseXmlFile(getScreenInfo());
			targetContent = getLocalizedText(contentName);

			NodeList nameList = doc.getElementsByTagName("Value");

			for(int i = 0; i < nameList.getLength(); i ++ ){
				nameLocator = nameList.item(i).getTextContent();

				if(StringUtils.containsIgnoreCase(nameLocator, targetContent)){
					log("The content '" + targetContent + "' was found on the screen!");
					validate = true;
					break;
				}
			}
			retry -- ;
			if( ! validate){
				try{
					Thread.sleep(250);
				} catch(Exception e){
				}
			}
		}
		return validate;
	}

	/**
	 * Looks for an element by name in the xml of the current page.
	 * Will retry after a second, if not found
	 * 
	 * @param elementName
	 * 			The element you want to look for.
	 * 
	 * @return true if the element was found
	 */
	private boolean checkElement(String elementName){

		boolean validate = false;
		String elementLocator = null;
		String locator = getElementLocator(elementName);
		try{
			for(int i = 1; i <= 10; i ++ ){
				elementLocator = getWidgetLocatorFromXml(elementName);
				if( ! elementLocator.equalsIgnoreCase(locator))
					Thread.sleep(250);
				else
					break;
			}
		} catch(Exception e){

		}
		if(elementLocator.equalsIgnoreCase(locator))
			validate = true;

		return validate;
	}

	/**
	 * Verify that the specified element or text is displayed on the screen.
	 * 
	 * @param elementOrContentName
	 * 			The element name or literal text you want to find.
	 * 
	 * @return true if the element or text is found; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsShown(String elementOrContentName){

		boolean validate = false;
		log("Looking for Element in current screen info: '" + elementOrContentName + "'.");
		String locator = getElementLocator(elementOrContentName);
		
		if(locator.isEmpty()){
			if(checkContent(elementOrContentName))
				validate = true;
			else{
				log("'" + elementOrContentName + "' is not found", 4);
				takeFullScreenShot(elementOrContentName);
				throw new RuntimeException("Element not found! " + elementOrContentName);
			}
		} else{
			if(checkElement(elementOrContentName))
				validate = true;
			else{
				log("'" + elementOrContentName + "' is not found", 4);
				takeFullScreenShot(elementOrContentName);
				throw new RuntimeException("Element not found! " + elementOrContentName);
			}
		}

		return validate;
	}

	/**
	 * Verify that the specified element or text is NOT displayed on the screen.
	 * 
	 * @param elementOrContentName
	 * 			The element name or literal text you want to find.
	 * 
	 * @return true if the element or text is NOT displayed; otherwise, logs a failure and returns false.
	 */
	public boolean verifyIsNotShown(String elementName){

		boolean validate = false;
		String locator = getElementLocator(elementName);
		String elementLocator = getWidgetLocatorFromXml(elementName);

		if( ! elementLocator.equalsIgnoreCase(locator) || ! checkContent(elementName)){
			validate = true;
		} else
			throw new RuntimeException("Element is not disappear!" + elementName);

		return validate;

	}

	protected boolean takeFullScreenShot(String fileName){

		boolean validate = false;
		String screenCaptureURL = "http://" + deviceip + "/TestService/UI/ScreenCapture";
		fileName = timeStamp + "_" + CommonTools.replaceIllegalFileName(fileName, "_");
		if(StringUtils.endsWith(fileName, "_"))
			fileName = timeStamp;
		String filePath = screenCapturePath + "/" + fileName + ".png";

		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		try{

			URL url = new URL(screenCaptureURL);
			HttpURLConnection httpUrl = (HttpURLConnection)url.openConnection();
			httpUrl.connect();
			BufferedInputStream bis = new BufferedInputStream(httpUrl.getInputStream());

			FileOutputStream fos = new FileOutputStream(filePath);
			while((size = bis.read(buf)) != - 1){
				fos.write(buf, 0, size);
			}
			fos.flush();
			fos.close();
			bis.close();
			httpUrl.disconnect();
			validate = true;
		} catch(Exception e){
		}
		return validate;
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
	 * - If no successful path is found, return false	 * 
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
	 */	public boolean setViewTo(String requestedView){

		boolean setViewTo = uiMapSetView(requestedView);
		if( ! setViewTo)
			throw new RuntimeException("Target view \"" + requestedView + "\" not found!");
		return setViewTo;
	}

	protected boolean validateLED(String LEDName){

		JsonNode ledNode;
		try{
			ledNode = getViewAttribute("gestures");
		} catch(NullPointerException e){
			return false;
		}
		if(ledNode.has(LEDName))
			return true;
		else{
			return false;
		}
	}
}
