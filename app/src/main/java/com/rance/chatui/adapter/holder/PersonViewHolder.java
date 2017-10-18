package com.rance.chatui.adapter.holder;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.rance.chatui.R;
import com.rance.chatui.enity.Person;
import com.rance.chatui.util.Logger;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Mr.Jude on 2015/2/22.
 */
public class PersonViewHolder extends BaseViewHolder<Person> {
    private TextView mTv_name;
    private ImageView mImg_face;
    private TextView mTv_sign;
    private TextView mTv_time;


    public PersonViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_person);
        mTv_name = $(R.id.person_name);
        mTv_sign = $(R.id.person_sign);
        mImg_face = $(R.id.person_face);
        mTv_time = $(R.id.person_time);
        Logger.i("PersonViewHolder 执行了:"+mImg_face);
    }

    @Override
    public void setData(final Person person){
        Log.i("ViewHolder","position"+getDataPosition());
        mTv_name.setText(person.getName());
        mTv_sign.setText(person.getSign());
        mTv_time.setText(person.getTime());
        Glide.with(getContext())
                .load(person.getFace())
                .placeholder(R.drawable.picture2)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mImg_face);
    }
}
