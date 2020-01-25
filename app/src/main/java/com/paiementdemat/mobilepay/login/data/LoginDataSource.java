package com.paiementdemat.mobilepay.login.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.paiementdemat.mobilepay.App;
import com.paiementdemat.mobilepay.R;
import com.paiementdemat.mobilepay.RequestHandler;
import com.paiementdemat.mobilepay.login.data.model.LoggedInUser;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource extends AppCompatActivity {

    public String result;
    public String backend_ip;

    /*
    Class handling credentials received.
    */
    public Result<LoggedInUser> login(String username, String password, Boolean isLogin) {

        backend_ip = App.getResourses().getString(R.string.backend_ip);
        try {
            // TODO: handle loggedInUser authentication
            if(isLogin) result = new LoginTask().execute(username, password).get();
            else {
                result = new SignupTask().execute(username, password).get();
                result = new LoginTask().execute(username, password).get();
            }

            JSONObject resultJSON = (JSONObject) new JSONTokener(result).nextValue();
            String status = resultJSON.getString("status");

            //A CHANGER!!!!
            if(status.equals("error")){
                throw new Exception();
            }

            resultJSON = (JSONObject) new JSONTokener(result).nextValue();
            String token = resultJSON.getString("api_token");

            LoggedInUser User = new LoggedInUser(token, username);

            //Si on effectue une inscription, un faux compte bancaire est automatiquement ajouté.
            if(!isLogin) {
                result = new BankAccountTask().execute(token).get();
            }

            return new Result.Success<>(User);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    /*
    Method to connect to the backend API server for login.
    */
    public class LoginTask extends AsyncTask<String, Void, String> {
        //RestTemplate restTemplate = new RestTemplate();

        @Override
        protected String doInBackground(String... strings) {
            JSONObject user = new JSONObject();
            JSONObject global = new JSONObject();
            try{
                user.put("email", strings[0]);
                user.put("password", strings[1]);
                global.put("user", user);
                Log.d("username", strings[0]);
                Log.d("pwd", strings[1]);
                String addr = backend_ip + ":10000/auth/login";

                return RequestHandler.sendPost(addr, global);
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

    /*
    Method to connect to the backend API server for signup.
    */
    public class SignupTask extends AsyncTask<String, Void, String> {
        //RestTemplate restTemplate = new RestTemplate();

        @Override
        protected String doInBackground(String... strings) {
            JSONObject user = new JSONObject();
            JSONObject global = new JSONObject();
            JSONObject details = new JSONObject();

            try{
                details.put("first_name", "null");
                details.put("last_name", "null");
                user.put("email", strings[0]);
                user.put("password", strings[1]);
                user.put("username", "null");
                user.put("details", details);
                global.put("user", user);
                String addr = backend_ip + ":10000/auth/signup";

                //JSONObject obj = new JSONObject("{ \"user\": { \"email\": \"dev2@app.com\", \"password\": \"admindev\", \"username\": \"flox27\", \"details\": { \"first_name\": \"florian\", \"last_name\": \"quibel\" } } }");
                return RequestHandler.sendPost(addr, global);
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

    /*
    Method to create a bank account linked to the user
    */
    public class BankAccountTask extends AsyncTask<String, Void, String> {
        //RestTemplate restTemplate = new RestTemplate();

        @Override
        protected String doInBackground(String... strings) {
            JSONObject global = new JSONObject();
            JSONObject account = new JSONObject();

            try{
                account.put("type", "STANDARD");
                String addr = backend_ip + ":10001/account";
                global.put("account", account);

                //JSONObject obj = new JSONObject("{ \"user\": { \"email\": \"dev2@app.com\", \"password\": \"admindev\", \"username\": \"flox27\", \"details\": { \"first_name\": \"florian\", \"last_name\": \"quibel\" } } }");
                Map<String, String> parameters = new HashMap<>();
                parameters.put("Content-Type", "application/json");
                String token = "Bearer " + strings[0];
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
}
