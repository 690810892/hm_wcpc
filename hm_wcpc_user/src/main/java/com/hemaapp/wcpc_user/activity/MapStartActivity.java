package com.hemaapp.wcpc_user.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.BaseActivity;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.CircularAnim;
import com.hemaapp.wcpc_user.EventBusModel;
import com.hemaapp.wcpc_user.R;
import com.hemaapp.wcpc_user.hm_WcpcUserApplication;
import com.hemaapp.wcpc_user.module.Area;
import com.hemaapp.wcpc_user.module.User;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomToastUtil;

/**
 * 起点地图
 */
public class MapStartActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnCameraChangeListener {

    @BindView(R.id.title_btn_left)
    ImageView titleBtnLeft;
    @BindView(R.id.title_btn_right)
    TextView titleBtnRight;
    @BindView(R.id.title_text)
    TextView titleText;
    @BindView(R.id.bmapView)
    MapView mapView;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.lv_search)
    LinearLayout lvSearch;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private User user;
    private String token = "";
    private ArrayList<Area> areas = new ArrayList<>();
    private AMap aMap;
    private Polygon polygon;
    private Marker marker;
    private String move_lat, move_lng, addPrice, myAddress, city, title, lng, lat;
    private ArrayList<Polygon> polygons = new ArrayList<>();
    private ArrayList<String> prices = new ArrayList<>();
    private boolean inArea = false;
    private GeocodeSearch geocoderSearch;
    private LatLng latlng;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private OnLocationChangedListener mListener;
    Marker screenMarker = null;
    private boolean Loc = false, isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_map_start);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        user = hm_WcpcUserApplication.getInstance().getUser();
        if (user == null)
            token = "";
        else
            token = user.getToken();
        init();
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    addMarkerInScreenCenter();
                    setUpMap();
                }
            });
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);
            //setUpMap();
        }
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter() {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

    }

    private void setUpMap() {
        aMap.setOnCameraChangeListener(this);
        for (Area area : areas) {
            // 绘制一个长方形
            PolygonOptions pOption = new PolygonOptions();
            String[] str = area.getLnglat().split(";");
            for (int i = 0; i < str.length; i++) {
                String lat = str[i].split(",")[1];
                String lng = str[i].split(",")[0];
                pOption.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
            }
            Polygon polygon = aMap.addPolygon(pOption.strokeWidth(4)
                    .strokeColor(Color.argb(50, 1, 1, 1))
                    .fillColor(0x20F8F64C));
            polygons.add(polygon);
            prices.add(area.getAddprice());
            if (isNull(move_lat)) {
                move_lat = str[0].split(",")[1];
                move_lng = str[0].split(",")[0];
            }
        }
        log_e("moveCamera111------------------------------");
//        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(move_lat),
//                Double.parseDouble(move_lng)), 15));
        prepareLocation();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

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
        locationOption.setOnceLocation(true);
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

    public void onEventMainThread(EventBusModel event) {
        switch (event.getType()) {
            case REFRESH_BLOG_LIST:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                break;
            case TRIPS_SAVEOPERATE:
                showProgressDialog("请稍后");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForServerSuccess(HemaNetTask netTask,
                                            HemaBaseResult baseResult) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                break;
            case TRIPS_SAVEOPERATE:
                break;
            default:
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
                showTextDialog(baseResult.getMsg());
                break;
            default:
                break;
        }
    }

    @Override
    protected void callBackForGetDataFailed(HemaNetTask netTask, int failedType) {
        // TODO Auto-generated method stub
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case TRIPS_LIST:
                showTextDialog("加载失败");
                break;
            default:
                break;
        }
    }

    @Override
    protected void findView() {
    }

    @Override
    protected void getExras() {
        areas.addAll((Collection<? extends Area>) mIntent.getSerializableExtra("areas"));
        city = mIntent.getStringExtra("city");
        title = mIntent.getStringExtra("title");
        String center_point = mIntent.getStringExtra("center_city");
        if (!isNull(center_point)) {
            move_lng = center_point.split(",")[0];
            move_lat = center_point.split(",")[1];
        }
    }

    @Override
    protected void setListener() {
        titleText.setText(title);
        titleBtnRight.setText("确定");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case 1:
                Double la = data.getDoubleExtra("lat", 0);
                Double ln = data.getDoubleExtra("lng", 0);
                lng = ln + "";
                lat = la + "";
                myAddress = data.getStringExtra("name");
                latlng = new LatLng(la, ln);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
//                if (marker != null) {
//                    marker.remove();
//                }
//                marker = aMap.addMarker(new MarkerOptions().position(latlng));
                boolean b1 = false;
                for (int i = 0; i < polygons.size(); i++) {
                    if (polygons.get(i).contains(latlng)) {
                        b1 = true;
                        addPrice = prices.get(i);
                        break;
                    }
                }
                inArea = b1;
                if (inArea) {
                    tvSearch.setText(myAddress);
                } else {
                    showTextDialog("该区域暂未开通");
                }
                break;
        }
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.lv_search})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                if (isNull(myAddress)) {
                    showTextDialog("请" + title);
                    break;
                }
                if (inArea) {
                    mIntent.putExtra("address", myAddress);
                    mIntent.putExtra("lng", lng);
                    mIntent.putExtra("lat", lat);
                    mIntent.putExtra("addPrice", addPrice);
                    setResult(RESULT_OK, mIntent);
                    finish();
                } else {
                    showTextDialog("您选择的地点暂未开通，请重新选择！");
                }
                break;
            case R.id.lv_search:
                Intent it = new Intent(mContext, SearchActivity.class);
                it.putExtra("citycode", city);
                if (title.equals("选择出发地"))
                    it.putExtra("hint", "从哪出发");
                else
                    it.putExtra("hint", "你要去哪");
                startActivityForResult(it, 1);
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
//        if (marker != null) {
//            marker.remove();
//        }
//        marker = aMap.addMarker(new MarkerOptions().position(latLng));
//        boolean b1 = false;
//        for (int i = 0; i < polygons.size(); i++) {
//            if (polygons.get(i).contains(latLng)) {
//                b1 = true;
//                addPrice = prices.get(i);
//            }
//        }
//        inArea = b1;
//        if (inArea) {
//            showProgressDialog("正在定位...");
//            latlng = latLng;
//            lat = latLng.latitude + "";
//            lng = latLng.longitude + "";
//            LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
//            RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
//                    GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//            geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
//        } else {
//            showTextDialog("该区域暂未开通");
//        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        //cancelProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                RegeocodeAddress address = result.getRegeocodeAddress();
                if (address.getAois() != null && address.getAois().size() > 0)
                    myAddress = address.getCity() + address.getAois().get(0).getAoiName();
                else
                    myAddress = address.getFormatAddress();
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                tvSearch.setText(myAddress);
                tvSearch.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        CircularAnim.show(lvSearch).go();
                    }
                }, 500);

            } else {
                XtomToastUtil.showShortToast(mContext, "抱歉，没有找到符合的结果");
            }
        } else if (rCode == 27) {
            XtomToastUtil.showShortToast(mContext, "网络出现问题,请重新检查");
        } else if (rCode == 32) {
            XtomToastUtil.showShortToast(mContext, "应用key值,请重新检查");
        } else {
            XtomToastUtil.showShortToast(mContext, "出现其他类型的问题,请重新检查");
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        log_e("执行定位------------------------------");
        if (mListener != null && location != null) {
            //mListener.onLocationChanged(location);// 显示系统小蓝点
            deactivate();
            if (location.getCity().equals(city)) {//当前城市和要选择的城市相同才定位
                if (marker != null) {
                    marker.remove();
                }
                LatLng latlng0 = new LatLng(location.getLatitude(), location.getLongitude());
                marker = aMap.addMarker(new MarkerOptions()
                        .position(latlng0)
                        .title("起点")
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.
                                        decodeResource(getResources(), R.mipmap.loc_my))));
              //  marker = aMap.addMarker(new MarkerOptions().position(latlng0));
                boolean b1 = false;
                for (int i = 0; i < polygons.size(); i++) {
                    if (polygons.get(i).contains(latlng0)) {
                        b1 = true;
                        addPrice = prices.get(i);
                    }
                }
                inArea = b1;
                if (inArea) {
                    latlng = latlng0;
                    lng = String.valueOf(location.getLongitude());
                    lat = String.valueOf(location.getLatitude());
                    myAddress = location.getCity() + location.getAoiName();
                    Loc = true;
                    log_e("moveCamera222------------------------------");
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
                    tvSearch.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvSearch.setText(myAddress);
                        }
                    }, 500);

                } else {
                    log_e("moveCamera333------------------------------");
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(move_lat),
                            Double.parseDouble(move_lng)), 15));
                    //showTextDialog("该区域暂未开通");
                }
            } else {
                log_e("moveCamera444------------------------------");
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(move_lat),
                        Double.parseDouble(move_lng)), 15));
            }
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        log_e("activate------------------------------");
        mListener = listener;
        startLocation();
    }

    private void startLocation() {
        // 启动定位
        locationClient.startLocation();
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {


    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isFirst) {
            isFirst = false;
        } else {
            if (!Loc) {
                log_e("onCameraChangeFinish------------------------------");
                tvSearch.setText("");
                myAddress = "";
                CircularAnim.hide(lvSearch).go();
                progressBar.setVisibility(View.VISIBLE);
//屏幕中心的Marker跳动
                startJumpAnimation();
                LatLng latLng = cameraPosition.target;
                boolean b1 = false;
                for (int i = 0; i < polygons.size(); i++) {
                    if (polygons.get(i).contains(latLng)) {
                        b1 = true;
                        addPrice = prices.get(i);
                    }
                }
                inArea = b1;
                if (inArea) {
                    latlng = latLng;
                    lat = latLng.latitude + "";
                    lng = latLng.longitude + "";
                    LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                    RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                            GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                    geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
                } else {
                    showTextDialog("该区域暂未开通");
                    log_e("lat=====" + latLng.latitude + "");
                    log_e("lng=====" + latLng.longitude + "");
                }
            } else {
                Loc = false;
            }
        }
    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = screenMarker.getPosition();
            Point point = aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this, 50);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if (input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f) * (1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(500);
            //设置动画
            screenMarker.setAnimation(animation);
            //开始动画
            screenMarker.startAnimation();

        } else {
            Log.e("amap", "screenMarker is null");
        }
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
