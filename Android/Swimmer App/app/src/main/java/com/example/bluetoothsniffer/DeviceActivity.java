package com.example.bluetoothsniffer;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * This is where we manage the BLE device and the corresponding services, characteristics et c.
 * <p>
 * NB: In this simple example there is no other way to turn off notifications than to
 * leave the activity (the BluetoothGatt is disconnected and closed in activity.onStop).
 */
public class DeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MYTAG";
    /**
     * Documentation on UUID:s and such for services on a BBC Micro:bit.
     * Characteristics et c. are found at
     * https://lancaster-university.github.io/microbit-docs/resources/bluetooth/bluetooth_profile.html
     */
    // Below: gui stuff...
    private TextView mDeviceName;
    private TextView mDeviceOtherInfo;
    private TextView mDeviceRSSI;
    private EditText mTextInputSensorOne;
    private EditText mTextInputSensorTwo;

    private Button backButton;
    private Button trainButton;
    private Button submitSensorButton;

    private String deviceName;

    public static final UUID ACCELEROMETER_SERVICE_UUID =
            UUID.fromString("E95D0753-251D-470A-A062-FA1922DFA9A8");
    public static final UUID ACCELEROMETER_DATA_CHARACTERISTIC_UUID =
            UUID.fromString("E95DCA4B-251D-470A-A062-FA1922DFA9A8");
    public static final UUID ACCELEROMETER_PERIOD_CHARACTERISTIC_UUID =
            UUID.fromString("E95DFB24-251D-470A-A062-FA1922DFA9A8");
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice mConnectedDevice = null;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothGattService mAccelerometerService = null;

    private Handler mHandler;

    @Override
    protected void onStart() {
        super.onStart();
        mConnectedDevice = ConnectedDevice.getInstance();
        if (mConnectedDevice != null) {
            if(mDeviceName != null)
            {
                mDeviceName.setText(mConnectedDevice.getName());

                if(mConnectedDevice.getName() == null)
                {
                    mDeviceName.setText("No set name (null)");
                }

                //get the RSSI value
                mDeviceRSSI.setText("" + getIntent().getExtras().getInt("RSSIValue", 0));
                deviceName = (String) getIntent().getExtras().get("deviceName");

                System.out.println("Device name är: " + deviceName);

                mDeviceOtherInfo.setText("None");
                connect();
            }
            else
            {
                showToast("mDeviceView == null");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        ConnectedDevice.removeInstance();
        mConnectedDevice = null;

        finish();
    }

    private void connect() {
        if (mConnectedDevice != null) {
            // register call backs for bluetooth gatt
            mBluetoothGatt = mConnectedDevice.connectGatt(this, false, mBtGattCallback);
            Log.d(TAG, "connect: connctGatt called");
        }
    }

    /**
     * Callbacks for bluetooth gatt changes/updates
     * The documentation is not clear, but (some of?) the callback methods seems to
     * be executed on a worker thread - hence use a Handler when updating the ui.
     */
    private BluetoothGattCallback mBtGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                mBluetoothGatt = gatt;
                gatt.discoverServices();
                mHandler.post(new Runnable() {
                    public void run() {
                        //mDataView.setText("Connected");
                        showToast("Connected to device");
                    }
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // close connection and display info in ui
                mBluetoothGatt = null;
                mHandler.post(new Runnable() {
                    public void run() {
                        //mDataView.setText("Disconnected");
                        showToast("Device disconnected");
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                // debug, list services

                BluetoothGattService arduinoService = null;
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    String uuid = service.getUuid().toString();
                    Log.d(TAG, "service: " + uuid);

                    //Unique for every Arduino
                    if(uuid.equals("19b10001-e8f2-537e-4f6c-d104768a1214"))
                    {
                        arduinoService = service;
                    }
                }

                if(arduinoService == null)
                {
                    Log.d(TAG, "Could not find a matching service UUID name..");
                }
                else
                {
                    BluetoothGattCharacteristic characteristic = gatt.getService(arduinoService.getUuid()).getCharacteristic(arduinoService.getUuid());
                    gatt.readCharacteristic(characteristic);
                }
            }
        }

        @Override
        public void onDescriptorWrite(final BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite: descriptor " + descriptor.getUuid());
            Log.d(TAG, "onDescriptorWrite: status " + status);
//            mHandler.post(new Runnable() {
//                public void run() {
//                    enableAccelerometerDataNotifications(gatt);
//                }
//            });
            if (CLIENT_CHARACTERISTIC_CONFIG.equals(descriptor.getUuid()) && status == BluetoothGatt.GATT_SUCCESS) {

                mHandler.post(new Runnable() {
                    public void run() {
                        showToast("Acc-data notifications enabled");
                        //mDeviceName.setText("Accelerometer service");
                    }
                });
            }
        }

        /**
         * Call back called on characteristic changes, e.g. when a data value is changed.
         * This is where we receive notifications on updates of accelerometer data.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged" + characteristic.getUuid().toString());
            // TODO: check which service and characteristic caused this call (in this simple
            // example we assume it's the accelerometer sensor)
            BluetoothGattCharacteristic irDataConfig = mAccelerometerService.getCharacteristic(ACCELEROMETER_DATA_CHARACTERISTIC_UUID);
            final byte[] value = irDataConfig.getValue();
            // update ui
            mHandler.post(new Runnable() {
                public void run() {


                }
            });
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, characteristic.getUuid().toString());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] bytesReceived = characteristic.getValue();

            String strReceived = "";
            for (int i = 0; i < bytesReceived.length; i++) {
                strReceived = strReceived + (char) bytesReceived[i];
            }

            Log.d(TAG, "characteristic: "  + strReceived);
            Log.d(TAG, characteristic.getUuid().toString());
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        System.out.println("DeviceActivity");

        trainButton = findViewById(R.id.train_button);
        mDeviceName = findViewById(R.id.textDeviceName);
        mDeviceOtherInfo = findViewById(R.id.textOther);
        mDeviceRSSI = findViewById(R.id.textDeviceRSSI);
        mTextInputSensorOne = findViewById(R.id.editTextSensorOne);
        mTextInputSensorTwo = findViewById(R.id.editTextSensorTwo);

        submitSensorButton = findViewById(R.id.submitSensorButton);
        backButton = findViewById(R.id.buttonBack);
        backButton.setText("Back");

        submitSensorButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        trainButton.setOnClickListener(this);

        mHandler = new Handler();

        System.out.println("+++++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++++");
    }




    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {

        if(v==trainButton){
            startActivity(new Intent(DeviceActivity.this, TrainActivity.class));
        }

        else if(v==backButton){
            startActivity(new Intent(DeviceActivity.this, MainActivity.class));
        }



        else if(v==submitSensorButton)
        {
            String sensorInputOne = mTextInputSensorOne.getText().toString();
            String sensorInputTwo = mTextInputSensorTwo.getText().toString();

            if(!sensorInputOne.isEmpty() && !sensorInputTwo.isEmpty())
            {
                //TODO
                //send sensor name to arduino
            }
            else
            {
                showToast("Please enter sensor name");
            }
        }
    }
}
