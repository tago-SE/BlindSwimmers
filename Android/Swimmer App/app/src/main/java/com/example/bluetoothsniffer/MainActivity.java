package com.example.bluetoothsniffer;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    public static final int REQUEST_ENABLE_BT = 1000;
    public static final int REQUEST_ACCESS_LOCATION = 1001;

    // period for scan, 5000 ms
    private static final long SCAN_PERIOD = 5000;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private FloatingActionButton fabSniffButton;

    private ArrayList<BluetoothDevice> mDeviceList;
    private BTDeviceArrayAdapter mAdapter;
    private TextView mScanInfoView;

    String deviceName;   ///device name

    private int RSSI = 0;

    private void initBLE() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showToast("BLE is not supported");
            finish();
        } else {
            showToast("BLE is supported");
            // Access Location is a "dangerous" permission
            int hasAccessLocation = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasAccessLocation != PackageManager.PERMISSION_GRANTED) {
                // ask the user for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_LOCATION);
                // the callback method onRequestPermissionsResult gets the result of this request
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // turn on BT
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    // callback for ActivityCompat.requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_LOCATION: {
                // if request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO:
                    // ...
                } else {
                    // stop this activity
                    this.finish();
                }
                break;
            }
        }
    }

    // callback for request to turn on BT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if user chooses not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // device selected, start DeviceActivity (displaying data)
    private void onDeviceSelected(int position) {
        ConnectedDevice.setInstance(mDeviceList.get(position));
        Log.d("banana", "Selected device: " + ConnectedDevice.getInstance().getName());
        showToast(ConnectedDevice.getInstance().toString());
        Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
        intent.putExtra("deviceName", ConnectedDevice.getInstance().getName()); //send the RSSI value forward to DeviceActivity to catch
        intent.putExtra("RSSIValue", RSSI); //send the RSSI value forward to DeviceActivity to catch
        startActivity(intent);
    }



    private void scanLeDevice(final boolean enable){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        if (enable)
        {
            if(!mScanning){

                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (mScanning)
                        {
                            mScanning = false;
                            mBluetoothAdapter.startDiscovery();
                            showToast("BLE scan stopped");
                        }
                    }
                }, SCAN_PERIOD);

                mScanning = true;
            }
        }else{

            if (mScanning)
            {
                mScanning = false;
                mBluetoothAdapter.cancelDiscovery();
                showToast("BLE scan stopped");
            }
        }
    }

    /**
     * Below: Manage activity, and hence bluetooth, life cycle,
     * via onCreate, onStart and onStop.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        System.out.println("Main Activity");
        mDeviceList = new ArrayList<>();

        mHandler = new Handler();

        mScanInfoView = findViewById(R.id.scanInfo);
        final Intent intent = new Intent(MainActivity.this, DeviceActivity.class);

        fabSniffButton = (FloatingActionButton) findViewById(R.id.fabSniff);
        fabSniffButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(fabSniffButton.isPressed())
                {
                    Intent intent = new Intent(MainActivity.this, SniffActivity.class);
                    startActivity(intent);
                }
            }
        });

        Button startScanButton = findViewById(R.id.startScanButton);
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeviceList.clear();
                scanLeDevice(true);
            }
        });

        ListView scanListView = findViewById(R.id.scanListView);


        mAdapter = new BTDeviceArrayAdapter(this, mDeviceList);
        scanListView.setAdapter(mAdapter);
        scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                onDeviceSelected(position);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initBLE();
        mDeviceList.clear();
        scanLeDevice(true);
        startScanBluetooth();
        mBluetoothAdapter.startDiscovery();
    }

    // TODO ...
    @Override
    protected void onStop() {
        super.onStop();
        // stop scanning
        scanLeDevice(false);
        mDeviceList.clear();
        mAdapter.notifyDataSetChanged();
        mBluetoothAdapter.cancelDiscovery();
        // NB !release additional resources
        // ...BleGatt...
    }

    // short messages
    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void startScanBluetooth()
    {
        final BroadcastReceiver bReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    RSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    //Log.d("banana", "onReceive: " + RSSI);

                    deviceName = device.getName();
                    if(deviceName==null) {
                        deviceName = "null";
                    }
                    else if (!mDeviceList.contains(device) && deviceName.startsWith("Arduino Swimmer"))
                    {
                        mDeviceList.add(device);
                        mAdapter.notifyDataSetChanged();
                        String msg = getString(R.string.found_devices_msg, mDeviceList.size());
                        mScanInfoView.setText(msg);
                        Log.d("banana", "Swimmer found as: " + device.getName() + ", Address: " + device.getAddress());
                    }
                    Log.d("banana", ". New Device found in scan: " + device.getName() + ", Address: " + device.getAddress());
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReceiver, filter);
    }

    @Override
    public void onClick(View v) {



    }
}