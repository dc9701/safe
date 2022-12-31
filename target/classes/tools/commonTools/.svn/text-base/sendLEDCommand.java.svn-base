package tools.commonTools;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * Class to make HTTP calls.
 * 
 */
@SuppressWarnings({"deprecation"})
public class sendLEDCommand{

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

	public sendLEDCommand(String uri,String method){

		this.uri = uri;
		this.method = method;
	}

	/**
	 * Adds the string to the URI. It will build the final URI with the correct URI syntax. Any missing '/' will be compensated for when the
	 * final URI is built.
	 * 
	 * @param path
	 *            Part of the URI, that gets the appended to the URI.
	 * @return
	 *         {@link sendLEDCommand} the HPCRequest with the given path appended to the URI.
	 */
	public sendLEDCommand path(String path){

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
	 *         {@link sendLEDCommand} the HPCRequest with the given query parameter set in the request.
	 */
	public sendLEDCommand param(String key, String value){

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
	 *         {@link sendLEDCommand} the HPCRequest with the given fragment appended in the request.
	 */
	public sendLEDCommand fragment(String fragment){

		this.fragment = fragment.trim();
		return this;
	}

	/**
	 * Sets the content type in the request object.
	 * 
	 * @param mediaType
	 *            content type of the request
	 * @return
	 *         {@link sendLEDCommand} the HPCRequest with the given content type set in the request.
	 */
	public sendLEDCommand type(String type){

		this.headers.put("Content-Type", type.trim());
		return this;
	}

	/**
	 * sets the body of the request to given parameter.
	 * 
	 * @param body
	 *            The raw characters that goes into the request. (Based on the content type this could be XML or JSON)
	 * @return
	 *         {@link sendLEDCommand} the HPCRequest with the given body set in the request.
	 */
	public sendLEDCommand body(String body){

		this.body = body.trim();
		return this;
	}

	/**
	 * Executes the HTTP call with the parameters specified by the this object.
	 * 
	 * @return
	 *         Returns an instance of HttpResponse.
	 */
	public void execute(){

		try{

			client = new DefaultHttpClient();

			// Sets the connect timeout of the request.
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 600000);
			// Sets the read timeout of the request.
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

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
			if(method.equalsIgnoreCase("GET")){
				HttpGet get = new HttpGet(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						get.addHeader(header.getKey(), header.getValue());
					}
				}
				response = client.execute(get);
			} else if(method.equalsIgnoreCase("POST")){
				HttpPost post = new HttpPost(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						post.addHeader(header.getKey(), header.getValue());
					}
				}
				post.setEntity(entity);
				response = client.execute(post);
			} else if(method.equalsIgnoreCase("PUT")){
				HttpPut put = new HttpPut(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						put.addHeader(header.getKey(), header.getValue());
					}
				}
				put.setEntity(entity);
				response = client.execute(put);
			} else if(method.equalsIgnoreCase("DELETE")){
				HttpDelete delete = new HttpDelete(uri);
				if( ! this.headers.isEmpty()){
					for(Entry<String, String> header:headers.entrySet()){
						delete.addHeader(header.getKey(), header.getValue());
					}
				}
				response = client.execute(delete);
			}

		} catch(Exception e){
		}

		// Read the response body and display it on the console.
		loadResponseBody();
		int responseCode = response.getStatusLine().getStatusCode();
		if(responseCode > 399 || responseCode < 200){
			throw new RuntimeException("Send LEDM request failed!");
		}
		client.getConnectionManager().shutdown();
	}

	public Header[] getHeaders(String headerName){

		return response.getHeaders(headerName);
	}

	public String loadResponseBody(){

		try{
			if(response.getEntity() != null){
				this.body = EntityUtils.toString(response.getEntity());
			} else{
				this.body = "";
			}
		} catch(Exception e){
		}
		return this.body;

	}

}
