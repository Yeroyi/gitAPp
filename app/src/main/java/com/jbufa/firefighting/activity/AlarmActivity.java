package com.jbufa.firefighting.activity;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.LoginFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.share.ShareSearch;
import com.jbufa.firefighting.Event.PushMessage;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.ui.DrivingRouteOverlay;
import com.jbufa.firefighting.ui.WaveView;
import com.jbufa.firefighting.utils.SharedPreferencesUtil;
import com.jbufa.firefighting.utils.SoundPlayUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Lemon on 2018/6/21.
 */

public class AlarmActivity extends BaseActivity implements AMap.InfoWindowAdapter, AMap.OnMapClickListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, RouteSearch.OnRouteSearchListener, ShareSearch.OnShareSearchListener, AMapLocationListener {

    private TextView device_name;
    private String data;
    private ImageButton callPhone;
    private MapView mMapView;
    private AMap aMap;
    private LatLonPoint mStartPoint = new LatLonPoint(39.992934, 116.33756);//起点，39.942295,116.335891
    private LatLonPoint mEndPoint = new LatLonPoint(39.992998, 116.319009);//终点，39.995576,116.481288
    private RouteSearch mRouteSearch;
    private final int ROUTE_TYPE_DRIVE = 2;
    private DriveRouteResult mDriveRouteResult;
    private TextView locationText;
    private TextView timeText;
    private ShareSearch mShareSearch;
    private WebView mUrlView;
    private ImageButton share_route;
    private AMapLocation location;
    private LocationSource.OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private ImageButton callUser;
    private String number;
    private boolean isFirst = true;
    private Button fragmentbutton;
    private String deviceName;
    private RelativeLayout wave_bg;
    private WaveView waveImage;
    private AudioManager mAudioManager;
    private int currentVolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alarm);
        Intent intent = getIntent();

//        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        ImageButton button = findViewById(R.id.back_btn);
        device_name = findViewById(R.id.device_name);
        locationText = findViewById(R.id.locationText);
        callUser = findViewById(R.id.callUser);
        callUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(
                        Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            }
        });
        timeText = findViewById(R.id.happenTime);
        callPhone = findViewById(R.id.callPhone);
        fragmentbutton = findViewById(R.id.fragmentbutton);
        share_route = findViewById(R.id.share_Route);
        wave_bg = findViewById(R.id.wave_bg);
        waveImage = findViewById(R.id.waveImage);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diallPhone("119");
            }
        });
