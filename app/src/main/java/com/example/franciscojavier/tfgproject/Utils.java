package com.example.franciscojavier.tfgproject;

import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;

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
}
