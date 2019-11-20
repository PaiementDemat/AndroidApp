package com.paiementdemat.mobilepay;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class QRGen extends AppCompatActivity {

    private Button generate;
    private ImageView imageView;
    private EditText editText;
    private TextView infoDisplay;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        generate = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.price);
        infoDisplay = findViewById(R.id.infoDisplay);
        progressBar = findViewById(R.id.progressBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try{
                    BitMatrix bitMatrix = qrCodeWriter.encode(editText.getText().toString(), BarcodeFormat.QR_CODE, 2000, 2000);
                    //infoDisplay.setText(editText.getText().toString());
                    int height = bitMatrix.getHeight();
                    int width = bitMatrix.getWidth();
                    Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                    for(int x=0; x<width; x++){
                        for(int y=0; y<height; y++){
                            bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                        }
                    }
                    imageView.setImageBitmap(bmp);
                    Context context = getApplicationContext();
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 50);
                    Window window = getWindow();
                    //Get the current window attributes
                    WindowManager.LayoutParams layoutpars = window.getAttributes();
                    //Set the brightness of this window
                    layoutpars.screenBrightness = 100 / (float)255;
                    //Apply attribute changes to this window
                    window.setAttributes(layoutpars);
                }
                catch(Exception e){
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorQRGen, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });*/

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QRGenerator().execute(editText.getText().toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    private class QRGenerator extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String string = params[0];
            string = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"+string;
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try{
                int minSize = 0;
                if(getScreenHeight() <= getScreenWidth()) minSize = getScreenHeight(); else minSize = getScreenWidth();
                //minSize = 177;
                BitMatrix bitMatrix = qrCodeWriter.encode(string, BarcodeFormat.QR_CODE, minSize, minSize);
                //infoDisplay.setText(editText.getText().toString());
                int height = bitMatrix.getHeight();
                int width = bitMatrix.getWidth();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for(int x=0; x<width; x++){
                    publishProgress((int)(x / (float)width * 100));
                    for(int y=0; y<height; y++){
                        bmp.setPixel(x, y, bitMatrix.get(x,y) ? Color.BLACK : Color.WHITE);
                    }
                }
                return bmp;
            }
            catch(Exception e){
                Log.e("Error", e.getMessage());
                Toast toast = Toast.makeText(getApplicationContext(), R.string.ErrorQRGen, Toast.LENGTH_LONG);
                toast.show();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            imageView.setImageBitmap(bmp);
            Context context = getApplicationContext();
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 50);
            Window window = getWindow();
            //Get the current window attributes
            WindowManager.LayoutParams layoutpars = window.getAttributes();
            //Set the brightness of this window
            layoutpars.screenBrightness = 100 / (float)255;
            //Apply attribute changes to this window
            window.setAttributes(layoutpars);
        }
    }

}
