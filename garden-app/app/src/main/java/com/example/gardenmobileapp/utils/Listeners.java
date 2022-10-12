package com.example.gardenmobileapp.utils;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gardenmobileapp.R;

import org.json.JSONException;

public class Listeners {

    //ALARM LISTENER

    public static View.OnClickListener getOnClickAlarm() {
        return v -> {
            try {
                ApiUtils.doPost(ApiUtils.stdURL + "/alarm", false, v.getContext());
                v.setEnabled(false);
                v.setVisibility(View.INVISIBLE);
                showToast("Alarm OFF", v.getContext());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };
    }

    public static CompoundButton.OnCheckedChangeListener getOnChangeSwitch(BthUtils bth, @Nullable NumberPicker speed){

        return (buttonView, isChecked) -> {
            switch (buttonView.getId()){
                case R.id.led1:
                    bth.setLed1(boolToInt(isChecked));
                    break;
                case R.id.led2:
                    bth.setLed2(boolToInt(isChecked));
                    break;
                case R.id.IrrigationOnOffButton:
                    bth.setIrrigationSpeed(boolToInt(isChecked));
                    speed.setEnabled(isChecked);
                    break;
                default:
                    Log.d("Listener", buttonView.getContext().toString());
            }
        };
    }

    public static NumberPicker.OnValueChangeListener getOnChangeNumberPicker (BthUtils bth) {
        return (picker, oldVal, newVal) -> {
            switch (picker.getId()) {
                case R.id.led3:
                    bth.setLed3(newVal);
                    showToast("led3" + newVal, picker.getContext());
                    break;
                case R.id.led4:
                    bth.setLed4(newVal);
                    showToast("led4" + newVal, picker.getContext());
                    break;
                case R.id.speed:
                    bth.setIrrigationSpeed(newVal);
                    showToast("irrigation" + newVal, picker.getContext());
                    break;
                default:
                    Log.d("Listener", picker.getContext().toString());

            }
        };
    }

    //toast message function
    public static void showToast(String msg, Context context) {
        Toast.makeText(context , msg, Toast.LENGTH_SHORT).show();
    }

    private static int boolToInt(boolean b) {
        return b ? 1 : 0;
    }
}
