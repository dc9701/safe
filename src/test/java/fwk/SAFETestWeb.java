package fwk;

import tools.commonTools.CommonTools;
import base.WebApp;

public class SAFETestWeb extends WebApp {

	public SAFETestWeb() {
		this("");
	}

	public SAFETestWeb(String SUT) {
		this(SUT,"");
		
	}

	public SAFETestWeb(String SUT, String profileName) {
		super(SUT, profileName);
		
	}

	protected String getAppName(){

		return "SAFETest";
	}
	
	protected String getAppUrl(){
		return "file://"+testConfigRoot+"SAFETest/target/";
	}
	
}
