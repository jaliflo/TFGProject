package com.example.franciscojavier.tfgproject;

public interface Constants {
    public static final String PREFFS_NAME = "UserProfile";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_USER_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_ASK_CONFIRM = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
