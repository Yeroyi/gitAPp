package com.jbufa.firefighting.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jbufa.firefighting.Event.PushMessage;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends BaseActivity {

    private ImageButton back_btn;
    private TextView versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        back_btn = findViewById(R.id.back_btn);
        versionName = findViewById(R.id.versionName);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String verName = DeviceUtil.getVerName(this);
        versionName.setText("版本： " + verName);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }
}
