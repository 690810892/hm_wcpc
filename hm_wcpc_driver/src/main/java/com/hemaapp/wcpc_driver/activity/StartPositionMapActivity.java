package com.hemaapp.wcpc_driver.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.view.LocationUtils;

import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

/**
 * Created by WangYuxia on 2016/5/10.
 * flag = 0:市内行程，flag = 1：跨市行程
 */
public class StartPositionMapActivity
        extends BaseActivity implements LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener{

    private ImageView left;
    private TextView title;
    private TextView right;

    private MapView mapView;
    private AMap aMap;
    private LinearLayout layout_search;
    private TextView text_search;

    private OnLocationChangedListener mListener;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private String data;
    private GeocodeSearch geocoderSearch;
    private LatLonPoint latLonPoint; //点击地点

    private String lng, lat, my_address;
    private LatLng latlng;
    private boolean isFrist = true;
    private boolean isReturn;
    private String pos_lng, pos_lat, pos_address;
    private String city;
    private int flag, count = 0;
    private String citycode;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            XtomToastUtil.showLongToast(StartPositionMapActivity.this, data);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_startposition);
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        city = XtomSharedPreferencesUtil.get(mContext, "city");
        checkLocation();
    }

    private void init(){
        prepareLocation();
        if(isNull(lng)&& isNull(lat)){
            isFrist = true;
            initMap();
        }else{
            isFrist = false;
            if (aMap == null)
                aMap = mapView.getMap();
            latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
                    15);
            aMap.moveCamera(update);
            marker = aMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title(data)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.
                                    decodeResource(getResources(), R.mipmap.img_startposition_logo))));
            registerListener();
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
    }

    //检测是否有定位权限
    private void checkLocation() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {//判断是否拥有定位权限

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    3);
        } else {
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 3) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED)//未获得定位权限
            {
                showTextDialog("没有定位权限，请添加后重试");
                title.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            } else {
                init();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void prepareLocation(){
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置定位参数
        locationOption.setOnceLocation(true);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        String strInterval = "1000";
        if (!TextUtils.isEmpty(strInterval)) {
            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
            locationOption.setInterval(Long.valueOf(strInterval));
        }
        locationClient.setLocationOption(locationOption);
    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                //开始定位
                case LocationUtils.MSG_LOCATION_START:
                    break;
                // 定位完成
                case LocationUtils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    lng = String.valueOf(loc.getLongitude());
                    lat = String.valueOf(loc.getLatitude());
                    data = loc.getAddress();
                    if(count == 0){
                        my_address = loc.getProvince()+loc.getCity();
                        count ++;
                    }
                    latLonPoint = new LatLonPoint(loc.getLatitude(), loc.getLongitude());
                    citycode = loc.getCity();
                    if(isNull(citycode)){
                        citycode = loc.getProvince();
                    }
                    XtomSharedPreferencesUtil.save(mContext, "city", citycode);

                    if(isFrist){
                        latLonPoint = new LatLonPoint(Double.parseDouble(lat), Double.parseDouble(lng));
                        latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
                    }

                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
                            15);
                    aMap.moveCamera(update);
                    if(!isFrist){
                        if(marker != null)
                            marker.remove();
                        marker = aMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(data)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.
                                                decodeResource(getResources(), R.mipmap.img_startposition_logo))));
                    }else{
                        isFrist = false;
                    }
                    if(isReturn){
                        pos_lat = String.valueOf(loc.getLatitude());
                        pos_lng = String.valueOf(loc.getLongitude());
                        isReturn = false;
                    }
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    break;
            }
        };
    };

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            registerListener();
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
        }
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(this);
        aMap.setMyLocationEnabled(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
    }

    @Override
    protected void findView() {
        left = (ImageView) findViewById(R.id.title_btn_left);
        title = (TextView) findViewById(R.id.title_text);
        right = (TextView) findViewById(R.id.title_btn_right);
        mapView = (MapView) findViewById(R.id.bmapView);
        layout_search = (LinearLayout) findViewById(R.id.linearlayout);
        text_search = (TextView) findViewById(R.id.textview);
    }

    @Override
    protected void getExras() {
        lng = mIntent.getStringExtra("lng");
        lat = mIntent.getStringExtra("lat");
        isReturn = mIntent.getBooleanExtra("isReturn", false);
        flag = mIntent.getIntExtra("keytype", 0);
        data = mIntent.getStringExtra("address");
    }

    @Override
    protected void setListener() {
        title.setText("起点");
        right.setText("确定");
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latlng == null) {
                    XtomToastUtil.showShortToast(mContext, "请点击地图选择地点!");
                    return;
                }
                mIntent.putExtra("data", data);
                mIntent.putExtra("lng", String.valueOf(latlng.longitude));
                mIntent.putExtra("lat", String.valueOf(latlng.latitude));
                mIntent.putExtra("city", city);
                if(!isNull(my_address)){
                    mIntent.putExtra("my_address", my_address);
                }
                if(!isNull(pos_lng))
                    mIntent.putExtra("pos_lng", pos_lng);
                if(!isNull(pos_lat))
                    mIntent.putExtra("pos_lat", pos_lat);
                if(!isNull(pos_address))
                    mIntent.putExtra("pos_address", pos_address);
                setResult(RESULT_OK, mIntent);
                finish();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag == 0){ //市内行程
                    Intent it = new Intent(mContext, SelectStartPositionActivity.class);
                    if(isNull(citycode))
                        citycode = XtomSharedPreferencesUtil.get(mContext, "city");
                    it.putExtra("citycode", citycode);
                    startActivityForResult(it, R.id.linearlayout);
                }else{ //跨市行程
                    Intent it = new Intent(mContext, SelectEndPositionActivity.class);
                    if(isNull(citycode))
                        citycode = XtomSharedPreferencesUtil.get(mContext, "city");
                    it.putExtra("citycode", citycode);
                    startActivityForResult(it, R.id.linearlayout);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (mListener != null && location != null) {
            mListener.onLocationChanged(location);// 显示系统小蓝点
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度

            Message msg = mHandler.obtainMessage();
            msg.obj = location;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
        deactivate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case R.id.linearlayout:
                lat = String.valueOf(data.getDoubleExtra("lat", 0.0));
                lng = String.valueOf(data.getDoubleExtra("lng", 0.0));
                city = data.getStringExtra("city");
                String name = data.getStringExtra("name");
                text_search.setText(name);

                latLonPoint = new LatLonPoint(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0));

                latlng = new LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0));
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        startLocation();
    }

    private void startLocation(){
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    private Marker marker;

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress address = result.getRegeocodeAddress();
                if(flag == 0 && !isNull(city) && !address.getCity().equals(city)){
                    showTextDialog("抱歉，请选择本市内的地址");
                    return;
                }

                citycode = address.getCity();
                if(isNull(citycode)){
                    citycode = address.getProvince();
                }

                data = address.getFormatAddress();
                if(!isNull(pos_lat) && !isNull(pos_lng)){
                    pos_address = data;
                }
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                if(marker != null)
                    marker.remove();
                marker = aMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(data)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.
                                        decodeResource(getResources(), R.mipmap.img_startposition_logo))));
                Message msg = new Message();
                if (handler != null) {
                    handler.sendMessage(msg);
                }

            } else {
                XtomToastUtil.showShortToast(mContext, "抱歉，没有找到符合的结果");
            }
        } else if (rCode == 27) {
            XtomToastUtil.showShortToast(mContext, "网络出现问题,请重新检查");
        } else if (rCode == 32) {
            XtomToastUtil.showShortToast(mContext, "应用key值,请重新检查");
        } else {
            XtomToastUtil.showShortToast(mContext, "出现其他类型的问题,请重新检查");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onMapClick(LatLng latlng) {
        this.latlng = latlng;
        lng = String.valueOf(latlng.longitude);
        lat = String.valueOf(latlng.latitude);
        latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

}
