package com.rance.chatui.ui.mainview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rance.chatui.R;
import com.rance.chatui.ui.activity.ReadActivity;


public class DiscoverFragment extends Fragment{


    private LinearLayout linear_read;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_discovery,container,false);

        linear_read = (LinearLayout) view.findViewById(R.id.linear_read);
        linear_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReadActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

}
