package test.SAFETest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import fwk.SAFETestWeb;

public class TestButtonClick{

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
	public void TestButtonClicked(){

		if(ui.verifyValueOf("buttonResult", "")){
			ui.log("The button is not clicked");
		}

		ui.clickOn("testButton");
		ui.waitByTimeout(2000);
		if(ui.verifyValueOf("buttonResult", "button clicked")){
			ui.log("Found ButtonTest is clicked, pass");
		}

	}
}
