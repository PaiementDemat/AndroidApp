package com.paiementdemat.mobilepay;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.paiementdemat.mobilepay.login.ui.login.LoginActivity;
import com.paiementdemat.mobilepay.login.ui.login.LoginViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.Preference;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;



public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Intent intent;
    private TextView balance;
    private TextView devise;
    private Button provision;
    private AlertDialog.Builder popupProvision;
    private Button button5;
    public String result;
    private SharedPreferences sharedPreferences;
    public SharedPreferences credentials;
    public String apikey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(R.string.AskAuthorizations);
                dialog.setTitle(R.string.Authorization);
                dialog.setPositiveButton(R.string.Yes, (dialog1, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 200);
                });
                dialog.setNegativeButton(R.string.No, (dialog12, which) -> Toast.makeText(getApplicationContext(), R.string.AuthorizationsRemember, Toast.LENGTH_LONG).show());
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            }
        }
        credentials = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);

        dl = findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch (id) {
                case R.id.login:
                    intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.pay:
                    intent = new Intent(this, QRead.class);
                    startActivity(intent);
                    break;
                case R.id.settings:
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ask:
                    intent = new Intent(this, QRGen.class);
                    startActivity(intent);
                    break;
                case R.id.history:
                    intent = new Intent(this, HistoryActivity.class);
                    startActivity(intent);
                    break;
                default:
                    return true;
            }
            return true;

        });

        popupProvision = new AlertDialog.Builder(this);

        final SeekBar seekProvision = new SeekBar(this);
        seekProvision.setMax(200);
        seekProvision.setKeyProgressIncrement(5);

        final TextView displayAmount = new TextView(this);
        displayAmount.setText("0 €");
        displayAmount.setPadding(40, 40, 40, 30);
        displayAmount.setGravity(Gravity.CENTER);
        displayAmount.setTextSize(15);

        final LinearLayout layoutDialog = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutDialog.setOrientation(LinearLayout.VERTICAL);
        layoutDialog.setLayoutParams(parms);
        layoutDialog.setGravity(Gravity.CLIP_VERTICAL);
        layoutDialog.setPadding(2, 2, 2, 2);

        /*LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;

        layoutDialog.addView(seekProvision, tv1Params);
        layoutDialog.addView(displayAmount, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));*/
        layoutDialog.addView(displayAmount);
        layoutDialog.addView(seekProvision);


        balance = findViewById(R.id.textView3);
        devise = findViewById(R.id.textView);
        Boolean balanceOnScreen = true;
        try{
            balanceOnScreen = credentials.getBoolean("balance_on_screen", Boolean.TRUE);
        } catch(Exception e){
            Log.e("Error in getting bool", e.getMessage());
        }

        if(balanceOnScreen){
            balance.setVisibility(View.VISIBLE);
            devise.setVisibility(View.VISIBLE);
        } else{
            balance.setVisibility(View.INVISIBLE);
            devise.setVisibility(View.INVISIBLE);
        }


        popupProvision.setMessage(R.string.provisionMessage)
                .setTitle(R.string.recharger_le_compte)
                .setView(layoutDialog)
                .setPositiveButton(R.string.Validate, (dialog, which) -> {
                    int value = 0;
                    value += seekProvision.getProgress();

                    String result;
                    try{
                        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        String userID = sharedPreferences.getString(getString(R.string.userID), null);
                        String apikey = sharedPreferences.getString(getString(R.string.api_token), null);
                        if(apikey == null) apikey = getString(R.string.api_token);

                        result = new BankAccountTask().execute(value, apikey, userID).get();
                        JSONObject resp = (JSONObject) new JSONTokener(result).nextValue();
                        String status = resp.getString("status");
                        Log.d("Status add money: ", status);
                        if(status.equals("success")){
                            result = new GetAccount().execute(apikey).get();
                            JSONObject resultJSON = (JSONObject) new JSONTokener(result).nextValue();
                            JSONArray accounts = resultJSON.getJSONArray("accounts");
                            JSONObject accounts0 = accounts.getJSONObject(0);
                            Double val = accounts0.getDouble("balance");
                            balance.setText(Double.toString(val));
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.creditedAccount) + " " + value + "€.", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                    catch(Exception e){
                        Log.d("Error: ", e.getMessage());
                    }
                    seekProvision.setProgress(0);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.Close, (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog alert = popupProvision.create();

        provision = findViewById(R.id.recharger);

        provision.setOnClickListener(v -> {

            alert.show();
        });

        button5 = findViewById(R.id.button5);
        button5.setOnClickListener(v -> {
            RefreshBalance();
        });

        seekProvision.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String displayText = progress + " €";
                displayAmount.setText(displayText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Boolean autoLoginEnabled = credentials.getBoolean(getString(R.string.autologin), false);
        if(autoLoginEnabled){
            AutoLogin();
        }

        apikey = credentials.getString(getString(R.string.api_token), null);
        if(apikey == null) apikey = getString(R.string.api_token);


        /*button5 = findViewById(R.id.button5);

        button5.setOnClickListener(v ->{
            AutoLogin();
        });

        Log.e("Calling activity", getIntent().toString());*/
    }

    @Override
    protected void onResume(){
        super.onResume();
        Boolean balanceOnScreen = true;
        try{
            balanceOnScreen = credentials.getBoolean("balance_on_screen", Boolean.TRUE);
        } catch(Exception e){
            Log.e("Error in getting bool", e.getMessage());
        }
        RefreshBalance();
        if(balanceOnScreen){
            balance.setVisibility(View.VISIBLE);
            devise.setVisibility(View.VISIBLE);
        } else{
            balance.setVisibility(View.INVISIBLE);
            devise.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public void AutoLogin(){
        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:

                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:

                break;
        }

        /*Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("autoLogin", true);
        startActivity(intent);*/
    }

    public void startAutoLogin(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("autoLogin", true);
        startActivity(intent);
        RefreshBalance();
    }

    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getString(R.string.Reconnection))
                        .setSubtitle(getString(R.string.toReconnect))
                        .setDeviceCredentialAllowed(true)
                        .setConfirmationRequired(false)
                        .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                BiometricPrompt.CryptoObject authenticatedCryptoObject =
                        result.getCryptoObject();

                startAutoLogin();
                // User has verified the signature, cipher, or message
                // authentication code (MAC) associated with the crypto object,
                // so you can use it in your app's crypto-driven workflows.
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Displays the "log in" prompt.
        biometricPrompt.authenticate(promptInfo);
    }

    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };


    public class BankAccountTask extends AsyncTask<Object, Void, String> {
        //RestTemplate restTemplate = new RestTemplate();
        String backend_ip = getString(R.string.backend_ip);

        //strings[0]: amount
        @Override
        protected String doInBackground(Object... strings) {
            JSONObject global = new JSONObject();
            JSONObject account = new JSONObject();

            try{
                account.put("add", (int)strings[0]);
                String addr = backend_ip + ":10001/account/add/" + strings[2];
                Log.d("URL: ", addr);
                global.put("account", account);

                //JSONObject obj = new JSONObject("{ \"user\": { \"email\": \"dev2@app.com\", \"password\": \"admindev\", \"username\": \"flox27\", \"details\": { \"first_name\": \"florian\", \"last_name\": \"quibel\" } } }");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("Content-Type", "application/json");
                String token = "Bearer " + strings[1];
                parameters.put("Authorization", token);
                return RequestHandler.sendPostWithHeaders(addr, global, parameters);
            }
            catch(Exception e){
                return new String("Exception: " +e.getMessage());
            }


        }



        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result = s;
        }
    }

    class GetAccount extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings){
            try{
                String backend_ip = getString(R.string.backend_ip);
                String url = backend_ip + ":10001/account";
                Map<String, String> parameters = new HashMap<>();
                String token = "Bearer " + strings[0];
                parameters.put("Authorization", token);
                parameters.put("Content-Type", "application/json");
                return RequestHandler.sendGetWithHeaders(url, parameters);
            }
            catch (Exception e){
                return new String("Exception: " +e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            result = s;
        }

    }

    public void RefreshBalance(){
        try{
            SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            apikey = sharedPreferences.getString(getString(R.string.api_token), null);
            String res = new GetAccount().execute(apikey).get();
            JSONObject resultJSON = (JSONObject) new JSONTokener(res).nextValue();
            JSONArray accounts = resultJSON.getJSONArray("accounts");
            JSONObject accounts0 = accounts.getJSONObject(0);
            Double val = accounts0.getDouble("balance");
            balance.setText(Double.toString(val));
        }
        catch (Exception e){
            Log.e("Problem: ", e.getMessage());
        }
    }


}
