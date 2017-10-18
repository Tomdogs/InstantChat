package com.rance.chatui.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.rance.chatui.R;
import com.rance.chatui.adapter.ChatAdapter;
import com.rance.chatui.adapter.CommonFragmentPagerAdapter;
import com.rance.chatui.enity.FullImageInfo;
import com.rance.chatui.enity.MessageInfo;
import com.rance.chatui.manager.BroadcastManager;
import com.rance.chatui.ui.fragment.ChatEmotionFragment;
import com.rance.chatui.ui.fragment.ChatFunctionFragment;
import com.rance.chatui.util.Constants;
import com.rance.chatui.util.DateUtil;
import com.rance.chatui.util.GlobalOnItemClickManagerUtils;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.MediaManager;
import com.rance.chatui.widget.EmotionInputDetector;
import com.rance.chatui.widget.NoScrollViewPager;
import com.rance.chatui.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.rance.chatui.DB.SQLiteUserDataStore.getCahtDataFormJID;
import static com.rance.chatui.ui.mainview.MainFragmentActivity.dataStore;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.chat_list)
    EasyRecyclerView chatList;
    @Bind(R.id.emotion_voice)
    ImageView emotionVoice;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.voice_text)
    TextView voiceText;
    @Bind(R.id.emotion_button)
    ImageView emotionButton;
    @Bind(R.id.emotion_add)
    ImageView emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.emotion_layout)
    RelativeLayout emotionLayout;
    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;
    @Bind(R.id.ibUserPicture)
    ImageButton ibUserPicture;
    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;
    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;


    public static Chat newChat;
    public static String toUser, userGetFrom;

    private LinearLayout fileIsvisibility;

    private ProgressBar bar;
    // 记录ProgressBar的完成进度
    private double progressStatus = 0;
    private static double progress;
    private boolean flage = true;

    //录音相关
    int animationRes = 0;
    int res = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    // public static File externalFileDir;

    /**
     * Flag indicates that we have loaded the history for the first time.
     */
    private boolean historyLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        toUser = intent.getStringExtra("user");
        userGetFrom = intent.getStringExtra("userGetFrom");
        System.out.println("user:" + toUser);
        System.out.println("userGetFrom:" + userGetFrom);


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            verifyStoragePermissions(MainActivity.this);

        }

        messageInfos = new ArrayList<>();
        initMemu();
        initWidget();


        fileIsvisibility = (LinearLayout) findViewById(R.id.fileIsvisibility);
        bar = (ProgressBar) findViewById(R.id.chat_bar);
        ChatManager chatmanager = ChatManager.getInstanceFor(LoginActivity.xmppconnection);
        newChat = chatmanager.createChat(toUser, null);

        if(toUser != null){

            BroadcastManager broadcastManager = BroadcastManager.getInstance(getApplication());
            broadcastManager.register("com.rance.chatui.Service",new MessageReceiver());
            broadcastManager.register("com.rance.service.FileListenerService",new FileReceiver());

        }


    }



    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend, 0)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();

        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);

        loadHistoryData();

    }

    /**
     * 初始化菜单
     */
    private void initMemu(){
        ivToolbarNavigation.setVisibility(View.VISIBLE);
        ibUserPicture.setVisibility(View.VISIBLE);
        ibAddMenu.setVisibility(View.GONE);
        if(toUser.contains("@")){
            String toJid = toUser.split("@")[0];
            tvToolbarTitle.setText(toJid);
        }else {
            tvToolbarTitle.setText(toUser);
        }
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener = new ChatAdapter.onItemClickListener() {
        @Override
        public void onHeaderClick(int position) {
            Toast.makeText(MainActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onImageClick(View view, int position) {

            int location[] = new int[2];
            view.getLocationOnScreen(location);

            String imageUrl = messageInfos.get(position).getImageUrl();


            if( imageUrl == null ){
                //文件中的图片
                String fileUrl = messageInfos.get(position).getFilepath();

                FullImageInfo fullImageInfo = new FullImageInfo();
                fullImageInfo.setWidth(view.getWidth());
                fullImageInfo.setHeight(view.getHeight());
                fullImageInfo.setImageUrl(fileUrl);

                Logger.i("item点击事件的位置：" + position);
                //Log.i(MainActivity.this + "", "路径：" + externalFileDir + messageInfos.get(position).getImageUrl());
                Log.i(MainActivity.this + "", "路径：" + imageUrl);

                EventBus.getDefault().postSticky(fullImageInfo);
                startActivity(new Intent(MainActivity.this, FullImageActivity.class));
                overridePendingTransition(0, 0);

            }else {
                //图片
                FullImageInfo fullImageInfo = new FullImageInfo();
                fullImageInfo.setWidth(view.getWidth());
                fullImageInfo.setHeight(view.getHeight());
                fullImageInfo.setImageUrl(imageUrl);

                Logger.i("item点击事件的位置：" + position);
                //Log.i(MainActivity.this + "", "路径：" + externalFileDir + messageInfos.get(position).getImageUrl());
                Log.i(MainActivity.this + "", "路径：" + imageUrl);

                EventBus.getDefault().postSticky(fullImageInfo);
                startActivity(new Intent(MainActivity.this, FullImageActivity.class));
                overridePendingTransition(0, 0);
            }


        }

        @Override
        public void onVoiceClick(final ImageView imageView, final int position) {
            if (animView != null) {
                animView.setImageResource(res);
                animView = null;
            }
            switch (messageInfos.get(position).getType()) {
                case 1:
                    animationRes = R.drawable.voice_left;
                    res = R.mipmap.icon_voice_left3;
                    break;
                case 2:
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    break;
            }
            animView = imageView;
            animView.setImageResource(animationRes);
            animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.start();
            MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    animView.setImageResource(res);
                }
            });


        }
    };

    /**
     * 加载历史数据
     */
    private void loadHistoryData() {


        //chatAdapter.addAll(messageInfos);


        LinkedList<MessageInfo> infoLinkedList =  getCahtDataFormJID(toUser,dataStore);
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for(MessageInfo m :infoLinkedList){
            Logger.i("文字99消息："+m.getContent());
            Logger.i("文字99时间："+m.getTime());
            Logger.i("文字99type："+m.getType());

            String dateStr = dateformat.format(Long.parseLong(m.getTime()));

            MessageInfo message = new MessageInfo();
            message.setTime(dateStr);

            if(m.getContent() == null){

                message.setFilepath(m.getFilepath());
                if(m.getType() == Constants.CHAT_ITEM_TYPE_LEFT){
                    Logger.i("文字type："+m.getType());
                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                }else {
                    Logger.i("文字type："+m.getType());
                    message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                    message.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");

                }

            }else {

                message.setContent(m.getContent());
                if(m.getType() == Constants.CHAT_ITEM_TYPE_LEFT){

                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                }else {
                    message.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                    message.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
                }
            }

            //message.setType(Constants.CHAT_ITEM_TYPE_LEFT);

            messageInfos.add(message);
            chatAdapter.add(message);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);

        }




    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(final MessageInfo messageInfo) {
        messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
        chatList.scrollToPosition(chatAdapter.getCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }





/*

    */
/**
 * 获取消息广播数据
 *//*

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            resultMessageGetFrom = bundle.getString("messageGetFrom");
            resultMessage = bundle.getString("messageBody");
            Log.i("聊天数据：", "" + bundle);


            MessageInfo message = new MessageInfo();
            message.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
            message.setContent(resultMessage);
            message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
            message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
            messageInfos.add(message);
            chatAdapter.add(message);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);

        }
    }


    */
    /**
     * 获取文件广播数据
     */
/*

  public class FileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String fileName = bundle.getString("fileMessageName");
            String fromWhere = bundle.getString("fileFromWhere");

            System.out.println("FileReceiver接收的消息：" + fileName);

            new Thread() {
                public void run() {
                    // while (progressStatus < 100) {

                    landingDir = new File(externalFileDir, incomingFileTransfer.getFileName());
                    Logger.i("jie存储的路径是landingDir:" + landingDir);

                    try {
                        //接收文件
                        incomingFileTransfer.recieveFile(landingDir);

                        while (!incomingFileTransfer.isDone()) {
                            if (incomingFileTransfer.getStatus().equals(FileTransfer.Status.error)) {

                                Log.i("123", "jie文件传输出错了：" + incomingFileTransfer.getError());
                                return;
                            } else {

                                progress = incomingFileTransfer.getProgress();

                                progressStatus = progress * 100;
                                //Logger.i("jie文件传输的状态："+progress);
                                //Logger.i("jie文件传输的状态："+incomingFileTransfer.getStatus());
                                //Logger.i("jie存储的路径是landingDir66666:" + landingDir);

                                Message m = new Message();

                                // 发送消息到Handler
                                handler.sendMessage(m);
                                if (progressStatus > 30) {
                                    m.what = 0x111;
                                }


                            }

                        }


                    } catch (SmackException e) {
                        e.printStackTrace();
                        Log.e("123", "jie文件传输失败时");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("123", "jie  IO文件异常！");
                        //}

                    }
                    Log.i("sdfdsa", "线程While循环结束！");

                }
            }.start();

            progressStatus = 0;
            flage = true;
            Log.i("sdfdsa", "线程执行完毕！");
        }
    }

*/


    /**
     * 获取消息广播数据
     */
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String resultMessageGetFrom = bundle.getString("messageGetFrom");
            String resultMessage = bundle.getString("messageBody");

            Logger.i(resultMessageGetFrom+" : main广播接收的文字消息：" + resultMessage);

            if(resultMessageGetFrom.contains("/")){
                resultMessageGetFrom = resultMessageGetFrom.split("/")[0];
                Logger.i(resultMessageGetFrom+" : 分割后的resultMessageGetFrom：");
            }
            Logger.i(toUser+" : toUser main广播接收的文字消息：");
            if(resultMessageGetFrom.equals(toUser)){


                MessageInfo message = new MessageInfo();
                message.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
                message.setContent(resultMessage);
                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                messageInfos.add(message);
                chatAdapter.add(message);
                //页面在没有加载之前不能调用
                if(chatList != null){
                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                }

            }



            /*String [] strjid = resultMessageGetFrom.split("/");



            //使用ContentValues 对数据进行组装
            ContentValues values = new ContentValues();
            values.put("jid",strjid[strjid.length-2]);
            values.put("time",System.currentTimeMillis());
            values.put("content",resultMessage);
            values.put("send_accept_state",0);
            values.put("type",Constants.CHAT_ITEM_TYPE_LEFT);
            database.insert("chat",null,values);
*/
            //Logger.i("main往数据库中加入数据："+strjid[0]);

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

            if(fromWhere.contains("/")){
                fromWhere = fromWhere.split("/")[0];
            }
            Logger.i(fromWhere+" : main广播接收的文字消息：");
            Logger.i(toUser+" : main广播接收的文字消息：");
            if(fromWhere.equals(toUser)){


                Logger.i("main广播接收的文件消息：" + filePath);


                MessageInfo message = new MessageInfo();
                message.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));

                String fileTpye []= filePath.split("\\.");
                String type= fileTpye[fileTpye.length-1];

                if(type.equals("jpg")||type.equals("jpeg")||type.equals("png")||type.equals("bmp")||type.equals("gif")){

                    Logger.i("接收的图片路径："+ filePath);

                    message.setImageUrl(filePath);
                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                    messageInfos.add(message);
                    //chatAdapter.add(message);


                }else {

                    Logger.i("接收的文件路径："+ filePath);
                    message.setFilepath(filePath);
                    message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");


                    if(type.equals("amr")){
                        //Logger.i("接收的音频的时长："+getVoiceTime(filePath));
                        message.setVoiceTime(2);
                        messageInfos.add(message);
                        //chatAdapter.add(message);
                        //chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    }else {

                        messageInfos.add(message);

                        //chatList.scrollToPosition(chatAdapter.getCount() - 1);
                    }



                }

                chatAdapter.add(message);
                if(chatList != null ){
                    chatList.scrollToPosition(chatAdapter.getCount() - 1);
                }
            }
