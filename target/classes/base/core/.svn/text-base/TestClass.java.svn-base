/*
 * History
 * Date Ver Author Change Description
 * ----------- --- ------------- ----------------------------------------
 * 21 Feb 2014 001 Karl Create.
 * 1. Generally speaking, same as the old version HpcTestUI class.
 * 2. To prevent possible impact to old the framework, we make a separate copy
 * of HpcTestUI and make changes on it.
 * 3. Remove unnecessary static variable definition, and convert them to instance level
 * 4. Split big method into separate small methods to increase readability.
 * 5. Make current framework to support configure multiple web application.
 * 27 Feb 2014 002 Karl SoapUi used log4j, and log4j prints debug info in console. Disable it.
 * Move web related methods to WebApp and keep only common methods for reading properties, etc.
 */
package base.core;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.Reporter;

import tools.commonTools.CommonTools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for exercising an application, device or REST interfaces.
 * 
 * Provides methods for getting test properties from configuration files
 * and console logging capabilities.  All other functionality is in TestUiClass.
 */
public abstract class TestClass{

	static{
		disableLog4jLogs();
	}
	/*
	 * Default config filenames. Locates initialConfig file by the environment variable
	 * SeleniumConfigFile; if not set, uses the default conf/initialConfig.properties.
	 */
	private static String initialConfigFilePath = StringUtils.defaultString(System.getenv("SeleniumConfigFile"), "conf/AutotestConfig.properties");
	protected static String testRoot = CommonTools.getTestRoot();
	protected static String testConfigRoot;
	protected static String testDataRoot;
	protected String sutFileName = "";

	/*
	 * Test parameters from Initial config file.
	 */
	private static String projectLevelSutConfigPath = "";
	protected static String projectLevelBrowserConfigPath = "";
	protected static String projectLevelUiConfigPath = "";

	protected String pageTimeout = "30000";
	protected String viewTimeout = "20000";
	protected String elementTimeout = "15000";
	protected String playbackSpeed = "200";

	/*
	 * Test parameters from SUT config file.
	 */
	protected String appUrl = "";
	protected String appApiUrl = "";
	protected String appLocale = "";

	protected static ObjectMapper jsonMapper = new ObjectMapper();
	/*
	 * Content, SUT and UI Map json objects read from config files.
	 */
	protected static JsonNode initialConfig = jsonMapper.createObjectNode();
	protected JsonNode sut = jsonMapper.createObjectNode();

	protected final static String UIMAP_DELIM = ":";

	/*
	 * The target which is under test. It could be "lifesaver", "instantInk", "lifeAndroid", etc
	 * This name
	 */
	protected abstract String getAppName();

	protected abstract String getAppType();

	protected TestClass(){

		this("", "");
	}

	protected TestClass(String SUT){

		this(SUT, "");
	}

	protected TestClass(String SUT,String profileName){

		log(">==================================================<");
		try{
			ITestResult it = Reporter.getCurrentTestResult();
			log("Now Starting: " + it.getTestClass().getName());
		} catch(Exception e){
			log("No testNG executor detected; test may continue, but consider migrating to testNG.", 3);
		}
		frameworkInitiate(SUT);

	}

	private void frameworkInitiate(String indicatedSutFileName){

		testConfigRoot = testRoot.replace("target/test-classes/", "src/test/resources/conf/");
		testDataRoot = testRoot.replace("target/test-classes/", "src/test/resources/data/");
		initialConfig = CommonTools.getDataFromConfigFile(testRoot + initialConfigFilePath);

		projectLevelSutConfigPath = testConfigRoot + "[appname]/" + getInitialProperty("path.conf.sut");
		sutFileName = StringUtils.defaultIfEmpty(indicatedSutFileName, getDefaultSutFileName());
		sut = CommonTools.getDataFromConfigFile(getSutFullFileName(sutFileName));
		getAdditionalConf(sut);

	}

	private void getAdditionalConf(JsonNode sut){

		Iterator<String> fieldNames = sut.fieldNames();

		String appName = getAppName();
		appName = (StringUtils.isBlank(appName)?"":appName + "/");

		String fieldName;
		String path;
		JsonNode additionalConf = jsonMapper.createObjectNode();
		JsonNode temp;

		while(fieldNames.hasNext()){
			fieldName = fieldNames.next();

			if(fieldName.contains("conf.") && (fieldName.split("\\.").length == 2)){
				path = testConfigRoot + appName + StringUtils.defaultIfEmpty(getInitialProperty("path.conf." + fieldName.substring(5)), fieldName.substring(5))
						+ "/" + sut.get(fieldName).textValue();
				temp = getConfProperty(path);
				additionalConf = CommonTools.mergeProperties(additionalConf, temp);
			}
		}
		sut = CommonTools.mergeProperties(sut, additionalConf);
	}

	protected void languageSupportInitiate(){

		// throw new RuntimeException("languageSupportInitiate must be override in language support level classes.");
	}

	protected void platformSupportInitiate(String profileName){

		// throw new RuntimeException("platformSupportInitiate must be override in platform support level classes.");

	}

	protected void applicationSupportInitiate(){

		// throw new RuntimeException("applicationSupportInitiate must be override in application support level classes.");
	}

	/**
	 * Private function to load property files based on the lookup string in sut file.
	 * If a property file does not exist, will log a warning in the console and skip it.
	 * 
	 * @param fileName
	 *            String to lookup the property file name in sut file.
	 * @return
	 *         JsonNode created based on the property.
	 */

	protected JsonNode getConfProperty(String fileName){

		JsonNode returnNode = jsonMapper.createObjectNode();
		File checkFile = new File(fileName);
		if(checkFile.exists() && checkFile.isFile())
			returnNode = CommonTools.getDataFromConfigFile(fileName);
		else{
			log("Cannot locate config file at " + fileName + ". Will try continue without it.", 3);
			returnNode = null;
		}
		return returnNode;
	}

