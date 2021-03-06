//package com.hemaapp.wcpc_user.activity;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.CameraUpdate;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.LocationSource;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.overlay.DrivingRouteOverlay;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.geocoder.GeocodeResult;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.RegeocodeAddress;
//import com.amap.api.services.geocoder.RegeocodeQuery;
//import com.amap.api.services.geocoder.RegeocodeResult;
//import com.amap.api.services.route.BusRouteResult;
//import com.amap.api.services.route.DrivePath;
//import com.amap.api.services.route.DriveRouteResult;
//import com.amap.api.services.route.RouteSearch;
//import com.amap.api.services.route.WalkRouteResult;
//import com.hemaapp.hm_FrameWork.HemaNetTask;
//import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
//import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
//import com.hemaapp.hm_FrameWork.view.RoundedImageView;
//import com.hemaapp.wcpc_user.BaseActivity;
//import com.hemaapp.wcpc_user.BaseHttpInformation;
//import com.hemaapp.wcpc_user.BaseUtil;
//import com.hemaapp.wcpc_user.R;
//import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
//import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
//import com.hemaapp.wcpc_user.module.ReplyItems;
//import com.hemaapp.wcpc_user.module.Route;
//import com.hemaapp.wcpc_user.module.User;
//import com.hemaapp.wcpc_user.view.LocationUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import xtom.frame.util.XtomSharedPreferencesUtil;
//import xtom.frame.util.XtomToastUtil;
//
///**
// * Created by wangyuxia on 2017/9/27.
// * 我当前的行程
// * 1、先获取用户的当前行程，如没有行程，则定位，显示当前位置，并隐藏导航。
// * 2、根据当前行程，规划处路径，导航显示。
// */
//public class MyCurrentTripActivity extends BaseActivity implements LocationSource,
//        AMapLocationListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener,
//        AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
//
//    private TextView title;
//    private TextView right;
//    private ImageView left;
//
//    private MapView mapView;
//    private LinearLayout layout_top;
//    private RelativeLayout layout_order;
//    private RoundedImageView image_avatar;
//    private TextView tv_nickname;
//    private ImageView img_sex;
//    private TextView tv_status;
//    private TextView tv_startaddress;
//    private TextView tv_endaddress;
//    private TextView tv_car_brand;
//    private TextView tv_car_no;
//    private TextView tv_money;
//    private TextView bt_cancel;
//    private TextView bt_ok;
//
//    private LinearLayout layout_bottom;
//    private AMap aMap;
//
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = null;
//    private GeocodeSearch geocoderSearch;
//    private LatLonPoint latLonPoint; //点击地点
//    private OnLocationChangedListener mListener;
//    private LatLng latlng;
//    private String map_title;
//
//    private String lng, lat, start_city;
//    private User user;
//    private CurrentTripsInfor infor;
//    private RouteSearch routeSearch;
//    private ArrayList<Route> carRoutes = new ArrayList<>();
//    private LatLonPoint toPointExra;
//    private LatLonPoint fromPointExra;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_current_trip);
//        super.onCreate(savedInstanceState);
//        mapView.onCreate(savedInstanceState);// 此方法必须重写
//
//        user = hm_WcpcUserApplication.getInstance().getUser();
//        getNetWorker().currentTrips(user.getToken());
//    }
//
//    private void initData() {
//        checkLocation();
//        if (infor == null) { //没有当前行程
//            layout_top.setVisibility(View.GONE);
//            layout_bottom.setVisibility(View.GONE);
//        } else {
//            initOrderInfor();
//            layout_top.setVisibility(View.VISIBLE);
//            layout_bottom.setVisibility(View.VISIBLE);
//            //drawRoute();
////            initStart();
////            initEnd();
//        }
//    }
//
//    private void initEnd() {
//        aMap.setOnMapClickListener(this);
//        aMap.setMyLocationEnabled(true);
//
//        if (!isNull(infor.getLat_end()) && !isNull(infor.getLng_end())) {
//            startSearch();
//        }
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//    }
//
//    private void startSearch() {
//        LatLng end_latlng = new LatLng(Double.parseDouble(infor.getLat_end()), Double.parseDouble(infor.getLng_end()));
//        Marker endMarker = aMap.addMarker(new MarkerOptions()
//                .position(end_latlng)
//                .title("终点")
//                .icon(BitmapDescriptorFactory
//                        .fromBitmap(BitmapFactory.
//                                decodeResource(getResources(), R.mipmap.img_endposition_logo))));
//
//        toPointExra = new LatLonPoint(Double.parseDouble(infor.getLat_end()), Double.parseDouble(infor.getLng_end()));
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                fromPointExra, toPointExra);
//        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
//                RouteSearch.DrivingDefault, null, null, "");
//        routeSearch.calculateDriveRouteAsyn(query);
//    }
//
//    private void initStart() {
//        LatLng start_latlng = new LatLng(Double.parseDouble(infor.getLat_start()),
//                Double.parseDouble(infor.getLat_start()));
//        Marker startMarker = aMap.addMarker(new MarkerOptions()
//                .position(start_latlng)
//                .title("起点")
//                .icon(BitmapDescriptorFactory
//                        .fromBitmap(BitmapFactory.
//                                decodeResource(getResources(), R.mipmap.img_startposition_logo))));
//        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(start_latlng,
//                15);
//        aMap.moveCamera(update);
//    }
//
//
//    private void drawRoute() {
//        routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        fromPointExra = new LatLonPoint(Double.parseDouble(infor.getLat_start()), Double.parseDouble(infor.getLng_start()));
//        toPointExra = new LatLonPoint(Double.parseDouble(infor.getLat_end()), Double.parseDouble(infor.getLat_end()));
//    }
//
//    private void startDriverUI() {
//        DrivePath drivePath = carRoutes.get(0).getDrivePath();
//        if (drivePath != null) {
//            DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
//                    this, aMap, drivePath, carRoutes.get(0).getFromPoint(),
//                    carRoutes.get(0).getToPoint(),null);
//            drivingRouteOverlay.removeFromMap();
//            drivingRouteOverlay.addToMap();
//            drivingRouteOverlay.zoomToSpan();
//            drivingRouteOverlay.setNodeIconVisibility(false);
//        }
//    }
//
//    private void initOrderInfor() {
//        if(infor.getStatus().equals("0")){
//            image_avatar.setVisibility(View.GONE);
//            tv_nickname.setVisibility(View.INVISIBLE);
//            img_sex.setVisibility(View.INVISIBLE);
//            tv_status.setText("等待接单");
//            tv_status.setTextColor(0xff25a4df);
//            tv_car_brand.setVisibility(View.INVISIBLE);
//            layout_order.setEnabled(false);
//            tv_car_no.setVisibility(View.INVISIBLE);
//            bt_ok.setVisibility(View.GONE);
//            bt_cancel.setVisibility(View.VISIBLE);
//            bt_cancel.setText("取消订单");
//        }else{
//            image_avatar.setVisibility(View.VISIBLE);
//            tv_nickname.setVisibility(View.VISIBLE);
//            img_sex.setVisibility(View.VISIBLE);
//            layout_order.setEnabled(true);
//            tv_car_brand.setVisibility(View.VISIBLE);
//            tv_car_no.setVisibility(View.VISIBLE);
//            bt_ok.setVisibility(View.VISIBLE);
////            switch (infor.getPayflag()) {
////                case "0": //未支付
////                    if ("0".equals(infor.getReachflag())) { //未送达
////                        if ("0".equals(infor.getStatusflag())) {
////                            bt_cancel.setVisibility(View.VISIBLE);
////                            bt_ok.setVisibility(View.VISIBLE);
////                        } else {
////                            bt_cancel.setVisibility(View.GONE);
////                            bt_ok.setVisibility(View.GONE);
////                        }
////                        bt_ok.setText("确认上车");
////                        bt_cancel.setText("取消订单");
////                        lng = XtomSharedPreferencesUtil.get(mContext, "lng");
////                        lat = XtomSharedPreferencesUtil.get(mContext, "lat");
////                        Double d_lng = Double.parseDouble(lng);
////                        Double d_lat = Double.parseDouble(lat);
////                        Double distance = BaseUtil.GetDistance(d_lat, d_lng,
////                                Double.parseDouble(isNull(infor.getLat())? "0.0" : infor.getLat()),
////                                Double.parseDouble(isNull(infor.getLng())? "0.0" : infor.getLng()));
////                        tv_status.setText("距离" + BaseUtil.transDistance(Float.parseFloat(String.valueOf(distance))));
////                        tv_status.setTextColor(0xff25a4df);
////                    } else { //已送达
////                        bt_ok.setVisibility(View.VISIBLE);
////                        bt_cancel.setVisibility(View.GONE);
////                        bt_ok.setText("去支付");
////                        tv_status.setText("到达目的地");
////                        tv_status.setTextColor(getResources().getColor(R.color.yellow));
////                    }
////                    break;
////                case "1": //已支付
////                    tv_status.setText("待评价");
////                    tv_status.setTextColor(getResources().getColor(R.color.yellow));
////                    bt_ok.setVisibility(View.GONE);
////                    bt_cancel.setVisibility(View.VISIBLE);
////                    bt_cancel.setText("去评价");
////                    break;
////                case "2": //已评价
////                    tv_status.setText("已完成");
////                    tv_status.setTextColor(getResources().getColor(R.color.yellow));
////                    layout_bottom.setVisibility(View.VISIBLE);
////                    bt_ok.setVisibility(View.GONE);
////                    bt_cancel.setVisibility(View.VISIBLE);
////                    bt_cancel.setText("删除订单");
////                    break;
////                case "3": //已取消
////                    tv_status.setText("已取消");
////                    tv_status.setTextColor(getResources().getColor(R.color.yellow));
////                    bt_ok.setVisibility(View.GONE);
////                    bt_cancel.setVisibility(View.VISIBLE);
////                    bt_cancel.setText("删除订单");
////                    break;
////            }
//        }
//        tv_startaddress.setText(infor.getStartaddress());
//        tv_endaddress.setText(infor.getEndaddress());
////        if ("0".equals(infor.getIs_pool()))
////            tv_money.setText(infor.getFailfee());
////        else
////            tv_money.setText(infor.getSuccessfee());
//
//        tv_nickname.setText(infor.getRealname());
//        if ("男".equals(infor.getSex()))
//            img_sex.setImageResource(R.mipmap.img_sex_boy);
//        else
//            img_sex.setImageResource(R.mipmap.img_sex_girl);
//        tv_car_brand.setText(infor.getCarbrand());
//        tv_car_no.setText(infor.getCarnumber());
////        if ("1".equals(infor.getIs_pool()))
////            tv_money.setText(infor.getSuccessfee());
////        else
////            tv_money.setText(infor.getFailfee());
//
//    }
//
//    private void checkLocation() {
//        if ((ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED)) {//判断是否拥有定位权限
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
//                    3);
//        } else {
//            init();
//        }
//    }
//
//    private void init() {
//        prepareLocation();
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            registerListener();
//            aMap.setLocationSource(this);// 设置定位监听
//            aMap.getUiSettings().setZoomControlsEnabled(false);
//            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//            aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//            geocoderSearch = new GeocodeSearch(this);
//            geocoderSearch.setOnGeocodeSearchListener(this);
//        }
//    }
//
//    private void prepareLocation() {
//        locationClient = new AMapLocationClient(getApplicationContext());
//        locationOption = new AMapLocationClientOption();
//        // 设置定位模式为高精度模式
//        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        // 设置定位监听
//        locationClient.setLocationListener(this);
//        // 设置是否需要显示地址信息
//        locationOption.setNeedAddress(true);
//        // 设置定位参数
//        locationOption.setOnceLocation(true);
//        /**
//         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
//         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
//         */
//        locationOption.setGpsFirst(false);
//        String strInterval = "1000";
//        if (!TextUtils.isEmpty(strInterval)) {
//            // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
//            locationOption.setInterval(Long.valueOf(strInterval));
//        }
//        locationClient.setLocationOption(locationOption);
//    }
//
//    /**
//     * 注册监听
//     */
//    private void registerListener() {
////        aMap.setOnMapClickListener(this);
//        aMap.setMyLocationEnabled(true);
//        aMap.setInfoWindowAdapter(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//        deactivate();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case CURRENT_TRIPS:
//                showProgressDialog("请稍后...");
//                break;
//        }
//    }
//
//    @Override
//    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case CURRENT_TRIPS:
//                cancelProgressDialog();
//                break;
//        }
//    }
//
//    @Override
//    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case CURRENT_TRIPS:
//                HemaArrayResult<CurrentTripsInfor> cResult = (HemaArrayResult<CurrentTripsInfor>) hemaBaseResult;
//                if (cResult.getObjects() != null && cResult.getObjects().size() > 0)
//                    infor = cResult.getObjects().get(0);
//                initData();
//                break;
//        }
//    }
//
//    @Override
//    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
//        BaseHttpInformation information = (BaseHttpInformation) hemaNetTask.getHttpInformation();
//        switch (information) {
//            case CURRENT_TRIPS:
//                showTextDialog("抱歉，获取当前行程失败");
//                break;
//        }
//    }
//
//    @Override
//    protected void findView() {
//        left = (ImageView) findViewById(R.id.title_btn_left);
//        right = (TextView) findViewById(R.id.title_btn_right);
//        title = (TextView) findViewById(R.id.title_text);
//
//        mapView = (MapView) findViewById(R.id.bmapView);
//        layout_bottom = (LinearLayout) findViewById(R.id.linearlayout);
//        layout_top = (LinearLayout) findViewById(R.id.layout_top);
//        layout_order = (RelativeLayout) findViewById(R.id.layout);
//        image_avatar = (RoundedImageView) findViewById(R.id.imageview);
//        tv_nickname = (TextView) findViewById(R.id.textview_1);
//        img_sex = (ImageView) findViewById(R.id.imageview_0);
//        tv_status = (TextView) findViewById(R.id.tv_status);
//        tv_startaddress = (TextView) findViewById(R.id.textview_2);
//        tv_endaddress = (TextView) findViewById(R.id.textview_3);
//        tv_car_brand = (TextView) findViewById(R.id.textview_4);
//        tv_car_no = (TextView) findViewById(R.id.textview_5);
//        tv_money = (TextView) findViewById(R.id.textview_8);
//        bt_cancel = (TextView) findViewById(R.id.textview_9);
//        bt_ok = (TextView) findViewById(R.id.textview_10);
//    }
//
//    @Override
//    protected void getExras() {
//    }
//
//    @Override
//    protected void setListener() {
//        title.setText("当前行程");
//        right.setVisibility(View.INVISIBLE);
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//        layout_bottom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isAvilible(mContext, "com.autonavi.minimap")) {
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.addCategory(Intent.CATEGORY_DEFAULT);
//                    Uri uri = Uri.parse("androidamap://navi?sourceApplication="+mContext.getResources().getString(R.string.app_name)
//                            +"&amp;poiname="+infor.getStartaddress()+"g&amp;lat="+infor.getLat_start()+"&amp;lon="+infor.getLng_start()
//                            +"&amp;dev=1&amp;style=2");
//                    intent.setData(uri);
//                    startActivity(intent);
//                } else {
//                    showTextDialog("您尚未安装高德地图或地图版本过低");
//                }
//            }
//        });
//
//
//        layout_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(mContext, OrderDetialInforActivity.class);
//                it.putExtra("id", infor.getId());
//                startActivityForResult(it, R.id.layout_1);
//            }
//        });
//        //取消订单
//        bt_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it = new Intent(mContext, CancelOrderActivity.class);
//                it.putExtra("id", infor.getId());
//                startActivityForResult(it, R.id.layout_1);
//            }
//        });
//
//        bt_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String value = ((TextView) view).getText().toString();
//                log_e("--------------- value = " + value);
//                if ("去支付".equals(value)) {
//                    Intent it = new Intent(mContext, ToPayActivity.class);
//                    it.putExtra("id", infor.getId());
////                    if ("1".equals(infor.getIs_pool()))
////                        it.putExtra("total_fee", infor.getSuccessfee());
////                    else
////                        it.putExtra("total_fee", infor.getFailfee());
//                    startActivityForResult(it, R.id.layout);
//                } else if (value.contains("确认上车")) {
//                   // getNetWorker().orderOperate(user.getToken(), "8", infor.getId(), "", "");
//                }
//            }
//        });
//    }
//
//    public static boolean isAvilible(Context context, String packageName) {
//        //获取packagemanager
//        final PackageManager packageManager = context.getPackageManager();
//        //获取所有已安装程序的包信息
//        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
//        //用于存储所有已安装程序的包名
//        List<String> packageNames = new ArrayList<>();
//        //从pinfo中将包名字逐一取出，压入pName list中
//        if (packageInfos != null) {
//            for (int i = 0; i < packageInfos.size(); i++) {
//                String packName = packageInfos.get(i).packageName;
//                packageNames.add(packName);
//            }
//        }
//        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
//        return packageNames.contains(packageName);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK)
//            return;
//        switch (requestCode) {
//            case R.id.layout:
//            case R.id.layout_1:
//                getNetWorker().currentTrips(user.getToken());
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation location) {
//        if (mListener != null && location != null) {
//            mListener.onLocationChanged(location);// 显示系统小蓝点
//            float bearing = aMap.getCameraPosition().bearing;
//            aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
//
//            Message msg = mHandler.obtainMessage();
//            msg.obj = location;
//            msg.what = LocationUtils.MSG_LOCATION_FINISH;
//            mHandler.sendMessage(msg);
//        }
//        deactivate();
//    }
//
//    Handler mHandler = new Handler() {
//        public void dispatchMessage(Message msg) {
//            switch (msg.what) {
//                //开始定位
//                case LocationUtils.MSG_LOCATION_START:
//                    break;
//                // 定位完成
//                case LocationUtils.MSG_LOCATION_FINISH:
//                    if (infor == null) {
//                        AMapLocation loc = (AMapLocation) msg.obj;
//                        lng = String.valueOf(loc.getLongitude());
//                        lat = String.valueOf(loc.getLatitude());
//                        XtomSharedPreferencesUtil.save(mContext, "lng", lng);
//                        XtomSharedPreferencesUtil.save(mContext, "lat", lat);
//                        latLonPoint = new LatLonPoint(loc.getLatitude(), loc.getLongitude());
//
//                        latLonPoint = new LatLonPoint(Double.parseDouble(lat), Double.parseDouble(lng));
//                        latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//                        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//                        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//
//                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latlng,
//                                15);
//                        aMap.moveCamera(update);
//                    }
//                    break;
//                //停止定位
//                case LocationUtils.MSG_LOCATION_STOP:
//                    break;
//            }
//        }
//    };
//
//    @Override
//    public void activate(OnLocationChangedListener listener) {
//        mListener = listener;
//        startLocation();
//    }
//
//    private void startLocation() {
//        // 启动定位
//        locationClient.startLocation();
//        mHandler.sendEmptyMessage(LocationUtils.MSG_LOCATION_START);
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//    }
//
//    private Marker marker;
//
//    /**
//     * 逆地理编码回调
//     */
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                RegeocodeAddress ad = result.getRegeocodeAddress();
//                map_title = isNull(ad.getBuilding()) ? ad.getFormatAddress() : ad.getBuilding();
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
//                start_city = ad.getCity();
//                if (marker != null)
//                    marker.remove();
//                marker = aMap.addMarker(new MarkerOptions()
//                        .position(latlng)
//                        .title(map_title)
//                        .icon(BitmapDescriptorFactory
//                                .fromBitmap(BitmapFactory.
//                                        decodeResource(getResources(), R.mipmap.img_marker))));
//                Message msg = new Message();
//                if (handler != null) {
//                    handler.sendMessage(msg);
//                }
//            } else {
//                XtomToastUtil.showShortToast(mContext, "抱歉，没有找到符合的结果");
//            }
//        } else if (rCode == 27) {
//            XtomToastUtil.showShortToast(mContext, "网络出现问题,请重新检查");
//        } else if (rCode == 32) {
//            XtomToastUtil.showShortToast(mContext, "应用key值,请重新检查");
//        } else {
//            XtomToastUtil.showShortToast(mContext, "出现其他类型的问题,请重新检查");
//        }
//    }
//
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            marker.showInfoWindow();
//        }
//    };
//
//    @Override
//    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//
//    }
//
//    @Override
//    public void onMapClick(LatLng latlng) {
//        this.latlng = latlng;
//        lng = String.valueOf(latlng.longitude);
//        lat = String.valueOf(latlng.latitude);
//        latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//    }
//
//    @Override
//    public View getInfoWindow(Marker marker) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_marker1, null);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it = new Intent(mContext, PublishInforActivity.class);
//                it.putExtra("start_lng", lng);
//                it.putExtra("start_lat", lat);
//                it.putExtra("start_position", map_title);
//                it.putExtra("start_city", start_city);
//
//                startActivity(it);
//            }
//        });
//        return view;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
//
//    @Override
//    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
//
//    }
//
//    @Override
//    public void onDriveRouteSearched(DriveRouteResult result, int i) {
//        if (result != null && result.getPaths() != null
//                && result.getPaths().size() > 0) {
//            List<DrivePath> paths = result.getPaths();
//            for (DrivePath drivePath : paths) {
//                long duration = drivePath.getDuration();
//                String time = "预计" + BaseUtil.transDuration(duration);
//                float dist = drivePath.getDistance();
//                String distance = BaseUtil.transDistance(dist);
//                String name = distance + "," + time;
//                carRoutes.add(new Route(name, time, distance, null, drivePath,
//                        null, fromPointExra, toPointExra));
//            }
//            startDriverUI();
//        } else {
//            log_i("驾车路线查询失败");
//        }
//    }
//
//    @Override
//    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {
//
//    }
//
//}
