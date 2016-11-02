package com.example.franciscojavier.tfgproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franciscojavier.tfgproject.database.DbHelper;
import com.example.franciscojavier.tfgproject.database.DbManager;
import com.example.franciscojavier.tfgproject.datamodel.Chat;
import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;
import com.example.franciscojavier.tfgproject.datamodel.User;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity{

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_DISCOVERABLE_BT = 3;

    private String mConnectedUserName = "";

    private ArrayAdapter<String> mConversationArrayAdapter;

    private StringBuffer mOutStringBuffer;

    private BluetoothChatService mChatService = null;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean askedDiscoverability = false;

    private String mUsername;

    private DbManager dbManager;

    private Chat mConnectedChat;

    private RestService restService;

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        int numMsgSent = 0;

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(R.string.title_connected + mConnectedUserName);
                            setupChat();
                            mConversationArrayAdapter.clear();
                            sendUserName();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_ASK_CONFIRM:
                    String username = msg.obj.toString();
                    askConnectionConfirmation(username);
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    numMsgSent++;
                    if(numMsgSent>1) {
                        mConversationArrayAdapter.add("Me:  " + writeMessage);
                    }
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedUserName + ":  " + readMessage);
                    sendNotification(readMessage);
                    break;
                case Constants.MESSAGE_USER_NAME:
                    // save the connected device's name
                    mConnectedUserName = msg.getData().getString(Constants.DEVICE_NAME);
                    break;
                case Constants.MESSAGE_END_CONNECTION:
                    endChat();
                case Constants.MESSAGE_TOAST:

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restService = new RestService();

        final Context context = this;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        dbManager = new DbManager(context);

        Button discoveryB = (Button) findViewById(R.id.discoveryB);
        discoveryB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBluetooth()) {
                    startDeviceListActivity(context);
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME, 0);
        mUsername = settings.getString("Username", "");

        ListView chatList = (ListView) findViewById(R.id.chatList);
        Button sendB = (Button) findViewById(R.id.sendB);
        EditText messageText = (EditText) findViewById(R.id.messageText);

        chatList.setVisibility(View.GONE);
        sendB.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);

        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (checkBluetooth() && mChatService == null) {
            mChatService = new BluetoothChatService(mHandler, mUsername);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private boolean checkBluetooth(){
        // If bluetooth is not enable, then turn on it and make the device discoverable permanently
        if(!mBluetoothAdapter.isEnabled()){
            Intent discoverableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivityForResult(discoverableBtIntent, REQUEST_ENABLE_BT);
        }else if(!askedDiscoverability){
            Intent onlyDiscoverableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            onlyDiscoverableBtIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivityForResult(onlyDiscoverableBtIntent, REQUEST_DISCOVERABLE_BT);
        }

        return mBluetoothAdapter.isEnabled();
    }

    private void startDeviceListActivity(Context context){
        Intent intent = new Intent(context, DeviceListActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
    }

    private void connectDevice(String address) {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device);
    }

    private void askConnectionConfirmation(String username){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        User user = new User();
        user.setName(username);

        final Context context = this;

        restService.getApiTFGService().getUserData(user, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle("Chat Request");
                alertDialogBuilder
                        .setMessage("Do you want to chat with "+user.getName() + "?\n"
                            +"Job: "+user.getJob()+"\n"
                            +"City And Country: "+user.getCityAndCountry()+"\n"
                            +"Hobbies: "+user.getHobbies()+"\n"
                            +"Music Tastes: "+user.getMusicTastes()+"\n"
                            +"Film Tastes: "+user.getFilmsTastes()+"\n"
                            +"Reading Tastes: "+user.getReadingTastes())
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChatService.confirmRequest(true);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChatService.confirmRequest(false);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                if(progressDialog.isShowing())progressDialog.dismiss();
                alertDialog.show();
            }

            @Override
            public void failure(RetrofitError error) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        this.sendNotification("Chat Request");
    }

    private void setupChat(){
        if(DeviceListActivity.deviceListActivity != null) {
            DeviceListActivity.deviceListActivity.finish();
        }

        mConversationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView conversationView = (ListView) findViewById(R.id.chatList);
        conversationView.setAdapter(mConversationArrayAdapter);

        Button discoveryB = (Button) findViewById(R.id.discoveryB);
        Button sendB = (Button) findViewById(R.id.sendB);
        EditText messageText = (EditText) findViewById(R.id.messageText);

        discoveryB.setVisibility(View.GONE);
        conversationView.setVisibility(View.VISIBLE);
        sendB.setVisibility(View.VISIBLE);
        messageText.setVisibility(View.VISIBLE);

        sendB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageText = (EditText) findViewById(R.id.messageText);
                String message = messageText.getText().toString();
                if (!message.equals("")) {
                    sendMessage(message);
                }
            }
        });

        mConnectedChat = new Chat();
        mConnectedChat.setStartDateTime(Calendar.getInstance().getTime());
    }

    private void endChat(){
        ListView conversationView = (ListView) findViewById(R.id.chatList);
        Button discoveryB = (Button) findViewById(R.id.discoveryB);
        Button sendB = (Button) findViewById(R.id.sendB);
        EditText messageText = (EditText) findViewById(R.id.messageText);

        mOutStringBuffer.setLength(0);
        messageText.setText(mOutStringBuffer);

        conversationView.setVisibility(View.GONE);
        sendB.setVisibility(View.GONE);
        messageText.setVisibility(View.GONE);
        discoveryB.setVisibility(View.VISIBLE);

        if(dbManager.getUserByName(mConnectedUserName) == null){
            dbManager.insertUser(mConnectedUserName);
        }

        mConnectedChat.setFinishDateTime(Calendar.getInstance().getTime());
        mConnectedChat.setUser(dbManager.getUserByName(mConnectedUserName));
        dbManager.insertChat(mConnectedChat);

        for(int i=0;i<mConversationArrayAdapter.getCount();i++){
            String rawMessage = mConversationArrayAdapter.getItem(i);
            ChatMessage message = Utils.stringToMessage(rawMessage);
            message.setUser(dbManager.getUserByName(mConnectedUserName));
            message.setChat(dbManager.getChatByDates(mConnectedChat.getStartDateTime(), mConnectedChat.getFinishDateTime()));
            dbManager.insertMessage(message);
        }

        mConnectedChat = null;
        mConnectedUserName = null;
    }

    private void sendUserName(){
        byte[] send = mUsername.getBytes();
        mChatService.write(send);
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            EditText messageText = (EditText) findViewById(R.id.messageText);
            messageText.setText(mOutStringBuffer);
        }
    }

    public void sendNotification(String message){
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setContentTitle("ItsMe")
                .setContentText(mConnectedUserName+": "+message);

        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {

        if (null == this) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {

        if (null == this) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode != Activity.RESULT_CANCELED){
                Toast.makeText(this, "Bluetooth is enable and this device is discoverable", Toast.LENGTH_SHORT).show();
                mChatService = new BluetoothChatService(mHandler, mUsername);
                askedDiscoverability = true;
            }else{
                Toast.makeText(this, "Bluetooth was not enabled", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == REQUEST_DISCOVERABLE_BT){
            if(resultCode != Activity.RESULT_CANCELED){
                Toast.makeText(this, "This device is discoverable", Toast.LENGTH_SHORT).show();
                askedDiscoverability = true;
            }
        } else if(requestCode == REQUEST_CONNECT_DEVICE){
            if(resultCode == Activity.RESULT_OK){
                // Get the device MAC address
                String address = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                connectDevice(address);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.chat_history:
                Intent intent = new Intent(this, ChatHistoryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        endChat();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
