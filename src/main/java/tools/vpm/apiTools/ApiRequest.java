package tools.vpm.apiTools;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import tools.commonTools.CommonTools;
import tools.commonTools.XmlFormatter;

/**
 * Class to make HTTP calls.
 * 
 */
public class ApiRequest{

	private HttpClient client = null;
	private StringEntity entity = null;
	private HttpResponse response = null;

	private String method = "";
	private String uri = "";
	private String path = "";
	private String param = "";
	private String fragment = "";
	private String body = "";
	private Map<String, String> headers = new HashMap<String, String>();

	private int connection_timeout = 0;
	private int so_timeout = 0;

	private static final int CONNECTION_TIMEOUT = 240000;
	private static final int SO_TIMEOUT = 240000;

	public ApiRequest(String uri,String method){

		this.uri = uri;
		this.method = method;
		try{
			client = new DefaultHttpClient();
		} catch(Exception e){
		}

	}

	/**
	 * Sets the HTTP method to GET and requested URL to the given URI.
	 * 
	 * @param uri
	 *            URI on which a get request will be called.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with URI and HTTP method state set.
	 */
	public static ApiRequest GET(String uri){

		return new ApiRequest(uri, "GET");
	}

	/**
	 * Sets the HTTP method to POST and requested URL to the given URI.
	 * 
	 * @param uri
	 *            URI on which a post request will be called.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with URI and HTTP method state set.
	 */
	public static ApiRequest POST(String uri){

		return new ApiRequest(uri, "POST");
	}

	/**
	 * Sets the HTTP method to PUT and requested URL to the given URI.
	 * 
	 * @param uri
	 *            URI on which a put request will be called.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with URI and HTTP method state set.
	 */
	public static ApiRequest PUT(String uri){

		return new ApiRequest(uri, "PUT");
	}

	/**
	 * Sets the HTTP method to DELETE and requested URL to the given URI.
	 * 
	 * @param uri
	 *            URI on which a delete request will be called.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with URI and HTTP method state set.
	 */
	public static ApiRequest DELETE(String uri){

		return new ApiRequest(uri, "DELETE");
	}

	/**
	 * Adds the string to the URI. It will build the final URI with the correct URI syntax. Any missing '/' will be compensated for when the
	 * final URI is built.
	 * 
	 * @param path
	 *            Part of the URI, that gets the appended to the URI.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given path appended to the URI.
	 */
	public ApiRequest path(String path){

		this.path = path.trim();
		return this;
	}

	/**
	 * Sets the query parameter in the request object.
	 * 
	 * @param param
	 *            query parameter key
	 * @param value
	 *            query parameter value
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given query parameter set in the request.
	 */
	public ApiRequest param(String key, String value){

		if( ! StringUtils.isBlank(this.param)){
			this.param += "&";
		}
		this.param += key.trim() + "=" + value.trim();
		return this;
	}

	/**
	 * Adds the string to the URI. It will append after "#" at the end of URI.
	 * 
	 * @param param
	 *            fragment of the URI, that gets the appended to the URI.
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given fragment appended in the request.
	 */
	public ApiRequest fragment(String fragment){

		this.fragment = fragment.trim();
		return this;
	}

	/**
	 * Sets the content type in the request object.
	 * 
	 * @param mediaType
	 *            content type of the request
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given content type set in the request.
	 */
	public ApiRequest type(String type){

		headers.put("Content-Type", type.trim());
		return this;
	}

	/**
	 * Sets the header parameter in the request object.
	 * 
	 * @param param
	 *            header parameter key
	 * @param value
	 *            header parameter value
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given header parameter set in the request.
	 */
	public ApiRequest header(String headerName, String headerValue){

		headers.put(headerName.trim(), headerValue.trim());
		return this;
	}

	/**
	 * sets the body of the request to given parameter.
	 * 
	 * @param body
	 *            The raw characters that goes into the request. (Based on the content type this could be XML or JSON)
	 * @return
	 *         {@link ApiRequest} the HPCRequest with the given body set in the request.
	 */
	public ApiRequest body(String body){

		this.body = body.trim();
		return this;
	}

