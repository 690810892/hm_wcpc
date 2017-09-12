package com.hemaapp.wcpc_driver.getui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;

import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

public class ServiceLocationGPS extends Service implements AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener{

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private hm_WcpcDriverApplication application;
    private static final long INTERVAL = 30 * 1000;//定位时间间隔

    private GeocodeSearch geocoderSearch;
    private LatLonPoint latLonPoint; //点击地点

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = hm_WcpcDriverApplication.getInstance();
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = new AMapLocationClientOption();
        locationOption.setNeedAddress(true);
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        //设置定位间隔,单位毫秒,默认为2000ms
        locationOption.setInterval(INTERVAL);
        locationClient.startLocation();

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, Service.START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        if (null != locationClient) {
            locationClient.stopLocation();
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            XtomSharedPreferencesUtil.save(application, "lng", String.valueOf(aMapLocation.getLongitude()));
            XtomSharedPreferencesUtil.save(application, "lat", String.valueOf(aMapLocation.getLatitude()));
            if(isNull(aMapLocation.getProvince()) && isNull(aMapLocation.getCity())){
                latLonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
            }else{
                XtomSharedPreferencesUtil.save(application, "city", aMapLocation.getCity());
                XtomSharedPreferencesUtil.save(application, "address", aMapLocation.getAddress());
                XtomSharedPreferencesUtil.save(application, "district_name", aMapLocation.getCity());
                XtomSharedPreferencesUtil.save(application, "district", aMapLocation.getProvince()+aMapLocation.getCity());
                Intent msgIntent = new Intent();
                msgIntent.setAction("com.hemaapp.push.location");
                // 发送 一个无序广播
                sendBroadcast(msgIntent);
            }
        }
    }

    private Boolean isNull(String value){
        if(value == null || "".equals(value) || "null".equals(value))
            return true;
        return false;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress address = result.getRegeocodeAddress();
                XtomSharedPreferencesUtil.save(application, "city", address.getCity());
                XtomSharedPreferencesUtil.save(application, "address", address.getFormatAddress());
                XtomSharedPreferencesUtil.save(application, "district_name", address.getCity());
                XtomSharedPreferencesUtil.save(application, "district", address.getProvince()+address.getCity());
                Intent msgIntent = new Intent();
                msgIntent.setAction("com.hemaapp.push.location");
                // 发送 一个无序广播
                sendBroadcast(msgIntent);
            } else {
                XtomToastUtil.showShortToast(application, "抱歉，没有找到符合的结果");
            }
        } else if (rCode == 27) {
            XtomToastUtil.showShortToast(application, "网络出现问题,请重新检查");
        } else if (rCode == 32) {
            XtomToastUtil.showShortToast(application, "应用key值,请重新检查");
        } else {
            XtomToastUtil.showShortToast(application, "出现其他类型的问题,请重新检查");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
    }
}
