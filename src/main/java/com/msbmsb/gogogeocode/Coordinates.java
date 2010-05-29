/******************************************************************************
* Coordinates
* Class for containing simple coordinate information of latitude and longitude
* Default coordinates: (-1,-1)
* The toString() function outputs the coordinates in the order of:
*   longitude,latitude
* 
* Author:       Mitchell Bowden <mitchellbowden AT gmail DOT com>
* License:      MIT License: http://creativecommons.org/licenses/MIT/
******************************************************************************/

package com.msbmsb.gogogeocode;

/**
 * Simple class for initializing and storing coordinates
 */
public class Coordinates {
  public double latitude = -1d;
  public double longitude = -1d;

  /**
   * Default constructor
   * Coordinates initialized to (-1,-1)
   */
  public Coordinates() {}

  /**
   * Constructor that will attempt to parse a comma-delimited string
   * containing [longitude,latitude]
   * @param csvStr comma-delimited "longitude,latitude" string
   */
  public Coordinates(String csvStr) {
    parse(csvStr);
  }

  /**
   * Constructor to add string values in order of [longitude, latitude]
   * @param longStr String representing the longitude value
   * @param latStr String representing the latitude value
   */
  public Coordinates(String longStr, String latStr) {
    longitude = Double.parseDouble(longStr);
    latitude = Double.parseDouble(latStr);
  }

  /**
   * Constructor to add Double values in order of [longitude, latitude]
   * @param longD Double representing the longitude value
   * @param latD Double representing the latitude value
   */
  public Coordinates(Double longD, Double latD) {
    longitude = longD;
    latitude = latD;
  }

  /**
   * Parse the comma-delimited string
   * @param csvStr comma-delimited "longitude,latitude" string
   */
  public void parse(String csvStr) {
    String[] toks = csvStr.split(",");
    
    if(toks.length == 2) {
      longitude = Double.parseDouble(toks[0]);
      latitude = Double.parseDouble(toks[1]);
    } else {
      // unexpected
      System.out.println("Unexpected coordinates string");
      System.out.println("  Expected: 'long,lat'");
      System.out.println("  Received: " + csvStr);
    }
  }

  /**
   * @return String representation in the form of "longitude,latitude"
   */
  public String toString() {
    return longitude + "," + latitude;
  }
}
