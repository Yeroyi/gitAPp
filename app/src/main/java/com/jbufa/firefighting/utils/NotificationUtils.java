package com.jbufa.firefighting.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jbufa.firefighting.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by LaoZhao on 2017/11/19.
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager manager;
    public static final String id = "channel_1";
    public static final String name = "channel_name_1";
    public static int flag = 1;

    public NotificationUtils(Context context) {
        super(context);
    }

    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public Notification.Builder getChannelNotification(String title, String content, Context context) {
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle();
        bigTextStyle.setBigContentTitle(title)
                // 标题
                .bigText(content);
        //创建一个pendingIntent对象用于点击notification之后跳转
        Intent intent = new Intent();
        PendingIntent intent1 = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent1)
                .setAutoCancel(true)
                .setStyle(bigTextStyle);

        return builder;

    }

    public NotificationCompat.Builder getNotification_25(String title, String content, Context context) {


        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title)
                // 标题
                .bigText(content);
        Intent intent = new Intent();
        PendingIntent intent1 = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent1)
                .setAutoCancel(true)
                .setStyle(bigTextStyle);

        return builder;
    }

    public void sendNotification(String title, String content, Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (title, content, context).build();
            getManager().notify(flag, notification);
        } else {
            Notification notification = getNotification_25(title, content, context).build();
            getManager().notify(flag, notification);
        }
        flag += 1;
    }

}
