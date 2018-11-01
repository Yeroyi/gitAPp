package com.jbufa.firefighting.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.jbufa.firefighting.R;
import com.jbufa.firefighting.utils.SharedPreferences;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class SettingActivity extends BaseActivity {

    private Switch alarm_switch;
    private Switch shake_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //警报声
        alarm_switch = findViewById(R.id.alarm_switch);
        //震动声
        shake_switch = findViewById(R.id.shake_switch);
        boolean alarm_isOpen = (boolean) SharedPreferencesUtil.getParam(SettingActivity.this, "alarm_switch", true);
        if (alarm_isOpen) {
            alarm_switch.setChecked(true);
        }
        boolean shake_isOpen = (boolean) SharedPreferencesUtil.getParam(SettingActivity.this, "shake_switch", true);
        if (shake_isOpen) {
            shake_switch.setChecked(true);
        }
        alarm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtil.setParam(SettingActivity.this, "alarm_switch", b);
            }
        });
        shake_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesUtil.setParam(SettingActivity.this, "shake_switch", b);
            }
        });
        CommonTitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    finish();
                }
            }
        });
    }
}
