package test.SAFETest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import fwk.SAFETestWeb;

/**
 * Test cases designed to check if isElementEnabled, waitForElementEnabled, isElementShown, waitForElementShown, waitForElementNotShown works as expected.
 *
 */
public class TestElementStatus{

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

	/**
	 * Test isElementEnabled, waitForElementEnabled.
	 * 
	 */
	@Test
	public void TestElementEnabled(){

		// Check the initial status, button1 should be enabled, button2 should be disabled.
		if(ui.isElementEnabled("enabledButton"))
			ui.log("Found Button1 is Enabled, pass");
		if( ! ui.isElementEnabled("disabledButton"))
			ui.log("Found Button2 is Disabled, pass");

		// Click on Switch button, button1 should become disabled right away, button2 should become enabled after 10 sec.
		ui.clickOn("enabledSwitch");
		ui.waitByTimeout(2000);
		if( ! ui.isElementEnabled("enabledButton"))
			ui.log("Found Button1 is Disabled now, pass");
		ui.waitForElementEnabled("disabledButton");
		if(ui.isElementEnabled("disabledButton"))
			ui.log("Waited and Found Button2 is Enabled in 30 sec, pass");
		ui.verifyValueOf("enabledStatus", ui.isElementEnabled("enabledButton") + " " + ui.isElementEnabled("disabledButton"));

	}

	/**
	 * Test isElementShown, waitForElementShown, waitForElementNotShown.
	 */
	@Test
	public void TestElementVisible(){

		// Checking initial status, button1 should be visible, button2 should be hidden.
		if(ui.isElementShown("visibleButton"))
			ui.log("Found Button1 is visible, pass");
		if( ! ui.isElementShown("invisibleButton"))
			ui.log("Found Button2 is invisible, pass");

		// click Switch button, button1 should be hidden right away, button2 should be displayed after 10 sec.
		ui.clickOn("visibleSwitch");
		ui.waitByTimeout(2000);
		if( ! ui.isElementShown("visibleButton"))
			ui.log("Found Button1 is invisible now, pass");
		if(ui.waitForElementShown("invisibleButton"))
			ui.log("Waited and Found Button2 is visible in 30 sec, pass");
		ui.verifyValueOf("visibleStatus", ui.isElementShown("visibleButton") + " " + ui.isElementShown("invisibleButton"));

		// click Switch button again, button1 should appear immediately, button2 should be hidden after 10 sec.
		ui.clickOn("visibleSwitch");
		ui.waitByTimeout(2000);
		if(ui.isElementShown("visibleButton"))
			ui.log("Found Button 1 is visible now, pass");
		if(ui.waitForElementNotShown("invisibleButton"))
			ui.log("Waited and Found Button2 is invisible in 30 sec, pass");
		ui.verifyValueOf("visibleStatus", ui.isElementShown("visibleButton") + " " + ui.isElementShown("invisibleButton"));

	}

}
