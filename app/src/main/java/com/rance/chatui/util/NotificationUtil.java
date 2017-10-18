package com.rance.chatui.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.rance.chatui.R;
import com.rance.chatui.ui.activity.MainActivity;

/**
 * Created by Tomdog on 2017/6/9.
 */

public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;

    public NotificationUtil(Context context){
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    /**
     * 普通的Notification
     */
    public void postNotification() {

        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);  //需要跳转指定的页面
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.imlogo);// 设置图标
        builder.setContentTitle("标题2");// 设置通知的标题
        builder.setContentText("内容2");// 设置通知的内容
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        builder.setAutoCancel(true); //自己维护通知的消失
        builder.setTicker("new message2");// 第一次提示消失的时候显示在通知栏上的
        builder.setOngoing(true);
        builder.setNumber(20);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_NO_CLEAR;  //只有全部清除时，Notification才会清除
        notification.flags = Notification.FLAG_AUTO_CANCEL; //如果是已经通知，自动消失

        notificationManager.notify(0,notification);
    }
}
