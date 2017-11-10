package com.hemaapp.wcpc_user.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseUtil;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.RecycleUtils;
import com.hemaapp.wcpc_user.adapter.TogetherAdapter;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.Client;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.DriverPosition;
import com.hemaapp.wcpc_user.module.SysInitInfo;
import com.hemaapp.wcpc_user.module.User;
import com.hemaapp.wcpc_user.view.LocationUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomTimeUtil;

/**
 * 我当前的行程
 * 1、先获取用户的当前行程，如没有行程，则定位，显示当前位置，并隐藏导航。
 */
public class MyCurrentTrip2Activity extends BaseActivity implements AMap.OnMyLocationChangeListener, LocationSource,
        AMapLocationListener, AMap.InfoWindowAdapter {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.bmapView)
    MapView mapView;
    @BindView(R.id.tv_tip)
    TextView tvTip;
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_start)
    TextView tvStart;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.lv_rout)
    LinearLayout lvRout;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.tv_distance)
    TextView tvDistance;
    @BindView(R.id.tv_car)
    TextView tvCar;
    @BindView(R.id.lv_driver)
    LinearLayout lvDriver;
    @BindView(R.id.iv_tel)
    ImageView ivTel;
    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.tv_couple)
    TextView tvCouple;
    @BindView(R.id.tv_together)
    TextView tvTogether;
    @BindView(R.id.rv_list)
    RecyclerView rvList;
    @BindView(R.id.tv_button0)
    TextView tvButton0;
    @BindView(R.id.tv_button1)
    TextView tvButton1;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.lv_bottom)
    LinearLayout lvBottom;
    @BindView(R.id.lv_together)
    LinearLayout lvTogether;
    @BindView(R.id.iv_visible)
    ImageView ivVisible;
    @BindView(R.id.iv_send)
    ImageView ivSend;
    @BindView(R.id.tv_notrip)
    TextView tvNotrip;
    private AMap aMap;

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LatLonPoint latLonPoint; //点击地点
    private OnLocationChangedListener mListener;
    private LatLng latlng;
    private String map_title;

    private String lng, lat, start_city;
    private User user;
    private CurrentTripsInfor infor;
    private LatLonPoint toPointExra;
    private LatLonPoint fromPointExra;
    AlphaAnimation appearAnimation, disappearAnimation;

    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;

    private SysInitInfo sysInitInfo;
    private String phone;
    private TogetherAdapter togetherAdapter;
    private ArrayList<Client> clients = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_current_trip_new);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        user = hm_WcpcUserApplication.getInstance().getUser();
        EventBus.getDefault().register(this);
        sysInitInfo = hm_WcpcUserApplication.getInstance().getSysInitInfo();
        getNetWorker().currentTrips(user.getToken());
        appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setDuration(500);
        disappearAnimation = new AlphaAnimation(1, 0);
        disappearAnimation.setDuration(500);
        togetherAdapter = new TogetherAdapter(mContext, clients);
        RecycleUtils.initHorizontalRecyle(rvList);
        rvList.setAdapter(togetherAdapter);
    }

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_BLOG_LIST:
                getNetWorker().currentTrips(user.getToken());
                break;
        }
    }

    private void initData() {
        checkLocation();
        if (infor == null) { //没有当前行程
            layoutTop.setVisibility(View.GONE);
            lvBottom.setVisibility(View.GONE);
            ivSend.setVisibility(View.VISIBLE);
            tvNotrip.setVisibility(View.VISIBLE);
            ivVisible.setVisibility(View.GONE);

        } else {
            initOrderInfor();
            tvNotrip.setVisibility(View.GONE);
            layoutTop.startAnimation(appearAnimation);
            ivVisible.startAnimation(appearAnimation);
            layoutTop.setVisibility(View.VISIBLE);
            ivVisible.setVisibility(View.VISIBLE);
            lvBottom.setVisibility(View.VISIBLE);
            ivSend.setVisibility(View.GONE);
        }
    }

    private void initOrderInfor() {
//        if (infor.getCarpoolflag().equals("0")) {
//            tvNum.setVisibility(View.GONE);
//        }
        if (infor.getStatus().equals("0")) {//待派单
            tvTip.setVisibility(View.VISIBLE);
            lvRout.setVisibility(View.VISIBLE);
            lvDriver.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.GONE);
            tvDistance.setVisibility(View.GONE);
            ivTel.setImageResource(R.mipmap.img_order_kefu);
            lvTogether.setVisibility(View.INVISIBLE);
            tvButton0.setVisibility(View.VISIBLE);
            tvButton1.setVisibility(View.GONE);
            tvButton0.setTextColor(0xff5e5e5e);
            tvButton0.setBackgroundResource(R.drawable.bg_operate);
            tvButton0.setText("取消订单");
            tvStart.setText(infor.getStartaddress());
            tvEnd.setText(infor.getEndaddress());
            tvNum.setText("我的乘车人数:" + infor.getNumbers() + "人");
            tvTime.setVisibility(View.VISIBLE);
            tvNum.setVisibility(View.VISIBLE);
            tvTime.setText("我的出发时间:" + XtomTimeUtil.TransTime(infor.getBegintime(), "MM-dd HH:mm"));
            tvPrice.setText(infor.getTotal_fee() + "元");
            if (isNull(infor.getCoupon_fee()) || infor.getCoupon_fee().equals("0.00")) {
                tvCouple.setVisibility(View.GONE);
            } else {
                tvCouple.setText("(代金券抵扣" + infor.getCoupon_fee() + "元)");
            }

        } else {
            if (infor.getStatus().equals("1")) {//待上车
                tvButton0.setVisibility(View.VISIBLE);
                tvButton1.setVisibility(View.VISIBLE);
                tvDistance.setVisibility(View.VISIBLE);
                tvButton0.setText("取消订单");
                tvButton1.setText("确认上车");
                tvButton0.setTextColor(0xff5e5e5e);
                tvButton1.setTextColor(0xffffffff);
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText("我的出发时间:" + XtomTimeUtil.TransTime(infor.getBegintime(), "MM-dd HH:mm"));
                tvButton0.setBackgroundResource(R.drawable.bg_operate);
                tvButton1.setBackgroundResource(R.drawable.bt_qiangdan);
            } else if (infor.getStatus().equals("3")) {//待送达
                tvTime.setVisibility(View.GONE);
                tvButton0.setVisibility(View.GONE);
                tvButton1.setVisibility(View.VISIBLE);
                tvDistance.setVisibility(View.GONE);
                tvButton1.setText("到达目的地");
                tvButton1.setTextColor(0xffffffff);
                tvButton1.setBackgroundResource(R.drawable.bt_qiangdan);
            } else if (infor.getStatus().equals("5")) {//待支付
                tvTime.setVisibility(View.GONE);
                tvButton0.setVisibility(View.VISIBLE);
                tvButton1.setVisibility(View.VISIBLE);
                tvDistance.setVisibility(View.GONE);
                tvButton0.setText("到达目的地");
                tvButton1.setText("去支付");
                tvButton0.setTextColor(0xffff9900);
                tvButton1.setTextColor(0xffffffff);
                tvButton1.setBackgroundResource(R.drawable.bt_qiangdan);
                tvButton0.setBackgroundColor(0xffffffff);
            }
            ivAvatar.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.GONE);
            lvRout.setVisibility(View.GONE);
            lvDriver.setVisibility(View.VISIBLE);
            ivTel.setImageResource(R.mipmap.img_order_tel);
            lvTogether.setVisibility(View.VISIBLE);

            ImageLoader.getInstance().displayImage(infor.getDriver_avatar(), ivAvatar, hm_WcpcUserApplication.getInstance()
                    .getOptions(R.mipmap.default_driver));
            ivAvatar.setCornerRadius(100);
            tvName.setText(infor.getRealname());
            tvCar.setText(infor.getCarbrand() + " " + infor.getCarnumber());
            tvNum.setText("我的乘车人数:" + infor.getNumbers() + "人");
            if (isNull(infor.getCoupon_fee()) || infor.getCoupon_fee().equals("0.00")) {
                tvCouple.setVisibility(View.GONE);
            } else {
                tvCouple.setText("(代金券抵扣" + infor.getCoupon_fee() + "元)");
            }
            tvPrice.setText(infor.getTotal_fee() + "元");
            clients.clear();
            clients.addAll(infor.getClients());
            if (clients.size() == 0)
                tvTogether.setVisibility(View.GONE);
            else
                tvTogether.setVisibility(View.VISIBLE);
            togetherAdapter.notifyDataSetChanged();

        }
        if ("男".equals(infor.getDriver_sex()))
            ivSex.setImageResource(R.mipmap.img_sex_boy);
        else
            ivSex.setImageResource(R.mipmap.img_sex_girl);
        if (infor.getStatus().equals("5")) {//待支付
            Intent  it = new Intent(mContext, ToPayActivity.class);
            it.putExtra("id", infor.getId());
            it.putExtra("total_fee", infor.getTotal_fee());
            it.putExtra("driver_id", infor.getDriver_id());
            startActivityForResult(it, R.id.layout);
        }
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
            aMap.moveCamera(CameraUpdateFactory.zoomTo(12));//默认显示级别
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
        String strInterval = "10000";
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
        aMap.setInfoWindowAdapter(this);
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
        //deactivate();
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case CURRENT_TRIPS:
                showProgressDialog("请稍后...");
                break;
            case ORDER_OPERATE:
                showProgressDialog("请稍后...");
                break;
            case CAN_TRIPS:
                showProgressDialog("请稍后...");
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case CURRENT_TRIPS:
            case ORDER_OPERATE:
            case CAN_TRIPS:
                cancelProgressDialog();
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case CURRENT_TRIPS:
                HemaArrayResult<CurrentTripsInfor> cResult = (HemaArrayResult<CurrentTripsInfor>) hemaBaseResult;
                if (cResult.getObjects() != null && cResult.getObjects().size() > 0)
                    infor = cResult.getObjects().get(0);
                else
                    infor = null;
                initData();
                break;
            case ORDER_OPERATE:
                getNetWorker().currentTrips(user.getToken());
                break;
            case CAN_TRIPS:
                HemaArrayResult<String> sResult = (HemaArrayResult<String>) hemaBaseResult;
                String keytype = sResult.getObjects().get(0);
                if ("1".equals(keytype)) {
                    Intent it = new Intent(mContext, SendActivity.class);
                    it.putExtra("flag",1);
                    startActivity(it);
                } else if ("2".equals(keytype)) {
                    showTextDialog("抱歉，您有尚未结束的行程，无法发布");
                    return;
                } else {
                    String start = BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext, "order_start"), "HH:mm");
                    String end = BaseUtil.TransTimeHour(XtomSharedPreferencesUtil.get(mContext, "order_end"), "HH:mm");
                    showTextDialog("请在" + start + "至" + end + "期间下单");
                }
                break;
            case DRIVER_POSITION_GET:
                HemaArrayResult<DriverPosition> DResult = (HemaArrayResult<DriverPosition>) hemaBaseResult;
                DriverPosition position = DResult.getObjects().get(0);
                LatLng end_latlng = new LatLng(Double.parseDouble(position.getLat()), Double.parseDouble(position.getLng()));
                initStart();
                Marker endMarker = aMap.addMarker(new MarkerOptions()
                        .position(end_latlng)
                        .title("终点")
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.
                                        decodeResource(getResources(), R.mipmap.img_marker_car))));
                Double d_lng = Double.parseDouble(lng);
                Double d_lat = Double.parseDouble(lat);
                Double distance = BaseUtil.GetDistance(d_lat, d_lng,
                        Double.parseDouble(position.getLat()),
                        Double.parseDouble(position.getLng()));
                log_e("lat====" + lat);
                log_e("lng====" + lng);
                log_e("lat2====" + position.getLat());
                log_e("lng2====" + position.getLng());
                log_e("distance====" + distance);
