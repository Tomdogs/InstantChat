package com.rance.chatui.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.List;



public class MultiChatListenerService extends Service {

    private final static String TAG = "MultiChatListenerLog";

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        //有问题？？？？？？？？？？？？？？？？、
        List<MultiUserChat> multiUserChatList= XmppTool.getAllMucRoom();

            //List userRoomIds=multiUserChatManager.getJoinedRooms(conn.getUser());
            //循环获取user加入的群的ID号，并加入监听
            for (MultiUserChat m : multiUserChatList){

                m.addMessageListener(messageListener);

            }

        System.out.println("-------------MultiChatListenerService start");

        return START_STICKY;
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Message message) {


            Log.i(TAG,"MultiChatListenerService 群发的服务监听getFrom:"+message.getFrom());
            Log.i(TAG,"MultiChatListenerService 群发的服务监听getBody:"+message.getBody());
            Log.i(TAG,"MultiChatListenerService 群发的服务监听getTo:"+message.getTo());

            //if(message.getBody() != null){

                //发送广播
                Intent messageIntent = new Intent();
                messageIntent.putExtra("multiMessageGetFrom", message.getFrom());
                messageIntent.putExtra("multiMessageBody",message.getBody());
                messageIntent.putExtra("multiMessagetTo",message.getTo());

                messageIntent.setAction("comn.rance.chatui.Service.MultiChatListenerService");
                sendBroadcast(messageIntent);

            //}



        }
    };



}
