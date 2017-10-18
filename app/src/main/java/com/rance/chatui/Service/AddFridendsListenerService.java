package com.rance.chatui.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.rance.chatui.ui.activity.LoginActivity;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
//
//import com.johnny.fragmentcontroller.test.ui.activity.LoginActivity;
//
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.StanzaListener;
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.StanzaFilter;
//import org.jivesoftware.smack.filter.StanzaTypeFilter;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.packet.Stanza;

//import comn.example.imforsmack.LoginActivity;

public class AddFridendsListenerService extends Service {

    private final static String TAG = "AddFridendsListener";
    private final static String ADDFRIEND_ACTION = "comn.example.imforsmack.addFriendsAction";

    private String response,acceptAdd;



    public AddFridendsListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG,"----------------addFriendservice start");

       //条件过滤器
        StanzaFilter filter = new AndFilter(new StanzaTypeFilter(Presence.class));


        LoginActivity.xmppconnection.addAsyncStanzaListener(stanzaListener,filter);

        return super.onStartCommand(intent, flags, startId);
    }

    StanzaListener stanzaListener = new StanzaListener() {
        @Override
        public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
            //Log.i(TAG,stanza.toXML()+"");


            if(stanza instanceof Presence){

                Presence presence = (Presence)stanza;
                String from = presence.getFrom();//发送方
                String to = presence.getTo();//接收方


                //收到订阅（好友请求）
                if (presence.getType().equals(Presence.Type.subscribe)) {

                    System.out.println("收到添加请求！");
                    //发送广播传递发送方的JIDfrom及字符串
                    acceptAdd = "收到添加请求！";
                    Intent intent = new Intent();
                    intent.putExtra("fromName", from);
                    intent.putExtra("acceptAdd", acceptAdd);
                    intent.setAction(ADDFRIEND_ACTION);
                    sendBroadcast(intent);

                }
                //同意订阅
                else if (presence.getType().equals(Presence.Type.subscribed)) {
                    //发送广播传递response字符串
                    response = "恭喜，对方同意添加好友！";
                    Intent intent = new Intent();
                    intent.putExtra("response", response);
                    intent.setAction(ADDFRIEND_ACTION);
                    sendBroadcast(intent);

                }
                //取消订阅
                else if (presence.getType().equals(Presence.Type.unsubscribe)) {

                    //发送广播传递response字符串
                    response = "抱歉，对方拒绝添加好友，将你从好友列表移除！";
                    Intent intent = new Intent();
                    intent.putExtra("response", response);
                    intent.setAction(ADDFRIEND_ACTION);
                    sendBroadcast(intent);

                }
                //拒绝订阅
                else if (presence.getType().equals(Presence.Type.unsubscribed)){
                }
                //下线
                else if (presence.getType().equals(Presence.Type.unavailable)) {
                    //System.out.println("好友下线！");
                } else {
                    //System.out.println("好友上线！");
                }


            }
        }
    };
}
