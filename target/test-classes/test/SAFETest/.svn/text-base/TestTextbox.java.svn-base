package test.SAFETest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import fwk.SAFETestWeb;

public class TestTextbox{

	SAFETestWeb ui;

	@BeforeTest
	public void beforeTest(){

		ui = new SAFETestWeb();
		ui.openApp();
	}

	@AfterTest
	public void afterTest(){

		ui.close();
	}

	@Test
	public void TestSetValue(){

		if(ui.verifyValueOf("textboxResult", "")){
			ui.log("The text box is null");
		}
		ui.setValueTo("testTextbox", "TestValue");
		ui.clickOn("testTextboxSubmit");
		ui.waitByTimeout(2000);
		if(ui.verifyValueOf("textboxResult", "TestValue")){
			ui.log("Found the value is set in textbox, pass");
		}
	}

}
