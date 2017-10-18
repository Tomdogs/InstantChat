package com.rance.chatui.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rance.chatui.DB.sp.SystemConfigSp;
import com.rance.chatui.R;
import com.rance.chatui.ui.mainview.MainFragmentActivity;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/20.
 */

public class LoginActivity extends AppCompatActivity {

    private Handler uiHandler = new Handler();
    //private InputMethodManager intputManager;

    private final static String TAG="LoginActivityLog：";
    private EditText mNameView;
    private EditText mPasswordView;
    private static EditText editText;
    private Button login;
    private Button register;
    private static boolean isLogined=false;//登录是否成功
    private View loginPage;
    private View splashPage;
    public static AbstractXMPPConnection xmppconnection;
    private TextView mSwitchLoginServer;
    private final  int HIDE_LOGIN_PAGE = 1;
    private final  int SHOW_LOGIN_PAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //intputManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        //util = new SettingUtil(this);
        setContentView(R.layout.tt_activity_login);


        SystemConfigSp.instance().init(getApplicationContext());
        mSwitchLoginServer = (TextView)findViewById(R.id.sign_switch_login_server);
        login = (Button) findViewById(R.id.sign_in_button);
        register = (Button) findViewById(R.id.register_in_button);
        mNameView = (EditText) findViewById(R.id.name);
        mPasswordView = (EditText) findViewById(R.id.password);
        splashPage = findViewById(R.id.splash_page);
        loginPage = findViewById(R.id.login_page);


        loginServerAlertDialog();

       /* if (TextUtils.isEmpty(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.USERNAEM))) {
            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER, UrlConstant.ACCESS_MSG_ADDRESS);
        }*/


        final String name = SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.USERNAEM);
        final String password = SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.USERPASSWORD);
        Logger.i(LoginActivity.class," SystemConfigSp name:"+name+"\n"+"SystemConfigSp password:"+password);
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {

            handleNoLoginIdentity(HIDE_LOGIN_PAGE);
            login(name,password);
        }else {

            handleNoLoginIdentity(SHOW_LOGIN_PAGE);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tologin();
                }
            });
        }


        /*mNameView.setText(name);
        mPasswordView.setText(password);
*/
        /*Log.i("name:",""+name +"   "+"password :"+password);
        if(name != "" && password != ""){


        }else {

        }*/

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });



    }

    private void loginServerAlertDialog() {

        mSwitchLoginServer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, android.R.style.Theme_Holo_Light_Dialog));
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialog_view = inflater.inflate(R.layout.tt_custom_dialog, null);
                editText = (EditText)dialog_view.findViewById(R.id.dialog_edit_content);
                editText.setText("112.74.181.112");
                //editText.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER));

                TextView textText = (TextView)dialog_view.findViewById(R.id.dialog_title);
                textText.setText(R.string.switch_login_server_title);
                builder.setView(dialog_view);
                builder.setPositiveButton(getString(R.string.tt_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(!TextUtils.isEmpty(editText.getText().toString().trim()))
                        {
                            //设置服务器host
                            XmppTool.setHost(editText.getText().toString());
                            SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOGINSERVER,editText.getText().toString().trim());
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.tt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }


    private void tologin() {

        final String userName = mNameView.getText().toString();
        final String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        try{

            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(this, getString(R.string.error_pwd_required), Toast.LENGTH_SHORT).show();
                focusView = mPasswordView;
                cancel = true;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, getString(R.string.error_name_required), Toast.LENGTH_SHORT).show();
                focusView = mNameView;
                cancel = true;
            }
            if (cancel) {
                focusView.requestFocus();
            } else {

                Log.i("edit user",""+userName);
                Log.i("edit pass",""+password);

                login(userName,password);

            }
        }catch(NullPointerException e){
            Toast.makeText(LoginActivity.this,"请输入服务器地址！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    private void login(final String userName, final String password){
        new Thread(new Runnable(){
            @Override
            public void run() {

                xmppconnection=XmppTool.XMPPConnection();

                try {
                    //建立连接
                    xmppconnection.connect();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }

                isLogined = XmppTool.userLogin(xmppconnection, userName, password);
                Log.i(TAG,"isLogined的状态："+isLogined);

                Message message = new Message();
                if(isLogined){
                    message.what=0;
                    loginHander.sendMessage(message);
                    SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.USERNAEM,userName);
                    SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.USERPASSWORD,password);

                    //util.saveString(ACCOUNT_KEY,userName);
                    //util.saveString(PASSWORD_KEY,password);
                    Log.i(TAG,"登录成功："+isLogined);
                }else {
                    message.what=1;
                    loginHander.sendMessage(message);
                    Log.i(TAG,"登录不成功："+isLogined);
                    XmppTool.closeConnection(xmppconnection);
                }

            }
        }).start();
    }
    public Handler loginHander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(LoginActivity.this,"登陆成功，欢迎您！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(LoginActivity.this,"登录出错请稍后再试！", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void handleNoLoginIdentity(final int type) {

        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(type == HIDE_LOGIN_PAGE){
                    hideLoginPage();
                }else {
                    showLoginPage();
                }

            }
        }, 1000);
    }


    private void showLoginPage() {
        splashPage.setVisibility(View.GONE);
        loginPage.setVisibility(View.VISIBLE);
    }


    private void hideLoginPage() {
        splashPage.setVisibility(View.VISIBLE);
        loginPage.setVisibility(View.GONE);
    }

}