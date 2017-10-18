package com.rance.chatui.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.rance.chatui.ui.activity.LoginActivity;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;


public class RosterListenerService extends Service {
    private Roster roster;
    public RosterListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        roster = Roster.getInstanceFor(LoginActivity.xmppconnection);
        roster.addRosterListener(new MyRosterListener());

        //Logger.i("-------------RosterListenerService start");
        return START_STICKY;
    }

    // 添加花名册监听器，监听好友状态的改变。
    private class  MyRosterListener implements RosterListener {


        @Override
        public void entriesAdded(Collection<String> collection) {
            for(String s :collection){

                //Logger.i(this.getClass(),"添加新的好友："+s);
            }

        }

        @Override
        public void entriesUpdated(Collection<String> collection) {

            for(String s :collection){

                //Logger.i(this.getClass(),"变化的好友："+s);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {

            for(String s :collection){

                //Logger.i(this.getClass(),"删除的好友："+s);
            }
        }

        @Override
        public void presenceChanged(Presence presence) {

            String from  = presence.getFrom();
            String to  = presence.getTo();
            String status = presence.getStatus();
            Presence.Type type = presence.getType();
            //Logger.i(this.getClass(),"好友状态变化-Presence changed:"+from+":"+status+":"+type+",to:"+to);
            /*if(from.equals(to)){
                ucManager.handleOwnerStatusEvent(from, status, type.toString());
            }else{
                ucManager.handleFriendStatusEvent(from,status,type.toString());
            }*/
        }
    }
}
