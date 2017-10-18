package com.rance.chatui.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.rance.chatui.ui.activity.LoginActivity;
import com.rance.chatui.util.Logger;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaExtensionFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.offline.OfflineMessageManager;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.xevent.MessageEventManager;
import org.jivesoftware.smackx.xevent.MessageEventNotificationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MessageListenerService extends Service {

    private static XmppMessageManager xmppMessageMgr;

    private XMPPConnection connection = LoginActivity.xmppconnection;

    private static final StanzaFilter PACKET_FILTER;
    private static final  StanzaFilter filters;

    static {
        PACKET_FILTER = new AndFilter(new StanzaFilter[]{new StanzaExtensionFilter(new OfflineMessageInfo()), StanzaTypeFilter.MESSAGE});
        filters = new AndFilter(new StanzaTypeFilter(Message.class));
    }

    public MessageListenerService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        StanzaFilter filter = new AndFilter(PACKET_FILTER);

        connection.addAsyncStanzaListener(stanzaListener,filters);


        xmppMessageMgr = new XmppMessageManager();
        xmppMessageMgr.initialize(connection);


        System.out.println("-------------messageservice start");



        return START_STICKY;
    }


    StanzaListener stanzaListener = new StanzaListener() {
        @Override
        public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {

            if(stanza instanceof Message){
                Logger.i("离线的stanza.toXML："+ stanza.toXML());
                String xmlStr =stanza.toXML().toString();
                //offLineMessage(xmlStr);


                //XmlAnalysis xmlAnalysis = new XmlAnalysis();

                Presence presence = Roster.getInstanceFor(LoginActivity.xmppconnection).getPresence(connection.getUser());
                //Logger.i("离线  用户是否在线："+  (presence.getType() == Presence.Type.available));


                //Log.i("off","执行到这");
               /* List<OffLineMessage> list =xmlAnalysis.offLineMessage(xmlStr);
                for(OffLineMessage offMessages : list){

                    Logger.i("得到的离线从"+ offMessages.getFromJID()+"  的消息： "+offMessages.getMessage()+"  到： "+offMessages.getToJID()+"   时间为："+offMessages.getTime());

                }*/

            }
        }
    };





    public class XmppMessageManager implements ChatManagerListener {
        private XMPPConnection _connection;
        private ChatManager manager = null;

        public void initialize(XMPPConnection connection) {
            _connection = connection;
            manager = ChatManager.getInstanceFor(_connection);
            manager.addChatListener(this);

//-----------------------------------------离线---------------------------------------------------
            //另一种离线消息的实现方式
            //这里是为了接收离线消息，之前的登录状态是不可用unavailable
            //在线功能设计
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("luo am offline");
            try {
                connection.sendStanza(presence);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }

            //第一种
            //offMessage();


            /*List<Message> lists = getMessagess();

            for(Message message : lists){
                Logger.i("message.toXML1()："+ message.toXML());

            }*/

            //第二种
          /*List<Message> list = getMessages();
            try {

            for(Message message : list){
                Logger.i("message.toXML2()："+ message.toXML());

            }
            }catch (Exception e){

                e.printStackTrace();
            }*/

//------------------------------------消息状态跟踪------------------------------------------------------
            //消息状态跟踪
            MessageEventManager messageEventManager = MessageEventManager.getInstanceFor(connection);
            messageEventManager.addMessageEventNotificationListener( new MessageEventNotificationListener() {
                @Override
                public void deliveredNotification(String s, String s1) {

                    Logger.i("当收到提供的消息的通知时调用"+s+"\n"+s1);
                }

                @Override
                public void displayedNotification(String s, String s1) {

                    Logger.i("当接收到显示消息的通知时调用"+s+"\n"+s1);
                }

                @Override
                public void composingNotification(String s, String s1) {

                    Logger.i("当接收到消息的接收者正在组成回复的通知时被调用"+s+"\n"+s1);
                }

                @Override
                public void offlineNotification(String s, String s1) {

                    Logger.i("当接收到消息的接收者的通知被接收时被调用"+s+"\n"+s1);
                }

                @Override
                public void cancelledNotification(String s, String s1) {

                    Logger.i("当接收到消息的接收者取消回复的通知时被调用"+s+"\n"+s1);
                }
            });

        }

        @Override
        public void chatCreated(Chat chat, boolean arg1) {
            // TODO Auto-generated method stub
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {


                    if(null!= message.getBody()){
                        //发送广播
                        Intent messageIntent = new Intent();
                        messageIntent.putExtra("messageGetFrom", message.getFrom());
                        messageIntent.putExtra("messageBody", message.getBody());
                        messageIntent.setAction("com.rance.chatui.Service");
                        sendBroadcast(messageIntent);
                        Logger.i("XmppMessageManager6688：" + message.getBody()+"\n+from:"+message.getFrom());
                        Logger.i("to:"+ message.getTo());

                    }
                }
            });
        }
    }


    private void offMessage(){

        //离线消息
        OfflineMessageManager offlineManager = new OfflineMessageManager(connection);

        try
        {
            /*OfflineMessageRequest offlineMessageRequest = new OfflineMessageRequest();
            Logger.i("离线的toXML："+offlineMessageRequest.toXML());
            Logger.i("离线的isFetch："+ offlineMessageRequest.isFetch());
            offlineMessageRequest.setTo(connection.getUser());
            offlineMessageRequest.setFetch(true);*/


            OfflineMessageRequest request = new OfflineMessageRequest();
            Iterator<Message> it = offlineManager.getMessages().iterator();


            //Logger.i("离线的离线的:"+request.toXML());
            //System.out.println("支持灵活离线消息检索:"+offlineManager.supportsFlexibleRetrieval());
            //System.out.println("离线消息数量: " + offlineManager.getMessageCount());

            // List offlineMessageHeader= offlineManager.getHeaders();


            Map<String,ArrayList<Message>> offlineMsgs = new HashMap<String,ArrayList<Message>>();

            while (it.hasNext()) {
                org.jivesoftware.smack.packet.Message message = it.next();
                System.out.println("收到离线消息, Received from" + message.getFrom()
                        + message.getBody());
                String fromUser = message.getFrom().split("/")[0];

                if(offlineMsgs.containsKey(fromUser))
                {
                    offlineMsgs.get(fromUser).add(message);
                }else{
                    ArrayList<org.jivesoftware.smack.packet.Message> temp = new ArrayList<org.jivesoftware.smack.packet.Message>();
                    temp.add(message);
                    offlineMsgs.put(fromUser, temp);
                }
            }

            offlineManager.deleteMessages();

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {

            //将状态设置成在线
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("luo2 am online");
            connection.sendStanza(presence);

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }



    }


    public List<Message> getMessagess() {


        List<Message> messages = new ArrayList<Message>();
        OfflineMessageRequest request = new OfflineMessageRequest();
        request.setFetch(true);
        PacketCollector messageCollector =connection.createPacketCollector(PACKET_FILTER);

        try {
            connection.createPacketCollectorAndSend(request).nextResultOrThrow();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }


        Message message = (Message) messageCollector.nextResult();
        while (message != null)
        {
            messages.add(message);
            message = (Message) messageCollector.nextResult();
        }
        // Stop queuing offline messages
        messageCollector.cancel();
        return messages;
    }


    public List<Message> getMessages() {


        StanzaFilter filter = new AndFilter(new StanzaTypeFilter(Message.class));

        OfflineMessageRequest request = new OfflineMessageRequest();

        request.setFetch(true);
        request.setTo(connection.getServiceName());
        Logger.i("离线的jid："+connection.getUser());
        Logger.i("离线的离线的:"+request.toXML());

        PacketCollector resultCollector = null;
        try {
            resultCollector = connection.createPacketCollectorAndSend(request);


        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        PacketCollector.Configuration messageCollectorConfiguration = PacketCollector.newConfiguration().setStanzaFilter(PACKET_FILTER).setCollectorToReset(resultCollector);
        PacketCollector messageCollector = connection.createPacketCollector(messageCollectorConfiguration);

        List<Message> messages = null;
        try {

            resultCollector.nextResultOrThrow();
            //resultCollector.nextResult(1000);


            Logger.i("resultCollector.getStanzaFilter(有没有);"+resultCollector.getStanzaFilter());
            Logger.i(" resultCollector.pollResult(有没有):"+ resultCollector.pollResult());

            //resultCollector.nextResultOrThrow(10000);

            // Be extra safe, cancel the message collector right here so that it does not collector
            // other messages that eventually match (although I've no idea how this could happen in
            // case of XEP-13).
            messageCollector.cancel();
            messages = new ArrayList<>(messageCollector.getCollectedCount());
            Logger.i("messageCollector.getCollectedCount(有没有)："+messageCollector.getCollectedCount());
            Message message;
            while ((message = messageCollector.pollResult()) != null) {
                messages.add(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            // Ensure that the message collector is canceled even if nextResultOrThrow threw. It
            // doesn't matter if we cancel the message collector twice
            messageCollector.cancel();
        }
        return messages;
    }

}
