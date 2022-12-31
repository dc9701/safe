package tools.vpm;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tools.commonTools.CommonTools;
import tools.vpm.apiTools.ApiRequest;
import tools.vpm.apiTools.ApiResponse;
import tools.vpm.apiTools.ContentType;
import tools.vpm.PrinterRegistration.SupportedPrinterFamily;
import tools.vpm.StackInfo.Stack;
import tools.vpm.VirtualPrinter.VirtualPrinterStatus;

import com.fasterxml.jackson.databind.JsonNode;

public class PrinterManager{

	private String printRegistServer;
	public static List<VirtualPrinter> printerPool = new ArrayList<VirtualPrinter>();
	String regXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><registerPrinter><stack>%s</stack><modelName>%s</modelName><modelNumber>%s</modelNumber><duration>%s</duration><registerTime>%s</registerTime><runOptin>false</runOptin><owner>{{vpOwner}}</owner><ownerPassword>{{vpPass}}</ownerPassword></registerPrinter>";
	String disconnectXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><printerManagement><dropConnection><time>%s</time></dropConnection></printerManagement>";
	String vPMDataRoot;
	String vPMConfigRoot;
	JsonNode vPMConfig;

	Stack stackName;

	static{
			disableLog4jLogs();
		}
	
	private static void disableLog4jLogs(){

		@SuppressWarnings("unchecked")
		List<Logger> loggers = Collections.<Logger>list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for(Logger logger:loggers){
			logger.setLevel(Level.OFF);
		}
	}

	public PrinterManager(Stack stackName, String vpOwner, String vpPass){
		regXML=regXML.replace("{{vpOwner}}", vpOwner).replace("{{vpPass}}", vpPass);
		vPMDataRoot = CommonTools.getTestRoot().replace("target/test-classes/", "src/test/resources/data/vpm/");
		vPMConfigRoot = CommonTools.getTestRoot().replace("target/test-classes/", "src/test/resources/conf/vpm/");
		vPMConfig = CommonTools.getDataFromConfigFile(vPMConfigRoot + "default.properties");
		VirtualPrinterPool.init(vPMDataRoot);
		this.stackName = stackName;
	}

