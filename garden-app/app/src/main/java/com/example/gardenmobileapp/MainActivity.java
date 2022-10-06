package com.example.gardenmobileapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final String ARDUINO_BTH_NAME = "Redmi 9";

    NumberPicker led3, led4, speed;
    SwitchCompat led1, led2;
    private BluetoothAdapter bthAdapter;
    private BluetoothDevice targetDevice;
    private final String stdURL = "http://192.168.178.109:8000/";
    private boolean alarmStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check Internet Connection

        //init view
        initView();

        //permission Check
        /**
         * TODO permission Check
        //checkPermission(this);
        */

        //BTH
        findViewById(R.id.BTHButton).setOnClickListener(onClickBTHButton);
    }

    private void initView() {
        //disable all
        disableEnableControls(false, findViewById(R.id.mainView));

        //Set Number Picker Min/Max Value
        led1 = findViewById(R.id.led1);
        led2 = findViewById(R.id.led2);
        led3 = findViewById(R.id.led3);
        led4 = findViewById(R.id.led4);
        speed = findViewById(R.id.speed);
        led3.setMaxValue(5);
        led3.setMinValue(0);
        led4.setMaxValue(5);
        led4.setMinValue(0);
        speed.setMaxValue(5);
        speed.setMinValue(0);

        findViewById(R.id.BTHButton).setEnabled(true);
    }

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    /*
    LISTENER
     */
    //BTH BUTTON LISTENER
    @SuppressLint("MissingPermission")
    private final View.OnClickListener onClickBTHButton = v -> {

        //check BTH availability
        bthAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bthAdapter == null) {
            showToast("BT is not available on this device ");
            finish();
        }

        //Enable BTH
        if (!bthAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Discovering BTH

        if (!bthAdapter.isDiscovering()) {
            showToast("BTH Discovering ON");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_DISCOVER_BT);
        }

        //BTH DEVICES
        if (bthAdapter.isEnabled()) {
            targetDevice = null;
            Set<BluetoothDevice> pairedDevices = bthAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (!device.getName().equals(ARDUINO_BTH_NAME)) {
                    targetDevice = device;
                    showToast("Paired to " + targetDevice.getName());
                    disableEnableControls(true, findViewById(R.id.mainView));
                    alarmGet();
                    try {
                        doPost(stdURL+"mobile/bth", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return;

                }
            }
        }

        showToast(ARDUINO_BTH_NAME + " not Paired");
    };

    private final View.OnClickListener onClickAlarm = v -> {
        try {
            doPost(stdURL+"alarm",false);
            findViewById(R.id.alarmIcon).setEnabled(false);
            showToast("Alarm OFF");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    //LED 1,2 LISTENER
    /*private final CompoundButton.OnCheckedChangeListener onChangeSwitch = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            URL url = new URL()
            doPost()
        }
    }*/

    //API
    private void alarmGet() {
        //RequestQueue initialized
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = stdURL+"alarm";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        alarmStatus = (boolean) response.get("status");
                        if(alarmStatus){
                            findViewById(R.id.alarmIcon).isEnabled();
                            findViewById(R.id.alarmIcon).setVisibility(View.VISIBLE);
                            findViewById(R.id.alarmIcon).setOnClickListener(onClickAlarm);
                        }
                        else{
                            findViewById(R.id.alarmIcon).setEnabled(false);
                            findViewById(R.id.alarmIcon).setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> showToast(error.toString()));
        //showToast(jsonObjectRequest.getBody().toString());
        requestQueue.add(jsonObjectRequest);
    }
    private void doPost(String url, boolean status) throws JSONException {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject().put("status", status),
                response -> Log.d("POST",response.toString()),
                error -> Log.d("POST",error.toString()));
        requestQueue.add(jsonObjectRequest);
    }
}