package com.jbufa.firefighting.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.activity.AboutActivity;
import com.jbufa.firefighting.activity.ChangePasswordActivity;
import com.jbufa.firefighting.activity.HelperActivity;
import com.jbufa.firefighting.activity.MainActivity;
import com.jbufa.firefighting.activity.SettingActivity;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.ui.UIHelper;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.TipDialog;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Request;


public class MemberFragment extends Fragment {

    private Activity context;
    private View root;
    private String phone;
    private String token;
    private TextView userName;
    private ImageButton edit_userName;
    private InputDialog showInputName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return root = inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initView();
        initData();
    }

    void initView() {
        CommonTitleBar titleBar = root.findViewById(R.id.titlebar);
        titleBar.setBackgroundResource(R.drawable.shape_gradient);
        userName = root.findViewById(R.id.tv_user_name);
        edit_userName = root.findViewById(R.id.edit_userName);
        edit_userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputName = InputDialog.show(getActivity(), "用户名修改", "", new InputDialogOkButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog, String inputText) {
                        if (!inputText.trim().isEmpty()) {
                            if (inputText.length() < 6) {
                                setName(inputText);
                            } else {
                                Toast.makeText(getActivity(), "用户名过长", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "请填写用户名", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        TextView loginout_btn = root.findViewById(R.id.loginout_btn);
        loginout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity activity = (MainActivity) getActivity();
                UIHelper.showLogin(getActivity());
                JPushInterface.stopPush(getActivity());
                activity.finish();
                Log.e("ceshi", "退出登录");
            }
        });

        root.findViewById(R.id.textRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });
        root.findViewById(R.id.textCalculator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AboutActivity.class));
            }
        });
        root.findViewById(R.id.textHelper).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelperActivity.class));
            }
        });
        //设置
        root.findViewById(R.id.textSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });
        //清除缓存
        root.findViewById(R.id.textCleardata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.clearKey(getActivity(), "pushRegister");
                SharedPreferencesUtil.clearKey(getActivity(), "alarm_switch");
                SharedPreferencesUtil.clearKey(getActivity(), "shake_switch");
                TipDialog.show(getActivity(), "清除成功", TipDialog.SHOW_TIME_LONG, TipDialog.TYPE_FINISH);
            }
        });
    }

    private void setName(final String name) {
        JSONObject userNamejsonObject = new JSONObject();
        JSONObject userdata = new JSONObject();
        userdata.put("newUserName", name);
        userNamejsonObject.put("data", userdata);

        HttpClient.post(URL.UPDATEAPPUSERNAME, userNamejsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "修改用户名成功:" + response);
                showInputName.doDismiss();
                userName.setText(name);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "修改用户名失败:" + e.getMessage());
                showInputName.doDismiss();
            }
        });
    }

    private void initData() {
        phone = (String) SharedPreferencesUtil.getParam(getActivity(), "phone", "");
        if (!((String) SharedPreferencesUtil.getParam(getActivity(), "token", "")).isEmpty()) {
            token = (String) SharedPreferencesUtil.getParam(getActivity(), "token", "");
        }
        JSONObject userNamejsonObject = new JSONObject();
        JSONObject userdata = new JSONObject();
        userNamejsonObject.put("data", userdata);

        HttpClient.post(URL.APPUSERNAME, userNamejsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "用户名:" + response);
                userName.setText(response);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                Log.e("ceshi", "用户名失败:" + e.getMessage());
                userName.setText(phone);
            }
        });
    }
}