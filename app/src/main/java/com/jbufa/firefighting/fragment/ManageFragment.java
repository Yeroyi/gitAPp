package com.jbufa.firefighting.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jbufa.firefighting.Camera.ScanActivity;
import com.jbufa.firefighting.Event.RefreshEvent;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.activity.AddGroupActivity;
import com.jbufa.firefighting.activity.EditeRoom;
import com.jbufa.firefighting.activity.HomeDevices;
import com.jbufa.firefighting.activity.MainActivity;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.DeviceBean;
import com.jbufa.firefighting.model.GroupBean;
import com.jbufa.firefighting.model.MessageBean;
import com.jbufa.firefighting.model.PlaceBean;
import com.jbufa.firefighting.model.RomBean;
import com.jbufa.firefighting.ui.quickadapter.BaseAdapterHelper;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.jbufa.firefighting.ui.quickadapter.TabAdpater;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.MessageDialog;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;


public class ManageFragment extends BaseFragment {
    private MainActivity context;
    private int pno = 1;
    private boolean isLoadAll;
    private ListView tabList;
    private int lastPosition = -1;
    private View view;
    private QuickAdapter<GroupBean> groupBeanQuickAdapter;
    private ListView roomList;
    private List<PlaceBean> groupBeans;
    //    private List<DeviceBean> deviceBeans;
    private List<RomBean> roomBeans;
    private Gson gson;
    private QuickAdapter<DeviceBean> deviceBeanQuickAdapter;
    private QuickAdapter<RomBean> roomBeanQuickAdapter;
    private int clickFlag = 0;
    private TabAdpater tabAdpater;
    private boolean initTab = true;
    private int placeID = -1;
    private String deviceDid;
    private String placeName;
    private List<MessageBean> messageBeans = new ArrayList<>();
    private View customView;
    private ListView diaglog_list;
    private int postion;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (groupBeans != null && groupBeans.size() > 0) {
                        tabAdpater.addData(groupBeans);
                        tabAdpater.notifyDataSetChanged();
//                        loadProgressDialog.dismiss();
                        if (initTab) {
                            placeName = groupBeans.get(0).getPlaceName();
                            location = groupBeans.get(0).getLocation();
                            placeID = groupBeans.get(0).getPlaceId();
                            latitude = groupBeans.get(0).getLatitude();
                            longitude = groupBeans.get(0).getLongitude();
                            tabList.performItemClick(tabList.getChildAt(0), 0, tabList.getItemIdAtPosition(0));
                            initTab = false;
                        } else {
                            if (groupBeans.size() > 0) {
                                if (postion > groupBeans.size() - 1) {
                                    tabList.performItemClick(tabList.getChildAt(groupBeans.size() - 1), groupBeans.size() - 1, tabList.getItemIdAtPosition(groupBeans.size() - 1));
                                } else {
                                    getHome(placeID, false);
                                }
                            }
                        }
                    } else {
                        bindQuickAdapter.clear();
                        bindQuickAdapter.notifyDataSetChanged();
                        roomBeanQuickAdapter.clear();
                        roomBeanQuickAdapter.notifyDataSetChanged();
                        tabAdpater.clear();
                        placeID = -1;
                        initTab = true;
                    }
//                    loadProgressDialog.dismiss();
                    break;
                case 1:

