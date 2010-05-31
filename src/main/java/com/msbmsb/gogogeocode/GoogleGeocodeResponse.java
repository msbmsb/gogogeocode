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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.io.IOException;

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
   * @param xmlStream InputStream of API xml response
   */
  public GoogleGeocodeResponse(InputStream xmlStream) {
    initialize();

    buildDomDocument(xmlStream);

    parseXmlResponse();
  }

  /**
   * Constructor given a String representation of the xml response from the API
   * @param xmlString String of API xml response
   */
  public GoogleGeocodeResponse(String xmlString) {
    initialize();

    extractFromXmlResponse(xmlString);
  }

  /**
   * Initialize members
   */
  private void initialize() {
    // initialize members
    this.coordinates = new Coordinates();
    this.status = GoogleGeocodeStatus.NONE;
  }

  /**
   * Extracts desired data from input String representation of xml
   * @param xmlString String of API xml response
   */
  private void extractFromXmlResponse(String xmlString) {
    // extract status
    this.status = extractXmlValue(xmlString, "<status>");
    if(!successful()) return;

    coordinates.latitude = Double.parseDouble(extractXmlValue(xmlString, "<lat>"));
    coordinates.longitude = Double.parseDouble(extractXmlValue(xmlString, "<lng>"));
  }

  /**
   * Given a string representation of xml and a key, return the value of 
   * the first occurrence of that key
   * @param xmlString the xml string to search in
   * @param key the xml key to search for
   * @return the value of the first occurrence of key, null if not found
   */
  private String extractXmlValue(String xmlString, String key) {
    int keyIndex = xmlString.indexOf(key);
    if(keyIndex == -1) {
      return null;
    }
    keyIndex += key.length();
    int endKey = xmlString.indexOf("<", keyIndex);
    return xmlString.substring(keyIndex, endKey);
  }

  /**
   * Build a Dom Document from the given xml InputStream
   * @param xmlStream the InputStream of the xml
   * Sets the xmlResponse member.
   */
  private void buildDomDocument(InputStream xmlStream) {
    // Build a dom Document out of the xml response
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      xmlResponse = db.parse(xmlStream);
    } catch(ParserConfigurationException pce) {
      pce.printStackTrace();
    } catch(SAXException se) {
      se.printStackTrace();
    } catch(IOException ioe) {
      ioe.printStackTrace();
    }
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
