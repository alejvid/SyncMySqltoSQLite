//interface used for saving the results of barcode scanning, it is also accessed from newAsset.class
//in order to populate the information in the editText serial number

package com.example.alejandrovidales.syncmysqltosqlite;

/**
 * Created by alejandrovidales on 6/23/15.
 */
public interface ZBarConstants {
    public static final String SCAN_MODES = "SCAN_MODES";
    public static final String SCAN_RESULT = "SCAN_RESULT";
    public static final String SCAN_RESULT_TYPE = "SCAN_RESULT_TYPE";
    public static final String ERROR_INFO = "ERROR_INFO";
}
