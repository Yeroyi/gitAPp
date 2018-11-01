package com.jbufa.firefighting.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.UserTokenBean;
import com.jbufa.firefighting.ui.UIHelper;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.v2.TipDialog;
import com.kongzue.dialog.v2.WaitDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Request;

public class LoginActivity extends BaseActivity {
    private EditText phone;
    private EditText code;
    private Button btnSure;
    private String phoneNumber;
    private String pssword;
    private WaitDialog waitDialog;
    private Gson gson;
    private TextView text_fuwu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        gson = new Gson();
        String userPhone = (String) SharedPreferencesUtil.getParam(LoginActivity.this, "phone", "");
        String password = (String) SharedPreferencesUtil.getParam(LoginActivity.this, "password", "");
        Log.e("ceshi", "userPhone：" + userPhone);
        Log.e("ceshi", "password：" + password);
        phone = findViewById(R.id.phone);
        code = findViewById(R.id.code);
        text_fuwu = findViewById(R.id.text_fuwu);
        text_fuwu.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

//        String path = Environment.getExternalStorageDirectory().getPath()
//                + "/decodeImage.jpg";
//        BASE64Decoder base64Decoder = new BASE64Decoder();
//        try {
//            byte[] bytes = base64Decoder.decodeBuffer(content);
//            Log.e("ceshi", "bytes :" + bytes.length);
//            byte2image(bytes, path);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        phone.setText(userPhone);
        code.setText(password);
        btnSure = findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "手机号为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = phone.getText().toString();
                pssword = code.getText().toString();
                Log.e("ceshi", "执行登录");
                waitDialog = WaitDialog.show(LoginActivity.this, "登录中");
                loging();
//                UIHelper.showHome(LoginActivity.this);

            }
        });
        findViewById(R.id.userRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showRegister(LoginActivity.this);
            }
        });
        findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showResetPassWord(LoginActivity.this);
            }
        });

    }

    private void loging() {
        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        datObject.put("mobile", phoneNumber);
        datObject.put("password", pssword);
        jsonObject.put("data", datObject);

        HttpClient.post(URL.LOGIN, jsonObject, "", new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "登录成功");
                String phone = (String) SharedPreferencesUtil.getParam(LoginActivity.this, "phone", "");
                if (phone != null) {
                    if (!phone.isEmpty()) {
                        if (!phone.equals(phoneNumber)) {
                            SharedPreferencesUtil.clearKey(LoginActivity.this, "pushRegister");
                        }
                    }
                }
                SharedPreferencesUtil.setParam(LoginActivity.this, "phone", phoneNumber);
                SharedPreferencesUtil.setParam(LoginActivity.this, "password", pssword);
                userTokenBean = gson.fromJson(response, UserTokenBean.class);
                UserTokenBean.UserToken jwtAuthenticationDto = LoginActivity.this.userTokenBean.getJwtAuthenticationDto();
                if (jwtAuthenticationDto != null) {
                    SharedPreferencesUtil.setParam(LoginActivity.this, "token", jwtAuthenticationDto.getToken());
                }
                waitDialog.doDismiss();
                UIHelper.showHome(LoginActivity.this);
                finish();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "登录失败 : " + e.getMessage());
                TipDialog.show(LoginActivity.this, e.getMessage(), TipDialog.SHOW_TIME_LONG, TipDialog.TYPE_ERROR);
                waitDialog.doDismiss();
            }
        });

    }


    //byte数组到图片
    public void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            FileOutputStream imageOutput = new FileOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
            System.out.println("Make Picture success,Please find image in " + path);
        } catch (Exception ex) {
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                Log.e("ceshi", "不存在,创建文件夹");
                dir.mkdirs();
            }
            file = new File(filePath + fileName);
            Log.e("ceshi", "文件路径：" + file.getAbsolutePath());
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            Log.e("ceshi", "写入过程中发生错误");
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Log.e("ceshi", "写入完毕");
        }
    }
}
