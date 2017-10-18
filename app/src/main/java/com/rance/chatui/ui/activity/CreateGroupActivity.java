package com.rance.chatui.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rance.chatui.R;
import com.rance.chatui.adapter.CheckBoxAdapter;
import com.rance.chatui.enity.User;

import org.jivesoftware.smack.roster.Roster;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.util.XmppTool.updateRoster;

public class CreateGroupActivity extends AppCompatActivity {



    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;

    @Bind(R.id.ibAddFriend)
    TextView ibAddFriend;
    @Bind(R.id.search)
    ImageButton search;


    private ListView mListView;
    private List<User> models;
    private CheckBox mMainCkb;
    private CheckBoxAdapter checkBoxAdapter;

    //监听来源
    public boolean mIsFromItem = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        ButterKnife.bind(this);

        initView();
        initData();
        initViewOper();

    }

    private void initView(){

        ivToolbarNavigation.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("发起群聊");
        ibAddFriend.setText("确定");
        ibAddFriend.setVisibility(View.VISIBLE);
        ibAddMenu.setVisibility(View.GONE);
        search.setVisibility(View.GONE);

        mListView = (ListView) findViewById(R.id.list_main);
        mMainCkb = (CheckBox) findViewById(R.id.ckb_main);
    }



    /**
     * 数据加载
     */
    private void initData() {
        //模拟数据
        //models = new ArrayList<>();

        models =updateRoster(Roster.getInstanceFor(xmppconnection));
    }

    /**
     * 数据绑定
     */
    private void initViewOper() {
        checkBoxAdapter = new CheckBoxAdapter(models, CreateGroupActivity.this, new AllCheckListener() {

            @Override
            public void onCheckedChanged(boolean b) {
                //根据不同的情况对maincheckbox做处理
                if (!b && !mMainCkb.isChecked()) {
                    return;
                } else if (!b && mMainCkb.isChecked()) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(false);
                } else if (b) {
                    mIsFromItem = true;
                    mMainCkb.setChecked(true);
                }
            }
        });

        mListView.setAdapter(checkBoxAdapter);


        //全选的点击监听
        mMainCkb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //当监听来源为点击item改变maincbk状态时不在监听改变，防止死循环
                if (mIsFromItem) {
                    mIsFromItem = false;
                    Log.e("mainCheckBox", "此时我不可以触发");
                    return;
                }

                //改变数据
                for (User user : models) {
                    user.setIscheck(b);
                }
                //刷新listview
                checkBoxAdapter.notifyDataSetChanged();
            }
        });

    }

    //对item导致maincheckbox改变做监听
    public interface AllCheckListener {
        void onCheckedChanged(boolean b);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
