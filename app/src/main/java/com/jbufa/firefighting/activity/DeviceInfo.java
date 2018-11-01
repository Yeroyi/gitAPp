package com.jbufa.firefighting.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.DeviceInfoBean;
import com.jbufa.firefighting.model.Records;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import okhttp3.Request;

public class DeviceInfo extends BaseActivity implements AMap.InfoWindowAdapter {
    private MapView mMapView;
    private AMap mAMap;
    private TextView device_imei;
    private TextView fireAlarm;
    private TextView rssi;
    private TextView selfChecking;
    private TextView undervoltageFault;
    private TextView locationText;
    private TextView statusTime;
    private String deviceName;
    private String deviceMac;
    private Gson gson;
    private TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        device_imei = findViewById(R.id.device_imei);
        fireAlarm = findViewById(R.id.device_FireAlarm);
        rssi = findViewById(R.id.device_rssi);
        selfChecking = findViewById(R.id.device_SelfChecking);
        undervoltageFault = findViewById(R.id.device_UndervoltageFault);
        locationText = findViewById(R.id.locationText);
        statusTime = findViewById(R.id.device_statusTime);
        temperature = findViewById(R.id.device_temperature);
        gson = new Gson();

        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(18)); //缩放比例
            //设置amap的属性
            UiSettings settings = mAMap.getUiSettings();
//        settings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            mAMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            settings.setZoomControlsEnabled(false);
            mAMap.setInfoWindowAdapter(this);
        }

        CommonTitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.getRightTextView().setText("刷新");
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    onBackPressed();
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON
                        || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    getDeviceInfo();
                }
            }
        });


        if (getIntent() != null) {
            Intent intent = getIntent();
            deviceName = intent.getStringExtra("deviceName");
            String location = intent.getStringExtra("location");
            deviceMac = intent.getStringExtra("deviceMac");
            double longitude = intent.getDoubleExtra("longitude", -1);
            double latitude = intent.getDoubleExtra("latitude", -1);
            String placeName = intent.getStringExtra("placeName");
            String roomName = intent.getStringExtra("roomName");
            Records record = (Records) intent.getSerializableExtra("record");
            titleBar.getCenterTextView().setText(deviceName);
            locationText.setText(location + placeName + roomName);

            device_imei.setText("IMEI: " + deviceMac);


            if (record.isFireAlarm()) {
                fireAlarm.setText("火警：是");
            } else {
                fireAlarm.setText("火警：否");
            }

            rssi.setText("信号强度:" + record.getRssi());
            if (record.getSelfChecking() != null) {
                if (record.getSelfChecking().equals("true")) {
                    selfChecking.setText("自检：是");
                } else {
                    selfChecking.setText("自检：否");
                }
            } else {
                selfChecking.setText("自检：无");
            }
            if (record.getUndervoltageFault() != null) {
                if (record.getUndervoltageFault().equals("true")) {
                    undervoltageFault.setText("欠压：是");
                } else {
                    undervoltageFault.setText("欠压：否");
                }
            } else {
                undervoltageFault.setText("欠压：无");
            }

            mAMap.clear();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point))
                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mAMap.moveCamera(cu);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceInfo();
        mMapView.onResume();
    }

    private void getDeviceInfo() {

        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        datObject.put("mac", deviceMac);
//        datObject.put("productKey", URL.PRODUCTKEY);
        jsonObject.put("data", datObject);
        HttpClient.post(URL.DEVCURRENTSTATUS, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                Log.e("ceshi", "成功");
                DeviceInfoBean deviceInfo = gson.fromJson(response, DeviceInfoBean.class);
                if (deviceInfo.getProductName() != null) {
                    if (deviceInfo.getProductName().equals("燃气报警器")) {
                        if (deviceInfo.getFireAlarm() != null) {
                            if (deviceInfo.getFireAlarm().equals("true")) {
                                fireAlarm.setText("燃气泄露警报：是");
                            } else {
                                fireAlarm.setText("燃气泄露警报：否");
                            }
                        } else {
                            fireAlarm.setText("燃气泄露警报：无");
                        }
                        if (deviceInfo.getRSSI() != null) {
                            rssi.setText("信号强度:" + deviceInfo.getRSSI());
                        } else {
                            rssi.setText("信号强度: 无");
                        }

                        if (deviceInfo.getAnalogQuantity() != null) {
                            selfChecking.setText("可燃气体浓度：" + deviceInfo.getAnalogQuantity());
                        } else {
                            selfChecking.setText("可燃气体浓度：无");
                        }
                        if (deviceInfo.getUndervoltageFault() != null) {
                            if (deviceInfo.getUndervoltageFault().equals("true")) {
                                undervoltageFault.setText("正常：否");
                            } else {
                                undervoltageFault.setText("正常：是");
                            }
                        } else {
                            undervoltageFault.setText("正常：是");
                        }
                        if (deviceInfo.getStatus_time() != null) {
                            statusTime.setText("最近上报时间：" + deviceInfo.getStatus_time());
                        } else {
                            statusTime.setText("最近上报时间：无");
                        }
                        temperature.setVisibility(View.GONE);
                    } else {
                        if (deviceInfo.getFireAlarm() != null) {
                            if (deviceInfo.getFireAlarm().equals("true")) {
                                fireAlarm.setText("火警：是");
                            } else {
                                fireAlarm.setText("火警：否");
                            }
                        } else {
                            fireAlarm.setText("火警：无");
                        }
                        if (deviceInfo.getRSSI() != null) {
                            rssi.setText("信号强度:" + deviceInfo.getRSSI());
                        } else {
                            rssi.setText("信号强度: 无");
                        }
                        if (deviceInfo.getSelfChecking() != null) {
                            if (deviceInfo.getSelfChecking().equals("true")) {
                                selfChecking.setText("自检：是");
                            } else {
                                selfChecking.setText("自检：否");
                            }
                        } else {
                            selfChecking.setText("自检：无");
                        }

                        if (deviceInfo.getUndervoltageFault() != null) {
                            if (deviceInfo.getUndervoltageFault().equals("true")) {
                                undervoltageFault.setText("欠压：是");
                            } else {
                                undervoltageFault.setText("欠压：否");
                            }
                        } else {
                            undervoltageFault.setText("欠压：无");
                        }
                        if (deviceInfo.getStatus_time() != null) {
                            statusTime.setText("最近上报时间：" + deviceInfo.getStatus_time());
                        } else {
                            statusTime.setText("最近上报时间：无");
                        }
                        if (deviceInfo.getTemperature() != null) {
                            temperature.setText("温度传感器:" + deviceInfo.getTemperature()+"℃");
                        } else {
                            temperature.setText("温度传感器: 无");
                        }
                    }
                }
                Log.e("ceshi", "deviceInfo:" + deviceInfo.getStatus_time());


                Toast.makeText(DeviceInfo.this, "刷新成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                Toast.makeText(DeviceInfo.this, "刷新失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    @Override
    public View getInfoWindow(Marker marker) {
        View infoWindow = getLayoutInflater().inflate(
                R.layout.maker_layout, null);

        render(marker, infoWindow);
        return infoWindow;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {

        ImageView imageView = view.findViewById(R.id.badge);
        imageView.setImageResource(R.mipmap.device_icon);

        String title = marker.getTitle();
        TextView titleUi = (view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);

        } else {
            titleUi.setText(deviceName);
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
