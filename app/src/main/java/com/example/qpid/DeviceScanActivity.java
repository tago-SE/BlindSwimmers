package com.example.qpid;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;

import java.util.logging.Handler;

public class DeviceScanActivity extends ListActivity {
    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;
}
