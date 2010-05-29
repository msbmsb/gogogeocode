/******************************************************************************
* GoGoGeocodeTest
* Simple junit test for the GoGoGeocode class
* 
* Author:       Mitchell Bowden <mitchellbowden AT gmail DOT com>
* License:      MIT License: http://creativecommons.org/licenses/MIT/
******************************************************************************/

package com.msbmsb.gogogeocode;

import com.msbmsb.gogogeocode.GoGoGeocode;
import com.msbmsb.gogogeocode.Coordinates;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for GoGoGeocode.
 */
public class GoGoGeocodeTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public GoGoGeocodeTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( GoGoGeocodeTest.class );
    }

    /**
     * Rigourous Test
     */
    public void testGoGoGeocode()
    {
        GoGoGeocode gg = new GoGoGeocode();
        String results = gg.geocodeToString("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA");
        assertEquals(results, "-122.0845109,37.4220323");
    }
}
