package com.jbufa.firefighting.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class EditeRoom extends BaseActivity {

    private TextView device_id;
    private EditText edit_name;
    private TextView device_group;
    private String token;

    private String did;
    private Button btn_ok;
    private ImageView back_btn;
    private Button btn_unbind;
    private int placeID;
    private int roomId;
    private String roomName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty())) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }
        setContentView(R.layout.activity_edtit_room);

        Intent intent = getIntent();

        device_id = findViewById(R.id.device_id);
        edit_name = findViewById(R.id.edit_name);
        device_group = findViewById(R.id.device_group);
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
                setRoomName();

            }
        });
        if (intent != null) {


            roomId = intent.getIntExtra("roomId", -1);
            placeID = intent.getIntExtra("placeID", -1);
            String placeName = intent.getStringExtra("placeName");
            roomName = intent.getStringExtra("roomName");

            device_id.setText("房间ID：" + roomId);
            edit_name.setHint(roomName);
            device_group.setText("所属分组：" + placeName);
        }

        btn_unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRoom();
            }
        });
    }

    private void deleteRoom() {


        SelectDialog.show(this, "提示", "删除房间后，房间下的设备都会被删除", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!token.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject datObject = new JSONObject();
                    datObject.put("roomName", roomName);
                    datObject.put("placeId", placeID);
                    jsonObject.put("data", datObject);
                    HttpClient.post(URL.DELETEROOM, jsonObject, token, new HttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Toast.makeText(EditeRoom.this, "删除房间成功", Toast.LENGTH_SHORT).show();
                            setResult(7,new Intent());
                            finish();
                        }

                        @Override
                        public void onFailure(Request request, Exception e) {
                            Toast.makeText(EditeRoom.this, "删除房间失败", Toast.LENGTH_SHORT).show();
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

    private void setRoomName() {
        if (!token.isEmpty()) {
            if (!edit_name.getText().toString().trim().isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                JSONObject datObject = new JSONObject();
                datObject.put("roomName", roomName);
                datObject.put("placeId", placeID);
                datObject.put("roomId", roomId);
                datObject.put("newRoomName", edit_name.getText().toString().trim());
                jsonObject.put("data", datObject);
                HttpClient.post(URL.EDITROOM, jsonObject, token, new HttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(EditeRoom.this, "修改名称成功", Toast.LENGTH_SHORT).show();
                        setResult(7,new Intent());
                        finish();
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        Toast.makeText(EditeRoom.this, "修改名称失败", Toast.LENGTH_SHORT).show();
                        super.onFailure(request, e);
                    }
                });
            }else {
                Toast.makeText(EditeRoom.this, "请填写要修改的名称", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(EditeRoom.this, "用户Token为空", Toast.LENGTH_SHORT).show();
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
