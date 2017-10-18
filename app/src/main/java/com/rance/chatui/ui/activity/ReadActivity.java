package com.rance.chatui.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.jude.rollviewpager.Util;
import com.rance.chatui.R;
import com.rance.chatui.adapter.holder.NewsViewHolder;
import com.rance.chatui.enity.News;
import com.rance.chatui.util.JsonParse;
import com.rance.chatui.util.Logger;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ReadActivity extends AppCompatActivity implements RecyclerArrayAdapter.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.ivToolbarNavigation)
    ImageView ivToolbarNavigation;

    @Bind(R.id.tvToolbarTitle)
    TextView tvToolbarTitle;

    @Bind(R.id.ibAddMenu)
    ImageButton ibAddMenu;
    @Bind(R.id.search)
    ImageButton search;

    private FloatingActionButton top;
    private EasyRecyclerView listViewRecentChat;
    private RecyclerArrayAdapter<News> adapter;
    private int page = 0;
    private Handler handler = new Handler();
    private boolean hasNetWork = true;
    private ArrayList<News> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ButterKnife.bind(this);
        DownTask task = new DownTask();
        task.execute();

        listViewRecentChat = (EasyRecyclerView) findViewById(R.id.recent_chat_list);
        top = (FloatingActionButton) findViewById(R.id.top);

        ivToolbarNavigation.setVisibility(View.VISIBLE);
        tvToolbarTitle.setText("新闻头条");
        search.setVisibility(View.GONE);
        ibAddMenu.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ReadActivity.this);
        listViewRecentChat.setLayoutManager(layoutManager);

        DividerDecoration itemDecoration = new DividerDecoration(getResources().getColor(R.color.bg_content), Util.dip2px(this,10f), Util.dip2px(this,72),0);
        itemDecoration.setDrawLastItem(false);
        listViewRecentChat.addItemDecoration(itemDecoration);


        listViewRecentChat.setAdapterWithProgress(adapter = new RecyclerArrayAdapter<News>(ReadActivity.this) {
            @Override
            public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
                return new NewsViewHolder(parent);
            }
        });

        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_nomore);

     /*   adapter.setOnItemLongClickListener(new RecyclerArrayAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(int position) {
                //listViewRecentChat.getAdapter().getItemId(position);
                Toast.makeText(ReadActivity.this,"删除了列表："+adapter.getItem(position).getLtitle(),Toast.LENGTH_LONG).show();
                adapter.remove(position);
                return true;
            }
        });*/

        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                //Toast.makeText(ReadActivity.this,"点击了"+adapter.getItem(position).getLtitle(),Toast.LENGTH_LONG).show();
                String url = adapter.getItem(position).getUrl3w();
                Logger.i("url："+url);
                Intent intent = new Intent(ReadActivity.this, WebViewActivity.class);
                intent.putExtra("url",url);
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
                Toast.makeText(ReadActivity.this,"点击了到顶！",Toast.LENGTH_LONG).show();
                listViewRecentChat.scrollToPosition(0);
            }
        });

        listViewRecentChat.setRefreshListener(this);

    }


    @Override
    public void onRefresh() {


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

                for(News news : arrayList){
                    Logger.i("onRefresh page 的title："+news.getLtitle());
                    Logger.i("onRefresh page time："+news.getPtime());
                }


                adapter.addAll(arrayList);
                page=+20;
            }
        }, 1000);
    }

    @Override
    public void onLoadMore() {


       handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //刷新
                if (!hasNetWork) {
                    adapter.pauseMore();
                    return;
                }

                Logger.i("onLoadMore page 的值："+page);

                adapter.addAll(arrayList);
                page=+20;
            }
        }, 1000);

    }






    class DownTask extends AsyncTask<String,Integer,String>{

        public DownTask() {
        }

        @Override
        protected String doInBackground(String... params) {

            JsonParse jsonParse = new JsonParse();
            String s = jsonParse.connection();
            arrayList =  jsonParse.parse(s);
/*
            for(News ns : arrayList){

                Logger.i("解析的json数据："+ns.getLtitle());
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            onRefresh();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
