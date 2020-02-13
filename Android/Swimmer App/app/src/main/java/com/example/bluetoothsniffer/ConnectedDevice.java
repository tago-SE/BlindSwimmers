package com.example.bluetoothsniffer;

import android.bluetooth.BluetoothDevice;

/**
 * An ugly hack to administrate the selected Bluetooth device
 * between activities.
 */
public class ConnectedDevice {

    private static BluetoothDevice theDevice = null;
    private static final Object lock = new Object();

    private ConnectedDevice() {
    }

    public static BluetoothDevice getInstance() {
        synchronized (lock) {
            return theDevice;
        }
    }

    public static void setInstance(BluetoothDevice newDevice) {
        synchronized (lock) {
            theDevice = newDevice;
        }
    }

    public static void removeInstance() {
        synchronized(lock) {
            theDevice = null;
        }
    }
}