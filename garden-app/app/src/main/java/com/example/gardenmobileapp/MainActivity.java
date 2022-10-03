package com.example.gardenmobileapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final String ARDUINO_BTH_NAME = "Redmi 9";

    NumberPicker led3, led4, speed;
    Button bthButton;
    private BluetoothAdapter bthAdapter;
    private BluetoothDevice targetDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //disable all
        disableEnableControls(false, findViewById(R.id.mainView));

        //Set Number Picker Min/Max Value
        led3 = findViewById(R.id.led3);
        led4 = findViewById(R.id.led4);
        speed = findViewById(R.id.speed);
        led3.setMaxValue(5);
        led3.setMinValue(0);
        led4.setMaxValue(5);
        led4.setMinValue(0);
        speed.setMaxValue(5);
        speed.setMinValue(0);

        //BTH
        bthButton = findViewById(R.id.BTHButton);
        bthButton.setEnabled(true);
        bthAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bthAdapter == null) {
            showToast("BT is not available on this device ");
            finish();
        }

        bthButton.setOnClickListener(v -> {
            if (isBTHAvailable()) {
                disableEnableControls(true, findViewById(R.id.mainView));
            }
        });

    }

    //toast message function
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void disableEnableControls(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }
    @Override
    public void onActivityResult (int reqID , int res , Intent data ) {
        super.onActivityResult(reqID, res, data);
        if (reqID == REQUEST_ENABLE_BT && res == Activity.RESULT_OK) {
            // BT enabled
        }
        if (reqID == REQUEST_ENABLE_BT && res == Activity.RESULT_CANCELED) {
            // BT enabling process aborted
        }
    }

    private boolean isBTHAvailable(){
        //Enable BTH
        if (!bthAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                showToast("Permission Denied");
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Discovering BTH
        if(!bthAdapter.isDiscovering()){
            showToast("BTH Discovering ON");
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_DISCOVER_BT);
        }

        //BTH DEVICES
        if (bthAdapter.isEnabled()){
            targetDevice = null;
            Set<BluetoothDevice> pairedDevices = bthAdapter.getBondedDevices();
            for (BluetoothDevice device: pairedDevices){
                if (device.getName().equals(ARDUINO_BTH_NAME)) {
                    targetDevice = device;
                    showToast("Paired to " + targetDevice.getName());
                    return true;
                }
            }
        }

        showToast(ARDUINO_BTH_NAME + " not Paired");
        return false;
    }

}