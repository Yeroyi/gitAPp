package com.jbufa.firefighting.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.Event.FaultMessage;
import com.jbufa.firefighting.Event.PushMessage;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.activity.AlarmActivity;
import com.jbufa.firefighting.activity.FaultActivity;
import com.jbufa.firefighting.utils.NotificationUtils;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.Notification;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

import static com.kongzue.dialog.v2.DialogSettings.TYPE_MATERIAL;
import static com.kongzue.dialog.v2.Notification.TYPE_NORMAL;


public class MyPushMessageReceiver extends BroadcastReceiver {

    private static final String TAG = "ceshi";
    private Context mContext;
    private ArrayList<PushMessage> pushMessages = new ArrayList<>();
    private static ArrayList<String> deviceNames = new ArrayList<>();
    private static ArrayList<String> deviceFaultNames = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        EventBus.getDefault().register(this);
        try {
            Bundle bundle = intent.getExtras();
//            Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
//                Toast.makeText(context, bundle.getString(JPushInterface.EXTRA_MESSAGE), Toast.LENGTH_LONG).show();
                JSONObject parse = (JSONObject) JSONObject.parse(bundle.getString(JPushInterface.EXTRA_MESSAGE));
                String fireOrFaultName = parse.getString("fireOrFaultName");
                PushMessage pushMessageBean = new PushMessage();

                String placeName = parse.getString("placeName");
                String deviceName = parse.getString("deviceName");
                String roomName = parse.getString("roomName");
                String placeLocation = parse.getString("placeLocation");
                String productName = parse.getString("productName");
                if (fireOrFaultName.equals("FireAlarm")) {
                    if (productName != null && productName.equals("燃气报警器")) {
                        showNotification(context, "报警提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 疑似可燃气体泄漏");
                    } else {
                        showNotification(context, "火警提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 疑似发生火警");
                    }
                    boolean isStartActivity = costontData(deviceName);
                    if (isStartActivity) {
                        Intent alarmIntent = new Intent(context, AlarmActivity.class);
                        alarmIntent.putExtra("data", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(alarmIntent);
                    }
                } else {
                    if (fireOrFaultName.equals("UndervoltageFault")) {
                        if (productName.equals("燃气报警器")) {
                            showNotification(context, "故障提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 设备故障");
                        } else {
                            showNotification(context, "故障提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 欠压故障");
                        }
                        Intent alarmIntent = new Intent(context, FaultActivity.class);
                        alarmIntent.putExtra("data", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityJson(context, alarmIntent, bundle);
                    } else if (fireOrFaultName.equals("DetectorLossFault")) {
                        boolean isFault = costontFaultData(deviceName);
                        showNotification(context, "故障提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 丢失故障");
                        if (isFault) {
                            Intent alarmIntent = new Intent(context, FaultActivity.class);
                            alarmIntent.putExtra("data", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityJson(context, alarmIntent, bundle);
                        }
                    } else if (fireOrFaultName.equals("PollutionFault")) {
                        showNotification(context, "故障提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 污染故障");
                        Intent alarmIntent = new Intent(context, FaultActivity.class);
                        alarmIntent.putExtra("data", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityJson(context, alarmIntent, bundle);
                    }else if (fireOrFaultName.equals("StaticPointFault")) {
                        showNotification(context, "故障提醒", placeLocation + " - " + placeName + "\n" + roomName + " - " + deviceName + " 静态点故障");
                        Intent alarmIntent = new Intent(context, FaultActivity.class);
                        alarmIntent.putExtra("data", bundle.getString(JPushInterface.EXTRA_MESSAGE));
                        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityJson(context, alarmIntent, bundle);
                    }
                }

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            Log.e("ceshi", "错误信息:" + e.getMessage());
            Toast.makeText(context, "推送消息收到，解析遇到错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void startActivityJson(Context context, Intent intent, Bundle bundle) {
        context.startActivity(intent);
    }

    /**
     * 初始化数据
     *
     * @param
     */
    private boolean costontData(String name) {
        if (deviceNames.contains(name)) {
            return false;
        } else {
            deviceNames.add(name);
            return true;
        }
    }

    /**
     * 初始化数据
     *
     * @param
     */
    private boolean costontFaultData(String name) {
        if (deviceFaultNames.contains(name)) {
            return false;
        } else {
            deviceFaultNames.add(name);
            return true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void delteMessage(PushMessage message) {
        deviceNames.remove(message.getDeviceName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void delteFault(FaultMessage message) {
        deviceFaultNames.remove(message.getDeviceName());
    }

    private void showNotification(final Context context, String title, String content) {
        DialogSettings.type = TYPE_MATERIAL;
        Notification.show(context, 2, R.mipmap.jbvh, title, content, Notification.SHOW_TIME_LONG, TYPE_NORMAL)
                .setOnNotificationClickListener(new Notification.OnNotificationClickListener() {
                    @Override
                    public void OnClick(int id) {
                    }
                });
        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.sendNotification(title, content, context);
    }

    // 打印所有的 intent extra 数据
//    private static String printBundle(Bundle bundle) {
//        StringBuilder sb = new StringBuilder();
//        for (String key : bundle.keySet()) {
//            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//            }else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
//                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
//                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
//                    Log.i(TAG, "This message has no Extra data");
//                    continue;
//                }
//
//                try {
//                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//                    Iterator<String> it =  json.keys();
//
//                    while (it.hasNext()) {
//                        String myKey = it.next();
//                        sb.append("\nkey:" + key + ", value: [" +
//                                myKey + " - " +json.optString(myKey) + "]");
//                    }
//                } catch (JSONException e) {
//                    Log.e(TAG, "Get message extra JSON error!");
//                }
//
//            } else {
//                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
//            }
//        }
//        return sb.toString();
//    }

}
