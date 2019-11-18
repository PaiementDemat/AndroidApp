package com.paiementdemat.mobilepay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

public class QRead extends AppCompatActivity {

    ZXingScannerView qrCodeScanner;
    private final int REQUEST_PERMISSION_CAMERA=1;

    ZXingScannerView.ResultHandler mResultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result result) {
            Context context = getApplicationContext();
            if(result != null){
                Toast toast = Toast.makeText(context, result.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrread);
        Intent intent = getIntent();

        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        setScannerProperties();
    }

    private void setScannerProperties() {
        List<BarcodeFormat> listBC = new ArrayList<>();
        listBC.add(BarcodeFormat.QR_CODE);

        qrCodeScanner.setFormats(listBC);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
        /*if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true)) qrCodeScanner.setAspectTolerance(0.5f);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        }
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(mResultHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
    }
}
