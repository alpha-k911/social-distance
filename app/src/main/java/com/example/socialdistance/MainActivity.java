package com.example.socialdistance;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothHeadset bluetoothHeadset;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/alarm2");
//        mp = MediaPlayer.create(this, R.raw.alarm2);
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(getApplicationContext(),uri);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isPlaying()) {
                    mp.pause();
                }
                bluetoothAdapter.startDiscovery();
                System.out.println("Got a Click");
//                mp.start();

            }
        });
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                bluetoothAdapter.startDiscovery();
                System.out.println("Timer got a call");
            }
        };

        Timer timer = new Timer();
        timer.schedule(task,1);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_rssi, menu);
//        return true;
//
//    }
//    public void onP

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                String name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
                TextView rssi_msg = (TextView) findViewById(R.id.rssi_value);
                rssi_msg.setText("Device:" + name + " Strength: " + rssi + " units\n");
                System.out.println("Device:" + name + " Strength: " + rssi + " units\n");
//                System.out.println("Lesser ############################################################");
                if(mp.isPlaying()) {
                    mp.stop();
                }
                if(rssi > -60){
                    System.out.println("Greater ############################################################");
                    mp.start();
                }else{
//                    System.out.println("Lesser  ############################################################");
                    if(mp.isPlaying()) {
                        mp.stop();
                    }
                }

            }


        }
    };
}
