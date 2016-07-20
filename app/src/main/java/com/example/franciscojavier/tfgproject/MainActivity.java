package com.example.franciscojavier.tfgproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBluetooth();

        Button discoveryB = (Button) findViewById(R.id.discoveryB);
        discoveryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBluetooth()) {
                    startDeviceListActivity(context);
                }
            }
        });
    }

    private boolean checkBluetooth(){
        if(!bluetoothAdapter.isEnabled()){
            Intent discoverableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivityForResult(discoverableBtIntent, REQUEST_ENABLE_BT);
        }

        return bluetoothAdapter.isEnabled();
    }

    private void startDeviceListActivity(Context context){
        Intent intent = new Intent(context, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Bluetooth is enable and this device is discoverable", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
