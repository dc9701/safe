package tools.commonTools;

/**
 * This class provides common utilities for the framework.
 * 
 * e.g. TestRoot, initialConfig, sut, we need to use these parameters in different places, writing them in different files is complicated 
 * and we can put them here, whenever we need to use them we just need to call the static method form this class "CommonTools" 
 * 
 * To prevent from memory waste, this class is designed to have no static parameters and all methods are static.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import base.core.TestClass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CommonTools{

	private static ObjectMapper jsonMapper = new ObjectMapper();

	/**
	 * Get the value of a config parameter either from a JsonNode object or a system environment variable (if specified).
	 * 
	 * Environment variables, and JsonNode object properties can contain tags (delimited by "{{" and "}}") which will be
	 * replaced with the current value of the variable of the same name. Config jsonNode objects are searched in the
	 * following order for the specified variable(s): 1. testData, 2. sut, 3. initialConfig. For example:
	 * 
	 * disney.app.url = {{application.url}}/{{application.locale}}/#papdetails/12.2/1.0.0/1/
	 * 
	 * @param configObject
	 *            - The JsonNode config object containing the requested parameter.
	 * 
	 * @return - prop containing the contents of the config file.
	 * 
	 */
	public static String getConfigValue(JsonNode configObject, String propertyName){

		/*
		 * If the specified propertyName exists as an environment variable;
		 * if so, use it; otherwise, search the specified configObject.
		 */

		String returnValue = StringUtils.defaultString(System.getenv(propertyName));

		if(StringUtils.isBlank(returnValue)){
			JsonNode targetNode = configObject.path(propertyName);
			returnValue = StringUtils.defaultString(targetNode.textValue());
		}

		/*
		 * If the value includes {{tags}}, replace them with the values of the specified
		 * properties. If can't find the property, replace {{property.name}} with "".
		 */

		while(returnValue.contains("{{") && returnValue.contains("}}")){

			String embeddedPropertyName = StringUtils.substringBetween(returnValue, "{{", "}}");
			String embeddedPropertyValue = getConfigValue(configObject, embeddedPropertyName);

			returnValue = StringUtils.replace(returnValue, ("{{" + embeddedPropertyName + "}}"), embeddedPropertyValue);
		}

		return returnValue;
	}

	/**
	 * Get data from a json or properties config file and return a JsonNode
	 * Object.
	 * 
	 * If the config file is not a json file it is parsed as a Properties file, and the JsonNode will be a flat
	 * representation of the properties file; otherwise, it will represent the contents of the json file.
	 * 
	 * Both json and properties files can contain tags (delimited by "{{" and "}}") which will be replaced with the
	 * current value of the variable of the same name. Config jsonNode objects are searched in the following order
	 * for the specified variable(s): 1. testData, 2. sut, 3. initialConfig. For example:
	 * 
	 * disney.app.url = {{application.url}}{{application.locale}}#papdetails/12.2/1.0.0/1/
	 * 
	 * @param configFileName
	 *            - The config file; is recognized as Properties by the file
	 *            extention (*.properties), not by content.
	 * 
	 * @param testClass
	 *            - The java class of the current test so we know how to traverse the folder hierarchy.
	 *            May be blank if an absolute path was specified or we just want to look in resources/data.
	 * 
	 * @return - prop containing the contents of the config file.
	 */

	public static JsonNode getDataFromConfigFile(String configFileName, String testClass){

		boolean fileFound = true;
		String fileName = configFileName;
		String testRoot = CommonTools.getTestRoot();

		JsonNode jsonNode = jsonMapper.createObjectNode();

		/*
		 * If the specified config file doesn't exist (because it doesn't have a complete path), then
		 * traverse the folder hierarchy searching for it. The folder hierarchy starts with testRoot +
		 * current test classpath + /data, then works up to testRoot/test/cases/data. If not found,
		 * it looks finally in testRoot/data.
		 * NOTE: testClass will look something like "test.cases.BAT.testName", so we parse it via "."
		 */

		if( ! (new File(fileName)).exists()){

			fileFound = false;

			while( ! (StringUtils.isBlank(testClass) || fileFound)){

				testClass = StringUtils.substring(testClass, 0, StringUtils.lastIndexOf(testClass, "."));

				fileName = testRoot + StringUtils.replace(testClass, ".", "/") + "/data/" + configFileName;
				fileFound = (new File(fileName)).exists();
			}
		}

		try{

			/*
			 * First try parsing the config file as JSON.
			 */

			try{

				jsonNode = jsonMapper.readValue(new File(fileName), JsonNode.class);
			}

			catch(Exception e){

				/*
				 * If parsing as JSON failed, try parsing as a properties file.
				 */

				Properties pro = new Properties();
				FileInputStream fis = new FileInputStream(fileName);
				InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
				BufferedReader brProp = new BufferedReader(isr);
				pro.load(brProp);
				brProp.close();
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode node = mapper.createObjectNode();

				for(Entry<Object, Object> element:pro.entrySet()){

					/*
					 * Strip out any html tags and then un-escape any html
					 * entities in the value so browserbot can compare properly
					 * to text returned from the DOM.
					 */

					node.put(element.getKey().toString(), StringEscapeUtils.unescapeHtml(element.getValue().toString().replaceAll("\\<.*?>", "")));
				}

				jsonNode = (JsonNode)node;
			}
		} catch(IOException e){
			throw new IllegalStateException("Can't locate config file " + fileName, e);
		}

		return jsonNode;
	}

	/**
	 * Get data from a json or properties config file and return a JsonNode
	 * Object.
	 * 
	 * If the config file is not a json file it is parsed as a Properties file, and the JsonNode will be a flat
	 * representation of the properties file; otherwise, it will represent the contents of the json file.
	 * 
	 * @param configFileName
	 *            - The config file; is recognized as Properties by the file
	 *            extention (*.properties), not by content.
	 * 
	 * @return - prop containing the contents of the config file.
	 */

	public static JsonNode getDataFromConfigFile(String configFileName){

		return getDataFromConfigFile(configFileName, "");
	}

	/**
	 * Get the current time.
	 * 
	 * @return - String indicates the current time with format "HH:mm:ss.SSS".
	 */

	public static String getCurrentTime(){

		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss.SSS");
		String time = f.format(today);

		return time;
	}

	/**
	 * Get the current Date.
	 * 
	 * @return - String indicates the current Date with format "yyyy-MM-dd".
	 */

	public static String getDate(){

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(date);
	}

	/**
	 * Get full path of the test ROOT.
	 * 
	 * @return - full path of the test ROOT.
	 */

	public static String getTestRoot(){

		/*
		 * This is the root location to search for resources and tools.
		 * NOTE: getPath() returns a path beginning with "/C:/..." on Windows, so we strip the first char if needed.
		 */

		String testRoot = StringUtils.defaultString(Thread.currentThread().getContextClassLoader().getResource(".").getPath());

		if(StringUtils.contains(testRoot, ":/"))
			testRoot = StringUtils.substring(testRoot, 1);

		return testRoot;
	}

	/**
	 * Get contents of initialConfig file.
	 * 
	 * @return - Properties object includes all contents of the initialConfig file.
	 */

	public static JsonNode getInitialConfig(){

		String initialConfigFile = StringUtils.defaultString(System.getenv("SeleniumConfigFile"), "conf/AutotestConfig.properties");

		String configFile = getTestRoot().replace("target/test-classes/", "") + "src/test/resources/" + initialConfigFile;

		JsonNode initialConfig = getDataFromConfigFile(configFile);

		return initialConfig;
	}

	public static JsonNode getInitialConfig(String initialConfigFileName){

		String configFile = getTestRoot().replace("target/test-classes/", "") + "src/test/resources/" + initialConfigFileName;

		JsonNode initialConfig = getDataFromConfigFile(configFile);

		return initialConfig;
	}

	/**
	 * Check the proxy settings in initialConfig file.
	 */
	public static void configProxySettings(){

		/*
		 * Config if the test is running with proxy.
		 */
		if(getConfigValue(CommonTools.getInitialConfig(), "useProxy").equalsIgnoreCase("true")){

			Properties systemProperties = System.getProperties();
			systemProperties.setProperty("http.proxyHost", "web-proxy.houston.hp.com");
			systemProperties.setProperty("http.proxyPort", "8080");
			systemProperties.setProperty("https.proxyHost", "web-proxy.houston.hp.com");
			systemProperties.setProperty("https.proxyPort", "8080");
		}
	}

	public static void configProxySettings(TestClass testClass){

		/*
		 * Config if the test is running with proxy.
		 */
		if(testClass.getInitialProperty("useProxy").equalsIgnoreCase("true")){
			Properties systemProperties = System.getProperties();
			systemProperties.setProperty("http.proxyHost", "web-proxy.houston.hp.com");
			systemProperties.setProperty("http.proxyPort", "8080");
			systemProperties.setProperty("https.proxyHost", "web-proxy.houston.hp.com");
			systemProperties.setProperty("https.proxyPort", "8080");
		}
	}

	/**
	 * Replace the illegal characters in file name.
	 * 
	 * @param fileName
	 *            The file name.
	 * 
	 * @param newChar
	 *            The replace char.
	 * 
	 * @return - the new filename without illegal character.
	 */

	public static String replaceIllegalFileName(String fileName, String newChar){

		if(fileName != null){

			fileName = fileName.replace("\\", newChar);
			fileName = fileName.replace("/", newChar);
			fileName = fileName.replace(":", newChar);
			fileName = fileName.replace("*", newChar);
			fileName = fileName.replace("?", newChar);
			fileName = fileName.replace("\"", newChar);
			fileName = fileName.replace("<", newChar);
			fileName = fileName.replace(">", newChar);
			fileName = fileName.replace("|", newChar);
		}

		return fileName;
	}

	public static JsonNode merge(JsonNode mainNode, JsonNode updateNode){

		Iterator<String> fieldNames = updateNode.fieldNames();
		while(fieldNames.hasNext()){

			String fieldName = fieldNames.next();
			JsonNode jsonNode = mainNode.get(fieldName);
			// if field exists and is an embedded object
			if(jsonNode != null && jsonNode.isObject()){
				merge(jsonNode, updateNode.get(fieldName));
			} else{
				if(mainNode instanceof ObjectNode){
					// Overwrite field
					JsonNode value = updateNode.get(fieldName);
					if(jsonNode != null){
						if(jsonNode.isArray() && value.isArray()){
							String temp1 = jsonNode.toString();
							String temp2 = value.toString();
							temp1 = temp1.substring(1, temp1.length() - 1);
							temp2 = temp2.substring(1, temp2.length() - 1);

							try{
								((ObjectNode)mainNode).put(fieldName, jsonMapper.readValue("[" + temp1 + "," + temp2 + "]", JsonNode.class));
							} catch(Exception e){

							}

						} else{
							TestClass.log("Update Value of \"" + fieldName + "\" from \"" + jsonNode.textValue() + "\" to \"" + value.textValue() + "\".", 1);
							((ObjectNode)mainNode).put(fieldName, value);
						}
					} else
						((ObjectNode)mainNode).put(fieldName, value);

				}

			}

		}
		return mainNode;
	}

	public static JsonNode mergeProperties(JsonNode target, JsonNode...extraProperties){

		for(JsonNode currentContent:extraProperties){

			if(currentContent != null){
				target = (ObjectNode)merge(target, currentContent);
			}
		}
		return target;
	}

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

	public Element analysisXml(String xmlSource){

		Element root;
		SAXReader saxReader = new SAXReader();
		try{
			InputSource src = new InputSource(new StringReader(xmlSource));
			Document document = saxReader.read(src);
			root = document.getRootElement();
		} catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("xmlSource is not exist!");
		}
		return root;
	}

}