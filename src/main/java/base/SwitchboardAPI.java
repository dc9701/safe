package base;

import org.apache.commons.lang.StringUtils;

import tools.commonTools.CommonTools;
import base.core.TestClass;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class SwitchboardAPI extends TestClass{

	private static JsonNode soapUiConfig = jsonMapper.createObjectNode();

	protected String getAppName(){

		return "";
	}

	protected String getAppType(){

		return "WS";
	}

	protected String getTestType(){

		return "REST";
	}

	protected SwitchboardAPI(){

		this("");
	}

	protected SwitchboardAPI(String SUT){

		super(SUT);
		prepareTestEnvironment();
	}

	@Override
	protected void platformSupportInitiate(String profile){

		/*
		 * JsonNode ws = getConfProperty(getWsFullFileName(getProperty("conf.ws")));
		 * ObjectNode temp = jsonMapper.createObjectNode();
		 * CommonTools.mergeProperties(temp, sut, ws);
		 * sut = (JsonNode)temp;
		 */
		prepareTestEnvironment();
	}

	/**
	 * Convenience method for getting test property file.
	 * 
	 */
	protected String getTestPropertyFile(){

		String stackWS = getProperty("conf.ws");
		return testConfigRoot + getAppName() + "/ws/" + "/" + StringUtils.defaultIfEmpty(CommonTools.getConfigValue(soapUiConfig, stackWS), stackWS);
	}
}
