package com.example.gardenmobileapp;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
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
    private static boolean PERMISSION = false;

    NumberPicker led3, led4, speed;
    Button bthButton;
    BluetoothManager bthManager;
    View mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set Number Picker Min/Max Value

        disableEnableControls(false, findViewById(R.id.mainView));
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
        bthManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);

        //BTH Permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED){

            PERMISSION = true;
        }

        bthButton.setOnClickListener(v -> {
            if(PERMISSION){
                //check BTH connection
                if (bthManager.getAdapter() == null) {
                    showToast("BHT Connection Failed");
                } else {

                    showToast("BHT Connected");
                }

                //Enable BTH
                if (!bthManager.getAdapter().isEnabled()) {
                    showToast("BTH ENABLE");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }

                //Discovering BTH
                if(!bthManager.getAdapter().isDiscovering()){
                    showToast("BTH Discovering ON");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }


                //BTH DEVICES
                if (bthManager.getAdapter().isEnabled()){
                    Set<BluetoothDevice> devices = bthManager.getAdapter().getBondedDevices();
                    for (BluetoothDevice device: devices){
                        //showToast(device.getName()+" "+ bthManager.getConnectionState(device, BluetoothGatt.GATT));
                        if (device.getName().equals(ARDUINO_BTH_NAME)){

                            if(bthManager.getConnectionState(device, BluetoothGatt.GATT) == BluetoothProfile.STATE_CONNECTED){
                                showToast("Connected to " + ARDUINO_BTH_NAME);
                                //disableEnableControls(true, findViewById(R.id.mainView));

                            }
                            else{
                                showToast("Paired to " + ARDUINO_BTH_NAME + " but not connected");

                            }
                        }
                    }
                }
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
}