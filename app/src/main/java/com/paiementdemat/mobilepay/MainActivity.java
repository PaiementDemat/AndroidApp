package com.paiementdemat.mobilepay;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(R.string.AskAuthorizations);
                dialog.setTitle(R.string.Authorization);
                dialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 200);
                    }
                });
                dialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), R.string.AuthorizationsRemember, Toast.LENGTH_LONG).show();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();

            }
        }


        dl = findViewById(R.id.activity_main);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.pay:
                        intent = new Intent(getApplicationContext(), QRead.class);
                        startActivity(intent);
                        break;
                    case R.id.settings:

                        break;
                    case R.id.ask:
                        intent = new Intent(getApplicationContext(), QRGen.class);
                        startActivity(intent);
                        break;
                    default:
                        return true;
                }
                return true;

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


}
