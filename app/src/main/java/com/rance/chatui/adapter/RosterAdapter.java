package com.rance.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rance.chatui.R;
import com.rance.chatui.enity.User;

import java.util.List;

//import com.johnny.fragmentcontroller.test.utils.User;

//import test.utils.User;

/**
 * Created by mac on 2017/3/25.
 */

public class RosterAdapter extends BaseAdapter{
    private String isol;

    private List<User> list;
    private Context context;
    public RosterAdapter(List list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = (User) list.get(position);

        LayoutInflater mInflater = LayoutInflater.from(context);

        convertView = mInflater.inflate(R.layout.content_item, null);

        TextView isOnLine = (TextView) convertView.findViewById(R.id.isOnLine);
        //Logger.i("用户是否在线："+user.isAvailable());

        if (user.isAvailable()==true){
            isol="在线";
        }else{
            isol="离线";
        }
        isOnLine.setText(""+isol);
        TextView content  = (TextView) convertView.findViewById(R.id.content);
        String  name = user.getName();
        if(name.contains("@")){
            name = name.split("@")[0];
        }
        //Logger.i("用户的名字："+name);
        content.setText(name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.picture2);

        return convertView;
    }
}
