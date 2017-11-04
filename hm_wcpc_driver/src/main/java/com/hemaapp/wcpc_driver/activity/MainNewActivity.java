package com.hemaapp.wcpc_driver.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.dialog.HemaButtonDialog;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.hm_FrameWork.view.RefreshLoadmoreLayout;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.RecycleUtils;
import com.hemaapp.wcpc_driver.adapter.FirstAdapter;
import com.hemaapp.wcpc_driver.adapter.SelectPositionAdapter;
import com.hemaapp.wcpc_driver.getui.GeTuiIntentService;
import com.hemaapp.wcpc_driver.getui.PushUtils;
import com.hemaapp.wcpc_driver.getui.ServiceLocationGPS;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.CurrentTripsInfor;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.module.Workstatus;
import com.hemaapp.wcpc_driver.view.LocationUtils;
import com.iflytek.sunflower.FlowerCollector;
import com.iflytek.thridparty.G;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.PushService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.XtomActivityManager;
import xtom.frame.util.XtomDeviceUuidFactory;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;
import xtom.frame.view.XtomRefreshLoadmoreLayout;

/**
 * 首页
 */
public class MainNewActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, LocationSource,
        AMapLocationListener {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right_image)
    ImageView titleBtnRightImage;
    @BindView(R.id.title_point)
    ImageView titlePoint;
    @BindView(R.id.bmapView)
    MapView mapView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.tv_go)
    TextView tvGo;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.refreshLoadmoreLayout)
    RefreshLoadmoreLayout refreshLoadmoreLayout;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.empty)
    LinearLayout empty;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tv_button)
    Button tvButton;
    private ArrayList<CurrentTripsInfor> blogs = new ArrayList<>();
    private Integer currentPage = 0;
    private FirstAdapter firstAdapter;
    private String lng, lat, district;
    private User user;

    private long time;// 用于判断二次点击返回键的时间间隔
    private boolean isSuccess = false;
    private static MainNewActivity activity;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LatLonPoint latLonPoint; //点击地点
    private OnLocationChangedListener mListener;
    private LatLng latlng;
    private Workstatus workstatus;

    public static MainNewActivity getInstance() {
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        setContentView(R.layout.activity_main_new);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        EventBus.getDefault().register(this);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        firstAdapter = new FirstAdapter(mContext, blogs, getNetWorker());
        RecycleUtils.initVerticalRecyle(rvList);
        rvList.setAdapter(firstAdapter);
        getList(currentPage);
        getNoticeUnread();
        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
        district = XtomSharedPreferencesUtil.get(mContext, "district");
        startGeTuiPush();
        startService();
        checkLocation();
    }

    private void getList(int page) {
        getNetWorker().tripsList(user.getToken(), "2", page);
    }

    private void getWorkStatus() {
        getNetWorker().workStatusGet(user.getToken());
    }

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

    private void init() {
        prepareLocation();
        if (aMap == null) {
            aMap = mapView.getMap();
            registerListener();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//默认显示级别
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
            aMap.setOnMyLocationChangeListener(this);
        }
    }

    private void prepareLocation() {
        locationClient = new AMapLocationClient(getApplicationContext());
        locationOption = new AMapLocationClientOption();
        // 设置定位模式为高精度模式
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(this);
        // 设置是否需要显示地址信息
        locationOption.setNeedAddress(true);
        // 设置定位参数
        locationOption.setOnceLocation(false);
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        locationOption.setGpsFirst(false);
        String strInterval = "5000";
        if (!TextUtils.isEmpty(strInterval)) {
            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
            locationOption.setInterval(Long.valueOf(strInterval));
        }
        locationClient.setLocationOption(locationOption);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
//        aMap.setOnMapClickListener(this);
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_MAIN_DATA:
                break;
            case CLIENT_ID:
                saveDevice(event.getContent());
                break;
            case REFRESH_BLOG_LIST:
                currentPage = 0;
                getList(currentPage);
                break;
        }
    }

    private void startService() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {//判断是否拥有定位权限

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    3);
        } else {
            startService(new Intent(mContext, ServiceLocationGPS.class));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startService();
    }

    @Override
    protected boolean onKeyBack() {
//        if ((System.currentTimeMillis() - time) >= 2000) {
//            XtomToastUtil.showShortToast(mContext, "再按一次返回键退出程序");
//            time = System.currentTimeMillis();
//        } else {
//            XtomActivityManager.finishAll();
//        }
//        return true;
        moveTaskToBack(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        stopGeTuiPush();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mapView.onDestroy();
        if (null != locationClient) {
            locationClient.onDestroy();
        }
        //com.hemaapp.wcpc_driver.getui.PushReceiver.stop();
    }

    private void getNoticeUnread() {
        getNetWorker().noticeUnread(user.getToken(), "1", "2");
    }


    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(mContext);
        FlowerCollector.onPageStart("MainActivity");
        super.onResume();
        checkPermission();
        getNoticeUnread();
        mapView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd("MainNewActivity");
        FlowerCollector.onPause(mContext);
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
            case TRIPS_LIST:
            case WORKSTATUS_GET:
                break;
            case GRAP_TRIPS:
                showProgressDialog("");
                break;
            case TRIPS_SAVEOPERATE:
                showProgressDialog("请稍后");
                break;
        }
    }


    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
            case TRIPS_SAVEOPERATE:
                cancelProgressDialog();
                break;
            case TRIPS_LIST:
                progressbar.setVisibility(View.GONE);
                refreshLoadmoreLayout.setVisibility(View.VISIBLE);
                break;
            case GRAP_TRIPS:
                cancelProgressDialog();
                break;
            case WORKSTATUS_GET:
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case NOTICE_UNREAD:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) baseResult;
                int count = Integer.parseInt(isNull(sResult.getObjects().get(0)) ? "0" : sResult.getObjects().get(0));
                if (count > 0)
                    titlePoint.setVisibility(View.VISIBLE);
                else
                    titlePoint.setVisibility(View.INVISIBLE);
                break;
            case TRIPS_LIST:
                getWorkStatus();
                String page = netTask.getParams().get("page");
                @SuppressWarnings("unchecked")
                HemaPageArrayResult<CurrentTripsInfor> gResult = (HemaPageArrayResult<CurrentTripsInfor>) baseResult;
                ArrayList<CurrentTripsInfor> goods = gResult.getObjects();
                if (page.equals("0")) {// 刷新
                    refreshLoadmoreLayout.refreshSuccess();
                    this.blogs.clear();
                    this.blogs.addAll(goods);
                    int sysPagesize = getApplicationContext().getSysInitInfo()
                            .getSys_pagesize();
                    if (goods.size() < sysPagesize)
                        refreshLoadmoreLayout.setLoadmoreable(false);
                    else
                        refreshLoadmoreLayout.setLoadmoreable(true);
                } else {// 更多
                    refreshLoadmoreLayout.loadmoreSuccess();
                    if (goods.size() > 0)
                        this.blogs.addAll(goods);
                    else {
                        refreshLoadmoreLayout.setLoadmoreable(false);
                        XtomToastUtil.showShortToast(mContext, "已经到最后啦");
                    }
                }
                if (blogs.size() == 0) {
                    empty.setVisibility(View.VISIBLE);
                } else {
                    empty.setVisibility(View.INVISIBLE);
                }
                firstAdapter.notifyDataSetChanged();
                break;
            case DEVICE_SAVE:
                isSuccess = true;
                break;
            case TRIPS_SAVEOPERATE:
                currentPage = 0;
                getList(0);
                break;
            case WORKSTATUS_GET:
                HemaArrayResult<Workstatus> WResult = (HemaArrayResult<Workstatus>) baseResult;
                workstatus = WResult.getObjects().get(0);
                if (workstatus.getCurrent_driver_trip_id().equals("0")) {//没有行程，首页为空
                    tvButton.setVisibility(View.GONE);
                    tvTip.setVisibility(View.GONE);
                } else {
                    if (workstatus.getAllgetflag().equals("0")) {//当前行程的乘客是否都已上车
                        tvButton.setVisibility(View.GONE);
                        tvTip.setVisibility(View.GONE);
                    } else {
                        tvButton.setVisibility(View.VISIBLE);
                        if (workstatus.getFinalworkflag().equals("0")) {//返程接单开关状态
                            tvButton.setEnabled(true);
                            tvButton.setText("开始接返程订单");
                            tvTip.setVisibility(View.GONE);
                        } else {
                            tvButton.setEnabled(false);
                            tvButton.setText("接单地点:" + workstatus.getFinal_address());
                            tvTip.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
            case TRIPS_SAVEOPERATE:
                getWorkStatus();
                showTextDialog(baseResult.getMsg());
                break;
            case GRAP_TRIPS:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                getWorkStatus();
                showTextDialog("加载失败");
                break;
            case TRIPS_SAVEOPERATE:
                showTextDialog("操作失败");
                break;
            case GRAP_TRIPS:
                showTextDialog("抢单失败，请稍后重试");
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
    }

    @Override
    protected void setListener() {
        refreshLoadmoreLayout.setOnStartListener(new XtomRefreshLoadmoreLayout.OnStartListener() {

            @Override
            public void onStartRefresh(XtomRefreshLoadmoreLayout v) {
                currentPage = 0;
                getList(currentPage);
            }

            @Override
            public void onStartLoadmore(XtomRefreshLoadmoreLayout v) {
                currentPage++;
                getList(currentPage);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout:
            case R.id.layout_1:
                break;
            case R.id.layout_0:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* 推送相关 */
    private PushReceiver pushReceiver;

    private void startGeTuiPush() {
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
            mFilter.addAction("com.hemaapp.push.location");
            registerReceiver(pushReceiver, mFilter);
        }
    }

    private void unregisterPushReceiver() {
        if (pushReceiver != null)
            unregisterReceiver(pushReceiver);
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right_image, R.id.tv_go, R.id.tv_button})
    public void onViewClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left:
                it = new Intent(mContext, PersonCenterInforActivity.class);
                startActivity(it);
                break;
            case R.id.title_btn_right_image:
                it = new Intent(mContext, NoticeListActivity.class);
                startActivity(it);
                break;
            case R.id.tv_go:
                if (workstatus != null) {
                    it = new Intent(mContext, SelectPositionActivity.class);
                    it.putExtra("client", workstatus.getClients());
                    it.putExtra("flag", 0);
                    it.putExtra("allgetflag", workstatus.getAllgetflag());
                    startActivity(it);
                }
                break;
            case R.id.tv_button:
                dialog();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        log_e("执行定位------------------------------");
        if (mListener != null && location != null) {
            // mListener.onLocationChanged(location);// 显示系统小蓝点
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
            Message msg = mHandler.obtainMessage();
            msg.obj = location;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            mHandler.sendMessage(msg);
        }

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
                    XtomSharedPreferencesUtil.save(mContext, "lng", lng);
                    XtomSharedPreferencesUtil.save(mContext, "lat", lat);
                    latLonPoint = new LatLonPoint(loc.getLatitude(), loc.getLongitude());
                    latLonPoint = new LatLonPoint(Double.parseDouble(lat), Double.parseDouble(lng));
                    latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
                            16);
                    aMap.moveCamera(update);
                    getNetWorker().positionSave(user.getToken(), "2", lng, lat);
                    log_e("loc.getAoiName()=====" + loc.getAoiName());
                    log_e("loc.getPoiName()=====" + loc.getPoiName());
                    log_e("loc.getLocationDetail()=====" + loc.getLocationDetail());
                    log_e("loc.getStreet()=====" + loc.getStreet());
                    log_e("loc.getAddress()=====" + loc.getAddress());
                    log_e("loc.getDistrict()=====" + loc.getDistrict());
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    break;
            }
        }
    };

    @Override
    public void onMyLocationChange(Location location) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        startLocation();
    }

    private void startLocation() {
        // 启动定位
        locationClient.startLocation();
        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
    }

    @Override
    public void deactivate() {
        //mListener = null;
    }

    private class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            handleEvent(intent);
        }

        private void handleEvent(Intent intent) {
            String action = intent.getAction();
            if ("com.hemaapp.push.connect".equals(action)) {
                if (!isSuccess)
                    saveDevice();
            } else if ("com.hemaapp.push.msg".equals(action)) {
            } else if ("com.hemaapp.push.location".equals(action)) {
                String mlng = XtomSharedPreferencesUtil.get(mContext, "lng");
                String mlat = XtomSharedPreferencesUtil.get(mContext, "lat");
                getNetWorker().positionSave(user.getToken(), "2", mlng, mlat);
            }
        }
    }

    public void saveDevice() {
        User user = getApplicationContext().getUser();
        getNetWorker().deviceSave(user.getToken(),
                PushUtils.getChannelId(mContext),
                PushUtils.getChannelId(mContext));
    }

	/* 推送相关end */

    /*个推相关*/
    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = PushService.class;
    private static final int REQUEST_PERMISSION = 0;

    private void checkPermission() {
        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if (Build.VERSION.SDK_INT >= 23 && !sdCardWritePermission || !phoneSatePermission) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSION);
        } else {
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
        }

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().initialize(mContext.getApplicationContext(), PushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), GeTuiIntentService.class);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 3) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] != PackageManager.PERMISSION_GRANTED) {//未获得定位权限
            } else {
                startService();
            }
            return;
        }

        if (requestCode == REQUEST_PERMISSION) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            } else {
                Log.e("MainActivity", "We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void saveDevice(String channelId) {
        User user = getApplicationContext().getUser();
        if (user == null) {
            return;
        }
        String deviceId = PushUtils.getUserId(mContext);
        if (isNull(deviceId)) {// 如果deviceId为空时，保存为手机串号
            deviceId = XtomDeviceUuidFactory.get(mContext);
        }
        getNetWorker().deviceSave(user.getToken(),
                deviceId, channelId);
    }

    /*个推相关结束*/
    private HemaButtonDialog mDialog;

    public void dialog() {
        if (mDialog == null) {
            mDialog = new HemaButtonDialog(mContext);
        }
        mDialog.setLeftButtonText("取消");
        mDialog.setRightButtonText("确定");
        mDialog.setText("平台会在乘客出发前两个小时进行派单，确定2个小时可以到达接单地点范围？");
        mDialog.setButtonListener(new ButtonListener());
        mDialog.setRightButtonTextColor(mContext.getResources().getColor(R.color.yellow));

        mDialog.show();
    }

    private class ButtonListener implements HemaButtonDialog.OnButtonListener {

        @Override
        public void onLeftButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
        }

        @Override
        public void onRightButtonClick(HemaButtonDialog dialog) {
            dialog.cancel();
            Intent it = new Intent(mContext, SelectPositionActivity.class);
            it.putExtra("client", workstatus.getClients());
            it.putExtra("flag", 1);
            it.putExtra("allgetflag", workstatus.getAllgetflag());
            startActivity(it);
        }
    }
}
