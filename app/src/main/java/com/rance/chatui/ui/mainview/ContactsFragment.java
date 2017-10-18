package com.rance.chatui.ui.mainview;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.rance.chatui.R;
import com.rance.chatui.adapter.RosterAdapter;
import com.rance.chatui.enity.User;
import com.rance.chatui.ui.activity.AddFriendActivity;
import com.rance.chatui.ui.activity.MainActivity;
import com.rance.chatui.ui.activity.MultiChatRoomActivity;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.util.XmppTool.updateRoster;

public class ContactsFragment extends Fragment{
    private RosterAdapter rosterAdapter;
    private ListView listview;
    public Roster roster;
    private List<User> listRoster;

    @Bind(R.id.llNewFriend)
    LinearLayout newFriend;
    @Bind(R.id.llGroup)
    LinearLayout group;
    @Nullable
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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        verifyStoragePermissions(getActivity());

        View view=inflater.inflate(R.layout.fragment_two,container,false);
        listview=(ListView) view.findViewById(R.id.list_memeber);
        ButterKnife.bind(this,view);
        roster = Roster.getInstanceFor(xmppconnection);
        // 手动处理所有订阅请求。
        roster.setSubscriptionMode(Roster.SubscriptionMode.manual);

        listRoster=updateRoster(roster);


        loadFriend();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = listRoster.get(position);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("user", user.getUser());
                intent.putExtra("userGetFrom", user.getFrom());
                startActivity(intent);
            }
        });

        return view;

    }

    /**
     * 加载好友列表
     */
    private void loadFriend() {

        if (listRoster.size()==0){

            Toast.makeText(getActivity(),"你还没有任何好友",Toast.LENGTH_SHORT);

        }else{

            rosterAdapter = new RosterAdapter(listRoster,getActivity());
            listview.setAdapter(rosterAdapter);
            listview.setOnCreateContextMenuListener(this);

            // 添加花名册监听器，监听好友状态的改变。
            roster.addRosterListener(new RosterListener() {

                @Override
                public void entriesAdded(Collection<String> addresses) {
                    System.out.println("entriesAdded");
                }

                @Override
                public void entriesUpdated(Collection<String> addresses) {
                    System.out.println("entriesUpdated");
                }

                @Override
                public void entriesDeleted(Collection<String> addresses) {
                    System.out.println("entriesDeleted");
                }

                @Override
                public void presenceChanged(Presence presence) {
                    System.out.println("presenceChanged - >" + presence.getStatus());
                }

            });

            //updateRoster(roster);

        }

    }


    @OnClick(R.id.llNewFriend)
    public void onClickNewFriend(View view){
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.llGroup)
    public void onClickGroup(View view){
        Intent intent = new Intent(getActivity(), MultiChatRoomActivity.class);
        startActivity(intent);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        //Logger.i(ContactsFragment.class,"onHiddenChanged 调用了");
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        loadFriend();
        //Logger.i(ContactsFragment.class,"onResume 调用了");
        super.onResume();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
