package com.paiementdemat.mobilepay;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {
    public static String sendPost(String r_url , JSONObject postDataParams) throws Exception {
        URL url = new URL(r_url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        String jsonString = postDataParams.toString();
        Log.d("JSON: ", jsonString);
        try(OutputStream os = conn.getOutputStream()){
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode=conn.getResponseCode(); // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in=new BufferedReader( new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line="";
            while((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        }
        else{
            Log.e("Error. Response code: ", String.valueOf(responseCode));
        }
        return null;
    }

    public static String sendPostWithHeaders(String r_url , JSONObject postDataParams, Map<String, String> parameters) throws Exception {
        URL url = new URL(r_url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        for (Map.Entry<String, String> param: parameters.entrySet()){
            conn.setRequestProperty(param.getKey(), param.getValue());
        }
        //conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        String jsonString = postDataParams.toString();
        Log.d("JSON: ", jsonString);
        String jsonParams = parameters.toString();
        Log.d("Parameters POST: ", jsonParams);

        try(OutputStream os = conn.getOutputStream()){
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode=conn.getResponseCode(); // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in=new BufferedReader( new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line="";
            while((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        }
        else{
            Log.e("Error. Response code: ", String.valueOf(responseCode));
        }
        return null;
    }

    public static String sendGetWithHeaders(String r_url , Map<String, String> parameters) throws Exception {
        URL url = new URL(r_url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        for (Map.Entry<String, String> param: parameters.entrySet()){
            conn.setRequestProperty(param.getKey(), param.getValue());
        }
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        String jsonParams = parameters.toString();
        Log.d("Parameters GET: ", jsonParams);


        int responseCode=conn.getResponseCode(); // To Check for 200
        if (responseCode == HttpsURLConnection.HTTP_OK) {

            BufferedReader in=new BufferedReader( new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer("");
            String line="";
            while((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        }
        else{
            Log.e("Error. Response code: ", String.valueOf(responseCode));
        }
        return null;
    }

    /*public static String sendGet(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // connection ok
            BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            return "";
        }
    }*/
}
