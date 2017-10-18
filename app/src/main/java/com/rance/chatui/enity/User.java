package com.rance.chatui.enity;

//import org.jivesoftware.smack.packet.Presence;

import android.provider.ContactsContract;


public class User {
    private String name;
    private String user;
    private int size;
    private String status;
    private String from;
    private ContactsContract.Presence presence;
    private boolean isAvailable;//判断是否在线
    private String Email;
    private  boolean isLogin;
    private boolean ischeck;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


    public boolean isLogin() {
        return isLogin();
    }
    public void setLogin(boolean islogin){
        isLogin=islogin;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public ContactsContract.Presence getPresence() {
        return presence;
    }

    public void setPresence(ContactsContract.Presence presence) {
        this.presence = presence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
