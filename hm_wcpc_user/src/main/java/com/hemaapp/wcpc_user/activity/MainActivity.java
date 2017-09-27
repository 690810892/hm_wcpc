package com.hemaapp.wcpc_user.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.ToLogin;
import com.hemaapp.wcpc_user.adapter.TopAddViewPagerAdapter;
import com.hemaapp.wcpc_user.getui.PushUtils;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.AddListInfor;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.AutoChangeViewPager;

import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import xtom.frame.util.XtomToastUtil;

/**
 * Created by WangYuxia on 2016/4/29.
 * 首页
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ImageView image_left;
    private ImageView image_right;
    private ImageView image_point;
    private ImageView title_btn_feedback;

    private AutoChangeViewPager pager;
    private RelativeLayout layout_pager;

    private TextView tv_my_trips; //我的行程
    private TextView tv_publish;

    private User user;
    private ArrayList<AddListInfor> infors = new ArrayList<>();
    private TopAddViewPagerAdapter adapter;
    private int count;

    private long time;// 用于判断二次点击返回键的时间间隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        user = hm_WcpcUserApplication.getInstance().getUser();
        if(user == null){
            image_point.setVisibility(View.INVISIBLE);
        }
        ShareSDK.initSDK(this);
        startGeTuiPush();
        getAdvertiseList();
    }

    //获取广告位的列表
    private void getAdvertiseList(){
        getNetWorker().advertiseList();
    }

    @Override
    protected boolean onKeyBack() {
        if ((System.currentTimeMillis() - time) >= 2000) {
            XtomToastUtil.showShortToast(mContext, "再按一次返回键退出程序");
            time = System.currentTimeMillis();
        } else {
            finish();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        stopGeTuiPush();
        super.onDestroy();
    }

    private void initPage(){
        adapter = new TopAddViewPagerAdapter(mContext, infors,
                layout_pager);
        adapter.setInfors(infors);
        adapter.notifyDataSetChanged();
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new PageChangeListener(adapter));
        if(count == 0)
            image_point.setVisibility(View.INVISIBLE);
        else
            image_point.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = hm_WcpcUserApplication.getInstance().getUser();
        if(adapter != null){
            adapter.setInfors(infors);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                showProgressDialog("请稍后...");
                break;
            case NOTICE_UNREAD:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                cancelProgressDialog();
                break;
            case NOTICE_UNREAD:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                HemaPageArrayResult<AddListInfor> aResult = (HemaPageArrayResult<AddListInfor>) baseResult;
                infors = aResult.getObjects();
                user = hm_WcpcUserApplication.getInstance().getUser();
                if(user != null)
                    getNetWorker().noticeUnread(user.getToken(), "2", "1");
                else{
                    initPage();
                }
                break;
            case NOTICE_UNREAD:
                HemaArrayResult<String> cResult = (HemaArrayResult<String>) baseResult;
                count = Integer.parseInt(isNull(cResult.getObjects().get(0))?"0":cResult.getObjects().get(0));
                initPage();
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                showTextDialog("获取数据失败");
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ADVERTISE_LIST:
                showTextDialog("获取数据失败");
                break;
        }
    }


    @Override
    protected void findView() {
        image_left = (ImageView) findViewById(R.id.title_btn_left);
        image_right = (ImageView) findViewById(R.id.title_btn_right_image);
        image_point = (ImageView) findViewById(R.id.title_point);
        title_btn_feedback = (ImageView) findViewById(R.id.title_btn_feedback);

        pager = (AutoChangeViewPager) findViewById(R.id.viewpager);
        layout_pager = (RelativeLayout) findViewById(R.id.layout);
        tv_my_trips = (TextView) findViewById(R.id.tv_my_trip);
        tv_publish = (TextView) findViewById(R.id.tv_publish);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        image_left.setOnClickListener(this);
        image_right.setOnClickListener(this);
        tv_my_trips.setOnClickListener(this);
        tv_publish.setOnClickListener(this);
        title_btn_feedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        user = hm_WcpcUserApplication.getInstance().getUser();
        Intent it;
        switch (view.getId()){
            case R.id.title_btn_left:
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    it = new Intent(mContext, PersonCenterActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.title_btn_right_image:
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    it = new Intent(mContext, NoticeListActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.tv_my_trip:
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    it = new Intent(mContext, MyCurrentTripActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.tv_publish:
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    it = new Intent(mContext, PublishInforActivity.class);
                    startActivity(it);
                }
                break;
            case R.id.title_btn_feedback:
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    it = new Intent(mContext, FeedBackActivity.class);
                    startActivity(it);
                }
                break;
        }
    }

    // 广告位的数据适配器
    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        TopAddViewPagerAdapter mAdapter;

        public PageChangeListener(TopAddViewPagerAdapter mAdapter) {
            this.mAdapter = mAdapter;
        }

        @Override
        public void onPageSelected(int arg0) {
            if (mAdapter != null) {
                ViewGroup indicator = mAdapter.getIndicator();
                if (indicator != null) {
                    RadioButton rbt = (RadioButton) indicator.getChildAt(arg0);
                    if (rbt != null)
                        rbt.setChecked(true);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    /* 推送相关 */
    private PushReceiver pushReceiver;

    private void startGeTuiPush() {
        com.igexin.sdk.PushManager.getInstance().initialize(mContext);
        registerPushReceiver();
    }

    private void stopGeTuiPush() {
        unregisterPushReceiver();
    }

    private void registerPushReceiver() {
        if (pushReceiver == null) {
            pushReceiver = new PushReceiver();
            IntentFilter mFilter = new IntentFilter("com.hemaapp.push.connect");
            mFilter.addAction("com.hemaapp.push.msg");
            registerReceiver(pushReceiver, mFilter);
        }
    }

    private void unregisterPushReceiver() {
        if (pushReceiver != null)
            unregisterReceiver(pushReceiver);
    }

    private class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(intent);
        }

        private void handleEvent(Intent intent) {
            String action = intent.getAction();

            if ("com.hemaapp.push.connect".equals(action)) {
                saveDevice();

            } else if ("com.hemaapp.push.msg".equals(action)) {
                boolean unread = PushUtils.getmsgreadflag(
                        hm_WcpcUserApplication.getInstance(), "2");
                if (unread) {
                    log_i("有未读推送");
                } else {
                    log_i("无未读推送");
                }
            }
        }
    }

    public void saveDevice() {
        user = hm_WcpcUserApplication.getInstance().getUser();
        if(user == null){
            return;
        }
        getNetWorker().deviceSave(user.getToken(),
                PushUtils.getChannelId(mContext), "2",
                PushUtils.getChannelId(mContext));
    }

	/* 推送相关end */
}
