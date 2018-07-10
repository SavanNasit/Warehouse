package com.accrete.sixorbit.activity.barCodeScanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by agt on 7/12/17.
 */

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Programmatically initialize the scanner view
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        // Do something with the result here
        Intent resultIntent = new Intent();
        resultIntent.putExtra("scanResult", rawResult.getText());
        setResult(1100, resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
