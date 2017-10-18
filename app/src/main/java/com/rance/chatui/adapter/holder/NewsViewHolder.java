package com.rance.chatui.adapter.holder;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.rance.chatui.R;
import com.rance.chatui.enity.News;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Tomdog on 2017/5/30.
 */

public class NewsViewHolder extends BaseViewHolder<News> {


    private TextView title;
    private ImageView image;
    private TextView source;
    private TextView time;



    public NewsViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_mews);
        image = $(R.id.image);
        title = $(R.id.title);
        source = $(R.id.source);
        time = $(R.id.time);

    }

    @Override
    public void setData(News data) {
        super.setData(data);
        title.setText(data.getLtitle());
        source.setText(data.getSource());
        time.setText(data.getPtime());
        Glide.with(getContext())
                .load(data.getImgsrc())
                .placeholder(R.drawable.picture2)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(image);
    }
}
