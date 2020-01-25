package com.paiementdemat.mobilepay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ArrayList userList = getListData();
        final ListView lv = (ListView) findViewById(R.id.historyList);
        lv.setAdapter(new HistoryAdapter(this, userList));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                LauncherActivity.ListItem user = (LauncherActivity.ListItem) lv.getItemAtPosition(position);
            }
        });
    }


    //Méthode à adapter pour l'API
    @TargetApi(24)
    private ArrayList getListData(){
        ArrayList<History> results= new ArrayList<>();

        try{
            SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String apikey = sharedPreferences.getString(getString(R.string.api_token), null);

            String result = new GetAccount().execute(apikey).get();
            JSONObject resp = (JSONObject) new JSONTokener(result).nextValue();
            JSONArray accounts = resp.getJSONArray("accounts");
            JSONObject account1 = accounts.getJSONObject(0);
            JSONArray transactions_history = account1.getJSONArray("transactions_history");
            List<String> differentTransactions = new ArrayList<>();
            if(transactions_history != null){
                int len = transactions_history.length();
                for(int i=0; i<len; i++){
                    //Si cet élément n'est pas déjà dans la liste. Permet de limiter les appels API pour les doublons
                    if(!differentTransactions.contains(transactions_history.get(i).toString())){
                        differentTransactions.add(transactions_history.get(i).toString());
                    }
                }
            }
            int lengthList = differentTransactions.size();
            for(int i=0; i<lengthList; i++){
                String tempTransac = differentTransactions.get(i);
                String resultTemp = new GetHistory().execute(apikey, tempTransac).get();

                JSONObject tempResp = (JSONObject) new JSONTokener(resultTemp).nextValue();
                try{
                    JSONObject transaction = tempResp.getJSONObject("transaction");
                    JSONObject transaction_details = transaction.getJSONObject("transaction_details");
                    int amount = transaction_details.getInt("amount");
                    JSONObject commercant = transaction_details.getJSONObject("commercant");
                    String email = commercant.getString("email");
                    String date = transaction.getString("created_at");
                    History tempHist = new History(email, Integer.toString(amount)+" €", date);
                    results.add(tempHist);
                } catch (Exception e1){
                    Log.e("Syntax error", e1.getMessage());
                }

            }

        } catch(Exception e){
            Log.e("Request error", e.getMessage());
        }

//        History hist1 = new History("Amazon", "56", "2020-05-03");
//        History hist2 = new History("Carrefour", "15", "2020-05-02");
//        History hist3 = new History("Auchan", "30", "2020-05-01");
//        results.add(hist1); results.add(hist2); results.add(hist3);

        results.sort(new DateSorter());
        return results;
    }

    public class GetAccount extends AsyncTask<String, Void, String> {

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

    public class GetHistory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings){
            try{
                String backend_ip = getString(R.string.backend_ip);
                String url = backend_ip + ":10001/account/transaction/" + strings[1];
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
