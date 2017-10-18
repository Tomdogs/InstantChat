package com.rance.chatui.ui.mainview;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.rance.chatui.DB.SQLiteUserDataStore;
import com.rance.chatui.R;
import com.rance.chatui.Service.AddFridendsListenerService;
import com.rance.chatui.Service.ConnectionListenerService;
import com.rance.chatui.Service.FileListenerService;
import com.rance.chatui.Service.MessageListenerService;
import com.rance.chatui.Service.MultiChatListenerService;
import com.rance.chatui.Service.NetWorkStateReceiver;
import com.rance.chatui.Service.RosterListenerService;
import com.rance.chatui.manager.BroadcastManager;
import com.rance.chatui.ui.activity.AddFriendActivity;
import com.rance.chatui.ui.activity.MultiChatRoomActivity;
import com.rance.chatui.util.Constants;
import com.rance.chatui.util.Logger;

import org.jivesoftware.smack.roster.Roster;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.util.XmppTool.closeConnection;


public class MainFragmentActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener ,
        View.OnClickListener,NetWorkStateReceiver.BroadcastData{
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private RecentFragment recentFragment;
    private ContactsFragment contactsFragment;
    private DiscoverFragment discoverFragment;
    private MeFragment meFragment;
    public static Roster roster;

    private final static String TAG ="MainActivityLog";
    private ImageButton ibAddMenu;
    private PopupWindow popWnd;
    private MessageReceiver receiver;
    private FileReceiver fileReceiver;

    public static SQLiteUserDataStore dataStore;
    public static SQLiteDatabase database;

    public static String mediaPath;//数据存储的路径


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        fragmentManager=getFragmentManager();

        mediaPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + this.getPackageName();

        ((RadioGroup)findViewById(R.id.rgcontroller)).setOnCheckedChangeListener(this);
        // ((RadioGroup)findViewById(R.id.rgcontroller)).check(R.id.rbfour);/*默认先显示第一页*/

        ((RadioGroup)findViewById(R.id.rgcontroller)).check(R.id.rbone);/*默认先显示第一页*/


        ibAddMenu = (ImageButton) findViewById(R.id.ibAddMenu);
        ibAddMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainFragmentActivity.this,"点击了！",Toast.LENGTH_LONG).show();
                showPopupWindow();
            }
        });


        //开启消息的服务
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


        //开启群组的的服务
        Intent rosterIntent = new Intent(this, RosterListenerService.class);
        startService(rosterIntent);



        //注册消息广播接收器
       /* receiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.rance.chatui.Service");
        MainFragmentActivity.this.registerReceiver(receiver, filter);*/

        //注册广播接收器
        BroadcastManager broadcastManager = BroadcastManager.getInstance(getApplication());
        broadcastManager.register("com.rance.chatui.Service",new MessageReceiver());
        broadcastManager.register("com.rance.service.FileListenerService",new FileReceiver());
        broadcastManager.register("comn.rance.chatui.Service.MultiChatListenerService",new MultiChatMessageReceiver());

        //注册文件广播接收器
       /* fileReceiver = new FileReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.rance.service.FileListenerService");
        MainFragmentActivity.this.registerReceiver(fileReceiver, intentFilter);*/

        dataStore = new SQLiteUserDataStore(getApplication(),"im.db",null,1);
        database = dataStore.getWritableDatabase();

    }


    private void showPopupWindow() {

        View contentView = LayoutInflater.from(MainFragmentActivity.this).inflate(R.layout.menu_main, null);
        popWnd = new PopupWindow(getApplication());
        popWnd.setContentView(contentView);
        popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!popWnd.isShowing()) {
            // 以下拉方式显示popupwindow


            Log.i("","窗体是否显示："+popWnd.isShowing());

            popWnd.setFocusable(true);
            popWnd.setOutsideTouchable(true);
            // 刷新状态
            popWnd.update();
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0000000000);
            // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
            popWnd.setBackgroundDrawable(dw);

            // 设置SelectPicPopupWindow弹出窗体动画效果
            popWnd.setAnimationStyle(R.style.AnimationPreview);

            popWnd.showAsDropDown(ibAddMenu, 0, 0);
            //popWnd.showAsDropDown(ibAddMenu);

            TextView tvCreateGroup = (TextView) contentView.findViewById(R.id.tvCreateGroup);
            TextView tvAddFriend = (TextView) contentView.findViewById(R.id.tvAddFriend);
            TextView tvScan = (TextView) contentView.findViewById(R.id.tvScan);
            TextView tvHelpFeedback = (TextView) contentView.findViewById(R.id.tvHelpFeedback);


            tvHelpFeedback.setOnClickListener(this);
            tvScan.setOnClickListener(this);
            tvAddFriend.setOnClickListener(this);
            tvCreateGroup.setOnClickListener(this);

        } else {

            popWnd.dismiss();

        }

    }

    /**
     * 显示fragment
     */
    private void showFragment(int index){

        fragmentTransaction=fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);/*想要显示一个fragment,先隐藏所有fragment，防止重叠*/
        Logger.i("radio group index:"+index);
        switch (index){
            case 1:
                /*如果fragment1已经存在则将其显示出来*/
                if (recentFragment != null)
                    fragmentTransaction.show(recentFragment);
			    /*否则是第一次切换则添加fragment1，注意添加后是会显示出来的，replace方法也是先remove后add*/
                else {
                    recentFragment = new RecentFragment();
                    fragmentTransaction.add(R.id.fragment_controller, recentFragment);
                }
                break;
            case 2:
                if(contactsFragment !=null)
                    fragmentTransaction.show(contactsFragment);
                else{
                    contactsFragment =new ContactsFragment();
                    fragmentTransaction.add(R.id.fragment_controller, contactsFragment);
                }
                break;
            case 3:
                if(discoverFragment !=null)
                    fragmentTransaction.show(discoverFragment);
                else{
                    discoverFragment =new DiscoverFragment();
                    fragmentTransaction.add(R.id.fragment_controller, discoverFragment);
                }
                break;
            case 4:
                if(meFragment !=null)
                    fragmentTransaction.show(meFragment);
                else{
                    meFragment =new MeFragment();
                    fragmentTransaction.add(R.id.fragment_controller, meFragment);
                }
                break;
            default:
                /*如果fragment1已经存在则将其显示出来*/
                if (recentFragment != null)
                    fragmentTransaction.show(recentFragment);
			    /*否则是第一次切换则添加fragment1，注意添加后是会显示出来的，replace方法也是先remove后add*/
                else {
                    recentFragment = new RecentFragment();
                    fragmentTransaction.add(R.id.fragment_controller, recentFragment);
                }

        }
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏fragment
     */
    private void hideFragment(FragmentTransaction ft){
        if(recentFragment !=null)
            ft.hide(recentFragment);
        if(contactsFragment !=null)
            ft.hide(contactsFragment);
        if(discoverFragment !=null)
            ft.hide(discoverFragment);
        if(meFragment !=null)
            ft.hide(meFragment);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        Logger.i("radio group onCheckedChanged:"+checkedId);
        
        switch (checkedId){
            case R.id.rbone:
                showFragment(1);
                break;
            case R.id.rbtwo:
                showFragment(2);
                break;
            case R.id.rbthree:
                showFragment(3);
                break;
            case R.id.rbfour:
                showFragment(4);
                break;
            default:
                showFragment(1);
        }


    }


    @Override
    public void onClick(View v) {

        Intent intent;
        int id = v.getId();
        switch (id){
            case R.id.tvAddFriend:
                intent = new Intent(MainFragmentActivity.this,AddFriendActivity.class);
                startActivity(intent);
                popWnd.dismiss();
                break;
            case R.id.tvCreateGroup:
                intent = new Intent(MainFragmentActivity.this,MultiChatRoomActivity.class);
                startActivity(intent);
                popWnd.dismiss();
                break;
            case R.id.tvHelpFeedback:
                popWnd.dismiss();
                break;
            case R.id.tvScan:
                popWnd.dismiss();

            default:
                popWnd.dismiss();

        }
    }



    @Override
    public void netListener() {

    }

    /**
     * 获取消息广播数据
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String resultMessageGetFrom = bundle.getString("messageGetFrom");
            String resultMessage = bundle.getString("messageBody");

            Logger.i(resultMessageGetFrom+" :广播接收的文字消息：" + resultMessage);


            String [] strjid = resultMessageGetFrom.split("/");

            //使用ContentValues 对数据进行组装
            ContentValues values = new ContentValues();
            values.put("jid",strjid[strjid.length-2]);
            values.put("time",System.currentTimeMillis());
            values.put("content",resultMessage);
            values.put("send_accept_state",0);
            values.put("type",Constants.CHAT_ITEM_TYPE_LEFT);
            database.insert("chat",null,values);

            Logger.i("往数据库中加入数据："+strjid[0]);

            //NotificationUtil notificationUtil = new NotificationUtil(MainFragmentActivity.this);
            //notificationUtil.postNotification();

        }
    }

    /**
     * 获取文件广播数据
     */
    public class FileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String filePath = bundle.getString("fileMessageName");
            String fromWhere = bundle.getString("fileFromWhere");

            String from[] = fromWhere.split("/");
            String fromJID = from[0];

            //使用ContentValues 对数据进行组装
            ContentValues values = new ContentValues();
            values.put("jid",fromJID);
            values.put("time",System.currentTimeMillis());
            values.put("filepath",filePath);
            values.put("send_accept_state",0);
            values.put("type", Constants.CHAT_ITEM_TYPE_LEFT);
            database.insert("chat",null,values);


            Logger.i("广播接收的文件消息：" + filePath);


        }
    }


    /**
     * 群组消息的接收并存储
     */
    public class MultiChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            //先清除表数据，群组中数据服务器有保存
            //database.delete("im_group",null,null);

            Bundle bundle =intent.getExtras();
            String messageGetFrom= bundle.getString("multiMessageGetFrom");
            String messageBody= bundle.getString("multiMessageBody");
            String messagetTo= bundle.getString("multiMessagetTo");


            Logger.i("在mianfragmentactiv中 群发的服务监听说接受的东西："+messageGetFrom);
            Logger.i("在mianfragmentactiv中 群发的服务监听说接受的东西："+messageBody);
            Logger.i("在mianfragmentactiv中 群发的服务监听说接受的东西："+messagetTo);

            String strGid [] = messageGetFrom.split("/");
            String gid = strGid[0];
            String jid = strGid[strGid.length-2];

            Logger.i("在mianfragmentactiv中 群发的f分割的gid："+gid);
            Logger.i("在mianfragmentactiv中 群发的f分割的jid："+jid);

            //使用ContentValues 对数据进行组装
            ContentValues values = new ContentValues();
            values.put("gid",gid);
            values.put("get_from",jid);
            values.put("time",System.currentTimeMillis());
            values.put("send_accept_state",0);
            values.put("content",messageBody);

            //Logger.i("群组的用户的名称是："+xmppconnection.getUser());

            if(jid.equals(xmppconnection.getUser().split("/")[0])){

                values.put("type", Constants.CHAT_ITEM_TYPE_RIGHT);
            }else {
                values.put("type", Constants.CHAT_ITEM_TYPE_LEFT);
            }

            database.insert("im_group",null,values);

        }
    }

    NetWorkStateReceiver netWorkStateReceiver;

    //在onResume()方法注册
    @Override
    protected void onResume() {

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        netWorkStateReceiver.setNetData(this);
        System.out.println("注册");
        super.onResume();
    }

    //onPause()方法注销
    @Override
    protected void onPause() {


        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
       //关闭服务
        Intent messageIntent = new Intent(this, MessageListenerService.class);
        stopService(messageIntent);

        Intent fileIntent = new Intent(this,FileListenerService.class);
        stopService(fileIntent);

        //关闭添加朋友的服务
        Intent addFriendIntent = new Intent(this, AddFridendsListenerService.class);
        stopService(addFriendIntent);

        Intent multiChatIntent = new Intent(this, MultiChatListenerService.class);
        stopService(multiChatIntent);

        //关闭连接connection的的服务
        Intent connectionIntent = new Intent(this, ConnectionListenerService.class);
        stopService(connectionIntent);

        Intent rosterIntent = new Intent(this, RosterListenerService.class);
        stopService(rosterIntent);

        //listRoster.clear();

        closeConnection(xmppconnection);

        super.onDestroy();
    }
}
