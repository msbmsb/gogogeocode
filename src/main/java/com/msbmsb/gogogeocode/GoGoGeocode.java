/******************************************************************************
* GoGoGeocode
* {@code GoGoGeocode} is intended to be a very simple java package for 
* geocoding an address to coordinates. The goal is to do minimal work to get 
* the minimal information.

* It uses the Google Geocoding API (V3). More information on this API is at: 
*   http://code.google.com/apis/maps/documentation/geocoding/. 
* There is no API key necessary for this version of the web service. 
* The API is subject to a query limit of 2,500 geolocation requests per day. 
* Any additional queries over this limit will yield a status code of 
* "OVER_QUERY_LIMIT"

* This class will return coordinates of -1,-1 for any unsuccessful geocoding 
* attempt. Coordinates are returned in string form as "longitude,latitude".
* 
* Author:       Mitchell Bowden <mitchellbowden AT gmail DOT com>
* License:      MIT License: http://creativecommons.org/licenses/MIT/
******************************************************************************/

package com.msbmsb.gogogeocode;

import com.msbmsb.gogogeocode.Coordinates;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Main class for geocoding an address to long/lat coordinates
 */
public class GoGoGeocode {
  /**
   * URL for Google Geocoding API v3, xml output
   * No API key is needed for this version
   */
  private static final String GOOGLE_GEOCODE_URL_STUB = 
    "http://maps.google.com/maps/api/geocode/xml?sensor=false&";

  private static final HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());

  /**
   * Empty default constructor for initialization
   */ 
  public GoGoGeocode() { }

  /**
   * Build the url given the string of a physical address
   * @param address the string representation of a physical address
   * @return full URL of API call, as String. null if address is empty
   */
  private static String buildURL(String address) throws UnsupportedEncodingException {
    if(address == null || address.equals("")) {
      return null;
    }

    String requestUrl = GOOGLE_GEOCODE_URL_STUB;
    requestUrl += "address=" + URLEncoder.encode(address, "UTF-8");

    return requestUrl;
  }

  /**
   * Given the string of an address, return a string representation of the
   * coordinates in the form of "longitude,latitude"
   * @param address the string representation of a physical address
   * @return String of coordinates in the form of "longitude,latitude"
   */
  public static String geocodeToString(String address) {
    return geocode(address).toString();
  }

  /**
   * Given the string of an address, return a Coordinates object that contains
   * the coordinate information extracted from the API response
   * @param address the string representation of a physical address
   * @return Coordinates object containing coordinate information from response
   */
  public static Coordinates geocode(String address) {
    Coordinates coords = new Coordinates();

    String requestUrl = "";
    try {
      requestUrl = buildURL(address);
    } catch(UnsupportedEncodingException uee) {
      uee.printStackTrace();
    }

    GetMethod getMethod = new GetMethod(requestUrl);
    getMethod.setFollowRedirects(true);

    try {
      httpClient.executeMethod(getMethod);

      // build the response object
      GoogleGeocodeResponse response = new GoogleGeocodeResponse(getMethod.getResponseBodyAsStream());

      // only change coordinates from default if request successful
      if(response.successful()) {
        coords = response.getCoords();
      } 
    } catch(Exception e) {
      System.out.println("Geocode exception: " + e.toString());
    } finally {
      getMethod.abort();
      getMethod.releaseConnection();
    }
    
    return coords;
  }
}
