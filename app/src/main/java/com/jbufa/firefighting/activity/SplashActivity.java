package com.jbufa.firefighting.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.ui.UIHelper;
import com.jbufa.firefighting.utils.NotificationUtils;
import com.jbufa.firefighting.utils.SharedPreferences;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.squareup.picasso.Picasso;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Request;


public class SplashActivity extends BaseActivity {


    private ImageView splash_image;
    private int showTime = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_image = findViewById(R.id.splash_image);
        JPushInterface.stopPush(this);
        if (!((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty()) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        jsonObject.put("data", data);
        HttpClient.post(URL.APPAD, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                JSONObject parse = (JSONObject) JSONObject.parse(response);
                String picture = parse.getString("picture");
                Log.e("ceshi", "picture: " + picture);
                Picasso.with(SplashActivity.this).load(picture).fit().into(splash_image);
                showTime = parse.getIntValue("showTime");
                gotoLogin(showTime);

            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "启动页数据失败:" + e.getMessage());
                splash_image.setBackgroundResource(R.mipmap.splash);
                gotoLogin(showTime);
            }
        });
    }


    private void gotoLogin(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                UIHelper.showLogin(SplashActivity.this);
                finish();

            }
        }, time * 1000);
    }
}
