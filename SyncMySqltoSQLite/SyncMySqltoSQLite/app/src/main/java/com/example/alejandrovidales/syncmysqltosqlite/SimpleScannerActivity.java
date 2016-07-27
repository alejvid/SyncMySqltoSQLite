//class to control the camera for barcode scanning, it also takes the results and save it to ZBarConstants

package com.example.alejandrovidales.syncmysqltosqlite;

/**
 * Created by alejandrovidales on 6/23/15.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler, ZBarConstants {
    private ZBarScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        Toast.makeText(this, "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        Intent dataIntent = new Intent();
        dataIntent.putExtra(SCAN_RESULT, rawResult.getContents());
        setResult(Activity.RESULT_OK, dataIntent);
        finish();
    }
}
