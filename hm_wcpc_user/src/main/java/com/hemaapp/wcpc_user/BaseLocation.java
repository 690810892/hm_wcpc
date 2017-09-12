package com.hemaapp.wcpc_user;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hemaapp.wcpc_user.view.LocationUtils;

import xtom.frame.XtomObject;
import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/9.
 */
public class BaseLocation extends XtomObject {

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private static BaseLocation location;
    private hm_WcpcUserApplication application;

    private BaseLocation() {
        application = hm_WcpcUserApplication.getInstance();
        locationClient = new AMapLocationClient(application);
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

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

    public static BaseLocation getInstance() {
        if (location == null) {
            synchronized (BaseLocation.class) {
                if (location == null) {
                    location = new BaseLocation();
                }
            }
        }
        return location;
    }


    public void startLocation() {
//        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
        // 设置定位监听
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    Message msg = mHandler.obtainMessage();
                    msg.obj = loc;
                    msg.what = LocationUtils.MSG_LOCATION_FINISH;
                    mHandler.sendMessage(msg);
                }
            }
        });
        // 启动定位
        locationClient.startLocation();
    }

    public void stopLocation() {
        if (null != locationClient) {
            // 停止定位
            locationClient.stopLocation();
            mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_STOP);
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始定位
                case LocationUtils.MSG_LOCATION_START:
                    break;
                // 定位完成
                case LocationUtils.MSG_LOCATION_FINISH:
                    AMapLocation loc = (AMapLocation) msg.obj;
                    String result = LocationUtils.getLocationStr(loc);
                    log_i(result);
                    if (loc.getErrorCode() == 0) {//定位成功
                        XtomSharedPreferencesUtil.save(application, "lat", String.valueOf(loc.getLatitude()));
                        XtomSharedPreferencesUtil.save(application, "lng", String.valueOf(loc.getLongitude()));
                        XtomSharedPreferencesUtil.save(application, "address", loc.getAddress());
                        XtomSharedPreferencesUtil.save(application, "city", loc.getCity());
                        XtomSharedPreferencesUtil.save(application, "district_name", loc.getProvince() + loc.getCity() + loc.getDistrict());
                        XtomSharedPreferencesUtil.save(application, "district", loc.getProvince() + loc.getCity());
                    }
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    break;
            }
            stopLocation();
        }

        ;
    };
}

