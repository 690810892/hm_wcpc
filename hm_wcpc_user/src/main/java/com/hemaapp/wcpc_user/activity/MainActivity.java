//package com.hemaapp.wcpc_user.activity;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.view.ViewPager;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.hemaapp.hm_FrameWork.HemaNetTask;
//import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
//import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
//import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
//import com.hemaapp.wcpc_user.BaseActivity;
//import com.hemaapp.wcpc_user.BaseHttpInformation;
//import com.hemaapp.wcpc_user.BaseUtil;
//import com.hemaapp.wcpc_user.EventBusModel;
//import com.hemaapp.wcpc_user.R;
//import com.hemaapp.wcpc_user.ToLogin;
//import com.hemaapp.wcpc_user.adapter.TopAddViewPagerAdapter;
//import com.hemaapp.wcpc_user.getui.GeTuiIntentService;
//import com.hemaapp.wcpc_user.getui.PushUtils;
//import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
//import com.hemaapp.wcpc_user.module.AddListInfor;
//import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
//import com.hemaapp.wcpc_user.module.DistrictInfor;
//import com.hemaapp.wcpc_user.module.TimeRule;
//import com.hemaapp.wcpc_user.module.User;
//import com.hemaapp.wcpc_user.view.AutoChangeViewPager;
//import com.hemaapp.wcpc_user.view.ImageCarouselBanner;
//import com.hemaapp.wcpc_user.view.ImageCarouselHeadClickListener;
//import com.igexin.sdk.PushManager;
//import com.igexin.sdk.PushService;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//import de.greenrobot.event.EventBus;
//import xtom.frame.XtomActivityManager;
//import xtom.frame.util.XtomDeviceUuidFactory;
//import xtom.frame.util.XtomSharedPreferencesUtil;
//import xtom.frame.util.XtomTimeUtil;
//import xtom.frame.util.XtomToastUtil;
//
///**
// * Created by WangYuxia on 2016/4/29.
// * 首页
// */
//public class MainActivity extends BaseActivity implements View.OnClickListener {
//    ImageCarouselBanner imageCarouselBanner;
//    private ImageView image_left;
//    private ImageView image_right;
//    private ImageView image_point;
//    private ImageView title_btn_feedback;
//
//    private AutoChangeViewPager pager;
//    private RelativeLayout layout_pager;
//
//    private TextView tv_my_trips; //我的行程
//    private TextView tv_car_owner; //跨城车主
//    private TextView tv_publish;
//
//    private User user;
//    private ArrayList<AddListInfor> infors = new ArrayList<>();
//    private TopAddViewPagerAdapter adapter;
//    private int count;
//
//    private long time;// 用于判断二次点击返回键的时间间隔
//    private static MainActivity activity;
//    public ArrayList<DistrictInfor> allDistricts = new ArrayList<>();
//
//    public static MainActivity getInstance() {
//        return activity;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        activity = this;
//        setContentView(R.layout.activity_main);
//        super.onCreate(savedInstanceState);
//        EventBus.getDefault().register(this);
//        user = hm_WcpcUserApplication.getInstance().getUser();
//        if (user == null) {
//            image_point.setVisibility(View.INVISIBLE);
//        }
//        getNetWorker().timeRule();
//        getAdvertiseList();
//        getNetWorker().cityList("0");//获取已开通城市
//    }
//
//    //获取广告位的列表
//    private void getAdvertiseList() {
//        getNetWorker().advertiseList();
//    }
//    public void onEventMainThread(EventBusModel event) {
//        switch (event.getType()) {
//            case REFRESH_MAIN_DATA:
//                break;
//            case CLIENT_ID:
//                saveDevice(event.getContent());
//                break;
//        }
//    }
//    @Override
//    protected boolean onKeyBack() {
//        if ((System.currentTimeMillis() - time) >= 2000) {
//            XtomToastUtil.showShortToast(mContext, "再按一次返回键退出程序");
//            time = System.currentTimeMillis();
//        } else {
//            XtomActivityManager.finishAll();
//        }
//        return true;
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (imageCarouselBanner != null)
//            imageCarouselBanner.stopShow();
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    @Override
//    public void onStop() {
//        if (imageCarouselBanner != null)
//            imageCarouselBanner.stopShow();
//        super.onStop();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (imageCarouselBanner != null)
//            imageCarouselBanner.startShow();
//        user = hm_WcpcUserApplication.getInstance().getUser();
//        checkPermission();
////        if(adapter != null){
////            adapter.setInfors(infors);
////            adapter.notifyDataSetChanged();
////        }
//
//        if (user != null)
//            getNetWorker().noticeUnread(user.getToken(), "2", "1");
//        else {
//            if (count == 0)
//                image_point.setVisibility(View.INVISIBLE);
//            else
//                image_point.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case ADVERTISE_LIST:
//                showProgressDialog("请稍后...");
//                break;
//            case NOTICE_UNREAD:
//                break;
//            case CAN_TRIPS:
//                showProgressDialog("请稍后...");
//                break;
//        }
//    }
//
//    @Override
//    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case ADVERTISE_LIST:
//            case CAN_TRIPS:
//                cancelProgressDialog();
//                break;
//            case NOTICE_UNREAD:
//                break;
//        }
//    }
//
//    @Override
//    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult baseResult) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case ADVERTISE_LIST:
//                HemaPageArrayResult<AddListInfor> aResult = (HemaPageArrayResult<AddListInfor>) baseResult;
//                infors = aResult.getObjects();
//                initPager();
//                break;
//            case NOTICE_UNREAD:
//                HemaArrayResult<String> cResult = (HemaArrayResult<String>) baseResult;
//                count = Integer.parseInt(isNull(cResult.getObjects().get(0)) ? "0" : cResult.getObjects().get(0));
//                if (count == 0)
//                    image_point.setVisibility(View.INVISIBLE);
//                else
//                    image_point.setVisibility(View.VISIBLE);
//                break;
//            case CAN_TRIPS:
//                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
//                String keytype = sResult.getObjects().get(0);
//                if ("1".equals(keytype)) {
//                    Intent it = new Intent(mContext, SendActivity.class);
//                    startActivity(it);
//                } else if ("2".equals(keytype)) {
////                    Intent it = new Intent(mContext, PublishInforActivity.class);
////                    startActivity(it);
//                    Intent it = new Intent(mContext, SendActivity.class);
//                    startActivity(it);
//                    showTextDialog("抱歉，您有尚未结束的行程，无法发布");
//                    return;
//                } else {
//                    String start= BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext,"order_start"),"HH:mm");
//                    String end= BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext,"order_end"),"HH:mm");
//                    showTextDialog("请在"+start+"至"+end+"期间下单");
//                }
//                break;
//            case CURRENT_TRIPS:
//                break;
//            case CITY_LIST:
//                HemaArrayResult<DistrictInfor> CResult = (HemaArrayResult<DistrictInfor>) baseResult;
//                allDistricts = CResult.getObjects();
//                String citys = "";
//                for (DistrictInfor infor : allDistricts) {
//                    citys = citys + infor.getName();
//                }
//                XtomSharedPreferencesUtil.save(mContext, "citys", citys);
//                break;
//            case TIME_RULE:
//                HemaArrayResult<TimeRule> tResult = (HemaArrayResult<TimeRule>) baseResult;
//                TimeRule  rule = tResult.getObjects().get(0);
//                XtomSharedPreferencesUtil.save(mContext,"order_start",rule.getTime1_begin());
//                XtomSharedPreferencesUtil.save(mContext,"order_end",rule.getTime1_end());
//                XtomSharedPreferencesUtil.save(mContext,"pin_end",rule.getTime2_end());
//                XtomSharedPreferencesUtil.save(mContext,"pin_start",rule.getTime2_begin());
//                break;
//        }
//    }
//
//    public ArrayList<DistrictInfor> getCitys() {
//        return allDistricts;
//    }
//
//    private void initPager() {
//        ArrayList<String> urls = new ArrayList<>();
//        for (AddListInfor item : infors) {
//            urls.add(item.getImgurlbig());
//        }
////        imageCarouselBanner.getLayoutParams().height = screenWide * 5 / 13;
//        imageCarouselBanner.onInstance(mContext, urls, R.drawable.indicator_show,
//                new ImageCarouselHeadClickListener(mContext, infors, "1"));
//    }
//
//    @Override
//    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case ADVERTISE_LIST:
//                showTextDialog("获取数据失败");
//                break;
//            case CAN_TRIPS:
//                showTextDialog("检查失败，请稍后重试");
//                break;
//        }
//    }
//
//    @Override
//    protected void callBackForServerFailed(HemaNetTask netTask,
//                                           HemaBaseResult baseResult) {
//        BaseHttpInformation information = (BaseHttpInformation) netTask
//                .getHttpInformation();
//        switch (information) {
//            case ADVERTISE_LIST:
//            case CAN_TRIPS:
//                showTextDialog(baseResult.getMsg());
//                break;
//        }
//    }
//
//
//    @Override
//    protected void findView() {
//        image_left = (ImageView) findViewById(R.id.title_btn_left);
//        image_right = (ImageView) findViewById(R.id.title_btn_right_image);
//        image_point = (ImageView) findViewById(R.id.title_point);
//        title_btn_feedback = (ImageView) findViewById(R.id.title_btn_feedback);
//
//        pager = (AutoChangeViewPager) findViewById(R.id.viewpager);
//        layout_pager = (RelativeLayout) findViewById(R.id.relativelayout);
//        tv_my_trips = (TextView) findViewById(R.id.tv_my_trip);
//        tv_publish = (TextView) findViewById(R.id.tv_publish);
//        tv_car_owner = (TextView) findViewById(R.id.tv_car_owner);
//        imageCarouselBanner = (ImageCarouselBanner) findViewById(R.id.image_carousel_banner);
//    }
//
//    @Override
//    protected void getExras() {
//    }
//
//    @Override
//    protected void setListener() {
//        image_left.setOnClickListener(this);
//        image_right.setOnClickListener(this);
//        tv_my_trips.setOnClickListener(this);
//        tv_publish.setOnClickListener(this);
//        title_btn_feedback.setOnClickListener(this);
//        tv_car_owner.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        user = hm_WcpcUserApplication.getInstance().getUser();
//        Intent it;
//        switch (view.getId()) {
//            case R.id.title_btn_left:
//                if (user == null) {
//                    ToLogin.showLogin(mContext);
//                } else {
//                    it = new Intent(mContext, PersonCenterNewActivity.class);
//                    startActivity(it);
//                }
//                break;
//            case R.id.title_btn_right_image:
//                if (user == null) {
//                    ToLogin.showLogin(mContext);
//                } else {
//                    it = new Intent(mContext, NoticeListActivity.class);
//                    startActivity(it);
//                }
//                break;
//            case R.id.tv_my_trip:
//                if (user == null) {
//                    ToLogin.showLogin(mContext);
//                } else {
//                    it = new Intent(mContext, MyCurrentTrip2Activity.class);
//                    startActivity(it);
//                }
//                break;
//            case R.id.tv_car_owner:
//                it = new Intent(mContext, CarOwerListActivity.class);
//                startActivity(it);
//                break;
//            case R.id.tv_publish:
//                if (user == null) {
//                    ToLogin.showLogin(mContext);
//                } else
////                    getNetWorker().currentTrips(user.getToken());
//                    getNetWorker().canTrips(user.getToken());
//                break;
//            case R.id.title_btn_feedback:
//                if (user == null) {
//                    ToLogin.showLogin(mContext);
//                } else {
//                    it = new Intent(mContext, FeedBackActivity.class);
//                    startActivity(it);
//                }
//                break;
//        }
//    }
//
//    // 广告位的数据适配器
//    private class PageChangeListener implements ViewPager.OnPageChangeListener {
//        TopAddViewPagerAdapter mAdapter;
//
//        public PageChangeListener(TopAddViewPagerAdapter mAdapter) {
//            this.mAdapter = mAdapter;
//        }
//
//        @Override
//        public void onPageSelected(int arg0) {
//            if (mAdapter != null) {
//                ViewGroup indicator = mAdapter.getIndicator();
//                if (indicator != null) {
//                    indicator.setVisibility(View.VISIBLE);
//                    RadioButton rbt = (RadioButton) indicator.getChildAt(arg0);
//                    if (rbt != null)
//                        rbt.setChecked(true);
//                }
//            }
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//    }
//
//
//
////    public void saveDevice() {
////        user = hm_WcpcUserApplication.getInstance().getUser();
////        if (user == null) {
////            return;
////        }
////        getNetWorker().deviceSave(user.getToken(),
////                PushUtils.getChannelId(mContext), "2",
////                PushUtils.getChannelId(mContext));
////    }
//
//
//    /*个推相关*/
//    // DemoPushService.class 自定义服务名称, 核心服务
//    private Class userPushService = PushService.class;
//    private static final int REQUEST_PERMISSION = 0;
//
//    private void checkPermission() {
//        PackageManager pkgManager = getPackageManager();
//
//        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
//        boolean sdCardWritePermission =
//                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        // read phone state用于获取 imei 设备信息
//        boolean phoneSatePermission =
//                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;
//
//        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
//                    REQUEST_PERMISSION);
//        } else {
//            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//        }
//
//        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
//        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
//        // IntentService, 必须在 AndroidManifest 中声明)
//        PushManager.getInstance().initialize(mContext.getApplicationContext(), PushService.class);
//        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSION) {
//            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
//                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//            } else {
//                Log.e("MainActivity", "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
//                        + "functions will not work");
//                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
//            }
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    public void saveDevice(String channelId) {
//        User user = getApplicationContext().getUser();
//        if (user == null) {
//            return;
//        }
//        String deviceId = PushUtils.getUserId(mContext);
//        if (isNull(deviceId)) {// 如果deviceId为空时，保存为手机串号
//            deviceId = XtomDeviceUuidFactory.get(mContext);
//        }
//        getNetWorker().deviceSave(user.getToken(),
//                deviceId, "2", channelId);
//    }
//
//    /*个推相关结束*/
//}
