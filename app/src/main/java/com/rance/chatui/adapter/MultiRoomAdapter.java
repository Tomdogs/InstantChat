package com.rance.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rance.chatui.R;
import com.rance.chatui.enity.Room;

import java.util.List;

/**
 * Created by mac on 2017/3/29.
 */

public class MultiRoomAdapter extends BaseAdapter{

    private Context context;
    private List<Room> roomList;
    public MultiRoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }
    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Room room = roomList.get(position);

        LayoutInflater mInflater = LayoutInflater.from(context);

        convertView = mInflater.inflate(R.layout.content_item, null);

        TextView jid  = (TextView) convertView.findViewById(R.id.content);
        //Logger.i("群显示的jid："+room.getJid());
        String roomnickname = room.getJid();
        if(roomnickname.contains("@")){
            roomnickname = roomnickname.split("@")[0];
        }
        jid.setText(roomnickname);

        TextView name = (TextView) convertView.findViewById(R.id.isOnLine);
        //Logger.i("群显示的name："+room.getName());
        name.setText(room.getName());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.picture2);


        return convertView;
    }
}