                    break;
            }

        }
    };
    private InputDialog inputDialog;
    private String deviceMac;
    private MessageDialog bindDialog;
    private QuickAdapter<RomBean> bindQuickAdapter;
    private String location;
    private double latitude;
    private double longitude;

    //获取房间列表
    private void getHome(final int id, final boolean isShowDialog) {
        if (token != null && !token.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONObject datObject = new JSONObject();
            datObject.put("placeId", id);
            jsonObject.put("data", datObject);
            HttpClient.post(URL.ROOMLIST, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.e("ceshi", "获取房间列表成功");
                    roomBeans = gson.fromJson(response, new TypeToken<List<RomBean>>() {
                    }.getType());
                    Log.e("ceshi", "房间信息 : " + roomBeans.toString());
                    bindQuickAdapter.clear();
                    bindQuickAdapter.addAll(roomBeans);
                    roomBeanQuickAdapter.clear();
                    roomBeanQuickAdapter.addAll(roomBeans);
                    if (isShowDialog) {
                        showBindDialog();
                    }
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                }
            });
        }
    }

    @Override
    protected int setView() {
        return R.layout.fragment_demo_ptr;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {
        initData();
    }

    void initView(View view) {
        gson = new Gson();
        CommonTitleBar titleBar = view.findViewById(R.id.titlebar);
        titleBar.getCenterTextView().setTextColor(Color.WHITE);
        titleBar.setBackgroundResource(R.drawable.shape_gradient);
        View rightCustomLayout = titleBar.getRightCustomView();
        rightCustomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent(getContext(), ScanActivity.class);
                    intent.putExtra("placeName", placeName);
                    intent.putExtra("placeID", placeID);
                    startActivityForResult(intent, 1);
                }
            }
        });
        titleBar.getLeftCustomView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        ImageView addHome = view.findViewById(R.id.addHome);
        addHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPutHomeDialog(false);
            }
        });

        tabList = view.findViewById(R.id.tabList);
        roomList = view.findViewById(R.id.roomList);
        //跳转到房间编辑页
        roomBeanQuickAdapter = new QuickAdapter<RomBean>(getActivity(), R.layout.item_room) {
            @Override
            protected void convert(BaseAdapterHelper helper, final RomBean item) {
                helper.setText(R.id.roomName, item.getRoomName());
                helper.setOnClickListener(R.id.more, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditeRoom.class);
                        intent.putExtra("roomId", item.getRoomId());
                        intent.putExtra("deviceCount", item.getDeviceCount());
                        intent.putExtra("placeName", placeName);
                        intent.putExtra("roomName", item.getRoomName());
                        intent.putExtra("placeID", placeID);
                        startActivityForResult(intent, 2);
                    }
                });
            }
        };

        //选择绑定dialog弹窗
        bindQuickAdapter = new QuickAdapter<RomBean>(getActivity(), R.layout.item_tab_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, final RomBean item) {
                helper.setText(R.id.tabName, item.getRoomName());
            }
        };

        roomList.setAdapter(roomBeanQuickAdapter);
        //item跳转到房间列表页
        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), HomeDevices.class);
                intent.putExtra("roomName", roomBeans.get(i).getRoomName());
                intent.putExtra("roomID", roomBeans.get(i).getRoomId());
                intent.putExtra("location", location);
                intent.putExtra("longitude", longitude);
                intent.putExtra("placeName", placeName);
                intent.putExtra("latitude", latitude);
                startActivity(intent);
            }
        });


        tabAdpater = new TabAdpater(getActivity(), groupBeans, 0);
        tabList.setAdapter(tabAdpater);
        tabList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                roomList.setVisibility(View.INVISIBLE);
                PlaceBean groupBean = groupBeans.get(i);
                postion = i;
                tabAdpater.setSelectItem(postion);
                placeID = groupBean.getPlaceId();
                location = groupBean.getLocation();
                latitude = groupBean.getLatitude();
                longitude = groupBean.getLongitude();
                placeName = groupBean.getPlaceName();
                tabAdpater.notifyDataSetChanged();
                getHome(placeID, false);
                Log.e("ceshi", "点击分组后groupId值：" + placeID);
                Log.e("ceshi", "分组Name：" + groupBean.getPlaceName());
            }
        });

        customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter, null);
        diaglog_list = customView.findViewById(R.id.diaglog_list);
        diaglog_list.setAdapter(bindQuickAdapter);
        diaglog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RomBean romBean = roomBeans.get(i);