	public synchronized void createPrinterPool(int threadCount){

		List<Thread> arr = new ArrayList<Thread>();

		if(printerPool.size() == 0){
			for(int i = 0; i < threadCount; i ++ ){
				PrinterThread myThread = new PrinterThread(this);
				myThread.start();
				arr.add(myThread);
				try{
					Thread.sleep(600);
				} catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}

		// All threads waiting for the end.
		for(Thread t:arr){
			try{
				t.join();
			} catch(InterruptedException e1){
				e1.printStackTrace();
			}
		}

		System.out.println(">>>>>>>>>>                          INFO - Loading printers ......                          <<<<<<<<<<");

		File file = new File(vPMDataRoot + "virtualPrinterPool.txt");

		// If the file exists before we first create, delete and create it.
		if( ! file.exists()){
			try{
				file.createNewFile();
			} catch(IOException e){
				e.printStackTrace();
			}
		} else{
			try{
				file.delete();
				file.createNewFile();
			} catch(IOException e){
				e.printStackTrace();
			}
		}

		for(VirtualPrinter currentPrinter:printerPool){
			VirtualPrinterPool.savePrinterToPool(currentPrinter);
		}
	}

	public void savePrinterToPool(VirtualPrinter printer){

		VirtualPrinterPool.savePrinterToPool(printer);
	}

	public VirtualPrinter createPrinter(){

		return createPrinter(null, null);
	}

	public VirtualPrinter createPrinter(VirtualPrinterStatus status){

		return createPrinter(null, status);
	}

	public VirtualPrinter createPrinter(SupportedPrinterFamily printerFamily, VirtualPrinterStatus status){

//		VirtualPrinter printer = VirtualPrinterPool.getPrinterFromPool(printerFamily, status, stackName);
//
//		if(printer != null){
//
//			System.out.println("Get Printer : \" " + printer.getEmailID() + " \" From the printer pool!");
//			return printer;
//		} else{
			return registerNewVirtualPrinter(printerFamily, status, stackName);
//		}

	}

	public VirtualPrinter registerNewVirtualPrinter(SupportedPrinterFamily printerFamily, VirtualPrinterStatus status, Stack stack){

		if(printerFamily == null){
			printerFamily = SupportedPrinterFamily.MORGANI;
		}
		if(status == null){
			status = VirtualPrinterStatus.ONLINE;
		}

		VirtualPrinter printer = new VirtualPrinter();

		ApiResponse response = null;

		for(int i = 0; i < 2; i ++ ){
			try{
				printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server1");
				response = registerPrinter(PrinterRegistration.getPrinterRegistration(printerFamily, 7200), stack);
				if(String.valueOf(response.getStatus()).startsWith("20")){
						//|| (String.valueOf(response.getStatus()).equals("412") && response.getResponseBody().split(" ")[0].contains("@"))){
					break;
				}
			} catch(Exception e){
			}

			try{
				printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server2");
				response = registerPrinter(PrinterRegistration.getPrinterRegistration(printerFamily, 7200), stack);
				if(String.valueOf(response.getStatus()).startsWith("20")){
						//|| (String.valueOf(response.getStatus()).equals("412") && response.getResponseBody().split(" ")[0].contains("@"))){
					break;
				}
			} catch(Exception e){
			}
			if(i == 1){

				printer = null;
				assertTrue(" ERROR - Register Printer failed! ", false);
			}
		}

		String responseBody = response.getResponseBody();

		if(String.valueOf(response.getStatus()).startsWith("20")){

			printer.setStack(stackName.toString());
			printer.setModelNumber(getResponseValue(responseBody, "modelNumber"));
			printer.setPrinterActiveTime(getResponseValue(responseBody, "printerActiveTime"));
			printer.setEmailID(getResponseValue(responseBody, "printerEmailId"));
			printer.setPrinterId(getResponseValue(responseBody, "printerId"));
			printer.setClaimCode(getResponseValue(responseBody, "printerEmailId").substring(0, getResponseValue(responseBody, "printerEmailId").indexOf("@")));
			printer.setPrinterKey(getResponseValue(responseBody, "printerKey"));
			printer.setPrinterSetUID(getResponseValue(responseBody, "printerSetUID"));
			printer.setPrinterState(getResponseValue(responseBody, "printerState"));
			printer.setPrinterType(getResponseValue(responseBody, "printerType"));
			printer.setResponseTime(getResponseValue(responseBody, "responseTime"));
			printer.setSerialNumber(getResponseValue(responseBody, "serialNumber"));
			printer.setXmpp(getResponseValue(responseBody, "xmpp"));

		}

//		if(String.valueOf(response.getStatus()).equals("412")){
//
//			String[] printerInfo = responseBody.split(" ");
//			printer.setStack(stackName.toString());
//			printer.setModelNumber(PrinterRegistration.getPrinterRegistration(printerFamily, 7200).getModelNumber());
//			printer.setPrinterState(status.toString().replace("ONLINE", "Connected").replace("OFFLINE", "Disconnected"));
//			printer.setEmailID(printerInfo[0]);
//			printer.setPrinterId(printerInfo[1]);
//			printer.setClaimCode(printerInfo[0].split("@")[0]);
//
//		}

		if(status == VirtualPrinterStatus.OFFLINE){
			dropConnection(printer.getEmailID());
			printer.setPrinterState("Disconnected");
		}

		return printer;
	}

	/**
	 * If papi.printerSimulator.is.custom.stack in the SUT property file equals "true" then register
	 * the printer against the custom stack. Else register the printer to the stack.
	 * 
	 * @param printer
	 *            The printer registration object used to register the printer.
	 * @return Return the response data.
	 */
	private ApiResponse registerPrinter(PrinterRegistration printer, Stack stack){

		boolean isCustomStack = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.is.custom.stack").equalsIgnoreCase("true");

		String requestBody;
		if(isCustomStack){
			String customStackXMLPath = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.custom.XML.path");
			String customStackXMLName = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.custom.XML.name");
			String customXML = getXMLString(customStackXMLPath + customStackXMLName);
			requestBody = String.format(customXML, CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.custom.stack.offramp.id"),
					CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.custom.stack.onramp.id"), printer.getModelName(), printer.getModelNumber(),
					"43200", "180000");
		} else{
			requestBody = String.format(regXML, stack, printer.getModelName(), printer.getModelNumber(), "43200", "180000");
		}

		return ApiRequest.POST(printRegistServer).path("/PSRestUtils/v/1.0/printer/register").header("Content-type", "application/xml").body(requestBody)
				.execute();
	}

	public ApiResponse dropConnection(String printerEmailId){

		// String dropConnectionPayLoads = FileUtils.loadFileAsString(ProjectConfig.getRequestDataPath() + "dropConnection.xml");
		return ApiRequest.POST(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/management")
				.header("Content-type", ContentType.APPLICATION_XML).body(String.format(disconnectXML, "43200")).execute();
	}

	public ApiResponse removePrinter(String printerEmailId){

		ApiResponse response = null;
		printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server1");
		response = ApiRequest.DELETE(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/unregister").execute();
		if(response.getStatus() == 500){
			printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server2");
			response = ApiRequest.DELETE(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/unregister").execute();
		}
		return response;
	}

	public String getResponseValue(String responseBody, String name){

		return responseBody.substring(responseBody.indexOf("<" + name + ">") + name.length() + 2, responseBody.indexOf("</" + name + ">"));
	}

	public ApiResponse setPrinterStatus(String printerEmailId, ProductStatusCap productStatusCap, boolean wait){

		ApiResponse response = null;
		String printerStatusPayLoads = String.format(regXML, productStatusCap.StatusCategory, productStatusCap.StringId);
		if(printRegistServer == null){

			printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server1");
			response = ApiRequest.POST(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/productstatusdyn")
					.header("Content-type", ContentType.APPLICATION_XML).body(String.format(printerStatusPayLoads, "43200")).execute();
			if(response.getStatus() == 500){
				printRegistServer = CommonTools.getConfigValue(vPMConfig, "papi.printerSimulator.server2");
				response = ApiRequest.POST(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/productstatusdyn")
						.header("Content-type", ContentType.APPLICATION_XML).body(String.format(printerStatusPayLoads, "43200")).execute();
			}

		} else{

			response = ApiRequest.POST(printRegistServer).path("/PSRestUtils/v/1.0/printer/" + printerEmailId + "/productstatusdyn")
					.header("Content-type", ContentType.APPLICATION_XML).body(String.format(printerStatusPayLoads, "43200")).execute();

		}

		if(String.valueOf(response.getStatus()).startsWith("2")){
			if(wait){

				System.out.println("=========================================================================");
				System.out.println("      Now, waiting for the printer status change - '120 seconds'");
				System.out.println("=========================================================================");

				try{
					Thread.sleep(120000);
				} catch(Exception e){
				}
			}
		}

		return response;
	}

	private String getXMLString(String filePath){

		String lineTxt = "";
		String returnValue = "";
		try{
			File file = new File(filePath);
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader bufferedReader = new BufferedReader(read);
			while((lineTxt = bufferedReader.readLine()) != null){
				lineTxt = lineTxt.trim();
				if( ! lineTxt.isEmpty()){
					returnValue += lineTxt;
				}
			}
			read.close();
		} catch(Exception e){
			throw new RuntimeException(e);
		}
		return returnValue;

	}

}

class PrinterThread extends Thread{

	private PrinterManager printerManager;

	public PrinterThread(PrinterManager printerManager){

		this.printerManager = printerManager;
	}

	public void run(){

		PrinterManager.printerPool.add(printerManager.registerNewVirtualPrinter(null, null, printerManager.stackName));
	}
}
