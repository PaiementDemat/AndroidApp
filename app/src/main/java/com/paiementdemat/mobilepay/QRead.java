package com.paiementdemat.mobilepay;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QRead extends AppCompatActivity {

    ZXingScannerView qrCodeScanner;
    private final int REQUEST_PERMISSION_CAMERA=1;
    private NfcAdapter nfcAdapter;
    private TextView typeOfScan;
    //NFC
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private Context context;

    ZXingScannerView.ResultHandler mResultHandler = new ZXingScannerView.ResultHandler() {
        @Override
        public void handleResult(Result result) {
            Context context = getApplicationContext();
            if(result != null){
                Toast toast = Toast.makeText(context, result.toString(), Toast.LENGTH_LONG);
                toast.show();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrread);
        Intent intent = getIntent();
        context = this;

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        typeOfScan = findViewById(R.id.typeOfScan);
        if (nfcAdapter == null) {
            typeOfScan.setText(R.string.ScanToPay);

        }
        else{
            if (!nfcAdapter.isEnabled()){
                typeOfScan.setText(R.string.ScanOrActivateToPay);

            } else{
                typeOfScan.setText(R.string.ScanOrApproachToPay);
                handleIntent(getIntent());
            }
        }
        qrCodeScanner = findViewById(R.id.qrCodeScanner);
        setScannerProperties();
    }


    private void setScannerProperties() {
        List<BarcodeFormat> listBC = new ArrayList<>();
        listBC.add(BarcodeFormat.QR_CODE);

        qrCodeScanner.setFormats(listBC);
        qrCodeScanner.setAutoFocus(true);
        qrCodeScanner.setLaserColor(R.color.colorAccent);
        qrCodeScanner.setMaskColor(R.color.colorAccent);
        /*if (Build.MANUFACTURER.equals(HUAWEI, ignoreCase = true)) qrCodeScanner.setAspectTolerance(0.5f);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
            }
        }
        qrCodeScanner.startCamera();
        qrCodeScanner.setResultHandler(mResultHandler);
        setupForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeScanner.stopCamera();
        stopForegroundDispatch(this, nfcAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        if(adapter != null){
            final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

            IntentFilter[] filters = new IntentFilter[1];
            String[][] techList = new String[][]{};

            // Notice that this is the same filter as in our manifest.
            filters[0] = new IntentFilter();
            filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            filters[0].addCategory(Intent.CATEGORY_DEFAULT);
            try {
                filters[0].addDataType(MIME_TEXT_PLAIN);
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("Check your mime type.");
            }

            adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
        }
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        if(adapter != null){
            adapter.disableForegroundDispatch(activity);
        }

    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {

            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {

                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {

                String s = new String(ndefRecord.getType());

                String s2 = new String(NdefRecord.RTD_TEXT);

                if (ndefRecord.getTnf() == 2 && Arrays.equals(ndefRecord.getType(), "text/plain".getBytes())) {

                    try {

                        String read = readText(ndefRecord);

                        return read;
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {
            /*
             * See NFC forum specification for "Text Record Type Definition" at 3.2.1
             *
             * http://www.nfc-forum.org/specs/
             *
             * bit_7 defines encoding
             * bit_6 reserved for future use, must be 0
             * bit_5..0 length of IANA language code
             */

            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            String str = null;
            try{
                str = new String(payload, 0, payload.length, textEncoding);
            }
            catch(Exception e){
                Log.e(TAG, e.getMessage());
            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(R.string.AuthorizePayment);
                dialog.setTitle(R.string.Pay);
                dialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                //typeOfScan.setText("Read content: " + result);
            }
        }
    }


}


