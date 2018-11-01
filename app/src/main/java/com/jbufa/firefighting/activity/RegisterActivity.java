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
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.jbufa.firefighting.utils.Utils;
import okhttp3.Request;


/**
 * Created by Lemon on 2018/6/22.
 */

public class RegisterActivity extends BaseActivity {

    private EditText userPhone;
    private EditText phonecode;
    private Button register_btn;
    private CheckCodeCountDown mCheckCodeCountDown;
    private String mPhoneNumber;
    private EditText password_edit;
    private String password;
    private String code;
    private ImageView back_btn;
    private EditText password_agin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registered);
        userPhone = findViewById(R.id.userPhone);
        phonecode = findViewById(R.id.phonecode);
        password_edit = findViewById(R.id.password_edit);
        back_btn = findViewById(R.id.back_btn);
        register_btn = findViewById(R.id.register_btn);
        password_agin = findViewById(R.id.password_agin_edit);
        mCheckCodeCountDown = findViewById(R.id.btnGetCode);

        mCheckCodeCountDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //必须调用 , 输入框中输入的是手机号 true , 否则 false ,这么做是为了防止不是手机号也进入倒计时
                mPhoneNumber = userPhone.getText().toString();
                if (mPhoneNumber.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                boolean phoneNumber = Utils.isPhoneNumber(mPhoneNumber);
                mCheckCodeCountDown.performOnClick(phoneNumber);
            }
        });
        mCheckCodeCountDown.setOnSendCheckCodeListener(new CheckCodeCountDown.OnSendCheckCodeListener() {
            @Override
            public void sendCheckCode() {
//               向手机发送验证码的逻辑
                mPhoneNumber = userPhone.getText().toString();
                sendMessage();

            }
        });
        mCheckCodeCountDown.setOnFinishListener(new CheckCodeCountDown.OnFinishListener() {
            @Override
            public void OnFinish() {
//              计时结束后的操作
                mCheckCodeCountDown.setText("重新发送");
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneNumber = userPhone.getText().toString();
                if (mPhoneNumber.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                password = password_edit.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写密码", Toast.LENGTH_LONG).show();
                    return;
                }
                String password_two = password_agin.getText().toString();
                if (password_two.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写密码", Toast.LENGTH_LONG).show();
                    return;
                } else if (!password_two.equals(password)) {
                    Toast.makeText(RegisterActivity.this, "两次输入密码内容不正确", Toast.LENGTH_LONG).show();
                    return;
                }
                code = phonecode.getText().toString();
                if (code.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "请填写验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                register();
                Log.e("ceshi", "number: " + mPhoneNumber + " password: " + password + " code: " + code);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void sendMessage() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", mPhoneNumber);
        HttpClient.post(URL.MESSAGE, jsonObject, "",new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "发生验证码成功");
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("ceshi", "发生验证码失败 : "+e.getMessage());
            }
        });
    }

    private void register() {
        JSONObject jsonObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        jsonObject.put("data",dataObject);
        dataObject.put("message", code);
        dataObject.put("mobile", mPhoneNumber);
        dataObject.put("password", password);
        HttpClient.post(URL.REGISTER, jsonObject,"", new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "注册成功");
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_LONG).show();
                SharedPreferencesUtil.clear(RegisterActivity.this);
                finish();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "注册失败 : "+e.getMessage());
                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