	/**
	 * Sets trust all SSL for the request.
	 */
	public ApiRequest setTrustAllSSL(){

		TrustSSLClient.wrapClient(client);
		return this;
	}

	/**
	 * Sets Proxy
	 */
	public ApiRequest setProxy(String hostName, int port){

		HttpHost proxy = new HttpHost(hostName, port);
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		return this;
	}

	/**
	 * Sets Timeout
	 */
	public ApiRequest setTimeout(int connection_timeout, int so_timeout){

		this.connection_timeout = connection_timeout;
		this.so_timeout = so_timeout;
		return this;
	}

	/**
	 * Executes the HTTP call with the parameters specified by the this object.
	 * 
	 * @return
	 *         Returns an instance of HttpResponse.
	 */
	public ApiResponse execute(){

		try{

			// Sets the connect timeout of the request.
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, (connection_timeout != 0)?connection_timeout:CONNECTION_TIMEOUT);
			// Sets the read timeout of the request.
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, (so_timeout != 0)?so_timeout:SO_TIMEOUT);

			if( ! StringUtils.isBlank(path)){
				uri += path;
			}

			if( ! StringUtils.isBlank(param)){
				uri += "?" + param;
			}

			if( ! StringUtils.isBlank(fragment)){
				uri += "#" + fragment;
			}

			if( ! StringUtils.isBlank(body)){
				entity = new StringEntity(body);
			}

			if(method.equals("GET")){
				HttpGet get = new HttpGet(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						get.addHeader(header.getKey(), header.getValue());
					}
				}
				showRequestInfo(get);
				response = client.execute(get);
			} else if(method.equals("POST")){
				HttpPost post = new HttpPost(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						post.addHeader(header.getKey(), header.getValue());
					}
				}
				post.setEntity(entity);
				showRequestInfo(post);
				response = client.execute(post);
			} else if(method.equals("PUT")){
				HttpPut put = new HttpPut(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						put.addHeader(header.getKey(), header.getValue());
					}
				}
				put.setEntity(entity);
				showRequestInfo(put);
				response = client.execute(put);
			} else if(method.equals("DELETE")){
				HttpDelete delete = new HttpDelete(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						delete.addHeader(header.getKey(), header.getValue());
					}
				}
				showRequestInfo(delete);
				response = client.execute(delete);
			}

		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}

		// Read the response body and display it on the console.
		ApiResponse hpcResponse = new ApiResponse(response);
		hpcResponse.loadResponseBody();
		showResponseInfo(hpcResponse);

		// Shut down the HTTP client connection.
		client.getConnectionManager().shutdown();
		return hpcResponse;

	}

	/**
	 * Display the request data on the console.
	 * 
	 * @param request
	 */
	public void showRequestInfo(HttpUriRequest request){

		
		log(">>>>>>Sending "+request.getRequestLine().getMethod()+" request to "+request.getRequestLine().getUri() +" with following content:");
		//log(new Date().toString());
		log("Headers:");
		for(Header header:request.getAllHeaders()){

			log("" + header);
		}
		log("Body:");
		try{
			System.out
					.println(entity != null?new XmlFormatter().format(EntityUtils.toString(entity).replaceAll("<ownerPassword>([\\s\\S]{0,50})</ownerPassword>", "<ownerPassword>******</ownerPassword>")):"");
			
		} catch(ParseException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Display the response data on the console.
	 * 
	 * @param hpcResponse
	 */
	public void showResponseInfo(ApiResponse hpcResponse){

		
		log("<<<<<<Server responsed with following info:");
		//log(new Date().toString());
		log("Status: " + response.getStatusLine().getStatusCode());
		log("Headers:");
		for(Header header:response.getAllHeaders()){
			log("" + header);
		}
		log("Body:");
		String responseBody=hpcResponse.getResponseBody();
		if(responseBody.startsWith("<?xml")){
			System.out
				.println(new XmlFormatter().format(hpcResponse.getResponseBody()));
			
		}else{
			log("Non-xml object returned!");
		}
	}
	
	private void log(String content){
		CommonTools.log(content, 1);
	}

}
