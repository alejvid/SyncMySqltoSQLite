//class which populate the spinners in the first module (scan)
//this class also controls the barcode scan button for serial numbers

package com.example.alejandrovidales.syncmysqltosqlite;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.sourceforge.zbar.Symbol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class newAsset extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    // DB Class to perform DB related operations
    DBController controller = new DBController(this);

    // DB Controller to add new assets to SQLite
    DBControllerNewAsset controller1 = new DBControllerNewAsset(this);

    // Progress Dialog Object
    //ProgressDialog prgDialog;
    HashMap<String, String> queryValues;
    Spinner spinnerAType;
    Spinner spinnerBrand;
    Spinner spinnerModel;
    Spinner spinnerLocation;
    Spinner spinnerProperty;
    Spinner spinnerOwner;
    TextView passCodeValue;

    //variable for taking the information from spinner or text to SQLite
    SQLiteDatabase SQLiteAssets;
    EditText assetTag;
    EditText barcodeText;
    EditText assetFloor;
    EditText assetRoom;
    Button buttonSave;
    Cursor mCursor;
    SimpleCursorAdapter mAdapter;
    String assetType;
    String assetBrand;
    String assetModel;
    String assetProperty;
    String assetLocation;
    String assetOwner;
    String assetT;
    String assetF;
    String assetR;
    String barcodeT;
    private static final String url = "http://192.168.0.51/mysqlsqlitesync/";
    static String userID = "alejvid";
    private Toolbar myToolbar;

    //variable for scanning barcode and QR
    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_asset);

        myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);

        //barcode button listener
        findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScanner();
                //launchQRScanner();  for future QR compatibility
            }
        });

        //text for serial number field
        passCodeValue = (EditText)findViewById(R.id.barcodeText);

        //text for the rest of edit text
        assetTag = (EditText)findViewById(R.id.assetTag);
        assetFloor = (EditText)findViewById(R.id.assetFloor);
        assetRoom = (EditText)findViewById(R.id.assetRoom);
        buttonSave = (Button)findViewById(R.id.buttonSave);
        //cv.put(controller1.KEY_TAG, assetTag.getText().toString());
        //cv.put(controller1.KEY_SERIAL_NUMBER, passCodeValue.getText().toString());
        buttonSave.setOnClickListener(this);

        //Spinner element
        spinnerAType = (Spinner) findViewById(R.id.assetType);
        spinnerBrand = (Spinner) findViewById(R.id.assetBrand);
        spinnerModel = (Spinner) findViewById(R.id.assetModel);
        spinnerLocation = (Spinner) findViewById(R.id.assetLocationType);
        spinnerProperty = (Spinner) findViewById(R.id.assetProperty);
        spinnerOwner = (Spinner) findViewById(R.id.assetOwnerName);

        //spinner click listener
        spinnerAType.setOnItemSelectedListener(this);
        spinnerBrand.setOnItemSelectedListener(this);
        spinnerModel.setOnItemSelectedListener(this);
        spinnerLocation.setOnItemSelectedListener(this);
        spinnerProperty.setOnItemSelectedListener(this);
        spinnerOwner.setOnItemSelectedListener(this);

        //loading spinner data from database
        loadSpinnerData();
        loadSpinnerDataBrands();
        loadSpinnerDataModels();
        loadSpinnerDataLocations();
        loadSpinnerDataProperties();
        loadSpinnerDataOwners();

        //calling method sync sqlite
        syncSQLiteMySQLDB();
        syncSQLiteMySQLDBBrands();
        syncSQLiteMySQLDBModels();
        syncSQLiteMySQLDBLocations();
        syncSQLiteMySQLDBProperties();
        syncSQLiteMySQLDBOwners();

        //prgDialog = new ProgressDialog(this);

        //prgDialog.setMessage("Transferring Data from Remote MySQL DB and Syncing SQLite. Please wait...");

        //prgDialog.setCancelable(false);

        // BroadCase Receiver Intent Object
        //Intent alarmIntent = new Intent(getApplicationContext(), SampleBC.class);

        // Pending Intent Object
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Alarm Manager Object
        //AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        // Alarm Manager calls BroadCast for every Ten seconds (10 * 1000), BroadCase further calls service to check if new records are inserted in
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, 10 * 1000, pendingIntent);
    }

    @Override
    public void onClick(View v) {
        String check1 = spinnerAType.getSelectedItem().toString();
        int pos1 =spinnerAType.getSelectedItemPosition();
        if(pos1!=0) {
            assetType = spinnerAType.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the asset type !!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check1.equals(""))
        {
            assetType = spinnerAType.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the asset type !!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String check2 = spinnerBrand.getSelectedItem().toString();
        int pos2 =spinnerBrand.getSelectedItemPosition();
        if(pos2!=0) {
            assetBrand = spinnerBrand.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the brand!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check2.equals(""))
        {
            assetBrand = spinnerBrand.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the brand!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String check3 = spinnerModel.getSelectedItem().toString();
        int pos3 =spinnerModel.getSelectedItemPosition();
        if(pos3!=0) {
            assetModel = spinnerModel.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the model!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check3.equals(""))
        {
            assetModel = spinnerModel.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the model!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String check4 = spinnerProperty.getSelectedItem().toString();
        int pos4 =spinnerProperty.getSelectedItemPosition();
        if(pos4!=0) {
            assetProperty = spinnerProperty.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the property!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check4.equals(""))
        {
            assetProperty = spinnerProperty.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the property!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String check5 = spinnerLocation.getSelectedItem().toString();
        int pos5 =spinnerLocation.getSelectedItemPosition();
        if(pos5!=0) {
            assetLocation = spinnerLocation.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the location!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check5.equals(""))
        {
            assetLocation = spinnerLocation.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the location!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String check6 = spinnerOwner.getSelectedItem().toString();
        int pos6 =spinnerOwner.getSelectedItemPosition();
        if(pos6!=0) {
            assetOwner = spinnerOwner.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the owner!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if(!check6.equals(""))
        {
            assetOwner = spinnerOwner.getSelectedItem().toString();
        }
        else{
            Toast.makeText(this,
                    "Please Select the owner!!", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        assetT = assetTag.getText().toString();
        if(TextUtils.isEmpty(assetT)) {
            assetTag.setError("This Field Cannot Be Empty");
            return;
        }
        assetF = assetFloor.getText().toString();
        if(TextUtils.isEmpty(assetF)) {
            assetFloor.setError("This Field Cannot Be Empty");
            return;
        }
        assetR = assetRoom.getText().toString();
        if(TextUtils.isEmpty(assetR)) {
            assetRoom.setError("This Field Cannot Be Empty");
            return;
        }
        barcodeT = passCodeValue.getText().toString();
        if(TextUtils.isEmpty(barcodeT)) {
            passCodeValue.setError("This Field Cannot Be Empty");
            return;
        }
        insertData(assetT, assetType, assetBrand, assetModel, barcodeT, assetProperty, assetLocation, assetF, assetR, assetOwner);
    }

    private void insertData(String assetT2, String assetType2, String assetBrand2, String assetModel2, String barcodeT2, String assetProperty2,
                            String assetLocation2, String assetF2, String assetR2, String assetOwner2) {
        DBControllerNewAsset helper = new DBControllerNewAsset(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBControllerNewAsset.KEY_TAG, assetT);
        cv.put(DBControllerNewAsset.KEY_TYPE, assetType);
        cv.put(DBControllerNewAsset.KEY_BRAND, assetBrand);
        cv.put(DBControllerNewAsset.KEY_MODEL, assetModel);
        cv.put(DBControllerNewAsset.KEY_SERIAL_NUMBER, barcodeT);
        cv.put(DBControllerNewAsset.KEY_PROPERTY, assetProperty);
        cv.put(DBControllerNewAsset.KEY_LOCATION, assetLocation);
        cv.put(DBControllerNewAsset.KEY_FLOOR, assetF);
        cv.put(DBControllerNewAsset.KEY_ROOM, assetR);
        cv.put(DBControllerNewAsset.KEY_OWNER, assetOwner);
        cv.put("updateStatus", "no");
        if (cv != null) {
            database.insert(DBControllerNewAsset.TABLE_NAME, null, cv);
            Toast.makeText(this, "Information Saved Locally", Toast.LENGTH_SHORT).show();
            assetTag.setText(null);
            spinnerAType.setSelection(0);
            spinnerBrand.setSelection(0);
            spinnerModel.setSelection(0);
            passCodeValue.setText(null);
            spinnerProperty.setSelection(0);
            spinnerLocation.setSelection(0);
            assetFloor.setText(null);
            assetRoom.setText(null);
            spinnerOwner.setSelection(0);
        }
        else{
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
    }
    }

    public void launchScanner() {
        if (isCameraAvailable()){
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        }else{
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    //method to activate the camera for QR scanning
    public void launchQRScanner() {
        if (isCameraAvailable()) {
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES,
                    new int[] { Symbol.QRCODE });
            startActivityForResult(intent, ZBAR_QR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //results for scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_QR_SCANNER_REQUEST:
            case ZBAR_SCANNER_REQUEST:
                if (resultCode == RESULT_OK) {
                    passCodeValue.setText(data.getStringExtra(ZBarConstants.SCAN_RESULT));

                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //load spinner data asset type
    private void loadSpinnerData() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllLabels();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerAType.setAdapter(dataAdapter);
    }

    //load spinner data brands
    private void loadSpinnerDataBrands() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllBrands();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerBrand.setAdapter(dataAdapter);
    }

    //load spinner data models
    private void loadSpinnerDataModels() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllModels();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerModel.setAdapter(dataAdapter);
    }

    //load spinner data locations
    private void loadSpinnerDataLocations() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllLocations();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerLocation.setAdapter(dataAdapter);
    }

    //load spinner data properties
    private void loadSpinnerDataProperties() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllProperties();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerProperty.setAdapter(dataAdapter);
    }

    //load spinner data owners
    private void loadSpinnerDataOwners() {
        //database handler
        DBController db = new DBController(getApplicationContext());

        //spinner drop down elements
        List<String> labels = db.getAllOwners();

        //Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, labels);

        //drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinnerOwner.setAdapter(dataAdapter);
    }


    // Options Menu (ActionBar Menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // When Options Menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        // When Sync action button is clicked
        if (id == R.id.refresh) {
            // Transfer data from remote MySQL DB to SQLite on Android and perform Sync
            syncSQLiteMySQLDB();
            //loadSpinnerData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to Sync MySQL to SQLite DB
    public void syncSQLiteMySQLDB() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getusers.php
        client.post(url+"getusers.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getusers.php
                updateSQLite(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to Sync MySQL to SQLite DB for Brands
    public void syncSQLiteMySQLDBBrands() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getbrands.php
        client.post(url+"getbrands.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getbrands.php
                updateSQLiteBrands(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to Sync MySQL to SQLite DB for Models
    public void syncSQLiteMySQLDBModels() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getbrands.php
        client.post(url+"getmodels.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getbrands.php
                updateSQLiteModels(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to Sync MySQL to SQLite DB for Locations
    public void syncSQLiteMySQLDBLocations() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getlocations.php
        client.post(url+"getlocations.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getlocations.php
                updateSQLiteLocations(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to Sync MySQL to SQLite DB for Property
    public void syncSQLiteMySQLDBProperties() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getproperties.php
        client.post(url+"getproperties.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getproperties.php
                updateSQLiteProperties(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Method to Sync MySQL to SQLite DB for Models
    public void syncSQLiteMySQLDBOwners() {

        // Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();

        // Http Request Params Object
        RequestParams params = new RequestParams();

        // Make Http call to getowners.php
        client.post(url+"getowners.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                // Update SQLite DB with response sent by getowners.php
                updateSQLiteOwners(response);
            }

            // When error occured
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // TODO Auto-generated method stub
                // Hide ProgressBar
                //prgDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Method to Sync MySQL to SQLite DB for Assets
    public static void syncSQLiteMySQLDBAssets() throws JSONException {



    }

    public void updateSQLite(String response){
        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("userId"));
                    System.out.println(obj.get("userName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add userID extracted from Object
                    queryValues.put("userId", obj.get("userId").toString());
                    // Add userName extracted from Object
                    queryValues.put("userName", obj.get("userName").toString());
                    // Insert User into SQLite DB
                    controller.insertUser(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("userId").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncSts(gson.toJson(usersynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void updateSQLiteBrands(String response){
        ArrayList<HashMap<String, String>> brandsynclist;
        brandsynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("brandId"));
                    System.out.println(obj.get("brandName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("brandId", obj.get("brandId").toString());
                    // Add brandName extracted from Object
                    queryValues.put("brandName", obj.get("brandName").toString());
                    // Insert User into SQLite DB
                    controller.insertBrand(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("brandId").toString());
                    map.put("status", "1");
                    brandsynclist.add(map);
               }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsBrands(gson.toJson(brandsynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateSQLiteModels(String response){
        ArrayList<HashMap<String, String>> modelsynclist;
        modelsynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("modelId"));
                    System.out.println(obj.get("modelName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("modelId", obj.get("modelId").toString());
                    // Add brandName extracted from Object
                    queryValues.put("modelName", obj.get("modelName").toString());
                    // Insert User into SQLite DB
                    controller.insertModel(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("modelId").toString());
                    map.put("status", "1");
                    modelsynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsModels(gson.toJson(modelsynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateSQLiteLocations(String response){
        ArrayList<HashMap<String, String>> locationsynclist;
        locationsynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("locationId"));
                    System.out.println(obj.get("locationName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("locationId", obj.get("locationId").toString());
                    // Add brandName extracted from Object
                    queryValues.put("locationName", obj.get("locationName").toString());
                    // Insert User into SQLite DB
                    controller.insertLocation(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("locationId").toString());
                    map.put("status", "1");
                    locationsynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsLocations(gson.toJson(locationsynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateSQLiteProperties(String response){
        ArrayList<HashMap<String, String>> propertysynclist;
        propertysynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("propertyId"));
                    System.out.println(obj.get("propertyName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("propertyId", obj.get("propertyId").toString());
                    // Add brandName extracted from Object
                    queryValues.put("propertyName", obj.get("propertyName").toString());
                    // Insert User into SQLite DB
                    controller.insertProperty(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("propertyId").toString());
                    map.put("status", "1");
                    propertysynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsProperties(gson.toJson(propertysynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateSQLiteOwners(String response){
        ArrayList<HashMap<String, String>> ownersynclist;
        ownersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if(arr.length() != 0){
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("ownerId"));
                    System.out.println(obj.get("ownerName"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("ownerId", obj.get("ownerId").toString());
                    // Add brandName extracted from Object
                    queryValues.put("ownerName", obj.get("ownerName").toString());
                    // Insert User into SQLite DB
                    controller.insertOwner(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("ownerId").toString());
                    map.put("status", "1");
                    ownersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsOwners(gson.toJson(ownersynclist));
                // Reload the Main Activity
                reloadActivity();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

/*  /*//*  public void updateSQLiteAssets(String response) {
    *    ArrayList<HashMap<String, String>> assetsynclist;
    *    assetsynclist = new ArrayList<HashMap<String, String>>();
    *    // Create GSON object
    *    Gson gson = new GsonBuilder().create();
    *    try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has brand id and name
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);
                    System.out.println(obj.get("userId"));
                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, String>();
                    // Add brandId extracted from Object
                    queryValues.put("userId", obj.get("userId").toString());

                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("brandId").toString());
                    map.put("status", "1");
                    brandsynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncStsBrands(gson.toJson(assetsynclist));
                // Reload the Main Activity
                reloadActivity();
            }

        }

   *//* }*/

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post(url+"updatesyncsts.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncStsBrands(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Brands
        client.post(url+"updatesyncstsbrand.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncStsModels(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Brands
        client.post(url+"updatesyncstsmodel.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncStsLocations(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Brands
        client.post(url+"updatesyncstslocation.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncStsProperties(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Brands
        client.post(url+"updatesyncstsproperty.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncStsOwners(String json) {
        System.out.println(json);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Brands
        client.post(url+"updatesyncstsowner.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //Toast.makeText(getApplicationContext(), "MySQL DB has been informed about Sync activity", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Reload MainActivity
    public void reloadActivity() {
        Intent objIntent = new Intent(getApplicationContext(), activity_main.class);
        startActivity(objIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // on selecting a spinner item
        //String label = parent.getItemAtPosition(position).toString();

        //showing selected spinner item
        //Toast.makeText(parent.getContext(), "You selected: " + label,
                //Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

}