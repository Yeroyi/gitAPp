package com.jbufa.firefighting.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jbufa.firefighting.activity.AlarmActivity;
import com.jbufa.firefighting.activity.BaseActivity;
import com.jbufa.firefighting.activity.LoginActivity;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.v2.DialogSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.kongzue.dialog.v2.DialogSettings.THEME_LIGHT;
import static com.kongzue.dialog.v2.DialogSettings.TYPE_KONGZUE;


/**
 * Created by Lemon on 2018/6/22.
 */

public abstract class BaseFragment extends Fragment {

    protected BaseActivity mActivity;
    protected String token;

    protected abstract int setView();

    protected boolean isFirst = true;

    protected abstract void init(View view);


    protected abstract void initData(Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(setView(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        DialogSettings.use_blur = false;                 //设置是否启用模糊
        DialogSettings.type = TYPE_KONGZUE;
        DialogSettings.tip_theme = THEME_LIGHT;
        DialogSettings.dialog_theme = THEME_LIGHT;
    }


    protected void initUser() {
        if (!((String) SharedPreferencesUtil.getParam(getActivity(), "token", "")).isEmpty()) {
            token = (String) SharedPreferencesUtil.getParam(getActivity(), "token", "");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUser();
        initData(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
