package com.example.bluetoothsniffer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SniffActivity extends AppCompatActivity
{
    private Button backButton;
    private FloatingActionButton fabStartSniffing;
    private FloatingActionButton fabStopSniffing;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        setContentView(R.layout.activity_sniff);
        backButton = findViewById(R.id.buttonBack);
        backButton.setText("Back");
        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(backButton.isPressed())
                {
                    startActivity(new Intent(SniffActivity.this, MainActivity.class)); //SHOULD ALSO STOP SCANNING ==================================================
                }
            }
        });

        fabStartSniffing = (FloatingActionButton) findViewById(R.id.fabStart);
        fabStartSniffing.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(fabStartSniffing.isPressed())
                {
                    //clear adapter array
                    showToast("Start sniffing");
                    bluetoothAdapter.startDiscovery();
                }
            }
        });

        fabStopSniffing = (FloatingActionButton) findViewById(R.id.fabStop);
        fabStopSniffing.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(fabStopSniffing.isPressed())
                {
                    showToast("Stop sniffing");
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        });

        final BroadcastReceiver bReceiver = new BroadcastReceiver()
        {
            int numberOfDevices = 0;
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                Log.d("banana", "numberOfDevices: " + this.numberOfDevices);

                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Create a new device item
                    //DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                    // Add it to our adapter
                    //mAdapter.add(newDevice);

                    Log.d("banana", numberOfDevices + ". New Device: " + device.getName() + ", Address: " + device.getAddress());

                    numberOfDevices++;
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bReceiver, filter);


    }

    // short messages
    protected void showToast(String msg)
    {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}

