package com.example.rajneesh.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothadapter = null;
    BluetoothSocket btsocket = null;
    Set<BluetoothDevice> pairedDevices;
    String address, name;
     Intent btenablingintent;
     InputStream inputStream;
    byte[] buffer;
     static final UUID myUUid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    Button getdata;
    ProgressBar progressBar;
    TextView data,devname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getdata = findViewById(R.id.connect);
        data= findViewById(R.id.data);
        devname= findViewById(R.id.device);
        progressBar= findViewById(R.id.progressbar);
        progressBar.setMax(10);

        btenablingintent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        bluetoothadapter= BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothadapter.isEnabled()){
            startActivityForResult(btenablingintent,1);
        }
        connect_device();


        getdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                byte[] buffer = new byte[1024];
                int begin = 0;
                int bytes = 0;

                    try {
                        inputStream= btsocket.getInputStream();
                        bytes= inputStream.read(buffer);
                        String msg= new String(buffer,0,bytes);
                        data.setText(msg);

                    } catch (IOException e) {
                        Log.d("exp",e.getLocalizedMessage());
                        Log.d("exp",e.getMessage());
                        Log.d("exp",e.getCause().toString());
                        Log.d("exp",e.getStackTrace().toString());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }




                }
            }
        );

    }



    private void connect_device()  {

        try{
            address= bluetoothadapter.getAddress().toString();
            pairedDevices= bluetoothadapter.getBondedDevices();
            if(pairedDevices.size()>0) {
                for (BluetoothDevice bt : pairedDevices) {
                    address = bt.getAddress().toString();
                    name = bt.getName().toString();
                }
                devname.setText("Name::" + name + "\nAddress::" + address);

                BluetoothDevice device = bluetoothadapter.getRemoteDevice(address);
                btsocket = device.createInsecureRfcommSocketToServiceRecord(myUUid);
                btsocket.connect();
            }
        }
        catch (Exception e){
            Toast.makeText(this,"inside connect device=="+ e.toString(), Toast.LENGTH_LONG).show();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();

                connect_device();

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Need to provide with permission", Toast.LENGTH_SHORT).show();

            }


        }
    }

    public class GetData extends AsyncTask<Void,Integer, byte[]>{

        @Override
        protected byte[] doInBackground(Void... voids) {
            try {
                inputStream= btsocket.getInputStream();
                 buffer= new byte[1024];
                while(true) {
                    inputStream.read(buffer);
                }

            } catch (IOException e) {
                Log.d("error",e.toString());
            }
            catch (Exception e){
                Log.d("error",e.toString());
            }
            return buffer;
        }

        @Override
        protected void onProgressUpdate(Integer... integers) {
            progressBar.setProgress(integers[0]);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            data.setText(buffer.toString());
        }
    }



}