package com.paiementdemat.mobilepay.login.ui.login;

import android.app.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.paiementdemat.mobilepay.R;
import com.paiementdemat.mobilepay.RequestHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Switch switchLogin;
    public String result;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /*final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);*/
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        /*final Button loginButton = findViewById(R.id.login);*/
        loginButton = findViewById(R.id.login);
        switchLogin = findViewById(R.id.switch1);
        switchLogin.setChecked(getAutoLogPref());

        final Button signupButton = findViewById(R.id.signup);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                signupButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        /*
        Handles result of login
        */
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {

                    if(switchLogin.isChecked()){
                        setAutoLogPref(true);
                        SaveCredentials();
                    } else{
                        setAutoLogPref(false);
                    }

                    String api_token = loginResult.getSuccess().getUserId();
                    saveApiToken(api_token);
                    updateUiWithUser(loginResult.getSuccess());

                    //Store the User ID in preferences

                    String response = null;
                    try{
                        response = new GetUserIdTask().execute(api_token).get();
                        JSONObject resultJSON = (JSONObject) new JSONTokener(response).nextValue();
                        //Log.d("Complete JSON: ", resultJSON.toString());

                        JSONArray accounts = resultJSON.getJSONArray("accounts");
                        JSONObject accounts0 = accounts.getJSONObject(0);
                        String userID = accounts0.getString("_id");
                        saveUserID(userID);


                    }
                    catch(Exception e){
                        Log.d("Error: ", e.toString());
                    }


                    finish();
                }
                setResult(Activity.RESULT_OK);


            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.signup(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        try{
            Bundle bundle = getIntent().getExtras();
            if(bundle != null){
                Boolean autoLogin = bundle.getBoolean("autoLogin");
                if(autoLogin){
                    AutoLogin();
                }
            }
        }
        catch(Exception e){

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    /*
    Store locally the API Key received.
    */
    private void saveApiToken(String token){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.api_token), token);
        editor.apply();
    }

    private void saveUserID(String userID){
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.userID), userID);
        editor.apply();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void SaveCredentials(){
        SharedPreferences credentials = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentials.edit();
        editor.putString(getString(R.string.username), usernameEditText.getText().toString());
        editor.putString(getString(R.string.pwd), passwordEditText.getText().toString());
        editor.apply();
    }

    private void AutoLogin(){
        SharedPreferences credentials = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        Boolean autoLoginEnabled = credentials.getBoolean(getString(R.string.autologin), false);

        if(autoLoginEnabled)
        {
            String username = credentials.getString(getString(R.string.username), null);
            String password = credentials.getString(getString(R.string.pwd), null);
            usernameEditText.setText(username);
            passwordEditText.setText(password);
            if(username != null && password != null) loginButton.performClick();
        }
    }

    private void setAutoLogPref(Boolean save){
        SharedPreferences credentials = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = credentials.edit();
        editor.putBoolean(getString(R.string.autologin), save);
        editor.apply();
    }

    public Boolean getAutoLogPref(){
        SharedPreferences credentials = this.getSharedPreferences(getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        return credentials.getBoolean(getString(R.string.autologin), false);
    }

    class GetUserIdTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings){
            try{
                String url = "http://93.30.105.184:10001/account";
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

}
