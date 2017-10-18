package com.rance.chatui.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.rance.chatui.DB.sp.SystemConfigSp;
import com.rance.chatui.R;
import com.rance.chatui.util.XmppTool;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.rance.chatui.ui.activity.LoginActivity.xmppconnection;

public class MeSettingActivity extends AppCompatActivity {

    @Bind(R.id.line_singout)
    LinearLayout linearLayoutSingOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_setting);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.line_singout)
    public void singOut(){

        AlertDialog.Builder normalDialog = new AlertDialog.Builder(MeSettingActivity.this);
        normalDialog.setIcon(R.drawable.picture1);
        normalDialog.setTitle("警告！");
        normalDialog.setMessage("你确定要退出吗?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SystemConfigSp.instance().removeConfig(SystemConfigSp.SysCfgDimension.USERNAEM);
                        SystemConfigSp.instance().removeConfig(SystemConfigSp.SysCfgDimension.USERPASSWORD);
                        XmppTool.closeConnection(xmppconnection);
                        Intent intent = new Intent(MeSettingActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(toString());
    }
}
