package com.example.alejandrovidales.syncmysqltosqlite;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by alejandrovidales on 9/11/15.
 */
public class viewAssets extends AppCompatActivity  {

    //variable for scanning barcode and QR
    private static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    TextView passCodeValue;
    ProgressDialog prgDialog;
    String barcodeT;
    private SQLiteDatabase db;
    private DBControllerNewAsset dbHelper;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_asset);

        myToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(myToolbar);

        Button search = (Button) findViewById(R.id.buttonSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInfoEditText();
            }
        });

        //barcode button listener
        ((Button) findViewById(R.id.scanButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScanner();
                //launchQRScanner();  for future QR compatibility
            }
        });

        //text for serial number field
        passCodeValue = (EditText)findViewById(R.id.barcodeText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void launchScanner() {
        if (isCameraAvailable()){
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        }else{
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
                    loadInfoEditText();

                } else if (resultCode == RESULT_CANCELED && data != null) {
                    String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                    if (!TextUtils.isEmpty(error)) {
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void loadInfoEditText(){
        barcodeT = passCodeValue.getText().toString();
        dbHelper = new DBControllerNewAsset(this);
        db = dbHelper.getWritableDatabase();
        String selectQuery = "SELECT * FROM asset where serialnumber = '"+ barcodeT +"'";
        final Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() <= 0){
            Toast.makeText(this, "This Asset Does Not Exist", Toast.LENGTH_SHORT).show();
        }
        final ArrayList<String> row0 = new ArrayList<String>();
        final ArrayList<String> row1 = new ArrayList<String>();
        final ArrayList<String> row2 = new ArrayList<String>();
        final ArrayList<String> row3 = new ArrayList<String>();
        final ArrayList<String> row4 = new ArrayList<String>();
        final ArrayList<String> row5 = new ArrayList<String>();
        final ArrayList<String> row6 = new ArrayList<String>();
        final ArrayList<String> row7 = new ArrayList<String>();
        final ArrayList<String> row8 = new ArrayList<String>();
        final ArrayList<String> row9 = new ArrayList<String>();

        //String[] data = null;
        if(cursor!=null){
            //Toast.makeText(this, "Vengo por aqui", Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            startManagingCursor(cursor);
            for(int i=0;i<cursor.getCount();i++){
                //Toast.makeText(this, "No llega", Toast.LENGTH_SHORT).show();
                final String tag = cursor.getString(0);
                final String type = cursor.getString(1);
                final String brand = cursor.getString(2);
                final String model = cursor.getString(3);
                final String serialnumber = cursor.getString(4);
                final String property = cursor.getString(5);
                final String location = cursor.getString(6);
                final String floor = cursor.getString(7);
                final String room = cursor.getString(8);
                final String owner = cursor.getString(9);
                row0.add(tag);
                row1.add(type);
                row2.add(brand);
                row3.add(model);
                row4.add(serialnumber);
                row5.add(property);
                row6.add(location);
                row7.add(floor);
                row8.add(room);
                row9.add(owner);

                final EditText assetTag = (EditText) findViewById(R.id.assetTag);
                        assetTag.setText(tag);

                final EditText assetFloor = (EditText) findViewById(R.id.assetFloor);
                        assetFloor.setText(floor);

                final EditText assetRoom = (EditText) findViewById(R.id.assetRoom);
                        assetRoom.setText(room);

                final EditText assetOwner = (EditText) findViewById(R.id.assetOwnerName);
                        assetOwner.setText(owner);

                final EditText assetType = (EditText) findViewById(R.id.assetType);
                        assetType.setText(type);

                final EditText assetBrand = (EditText) findViewById(R.id.assetBrand);
                        assetBrand.setText(brand);

                final EditText assetModel = (EditText) findViewById(R.id.assetModel);
                        assetModel.setText(model);

                final EditText assetProperty = (EditText) findViewById(R.id.assetProperty);
                        assetProperty.setText(property);

                final EditText assetLocation = (EditText) findViewById(R.id.assetLocationType);
                        assetLocation.setText(location);


                cursor.moveToNext();
                }

            }
        else {
            Toast.makeText(this, "Asset Does Not Exist", Toast.LENGTH_SHORT).show();
        }
        }

    }