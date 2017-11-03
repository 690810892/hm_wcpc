package com.hemaapp.wcpc_user.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetWorker;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.adapter.ShowAdapter;
import com.hemaapp.wcpc_user.module.User;

import xtom.frame.util.XtomSharedPreferencesUtil;

/**
 * Created by WangYuxia on 2016/5/3.
 */
public class ShowActivity extends BaseActivity {

    private ViewPager mViewPager;
    private RelativeLayout layout;
    private ImageView start;
    private ShowAdapter mAdapter;

    public boolean isAutomaticLogin = false;// 是否自动登录

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show);
        super.onCreate(savedInstanceState);
        String[] imgs = new String[] { "start_1.png", "start_2.png", "start_3.png", "start_4.png"};
        mAdapter = new ShowAdapter(mContext, imgs, layout);
        mViewPager.setAdapter(mAdapter);
    }

    // 检查是否自动登录
    private boolean isAutoLogin() {
        isAutomaticLogin = "true".equals(XtomSharedPreferencesUtil.get(
                mContext, "isAutoLogin"));
        return isAutomaticLogin;
    }

    @Override
    protected void onDestroy() {
        XtomSharedPreferencesUtil.save(mContext, "isShowed", "true"); // 将isShowed参数保存到XtomSharedPreferencesUtils里面
        super.onDestroy();
    }

    @Override
    public void finish() {
        //判断是否自动登录
        if(isAutoLogin()){
            String username = XtomSharedPreferencesUtil.get(this, "username");
            String password = XtomSharedPreferencesUtil.get(this, "password");
            if (!isNull(username) && !isNull(password)) {
                BaseNetWorker netWorker = getNetWorker();
                netWorker.clientLogin(username, password);
            }else {
                toLogin();
            }
        }else {
            toLogin();
        }
        super.finish();
    }

    private void toLogin(){
        Intent it = new Intent(mContext, LoginActivity.class);
        startActivity(it);
    }

    @Override
    protected void findView() {
        start = (ImageView) findViewById(R.id.imageview);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        layout = (RelativeLayout) findViewById(R.id.layout);
    }

    @Override
    protected void getExras() {

    }

    @Override
    protected boolean onKeyBack() {
        return false;
    }

    @Override
    protected boolean onKeyMenu() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void setListener() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                ViewGroup indicator = mAdapter.getmIndicator();
                if (indicator != null) {
                    RadioButton rbt = (RadioButton) indicator
                            .getChildAt(position);
                    if (rbt != null)
                        rbt.setChecked(true);
                }
                start.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            case CLIENT_LOGIN:
                HemaArrayResult<User> uResult = (HemaArrayResult<User>) baseResult;
                user = uResult.getObjects().get(0);
                getApplicationContext().setUser(user);
                toMain();
                break;
            default:
                break;
        }
    }

    private void toMain(){
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
            case CLIENT_LOGIN:
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
            case CLIENT_LOGIN:
                toMain();
                break;
            default:
                break;
        }
    }
}