//                Log.e("ceshi", "要绑定了,deviceMac: " + deviceMac + " RoomID:" + romBean.getRoomId() +)
                bindQRDevice(deviceMac, romBean.getRoomId(), romBean.getRoomName());
            }
        });
    }

    private void showPutHomeDialog(final boolean isShowBind) {
        inputDialog = InputDialog.show(getActivity(), "房间名称", "", "确定", new InputDialogOkButtonClickListener() {
            @Override
            public void onClick(Dialog dialog, String inputText) {
                if (inputText.isEmpty()) {
                    Toast.makeText(getActivity(), "请输入房间名称", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (inputText.length() > 6) {
                        Toast.makeText(getActivity(), "房间名称最大6个字", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    putHome(inputText, isShowBind);
                }

            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
    }


    /**
     * 创建房间
     *
     * @param inputText
     * @param isShowBind
     */
    private void putHome(final String inputText, final boolean isShowBind) {

        if (token != null && !token.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONObject datObject = new JSONObject();
            datObject.put("roomName", inputText);
            datObject.put("placeId", placeID);
            jsonObject.put("data", datObject);
            HttpClient.post(URL.ADDHOME, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.e("ceshi", "创建房间成功");
                    if (inputDialog != null) {
                        inputDialog.doDismiss();
                    }
                    getHome(placeID, isShowBind);
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                    Log.e("ceshi", "创建房间失败");
                    if (inputDialog != null) {
                        inputDialog.doDismiss();
                    }
                }
            });
        } else {
            Toast.makeText(getActivity(), "用户Token为空", Toast.LENGTH_LONG).show();
        }
    }

    private void showBindDialog() {
        bindDialog = MessageDialog.show(getActivity(), "绑定房间", null, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setCustomView(customView).setCanCancel(true);
    }

    private void getGroup() {
        if (token != null && !token.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONObject datObject = new JSONObject();
            datObject.put("id", "0");
            datObject.put("placeName", "");
            jsonObject.put("data", datObject);
            HttpClient.post(URL.SELECTPLACES, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.e("ceshi", "获取场地成功");

                    groupBeans = gson.fromJson(response, new TypeToken<List<PlaceBean>>() {
                    }.getType());

                    if (groupBeans == null) {
                        Log.e("ceshi", "groupBeans 为null");
                    }
                    mHandler.sendEmptyMessage(0);
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                    Log.e("ceshi", "获取场景失败");
                }
            });
        } else {
            Toast.makeText(getActivity(), "用户Token为空", Toast.LENGTH_LONG).show();
        }

    }

    private void initData() {
        //获取分组
        new Thread(new Runnable() {
            @Override
            public void run() {
                getGroup();
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();

//        Picasso.with(context).resumeTag(context);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getGroup();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        Picasso.with(context).pauseTag(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Picasso.with(context).cancelTag(context);
    }


    private void bindQRDevice(String mac, int roomID, String roomName) {
        if (!((String) SharedPreferencesUtil.getParam(getActivity(), "token", "")).isEmpty()) {
            String token = (String) SharedPreferencesUtil.getParam(getActivity(), "token", "");
            JSONObject jsonObject = new JSONObject();
            JSONObject datObject = new JSONObject();
            datObject.put("roomName", roomName);
            datObject.put("mac", mac);
            datObject.put("roomId", roomID);
            jsonObject.put("data", datObject);
            HttpClient.post(URL.ROOMBINDDEVICE, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.e("ceshi", "绑定设备成功");
                    if (response.equals("设备已经绑定！")) {
                        Toast.makeText(getActivity(), "设备已绑定/或被其他房间绑定", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "绑定成功", Toast.LENGTH_SHORT).show();
                    }
                    bindDialog.doDismiss();
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                    Log.e("ceshi", "绑定设备失败");
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    bindDialog.doDismiss();
                }
            });

        } else {
            Toast.makeText(getActivity(), "用户Token为空", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 5) {
            Log.e("ceshi", "二维码扫描完毕，返回主界面");
            if (data != null && !data.getStringExtra("mac").equals("null")) {
                deviceMac = data.getStringExtra("mac");
                Log.e("ceshi", "二维码信息:" + deviceMac);
            }
            if (placeID == -1) {
                Log.e("ceshi", "用没有场景，需要创建场景");
                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                intent.putExtra("placeID", placeID);
                intent.putExtra("isFrist", true);
                startActivityForResult(intent, 1);
            } else {
                Log.e("ceshi", "用户没有房间，需要创建房间");
                if (roomBeans == null || roomBeans.size() == 0) {
                    showPutHomeDialog(true);
                } else {
                    getHome(placeID, true);
                }
            }
        }
        if (resultCode == 6) {
            Log.e("ceshi", "resultCode==6");
            boolean isFrist = data.getBooleanExtra("isFrist", false);
            if (isFrist) {
                Log.e("ceshi", "用户创建完毕场景，开始创建房间");
                //TODO 这里的接口，需要直接返回房间的ID，可以省略一些步骤
                showPutHomeDialog(true);
            } else {
//                Log.e("ceshi", "用户存在场景和房间，开始绑定");
//                getHome(placeID, true);
            }
        }
        RefreshEvent refreshEvent = new RefreshEvent();
        EventBus.getDefault().post(refreshEvent);
        getGroup();
    }
}