package com.rance.chatui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rance.chatui.R;

import org.jivesoftware.smackx.muc.Affiliate;

import java.util.List;



/**
 * Created by 罗国都 on 2017/3/23.
 */

public class MultiRoomDetailsAdapter extends BaseAdapter {
    private Context context;
    private List<Affiliate> list;

    public MultiRoomDetailsAdapter(Context context, List<Affiliate> list) {
        this.context = context;
        this.list = list;
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

        Affiliate affiliate =list.get(position);
        LayoutInflater mInflater = LayoutInflater.from(context);

        convertView = mInflater.inflate(R.layout.content_item, null);

        TextView isOnLine = (TextView) convertView.findViewById(R.id.isOnLine);
        isOnLine.setText(affiliate.getNick());
        TextView content  = (TextView) convertView.findViewById(R.id.content);
        content.setText(affiliate.getJid());
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.picture1);

        return convertView;
    }
}
