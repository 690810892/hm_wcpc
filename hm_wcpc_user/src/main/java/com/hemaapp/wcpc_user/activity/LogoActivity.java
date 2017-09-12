package com.hemaapp.wcpc_user.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.UpGrade;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.LocationUtils;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * 开机启动页
 */
public class LogoActivity extends BaseActivity implements AMapLocationListener {

    private View view;
    private ImageView imageview;
    private SysInitInfo infor; // 系统初始化
    private User user;

    private boolean isShowed;// 展示页是否看过，预留下来，现在默认为已经展示过了
    private boolean isAutomaticLogin = false;// 是否自动登录

    private UpGrade upGrade;
    private String lng, lat, district_name, address, city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_logo);
        super.onCreate(savedInstanceState);
        hm_WcpcUserApplication application = getApplicationContext();
        infor = application.getSysInitInfo();
        user = application.getUser();
        checkLocation();
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
                imageview.postDelayed(new Runnable() {
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

    /**
     * 启动开机动画
     */
    private void init() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo);
        animation.setAnimationListener(new StartAnimationListener());
        view.startAnimation(animation);
        startLocation();
    }

    // 检查是否已经展示过引导页界面了
    private boolean isShow() {
        isShowed = "true".equals(XtomSharedPreferencesUtil.get(mContext,
                "isShowed"));
        return isShowed;
    }

    // 检查是否自动登录
    private boolean isAutoLogin() {
        isAutomaticLogin = "true".equals(XtomSharedPreferencesUtil.get(
                mContext, "isAutoLogin"));
        return isAutomaticLogin;
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case INIT:
                HemaArrayResult<SysInitInfo> sResult = (HemaArrayResult<SysInitInfo>) baseResult;
                infor = sResult.getObjects().get(0);
                getApplicationContext().setSysInitInfo(infor);
                toAdvertisement();
                break;
            case CLIENT_LOGIN:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                getApplicationContext().setUser(user);
                if (!isNull(lng) && !isNull(lat)) {
                    getNetWorker().positionSave(user.getToken(), "1",
                            lng, lat);
                } else {
                    toMain();
                }
                break;
            case POSITION_SAVE:
                toMain();
                break;
        }
    }

    private void toAdvertisement() {
        imageview.setVisibility(View.VISIBLE);
        // 软件升级
        upGrade = new UpGrade(mContext) {

            @Override
            public void NoNeedUpdate() {
                checkLogin();
            }
        };
        String sysVersion = infor.getAndroid_last_version();
        String version = HemaUtil.getAppVersionForSever(mContext);
        if (HemaUtil.isNeedUpDate(version, sysVersion)) {
            upGrade.alert();
        } else {
            // 登录
            checkLogin();
        }
    }

    private void checkLogin(){
        // 判断信息引导页是否展示过了
        if (!isShow()) {
            Intent it = new Intent(mContext, ShowActivity.class);
            startActivity(it);
            finish();
        } else {
            // 判断是否自动登录
            if (isAutoLogin()) {
                String username = XtomSharedPreferencesUtil.get(this,
                        "username");
                String password = XtomSharedPreferencesUtil.get(this,
                        "password");
                if (!isNull(username) && !isNull(password)) {
                    BaseNetWorker netWorker = getNetWorker();
                    netWorker.clientLogin(username, password);
                } else {
                    toLogin();
                }
            } else {
                hm_WcpcUserApplication.getInstance().setUser(null);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "false");
                toMain();
            }
        }
    }

    private void toLogin() {
        Intent it = new Intent(mContext, LoginActivity.class);
        startActivity(it);
        finish();
        stopLocation();
    }

    private void toMain() {
        Intent it = new Intent(mContext, MainActivity.class);
        startActivity(it);
        finish();
        stopLocation();
    }

    private void stopLocation() {
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

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog(baseResult.getMsg());
                break;
            case CLIENT_LOGIN:
                hm_WcpcUserApplication.getInstance().setUser(null);
                XtomSharedPreferencesUtil.save(mContext, "isAutoLogin", "false");
                toMain();
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case INIT:
                showTextDialog("获取系统初始化信息失败啦\n请检查网络连接重试");
                break;
            case CLIENT_LOGIN:
                toMain();
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
        view = findViewById(R.id.view);
        imageview = (ImageView) findViewById(R.id.imageview);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
    }

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private void startLocation() {
        locationClient = new AMapLocationClient(mContext);
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
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
    }

    @Override
    public void onLocationChanged(AMapLocation loc) {
        if (null != loc) {
            Message msg = mHandler.obtainMessage();
            msg.obj = loc;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }
    }

    Handler mHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                //开始定位
                case LocationUtils.MSG_LOCATION_START:
                    log_i("正在定位...");
                    break;
                // 定位完成
                case LocationUtils.MSG_LOCATION_FINISH:
                    log_i("定位成功....");
                    AMapLocation loc = (AMapLocation) msg.obj;

                    lng = String.valueOf(loc.getLongitude());
                    lat = String.valueOf(loc.getLatitude());
                    district_name = loc.getProvince() + loc.getCity() + loc.getDistrict();
                    address = loc.getAddress();
                    city = loc.getCity();
                    XtomSharedPreferencesUtil.save(mContext, "lng", lng);
                    XtomSharedPreferencesUtil.save(mContext, "lat", lat);
                    XtomSharedPreferencesUtil.save(mContext, "district_name", district_name);
                    XtomSharedPreferencesUtil.save(mContext, "address", address);
                    XtomSharedPreferencesUtil.save(mContext, "district", loc.getProvince() + loc.getCity());
                    XtomSharedPreferencesUtil.save(mContext, "city", city);
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    log_i("定位停止...");
                    break;
            }
        }
    };

    private class StartAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            BaseNetWorker netWorker = getNetWorker();
            netWorker.init();
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
