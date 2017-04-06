package com.vmks.bluetootharduinolock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int ON_REQUEST_CODE = 3122;
    private static final int VISIBLE_REQUEST_CODE = 1311;
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.bluetoothSwitch)
    Switch bluetoothSwitch;

    @BindView(R.id.pairedListView)
    ListView pairedListView;

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothState();

        IntentFilter filter = new IntentFilter();
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i(TAG, intent.getAction());
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(MainActivity.this, "Scan started", Toast.LENGTH_SHORT).show();

                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Toast.makeText(MainActivity.this, "Device name: " + device.getName(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, device.getName());
            }
        }
    };

    private void checkBluetoothState() {
        if (bluetoothAdapter == null)
            bluetoothSwitch.setChecked(false);
        else
            bluetoothSwitch.setChecked(bluetoothAdapter.isEnabled());

    }

    @OnClick(R.id.getDevBtn)
    public void getDevBtnClick() {
        Log.d(TAG, "startDiscovery()");
        bluetoothAdapter.startDiscovery();
//        Intent vis = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        startActivityForResult(vis, VISIBLE_REQUEST_CODE);
    }

    @OnClick(R.id.listDevBtn)
    public void listDevBtnClick() {
        Toast.makeText(this, "listDevBtnClick()", Toast.LENGTH_SHORT).show();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        ArrayList list = new ArrayList();

        for(BluetoothDevice dev: pairedDevices) list.add(dev.getName());
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        pairedListView.setAdapter(adapter);
    }

    @OnCheckedChanged(R.id.bluetoothSwitch)
    public void checkedChanged(boolean isChecked) {

        if (isChecked) {
            if (!bluetoothAdapter.isEnabled()) {
                Intent on = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(on, ON_REQUEST_CODE);
                Toast.makeText(this, "Turned on: " + bluetoothAdapter.isEnabled(), Toast.LENGTH_SHORT).show();
            }
        } else {
            bluetoothAdapter.disable();
            Toast.makeText(this, "Turned on: " + bluetoothAdapter.isEnabled(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}