	/**
	 * Gets the value of a property from the initialConfig properties json object.
	 * 
	 * The initialConfig json object is created from conf/AutotestConfig.properties by default;
	 * a different initialConfig file may be specified in the SeleniumConfigFile environment variable. 
	 * 
	 * For example, if your properties file contains:
	 * 
	 * 		path.conf = conf/
	 * 
	 * Then the return value of getInitialProperty("path.conf") will be "conf/".
	 * 
	 * NOTE: Calls CommonTools.getConfigValue() to obtain the property value; see getConfigValue()
	 * for details concerning precedence (between config files, values specified in test cases,
	 * and environment variables), as well as property tags.
	 * 
	 * @param property
	 * 			The name of the property, such as "path.conf".
	 * 
	 * @return The value of the specified property.
	 */
	public String getInitialProperty(String property){

		return CommonTools.getConfigValue(initialConfig, property);
	}

	private String getDefaultSutFileName(){

		String sutFileKey = String.format("sut.%s.%s.file", getAppName(), getAppType());

		sutFileKey = StringUtils.replace(sutFileKey, "..", ".", - 1);

		String rtn = getInitialProperty(sutFileKey);
		if(rtn == null || StringUtils.isEmpty(rtn)){
			System.out.println("!Warning: can't find any key " + sutFileKey + " in the initial config file. ");
		}
		return rtn;
	}

	protected String getSutPath(){

		String appName = getAppName();

		return projectLevelSutConfigPath.replace("[appname]/", (StringUtils.isBlank(appName)?"":appName + "/"));
	}

	protected String getCurrentSutFullFileName(){

		return getSutPath() + sutFileName;
	}

	protected String getSutFullFileName(String SUT){

		String fileName = StringUtils.defaultString(SUT, getDefaultSutFileName());
		return getSutPath() + fileName;
	}

	/**
	 * Gets the value of a property from the sut (system under test) properties json object.
	 * 
	 * The sut json object is created from the sut file specified in initialConfig, plus the
	 * contents of all config files specified in the sut file. 
	 * 
	 * For example, if your properties file contains:
	 * 
	 * 		path.conf = conf/
	 * 
	 * Then the return value of getProperty("path.conf") will be "conf/".
	 * 
	 * NOTE: Calls CommonTools.getConfigValue() to obtain the property value; see getConfigValue()
	 * for details concerning precedence (between config files, values specified in test cases,
	 * and environment variables), as well as property tags.
	 * 
	 * @param property
	 * 			The name of the property, such as "path.conf".
	 * 
	 * @return The value of the specified property.
	 */
	public String getProperty(String propertyKey){

		return CommonTools.getConfigValue(sut, propertyKey);
	}

	protected void prepareTestEnvironment(){

	}

	/**********************************************************************************************
	 * Accessor methods for variables we want to use in test cases. The values
	 * are read from the src/test/resources/data config file or, in the case of
	 * newUser, created at the beginning of the test run.
	 **********************************************************************************************/

	/**
	 * Get the application URL.
	 * 
	 * @return The URL of the application that is being tested.
	 */
	protected String getAppUrl(){

		return appUrl;
	}

	/**
	 * Get the application API URL.
	 * 
	 * @return The URL of the application that is being tested.
	 */
	protected String getAppApiUrl(){

		return appApiUrl;
	}

	/**
	 * Get the specified sub Node in a JSON object.
	 * 
	 * @param nodesToSearch
	 *            json - JsonObject you want to get from.
	 * @param fieldName
	 *            fieldName - The name of the field which is used to be the
	 *            filter here.
	 * @param regex
	 *            regex - The value of the field which is used to be the filter
	 *            here.
	 * 
	 * @return - The value of the specified node or empty node if not found.
	 */
	protected JsonNode getJsonNodeMatching(JsonNode nodesToSearch, String fieldName, String regex){

		for(JsonNode currentNode:nodesToSearch){

			if(StringUtils.isNotEmpty(currentNode.path(fieldName).textValue()) && StringUtils.isNotEmpty(regex)){

				if(StringUtils.isNotEmpty(currentNode.path(fieldName).textValue()) && currentNode.path(fieldName).textValue().matches(regex)){

					return currentNode;
				}
			}
		}

		return jsonMapper.createObjectNode(); // Should return isMissingNode() = true.
	}

	/**
	 * Log info to Console by different types.
	 * 
	 * @param content
	 *            Message to log to the Console.
	 * @param type
	 *            1: Normal INFO 2: ERROR 3: WARNING (show in black) 4: WARNING (show in red)
	 */
	public static void log(String content, Integer type){

		switch(type){
		case 1:{
			System.out.println(CommonTools.getCurrentTime() + " (" + Thread.currentThread().getId() + ") INFO - " + content);
			break;
		}
		case 2:{
			System.err.println(CommonTools.getCurrentTime() + " (" + Thread.currentThread().getId() + ") ERROR - " + content);
			break;
		}
		case 3:{
			System.out.println(CommonTools.getCurrentTime() + " (" + Thread.currentThread().getId() + ") WARNING - " + content);
			break;
		}
		case 4:{
			System.err.println(CommonTools.getCurrentTime() + " (" + Thread.currentThread().getId() + ") WARNING - " + content);
			break;
		}
		}

	}

	/**
	 * Log standard info to Console.
	 * 
	 * @param content
	 *            Message to log to the Console.
	 */
	public void log(String content){

		log(content, 1);
	}

	private static void disableLog4jLogs(){

		@SuppressWarnings("unchecked")
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for(Logger logger:loggers){
			logger.setLevel(Level.OFF);
		}
	}

}
