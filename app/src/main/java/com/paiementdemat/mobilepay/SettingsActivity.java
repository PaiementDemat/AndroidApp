package com.paiementdemat.mobilepay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

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


    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        SharedPreferences mPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String apikey = sharedPreferences.getString(getString(R.string.api_token), null);
            if(apikey == null) apikey = getString(R.string.api_token);
            String userid = sharedPreferences.getString(getString(R.string.userID), null);
            if(userid == null) userid = getString(R.string.userID);

            Preference api = findPreference("api_key");
            api.setSummary(apikey);
            Preference userID = findPreference("userID");
            userID.setSummary(userid);
            //api.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference){

            /*if(preference.getKey().equals("api_key")){
                preference.setSummary(apikey);
            }*/
            return false;
        }


    }
}
