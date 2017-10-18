package com.rance.chatui.ui.mainview;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;
import com.rance.chatui.R;
import com.rance.chatui.adapter.holder.PersonViewHolder;
import com.rance.chatui.enity.Person;
import com.rance.chatui.ui.activity.MainActivity;
import com.rance.chatui.util.Logger;

import java.util.ArrayList;

import static com.rance.chatui.DB.SQLiteUserDataStore.queryRecentChatData;
import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;
import static com.rance.chatui.ui.mainview.MainFragmentActivity.dataStore;

public class RecentFragment extends Fragment implements RecyclerArrayAdapter.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener{

    private FloatingActionButton top;
    private EasyRecyclerView listViewRecentChat;
    private RecyclerArrayAdapter<Person> adapter;
    private int page = 0;
    private Handler handler = new Handler();
    private boolean hasNetWork = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_one,container,false);

        //ButterKnife.bind(this,view);

        listViewRecentChat = (EasyRecyclerView) view.findViewById(R.id.recent_chat_list);
        top = (FloatingActionButton) view.findViewById(R.id.top);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listViewRecentChat.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(Color.GRAY, Util.dip2px(getActivity(),0.5f), Util.dip2px(getActivity(),72),0);
        itemDecoration.setDrawLastItem(false);
        listViewRecentChat.addItemDecoration(itemDecoration);


        listViewRecentChat.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<Person>(getActivity()) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new PersonViewHolder(parent);
            }
        });

        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);

        adapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                //listViewRecentChat.getAdapter().getItemId(position);
                Toast.makeText(getActivity(),"删除了列表："+adapter.getItem(position).getName(),Toast.LENGTH_LONG).show();
                adapter.remove(position);
                return true;
            }
        });

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Toast.makeText(getActivity(),"点击了"+adapter.getItem(position).getName(),Toast.LENGTH_LONG).show();
                String tojid = adapter.getItem(position).getName()+"@"+xmppconnection.getServiceName();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("user",tojid);
                startActivity(intent);
            }
        });

        adapter.setError(R.layout.view_error, new RecyclerArrayAdapter.OnErrorListener() {
            @Override
            public void onErrorShow() {
                adapter.resumeMore();
            }

            @Override
            public void onErrorClick() {
                adapter.resumeMore();
            }
        });

        top.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"点击了到顶！",Toast.LENGTH_LONG).show();
                listViewRecentChat.scrollToPosition(0);
            }
        });

        listViewRecentChat.setRefreshListener(this);
        onRefresh();
        return view;
    }


   //第四页会返回空,意为数据加载结束
    @Override
    public void onLoadMore() {
        Logger.i("onLoadMore 执行力度1");
        //Log.i("EasyRecyclerView","onLoadMore");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }
                Logger.i("onLoadMore page 的值："+page);
                ArrayList<Person>  persons = queryRecentChatData(dataStore,page);
                for(Person p : persons){

                    Logger.i("onLoadMore 得到的名字1："+p.getName());
                    Logger.i("onLoadMore 得到的内容1："+p.getSign());
                }
                adapter.addAll(persons);
                page=+20;
            }
        }, 1000);
    }

    @Override
    public void onRefresh() {

        Logger.i("onRefresh 执行力度2");

        page = 0;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.clear();

                Logger.i("onRefresh 执行了2:"+hasNetWork);

                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }

                Logger.i("onRefresh page 的值："+page);
                ArrayList<Person>  persons = queryRecentChatData(dataStore,page);
                for(Person p : persons){

                    Logger.i("onRefresh 得到的名字2："+p.getName());
                    Logger.i("onRefresh 得到的内容2："+p.getSign());
                }
                Logger.i("onRefresh 执行了2:");
                adapter.addAll(persons);
                page=+20;
            }
        }, 2000);
    }




    @Override
    public void onHiddenChanged(boolean hidden) {
        Logger.i(ContactsFragment.class,"onHiddenChanged 调用了");
        //queryRecentChatData(dataStore);
        //onRefresh();
        //onLoadMore();
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        //onRefresh();
        //onLoadMore();
        //queryRecentChatData(dataStore);
        Logger.i(ContactsFragment.class,"onResume 调用了");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }
}
