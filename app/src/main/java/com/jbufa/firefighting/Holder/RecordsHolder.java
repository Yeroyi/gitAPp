package com.jbufa.firefighting.Holder;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbufa.firefighting.R;
import com.jbufa.firefighting.activity.HomeDevices;
import com.jbufa.firefighting.model.Records;

import cn.lemon.view.adapter.BaseViewHolder;

public class RecordsHolder extends BaseViewHolder<Records> {

    private TextView tabName;
    private TextView stuts;
    private TextView more;
    private ImageView wifiImage;
    private HomeDevices homeDevices;

    public RecordsHolder(ViewGroup parent, HomeDevices homeDevices) {
        super(parent, R.layout.item_device_list);
        this.homeDevices = homeDevices;
    }

    public RecordsHolder(ViewGroup parent) {
        super(parent, R.layout.item_device_list);
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        tabName = findViewById(R.id.tabName);
        stuts = findViewById(R.id.stuts);
        more = findViewById(R.id.more);
        wifiImage = findViewById(R.id.wifi);

    }

    @Override
    public void setData(final Records item) {
        super.setData(item);
        tabName.setText(item.getDeviceName());
        if (item.getRssi() != -1 && item.getRssi() < 11) {
            wifiImage.setImageResource(R.mipmap.wifi_bad);
        } else if (item.getRssi() != -1 && item.getRssi() > 11 || item.getRssi() < 20) {
            wifiImage.setImageResource(R.mipmap.wifi_soso);
        } else if (item.getRssi() != -1 && item.getRssi() > 20 || item.getRssi() < 31) {
            wifiImage.setImageResource(R.mipmap.wifi_good);
        }
        if (item.isFireAlarm()) {
            stuts.setText("报警");
            stuts.setTextColor(Color.RED);
        } else {
//                    if (item.getDetectorLossFault() != null && item.getDetectorLossFault().equals("true")) {
//                        helper.setText(R.id.stuts, "故障");
//                        helper.setTextColor(R.id.stuts, Color.parseColor("#FFD700"));
//                    } else

            if (item.getUndervoltageFault() != null && item.getUndervoltageFault().equals("true")) {
                stuts.setText("故障");
                stuts.setTextColor(Color.parseColor("#FFD700"));
            } else if (item.getPollutionFault() != null && item.getPollutionFault().equals("true")) {
                stuts.setText("故障");
                stuts.setTextColor(Color.parseColor("#FFD700"));
            } else if (item.getStaticPointFault() != null && item.getStaticPointFault().equals("true")) {
                stuts.setText("故障");
                stuts.setTextColor(Color.parseColor("#FFD700"));
            } else {
                stuts.setText("正常");
                stuts.setTextColor(Color.GREEN);
            }
        }
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeDevices.startEditeDevice(item);
            }
        });

    }

    @Override
    public void onItemViewClick(Records data) {
        super.onItemViewClick(data);
        homeDevices.startDeviceInfo(data);
    }
}
