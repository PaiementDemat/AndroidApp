package com.paiementdemat.mobilepay;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class RequestHandler {
    public static String sendPost(String r_url , JSONObject postDataParams) throws Exception {
        URL url = new URL(r_url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        /*conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);*/
        conn.setRequestMethod("POST");
//        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        //OutputStream os = conn.getOutputStream();

        /*BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
        writer.write(encodeParams(postDataParams));
        writer.flush();
        writer.close();
        os.close();*/

        String jsonString = encodeParams(postDataParams);
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
    public static String sendGet(String url) throws IOException {
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
    }
    private static String encodeParams(JSONObject params) throws Exception {
        /*StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }*/
        String result = params.toString();
//        return result.toString();
        return result;
    }
}
