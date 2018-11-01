package com.jbufa.firefighting.Camera;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;

import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;
import okhttp3.Request;

public class ScanActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("ceshi", getClass().getName() + ".onCreate()");
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String placeName = intent.getStringExtra("placeName");
        int placeID = intent.getIntExtra("placeID", -1);
        Log.e("ceshi", "获取到的场景ID和场景名称：" + placeName + "  ," + placeID);
        QrConfig qrConfig = new QrConfig.Builder()
                .setDesText("(将二维码放入框内，即可自动扫描)")//扫描框下文字
                .setShowDes(true)//是否显示扫描框下面文字
                .setShowLight(true)//显示手电筒按钮
                .setShowTitle(true)//显示Title
                .setShowAlbum(true)//显示从相册选择按钮
                .setCornerColor(Color.WHITE)//设置扫描框颜色
                .setLineColor(Color.WHITE)//设置扫描线颜色
                .setLineSpeed(QrConfig.LINE_MEDIUM)//设置扫描线速度
                .setScanType(QrConfig.TYPE_QRCODE)//设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
                .setScanViewType(QrConfig.SCANVIEW_TYPE_QRCODE)//设置扫描框类型（二维码还是条形码，默认为二维码）
                .setCustombarcodeformat(QrConfig.BARCODE_I25)//此项只有在扫码类型为TYPE_CUSTOM时才有效
                .setPlaySound(true)//是否扫描成功后bi~的声音
                .setIsOnlyCenter(true)//是否只识别框中内容(默认为全屏识别)
                .setTitleBackgroudColor(Color.BLUE)//设置状态栏颜色
                .setTitleTextColor(Color.BLACK)//设置Title文字颜色
                .create();
        QrManager.getInstance().init(qrConfig).startScan(ScanActivity.this, new QrManager.OnScanResultCallback() {
            @Override
            public void onScanSuccess(String result) {

                try {
                    String mac;
                    if (result.contains("&")) {
                        String[] split = result.split("&");
                        mac = split[1];
                        Log.e("ceshi", "旧版的二维码格式：" + mac);
                    } else {
                        String[] split = result.split("IMEI:");
                        mac = split[1];
                        Log.e("ceshi", "新版的二维格式：" + mac);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("mac", mac);
                    setResult(5, intent);
                    finish();
                } catch (Exception e) {
                    Log.e("ceshi", "二维码格式变了");
                    finish();
                }
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
