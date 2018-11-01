package com.jbufa.firefighting.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.google.gson.reflect.TypeToken;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.CotactPerson;
import com.jbufa.firefighting.ui.quickadapter.BaseAdapterHelper;
import com.jbufa.firefighting.ui.quickadapter.QuickAdapter;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.jbufa.firefighting.utils.Utils;
import com.kongzue.dialog.v2.SelectDialog;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;
import com.yanzhenjie.permission.AndPermission;
import java.util.List;
import com.yanzhenjie.permission.Action;
import okhttp3.Request;


public class GroupActivity extends BaseActivity implements AMap.InfoWindowAdapter {


    private String token;
    private Gson gson;
    private int placeID;
    private String name;
    private MapView mMapView;
    private AMap mAMap;
    private String roomName;
    private List<CotactPerson> cotactPersonList;
    private QuickAdapter<CotactPerson> cotacAdapter;
    private ListView group_list;
    private EditText username;
    private EditText userPhone;
    private EditText place_edit;
    private Button modify_btn;
    private Button delte_btn;
    private String location;
    private ScrollView group_scroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        gson = new Gson();

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        TextView locationText = findViewById(R.id.locationText);
        group_list = findViewById(R.id.group_list);
        place_edit = findViewById(R.id.place_edit);
        group_scroll = findViewById(R.id.group_scroll);
        modify_btn = findViewById(R.id.modify_btn);
        delte_btn = findViewById(R.id.delte_btn);

        CommonTitleBar titleBar = findViewById(R.id.titlebar);
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(18)); //缩放比例
            //设置amap的属性
            UiSettings settings = mAMap.getUiSettings();