//                tvDistance.setText("距您" + BaseUtil.transDistance(Float.parseFloat(String.valueOf(distance))));
                tvDistance.setText("距您" + distance+"km");
                break;
        }
    }

    private void initStart() {
        aMap.clear();
        LatLng start_latlng = new LatLng(Double.parseDouble(lat),
                Double.parseDouble(lng));
        Marker startMarker = aMap.addMarker(new MarkerOptions()
                .position(start_latlng)
                .title("起点")
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.
                                decodeResource(getResources(), R.mipmap.img_marker_me))));
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(start_latlng,
                12);
        aMap.moveCamera(update);
    }

    @Override
    protected void callBackForServerFailed(HemaNetTask netTask,
                                           HemaBaseResult baseResult) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case ORDER_OPERATE:
                showTextDialog(baseResult.getMsg());
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
        switch (information) {
            case CURRENT_TRIPS:
                showTextDialog("抱歉，获取当前行程失败");
                break;
            case ORDER_OPERATE:
                showTextDialog("抱歉，操作失败");
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
        titleText.setText("当前行程");
        titleBtnRight.setVisibility(View.INVISIBLE);

    }

    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case R.id.layout:
            case R.id.layout_1:
            case 1:
                getNetWorker().currentTrips(user.getToken());
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        log_e("执行定位------------------------------");
        if (mListener != null && location != null) {
            //mListener.onLocationChanged(location);// 显示系统小蓝点
            float bearing = aMap.getCameraPosition().bearing;
            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
            Message msg = mHandler.obtainMessage();
            msg.obj = location;
            msg.what = LocationUtils.MSG_LOCATION_FINISH;
            //  mHandler.sendMessage(msg);
            lng = String.valueOf(location.getLongitude());
            lat = String.valueOf(location.getLatitude());
            if (infor != null && !infor.getDriver_id().equals("0") && infor.getStatus().equals("1"))
                getNetWorker().driverPositionGet(user.getToken(), infor.getId(), infor.getDriver_id());
            else {
                initStart();
            }
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
                            12);
                    aMap.moveCamera(update);
                    break;
                //停止定位
                case LocationUtils.MSG_LOCATION_STOP:
                    break;
            }
        }
    };

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
        mListener = null;
    }

    private Marker marker;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            marker.showInfoWindow();
        }
    };


    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_marker1, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, PublishInforActivity.class);
                it.putExtra("start_lng", lng);
                it.putExtra("start_lat", lat);
                it.putExtra("start_position", map_title);
                it.putExtra("start_city", start_city);

                startActivity(it);
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.iv_tel, R.id.tv_button0, R.id.tv_button1, R.id.lv_bottom, R.id.iv_visible,
            R.id.iv_send})
    public void onViewClicked(View view) {
        Intent it;
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                break;
            case R.id.iv_tel:
                if (infor != null) {
                    if (infor.getStatus().equals("0")) {//待派单
                        phone = sysInitInfo.getSys_service_phone();
                        toMakePhone();
                    } else {
                        phone = infor.getDriver_username();
                        toMakePhone();
                    }
                }
                break;
            case R.id.tv_button0:
                if (infor != null) {
                    if (infor.getStatus().equals("0") || infor.getStatus().equals("1")) {//取消订单
                        CancelTip();
//                        it = new Intent(mContext, CancelOrderActivity.class);
//                        it.putExtra("id", infor.getId());
//                        startActivityForResult(it, 1);
                    }
                }
                break;
            case R.id.tv_button1:
                if (infor != null) {
                    if (infor.getStatus().equals("1")) {//确认上车
                        shangcheDialog();
                    } else if (infor.getStatus().equals("3")) {//确认送达
                        showarrivedDialog();
                    } else if (infor.getStatus().equals("5")) {//支付
                        it = new Intent(mContext, ToPayActivity.class);
                        it.putExtra("id", infor.getId());
                        it.putExtra("total_fee", infor.getTotal_fee());
                        it.putExtra("driver_id", infor.getDriver_id());
                        startActivityForResult(it, R.id.layout);
                    }
                }
                break;
            case R.id.lv_bottom:
                if (isAvilible(mContext, "com.autonavi.minimap")) {

//                    StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
//                            .append("小叫车");
//                    if (!TextUtils.isEmpty(infor.getEndaddress())) {
//                        stringBuffer.append("&poiname=").append(infor.getEndaddress());
//                    }
//                    stringBuffer.append("&lat=").append(infor.getLat_end())
//                            .append("&lon=").append(infor.getLng_end())
//                            .append("&dev=").append("1")
//                            .append("&style=").append("2");

//                    Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
//                    intent.setPackage("com.autonavi.minimap");
//                    mContext.startActivity(intent);
                    StringBuffer stringBuffer = new StringBuffer("androidamap://keywordNavi?sourceApplication=")
                            .append("小叫车");
                    if (!TextUtils.isEmpty(infor.getEndaddress())) {
                        stringBuffer.append("&keyword=").append(infor.getEndaddress());
                    }
                    stringBuffer
                            .append("&style=").append("2");
                    Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
                    intent.setPackage("com.autonavi.minimap");
                    mContext.startActivity(intent);
                } else {
                    showTextDialog("您尚未安装高德地图或地图版本过低");
                }
                break;
            case R.id.iv_visible:
                if (layoutTop.getVisibility() == View.GONE) {
                    layoutTop.startAnimation(appearAnimation);
                    ivVisible.startAnimation(appearAnimation);
                    layoutTop.setVisibility(View.VISIBLE);
                    ivVisible.setImageResource(R.mipmap.img_visible);
                } else {
                    layoutTop.startAnimation(disappearAnimation);
                    ivVisible.startAnimation(disappearAnimation);
                    disappearAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                            ivVisible.setImageResource(R.mipmap.img_visible_v);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            layoutTop.setVisibility(View.GONE);
                        }
                    });
                }
                break;
            case R.id.iv_send:
                getNetWorker().canTrips(user.getToken());
                break;
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            lng = location.getLongitude() + "";
            lat = location.getLatitude() + "";
        }
    }

    private void toMakePhone() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_phone, null);
        content1 = (TextView) mViewGroup.findViewById(R.id.textview);
        content2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        if (infor.getStatus().equals("0"))
            content1.setText("拨打客服电话");
        else
            content1.setText("拨打司机电话");
        content2.setText(phone);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                //Intent.ACTION_CALL 直接拨打电话，就是进入拨打电话界面，电话已经被拨打出去了。
                //Intent.ACTION_DIAL 是进入拨打电话界面，电话号码已经输入了，但是需要人为的按拨打电话键，才能播出电话。
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                        + phone));
                startActivity(intent);
            }
        });
    }

    private void showarrivedDialog() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_exit, null);
        TextView exit = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        TextView pop_content = (TextView) mViewGroup.findViewById(R.id.textview);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        pop_content.setText("为了保障您的出行，请谨慎操作。\n确定到达目的地吗？");
        cancel.setText("取消");
        exit.setText("确定");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                getNetWorker().orderOperate(user.getToken(), "5", infor.getId(), "", "", "");
            }
        });
    }
    private void shangcheDialog() {
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_exit, null);
        TextView exit = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView cancel = (TextView) mViewGroup.findViewById(R.id.textview_0);
        TextView pop_content = (TextView) mViewGroup.findViewById(R.id.textview);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        pop_content.setText("为保障您的出行，请谨慎操作。\n确定已上车吗？");
        cancel.setText("取消");
        exit.setText("确定");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                getNetWorker().orderOperate(user.getToken(), "3", infor.getId(), "", "", "");
            }
        });
    }

    private void CancelTip() {
        user = hm_WcpcUserApplication.getInstance().getUser();
        if (mWindow != null) {
            mWindow.dismiss();
        }
        mWindow = new PopupWindow(mContext);
        mWindow.setWidth(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setHeight(FrameLayout.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setFocusable(true);
        mWindow.setAnimationStyle(R.style.PopupAnimation);
        mViewGroup = (ViewGroup) LayoutInflater.from(mContext).inflate(
                R.layout.pop_first_tip, null);
        TextView cancel = (TextView) mViewGroup.findViewById(R.id.textview_1);
        TextView ok = (TextView) mViewGroup.findViewById(R.id.textview_2);
        TextView title1 = (TextView) mViewGroup.findViewById(R.id.textview);
        TextView title2 = (TextView) mViewGroup.findViewById(R.id.textview_0);
        mWindow.setContentView(mViewGroup);
        mWindow.showAtLocation(mViewGroup, Gravity.CENTER, 0, 0);
        if (user.getToday_cancel_count().equals("3")) {
            title1.setText("您今天已取消3次订单！");
        } else
            title1.setText("确定要取消吗？");
        title2.setText("一天内订单取消不能超过3次,您已取消" + user.getToday_cancel_count() + "次");
        cancel.setText("取消");
        ok.setText("确定");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
                if (user.getToday_cancel_count().equals("3")) {
                    return;
                }
                Intent it = new Intent(mContext, CancelOrderActivity.class);
                it.putExtra("id", infor.getId());
                if (infor.getStatus().equals("0"))
                    it.putExtra("keytype", "1");
                else
                    it.putExtra("keytype", "6");
                startActivityForResult(it, 1);
            }
        });
    }
}
