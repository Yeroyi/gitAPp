package com.jbufa.firefighting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;

import org.w3c.dom.Text;

import java.util.List;

public class ResultActivity extends BaseActivity {

    private EditText device_name;
    private TextView device_id;
    private String groupID;
    private String did;
    private String uid;
    private String token;

    private String mac;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        device_id = findViewById(R.id.device_id);
        TextView device_group = findViewById(R.id.device_group);

        if (!((String) SharedPreferencesUtil.getParam(this, "uid", "")).isEmpty() && !(((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty())) {

            uid = (String) SharedPreferencesUtil.getParam(this, "uid", "");
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String groupName = (String) extras.get("groupName");
//                if (device != null) {
//                    Log.e("ceshi", "getAlias： " + device.getAlias());
//                }
                groupID = (String) extras.get("groupID");
                mac = (String) extras.get("mac");
                did = (String) extras.get("did");
                Log.e("ceshi", "groupName： " + groupName);
                Log.e("ceshi", "mac： " + did);

                device_id.setText("设备mac：" + mac);

                device_group.setText("所属场所：" + groupName);
                device_name = findViewById(R.id.device_Name);
            }
        }

        Button device_add = findViewById(R.id.device_add);
        device_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("deviceDid",did);
//                if (device != null) {
//                    device.setCustomInfo("", device_name.getText().toString());
//                }
                addDeviceToGroup();
                setResult(3, intent);
                finish();
            }
        });
    }


    private void addDeviceToGroup() {

        Log.e("ceshi", "二维码要开始清场了");
//        String format = String.format(URL.GETDEVICELIST, groupID);
        Log.e("ceshi", "二维码要添加的 组 id：" + groupID);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(did);
        Log.e("ceshi", "二维码要添加的 组 设备did：" + did);
        jsonObject.put("dids", jsonArray);

//        HttpClient.post(format, null, jsonObject, token, new HttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                Log.e("ceshi", "添加到分组请求成功:" + response);
//
//            }
//        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return false;
    }
}
