package com.rance.chatui.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.rance.chatui.R;
import com.rance.chatui.adapter.ChatAdapter;
import com.rance.chatui.adapter.CommonFragmentPagerAdapter;
import com.rance.chatui.enity.MessageInfo;
import com.rance.chatui.enity.MuiltRoom;
import com.rance.chatui.ui.fragment.ChatEmotionFragment;
import com.rance.chatui.ui.fragment.ChatFunctionFragment;
import com.rance.chatui.util.Constants;
import com.rance.chatui.util.DateUtil;
import com.rance.chatui.util.GlobalOnItemClickManagerUtils;
import com.rance.chatui.util.Logger;
import com.rance.chatui.widget.EmotionInputDetector;
import com.rance.chatui.widget.NoScrollViewPager;
import com.rance.chatui.widget.StateButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.rance.chatui.DB.SQLiteUserDataStore.getGroupDataFormJID;
import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.ui.mainview.MainFragmentActivity.dataStore;


/**
 */

public class GroupActivity extends AppCompatActivity{
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

    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;

    @Bind(R.id.ibGroupPicture)
    ImageButton ibGroupPicture;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;


    private String roomJID;
    //private MultiChatMessageReceiver multiChatMessageReceiver;

    public static MultiUserChat multiUserChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        messageInfos = new ArrayList<>();
        messageInfos.clear();



        Intent intent =getIntent();
        String roomName = intent.getStringExtra("roomName");
        roomJID = intent.getStringExtra("jid");
        Log.i("GroupActivity","传过来的roomjid:"+roomJID);
        Log.i("GroupActivity","传过来的roomName:"+roomName);

        initWidget();

        ivToolbarNavigation.setVisibility(View.VISIBLE);
        if(roomJID.contains("@")){
            tvToolbarTitle.setText("群组:"+roomJID.split("@")[0]);
        }else {
            tvToolbarTitle.setText("群组:"+roomJID);
        }

        ibGroupPicture.setVisibility(View.VISIBLE);
        ibAddMenu.setVisibility(View.GONE);


        multiUserChat = MultiUserChatManager.getInstanceFor(xmppconnection).getMultiUserChat(roomJID);
        if(roomJID != null ){
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("comn.rance.chatui.Service.MultiChatListenerService");
            GroupActivity.this.registerReceiver(new MultiChatMessageReceiver(),intentFilter);
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
                .bindToSendButton(emotionSend,1)
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
        //chatAdapter.addItemClickListener(itemClickListener);
        LoadMuiltHistoryData();
    }




    /**
     *加载群组历史数据
     */
    private void LoadMuiltHistoryData() {

        Logger.i("加载群组历史数据的roomJID："+roomJID);
        LinkedList<MuiltRoom> linkedList = getGroupDataFormJID(roomJID,dataStore);
        Logger.i("加载群组历史数据的是否为空："+linkedList);
        for(MuiltRoom m :linkedList){

            Logger.i("qun 文字99消息："+m.getContent());
            Logger.i("qun 文字99时间："+m.getTime());
            Logger.i("qun 文字99type："+m.getType());


            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = dateformat.format(Long.parseLong(m.getTime()));

            MessageInfo messageInfo = new MessageInfo();
            messageInfo.setTime(dateStr);

            String jid = m.getJid();
            if(jid.contains("@")){
                String jidStr [] =jid.split("@");
                messageInfo.setContent(jidStr[0]+"："+m.getContent());
            }else {
                messageInfo.setContent(jid+"："+m.getContent());
            }

                if(m.getType() == Constants.CHAT_ITEM_TYPE_LEFT){
                    messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                    messageInfo.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                }else {
                    messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
                    messageInfo.setHeader("http://img.dongqiudi.com/uploads/avatar/2014/10/20/8MCTb0WBFG_thumb_1413805282863.jpg");
                }



            messageInfos.add(messageInfo);
            chatAdapter.add(messageInfo);
            chatList.scrollToPosition(chatAdapter.getCount() - 1);

        }


        chatAdapter.addAll(messageInfos);
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


    /**
     * 获取群组消息广播数据
     */
    public class MultiChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle =intent.getExtras();

            String messageBody= bundle.getString("multiMessageBody");
            String messageGetFrom= bundle.getString("multiMessageGetFrom");
            String messagetTo= bundle.getString("multiMessagetTo");

            String gidStr  [] = messageGetFrom.split("/");
            String jidStr = gidStr[gidStr.length-2];

            jidStr = jidStr.substring( jidStr.indexOf("/")+1);
            Logger.i("群发的jid"+jidStr);
            if(xmppconnection.getUser().equals(jidStr+"/Smack")){
                Logger.i("自己的getuser2"+xmppconnection.getUser());
            }else {
                Log.i("群发的在线：",""+messageGetFrom+":"+messageBody+":"+messagetTo);
                MessageInfo message=new MessageInfo();
                if(messageGetFrom.contains("/")){
                    String mesStr [] = messageGetFrom.split("/");
                    messageGetFrom = mesStr[mesStr.length-1];
                    message.setContent(messageGetFrom+"："+messageBody);
                }else {
                    message.setContent(messageGetFrom+"："+messageBody);
                }

                message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                message.setTime(DateUtil.getTimeDiffDesc(new Date(System.currentTimeMillis())));
                message.setHeader("http://tupian.enterdesk.com/2014/mxy/11/2/1/12.jpg");
                messageInfos.add(message);
                chatAdapter.add(message);
                chatList.scrollToPosition(chatAdapter.getCount()-1);
            }




        }
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);

        //GroupActivity.this.unregisterReceiver(multiChatMessageReceiver);
    }
}
