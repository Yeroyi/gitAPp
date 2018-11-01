package com.jbufa.firefighting.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;

import okhttp3.Request;

public class ChangePasswordActivity extends BaseActivity {


    private Button resetPassword_btn;
    private EditText newPassword;
    private EditText oldPassword;
    private ImageButton back_btn;
    private String newpassword;
    private String oldpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        newPassword = findViewById(R.id.newPassword);
        back_btn = findViewById(R.id.back_btn);
        oldPassword = findViewById(R.id.oldPassword);
        resetPassword_btn = findViewById(R.id.resetPassword_btn);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        resetPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newpassword = newPassword.getText().toString();
                oldpassword = oldPassword.getText().toString();
                String password = (String) SharedPreferencesUtil.getParam(ChangePasswordActivity.this, "password", "");
                if (oldpassword.isEmpty() || newpassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newpassword.equals(oldpassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "新密码不能与旧密码相同", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (oldpassword.equals(password)) {
                    changePwd();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "旧密码不正确", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
    }

    private void changePwd() {

        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        datObject.put("newPassword", newpassword);
        datObject.put("oldPassword", oldpassword);
        jsonObject.put("data", datObject);

        HttpClient.post(URL.RESETPWD, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {

                Toast.makeText(ChangePasswordActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "修改失败");
                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
