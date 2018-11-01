package com.jbufa.firefighting.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Toast;


import com.jbufa.firefighting.common.AppManager;
import com.jbufa.firefighting.model.UserTokenBean;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.MessageDialog;

import java.util.List;

import static com.kongzue.dialog.v2.DialogSettings.THEME_DARK;
import static com.kongzue.dialog.v2.DialogSettings.THEME_LIGHT;
import static com.kongzue.dialog.v2.DialogSettings.TYPE_KONGZUE;
import static com.kongzue.dialog.v2.DialogSettings.TYPE_MATERIAL;


public class BaseActivity extends AppCompatActivity {
    protected UserTokenBean userTokenBean;
    protected String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        initDatas();
    }

    private void initDatas() {
        String dataToken = (String) SharedPreferencesUtil.getParam(this, "token", "");
        Log.e("ceshi", "token 数据：" + dataToken);
        if (!((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty()) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }
        DialogSettings.use_blur = false;                 //设置是否启用模糊
        DialogSettings.type = TYPE_KONGZUE;
        DialogSettings.tip_theme = THEME_LIGHT;
        DialogSettings.dialog_theme = THEME_LIGHT;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity从堆栈中移除
//        AppManager.getAppManager().finishActivity(this);
    }


    //定义一个变量，来标识是否退出
    private static boolean isExit = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (this instanceof MainActivity) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                exit();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            //利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(0, 2000);
        } else {
            // 结束Activity从堆栈中移除
            AppManager.getAppManager().finishActivity(this);
//            finish();
            System.exit(0);
        }

    }

}
