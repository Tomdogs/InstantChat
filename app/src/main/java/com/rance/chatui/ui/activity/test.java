package com.rance.chatui.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.rance.chatui.R;
import com.rance.chatui.Service.AddFridendsListenerService;
import com.rance.chatui.Service.ConnectionListenerService;
import com.rance.chatui.Service.FileListenerService;
import com.rance.chatui.Service.MessageListenerService;
import com.rance.chatui.Service.MultiChatListenerService;

/**
 * Created by mac on 2017/5/6.
 */

public class test extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent messageIntent = new Intent(this ,MessageListenerService.class);
        startService(messageIntent);


        //开启接收文件的服务
        Intent fileIntent = new Intent(this, FileListenerService.class);
        startService(fileIntent);


        //开启添加朋友的服务
        Intent addFriendIntent = new Intent(this, AddFridendsListenerService.class);
        startService(addFriendIntent);

        //开启群聊的服务
        Intent multiChatIntent = new Intent(this, MultiChatListenerService.class);
        startService(multiChatIntent);

        //开启连接的的服务
        Intent connectionIntent = new Intent(this, ConnectionListenerService.class);
        startService(connectionIntent);

    }
}
