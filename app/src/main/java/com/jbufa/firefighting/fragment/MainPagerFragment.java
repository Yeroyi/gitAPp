package com.jbufa.firefighting.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jbufa.firefighting.Event.RefreshEvent;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.activity.GroupActivity;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.PlaceBean;
import com.jbufa.firefighting.ui.quickadapter.BaseAdapterHelper;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Request;

/**
 * 主界面  设备详情页
 */
public class MainPagerFragment extends BaseFragment {


    private ListView mListView;
    private boolean flag = true;
    private List<PlaceBean> placeBeans;
    private Gson gson;
    private QuickAdapter<PlaceBean> mplaceAdapter;
    private Handler mHandlermHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
//            loadProgressDialog.dismiss();
            // TODO Auto-generated method stub
            if (placeBeans != null && placeBeans.size() > 0) {
                mplaceAdapter.clear();
                mplaceAdapter.addAll(placeBeans);
                mplaceAdapter.notifyDataSetChanged();
            }
        }

    };
    //    private LoadProgressDialog loadProgressDialog;


    @Override
    public void onResume() {
        super.onResume();
        getGroup();
    }

    @Override
    protected int setView() {
        return R.layout.fragment_home_pager;
    }

    @Override
    protected void init(View view) {
        mListView = view.findViewById(R.id.bottomsheet_text);
    }


    @Override
    protected void initData(Bundle savedInstanceState) {

        setup();
        // 注册订阅者
        EventBus.getDefault().register(this);

//        getGroup();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        getGroup();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }


    private void setup() {
        gson = new Gson();
        String phone = (String) SharedPreferencesUtil.getParam(getActivity(), "phone", "");
        JPushInterface.resumePush(getActivity());
        registerJPush(phone);
        mplaceAdapter = new QuickAdapter<PlaceBean>(getContext(), R.layout.item_group_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, PlaceBean item) {
                helper.setText(R.id.tabName, item.getPlaceName());
                Log.e("ceshi", "主页面分组列表：" + item.getPlaceName());
            }
        };

        mListView.setAdapter(mplaceAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlaceBean groupBean = placeBeans.get(i);
                Intent intent = new Intent(getActivity(), GroupActivity.class);
                intent.putExtra("groupName", groupBean.getPlaceName());
                intent.putExtra("groupID", groupBean.getPlaceId());
                intent.putExtra("location", groupBean.getLocation());
                intent.putExtra("latitude", groupBean.getLatitude());
                intent.putExtra("longitude", groupBean.getLongitude());
                startActivity(intent);

            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> permissions) {
                            Log.e("ceshi", "申请权限成功");
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(@NonNull List<String> permissions) {
                            Log.e("ceshi", "申请权失败");
                            if (AndPermission.hasAlwaysDeniedPermission(getActivity(), permissions)) {
                                //TODO 这里是，始终拒绝
//                            showSettingDialog(MainActivity.this, permissions);
                            }
                        }
                    })
                    .start();

        }
    }


    private void registerJPush(String phoneNumber) {
        boolean isRegister = (boolean) SharedPreferencesUtil.getParam(getActivity(), "pushRegister", false);
        if (!isRegister) {
            JPushInterface.setAlias(getActivity(), phoneNumber, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    if (i == 0) {
                        SharedPreferencesUtil.setParam(getActivity(), "pushRegister", true);
                        Log.e("ceshi", "设置别称成功");
                    } else {
                        Log.e("ceshi", "设置别称失败");
                    }
                }
            });
        }
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
                    Log.e("ceshi", "获取场景成功");

                    placeBeans = gson.fromJson(response, new TypeToken<List<PlaceBean>>() {
                    }.getType());
                    mplaceAdapter.clear();
                    mplaceAdapter.addAll(placeBeans);
                    mplaceAdapter.notifyDataSetChanged();
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
}
