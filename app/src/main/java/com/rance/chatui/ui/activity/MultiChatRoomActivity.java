package com.rance.chatui.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.adapter.MultiRoomAdapter;
import com.rance.chatui.enity.Room;
import com.rance.chatui.util.Logger;
import com.rance.chatui.util.XmppTool;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rance.chatui.util.XmppTool.getAllMucRoom;


public class MultiChatRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText createRoomName_edit,createRoomNickName_edit,searchRoom_edit,searchNickRoom_edit;
    private Button ceateRomm_button,searchRoom_button;
    private TextView mulitChat;
    private TextView isCreate_text,isSearch_text;
    private ListView listMulitRoom;
    private List<Room> roomList = new ArrayList<>();
    private MultiRoomAdapter multiRoomAdapter;
    private final static String TAG = "MultiChatRoomActivity";
    XMPPConnection conn = LoginActivity.xmppconnection;
    String userJID =conn.getUser();


    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;

    @Bind(R.id.ibGroupPicture)
    ImageButton ibGroupPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ButterKnife.bind(this);

        createRoomName_edit = (EditText) findViewById(R.id.createRoomName_edit);
        createRoomNickName_edit = (EditText) findViewById(R.id.createRoomNickName_edit);
        searchRoom_edit = (EditText) findViewById(R.id.searchRoom_edit);
        searchNickRoom_edit = (EditText) findViewById(R.id.searchNickRoom_edit);

        ceateRomm_button = (Button) findViewById(R.id.ceateRomm_button);
        searchRoom_button = (Button) findViewById(R.id.searchRoom_button);

        isCreate_text = (TextView) findViewById(R.id.isCreate_text);
        isSearch_text = (TextView) findViewById(R.id.isSearch_text);

        listMulitRoom = (ListView) findViewById(R.id.listMulitRoom);
        roomList.clear();
        multiRoomAdapter = new MultiRoomAdapter(this,roomList);
        listMulitRoom.setAdapter(multiRoomAdapter);
        listMulitRoom.setOnCreateContextMenuListener(this);

        ivToolbarNavigation.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("群组");

        ceateRomm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String roomName = createRoomName_edit.getText().toString();
                String roomNick = createRoomNickName_edit.getText().toString();
                //multiUserChat_share= XmppTool.createChatRoom(roomName,userJID,"123");
                int isCreate = XmppTool.createChatRoom(roomName,userJID,"123");

                if(isCreate==1){
                    Toast.makeText(MultiChatRoomActivity.this,"你已经加入了群聊！", Toast.LENGTH_SHORT).show();
                    isCreate_text.setText("你已经加入了群聊！！");
                }else if(isCreate==0){
                    isCreate_text.setText("创建成功!");
                    updateListMultiRoomList();
                }else {
                    isCreate_text.setText("创建不成功！");
                }



            }
        });

        searchRoom_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String roomName = searchRoom_edit.getText().toString();
                //String roomNick = searchNickRoom_edit.getText().toString();

                //multiUserChat_share= XmppTool.joinChatRoom(roomName, userJID,"123");
                boolean isJoin = XmppTool.joinChatRoom(roomName, userJID,"123");

                if(!isJoin){
                    isSearch_text.setText("搜索或创建不成功！");
                }else {
                    isSearch_text.setText("搜索或创建成功!");

//                    roomList.clear();
                    updateListMultiRoomList();

                }


            }
        });

        listMulitRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Room room = roomList.get(position);
                Intent intent = new Intent(MultiChatRoomActivity.this,GroupActivity.class);
                intent.putExtra("roomName",room.getName());
                intent.putExtra("jid",room.getJid());

                Logger.i("共享的room.getJid()"+""+room.getJid());

                //multiUserChat_share = multiUserChatManager.getMultiUserChat(room.getJid());
                Log.i(TAG,"room的name1："+room.getName());
                String roomNames = room.getJid().split("@")[0];
                Log.i(TAG,"room的name2："+roomNames);

                //multiUserChat_share= XmppTool.joinChatRoom(roomNames, userJID,"123");

                boolean isJoin = XmppTool.joinChatRoom(roomNames, userJID,"123");

                startActivity(intent);
            }
        });


        updateListMultiRoomList();



    }

    @OnClick(R.id.ibAddMenu)
    public void ibAddMenu(){
        Intent intent  = new Intent(MultiChatRoomActivity.this,CreateGroupActivity.class);
        startActivity(intent);
    }




    private void updateListMultiRoomList(){

        try {

            List<MultiUserChat> listRoom = getAllMucRoom();

            Log.i(TAG,"已加入到群的总数："+listRoom.size());


            //循环获取user加入的群的ID号
            for(MultiUserChat hostedRoom : listRoom){


                Room room =new Room();

                String name = hostedRoom.getNickname();
                String jid =hostedRoom.getRoom();

                room.setName(name);
                room.setJid(jid);

                roomList.add(room);

                Log.i(TAG,"群名"+name);
                Log.i(TAG,"群的jid"+jid);

            }
            multiRoomAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Handler handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 0x112:{
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

    }
}
