package com.jbufa.firefighting.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.v2.SelectDialog;

import okhttp3.Request;

public class EditeDevice extends BaseActivity {

    private TextView device_id;
    private EditText edit_name;
    private TextView device_group;
    private String token;

    private Button btn_ok;
    private ImageView back_btn;
    private Button btn_unbind;
    private int roomId;
    private String roomName;
    private String deviceMac;
    private String deviceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty())) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }
        setContentView(R.layout.activity_edtit_device);

        Intent intent = getIntent();

        device_id = findViewById(R.id.device_id);
        edit_name = findViewById(R.id.edit_name);
        btn_ok = findViewById(R.id.btn_ok);
        back_btn = findViewById(R.id.back_btn);
        btn_unbind = findViewById(R.id.btn_unbind);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDeviceName();

            }
        });
        if (intent != null) {


            deviceMac = intent.getStringExtra("deviceMac");
            deviceName = intent.getStringExtra("deviceName");
            roomName = intent.getStringExtra("roomName");
            roomId = intent.getIntExtra("roomID", -1);

            device_id.setText("设备IMEI：" + deviceMac);
            edit_name.setHint(deviceName);
        }

        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unBindeDevice();
            }
        });
    }

    private void unBindeDevice() {

        SelectDialog.show(this, "提示", "是否解绑设备", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!token.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject datObject = new JSONObject();
                    datObject.put("mac", deviceMac);
                    datObject.put("roomId", roomId);
                    datObject.put("roomName", roomName);
                    jsonObject.put("data", datObject);
                    HttpClient.post(URL.UNBINDDEVICE, jsonObject, token, new HttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(EditeDevice.this, "解绑设备成功", Toast.LENGTH_SHORT).show();
                            setResult(5,new Intent());
                            finish();
                        }

                        @Override
                        public void onFailure(Request request, Exception e) {
                            Toast.makeText(EditeDevice.this, "解绑设备失败", Toast.LENGTH_SHORT).show();
                            super.onFailure(request, e);
                        }
                    });
                }
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private void setDeviceName() {
        if (!token.isEmpty()) {
            if (!edit_name.getText().toString().trim().isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                JSONObject datObject = new JSONObject();
                datObject.put("deviceName", edit_name.getText().toString());
                datObject.put("mac", deviceMac);
                jsonObject.put("data", datObject);
                HttpClient.post(URL.EDITDEVICENAME, jsonObject, token, new HttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(EditeDevice.this, "修改名称成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        Toast.makeText(EditeDevice.this, "修改名称失败", Toast.LENGTH_SHORT).show();
                        super.onFailure(request, e);
                    }
                });
            } else {
                Toast.makeText(EditeDevice.this, "请填写要修改的名称", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditeDevice.this, "用户Token为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