//        device_name.setText(deviceName);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setfromandtoMarker();
        mUrlView = findViewById(R.id.url_view);
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);//设置其为定位完成后的回调函数
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 200, 500};
        boolean shakeIsopen = (boolean) SharedPreferencesUtil.getParam(AlarmActivity.this, "shake_switch", true);
        if (shakeIsopen) {
            v.vibrate(pattern, 0);
        }
        //初始化音频管理器
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager != null) {
            //获取系统最大音量
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            // 获取设备当前音量
            currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 5, AudioManager.FLAG_PLAY_SOUND);
            Log.e("ceshi", "设备最大音量:" + maxVolume);
            Log.e("ceshi", "当前设备音量:" + currentVolume);
        }
        boolean alarmIsopen = (boolean) SharedPreferencesUtil.getParam(AlarmActivity.this, "alarm_switch", true);
        if (alarmIsopen) {
            SoundPlayUtils.playSoundByMedia(this, R.raw.alarm);
        }
        init();
        if (intent != null) {
            data = intent.getStringExtra("data");
            if (data != null && !data.isEmpty()) {
                Log.e("ceshi", "数据：" + data);
                JSONObject parse = (JSONObject) JSONObject.parse(data);
                String placeLocation = parse.getString("placeLocation");
                String latitude = parse.getString("latitude");
                String placeName = parse.getString("placeName");
                deviceName = parse.getString("deviceName");
                String roomName = parse.getString("roomName");
                String longitude = parse.getString("longitude");
                String happenTime = parse.getString("happenTime");
                String productName = parse.getString("productName");
                if (productName != null && productName.equals("燃气报警器")) {
                    fragmentbutton.setText("泄漏");
                } else {
                    fragmentbutton.setText("火警");
                }
                device_name.setText(deviceName);
                locationText.setText("地址：" + placeLocation + "-" + placeName + "-" + roomName);
                timeText.setText("时间：" + happenTime);
                mEndPoint.setLatitude(Double.valueOf(latitude));
                mEndPoint.setLongitude(Double.valueOf(longitude));
            }
        }
        share_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareRoute();
            }
        });
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        setUpMap();
        registerListener();
        mShareSearch = new ShareSearch(this.getApplicationContext());
        mShareSearch.setOnShareSearchListener(this);
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    /**
     * 驾车路径规划短串分享
     */
    private void shareRoute() {

        ShareSearch.ShareFromAndTo fromAndTo = new ShareSearch.ShareFromAndTo(mStartPoint, mEndPoint);
        ShareSearch.ShareDrivingRouteQuery query = new ShareSearch.ShareDrivingRouteQuery(fromAndTo,
                ShareSearch.DrivingDefault);

        mShareSearch.searchDrivingRouteShareUrlAsyn(query);
    }


    /**
     * 注册监听
     */
    private void registerListener() {
//        aMap.setLocationSource(this);// 设置定位监听
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);

    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start)));
        aMap.addMarker(new MarkerOptions()
                .position(convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end)));
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            Toast.makeText(this, "定位中，稍后再试...", Toast.LENGTH_SHORT).show();

            return;
        }
        if (mEndPoint == null) {
            Toast.makeText(this, "终点未设置", Toast.LENGTH_SHORT).show();
        }

        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }


    /**
     * 根据2个坐标返回一个矩形Bounds
     * 以此来智能缩放地图显示
     */
    public static LatLngBounds createBounds(Double latA, Double lngA, Double latB, Double lngB) {
        LatLng northeastLatLng;
        LatLng southwestLatLng;

        Double topLat, topLng;
        Double bottomLat, bottomLng;
        if (latA >= latB) {
            topLat = latA;
            bottomLat = latB;
        } else {
            topLat = latB;
            bottomLat = latA;
        }
        if (lngA >= lngB) {
            topLng = lngA;
            bottomLng = lngB;
        } else {
            topLng = lngB;
            bottomLng = lngA;
        }
        northeastLatLng = new LatLng(topLat, topLng);
        southwestLatLng = new LatLng(bottomLat, bottomLng);
        return new LatLngBounds(southwestLatLng, northeastLatLng);
    }


    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 开启View闪烁效果
     */

    private void startFlick(View view) {

        if (null == view) {

            return;

        }

        Animation alphaAnimation = new AlphaAnimation(1, 0);

        alphaAnimation.setDuration(300);

        alphaAnimation.setInterpolator(new LinearInterpolator());

        alphaAnimation.setRepeatCount(Animation.INFINITE);

        alphaAnimation.setRepeatMode(Animation.REVERSE);

        view.startAnimation(alphaAnimation);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        StopVibrate();
        SoundPlayUtils.stopSound();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
        mMapView.onDestroy();
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_PLAY_SOUND);
        }
        PushMessage pushMessage = new PushMessage();
        pushMessage.setDeviceName(deviceName);
        EventBus.getDefault().post(pushMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    // 停止震动
    public void StopVibrate() {
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     */
    public void diallPhone(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + number);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                if (driveRouteResult.getPaths().size() > 0) {
                    mDriveRouteResult = driveRouteResult;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    if (drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();

                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();


                } else if (driveRouteResult != null && driveRouteResult.getPaths() == null) {
                    Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, "错误码" + i, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onPoiShareUrlSearched(String s, int i) {

    }

    @Override
    public void onLocationShareUrlSearched(String s, int i) {

    }

    @Override
    public void onNaviShareUrlSearched(String s, int i) {

    }

    @Override
    public void onBusRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onWalkRouteShareUrlSearched(String s, int i) {

    }

    @Override
    public void onDrivingRouteShareUrlSearched(String s, int i) {
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            mUrlView.loadUrl(s);
        } else {
            Toast.makeText(this, "错误码" + i, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
            if (isFirst) {
                mStartPoint.setLatitude(aMapLocation.getLatitude());
                mStartPoint.setLongitude(aMapLocation.getLongitude());
                searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
                LatLngBounds bounds = createBounds(mStartPoint.getLatitude(), mStartPoint.getLongitude(), mEndPoint.getLatitude(), mEndPoint.getLongitude());
                aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10), 1000L, null);
                isFirst = false;
                Log.e("ceshi", "aMapLocation:" + aMapLocation.getPoiName());
            }
        }
    }

    /**
     * 配置定位参数
     */
    private void setUpMap() {

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);


        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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
                number = usernumber.replace(" ", "");
                diallPhone(number);
                Log.e("ceshi", "number:" + number);
                Log.e("ceshi", "username:" + username);
            }
        }
    }
}
