package com.paiementdemat.mobilepay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRGen extends AppCompatActivity {

    private Button generate;
    private ImageView imageView;
    private EditText editText;
    private TextView infoDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        generate = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.price);
        infoDisplay = findViewById(R.id.infoDisplay);

        generate.setOnClickListener(new View.OnClickListener() {
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
        });
    }

}
