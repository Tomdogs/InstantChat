package com.rance.chatui.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.util.XmppTool;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/3/20.
 */

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private EditText password, username, Host;
    private final static String TAG = "RegisterActivity:";


    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;
    @Bind(R.id.search)
    ImageButton search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        ButterKnife.bind(this);
        search.setVisibility(View.GONE);
        ibAddMenu.setVisibility(View.GONE);
        tvToolbarTitle.setText("用户注册");
        ivToolbarNavigation.setVisibility(View.VISIBLE);

        register = (Button) findViewById(R.id.toregister);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        Host = (EditText) findViewById(R.id.hosttext);

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String user = username.getText().toString();
                        String pass = password.getText().toString();
                        String ht = Host.getText().toString();

                        Log.i(TAG,"username:"+user);
                        Log.i(TAG,"password:"+pass);
                        Log.i(TAG,"host:"+ht);

                        //XmppTool.setHost(ht);
                        boolean isRegister = XmppTool.userRegister(LoginActivity.xmppconnection, user, pass);

                        Log.i(TAG,"fdgdgsgfgsdfg::::"+isRegister);
                        Message msg = new Message();
                        if(isRegister){
                            msg.what = 0;
                            handler.sendMessage(msg);
                            Log.i(TAG,"已注册了:");
                        }else {
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                    }

                }).start();

            }
        });

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
         switch(msg.what){
                case 0:
                    Toast.makeText(RegisterActivity.this,"注册成功，请登陆！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(RegisterActivity.this,"注册出错，请重试！", Toast.LENGTH_SHORT).show();
                default:
                    break;
         }
         super.handleMessage(msg);
        }

    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
