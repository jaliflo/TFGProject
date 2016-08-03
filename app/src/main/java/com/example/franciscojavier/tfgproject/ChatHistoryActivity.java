package com.example.franciscojavier.tfgproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.franciscojavier.tfgproject.database.DbManager;
import com.example.franciscojavier.tfgproject.datamodel.Chat;
import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;
import com.example.franciscojavier.tfgproject.datamodel.User;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    private DbManager dbManager;

    private ArrayAdapter<String> mDataArrayAdapter;

    private int listViewState;
    private String currentUser;
    private Chat currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        dbManager = new DbManager(this);
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
        final List<Chat> chats = dbManager.getAllChatsByUser(user);

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

        mDataArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messages);

        ListView messagesView = (ListView) findViewById(R.id.dataView);
        messagesView.setOnItemClickListener(null);
        messagesView.setAdapter(mDataArrayAdapter);
        listViewState = 3;
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
