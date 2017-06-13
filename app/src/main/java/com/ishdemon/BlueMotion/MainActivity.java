package com.ishdemon.BlueMotion;

//Import needed files
import java.util.ArrayList;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.ishdemon.bluetooth.R;

//Main activity class
public class MainActivity extends ActionBarActivity {

    //Creates object variables
    private Button list, disconnect;
    private ListView lv;
    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private Toast toast;

    @Override
    //on create
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creates variables containing id's
        list = (Button) findViewById(R.id.button1);
        disconnect = (Button) findViewById(R.id.button2);
        lv = (ListView) findViewById(R.id.listView1);

        //When you click the list item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = lv.getAdapter().getItem(position);
                String str = obj.toString();
                int strStartIndex = str.indexOf("\n");
                str = str.substring(strStartIndex + 1);

                //check to see if bluetooth is enabled before
                //starting the service
                if (BA.isEnabled()) {
                    toast = Toast.makeText(getApplicationContext(),
                            "Connecting to: " + str, Toast.LENGTH_SHORT);
                    toast.show();
                    //connects to item you tapped, creating a service
                    Intent btConnectIntent = new Intent(MainActivity.this, ForegroundService.class);
                    btConnectIntent.setAction("btConnect");
                    btConnectIntent.putExtra("mac", str);
                    startService(btConnectIntent);
                }
                //Bluetooth isn't enabled
                else{
                    toast = Toast.makeText(getApplicationContext(),
                            "Bluetooth must be enabled!", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });
    }


    //When you click the list button
    public void list(View view) {
        //Get bluetooth adapter and set it to BA
        BA = BluetoothAdapter.getDefaultAdapter();
        //Make sure device has bluetooth
        if (BA != null) {
            //See if bluetooth is enabled and if it is, get the list of paired devices
            if (BA.isEnabled()) {
                bluetoothList();
            }
            //If bluetooth isn't enabled, enable it
            else {
                //Start new intent to see if they turn on bluetooth
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOn, 0);
                toast = Toast.makeText(getApplicationContext(),
                        "Bluetooth must be on!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        //Device doesn't have a bluetooth adapter
        else {
            toast = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support bluetooth!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    //Waiting to see if they turn on bluetooth
    //If they do, show list
    public void onActivityResult(int requestCode, int resultCode, Intent turnOn) {
        //if it turned on successfully, get the list of paired bluetooth devices
        if (resultCode == RESULT_OK) {
            bluetoothList();
        }
    }

    //When they click disconnect button, stop the service
    public void disconnect(View view) {
        Intent stopIntent = new Intent(MainActivity.this, ForegroundService.class);
        stopIntent.setAction("stopForeground");
        startService(stopIntent);
    }

    //Method that gets the list of paired devices
    private void bluetoothList() {
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName() + "\n" + bt.getAddress());

        //create array adapter
        final ArrayAdapter adapter = new ArrayAdapter
                (this, android.R.layout.simple_list_item_1, list);
        //connect our list view to the adapter
        lv.setAdapter(adapter);

        toast = Toast.makeText(getApplicationContext(),
                "Showing paired devices", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

