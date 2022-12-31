package tools.printHistory;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;

import tools.commonTools.CommonTools;
import base.core.TestClass;

import com.fasterxml.jackson.databind.JsonNode;

public class SESSendEmail {
	
	
	private JsonNode initialConfig;
	private JsonNode sut;
	private String testRoot;
	private String defaultEmail;
	private String emailBodyFile;
	private Long emailDelay;
	private String basePath;
	private Object baseUrl;
	private String server;
	
	private static TrustManager[]                 trustAllCerts                = null;
    private static HostnameVerifier               allHostsValid                = null;
       
    private static final String                   XML_HEADER                   = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
	private static final String                   SEND_EMAIL_REQUEST           = XML_HEADER
	                                                                                       + "<emailRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
	                                                                                       + "<subject>%s</subject>"
	                                                                                       + "<body>%s</body>"
	                                                                                       + "<to>%s</to>"
	                                                                                       + "<from>%s</from>"
	                                                                                       +"<replyTo></replyTo>"
	                                                                                       +"</emailRequest>";

	private static final String CONTENT_TYPE_HEADER = "Content-type";
	private static final String APP_XML_CONTENT_TYPE_UTF_8 = "application/xml; charset=\"utf-8\"";
	
	public SESSendEmail() {
		
        initialConfig = CommonTools.getInitialConfig();
        sut = CommonTools.getInitialConfig();
		testRoot = CommonTools.getTestRoot();

        server=CommonTools.getConfigValue(sut, "papi.emailManagement.server");
		basePath = CommonTools.getConfigValue(initialConfig,"papi.sendEmail.basePath");
        baseUrl = String.format("%s/%s", server, basePath);
		
		defaultEmail =CommonTools.getConfigValue(initialConfig, "test.user.defaultEmail").replaceAll("\"", "");
		
		emailBodyFile=(testRoot + CommonTools.getConfigValue(initialConfig, "path.tools.printhistory.body")).replace("/", "\\");
		emailDelay = Long.valueOf(StringUtils.defaultString(CommonTools.getConfigValue(sut, "sesEmail.delay"),
				StringUtils.defaultString(CommonTools.getConfigValue(initialConfig,"sesEmail.delay"), "30000")));
		 setTrustManager();
		
	}
	
	public void sendEmail(String from, String to, String[] bcc, String[] cc,
			String subject, String body, String attachments, int copies){
		
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String read = "";
		URLConnection conn;
		
		// Sender and recipient

		if (from.isEmpty()) {

			from=defaultEmail;
		} 
		
		try {
			br = new BufferedReader(new FileReader(emailBodyFile));
		} catch (FileNotFoundException e1) {

			e1.printStackTrace();
		}

		try {
			read = br.readLine();
			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
		} catch (IOException e) {

			e.printStackTrace();

		}
		
		 String xmlPayload = String.format(SEND_EMAIL_REQUEST, subject, sb,to, from);
		 
		 System.out.println(CommonTools.getCurrentTime() + " INFO - ================ Running Send Email Request ================");
	     System.out.println(String.format("Email Request - url: %s - payload: %s",String.format("%s"+"/html", baseUrl), xmlPayload));
		 
		for (int index = 0; index < copies; index++) {
			
        if(attachments=="eng.html"){
        	
		 conn = createRequest(String.format("%s"+"/html", baseUrl),"POST");	
		 
        }
        else {
        	
       	     conn = createRequest(String.format("%s"+"/text", baseUrl),"POST");	
        }
		 
	     Map<String, String> response = executePostRequest(conn, xmlPayload);  
	     
	        if (response.get("responseStatus").startsWith("20")) {

				System.out.println("===================================================================================");
				System.out.println("      Sending Email Successful!");
				System.out.printf("      Now, waiting for the print jobs to proceed - %d seconds\n", emailDelay/1000);
				System.out.println("===================================================================================");
				
				try {
					Thread.sleep(emailDelay);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	        
	}
	  
	private URLConnection createRequest(String tempUrl, String httpMethod) {

		try {
			
			 // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(tempUrl);
			URLConnection conn = url.openConnection();
			HttpURLConnection http = (HttpURLConnection) conn;
			http.setRequestMethod(httpMethod);
			http.setRequestProperty(CONTENT_TYPE_HEADER,
					APP_XML_CONTENT_TYPE_UTF_8);
			http.setConnectTimeout(10000);

			return (URLConnection) http;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}	

    private void setTrustManager() {

            // Create a trust manager that does not validate certificate chains
    	
            trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        // Create all-trusting host name verifier
         allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

    }
	
    private Map<String, String> executePostRequest(URLConnection conn, String xmlPayload) {

        String data = "";

        Map<String, String> response = new HashMap<String, String>();
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        try {
            conn.connect();
            OutputStream os = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(xmlPayload.getBytes());
            dos.close();
            dos.flush();
            InputStream is = conn.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte d[] = new byte[dis.available()];
            dis.read(d);
            dis.close();
            data = new String(d);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        HttpURLConnection httpCon = (HttpURLConnection) conn;
        try {
            response.put("responseBody", data);
            response.put("responseStatus", String.valueOf(httpCon.getResponseCode()));
            response.put("responseMessage", httpCon.getResponseMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return response;

    }
	
}
