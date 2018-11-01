package com.jbufa.firefighting.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jbufa.firefighting.Adpater.MessageAdpater;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.MessageBean;
import com.jbufa.firefighting.model.MessageResponse;
import com.jbufa.firefighting.model.PlaceBean;
import com.jbufa.firefighting.model.RomBean;
import com.jbufa.firefighting.ui.RecycleViewDivider;
import com.jbufa.firefighting.ui.quickadapter.BaseAdapterHelper;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.jbufa.firefighting.utils.FileUtil;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.kongzue.dialog.v2.DialogSettings;
import com.kongzue.dialog.v2.MessageDialog;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.List;

import butterknife.ButterKnife;
import cn.lemon.view.RefreshRecyclerView;
import cn.lemon.view.adapter.Action;
import okhttp3.Request;

import static com.kongzue.dialog.v2.DialogSettings.THEME_LIGHT;
import static com.kongzue.dialog.v2.DialogSettings.TYPE_KONGZUE;


public class MessageFragment extends BaseFragment {

    private Activity context;

    private boolean isLoadAll;

    private Gson gson;
    private List<MessageBean> deviceDatas;
    private RefreshRecyclerView mRecyclerView;
    private String token;
    private List<RomBean> roomBeans;
    private QuickAdapter<PlaceBean> mplaceAdapter;
    private List<PlaceBean> placeBeans;
    private View customView;
    private ListView diaglog_list;
    private MessageAdpater messageAdpater;
    private int page = 1;
    private MessageDialog messageDialog;

    @Override
    protected int setView() {
        return R.layout.recommend_shop_list;
    }

    @Override
    protected void init(View view) {
        mRecyclerView = view.findViewById(R.id.message_list);
        customView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter, null);
        diaglog_list = customView.findViewById(R.id.diaglog_list);
        CommonTitleBar titleBar = view.findViewById(R.id.title_linear);
        titleBar.getCenterTextView().setTextColor(Color.WHITE);
        titleBar.setBackgroundResource(R.drawable.shape_gradient);
        View rightCustomLayout = titleBar.getRightCustomView();
        rightCustomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroup();
                //                        messageAdpater.clear();
                messageDialog = MessageDialog.show(getActivity(), "选择场所", null, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCustomView(customView).setCanCancel(true);
            }
        });
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON
                        || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                }
            }
        });

        ButterKnife.bind(this, view);
        gson = new Gson();
        if (!((SharedPreferencesUtil.getParam(getActivity(), "token", "")).equals(""))) {
            token = (String) SharedPreferencesUtil.getParam(getActivity(), "token", "");

        }

        mplaceAdapter = new QuickAdapter<PlaceBean>(getContext(), R.layout.item_dialog_list) {
            @Override
            protected void convert(BaseAdapterHelper helper, PlaceBean item) {
                helper.setText(R.id.tabName, item.getPlaceName());
                Log.e("ceshi", "主页面分组列表：" + item.getPlaceName());
            }
        };
        diaglog_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PlaceBean placeBean = placeBeans.get(i);

                page = 1;
                String placeName = placeBean.getPlaceName();
                Log.e("ceshi", "筛选了：" + placeName);
                getMessage(placeName);
                messageDialog.doDismiss();
            }
        });
        diaglog_list.setAdapter(mplaceAdapter);

        messageAdpater = new MessageAdpater(getActivity());
        mRecyclerView.setSwipeRefreshColors(0xFF437845, 0xFFE44F98, 0xFF2FAC21);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(messageAdpater);


        mRecyclerView.addRefreshAction(new Action() {
            @Override
            public void onAction() {
                page = 1;
                getMessage("");
            }
        });

        mRecyclerView.setLoadMoreAction(new Action() {
            @Override
            public void onAction() {
                getMessage("");
                Log.e("ceshi", "加载更多");
            }
        });

        mRecyclerView.setLoadMoreErrorAction(new Action() {
            @Override
            public void onAction() {
                getMessage("");
            }
        });

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        initData();
    }


    private void getMessage(String placeName) {
        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        if (!placeName.equals("")) {
            datObject.put("placeName", placeName);
        }
        datObject.put("currentPage", page);
        datObject.put("pageSize", 20);
//        datObject.put("productKey", URL.PRODUCTKEY);
        jsonObject.put("data", datObject);

        HttpClient.post(URL.QUERYHISTORY, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onFailure(Request request, Exception e) {
                messageAdpater.clear();
                messageAdpater.notifyDataSetChanged();
                Log.e("ceshi", "获取消息日志：" + e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String response) {
//                String ifNotExist = FileUtil.createIfNotExist();
                MessageResponse messageBean = gson.fromJson(response, MessageResponse.class);
//                FileUtil.writeString(ifNotExist, messageBean.toString(),"utf-8");
                if (messageBean != null && messageBean.getRecords() != null) {
                    if (messageBean.getRecords().size() > 0) {
                        int pages = messageBean.getPages();
                        deviceDatas = messageBean.getRecords();
                        if (page == 1) {
                            messageAdpater.clear();
                        }
                        if (page < pages) {
                            messageAdpater.addAll(deviceDatas);
                            mRecyclerView.dismissSwipeRefresh();
                            page += 1;
                        } else {
                            mRecyclerView.showNoMore();
                        }
                        messageAdpater.notifyDataSetChanged();
                    }
                }
                Log.e("ceshi", "messageBean size: " + messageBean.getRecords().size());
            }
        });
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

                    placeBeans = gson.fromJson(response, new TypeToken<List<PlaceBean>>() {
                    }.getType());

                    if (placeBeans == null) {
                        Log.e("ceshi", "groupBeans 为null");
                    } else {
                        mplaceAdapter.clear();
                        mplaceAdapter.addAll(placeBeans);
                        mplaceAdapter.notifyDataSetChanged();
                    }
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


    void initData() {
        getMessage("");
    }


    @Override
    public void onResume() {
        super.onResume();
//        Picasso.with(context).resumeTag(context);
//        getMessage("");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            initData();
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
}