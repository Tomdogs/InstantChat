package com.rance.chatui.enity;

/**
 * Created by Tomdog on 2017/5/27.
 */

public class MuiltRoom {

    private String gid;
    private String jid;
    private String nickname;
    private String content;
    private int type;
    private int sendState;
    private String time;
    private String header;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSendState() {
        return sendState;
    }

    public void setSendState(int sendState) {
        this.sendState = sendState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "MuiltRoom{" +
                "gid='" + gid + '\'' +
                ", jid='" + jid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", sendState=" + sendState +
                ", time='" + time + '\'' +
                ", header='" + header + '\'' +
                '}';
    }
}
