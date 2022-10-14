package com.example.gardenmobileapp;

import static com.example.gardenmobileapp.utils.ApiUtils.doGet;
import static com.example.gardenmobileapp.utils.ApiUtils.doPost;
import static com.example.gardenmobileapp.utils.ApiUtils.stdURL;
import static com.example.gardenmobileapp.utils.Listeners.showToast;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.example.gardenmobileapp.utils.ApiUtils;
import com.example.gardenmobileapp.utils.BthConnection;
import com.example.gardenmobileapp.utils.BthUtils;
import com.example.gardenmobileapp.utils.Listeners;
import com.example.gardenmobileapp.utils.LoadingDialog;
import com.example.gardenmobileapp.utils.VolleyCallback;

import org.json.JSONException;

import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    private static final int POLLING_TIME_RATE = 1000;
    private static final String ARDUINO_BTH_NAME = "H3";

    public BthUtils bthUtils = new BthUtils();
    private final BthConnection bthConnection = new BthConnection();
    LoadingDialog loadingDialog;

    Handler bthHandler = new Handler();
    Runnable bthPolling = new Runnable() {
        @Override
        public void run() {
            bthConnection.sendMessageBluetooth(bthUtils.getBthString());
            ApiUtils.doGet(stdURL + "/alarm", MainActivity.this, callbackAlarm);
            bthHandler.postDelayed(bthPolling, POLLING_TIME_RATE); //wait 4 sec and run again
        }
    };
    Handler alarmHandler = new Handler();
    Runnable alarmPolling = new Runnable() {
        @Override
        public void run() {
            ApiUtils.doGet(stdURL + "/alarm", MainActivity.this, callbackAlarm);
            alarmHandler.postDelayed(alarmPolling, POLLING_TIME_RATE); //wait 4 sec and run again
        }
    };

    Handler endResponseHandler = new Handler();
    Runnable endResponsePolling = new Runnable() {
        @Override
        public void run() {
            bthConnection.sendMessageBluetooth(bthUtils.getBthString());
            doGet(stdURL + "/bth", MainActivity.this, callbackEnd);
            endResponseHandler.postDelayed(endResponsePolling, POLLING_TIME_RATE);
        }
    };
    Handler startResponseHandler = new Handler();
    Runnable startResponsePolling = new Runnable() {
        @Override
        public void run() {
            bthConnection.sendMessageBluetooth(bthUtils.getBthString());
            doGet(stdURL + "/bth", MainActivity.this, callbackStart);
            startResponseHandler.postDelayed(startResponsePolling, POLLING_TIME_RATE);
        }
    };

    public VolleyCallback callbackAlarm = new VolleyCallback() {
        @Override
        public void onSuccess(Object result) {
            if ((boolean) result) {
                findViewById(R.id.alarmIcon).setEnabled(true);
                findViewById(R.id.alarmIcon).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.alarmIcon).setEnabled(false);
                findViewById(R.id.alarmIcon).setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onError(Object result) {
            Log.d("API", "Error Alarm Api: " + result);
        }
    };

    public VolleyCallback callbackEnd = new VolleyCallback() {
        @Override
        public void onSuccess(Object result) {
            Log.d("result", result.toString());
            if (!(boolean) result) {
                loadingDialog.dismissDialog();
                stopPolling(endResponseHandler, endResponsePolling);
                finish();
            }
        }

        @Override
        public void onError(Object result) {
            Log.d("API", "onError: " + result);
        }
    };

    public VolleyCallback callbackStart = new VolleyCallback() {
        @Override
        public void onSuccess(Object result) {
            if ((boolean) result) {
                loadingDialog.dismissDialog();
                stopPolling(startResponseHandler, startResponsePolling);
            }
        }

        @Override
        public void onError(Object result) {
            Log.d("API", "onError: " + result);
        }
    };

    NumberPicker led3, led4, speed;
    SwitchCompat led1, led2, irrigationButton;
    Button bthBtn;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check Internet Connection
        if (!ApiUtils.checkInternetConnection(this)) {
            showToast("No Internet Connection", this);
            finish();
        }
        //init view
        initView();
        startPolling(alarmHandler,alarmPolling);
        //permission Check
        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPolling(alarmHandler,alarmPolling);
        finish();
    }

    private void initView() {
        //disable all
        disableEnableControls(false, findViewById(R.id.mainView));

        led1 = findViewById(R.id.led1);
        led1.setChecked(false);
        led1.setOnCheckedChangeListener(Listeners.getOnChangeSwitch(bthUtils, null));

        led2 = findViewById(R.id.led2);
        led2.setChecked(false);
        led2.setOnCheckedChangeListener(Listeners.getOnChangeSwitch(bthUtils, null));

        led3 = findViewById(R.id.led3);
        led3.setMaxValue(4);
        led3.setMinValue(0);
        led3.setOnValueChangedListener(Listeners.getOnChangeNumberPicker(bthUtils));

        led4 = findViewById(R.id.led4);
        led4.setMaxValue(4);
        led4.setMinValue(0);
        led4.setOnValueChangedListener(Listeners.getOnChangeNumberPicker(bthUtils));

        speed = findViewById(R.id.speed);
        speed.setMaxValue(5);
        speed.setMinValue(1);
        speed.setOnValueChangedListener(Listeners.getOnChangeNumberPicker(bthUtils));

        irrigationButton = findViewById(R.id.IrrigationOnOffButton);
        irrigationButton.setChecked(false);
        irrigationButton.setOnCheckedChangeListener(Listeners.getOnChangeSwitch(bthUtils, speed));

        findViewById(R.id.alarmIcon).setOnClickListener(Listeners.getOnClickAlarm());
        findViewById(R.id.alarmIcon).setEnabled(true);

        bthBtn = findViewById(R.id.BTHButton);
        bthBtn.setEnabled(true);
        bthBtn.setOnClickListener(onClickBTHButton);

        ApiUtils.doGet(stdURL + "/alarm", this, callbackAlarm);
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


    //BTH BUTTON LISTENER
    @SuppressLint("MissingPermission")
    private final View.OnClickListener onClickBTHButton = v -> {

        //check BTH availability
        bthConnection.setBthAdapter(BluetoothAdapter.getDefaultAdapter());
        if (bthConnection.getBthAdapter() == null) {
            showToast("BT is not available on this device ", this);
            finish();
        }

        //Enable BTH
        if (!bthConnection.getBthAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Discovering BTH

        if (!bthConnection.getBthAdapter().isDiscovering()) {
            showToast("BTH Discovering ON", this);
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(intent, REQUEST_DISCOVER_BT);
        }

        //BTH DEVICES
        if (bthConnection.getBthAdapter().isEnabled()) {
            Set<BluetoothDevice> pairedDevices = bthConnection.getBthAdapter().getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(ARDUINO_BTH_NAME)) {
                    bthConnection.setTargetDevice(device);
                    if (bthConnection.socketConnection()) {
                        try {
                            doPost(stdURL + "/mobile/bth", true, this);
                            loadingDialog = new LoadingDialog(MainActivity.this);
                            loadingDialog.startLoadingDialog();
                            startPolling(startResponseHandler, startResponsePolling);
                            disableEnableControls(true, findViewById(R.id.mainView));
                            speed.setEnabled(false);
                            bthConnection.sendMessageBluetooth(bthUtils.getBthString());
                            bthBtn.setText("Disconnect");
                            bthBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    bthUtils.setMode(2);
                                    stopPolling(bthHandler, bthPolling);

                                    try {
                                        doPost(stdURL + "/mobile/bth", false, view.getContext());
                                        loadingDialog = new LoadingDialog(MainActivity.this);
                                        loadingDialog.startLoadingDialog();
                                        startPolling(endResponseHandler, endResponsePolling);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            bthUtils.setMode(1);


                            startPolling(bthHandler, bthPolling);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        showToast("Bth Socket Connection Failed", this);
                    }
                }
            }
        }
    };

    private void stopPolling(Handler handler, Runnable runnable) {
        handler.removeCallbacks(runnable);
    }

    private void startPolling(Handler handler, Runnable runnable) {
        handler.postDelayed(runnable, 0); //wait 0 ms and run
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }
    }
}