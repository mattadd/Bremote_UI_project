package com.example.mariamaddai.bremote_ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private CardView ONOFF,Communicate,discoverability,discoverable;
    private Button btnLogout;  //new addition
    private Session session;  //new addition
    BluetoothAdapter bluetoothAdapter;
    String deviceAddress;
    public static String EXTRA_ADDRESS="device_address";
    public ArrayList<BluetoothDevice> BTDevices=new ArrayList<>();
    public DeviceListAdapter deviceListAdapter;
    ListView lvNewDevices;
    //private static final TAG ="MainActivity";

    private final BroadcastReceiver broadcastReceiver1=new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                /*switch(state){
                    case  BluetoothAdapter.STATE_OFF:
                        log.d(TAG, "onReceive:STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        log.d(TAG, "broadcastReceiver1:STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        log.d(TAG, "broadcastReceiver1:STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        log.d(TAG, "broadcastReceiver1:STATE TURNING ON");
                        break;
                }*/
            }
        }

    };

    private final BroadcastReceiver broadcastReceiver2=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action=intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode=intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
            }
        }
    };

    private BroadcastReceiver broadcastReceiver3=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action=intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDevices.add(device);
                deviceListAdapter=new DeviceListAdapter(context,R.layout.activity_discover, BTDevices);
                lvNewDevices.setAdapter(deviceListAdapter);
            }
        }
    };

    /*private final BroadcastReceiver broadcastReceiver4=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            }
        }
    };*/

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        unregisterReceiver(broadcastReceiver1);
        unregisterReceiver(broadcastReceiver2);
        unregisterReceiver(broadcastReceiver3);
        //unregisterReceiver(broadcastReceiver4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ONOFF=(CardView)findViewById(R.id.ONOFF_card);
        Communicate=(CardView)findViewById(R.id.Communicate_card);
        discoverability=(CardView)findViewById(R.id.discoverability_card);
        discoverable=(CardView)findViewById(R.id.discoverable_card);
        lvNewDevices=(ListView) findViewById(R.id.lvNewDevices);
        BTDevices=new ArrayList<>();
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        lvNewDevices.setOnItemClickListener(MainActivity.this);
        ONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* if(bluetoothAdapter==null){
                    Log.d(TAG, "enabledDisableBT:Does not have BT capabilities");
                }*/
                if(!bluetoothAdapter.isEnabled()){
                    Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBTIntent);

                    IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(broadcastReceiver1,BTIntent);
                }
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                    Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(enableBTIntent);

                    IntentFilter BTIntent=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                    registerReceiver(broadcastReceiver1,BTIntent);
                }
            }
        });
        discoverability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
                startActivity(discoverableIntent);
                IntentFilter intentFilter=new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
                registerReceiver(broadcastReceiver2, intentFilter);
            }
        });
        discoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isDiscovering()){
                    bluetoothAdapter.cancelDiscovery();
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    bluetoothAdapter.startDiscovery();
                }
                if(!bluetoothAdapter.isDiscovering()){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    bluetoothAdapter.startDiscovery();
                }
                IntentFilter discoverDevicesIntent=new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(broadcastReceiver3, discoverDevicesIntent);
            }
        });
//        Receive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent connection=new Intent(MainActivity.this,Connection.class);
//                connection.putExtra(EXTRA_ADDRESS, deviceAddress);
//                startActivity(connection);
//            }
//        });

        //Action of communicate button
        Communicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Connection.class);
                startActivity(intent);

            }
        });

        //action of login button
        /*login.setOnClickListener(){
            Intent intent=new Intent(MainActivity.this, Connection_led.class);
            startActivity(intent);
        }*/

        session=new Session(this);   //new addition
        if(!session.loggedIn()){
            logout();
        }
        btnLogout=(Button)findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                logout();
            }
        });  //end of new addition


    }// End of on Create

    //start of logout method
    private void logout() {
        session.setLoggedin(false);
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    } //end of logout method


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view,int i, long l){
        bluetoothAdapter.cancelDiscovery();
        String deviceName=BTDevices.get(i).getName();
        deviceAddress=BTDevices.get(i).getAddress();
        BTDevices.get(i).createBond();
        Intent connection=new Intent(MainActivity.this,Connection.class);
        connection.putExtra(EXTRA_ADDRESS, deviceAddress);


        /*if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN_MR2){
            BTDevices.get(i).createBond();
        }*/
    }




}

