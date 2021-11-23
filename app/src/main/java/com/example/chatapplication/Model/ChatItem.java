package com.example.chatapplication.Model;



public class ChatItem {
    private  String message;
    private  String sender;
    private  String reciver;
    private  String messageType;
    private  String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public ChatItem() {
    }

    public ChatItem(String message, String sender, String reciver, String messageType, String time) {
        this.message = message;
        this.sender = sender;
        this.reciver = reciver;
        this.messageType = messageType;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }



    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
