package com.example.franciscojavier.tfgproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.franciscojavier.tfgproject.database.DbManager;
import com.example.franciscojavier.tfgproject.datamodel.Chat;
import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;
import com.example.franciscojavier.tfgproject.datamodel.Chats;
import com.example.franciscojavier.tfgproject.datamodel.MessageToServer;
import com.example.franciscojavier.tfgproject.datamodel.Messages;
import com.example.franciscojavier.tfgproject.datamodel.User;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChatHistoryActivity extends AppCompatActivity {

    private DbManager dbManager;

    private ArrayAdapter<String> mDataArrayAdapter;

    private int listViewState;
    private String currentUser;
    private Chat currentChat;

    private List<Chat> chats;

    private RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        dbManager = new DbManager(this);
        restService = new RestService();

        final Context context = this;

        Button chatsBackupB = (Button) findViewById(R.id.chatsBackupb);
        chatsBackupB.setVisibility(View.GONE);
        chatsBackupB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                alertDialogBuilder.setTitle("Chats Backup");
                alertDialogBuilder
                        .setMessage("Do you want to make a chats backup?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadChats(chats);
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        showUsers();
    }

    private void showUsers(){
        List<String> userNames = new ArrayList<>();
        for(User user:dbManager.getAllUsers()){
            userNames.add(user.getName());
        }

        mDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userNames);

        ListView usersView = (ListView) findViewById(R.id.dataView);
        usersView.setAdapter(mDataArrayAdapter);
        listViewState = 1;

        Button chatsBackupB = (Button) findViewById(R.id.chatsBackupb);
        chatsBackupB.setVisibility(View.GONE);

        usersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentUser = ((TextView) view).getText().toString();
                showChats();
            }
        });
    }

    private void showChats(){
        User user = dbManager.getUserByName(currentUser);
        chats = dbManager.getAllChatsByUser(user);

        List<String> chatsDates = new ArrayList<>();
        for(Chat chat:chats){
            chatsDates.add(chat.getStartDateTime()+"-"+chat.getFinishDateTime());
        }

        mDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, chatsDates);

        ListView chatsView = (ListView) findViewById(R.id.dataView);
        chatsView.setAdapter(mDataArrayAdapter);
        listViewState = 2;
        chatsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentChat = chats.get(position);
                constructChat();
            }
        });

        Button chatsBackupB = (Button) findViewById(R.id.chatsBackupb);
        chatsBackupB.setVisibility(View.VISIBLE);
    }

    private void uploadChats(final List<Chat> chatsToUpload){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Uploading Chats...");
        progressDialog.show();

        Chats chatsObject = new Chats(chatsToUpload);
        SharedPreferences settings = getSharedPreferences(Constants.PREFFS_NAME,0);
        final int userid = settings.getInt("Id", 0);

        restService.getApiTFGService().uploadChats(userid, chatsObject, new Callback<Chats>() {
            @Override
            public void success(Chats chats, Response response) {
                int i = 0;
                for(Chat chat: chats.chats){
                    uploadMessages(chat, userid, chatsToUpload.get(i));
                    i++;
                }
                if(progressDialog.isShowing())progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Error: "+error);
            }
        });
    }

    private void uploadMessages(final Chat chat, final int userid, Chat realChat){
        final List<ChatMessage> messages = dbManager.getAllMessagesByChat(realChat);
        User user = new User();
        user.setName(messages.get(0).getUser().getName());

        restService.getApiTFGService().getUserData(user, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                convertMessages(messages, user, chat, userid);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        /*List<MessageToServer> messages = new ArrayList<>();
        for(ChatMessage message: dbManager.getAllMessagesByChat(realChat)){
            MessageToServer messageToServer = Utils.messageToServerFormat(message, userid);
            messages.add(messageToServer);
        }

        Messages messagesObject = new Messages(messages);
        restService.getApiTFGService().uploadMessages(chat.getId(), messagesObject, new Callback<Messages>() {
            @Override
            public void success(Messages messages, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                /*System.out.println("Error: "+error);
            }
        });*/
    }

    private void constructChat(){
        List<String> messages = new ArrayList<>();
        for(ChatMessage message:dbManager.getAllMessagesByChat(currentChat)){
            if(message.isSend()){
                messages.add("Me: "+message.getContent());
            }else {
                messages.add(message.getUser().getName()+": "+message.getContent());
            }
        }

        Button chatsBackupB = (Button) findViewById(R.id.chatsBackupb);
        chatsBackupB.setVisibility(View.GONE);

        mDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);

        ListView messagesView = (ListView) findViewById(R.id.dataView);
        messagesView.setOnItemClickListener(null);
        messagesView.setAdapter(mDataArrayAdapter);
        listViewState = 3;
    }

    private void convertMessages(List<ChatMessage> messages, User user, Chat chat, int userid){
        List<MessageToServer> messageToServerList = new ArrayList<>();
        for(ChatMessage chatMessage: messages){
            messageToServerList.add(Utils.messageToServerFormat(chatMessage, userid, user.getId()));
        }

        Messages messagesObject = new Messages(messageToServerList);
        restService.getApiTFGService().uploadMessages(chat.getId(), messagesObject, new Callback<Messages>() {
            @Override
            public void success(Messages messages, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("Error: "+error);
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(listViewState == 3){
            showChats();
        }else if(listViewState == 2){
            showUsers();
        }else {
            super.onBackPressed();
        }
    }
}
