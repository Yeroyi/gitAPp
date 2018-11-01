package com.jbufa.firefighting.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jbufa.firefighting.Contact.ContactsActivity;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.URL;
import com.jbufa.firefighting.http.HttpClient;
import com.jbufa.firefighting.http.HttpResponseHandler;
import com.jbufa.firefighting.model.CotactPerson;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.jbufa.firefighting.utils.Utils;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;

import okhttp3.Request;

public class AddGroupActivity extends BaseActivity implements AMap.OnMarkerClickListener,
        AMap.OnMapLoadedListener,
        AMap.OnMapClickListener,
        LocationSource,
        AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener,
        AMap.OnCameraChangeListener {
    private MapView mMapView;
    private AMap mAMap;
    private Marker mGPSMarker;             //定位位置显示
    private AMapLocation location;
    private LocationSource.OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //你编码对象
    private GeocodeSearch geocoderSearch;

    private MarkerOptions markOptions;
    private LatLng latLng;
    private String addressName;


    private ArrayList<CotactPerson> peoples;
    private EditText contact_name;
    private EditText contact_editext;
    private int placeID;
    private String token;
    private boolean isFrist;
    private int count = 0;

    private EditText area_text;
    private EditText addres_editext;
    private ImageButton back_btn;
    private ImageButton ok_btn;
    private EditText name_editText;
    private Button addContacts;
    private TextView contactSize_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (!((String) SharedPreferencesUtil.getParam(this, "token", "")).isEmpty()) {
            token = (String) SharedPreferencesUtil.getParam(this, "token", "");
        }


        setContentView(R.layout.activity_addgroup);
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        initMap(savedInstanceState);
        area_text = findViewById(R.id.area_text);
        addres_editext = findViewById(R.id.addres_editext);
        name_editText = findViewById(R.id.name_editText);
        addContacts = findViewById(R.id.addContacts);
        contactSize_txt = findViewById(R.id.contactSize);
        contact_name = findViewById(R.id.contact_name);
        contact_editext = findViewById(R.id.contact_editext);
        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AddGroupActivity.this, ContactsActivity.class), 1);
            }
        });
        CommonTitleBar titleBar = findViewById(R.id.titlebar);
        titleBar.getCenterTextView().setText("添加场所");
        titleBar.setListener(new CommonTitleBar.OnTitleBarListener() {
            @Override
            public void onClicked(View v, int action, String extra) {
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON
                        || action == CommonTitleBar.ACTION_LEFT_TEXT) {
                    onBackPressed();
                } else if (action == CommonTitleBar.ACTION_RIGHT_BUTTON
                        || action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                    submitGroup();
                }
            }
        });

        if (getIntent() != null) {
            Intent intent = getIntent();
            placeID = intent.getIntExtra("placeID", -1);
            isFrist = intent.getBooleanExtra("isFrist", false);
            Log.e("ceshi", "进入后获取场景的ID: " + placeID);
        }

    }

    private void submitGroup() {
        if (!token.isEmpty()) {
            if (mGPSMarker != null) {
                double longitude = mGPSMarker.getPosition().longitude;
                double latitude = mGPSMarker.getPosition().latitude;

                JSONObject jsonObject = new JSONObject();
                JSONObject data = new JSONObject();
                data.put("latitude", latitude);
                if (peoples == null || peoples.size() <= 0) {
                    if (contact_name.getText().toString().isEmpty()) {
                        Toast.makeText(this, "请填写联系人名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (contact_editext.getText().toString().isEmpty()) {
                        Toast.makeText(this, "请填写联系号码", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!contact_name.getText().toString().isEmpty() && !contact_editext.getText().toString().isEmpty()) {
                    if (Utils.isPhoneNumber(contact_editext.getText().toString())) {
                        data.put("linkManMobile", contact_editext.getText().toString());
                        data.put("linkManName", contact_name.getText().toString());
                    } else {
                        Toast.makeText(this, "手机号码格式有误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                final String placeName = name_editText.getText().toString();
                if (placeName.isEmpty()) {
                    Toast.makeText(this, "请填写场所名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (area_text.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请填写区域名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (addres_editext.getText().toString().isEmpty()) {
                    Toast.makeText(this, "请填写地址名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                data.put("location", area_text.getText().toString() + addres_editext.getText().toString());
                data.put("longitude", longitude);
                data.put("placeName", name_editText.getText().toString());
                jsonObject.put("data", data);
                HttpClient.post(URL.ADDPLACE, jsonObject, token, new HttpResponseHandler() {
                    @Override
                    public void onSuccess(String response) {
                        Log.e("ceshi", "创建场景成功");
                        JSONObject parse = (JSONObject) JSONObject.parse(response);
                        if (parse != null) {
                            int id = parse.getIntValue("placeId");
                            Log.e("ceshi", "解析场景ID：" + id);
                            if (peoples != null && peoples.size() > 0) {
                                uploadContacts(placeName, id);
                            } else {
//                            MessageEvent messageEvent = new MessageEvent();
                                Intent intent = new Intent();
                                if (placeID == -1 && isFrist) {
                                    Log.e("ceshi", "就一个联系人，并且是第一次创建场景");
                                    intent.putExtra("isFrist", true);
                                } else {
                                    Log.e("ceshi", "就一个联系人，并且不是第一次");
                                    intent.putExtra("isFrist", false);
                                }
                                Log.e("ceshi", "结束---");
                                setResult(6, intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(AddGroupActivity.this, "场景创建失败，无法获取场景ID", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Request request, Exception e) {
                        Toast.makeText(AddGroupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            Log.e("ceshi", "token为空");
        }
    }

    private void uploadContacts(String placeName, int placeId) {
        Log.e("ceshi", "开始上传联系人");
        if (peoples != null) {
            for (int i = 0; i < peoples.size(); i++) {
                CotactPerson cotactPerson = peoples.get(i);
                putContacts(cotactPerson, placeName, placeId);
            }
        }
    }

    /**
     * placeName	string	场地名称
     * placeId	String	场地id
     * linkManName	String	场地联系人名称
     * linkManMobile	String	场地联系人手机号
     *
     * @param cotactPerson
     */


    private void putContacts(CotactPerson cotactPerson, final String placeName, int placeId) {
        if (!token.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("placeName", placeName);
            data.put("placeId", placeId);
            data.put("linkManName", cotactPerson.getName());
            data.put("linkManMobile", cotactPerson.getPhone());
            jsonObject.put("data", data);
            HttpClient.post(URL.ADDLINKMAN, jsonObject, token, new HttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.e("ceshi", "添加联系人");
                    count++;
                    if (count == peoples.size()) {
                        Intent intent = new Intent();
                        if (placeID == -1 && isFrist) {
                            intent.putExtra("isFrist", true);
                        } else {
                            intent.putExtra("isFrist", false);
                        }
                        Log.e("ceshi", "多个联系人，结束-----");
                        setResult(6, intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Request request, Exception e) {
                    super.onFailure(request, e);
                }
            });
        }
    }


    private void initMap(Bundle savedInstanceState) {
        geocoderSearch = new GeocodeSearch(this);
        mAMap = mMapView.getMap();
        // 设置定位监听
        mAMap.setOnMapLoadedListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnMapClickListener(this);

        mAMap.setLocationSource(this);
        //设置地图拖动监听
        mAMap.setOnCameraChangeListener(this);
        // 绑定marker拖拽事件
//      mAMap.setOnMarkerDragListener(this);

        //逆编码监听事件
//              GeocodeSearch.OnGeocodeSearchListener,
        geocoderSearch.setOnGeocodeSearchListener(this);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.gps_point));// 设置小蓝点的图标
        //myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细
        myLocationStyle.anchor(0.5f, 0.7f);
        mAMap.setMyLocationStyle(myLocationStyle);

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17)); //缩放比例

        //添加一个圆
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.radius(20.0f);
        mAMap.addCircle(circleOptions);

        //设置amap的属性
        UiSettings settings = mAMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        settings.setZoomControlsEnabled(false);
        mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        location = aMapLocation;
        if (mListener != null && location != null) {
            if (location != null && location.getErrorCode() == 0) {
                mListener.onLocationChanged(location);// 显示系统箭头

                LatLng la = new LatLng(location.getLatitude(), location.getLongitude());

                setMarket(la, location.getCity(), location.getAddress());

                mLocationClient.stopLocation();
                //                this.location = location;
                // 显示导航按钮
                //                btnNav.setVisibility(View.VISIBLE);
            }
        } else {
//			Util.showToast(AttendanceViewMap.this, "定位失败");
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000 * 10);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG}, 0);
        }
        // aMapEx.onRegister();
    }

    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        // 销毁定位
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        //        if (aMapEx != null) {
        //            aMapEx.onUnregister();
        //        }
        mMapView.onDestroy();
    }

    private void setMarket(LatLng latLng, String title, String content) {
        if (mGPSMarker != null) {
            mGPSMarker.remove();
        }
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = (mMapView.getWidth()) / 2;
        int height = ((mMapView.getHeight()) / 2);
        markOptions = new MarkerOptions();
        markOptions.draggable(false);//设置Marker可拖动
        markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.gps_point))).anchor(0.5f, 0.7f);
        //设置一个角标
        mGPSMarker = mAMap.addMarker(markOptions);
        //设置marker在屏幕的像素坐标
        mGPSMarker.setPosition(latLng);
        mGPSMarker.setTitle(title);
        mGPSMarker.setSnippet(content);
        //设置像素坐标
        mGPSMarker.setPositionByPixels(width, height);
//        mMapView.invalidate();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        latLng = cameraPosition.target;
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;
        Log.e("latitude", latitude + "");
        Log.e("longitude", longitude + "");
        getAddress(latLng);

    }

    /**
     * 根据经纬度得到地址
     */
    public void getAddress(final LatLng latLonPoint) {
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(convertToLatLonPoint(latLonPoint), 1, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                addressName = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi

//                Log.e("ceshi", "逆地理编码回调  得到的地址：" + addressName);
//                for (AoiItem item : result.getRegeocodeAddress().getAois()) {
//                    Log.e("ceshi", "AOI 名字：" + item.getAoiName());
//                }
//                Log.e("ceshi", "逆地理编码回调  得到的District：" + result.getRegeocodeAddress().getDistrict());
//              mAddressEntityFirst = new AddressSearchTextEntity(addressName, addressName, true, convertToLatLonPoint(mFinalChoosePosition));
                setMarket(latLng, location.getCity(), addressName);
                if (result.getRegeocodeAddress().getAois() != null && result.getRegeocodeAddress().getAois().size() > 0) {
                    addres_editext.setText(result.getRegeocodeAddress().getAois().get(0).getAoiName());
                }
                area_text.setText(result.getRegeocodeAddress().getDistrict());
            }
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 0:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED && grantResults[1] ==
                        PackageManager.PERMISSION_GRANTED) {
                    //权限已授予
                } else {
                    //权限未授予
                    Toast.makeText(this, "请授予\"定位\"和\"读取联系人\"权限，在-设置-应用 里开启",
                            Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ceshi", "resultCode: " + resultCode);
        if (resultCode == 5) {
            if (data != null) {
                peoples = (ArrayList<CotactPerson>) data.getSerializableExtra("peoples");
                if (peoples != null) {
                    Log.e("ceshi", "联系人数量：" + peoples.size());
                    contactSize_txt.setText("已添加" + peoples.size() + "位联系人");
                }
            }
        }
    }
}