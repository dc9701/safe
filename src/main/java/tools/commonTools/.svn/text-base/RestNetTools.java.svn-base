package tools.commonTools;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestNetTools{

	private static ObjectMapper jsonMapper = new ObjectMapper();
	private ProxyTools pt = new ProxyTools();
	private static String responseStr = "";
	private String apiExec = "";

	public RestNetTools(){

	}

	/**
	 * Get the value of string responseStr.
	 * 
	 * @return responseStr.
	 */
	public String getResponseStr(){

		return responseStr;
	}

	public JsonNode getResponseJson(){

		return parseJsonStr(responseStr);
	}

	/**
	 * Send REST request to the url specified.
	 * 
	 * @param url
	 *            Url used to send the request.
	 * @return response code returned. 200 is OK.
	 */
	public int sendGetRequest(String url){

		// url="http://stage1.hplstcs.com/svc/api/getVersion";
		pt.setProxy();
		int responseCode = 0;
		try{

			URL ConnStr = new URL(url);

			HttpURLConnection conn = (HttpURLConnection)ConnStr.openConnection();

			conn.setRequestMethod("GET");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/5.0");

			responseCode = conn.getResponseCode();
			InputStream _is;
			if(responseCode == 200)
				_is = conn.getInputStream();
			else
				_is = conn.getErrorStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(_is));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while((inputLine = in.readLine()) != null){
				response.append(inputLine);
			}
			in.close();
			log(responseCode + " " + url);
			// System.out.println(response.toString());
			responseStr = response.toString();

		} catch(Exception e){
			log("ERROR:" + e);
			// e.printStackTrace();
		} finally{
			pt.clearProxy();

		}
		return responseCode;
	}

	/**
	 * Upload file specified by fileName to network location specified by url with name of fileOnServer.
	 * 
	 * @param url
	 *            Url of the target Server
	 * @param fileName
	 *            full path filename of the file to be uploaded.
	 * @param fileOnServer
	 *            filename that is going to be created on server.
	 * @return response code returned.
	 */
	public int upload(String url, String fileName, String fileOnServer, String requestType){

		pt.setProxy();
		// System.out.println(url);
		// System.out.println(fileName);
		// System.out.println(fileOnServer);
		int responseCode = 0;

		try{
			String BOUNDARY = "---------7d4a6d158c9";
			URL obj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)obj.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestType);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Apache-HttpClient/4.1.1 (java 1.5)");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", "multipart/mixed; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();

			File file = new File(fileName);
			StringBuilder sb = new StringBuilder();
			sb.append("--");
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"" + fileOnServer + "\";filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");

			byte[] data = sb.toString().getBytes();
			out.write(data);
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while((bytes = in.read(bufferOut)) != - 1){
				out.write(bufferOut, 0, bytes);
			}
			out.write("\r\n".getBytes());
			in.close();

			out.write(end_data);
			out.flush();
			out.close();

			responseCode = conn.getResponseCode();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while((line = reader.readLine()) != null){
				// System.out.println(line);
			}
			log(responseCode + " Upload Done!");

		} catch(Exception e){
			log("Uploading error:" + e);
			// e.printStackTrace();
		} finally{
			pt.clearProxy();

		}
		return responseCode;
	}

	/**
	 * Lifesaver/Rumble only. <br>
	 * Used to perform listDevices request.
	 * 
	 * @param stackurl
	 * @param valtoken
	 * @return true request success. <br>
	 *         false request failed.
	 */
	public boolean listDevices(String stackurl, String valtoken){

		int response = sendGetRequest(stackurl + "svc/api/listDevices?valtoken=" + valtoken);
		apiExec = "listDevices";
		if(response == 200)
			return true;
		else
			return false;
	}

	public boolean accountScrapeInfo(String stackurl, String valtoken){

		int response = sendGetRequest(stackurl + "svc/api/accountScrapeInfo?valtoken=" + valtoken);
		apiExec = "accountScrapeInfo";
		if(response == 200)
			return true;
		else
			return false;
	}

	public boolean searchFiles(String apiUrl, String valtoken, String deviceid, String serviceid, String searchcrit){

		int response = sendGetRequest(apiUrl + "/searchFiles?valtoken=" + valtoken + "&deviceid=" + deviceid + "&serviceid=" + serviceid + "&searchcrit="
				+ searchcrit);
		apiExec = "searchFiles";
		if(response == 200)
			return true;
		else
			return false;

	}

	public boolean listFiles(String apiUrl, String valtoken, String deviceid, String serviceid, String parentid){

		return listFiles(apiUrl, valtoken, deviceid, serviceid, parentid, 0);
	}

	public boolean listFiles(String apiUrl, String valtoken, String deviceid, String serviceid, String parentid, int maxCount){

		int response = 0;
		if(maxCount > 0)
			response = sendGetRequest(apiUrl + "/listFiles?maxcount=" + maxCount + "&valtoken=" + valtoken + "&deviceid=" + deviceid + "&serviceid="
					+ serviceid + "&parentid=" + parentid);
		else
			response = sendGetRequest(apiUrl + "/listFiles?valtoken=" + valtoken + "&deviceid=" + deviceid + "&serviceid=" + serviceid + "&parentid="
					+ parentid);
		apiExec = "listFiles";
		if(response == 200)
			return true;
		else
			return false;
	}

	public boolean createFile(String apiUrl, String valtoken, String deviceid, String serviceid, String parentid, String hddFile){

		int response = sendGetRequest(apiUrl + "/createFile?valtoken=" + valtoken + "&deviceid=" + deviceid + "&serviceid=" + serviceid + "&parentid="
				+ parentid + "&filename=" + hddFile + "&type=0");
		apiExec = "createFile";
		if(response == 200)
			return true;
		else
			return false;
	}

	public boolean removeFile(String apiUrl, String valtoken, String deviceid, String serviceid, String fileid){

		int response = sendGetRequest(apiUrl + "/removeFile?valtoken=" + valtoken + "&deviceid=" + deviceid + "&serviceid=" + serviceid + "&fileid=" + fileid);
		apiExec = "createFile";
		if(response == 200)
			return true;
		else
			return false;
	}

	public boolean uploadFile(String apiUrl, String valtoken, String deviceid, String serviceid, String parentid, String fileid, String fileName,
			String hddFile, String requestType){

		int response = upload(apiUrl.replace("/api", "/files/") + valtoken + "/" + deviceid + "/" + serviceid + "/" + fileid, fileName, hddFile, requestType);
		apiExec = "uploadFile";
		String resString = apiUrl.replace("/api", "/files/") + valtoken + "/" + deviceid + "/" + serviceid + "/" + fileid;
		log(resString);
		if(response == 200)
			return true;
		else
			return false;
	}

	public String getFolderId(String apiUrl, String valtoken, String deviceid, String serviceid, String hddPath){

		String parentid = "0";
		String folderid = "0";
		String hddPathList[] = hddPath.split("/");
		JsonNode resNode = jsonMapper.createObjectNode();
		for(int i = 0; i < hddPathList.length; i ++ ){
			listFiles(apiUrl, valtoken, deviceid, serviceid, parentid);
			try{
				resNode = jsonMapper.readValue(responseStr, JsonNode.class);
			} catch(Exception e){
			}
			for(JsonNode current:resNode.path("files")){
				if(current.path("filename").textValue().equalsIgnoreCase(hddPathList[i]) && current.path("type").textValue().equals("1")){
					folderid = current.path("fileid").textValue();
					break;
				}

			}
			if(parentid.equals(folderid)){
				log("Folder \"" + hddPathList[i] + "\" not found on HDD, Full path required: " + hddPath);
				throw new RuntimeException("Folder \"" + hddPathList[i] + "\" not found on HDD, Full path required: " + hddPath);
			} else
				parentid = folderid;

		}
		log("The folder id for \"" + hddPath + "\" is " + parentid);
		return parentid;
	}

	public JsonNode parseJsonStr(String jsonStr){

		try{
			return jsonMapper.readValue(responseStr, JsonNode.class);
		} catch(Exception e){

		}
		return null;
	}

	public String getDeviceId(){

		if( ! apiExec.equalsIgnoreCase("listDevices")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		return resNode.path("devices").path(0).path("deviceid").textValue();
	}

	public String getServiceId(){

		if( ! apiExec.equalsIgnoreCase("listDevices")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		return resNode.path("devices").path(0).path("services").path(0).path("serviceid").textValue();
	}

	public String getApiUrl(){

		if( ! apiExec.equalsIgnoreCase("listDevices")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		String apiUrl = resNode.path("devices").path(0).path("services").path(0).path("apiurl").textValue();
		if(apiUrl.endsWith("/"))
			apiUrl = apiUrl.substring(0, apiUrl.length() - 1);
		return apiUrl;
	}

	public String getAccounttype(){

		if( ! apiExec.equalsIgnoreCase("accountScrapeInfo")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		return resNode.path("accounts").path(0).path("accounttype").textValue();
	}

	public String getAccountid(){

		if( ! apiExec.equalsIgnoreCase("accountScrapeInfo")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		return resNode.path("accounts").path(0).path("accountid").textValue();
	}

	public String getFileId(){

		if( ! apiExec.equalsIgnoreCase("listFiles")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		String fileId = "";
		String fileSize = "";
		for(int i = 0; i < resNode.path("files").size(); i ++ ){
			fileId = resNode.path("files").path(i).path("fileid").textValue();
			fileSize = resNode.path("files").path(i).path("size").textValue();
			if(fileSize.equals("0")){
				fileId = "";
				continue;
			} else
				return fileId;
		}
		if(fileId.isEmpty())
			throw new RuntimeException("There is no valid file to share!");
		return fileId;

	}

	public String getNewFileId(){

		if( ! apiExec.equalsIgnoreCase("createFile")){
			return "";
		}
		JsonNode resNode = parseJsonStr(responseStr);
		return resNode.path("file").path("fileid").textValue();
	}

	protected static void log(String content, Integer type){

		switch(type){
		case 1:{
			System.out.println(CommonTools.getCurrentTime() + " INFO - " + content);
			break;
		}
		case 2:{
			System.err.println(CommonTools.getCurrentTime() + " ERROR - " + content);
			break;
		}
		case 3:{
			System.out.println(CommonTools.getCurrentTime() + " WARNING - " + content);
			break;
		}
		case 4:{
			System.err.println(CommonTools.getCurrentTime() + " WARNING - " + content);
			break;
		}
		}

	}

	public void log(String content){

		log(content, 1);
	}

	/*********************************************************************/
	/********** Instant Ink 2.0 **** Interaction with Rails_Admin **********/
	/*********************************************************************/

	// Codes/Parameters for obtaining the accessToken for Instant Ink
	String geminiUsername = "gemini";
	String geminiPassword = "inksub2";
	String superUserPUCToken = "c3VwcG9ydGFnZW50dXNlcmlkOnN1cHBvcnRhZ2VudHNlY3JldA==";
	String hisePUCToken = "MTEyN2JkN2E2ZTc2MTAyZDkyYjAxMjMxM3JzZXdwbWVnOjExMjdiZGEyLTZlNzYtMTAyZC05MmIwLTEyMzEzcnNld3BtZWc=";
	String accessToken = "";
	String accountEmail = "";
	String userID = "";
	int responseCode = 0;
	int stackdown = 0;
	int impersonate = 1;

	// JSON Strings
	String userInfo = "";
	String subscriptionInfo = "";
	String emailLogs = "";

	public RestNetTools(String epcID){

		requestUserID(epcID);
		accessToken = getImpersonationAccesToken(userID);

	}

	public String getImpersonationAccesToken(String userId){

		String inputLine = "";
		String url = "https://webauth-stage1.hpconnectedstage.com" + "/oauth/puma/token?" + "grant_type=client_credentials&" + "client_id=AUTH_REFERRER&"
				+ "uid=" + userId + "&" + "impersonate=true";
		pt.setProxy();
		try{
			URL myURL = new URL(url);

			log("Starting HPPT request using this url: " + url);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			String basicAuth = "Basic " + superUserPUCToken;

			log("Setting Authorization as : " + basicAuth);

			myURLConnection.setRequestProperty("Authorization", basicAuth);
			myURLConnection.setRequestMethod("POST");

			responseCode = myURLConnection.getResponseCode();
			InputStream _is;

			// Recording the responce from the server
			if(responseCode == 200)
				_is = myURLConnection.getInputStream();
			else
				_is = myURLConnection.getErrorStream();

			// Building the Document that contains the XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(_is);

			doc.getDocumentElement().normalize();

			// Parsing the accessToken out of the XML
			NodeList nodeList = doc.getElementsByTagName("token");
			Node node = nodeList.item(0);
			Element element = (Element)node;
			inputLine = element.getElementsByTagName("accessToken").item(0).getTextContent();
			log("Obtained AccessToken as: " + inputLine);
			return inputLine;

		} catch(Exception e){
			log("ERROR:" + e);
		}
		return inputLine;
	}

	public String sendInstantInkGetRequest(String url){

		pt.setProxy();
		try{
			URL myURL = new URL(url);

			log("Starting HPPT: GetUserBasicInformation using url: " + url);

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
			String bearerAuth = "Bearer " + accessToken;

			log("Setting Authorization as : " + bearerAuth);

			myURLConnection.setRequestProperty("Authorization", bearerAuth);
			myURLConnection.setRequestProperty("Accept", "text/*");
			myURLConnection.setRequestMethod("GET");

			responseCode = myURLConnection.getResponseCode();
			InputStream _is;
			if(responseCode == 200)
				_is = myURLConnection.getInputStream();
			else
				_is = myURLConnection.getErrorStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(_is));

			String inputLine;
			StringBuffer response = new StringBuffer();

			while((inputLine = in.readLine()) != null){
				response.append(inputLine);
			}
			in.close();
			log("Responce Code from HPPT Request: " + responseCode);
			// System.out.println(response.toString());
			return response.toString();

		} catch(Exception e){
			log("ERROR:" + e);
			// e.printStackTrace();
		} finally{
			pt.clearProxy();

		}
		return "ERROR";
	}

	/**
	 * TODO: obtain the UserId(epcid) using created hpc email
	 * need to obtain permission from devops to use api commant or
	 * request one to be developed
	 * 
	 * @param aEmail
	 * @throws ParserConfigurationException
	 */
	public boolean requestUserID(String aEmail){

		pt.setProxy();
		try{
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element credential = doc.createElement("credential");
			doc.appendChild(credential);

			Element email = doc.createElement("emailAddress");
			email.appendChild(doc.createTextNode(aEmail));
			credential.appendChild(email);

			Element password = doc.createElement("password");
			password.appendChild(doc.createTextNode("aio1test"));
			credential.appendChild(password);

			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);

			String XMLpayload = writer.toString();

			System.out.println(XMLpayload);

			URL myURL = new URL(
					"https://pam-stage1.hpconnectedstage.com/pam/services/pam/login?clientId=1127bd7a6e76102d92b012313dedp6cd&emailVerified=true&verifyEmail=true");

			log("Starting HPPT: POST getting EPCID");

			HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();

			myURLConnection.setDoOutput(true);
			myURLConnection.setRequestProperty("connection", "Keep-Alive");
			myURLConnection.setRequestProperty("Accept", "text/xml");
			myURLConnection.setRequestProperty("Content-Type", "application/xml");
			myURLConnection.setRequestProperty("Host", "pam-test1.hpconnectedtest.com");
			myURLConnection.setRequestMethod("POST");

			OutputStreamWriter writer1 = new OutputStreamWriter(myURLConnection.getOutputStream());
			writer1.write(XMLpayload);
			writer1.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

			responseCode = myURLConnection.getResponseCode();

			System.out.println("MY responceCode for Getting userID = " + responseCode);

			String inputLine;
			StringBuffer response = new StringBuffer();

			while((inputLine = reader.readLine()) != null){
				response.append(inputLine);
			}
			writer.close();
			reader.close();

			userID = StringUtils.substringBetween(response.toString(), "<userId>", "</userId>");

			return true;
		} catch(Exception e){
			log("ERROR:" + e);
			// e.printStackTrace();
		} finally{
			pt.clearProxy();

		}
		return false;
	}

	/**
	 * TODO: obtain health logs
	 * need to ask devops if there is an api/or request one to be developed
	 * 
	 * or pull from https://ink-status.hp-webplatform.com/
	 * 
	 * @param stackUrl
	 * @param tokens
	 */
	public boolean requestHealthLogs(String stackUrl, String tokens){

		return false;
	}

	/**
	 * TODO: recieve the User's basic inforamtion using the users id
	 * 
	 * api used : {root}/api/users/{userid}
	 * 
	 * Authorization Header : Impersonate user PUC
	 * 
	 * Response Code : 200 OK
	 * : 401 Unauthorized
	 * : 404 Resource does not exist
	 * : 50x Server error
	 * 
	 * Json return : subscriptionId
	 * : cloudPrinterID
	 * : deviceImmutableId
	 * : subscriptionState
	 * : accountStatus
	 * 
	 * 
	 */
	public boolean requestUserBasicInfo(){

		String url = "https://gemini:inksub2@instantink-stage1.hpconnectedstage.com/api/users/" + userID;
		userInfo = sendInstantInkGetRequest(url);
		if(responseCode == 200)
			return true;
		else
			return false;
	}

	/**
	 * TODO: receive the printer's Subscription information using the cloud printer id
	 * 
	 * api used : {root}/api/subscription/{cloudPrinterId}
	 * 
	 * Authorization Header : PUC
	 * 
	 * Response Code : 200 OK
	 * : 401 Unauthorized
	 * : 404 Resource does not exist
	 * : 50x Server error
	 * 
	 * Json return : apiVersion
	 * : userId
	 * : subscriptionId
	 * : cloudPrinterID
	 * : deviceImmutableId
	 * : cyclePageCount
	 * : lastCloudPrinterUpdateTime
	 * : subscriptionState
	 * : accountStatus
	 * : notifications
	 * : cartridge Shipments
	 * : planInfo
	 * : rolloverPages
	 * 
	 * 
	 * @param tokens
	 */

	public boolean requestSubscriptionInfo(String cloudPrinterID){

		String url = "https://gemini:inksub2@instantink-stage1.hpconnectedstage.com/api/subscriptions/" + cloudPrinterID;
		subscriptionInfo = sendInstantInkGetRequest(url);
		if(responseCode == 200)
			return true;
		else
			return false;
	}

	public boolean requestSubscriptionInfoBySubNumber(String subNumber){

		String url = "https://gemini:inksub2@instantink-stage1.hpconnectedstage.com/api/users/subscriptions/" + subNumber;
		subscriptionInfo = sendInstantInkGetRequest(url);
		if(responseCode == 200)
			return true;
		else
			return false;
	}

	/**
	 * TODO: get Email Logs from user Id
	 * 
	 * api used : {root}/api/users/emails/{userId}?start={startDate}&end={endDate}&offset={num}&limit={num}
	 * 
	 * Authorization Header : PUC
	 * 
	 * Response Code : 200 OK
	 * : 401 Unauthorized
	 * : 404 Resource does not exist
	 * : 50x Server error
	 * 
	 * Json return : list of emails
	 * 
	 * 
	 * 
	 */
	public boolean requestEmailLogs(){

		String url = "https://gemini:inksub2@instantink-stage1.hpconnectedstage.com/api/users/emails/" + userID;
		emailLogs = sendInstantInkGetRequest(url);
		if(responseCode == 200)
			return true;
		else
			return false;
	}

	public String getUserBasicInfo(){

		return userInfo;
	}

	public String getSubscriptionInfo(){

		return subscriptionInfo;
	}

	public String getEmailLogs(){

		return emailLogs;
	}

}
