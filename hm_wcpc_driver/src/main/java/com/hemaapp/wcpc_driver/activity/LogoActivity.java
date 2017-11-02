package com.hemaapp.wcpc_driver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetWorker;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.UpGrade;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;

import xtom.frame.util.XtomSharedPreferencesUtil;

public class LogoActivity extends BaseActivity {

    private View view;
    private ImageView imageview;
    private SysInitInfo infor; // 系统初始化
    private User user;

    private boolean isShowed;// 展示页是否看过，预留下来，现在默认为已经展示过了
    private boolean isAutomaticLogin = false;// 是否自动登录

    private UpGrade upGrade;
    private String lng, lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_logo);
        super.onCreate(savedInstanceState);
        hm_WcpcDriverApplication application = getApplicationContext();
        infor = application.getSysInitInfo();
        user = application.getUser();
        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
        init();
    }

    /**
     * 启动开机动画
     * */
    private void init() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.logo);
        animation.setAnimationListener(new StartAnimationListener());
        view.startAnimation(animation);
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
                XtomSharedPreferencesUtil.save(mContext, "loginflag", user.getLoginflag());
                if(!isNull(lng) && !isNull(lat)){
                    getNetWorker().positionSave(user.getToken(), "2",
                            lng, lat);
                }else{
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
                toLogin();
            }
        }
    }

    private void toLogin() {
        Intent it = new Intent(mContext, LoginActivity.class);
        startActivity(it);
        finish();
    }

    private void toMain() {
        Intent it = new Intent(mContext, MainNewActivity.class);
        startActivity(it);
        finish();
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
                toLogin();
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
                toLogin();
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

