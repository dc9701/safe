package tools.vpm.apiTools;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import static org.junit.Assert.*;

/**
 * The response class of HTTP call.
 *
 */
public class ApiResponse
{

  private HttpResponse response;
  private String body;

  public ApiResponse(HttpResponse response)
  {
    
    this.response = response;
  }

  /**
   * Asserts the status of the HTTP call. If the given status is not equal to the HTTP response's status then an assert error will be
   * thrown.
   * 
   * @param status
   *          status with which the HTTP response status will be compared.
   * @return
   *         {@link ApiResponse}
   */
  public ApiResponse assertStatus(int status)
  {
    
    assertEquals(status, response.getStatusLine().getStatusCode());
    return this;
  }

  /**
   * Asserts the status of the HTTP call. If the given contentType is not equal to the HTTP response's Content-Type then an assert error will be
   * thrown.
   * 
   * @param matcher
   *          Predicate for which the content-type header value is evaluated for true.
   * @return
   *         {@link ApiResponse}
   */
  public ApiResponse assertContentType(String type)
  {
    
    assertEquals(type, response.getFirstHeader("Content-Type").getValue());
    return this;
  }

  /**
   * Checks if the given header and matcher evaluates to true on the HTTP response's headers, if not then an assert error will be thrown.
   * 
   * @param headerName
   *          key identifying the header name (EX: Location, Content-Type, Authorization...)
   * @param headerValue
   *          value of the header name.
   * @return
   *         {@link ApiResponse}
   */
  public ApiResponse assertHeader(String headerName, String headerValue)
  {
    
    assertEquals(headerValue, response.getFirstHeader(headerName).getValue());
    return this;
  }

  /**
   * Checks if the response body contains or matches the given value.
   * 
   * @param expectedBody
   *          the expected data
   * @return
   *         {@link ApiResponse}
   */
  public ApiResponse assertBody(String expectedBody) throws IOException
  {
    
    assertTrue(body.contains(expectedBody) || body.matches(expectedBody));
    return this;
  }

  /**
   * Get the status response for the HPCResponse type.
   * 
   * @return
   *         An INT matching the HTTP status code.
   */
  public int getStatus()
  {
    
    return response.getStatusLine().getStatusCode();
  }

  /**
   * Get the related header response for the HPCResponse type.
   * 
   * @param headerName
   *        key identifying the header name (EX: Location, Content-Type, Authorization...)
   * @return
   *        The value response of the related header.
   */
  public String getHeader(String headerName)
  {
    
    return response.getFirstHeader(headerName).getValue();
  }

  /**
   * Get the related headers response for the HPCResponse type.
   * 
   * @param headerName
   *        key identifying the header name (EX: Location, Content-Type, Authorization...)
   * @return
   *        The headers response of the related header.
   */
  public Header[] getHeaders(String headerName)
  {
    
    return response.getHeaders(headerName);
  }

  /**
   * Get all headers response for the HPCResponse type.
   * 
   * @return
   *        The headers response of the related header.
   */
  public Header[] getHeaders()
  {
    
    return response.getAllHeaders();
  }

  /**
   * Get body response for the HPCResponse type.
   * 
   * @return
   *        The response body as String.
   */
  public String getResponseBody()
  {
    
    return this.body;
  }

  /**
   * Load body response as String from the HTTP response's entity.
   * 
   * @return
   *        The response body as String.
   */
  public String loadResponseBody()
  {
    
    try
    {
      if(response.getEntity() != null){
        this.body = EntityUtils.toString(response.getEntity());
      }else{
        this.body = "";
      }
    }
    catch(ParseException e)
    {
      e.printStackTrace();
    }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    return this.body;
  }

}
