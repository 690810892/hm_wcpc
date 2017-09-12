package com.hemaapp.wcpc_user.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
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
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * Created by WangYuxia on 2016/4/29.
 */
public class MainActivity extends BaseActivity {

    private ImageView image_left;
    private ImageView image_right;
    private ImageView image_point;

    private LinearLayout layout_hezuche, layout_keyun, layout_hunqing, layout_huoyun;
    private TextView text_hezuche, text_keyun, text_hunqing, text_huoyun;
    private ImageView image_hezuche, image_keyun, image_hunqing, image_huoyun;

    private ArrayList<LinearLayout> layouts = new ArrayList<>();
    private ArrayList<TextView> texts = new ArrayList<>();
    private ArrayList<ImageView> images = new ArrayList<>();

    private FrameLayout content_hezuche;
    private RefreshLoadmoreLayout layout;
    private ProgressBar progressBar;
    private AutoChangeViewPager pager;
    private RelativeLayout layout_pager;
    private LinearLayout layout_cityin;
    private LinearLayout layout_cityout;
    private LinearLayout layout_bottom;
    private TextView text_cityin;
    private TextView text_cityout;

    private LinearLayout content_keyun;
    private LinearLayout content_hunqing;
    private LinearLayout content_huoyun;

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
        init();
        ShareSDK.initSDK(this);
        startGeTuiPush();
        getAdvertiseList();
    }

    //获取广告位的列表
    private void getAdvertiseList(){
        getNetWorker().advertiseList();
    }

    private void init(){
        layouts.add(0, layout_hezuche);
        layouts.add(1, layout_keyun);
        layouts.add(2, layout_hunqing);
        layouts.add(3, layout_huoyun);

        texts.add(0, text_hezuche);
        texts.add(1, text_keyun);
        texts.add(2, text_hunqing);
        texts.add(3, text_huoyun);

        images.add(0, image_hezuche);
        images.add(1, image_keyun);
        images.add(2, image_hunqing);
        images.add(3, image_huoyun);
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
                break;
            case NOTICE_UNREAD:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                progressBar.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                layout_bottom.setVisibility(View.VISIBLE);
                break;
            case NOTICE_UNREAD:
                cancelProgressDialog();
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
                    layout.refreshSuccess();
                    initPage();
                }
                break;
            case NOTICE_UNREAD:
                HemaArrayResult<String> cResult = (HemaArrayResult<String>) baseResult;
                count = Integer.parseInt(isNull(cResult.getObjects().get(0))?"0":cResult.getObjects().get(0));
                layout.refreshSuccess();
                initPage();
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information){
            case ADVERTISE_LIST:
                layout.refreshFailed();
                progressBar.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
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
                layout.refreshFailed();
                progressBar.setVisibility(View.INVISIBLE);
                layout.setVisibility(View.VISIBLE);
                showTextDialog("获取数据失败");
                break;
        }
    }


    @Override
    protected void findView() {
        image_left = (ImageView) findViewById(R.id.title_btn_left);
        image_right = (ImageView) findViewById(R.id.title_btn_right_image);
        image_point = (ImageView) findViewById(R.id.title_point);

        layout_hezuche = (LinearLayout) findViewById(R.id.layout_0);
        text_hezuche = (TextView) findViewById(R.id.textview_0);
        image_hezuche = (ImageView) findViewById(R.id.imageview_0);

        layout_keyun = (LinearLayout) findViewById(R.id.layout_1);
        text_keyun = (TextView) findViewById(R.id.textview_1);
        image_keyun = (ImageView) findViewById(R.id.imageview_1);

        layout_hunqing = (LinearLayout) findViewById(R.id.layout_2);
        text_hunqing = (TextView) findViewById(R.id.textview_2);
        image_hunqing = (ImageView) findViewById(R.id.imageview_2);

        layout_huoyun = (LinearLayout) findViewById(R.id.layout_9);
        text_huoyun = (TextView) findViewById(R.id.textview_9);
        image_huoyun = (ImageView) findViewById(R.id.imageview_9);

        content_hezuche = (FrameLayout) findViewById(R.id.layout_hezuche);
        layout = (RefreshLoadmoreLayout) findViewById(R.id.refreshLoadmoreLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        pager = (AutoChangeViewPager) findViewById(R.id.viewpager);
        layout_pager = (RelativeLayout) findViewById(R.id.layout);
        layout_cityin = (LinearLayout) findViewById(R.id.layout_3);
        layout_cityout = (LinearLayout) findViewById(R.id.layout_4);
        layout_bottom = (LinearLayout) findViewById(R.id.linearlayout);
        text_cityin = (TextView) findViewById(R.id.textview_3);
        text_cityout = (TextView) findViewById(R.id.textview_4);

        content_keyun = (LinearLayout) findViewById(R.id.layout_keyun);
        content_hunqing = (LinearLayout) findViewById(R.id.layout_hunqing);
        content_huoyun = (LinearLayout) findViewById(R.id.layout_huoyun);
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        image_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    Intent it = new Intent(mContext, PersonCenterActivity.class);
                    startActivity(it);
                }
            }
        });

        image_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null){
                    ToLogin.showLogin(mContext);
                }else{
                    Intent it = new Intent(mContext, NoticeListActivity.class);
                    startActivity(it);
                }
            }
        });

        layout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {
            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
                getAdvertiseList();
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout xtomRefreshLoadmoreLayout) {
            }
        });
        layout.setLoadmoreable(false);

        setListener(layout_hezuche);
        setListener(layout_keyun);
        setListener(layout_hunqing);
        setListener(layout_huoyun);
        setListener(layout_cityin);
        setListener(layout_cityout);
        setListener(text_cityin);
        setListener(text_cityout);
    }

    private void setListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it;
                switch (v.getId()){
                    case R.id.layout_0: //合租车
                        setBackGround(0);
                        content_hezuche.setVisibility(View.VISIBLE);
                        content_keyun.setVisibility(View.GONE);
                        content_hunqing.setVisibility(View.GONE);
                        content_huoyun.setVisibility(View.GONE);
                        break;
                    case R.id.layout_1:
                        setBackGround(1);
                        content_hezuche.setVisibility(View.GONE);
                        content_keyun.setVisibility(View.VISIBLE);
                        content_hunqing.setVisibility(View.GONE);
                        content_huoyun.setVisibility(View.GONE);
                        break;
                    case R.id.layout_2:
                        setBackGround(2);
                        content_hezuche.setVisibility(View.GONE);
                        content_keyun.setVisibility(View.GONE);
                        content_hunqing.setVisibility(View.VISIBLE);
                        content_huoyun.setVisibility(View.GONE);
                        break;
                    case R.id.layout_9:
                        setBackGround(3);
                        content_hezuche.setVisibility(View.GONE);
                        content_keyun.setVisibility(View.GONE);
                        content_hunqing.setVisibility(View.GONE);
                        content_huoyun.setVisibility(View.VISIBLE);
                        break;
                    case R.id.layout_3:
                        it = new Intent(mContext, CarOwerListActivity.class);
                        it.putExtra("type", "1");
                        startActivity(it);
                        break;
                    case R.id.layout_4:
                        it = new Intent(mContext, CarOwerListActivity.class);
                        it.putExtra("type", "2");
                        startActivity(it);
                        break;
                    case R.id.textview_3:
                        user = hm_WcpcUserApplication.getInstance().getUser();
                        if(user == null){
                            ToLogin.showLogin(mContext);
                        }else{
                            it = new Intent(mContext, PublishInforActivity.class);
                            it.putExtra("type", "1");
                            startActivity(it);
                        }
                        break;
                    case R.id.textview_4:
                        user = hm_WcpcUserApplication.getInstance().getUser();
                        if(user == null){
                            ToLogin.showLogin(mContext);
                        }else{
                            it = new Intent(mContext, PublishInforActivity.class);
                            it.putExtra("type", "2");
                            startActivity(it);
                        }
                        break;
                }
            }
        });
    }

    private void setBackGround(int type){
        for(int i = 0; i< layouts.size(); i++){
            if(i == type){
                texts.get(i).setTextColor(getResources().getColor(R.color.yellow));
                images.get(i).setVisibility(View.VISIBLE);
            }else{
                texts.get(i).setTextColor(getResources().getColor(R.color.qianhui));
                images.get(i).setVisibility(View.INVISIBLE);
            }
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
                        getApplicationContext(), "2");
                if (unread) {
                    log_i("有未读推送");
                } else {
                    log_i("无未读推送");
                }
            }
        }
    }

    public void saveDevice() {
        User user = getApplicationContext().getUser();
        if(user == null){
            return;
        }
        getNetWorker().deviceSave(user.getToken(),
                PushUtils.getChannelId(mContext), "2",
                PushUtils.getChannelId(mContext));
    }

	/* 推送相关end */
}
