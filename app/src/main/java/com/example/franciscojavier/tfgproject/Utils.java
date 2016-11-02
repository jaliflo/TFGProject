package com.example.franciscojavier.tfgproject;

import android.os.Message;

import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;
import com.example.franciscojavier.tfgproject.datamodel.MessageToServer;
import com.example.franciscojavier.tfgproject.datamodel.User;
import com.example.franciscojavier.tfgproject.webapiclient.RestService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Utils {

    public static ChatMessage stringToMessage(String rawMessage){
        String[] splitedMessage = rawMessage.split(": ");
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(splitedMessage[1]);
        if(splitedMessage[0].equals("Me")){
            chatMessage.setSendOrReceive(true);
        }else {
            chatMessage.setSendOrReceive(false);
        }
        return chatMessage;
    }

    public static MessageToServer messageToServerFormat(ChatMessage message, int mainUserId, int userId) {
        MessageToServer messageToServer = new MessageToServer();
        messageToServer.setContent(message.getContent());

        if(message.isSend()){
            messageToServer.setUserEmitter(mainUserId);
            messageToServer.setUserReceiver(userId);
        }else{
            messageToServer.setUserEmitter(userId);
            messageToServer.setUserReceiver(mainUserId);
        }

        return messageToServer;
    }
}
