package com.example.mariamaddai.bremote_ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
//import com.google.android.gms.appindexing.Action;
//import com.google.android.gms.appindexing.AppIndex;
//import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Connection_led extends AppCompatActivity{
    TextView bluetoothstatus,bluetoothPaired;
    Button enableButton,btndisconnect,btnshut;
    SeekBar seekLED,seekFAN;
    BluetoothDevice pairedBluetoothDevice=null;
    boolean status;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket btSocket=null;  //new addition
    InputStream taInput;
    OutputStream taOut;
    ArrayList<String> devicesList;
    ArrayList<BluetoothDevice> ListDevices;
    ArrayAdapter<String> adapter;
    ListView listt;
    SeekBar brightness;   //new addition
    private ProgressDialog progress;   //new addition
    BluetoothAdapter myBluetooth=null;  //new addition
    String address=null;      //new addition
    private boolean isBtConnected=false;  //new addition
    static final UUID myUUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");    //new addition

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);



        bluetoothstatus = (TextView) findViewById(R.id.bluetooth_state);
        bluetoothPaired = (TextView) findViewById(R.id.bluetooth_paired);
        enableButton = (Button) findViewById(R.id.buttonlightup);
        btnshut = (Button) findViewById(R.id.buttonShut);
        //btndisconnect = (Button) findViewById(R.id.buttondisconnect);
        brightness = (SeekBar) findViewById(R.id.seekled1);
        //seekFAN=(SeekBar)findViewById(R.id.seekfan);
        //BluetoothSocket btSocket = null;

        //seekLED.setMax(25);
        //seekFAN.setMax(25);

        /*btnshut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket != null && btSocket.isConnected())
                {

                    //send2Bluetooth(13, 13);
                    turnOffLed();  //new addition

                }
            }
        });*/

        //new btnshut onclick
        btnshut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOffLed();
            }
        });

        //new enablebutton onclick
        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOnLed();
            }
        });

        /*brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true)
                {
                    lumn.setText(String.valueOf(progress));
                    try
                    {
                        btSocket.getOutputStream().write(String.valueOf(progress).getBytes());
                    }
                    catch (IOException e)
                    {

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/

       /* enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket != null && btSocket.isConnected())
                {

                   // send2Bluetooth(44, 45);
                    turnOnLed();  //new addition

                }
            }
        });*/

        btndisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket != null && btSocket.isConnected())
                {
                    try
                    {
                        btSocket.close();
                        Toast.makeText(getApplicationContext(), "disconnected", Toast.LENGTH_LONG).show();
                        bluetoothPaired.setText("DISCONNECTED");
                        //bluetoothPaired.setTextColor(getResources().getColor(R.color.red));

                    }catch (IOException ioe)
                    {
                        Log.e("app>", "Cannot close socket");
                        pairedBluetoothDevice = null;
                        Toast.makeText(getApplicationContext(), "Could not disconnect", Toast.LENGTH_LONG).show();

                    }

                }
            }
        });

        //IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(receiver, filter);

    /*listt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(getApplicationContext(), "item with address: " + devicesList.get(i) + " clicked", Toast.LENGTH_LONG).show();

            connect2LED(ListDevices.get(i));
        }
    });*/


       /* seekLED.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int currentVal = 0 ;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                currentVal = i ;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                send2Bluetooth ( 100, currentVal );


                Toast.makeText(getApplicationContext(), "LED 1 : "+ currentVal, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    //bluetooth socket part
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        /*@Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }*/

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                //msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                //msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    //turnOff method
    private void turnOffLed(){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("TF".toString().getBytes());
            }
            catch (IOException e)
            {
                //msg("Error");
                Log.e( "app>" , "Error " );   //new addition
            }
        }//else Log.e("app", "Socket Error");
    }

    //turnOn method
    private void turnOnLed()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("TO".toString().getBytes());
            }
            catch (IOException e)
            {
                //msg("Error");
                Log.e( "app>" , "Error " );   //new addition
            }
        }
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        //client.connect();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        status = bluetoothAdapter.isEnabled();
        bluetoothAdapter.startDiscovery();
        if (status)
        {
            bluetoothstatus.setText("ENABLED");
            registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
        else {
            bluetoothstatus.setText("NOT READY");
        }
    }*/

    /*void connect2LED(BluetoothDevice device)
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") ;
        try {
            blsocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            blsocket.connect();
            pairedBluetoothDevice = device;
            bluetoothPaired.setText("PAIRED: "+device.getName());
            //bluetoothPaired.setTextColor(getResources().getColor(R.color.green));

            Toast.makeText(getApplicationContext(), "Device paired successfully!",Toast.LENGTH_LONG).show();
        }catch(IOException ioe)
        {
            Log.e("taha>", "cannot connect to device :( " +ioe);
            Toast.makeText(getApplicationContext(), "Could not connect",Toast.LENGTH_LONG).show();
            pairedBluetoothDevice = null;
        }
    }*/

    /*void send2Bluetooth(int led, int brightness)
    {
        //make sure there is a paired device
        if ( pairedBluetoothDevice != null && btSocket != null )
        {
            try
            {
                taOut = btSocket.getOutputStream();
                taOut.write(led + brightness);

                taOut.flush();
            }catch(IOException ioe)
            {
                Log.e( "app>" , "Could not open a output stream "+ ioe );
            }
        }
    }*/




    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {

        public void onReceive(Context context, Intent intent)
        {

            Log.i("app>", "broadcast received") ;
            String action = intent.getAction();


            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                devicesList.add(device.getName() + " @"+device.getAddress());
                ListDevices.add(device);

                adapter.notifyDataSetChanged();
            }
        }
    };*/


}

