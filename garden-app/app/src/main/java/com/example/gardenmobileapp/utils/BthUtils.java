package com.example.gardenmobileapp.utils;

public class BthUtils {
    private int  irrigationSpeed, mode;
    private int led1;
    private int led2;
    private int led3;
    private int led4;


    public BthUtils() {}

    public String getBthString(){
        return irrigationSpeed + "," + led1 + "," + led2 + "," + led3 + "," + led4 + "," + mode;
    }

    public void setIrrigationSpeed(int irrigationSpeed) {
        this.irrigationSpeed = irrigationSpeed;
    }
    public void setLed1(int led1) {
        this.led1 = led1;
    }
    public void setLed2(int led2) {
        this.led2 = led2;
    }
    public void setLed3(int led3) {
        this.led3 = led3;
    }
    public void setLed4(int led4) {
        this.led4 = led4;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
