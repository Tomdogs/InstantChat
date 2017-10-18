package com.rance.chatui.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//import com.johnny.fragmentcontroller.test.ui.activity.LoginActivity;
//import com.johnny.fragmentcontroller.test.utils.Logger;
//import com.johnny.fragmentcontroller.test.utils.SettingUtil;
//import com.johnny.fragmentcontroller.test.utils.XmppTool;

import com.rance.chatui.ui.activity.LoginActivity;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.SettingUtil;
import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Timer;
import java.util.TimerTask;

//import comn.example.imforsmack.LoginActivity;
//import comn.example.imforsmack.util.Logger;
//import comn.example.imforsmack.util.SettingUtil;
//import comn.example.imforsmack.util.XmppTool;

public class ConnectionListenerService extends Service {
    public ConnectionListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LoginActivity.xmppconnection.addConnectionListener(new AllConnectionListener());

        Logger.i("----------------ConnectionListenerService start");

        return super.onStartCommand(intent, flags, startId);
    }

    public class AllConnectionListener implements ConnectionListener{

        private String userName,password;
        private Timer timer;
        private long longTime = 2000;


        @Override
        public void connected(XMPPConnection xmppConnection) {

            //Logger.i(this.getClass(),"connection监听："+xmppConnection.isConnected());

        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {

            //Logger.i(this.getClass(),"connection——authenticated监听："+xmppConnection.isConnected());
        }

        @Override
        public void connectionClosed() {
            //Logger.i(this.getClass(),"connection——connectionClosed监听：连接关闭");
        }

        @Override
        public void connectionClosedOnError(Exception e) {

           // Logger.i(this.getClass(),"连接出现异常，正在尝试重新连接");
            XmppTool.XMPPConnection().disconnect();
            timer = new Timer();
            timer.schedule(new TimeTask(),longTime);
           // Logger.i(this.getClass(),"连接出现异常，尝试重新连接成功！");

        }

        @Override
        public void reconnectionSuccessful() {
            //Logger.i(this.getClass(),"connection——reconnectionSuccessful监听：连接成功");
        }

        @Override
        public void reconnectingIn(int i) {
            //Logger.i(this.getClass(),"connection——reconnectingIn监听："+i);
        }

        @Override
        public void reconnectionFailed(Exception e) {
            //Logger.i(this.getClass(),"connection——reconnectionFailed监听:"+e);
        }

        class TimeTask extends TimerTask {

            private final String ACCOUNT_KEY = "IM_login_account";
            private final String PASSWORD_KEY = "IM_login_password";

            SettingUtil util = new SettingUtil(ConnectionListenerService.this);
            @Override
            public void run() {
                userName = util.getString(ACCOUNT_KEY);
                password = util.getString(PASSWORD_KEY);

                if(userName != null && password != null){

                    if(XmppTool.userLogin(XmppTool.XMPPConnection(),userName,password)){
                        //Logger.i(this.getClass(),"监听登录成功");
                        Log.i("CLS","监听登录成功");
                    }else {
                        timer.schedule(new TimeTask(),longTime);
                    }

                }

            }
        }
    }


}
