package com.rance.chatui.ui.mainview;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rance.chatui.DB.sp.SystemConfigSp;
import com.rance.chatui.R;
import com.rance.chatui.ui.activity.MeSettingActivity;

public class MeFragment extends Fragment{
    LinearLayout linear;
    TextView tvAccount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(getActivity(), R.layout.fragment_four,null);
        linear = (LinearLayout) view.findViewById(R.id.setting);

        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MeSettingActivity.class));
            }
        });

        tvAccount = (TextView) view.findViewById(R.id.tvAccount);
        tvAccount.setText(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.USERNAEM));
        return view;
    }


}
