/******************************************************************************
* GoogleGeocodeResponse
* Capture and parses the XML response from the Google Geocoding API
* Minimal information retrieved: status, latitude, longitude
* Default coordinates returned on an unsuccessful response: (-1,-1)
* 
* Expected response xml is in the form of:
* <GeocodeResponse>
*   <status></status>
*   <result>
*     ...
*     <geometry>
*       <location>
*         <lat>...</lat>
*         <lng>...</lng>
*       </location>
*       ...
*     </geometry>
*   </result>
* </GeocodeResponse>
* 
* Author:       Mitchell Bowden <mitchellbowden AT gmail DOT com>
* License:      MIT License: http://creativecommons.org/licenses/MIT/
******************************************************************************/

package com.msbmsb.gogogeocode;

import com.msbmsb.gogogeocode.Coordinates;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * {@code GoogleGeocodeResponse} parses the XML response from the Google 
 * Geocoding API and stores a minimal amount of information.
 * Only Coordinates and the status string are extracted from the response.
 */
public class GoogleGeocodeResponse {
  private Coordinates coordinates;
  private String status;
  private Document xmlResponse;

  /**
   * Internal class to store status code strings from the API
   */
  public class GoogleGeocodeStatus {
    public static final String NONE = "NONE";
    public static final String OK = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String REQUEST_DENIED = "REQUEST_DENIED";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
  }

  /**
   * Constructor given a dom xml response from the API
   * @param xmlResponse dom document of API xml response
   */
  public GoogleGeocodeResponse(Document xmlResponse) {
    // initialize members
    coordinates = new Coordinates();
    status = GoogleGeocodeStatus.NONE;

    this.xmlResponse = xmlResponse;

    parseXmlResponse();
  }

  /**
   * Given the xml response, was the request successful?
   * @return boolean value if status code is OK
   */
  public boolean successful() {
    return (status.equals(GoogleGeocodeStatus.OK));
  }

  /** 
   * @return Coordinates object stored in this response
   */
  public Coordinates getCoords() {
    return coordinates;
  }

  /**
   * @return status string of this response
   */
  public String getStatus() {
    return status;
  }

  /**
   * Function to parse the member Document
   */
  private void parseXmlResponse() {
    Element root = xmlResponse.getDocumentElement();

    parseStatus(root);
    if(successful()) {
      parseCoordinates(root);
    }
  }

  /**
   * Extracts the status string out of the given xml root element
   * If there is no status, the initialized value of "NONE" remains
   * @param xml root element
   */
  private void parseStatus(Element root) {
    NodeList nodes = root.getElementsByTagName("status");
    if(nodes != null && nodes.getLength() > 0) {
      Element statusElement = (Element)nodes.item(0);
      status = statusElement.getFirstChild().getNodeValue();
    }
  }

  /**
   * Extracts just the coordinates from the result xml
   * @param xml root element
   */
  private void parseCoordinates(Element root) {
    NodeList nodes = root.getElementsByTagName("result");
    if(nodes == null || nodes.getLength() == 0) {
      return;
    }

    Element result = (Element)(nodes.item(0));
    Element geometry = (Element)(result.getElementsByTagName("geometry")).item(0);
    Element location = (Element)(geometry.getElementsByTagName("location")).item(0);

    // get the lat & lng out of the location element
    Element locElement = (Element)(location.getElementsByTagName("lat")).item(0);
    coordinates.latitude = Double.parseDouble(locElement.getFirstChild().getNodeValue());
    locElement = (Element)(location.getElementsByTagName("lng")).item(0);
    coordinates.longitude = Double.parseDouble(locElement.getFirstChild().getNodeValue());
  }
}
