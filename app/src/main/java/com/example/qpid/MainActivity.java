package com.example.qpid;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean mScanning;
    private Handler handler = new Handler();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private TextView mText;
    private Button mAdvertiseButton;
    private Button mDiscoverButton;
    private List<ScanFilter> filters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filters = new ArrayList<>();
        //List<ScanFilter> filters = new List<>();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        //    finish();
        }
        else{
            Toast.makeText(this, R.string.ble_is_supported, Toast.LENGTH_SHORT).show();
          //  finish();
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);

        }

        if( !BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported() ) {
            Toast.makeText( this, "Multiple advertisement not supported", Toast.LENGTH_SHORT ).show();
            mAdvertiseButton.setEnabled( false );
            mDiscoverButton.setEnabled( false );
        }

        mText = (TextView) findViewById( R.id.text );
        mDiscoverButton = (Button) findViewById( R.id.discover_btn );
        mAdvertiseButton = (Button) findViewById( R.id.advertise_btn );

        mDiscoverButton.setOnClickListener( this );
        mAdvertiseButton.setOnClickListener( this );


      //  mBluetoothLeScanner = new BluetoothAdapter();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
      /*  ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid(UUID.fromString( getString(R.string.ble_uuid ) ) ) )
                .build();

        filters.add( filter );*/

       /* ScanSettings settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .build();
        mBluetoothLeScanner.startScan(filters,settings,mScanCallback);*/
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            System.out.println("Geronimo!");
            if( result == null
                    || result.getDevice() == null
                    || TextUtils.isEmpty(result.getDevice().getName()) )
                return;

            StringBuilder builder = new StringBuilder( result.getDevice().getName() );

            builder.append("\n").append(new String(result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0)), Charset.forName("UTF-8")));

            mText.setText(builder.toString());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.e( "BLE", "Discovery onBatchScanResults: " + 0 );
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e( "BLE", "Discovery onScanFailed: " + errorCode );
            super.onScanFailed(errorCode);
        }
    };

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.discover_btn ) {
            discover();
        } else if( v.getId() == R.id.advertise_btn ) {
             advertise();
        }
    }

    private void advertise() {
        BluetoothLeAdvertiser advertiser = BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();
       //advertise(advertiser,settings);
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString( getString( R.string.ble_uuid ) ) );

        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( pUuid )
                //.addServiceData( pUuid, "Data".getBytes( Charset.forName( "UTF-8" ) ) )
                .build();
        AdvertiseCallback advertisingCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.e( "BLE", "Advertising onStartSuccess: " + 0 );
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                Log.e( "BLE", "Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };

        advertiser.startAdvertising( settings, data, advertisingCallback );
    }

    private void discover() {
        System.out.println("in discover");
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid(UUID.fromString( getString(R.string.ble_uuid ) ) ) )
                .build();
        filters.add( filter );
        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .build();

        mBluetoothLeScanner.startScan(filters,settings,mScanCallback);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        }, 10000);
    }


}