/*
            String from[] = fromWhere.split("/");
            String fromJID = from[0];

            //使用ContentValues 对数据进行组装
            ContentValues values = new ContentValues();
            values.put("jid",fromJID);
            values.put("time",System.currentTimeMillis());
            values.put("filepath",filePath);
            values.put("send_accept_state",0);
            values.put("type", Constants.CHAT_ITEM_TYPE_LEFT);
            database.insert("chat",null,values);*/




        }
    }


    //解决Android API 23不能读写到手机内部存储的问题
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    /**
     * 得到音频的长度
     * @param string
     * @return
     */
    private int getVoiceTime(String string)
    {   //使用此方法可以直接在后台获取音频文件的播放时间，而不会真的播放音频
        MediaPlayer player = new MediaPlayer();  //首先你先定义一个mediaplayer
        try {
            player.setDataSource(string);  //String是指音频文件的路径
            player.prepare();        //这个是mediaplayer的播放准备 缓冲

        } catch (IllegalArgumentException e)
        { e.printStackTrace();  }
        catch (SecurityException e)
        {   e.printStackTrace();  }
        catch (IllegalStateException e)
        {e.printStackTrace();  } catch (IOException e)
        {e.printStackTrace();  }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {//监听准备

            @Override
            public void onPrepared(MediaPlayer player)
            {
                int size = player.getDuration();
                String timelong = size / 1000 + "''";

            }
        });
        int size =player.getDuration();//得到音频的时间
        String  timelong1 = (int) Math.ceil((size / 1000)) + "''";//转换为秒 单位为''
        player.stop();//暂停播放
        player.release();//释放资源
        return  size;  //返回音频时间

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
