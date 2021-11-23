package com.example.chatapplication.Model;

public class User {
    String PhoneNo;
    String UserId;
    String UserName;

    public User(String phoneNo, String userId, String userName) {
        PhoneNo = phoneNo;
        UserId = userId;
        UserName = userName;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        PhoneNo = phoneNo;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public User() {
    }
}


