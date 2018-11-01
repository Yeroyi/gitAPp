
package com.jbufa.firefighting.Holder;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jbufa.firefighting.R;
import com.jbufa.firefighting.model.MessageBean;

import cn.lemon.view.adapter.BaseViewHolder;

public class MessageHolder extends BaseViewHolder<MessageBean> {

    private TextView deviceName;
    private TextView location;
    private TextView reportingTime;
    private TextView deviceStatus;
    private ImageView device_logo;

    public MessageHolder(ViewGroup parent) {
        super(parent, R.layout.recommend_shop_list_item);
    }

    @Override
    public void setData(final MessageBean object) {
        super.setData(object);
        deviceName.setText("设备名称：" + object.getDeviceName());
        location.setText("位置：" + object.getLocation());
        reportingTime.setText("时间：" + object.getReportingTime());

        String dataPointKey = object.getDataPointKey();
        if (object.getProductName() != null) {
            if (object.getProductName().equals("燃气报警器")) {
                device_logo.setBackgroundResource(R.mipmap.keran);
            } else {
                device_logo.setBackgroundResource(R.mipmap.jbvh);
            }
        }
        if (dataPointKey != null) {
            if (dataPointKey.equals("SelfChecking")) {
                if (object.getDataPointValue().equals("true")) {
                    deviceStatus.setText("自检：是");
                } else {
                    deviceStatus.setText("自检：否");
                }
            } else if (dataPointKey.equals("FireAlarm")) {
                if (object.getProductName().equals("燃气报警器")) {
                    if (object.getDataPointValue().equals("true")) {
                        deviceStatus.setText("燃气泄漏：是");
                    } else {
                        deviceStatus.setText("燃气泄漏：否");
                    }
                } else {
                    if (object.getDataPointValue().equals("true")) {
                        deviceStatus.setText("火警：是");
                    } else {
                        deviceStatus.setText("火警：否");
                    }
                }
            } else if (dataPointKey.equals("DetectorLossFault")) {
                if (object.getDataPointValue().equals("true")) {
                    deviceStatus.setText("丢失故障：是");
                } else {
                    deviceStatus.setText("丢失故障：否");
                }
            } else if (dataPointKey.equals("UndervoltageFault")) {
                if (object.getProductName().equals("燃气报警器")) {
                    if (object.getDataPointValue().equals("true")) {
                        deviceStatus.setText("设备故障：是");
                    } else {
                        deviceStatus.setText("设备故障：否");
                    }
                } else {
                    if (object.getDataPointValue().equals("true")) {
                        deviceStatus.setText("欠压故障：是");
                    } else {
                        deviceStatus.setText("欠压故障：否");
                    }
                }
            } else if (dataPointKey.equals("PollutionFault")) {
                if (object.getDataPointValue().equals("true")) {
                    deviceStatus.setText("污染故障：是");
                } else {
                    deviceStatus.setText("污染故障：否");
                }
            } else if (dataPointKey.equals("StaticPointFault")) {
                if (object.getDataPointValue().equals("true")) {
                    deviceStatus.setText("静态点故障：是");
                } else {
                    deviceStatus.setText("静态点故障：否");
                }
            } else {
                deviceStatus.setText("状态：正常");
            }
        } else {
            deviceStatus.setText("状态：无");
        }
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        deviceName = findViewById(R.id.deviceName);
        location = findViewById(R.id.location);
        reportingTime = findViewById(R.id.reportingTime);
        deviceStatus = findViewById(R.id.deviceStatus);
        device_logo = findViewById(R.id.device_logo);
    }

    @Override
    public void onItemViewClick(MessageBean object) {
        super.onItemViewClick(object);
        //点击事件
        Log.i("CardRecordHolder", "onItemViewClick");
    }
}
