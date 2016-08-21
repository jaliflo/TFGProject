package com.example.franciscojavier.tfgproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.franciscojavier.tfgproject.datamodel.Chat;
import com.example.franciscojavier.tfgproject.datamodel.ChatMessage;
import com.example.franciscojavier.tfgproject.datamodel.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DbManager {
    //Table names
    public static final String TABLE_USERS = "Users";
    public static final String TABLE_CHATS = "Chats";
    public static final String TABLE_MESSAGES = "Messages";

    //Common column names
    public static final String KEY_ID = "id";
    public static final String KEY_USERID = "user_id";

    //Users table column names
    public static final String CN_NAME = "name";

    //Chats table column names
    public static final String CN_STARTTIME = "start_time";
    public static final String CN_ENDTIME = "end_time";

    //Messsages table column names
    public static final String CN_CONTENT = "content";
    public static final String CN_SENDORRECEIVE = "send_or_receive";
    public static final String KEY_CHATID = "chat_id";

    //Users create table
    public static final String CREATE_USERS = "create table "+TABLE_USERS+" ("
            +KEY_ID+" integer primary key autoincrement,"
            +CN_NAME+" text not null);";

    //Chats create table
    public static final String CREATE_CHATS = "create table "+TABLE_CHATS+" ("
            +KEY_ID+" integer primary key autoincrement,"
            +CN_STARTTIME+" text not null,"
            +CN_ENDTIME+" text not null,"
            +KEY_USERID+" integer not null,"
            +" foreign key ("+KEY_USERID+") references "+TABLE_USERS+" ("+KEY_ID+"));";

    //Messages create table
    public static final String CREATE_MESSAGES = "create table "+TABLE_MESSAGES+" ("
            +KEY_ID+" integer primary key autoincrement,"
            +CN_CONTENT+" text not null,"
            +CN_SENDORRECEIVE+" integer not null,"
            +KEY_USERID+" integer not null,"
            +KEY_CHATID+" integer not null,"
            +" foreign key ("+KEY_USERID+") references "+TABLE_USERS+" ("+KEY_ID+"),"
            +" foreign key ("+KEY_CHATID+") references "+TABLE_CHATS+" ("+KEY_ID+"));";

    private DbHelper helper;
    private SQLiteDatabase db;
    private SimpleDateFormat format;

    public DbManager(Context context){
        helper = new DbHelper(context);
        db = helper.getWritableDatabase();
        format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
    }

    public User getUser(int id){
        User user = null;
        String selectQuery = "select * from "+TABLE_USERS+" where "+KEY_ID+" = "+id;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null){
            c.moveToFirst();
            //user = new User(c.getInt(c.getColumnIndex(KEY_ID)),
              //      c.getString(c.getColumnIndex(CN_NAME)));
        }

        return user;
    }

    public User getUserByName(String name){
        User user = null;
        String selectQuery = "select * from "+TABLE_USERS+" where "+CN_NAME+" = '"+name+"'";
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            //user = new User(c.getInt(c.getColumnIndex(KEY_ID)),
              //      c.getString(c.getColumnIndex(CN_NAME)));
        }
        return user;
    }

    public List<User> getAllUsers(){
        List<User> users = new ArrayList<>();
        String selectQuery = "select * from "+TABLE_USERS;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
               // User user = new User(c.getInt(c.getColumnIndex(KEY_ID)),
                 //                    c.getString(c.getColumnIndex(CN_NAME)));

               // users.add(user);
            }while(c.moveToNext());
        }

        return users;
    }

    public void insertUser(String name){
        ContentValues values = new ContentValues();
        values.put(CN_NAME, name);

        db.insert(TABLE_USERS, null, values);
    }

    public Chat getChat(int id){
        Chat chat = null;
        String selectQuery = "select * from "+TABLE_CHATS+" where "+KEY_ID+" = "+id;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null){
            c.moveToFirst();
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = format.parse(c.getString(c.getColumnIndex(CN_STARTTIME)));
                endDate = format.parse(c.getString(c.getColumnIndex(CN_ENDTIME)));
            } catch (ParseException e) {

            }

            chat = new Chat(c.getInt(c.getColumnIndex(KEY_ID)),
                    startDate,
                    endDate,
                    getUser(c.getInt(c.getColumnIndex(KEY_USERID))));
        }
        return chat;
    }

    public Chat getChatByDates(Date startDate, Date finishDate){
        Chat chat = null;
        String selectQuery = "select * from "+TABLE_CHATS+" where "+CN_STARTTIME+" = '"
                +format.format(startDate)+"' and "+CN_ENDTIME+" = '"+format.format(finishDate)+"';";
        Cursor c = db.rawQuery(selectQuery, null);
        if(c.moveToFirst()){
            chat = new Chat(c.getInt(c.getColumnIndex(KEY_ID)),
                    startDate,
                    finishDate,
                    getUser(c.getInt(c.getColumnIndex(KEY_USERID))));
        }

        return chat;
    }

    public List<Chat> getAllChatsByUser(User user){
        List<Chat> chats = new ArrayList<>();
        String selectQuery = "select * from "+TABLE_CHATS+" where "+KEY_USERID+" = "+user.getId();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                Date startDate = null;
                Date finishDate = null;
                try {
                    startDate = format.parse(c.getString(c.getColumnIndex(CN_STARTTIME)));
                    finishDate = format.parse(c.getString(c.getColumnIndex(CN_ENDTIME)));
                }catch (ParseException e){

                }

                Chat chat = new Chat(c.getInt(c.getColumnIndex(KEY_ID)),
                        startDate,
                        finishDate,
                        user);

                chats.add(chat);
            }while (c.moveToNext());
        }

        return chats;
    }

    public void insertChat(Chat chat){
        ContentValues values = new ContentValues();
        values.put(CN_STARTTIME, format.format(chat.getStartDateTime()));
        values.put(CN_ENDTIME, format.format(chat.getFinishDateTime()));
        values.put(KEY_USERID, chat.getUser().getId());

        db.insert(TABLE_CHATS, null, values);
    }

    public List<ChatMessage> getAllMessagesByChat(Chat chat){
        List<ChatMessage> messages = new ArrayList<>();
        String selectQuery = "select * from "+TABLE_MESSAGES+" where "+KEY_CHATID+" = "+chat.getId()
                +" and "+KEY_USERID+" = "+chat.getUser().getId();
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()){
            do{
                ChatMessage message;
                if(c.getInt(c.getColumnIndex(CN_SENDORRECEIVE)) == 1){
                    message = new ChatMessage(c.getInt(c.getColumnIndex(KEY_ID)),
                            c.getString(c.getColumnIndex(CN_CONTENT)),
                            true,
                            chat,
                            chat.getUser());
                }else{
                    message = new ChatMessage(c.getInt(c.getColumnIndex(KEY_ID)),
                            c.getString(c.getColumnIndex(CN_CONTENT)),
                            false,
                            chat,
                            chat.getUser());
                }

                messages.add(message);
            }while (c.moveToNext());
        }

        return messages;
    }

    public void insertMessage(ChatMessage chatMessage){
        int sendOrReceive;
        if(chatMessage.isSend()){
            sendOrReceive = 1;
        }else {
            sendOrReceive = 0;
        }
        ContentValues values = new ContentValues();
        values.put(CN_CONTENT, chatMessage.getContent());
        values.put(CN_SENDORRECEIVE, sendOrReceive);
        values.put(KEY_USERID, chatMessage.getUser().getId());
        values.put(KEY_CHATID, chatMessage.getChat().getId());

        db.insert(TABLE_MESSAGES, null, values);
    }
}
