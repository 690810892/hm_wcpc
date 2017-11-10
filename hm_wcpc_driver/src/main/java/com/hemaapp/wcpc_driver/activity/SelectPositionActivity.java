package com.hemaapp.wcpc_driver.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
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
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.hm_FrameWork.view.RoundedImageView;
import com.hemaapp.wcpc_driver.BaseActivity;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.EventBusConfig;
import com.hemaapp.wcpc_driver.EventBusModel;
import com.hemaapp.wcpc_driver.R;
import com.hemaapp.wcpc_driver.hm_WcpcDriverApplication;
import com.hemaapp.wcpc_driver.module.TripClient;
import com.hemaapp.wcpc_driver.module.User;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomToastUtil;

/**
 */
public class SelectPositionActivity extends BaseActivity implements LocationSource, AMapLocationListener, AMap.OnMarkerClickListener {

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
    @BindView(R.id.iv_avatar)
    RoundedImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_sex)
    ImageView ivSex;
    @BindView(R.id.tv_car)
    TextView tvCar;
    @BindView(R.id.lv_driver)
    LinearLayout lvDriver;
    @BindView(R.id.iv_tel)
    ImageView ivTel;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.lv_bottom)
    LinearLayout lvBottom;
    @BindView(R.id.lv_warn)
    LinearLayout lvWarn;
    private User user;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private ArrayList<TripClient> models = new ArrayList<>();
    private TripClient model;
    AlphaAnimation appearAnimation;
    private PopupWindow mWindow;
    private ViewGroup mViewGroup;
    private TextView content1;
    private TextView content2;
    private TextView ok;
    private TextView cancel;
    private int flag;//1:开始接返程单   0：首页查看地图
    private String allgetflag;//当前行程的乘客是否都已上车
    private String lng, lat;//定位我的位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_select_position);
        ButterKnife.bind(this);
        super.onCreate(savedInstanceState);
        appearAnimation = new AlphaAnimation(0, 1);
        appearAnimation.setDuration(500);
        mapView.onCreate(savedInstanceState);
        user = hm_WcpcDriverApplication.getInstance().getUser();
        lvBottom.setVisibility(View.GONE);
        aMap = mapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));//默认显示级别
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.setOnMarkerClickListener(this);
    }

    private void setData() {
        aMap.clear();
        for (TripClient m : models) {
            LatLng ll;
            if (allgetflag.equals("1"))
                ll = new LatLng(Double.valueOf(m.getLat_end()), Double.valueOf(m.getLng_end()));
            else
                ll = new LatLng(Double.valueOf(m.getLat_start()), Double.valueOf(m.getLng_start()));
            Marker marker;
            if (m.isSelect()) {
                marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.
                                        decodeResource(getResources(), R.mipmap.img_marker_2))).position(ll));
            } else {
                marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory
                                .fromBitmap(BitmapFactory.
                                        decodeResource(getResources(), R.mipmap.img_marker1))).position(ll));
            }
            marker.setObject(m);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
                    13);
            aMap.moveCamera(update);
        }
        LatLng ll = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
        Marker marker;
        marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.
                                decodeResource(getResources(), R.mipmap.img_marker_my))).position(ll));
        if (models.size() == 0) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,
                    13);
            aMap.moveCamera(update);
        }
    }

    /**
     * 方法必须重写
     */
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
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void callBeforeDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case OPEN_WORKSTATUS:
                showProgressDialog("请稍后...");
                break;
            default:
                break;
        }
    }

    @Override
    protected void callAfterDataBack(HemaNetTask netTask) {
        BaseHttpInformation information = (BaseHttpInformation) netTask
                .getHttpInformation();
        switch (information) {
            case OPEN_WORKSTATUS:
                cancelProgressDialog();
                break;
            default:
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
            case OPEN_WORKSTATUS:
                EventBus.getDefault().post(new EventBusModel(EventBusConfig.REFRESH_BLOG_LIST));
                showTextDialog("设置成功");
                titleBtnRight.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
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
            case OPEN_WORKSTATUS:
                showTextDialog(baseResult.getMsg());
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
            case OPEN_WORKSTATUS:
                showTextDialog("设置失败");
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
        models.addAll((Collection<? extends TripClient>) mIntent.getSerializableExtra("client"));
        flag = mIntent.getIntExtra("flag", 0);
        allgetflag = mIntent.getStringExtra("allgetflag");
    }

    @Override
    protected void setListener() {
        if (flag == 0) {
            titleText.setText("地图");
            titleBtnRight.setVisibility(View.GONE);
            lvWarn.setVisibility(View.GONE);
        } else {
            titleText.setText("选择接单地点");
            titleBtnRight.setVisibility(View.VISIBLE);
            titleBtnRight.setText("确定");
            lvWarn.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.title_btn_left, R.id.title_btn_right, R.id.iv_tel, R.id.lv_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_btn_left:
                finish();
                break;
            case R.id.title_btn_right:
                if (model == null) {
                    showTextDialog("请选择最后下车乘客为返程接单地点");
                    break;
                }
                getNetWorker().openWorkStatus(user.getToken(), model.getLng_end(), model.getLat_end(), model.getEndaddress());
                break;
            case R.id.iv_tel:
                toMakePhone();
                break;
            case R.id.lv_bottom:
                if (layoutTop.getVisibility() == View.GONE) {
                    showTextDialog("请选择导航目的地");
                } else {
                    if (isAvilible(mContext, "com.autonavi.minimap")) {
                        String add, lng, lat;
                        if (allgetflag.equals("1")) {
                            add = model.getEndaddress();
                            lng = model.getLng_end();
                            lat = model.getLat_end();
                        } else {
                            add = model.getStartaddress();
                            lng = model.getLng_start();
                            lat = model.getLat_start();
                        }
//                        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
//                                .append("小叫车(司机)");
//                        if (!TextUtils.isEmpty(add)) {
//                            stringBuffer.append("&poiname=").append(add);
//                        }
//                        stringBuffer.append("&lat=").append(lat)
//                                .append("&lon=").append(lng)
//                                .append("&dev=").append("1")
//                                .append("&style=").append("2");
//
//                        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuffer.toString()));
//                        intent.setPackage("com.autonavi.minimap");
//                        mContext.startActivity(intent);
                        StringBuffer stringBuffer = new StringBuffer("androidamap://keywordNavi?sourceApplication=")
                                .append("小叫车(司机)");
                        if (!TextUtils.isEmpty(add)) {
                            stringBuffer.append("&keyword=").append(add);
                        }
                        stringBuffer
                                .append("&style=").append("2");
                        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
                        intent.setPackage("com.autonavi.minimap");
                        mContext.startActivity(intent);
                    } else {
                        showTextDialog("您尚未安装高德地图或地图版本过低");
                    }
                }
                break;
        }
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
        content1.setText("拨打乘客电话");
        content2.setText(model.getUsername());
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
                        + model.getUsername()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
//                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                float bearing = aMap.getCameraPosition().bearing;
                aMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
                deactivate();
                lng = Double.toString(aMapLocation.getLongitude());
                lat = Double.toString(aMapLocation.getLatitude());
                XtomSharedPreferencesUtil.save(mContext, "lat", lat.toString());
                XtomSharedPreferencesUtil.save(mContext, "lng", lng.toString());
                LatLng ll = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                //aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 12));
                setData();
                log_d("定位成功，lng=" + lng);
            } else {
                log_d("定位失败，重新定位");
                XtomToastUtil.showShortToast(mContext, "定位失败，重新定位");
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        model = (TripClient) marker.getObject();
        if (model != null) {
            if (layoutTop.getVisibility() == View.GONE) {
                lvWarn.setVisibility(View.GONE);
                layoutTop.startAnimation(appearAnimation);
                layoutTop.setVisibility(View.VISIBLE);
                lvBottom.setVisibility(View.VISIBLE);
            }
            if (model.isSelect()) {
                return false;
            }
            for (TripClient m : models) {
                m.setSelect(false);
            }
            model.setSelect(true);
            ImageLoader.getInstance().displayImage(model.getAvatar(), ivAvatar, hm_WcpcDriverApplication.getInstance()
                    .getOptions(R.mipmap.default_user));
            ivAvatar.setCornerRadius(100);
            tvName.setText(model.getNickname());
            if ("男".equals(model.getSex()))
                ivSex.setImageResource(R.mipmap.img_sex_boy);
            else
                ivSex.setImageResource(R.mipmap.img_sex_girl);
            tvCar.setText(model.getRemarks());
            tvAddress.setText(model.getEndaddress());
            setData();
        } else {
            layoutTop.setVisibility(View.GONE);
            lvBottom.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}