//        settings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            mAMap.setMyLocationEnabled(false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            settings.setZoomControlsEnabled(false);


        }

        if (!((SharedPreferencesUtil.getParam(this, "token", "")).equals(""))) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");

        }
        Intent intent = getIntent();
        if (intent != null) {
            placeID = intent.getIntExtra("groupID", -1);
            roomName = intent.getStringExtra("groupName");
            location = intent.getStringExtra("location");
            locationText.setText(location);
            double latitude = intent.getDoubleExtra("latitude", -1);
            double longitude = intent.getDoubleExtra("longitude", -1);
            titleBar.getCenterTextView().setText(roomName);
            place_edit.setHint(roomName);

            mAMap.clear();
            mAMap.setInfoWindowAdapter(this);
            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = mAMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point))
                    .anchor(0.5f, 0.5f));

            marker.showInfoWindow();

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, 18.0f);
            mAMap.moveCamera(cu);
        }
        titleBar.getRightTextView().setText("添加");
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    onBackPressed();
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON
                        || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    showAddContacts();
                }
            }
        });
        cotacAdapter = new QuickAdapter<CotactPerson>(this, R.layout.item_contact) {
            @Override
            protected void convert(BaseAdapterHelper helper, final CotactPerson item) {
                String name = item.getName();
                final String phone = item.getPhone();
                helper.setText(R.id.name, name);
                helper.setText(R.id.number, phone);
                helper.setOnClickListener(R.id.delete_user, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delteUser(phone, item);
                    }
                });
            }
        };
        group_list.setAdapter(cotacAdapter);
        group_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                group_scroll.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        modify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyPlace();

            }
        });
        delte_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltePlace();
            }
        });
    }

    private void deltePlace() {
        SelectDialog.show(this, "提示", "删除场地后，场地下的所有房间和设备都会被删除", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!token.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("placeId", placeID);
                    data.put("placeName", roomName);
                    jsonObject.put("data", data);
                    HttpClient.post(URL.PLACEDELETE, jsonObject, token, new HttpResponseHandler() {
                        @Override
                        public void onSuccess(String response) {
                            Log.e("ceshi", "删除场地成功");
                            finish();
//                    getPhoneNumbers(placeID);
                        }

                        @Override
                        public void onFailure(Request request, Exception e) {
                            super.onFailure(request, e);
                            Log.e("ceshi", "删除场地失败");
                            Toast.makeText(GroupActivity.this, "删除场地失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });


    }

    private void modifyPlace() {
        if (!token.isEmpty()) {
            if (!place_edit.getText().toString().isEmpty()) {
                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("location", location);
                data.put("placeId", placeID);
                data.put("placeName", place_edit.getText().toString());
                jsonObject.put("data", data);
                HttpClient.post(URL.PLACEEDIT, jsonObject, token, new HttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e("ceshi", "修改名称成功");
                        finish();
//                    getPhoneNumbers(placeID);
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        super.onFailure(request, e);
                        Log.e("ceshi", "修改名称失败");
                        Toast.makeText(GroupActivity.this, "修改名称失败", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(GroupActivity.this, "请填写要修改的名称", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddContacts() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.update_manage_dialog, null);
        ImageButton goto_cotact = v.findViewById(R.id.goto_cotact);
        goto_cotact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            }
        });
        username = v.findViewById(R.id.username);

        userPhone = v.findViewById(R.id.userPhone);


        Button btn_sure = (Button) v.findViewById(R.id.dialog_btn_sure);
        Button btn_cancel = (Button) v.findViewById(R.id.dialog_btn_cancel);
        //builer.setView(v);//这里如果使用builer.setView(v)，自定义布局只会覆盖title和button之间的那部分
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        dialog.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        btn_sure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(GroupActivity.this, "请填写姓名", Toast.LENGTH_SHORT).show();
                    return;
                } else if (userPhone.getText().toString().isEmpty()) {
                    Toast.makeText(GroupActivity.this, "请填写号码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!username.getText().toString().isEmpty() && !userPhone.getText().toString().isEmpty()) {
                    if (Utils.isPhoneNumber(userPhone.getText().toString())) {
                        if (cotactPersonList.size() < 5) {
                            CotactPerson cotactPerson = new CotactPerson();
                            String name = username.getText().toString();
                            String phone = userPhone.getText().toString();
                            putContacts(name, phone, roomName, placeID);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(GroupActivity.this, "最多只能有5个人联系人", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(GroupActivity.this, "号码格式有误", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });

    }

    private void putContacts(String username, String userPhone, final String placeName, int placeId) {
        if (!token.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("placeName", placeName);
            data.put("placeId", placeId);
            data.put("linkManName", username);
            data.put("linkManMobile", userPhone);
            jsonObject.put("data", data);
            HttpClient.post(URL.ADDLINKMAN, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.e("ceshi", "添加联系人");
                    getPhoneNumbers(placeID);
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                }
            });
        }
    }

    private void delteUser(String phone, final CotactPerson item) {
        if (cotactPersonList.size() > 1) {
            JSONObject jsonObject = new JSONObject();
            JSONObject datObject = new JSONObject();
            datObject.put("placeId", placeID);
            datObject.put("linkManMobile", phone);
            jsonObject.put("data", datObject);
            HttpClient.post(URL.DELETELINKMA, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    getPhoneNumbers(placeID);
                    Toast.makeText(GroupActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    super.onSuccess(response);
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    Toast.makeText(GroupActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    super.onFailure(request, e);
                }
            });
        } else {
            Toast.makeText(GroupActivity.this, "场景至少要有一个联系人", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        getPhoneNumbers(placeID);
    }

    private void getPhoneNumbers(int id) {
        JSONObject jsonObject = new JSONObject();
        JSONObject datObject = new JSONObject();
        datObject.put("placeId", id);
        datObject.put("placeName", roomName);
        jsonObject.put("data", datObject);
        HttpClient.post(URL.SHOWLINKMAN, jsonObject, token, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                cotactPersonList = gson.fromJson(response, new TypeToken<List<CotactPerson>>() {
                }.getType());
                cotacAdapter.clear();
                cotacAdapter.addAll(cotactPersonList);
                cotacAdapter.notifyDataSetChanged();
                Log.e("ceshi", "获取到联系人：" + response);
            }

            @Override
            public void onFailure(Request request, Exception e) {
                super.onFailure(request, e);
                Toast.makeText(GroupActivity.this, "获取联系人失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        // 销毁定位
        mMapView.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (Build.VERSION.SDK_INT >= 23) {
                AndPermission.with(this)
                        .runtime()
                        .permission(Manifest.permission.READ_CONTACTS ,Manifest.permission.READ_CALL_LOG)
                        .onGranted(new com.yanzhenjie.permission.Action<List<String>>() {

                            @Override
                            public void onAction(List<String> permissions) {
                                ContentResolver reContentResolverol = getContentResolver();
                                Uri contactData = data.getData();
                                @SuppressWarnings("deprecation")
                                Cursor cursor = managedQuery(contactData, null, null, null, null);
                                cursor.moveToFirst();
                                String username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                                Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                        null,
                                        null);
                                if (phone.moveToNext()) {
                                    String usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    String number = usernumber.replace(" ", "");
                                    GroupActivity.this.username.setText(username);
                                    userPhone.setText(number);
                                    Log.e("ceshi", "number:" + number);
                                    Log.e("ceshi", "username:" + username);

                                }
                            }
                        }).onDenied(new com.yanzhenjie.permission.Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(GroupActivity.this, "请授予读取联系人权限，在-设置-应用 里开启", Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        }
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
            titleUi.setText(roomName);
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
