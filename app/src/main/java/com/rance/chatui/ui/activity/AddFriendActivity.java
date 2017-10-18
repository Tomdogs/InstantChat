package com.rance.chatui.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.ui.mainview.MainFragmentActivity;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.util.XmppTool.addUser;

/**
 * Created by mac on 2017/3/26.
 */

public class AddFriendActivity extends Activity {
    private final static String TAG = "AddFriendsActivity";
    private TextView searchResult,text_response;
    private ImageView img_addFriend,img_seachFriend;

    private static ProgressDialog dialog;
    private String toJid;
    private MyReceiver receiver;

    private String response,acceptAdd,alertName,alertSubName;
    private  Roster roster;
    private static XMPPConnection conn= LoginActivity.xmppconnection;
    private VCard vCard;
    private final static String ADDFRIEND_ACTION = "comn.example.imforsmack.addFriendsAction";

    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;
    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;
    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;
    @Bind(R.id.search)
    ImageButton search;
    @Bind(R.id.ibAddFriend)
    TextView ibAddFriend;
    @Bind(R.id.llSearchUser)
    LinearLayout llSearchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend_activity);

        ButterKnife.bind(this);

        img_seachFriend = (ImageView) findViewById(R.id.img_seachFriend);
        img_addFriend = (ImageView) findViewById(R.id.img_addFriend);
        searchResult = (TextView) findViewById(R.id.searchResult);
        text_response = (TextView) findViewById(R.id.text_response);



        roster = Roster.getInstanceFor(xmppconnection);

        //注册广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ADDFRIEND_ACTION);
        registerReceiver(receiver, intentFilter);



        ivToolbarNavigation.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("新的好友");
        ibAddMenu.setVisibility(View.GONE);
        search.setVisibility(View.GONE);
        ibAddFriend.setVisibility(View.VISIBLE);
        ibAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSearchUser();
            }
        });



        img_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(dialog == null){
                    dialog =new ProgressDialog(AddFriendActivity.this);
                }
                dialog.setTitle("请等待");
                dialog.setMessage("正在发送好友申请...");
                dialog.setCancelable(true);
                dialog.show();
                //添加好友的副线程
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            String friendName = toJid;
                            boolean result = addUser(roster, toJid, friendName,null);
                            Message msg = new Message();
                            msg.what= 0x11;
                            Bundle b = new Bundle();
                            b.putBoolean("result", result);
                            msg.setData(b);
                            AddFriendHandler.sendMessage(msg);
                        } catch (Exception e) {
                            System.out.println("申请发生异常！！");
                            e.printStackTrace();
                        }
                    }
                });
                //启动线程和实例化handler
                thread.start();

            }
        });
    }

    @OnClick(R.id.llSearchUser)
    public void clickSearchUser(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AddFriendActivity.this);
        builder.setTitle("搜索好友");
        View dialogView = LayoutInflater.from(AddFriendActivity.this).inflate(R.layout.enter_jid_dialog, null);
        final TextView jabberIdDesc = (TextView) dialogView.findViewById(R.id.jabber_id);
        jabberIdDesc.setText(R.string.account_settings_jabber_id);

        final AutoCompleteTextView jid = (AutoCompleteTextView) dialogView.findViewById(R.id.jid);

        if(jid.getText() != null){
            jid.getText().append("@"+xmppconnection.getServiceName());
        }

        jid.setHint(R.string.account_settings_example_jabber_id);
        builder.setView(dialogView);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddFriendActivity.this,"点击了确定",Toast.LENGTH_LONG).show();

                Message msg = new Message();
                msg.what= 0x12;
                Bundle b = new Bundle();
                b.putString("jid",jid.getText().toString());
                msg.setData(b);
                AddFriendHandler.sendMessage(msg);
            }
        });
        builder.show();
    }


    //handler更新UI线程
    public Handler AddFriendHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0x11:

                    if(dialog != null){
                        dialog.setMessage("发送成功！");
                        dialog.dismiss();
                    }
                    Bundle b = msg.getData();
                    Boolean res = b.getBoolean("result");
                    if (res == true){
                        System.out.println("button发送添加好友请求成功！！");
                    }
                    break;
                case 0x12:
                    Bundle bundle = msg.getData();
                    String tojid = bundle.get("jid").toString();
                    Logger.i("从dialog中传来的得到数据jid："+tojid);
                    toJid = tojid;
                    searchFriend(tojid);
                    break;
            }
        }
    };


    /**
     * 根据jid搜索用户
     * @param tojid
     */
    private void searchFriend(String tojid){

        XmppTool.getInstance().searchUsers(tojid);
        Log.i(TAG,"搜索的用户"+tojid);
        try{
            vCard= XmppTool.getUserVCard(tojid);
            Log.i(TAG,"搜索的vCard"+vCard);

        }catch (XMPPException e){
            e.printStackTrace();
        }
        if (vCard!=null){
            searchResult.setText(tojid);
            img_addFriend.setVisibility(View.VISIBLE);
            //从服务器查询用户头像，并没有进行搜索，自行修改
            Bitmap bitmap = XmppTool.getUserImage(tojid);
            if(bitmap != null){
                img_seachFriend.setVisibility(View.VISIBLE);
                img_seachFriend.setBackgroundDrawable(new BitmapDrawable(bitmap));
            }

        }else{
            searchResult.setText("抱歉，未找到该用户");
            img_addFriend.setVisibility(View.GONE);
            img_seachFriend.setVisibility(View.GONE);
        }
    }



    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //接收传递的字符串response
            Bundle bundle = intent.getExtras();
            response = bundle.getString("response");
            System.out.println("广播收到"+response);

            text_response.setText(response);
            if(response==null){
                //获取传递的字符串及发送方JID
                acceptAdd = bundle.getString("acceptAdd");
                alertName = bundle.getString("fromName");

                Log.i(TAG,"alertName"+alertName);
                Log.i(TAG,"acceptAdd"+acceptAdd);

                if(alertName!=null){
                    //裁剪JID得到对方用户名
                    alertSubName = alertName.substring(0,alertName.indexOf("@"));
                }

                if(acceptAdd.equals("收到添加请求！")){
                    //弹出一个对话框，包含同意和拒绝按钮
                    AlertDialog.Builder builder  = new AlertDialog.Builder(AddFriendActivity.this);
                    builder.setTitle("添加好友请求" ) ;
                    builder.setMessage("用户"+alertName+"请求添加你为好友" ) ;

                    builder.setPositiveButton("同意",new DialogInterface.OnClickListener() {
                        //同意按钮监听事件，发送同意Presence包及添加对方为好友的申请
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.subscribed);
                            presenceRes.setTo(alertName);
                            try {
                                conn.sendStanza(presenceRes);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }

                            addUser(MainFragmentActivity.roster, alertName, alertSubName,null);
                        }
                    });

                    builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        //拒绝按钮监听事件，发送拒绝Presence包
                        @Override
                        public void onClick(DialogInterface dialog, int arg1) {
                            Presence presenceRes = new Presence(Presence.Type.unsubscribe);
                            presenceRes.setTo(alertName);
                            try {
                                conn.sendStanza(presenceRes);
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.show();
                }
            }

        }

    }
}
