package com.example.gardenmobileapp.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BthConnection {
    private final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bthAdapter;
    private BluetoothDevice targetDevice;
    private BluetoothSocket bthSocket;
    private OutputStream outStream = null;

    @SuppressLint("MissingPermission")
    public Boolean socketConnection(){

        try {
            bthSocket=targetDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            return false;
        }

        try{
            bthSocket.connect();
            outStream = bthSocket.getOutputStream();
            return true;
        }
        catch (IOException closeException){
            try{
                outStream.close();
                bthSocket.close();
            }
            catch (IOException ceXC){
                ceXC.printStackTrace();
            }
            return false;
        }
    }

    public boolean sendMessageBluetooth(String message) {
            if (outStream == null) {
                return false;
            }
            byte[] msgBuffer = message.getBytes();
            try {
                outStream.write(msgBuffer);
                return true;
            }
            catch (IOException e) {
                return false;
            }
    }

    public BluetoothAdapter getBthAdapter() {
        return bthAdapter;
    }

    public void setBthAdapter(BluetoothAdapter bthAdapter) {
        this.bthAdapter = bthAdapter;
    }

    public void setTargetDevice(BluetoothDevice targetDevice) {
        this.targetDevice = targetDevice;
    }

}
