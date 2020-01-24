package com.paiementdemat.mobilepay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsActivity extends AppCompatActivity{

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        context = this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //SharedPreferences mPreferences;
            //mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String apikey = sharedPreferences.getString(getString(R.string.api_token), null);
            if(apikey == null) apikey = getString(R.string.api_token);
            String userid = sharedPreferences.getString(getString(R.string.userID), null);
            if(userid == null) userid = getString(R.string.userID);

            Boolean useBiometry = sharedPreferences.getBoolean("use_biometry", Boolean.TRUE);
            Boolean balanceOnScreen = sharedPreferences.getBoolean("balance_on_screen", Boolean.TRUE);


            SwitchPreferenceCompat biometryPreference = getPreferenceManager().findPreference("biometry");
            biometryPreference.setOnPreferenceClickListener((preference) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(biometryPreference.isChecked()){
                    editor.putBoolean("use_biometry", true);
                } else{
                    editor.putBoolean("use_biometry", false);
                }
                editor.apply();
                return true;
            });

            SwitchPreferenceCompat balanceOnMainScreen = getPreferenceManager().findPreference("solde");
            balanceOnMainScreen.setOnPreferenceClickListener((preference) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(balanceOnMainScreen.isChecked()){
                    editor.putBoolean("balance_on_screen", true);
                } else{
                    editor.putBoolean("balance_on_screen", false);
                }
                editor.apply();
                return true;
            });

            if(useBiometry){
                biometryPreference.setChecked(true);
            } else{
                biometryPreference.setChecked(false);
            }

            if(balanceOnScreen){
                balanceOnMainScreen.setChecked(true);
            } else{
                balanceOnMainScreen.setChecked(false);
            }


            Preference api = findPreference("api_key");
            api.setSummary(apikey);
            Preference userID = findPreference("userID");
            userID.setSummary(userid);
            //api.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference){

            Log.d("Debug", "A preference has been clicked.");
            if(preference.getKey().equals("biometry")){
                Log.d("In the loop", null);
                SwitchPreferenceCompat biometry = (SwitchPreferenceCompat)preference;
                SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if(biometry.isChecked()){
                    editor.putBoolean("use_biometry", true);
                } else{
                    editor.putBoolean("use_biometry", false);
                }
                editor.apply();

            }
            /*if(preference.getKey().equals("api_key")){
                preference.setSummary(apikey);
            }*/
            return false;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue){
            Log.d("Debug", "A preference has been changed.");
            return false;
        }


    }
}
