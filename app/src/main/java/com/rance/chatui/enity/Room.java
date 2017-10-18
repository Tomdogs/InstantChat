package com.rance.chatui.enity;

/**
 * Created by mac on 2017/3/29.
 */

public class Room {
    private String name;
    private String Jid;

    public String getJid() {
        return Jid;
    }

    public void setJid(String jid) {
        Jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
