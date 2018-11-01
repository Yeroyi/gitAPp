package com.jbufa.firefighting.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.ui.CheckCodeCountDown;
import com.jbufa.firefighting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

public class ResetPasswordActivity extends BaseActivity {

    private EditText userPhone;
    private EditText phoneCode;
    private EditText password_edit;
    private Button resetPassword_btn;
    private CheckCodeCountDown mCheckCodeCountDown;
    private ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_resetpassword);
        userPhone = findViewById(R.id.userPhone);
        phoneCode = findViewById(R.id.phoneCode);
        back_btn = findViewById(R.id.back_btn);
        password_edit = findViewById(R.id.password_edit);
        resetPassword_btn = findViewById(R.id.resetPassword_btn);
        mCheckCodeCountDown = findViewById(R.id.btnGetCode);
//        public void resetPassword(String username, String code, String newPassword,
//                GizUserAccountType accountType)
        resetPassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = userPhone.getText().toString();
                if (userName.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "请填写手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                String code = phoneCode.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "请填写验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                String password = password_edit.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "请填写要修改的密码", Toast.LENGTH_LONG).show();
                    return;
                }


                findPassword();

            }
        });
        mCheckCodeCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean phoneNumber = Utils.isPhoneNumber(userPhone.getText().toString());
                mCheckCodeCountDown.performOnClick(phoneNumber);
            }
        });
        mCheckCodeCountDown.setOnSendCheckCodeListener(new CheckCodeCountDown.OnSendCheckCodeListener() {
            @Override
            public void sendCheckCode() {

                sendMessage();
            }
        });
        mCheckCodeCountDown.setOnFinishListener(new CheckCodeCountDown.OnFinishListener() {
            @Override
            public void OnFinish() {
//              TODO 计时结束后的操作
                mCheckCodeCountDown.setText("重新发送");
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void findPassword() {

        JSONObject jsonObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        jsonObject.put("data", dataObject);
        dataObject.put("message", phoneCode.getText().toString());
        dataObject.put("mobile", userPhone.getText().toString());
        dataObject.put("newPassword", password_edit.getText().toString());
        HttpClient.post(URL.FORGETPASSWORD, jsonObject, "", new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "找回密码成功");
                Toast.makeText(ResetPasswordActivity.this, "重置密码成功", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "找回密码失败 : " + e.getMessage());
                Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void sendMessage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", userPhone.getText().toString());
        HttpClient.post(URL.MESSAGECODE, jsonObject, "", new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "发生验证码成功");
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ceshi", "发生验证码失败 : " + e.getMessage());
            }
        });
    }

    @Override

    public void onWindowFocusChanged(boolean hasFocus) {

        // TODO Auto-generated method stub

        if (hasFocus) {

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
