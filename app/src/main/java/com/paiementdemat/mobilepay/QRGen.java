package com.paiementdemat.mobilepay;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;


public class QRGen extends AppCompatActivity {

    private Button generate;
    private ImageView imageView;
    private EditText editText;
    private TextView infoDisplay;
    private ProgressBar progressBar;
    public String backend_ip;
    public String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgen);
        backend_ip = getString(R.string.backend_ip);
        generate = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.price);
        infoDisplay = findViewById(R.id.infoDisplay);
        progressBar = findViewById(R.id.progressBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        generate.setOnClickListener(v -> {
            try{
                String token = new AskForQR().execute(editText.getText().toString()).get();
                JSONObject resultJSON = (JSONObject) new JSONTokener(token).nextValue();
                String transaction_key = resultJSON.getString("transaction_key");
                new QRGenerator().execute(transaction_key);
            }
            catch(Exception e){
                Log.e("Error: ", e.getMessage());

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


    public class AskForQR extends AsyncTask<String, Void, String> {
        //RestTemplate restTemplate = new RestTemplate();

        @Override
        protected String doInBackground(String... strings) {
            JSONObject global = new JSONObject();
            JSONObject transaction = new JSONObject();

            try{
                transaction.put("amount", Integer.parseInt(strings[0]));
                JSONArray comments = new JSONArray();
                comments.put("Virement");
                transaction.put("comments", comments);
                global.put("transaction", transaction);

                String addr = backend_ip + ":10001/transaction/payment";

                //JSONObject obj = new JSONObject("{ \"user\": { \"email\": \"dev2@app.com\", \"password\": \"admindev\", \"username\": \"flox27\", \"details\": { \"first_name\": \"florian\", \"last_name\": \"quibel\" } } }");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("Content-Type", "application/json");
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String apikey = sharedPreferences.getString(getString(R.string.api_token), null);
                if(apikey == null) apikey = getString(R.string.api_token);
                String token = "Bearer " + apikey;
                parameters.put("Authorization", token);

                Log.d("Toekn:", token);
                return RequestHandler.sendPostWithHeaders(addr, global, parameters);
            }
            catch(Exception e){
                return new String("Exception in Thread: " +e.getMessage());
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Result: ", s);
            result = s;
        }
    }


}
