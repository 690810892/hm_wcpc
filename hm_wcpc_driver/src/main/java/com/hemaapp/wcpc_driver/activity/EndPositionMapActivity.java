//package com.hemaapp.wcpc_driver.activity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
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
//import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
//import com.hemaapp.wcpc_driver.BaseActivity;
//import com.hemaapp.wcpc_driver.BaseUtil;
//import com.hemaapp.wcpc_driver.R;
//import com.hemaapp.wcpc_driver.module.Route;
//import com.hemaapp.wcpc_driver.view.DrivingRoute;
//import com.hemaapp.wcpc_driver.view.LocationUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import xtom.frame.util.XtomSharedPreferencesUtil;
//import xtom.frame.util.XtomToastUtil;
//
///**
// * Created by WangYuxia on 2016/5/10.
// * flag = 0:市内行程
// * flag = 1：跨城行程
// *
// */
//public class EndPositionMapActivity extends BaseActivity implements LocationSource,
//        AMapLocationListener, AMap.OnMapClickListener, RouteSearch.OnRouteSearchListener,
//        AMap.OnMapLoadedListener, GeocodeSearch.OnGeocodeSearchListener {
//
//    private ImageView left;
//    private TextView title;
//    private TextView right;
//
//    private MapView mapView;
//    private AMap aMap;
//    private LinearLayout layout_search;
//    private TextView text_search;
//
//    private LocationSource.OnLocationChangedListener mListener;
//
//    private AMapLocationClient locationClient = null;
//    private AMapLocationClientOption locationOption = null;
//    private String citycode;
//
//    private String data, start_city;
//    private GeocodeSearch geocoderSearch;
//    private LatLonPoint latLonPoint; //点击地点
//    private RouteSearch routeSearch;
//    private ArrayList<Route> carRoutes = new ArrayList<>();
//    private String start_lng, start_lat, end_lng, end_lat, distance;
//    private LatLonPoint toPointExra;
//    private LatLonPoint fromPointExra;
//    private LatLng start_latlng, end_latlng;
//
//    private Marker startMarker, endMarker;
//    private boolean isFrist = true;
//    private int flag;
//    private String city;
//    private boolean isClicked =false;
//
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            XtomToastUtil.showLongToast(EndPositionMapActivity.this, data);
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_endposition);
//        super.onCreate(savedInstanceState);
//        mapView.onCreate(savedInstanceState);// 此方法必须重写
//        aMap = mapView.getMap();
//        city = XtomSharedPreferencesUtil.get(mContext, "city");
//        checkLocation();
//    }
//
//    private void init(){
//        routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        fromPointExra = new LatLonPoint(Double.parseDouble(start_lat), Double.parseDouble(start_lng));
//        prepareLocation();
//        initStart();
//        initEnd();
//    }
//
//    //检测是否有定位权限
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
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//
//        if (requestCode == 3) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED ||
//                    grantResults[1] != PackageManager.PERMISSION_GRANTED)//未获得定位权限
//            {
//                showTextDialog("没有定位权限，请添加后重试");
//                title.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        finish();
//                    }
//                }, 1500);
//            } else {
//                init();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    private void prepareLocation(){
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
//    Handler mHandler = new Handler() {
//        public void dispatchMessage(Message msg) {
//            switch (msg.what) {
//                //开始定位
//                case LocationUtils.MSG_LOCATION_START:
//                    log_i("正在定位...");
//                    break;
//                // 定位完成
//                case LocationUtils.MSG_LOCATION_FINISH:
//                    AMapLocation loc = (AMapLocation) msg.obj;
//                    end_lng = String.valueOf(loc.getLongitude());
//                    end_lat = String.valueOf(loc.getLatitude());
//                    citycode = loc.getCity();
//                    if(isNull(citycode)){
//                        citycode = loc.getProvince();
//                    }
//                    data = loc.getAddress();
//                    latLonPoint = new LatLonPoint(loc.getLatitude(), loc.getLongitude());
//
//                    end_latlng = new LatLng(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(end_latlng,
//                            15);
//                    aMap.moveCamera(update);
//                    break;
//                //停止定位
//                case LocationUtils.MSG_LOCATION_STOP:
//                    log_i("定位停止...");
//                    break;
//                default:
//                    break;
//            }
//        };
//    };
//
//    private void initEnd(){
//        aMap.setOnMapClickListener(this);
//        aMap.setMyLocationEnabled(true);
//
//        if(!isNull(end_lat) && !isNull(end_lng)){
//            startSearch();
//            isFrist = false;
//        }else{
//            if(isNull(end_lat) && isNull(end_lng))
//                isFrist = true;
//        }
//        geocoderSearch = new GeocodeSearch(this);
//        geocoderSearch.setOnGeocodeSearchListener(this);
//    }
//
//    private void startSearch(){
//        end_latlng = new LatLng(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//        endMarker = aMap.addMarker(new MarkerOptions()
//                .position(end_latlng)
//                .title("终点")
//                .icon(BitmapDescriptorFactory
//                        .fromBitmap(BitmapFactory.
//                                decodeResource(getResources(), R.mipmap.img_endposition_logo))));
//
//        toPointExra = new LatLonPoint(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                fromPointExra, toPointExra);
//        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
//                RouteSearch.DrivingDefault, null, null, "");
//        routeSearch.calculateDriveRouteAsyn(query);
//    }
//
//    private void initStart(){
//        start_latlng = new LatLng(Double.parseDouble(start_lat), Double.parseDouble(start_lng));
//        startMarker = aMap.addMarker(new MarkerOptions()
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
//    private void startDriverUI(){
//        DrivePath drivePath = carRoutes.get(0).getDrivePath();
//        if (drivePath != null) {
//            aMap.clear();// 清理地图上的所有覆盖物
//            distance = carRoutes.get(0).getDistance();
//            DrivingRoute drivingRouteOverlay = new DrivingRoute(
//                    this, aMap, drivePath, carRoutes.get(0).getFromPoint(),
//                    carRoutes.get(0).getToPoint());
//            drivingRouteOverlay.removeFromMap();
//            drivingRouteOverlay.addToMap();
//            drivingRouteOverlay.zoomToSpan();
//            drivingRouteOverlay.setNodeIconVisibility(false);
//        }
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mapView.onPause();
//        deactivate();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }
//
//    @Override
//    protected void callBeforeDataBack(HemaNetTask hemaNetTask) {
//    }
//
//    @Override
//    protected void callAfterDataBack(HemaNetTask hemaNetTask) {
//    }
//
//    @Override
//    protected void callBackForServerSuccess(HemaNetTask hemaNetTask, HemaBaseResult hemaBaseResult) {
//    }
//
//    @Override
//    protected void callBackForGetDataFailed(HemaNetTask hemaNetTask, int i) {
//    }
//
//    @Override
//    protected void findView() {
//        left = (ImageView) findViewById(R.id.title_btn_left);
//        right = (TextView) findViewById(R.id.title_btn_right);
//        title = (TextView) findViewById(R.id.title_text);
//
//        mapView = (MapView) findViewById(R.id.bmapView);
//        layout_search = (LinearLayout) findViewById(R.id.linearlayout);
//        text_search = (TextView) findViewById(R.id.textview);
//    }
//
//    @Override
//    protected void getExras() {
//        start_lat = mIntent.getStringExtra("start_lat");
//        start_lng = mIntent.getStringExtra("start_lng");
//        end_lat = mIntent.getStringExtra("end_lat");
//        end_lng = mIntent.getStringExtra("end_lng");
//        flag = mIntent.getIntExtra("keytype", 0);
//        start_city = mIntent.getStringExtra("city");
//    }
//
//    @Override
//    protected void setListener() {
//        title.setText("目的地");
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        right.setText("确定");
//        right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (end_latlng == null) {
//                    XtomToastUtil.showShortToast(mContext, "请点击地图选择地点!");
//                    return;
//                }
//
//                if(isClicked){
//                    mIntent.putExtra("data", data);
//                    mIntent.putExtra("lng", String.valueOf(end_latlng.longitude));
//                    mIntent.putExtra("lat", String.valueOf(end_latlng.latitude));
//                    mIntent.putExtra("distance", distance);
//                    setResult(RESULT_OK, mIntent);
//                    finish();
//                }else{
//                    finish();
//                }
//            }
//        });
//
//        layout_search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(flag == 0){
//                    Intent it = new Intent(mContext, SelectStartPositionActivity.class);
//                    if(isNull(citycode))
//                        citycode = XtomSharedPreferencesUtil.get(mContext, "city");
//                    it.putExtra("citycode", citycode);
//                    startActivityForResult(it, R.id.linearlayout);
//                }else{
//                    Intent it = new Intent(mContext, SelectEndPositionActivity.class);
//                    if(isNull(citycode))
//                        citycode = XtomSharedPreferencesUtil.get(mContext, "city");
//                    it.putExtra("citycode", citycode);
//                    startActivityForResult(it, R.id.linearlayout);
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode != RESULT_OK)
//            return;
//        switch (requestCode){
//            case R.id.linearlayout:
//                isClicked = true;
//                String name = data.getStringExtra("name");
//                city = data.getStringExtra("city");
//                text_search.setText(name);
//                end_lat = String.valueOf(data.getDoubleExtra("lat", 0.0));
//                end_lng = String.valueOf(data.getDoubleExtra("lng", 0.0));
//                toPointExra = new LatLonPoint(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//                latLonPoint = new LatLonPoint(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0));
//
//                end_latlng = new LatLng(data.getDoubleExtra("lat", 0.0), data.getDoubleExtra("lng", 0.0));
//                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//                geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
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
//    @Override
//    public void activate(OnLocationChangedListener listener) {
//        mListener = listener;
//        startLocation();
//    }
//
//    private void startLocation(){
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
//    @Override
//    public void onMapClick(LatLng latlng) {
//        isClicked = true;
//        this.end_latlng = latlng;
//        end_lat = String.valueOf(latlng.latitude);
//        end_lng = String.valueOf(latlng.longitude);
//        toPointExra = new LatLonPoint(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//        latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
//        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//    }
//
//    @Override
//    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
//    }
//
//    @Override
//    public void onDriveRouteSearched(DriveRouteResult result, int i) {
//        if (result != null && result.getPaths() != null
//                && result.getPaths().size() > 0) {
//            List<DrivePath> paths = result.getPaths();
//            if(carRoutes != null && carRoutes.size() >0)
//                carRoutes.clear();
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
//    }
//
//    @Override
//    public void onMapLoaded() {
//        if(!isNull(end_lng) && !isNull(end_lat)){
//            startDriverUI();
//        }
//    }
//
//    @Override
//    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                RegeocodeAddress address = result.getRegeocodeAddress();
//                city = address.getCity();
//
//                if(flag == 0 && !isNull(city) && !start_city.equals(city)){
//                    showTextDialog("抱歉，您发布的是市内行程，请重新选择");
//                    return;
//                }
//
//                if(flag == 1 && !isNull(city) && start_city.equals(city)){
//                    showTextDialog("抱歉，您发布的是跨城行程，请重新选择");
//                    return;
//                }
//
//                citycode = address.getCity();
//                if(isNull(citycode)){
//                    citycode = address.getProvince();
//                }
//
//                data = address.getFormatAddress();
//                if(!isNull(address.getBuilding()))
//                    data = data + address.getBuilding();
//                else if(!isNull(address.getStreetNumber().getStreet())){
//                    data = data+address.getStreetNumber().getNumber();
//                }
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(end_latlng, 15));
//                if (endMarker == null) {
//                    endMarker = aMap.addMarker(new MarkerOptions()
//                            .position(end_latlng)
//                            .title(data)
//                            .icon(BitmapDescriptorFactory
//                                    .fromBitmap(BitmapFactory.
//                                            decodeResource(getResources(), R.mipmap.img_endposition_logo))));
//                } else {
//                    endMarker.remove();
//                    endMarker = aMap.addMarker(new MarkerOptions()
//                            .position(end_latlng)
//                            .title(data)
//                            .icon(BitmapDescriptorFactory
//                                    .fromBitmap(BitmapFactory.
//                                            decodeResource(getResources(), R.mipmap.img_endposition_logo))));
//                }
//                Message msg = new Message();
//                if (handler != null) {
//                    handler.sendMessage(msg);
//                }
//                startSearch();
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
//    @Override
//    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
//    }
//}
