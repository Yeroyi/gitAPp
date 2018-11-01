package com.jbufa.firefighting.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jbufa.firefighting.Adpater.HomeDevicesAdapter;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.DeviceBean;
import com.jbufa.firefighting.model.GroupBean;
import com.jbufa.firefighting.model.Records;
import com.jbufa.firefighting.ui.RecycleViewDivider;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.List;

import cn.lemon.view.RefreshRecyclerView;
import cn.lemon.view.adapter.Action;
import okhttp3.Request;

public class HomeDevices extends BaseActivity {


    private ImageButton back_btn;
    private TextView groupName;
    private RefreshRecyclerView group_list;
    private Gson gson;
    private List<GroupBean> groupBeans;
    private QuickAdapter<GroupBean> mGroupAdapter;
    private DeviceBean deviceBeans;
    private int roomID;
    private QuickAdapter<Records> deviceBeanQuickAdapter;
    private String name;
    private List<Records> deviceList;
    //    private LoadProgressDialog loadProgressDialog;
    private String location;
    private double longitude;
    private double latitude;
    private String placeName;
    private String roomName;
    private int page = 1;
    private HomeDevicesAdapter homeDevicesAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homedevices);
        group_list = findViewById(R.id.group_list);
        gson = new Gson();
        CommonTitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    onBackPressed();
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON
                        || action == CommonTitleBar.ACTION_RIGHT_TEXT) {

                }
            }
        });
        group_list.setSwipeRefreshColors(0xFF437845, 0xFFE44F98, 0xFF2FAC21);
        group_list.setLayoutManager(new LinearLayoutManager(this));
        group_list.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL));
        homeDevicesAdapter = new HomeDevicesAdapter(this, this);
        group_list.setAdapter(homeDevicesAdapter);
        group_list.addRefreshAction(new Action() {
            @Override
            public void onAction() {
                page = 1;
                getDeviceList(roomID);
            }
        });

        group_list.setLoadMoreAction(new Action() {
            @Override
            public void onAction() {
                getDeviceList(roomID);
            }
        });

        group_list.setLoadMoreErrorAction(new Action() {
            @Override
            public void onAction() {
                getDeviceList(roomID);
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            roomID = intent.getIntExtra("roomID", -1);
            roomName = intent.getStringExtra("roomName");
            placeName = intent.getStringExtra("placeName");
            location = intent.getStringExtra("location");
            longitude = intent.getDoubleExtra("longitude", -1);
            latitude = intent.getDoubleExtra("latitude", -1);
            titleBar.getCenterTextView().setText(roomName);
        }
        getDeviceList(roomID);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        getDeviceList(roomID);
    }

    public void startDeviceInfo(Records records) {
        Intent intent = new Intent(HomeDevices.this, DeviceInfo.class);
        String deviceName = records.getDeviceName();
        String deviceMac = records.getDeviceMac();
        intent.putExtra("deviceMac", deviceMac);
        intent.putExtra("deviceName", deviceName);
        intent.putExtra("roomName", roomName);
        intent.putExtra("location", location);
        intent.putExtra("longitude", longitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.putExtra("placeName", placeName);
        intent.putExtra("record", records);
        startActivityForResult(intent, 1);
    }

    public void startEditeDevice(Records item) {
        Intent intent = new Intent(HomeDevices.this, EditeDevice.class);
        String deviceMac = item.getDeviceMac();
        String deviceName = item.getDeviceName();
        intent.putExtra("deviceMac", deviceMac);
        intent.putExtra("roomID", roomID);
        intent.putExtra("roomName", roomName);
        intent.putExtra("deviceName", deviceName);
        startActivityForResult(intent, 2);
    }


    private void getDeviceList(int id) {
        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        datObject.put("currentPage", page);
        datObject.put("pageSize", 20);
//        datObject.put("productKey", URL.PRODUCTKEY);
        datObject.put("roomId", id + "");
        jsonObject.put("data", datObject);
        HttpClient.post(URL.SHOWDEVICELIST, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.e("ceshi", "设备列表获取到了：" + response);
                deviceBeans = gson.fromJson(response, DeviceBean.class);

                if (deviceBeans != null) {
                    int pagesize = deviceBeans.getPages();
                    if (deviceBeans.getRecords() != null && deviceBeans.getRecords().size() > 0) {
                        deviceList = deviceBeans.getRecords();
                        if (page == 1) {
                            homeDevicesAdapter.clear();
                        }
                        if (page <= pagesize) {
                            homeDevicesAdapter.addAll(deviceList);
                            page += 1;
                        } else {
                            group_list.showNoMore();
                        }

                    } else {
                        group_list.showNoMore();
                    }
                    group_list.dismissSwipeRefresh();
                    homeDevicesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomeDevices.this, "该房间暂无设备", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                homeDevicesAdapter.clear();
                homeDevicesAdapter.notifyDataSetChanged();
                group_list.showNoMore();
                Toast.makeText(HomeDevices.this, "该房间暂无设备", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5) {
            page = 1;
            getDeviceList(roomID);
        }
    }

}
