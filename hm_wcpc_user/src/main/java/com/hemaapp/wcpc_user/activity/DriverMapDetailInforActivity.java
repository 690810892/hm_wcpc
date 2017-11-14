//package com.hemaapp.wcpc_user.activity;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.route.BusRouteResult;
//import com.amap.api.services.route.DrivePath;
//import com.amap.api.services.route.DriveRouteResult;
//import com.amap.api.services.route.RouteSearch;
//import com.amap.api.services.route.WalkRouteResult;
//import com.hemaapp.hm_FrameWork.HemaNetTask;
//import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
//import com.hemaapp.wcpc_user.BaseActivity;
//import com.hemaapp.wcpc_user.BaseUtil;
//import com.hemaapp.wcpc_user.R;
//import com.hemaapp.wcpc_user.module.Route;
//import com.hemaapp.wcpc_user.view.DrivingRoute;
//
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.List;
//
//import xtom.frame.image.load.XtomImageTask;
//import xtom.frame.util.XtomImageUtil;
//
///**
// * Created by WangYuxia on 2016/5/13.
// */
//public class DriverMapDetailInforActivity extends BaseActivity
//        implements RouteSearch.OnRouteSearchListener, AMap.InfoWindowAdapter{
//
//    private ImageView left;
//    private TextView title;
//    private TextView right;
//
//    private MapView mapView;
//    private AMap aMap;
//
//    private LatLonPoint latLonPoint; //点击地点
//    private RouteSearch routeSearch;
//    private ArrayList<Route> carRoutes = new ArrayList<>();
//
//    private LatLonPoint toPointExra;
//    private LatLonPoint fromPointExra;
//    private String start_lng, start_lat, end_lng, end_lat;
//    private String address, lng, lat, avatar;
//
//    private ImageView imageView;
//    private Marker marker;
//    public Bitmap bitmap;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_drivermapdetail);
//        super.onCreate(savedInstanceState);
//        mapView.onCreate(savedInstanceState);// 此方法必须重写
//        aMap = mapView.getMap();
//        aMap.setInfoWindowAdapter(this);
//        routeSearch = new RouteSearch(this);
//        routeSearch.setRouteSearchListener(this);
//        fromPointExra = new LatLonPoint(Double.parseDouble(start_lat), Double.parseDouble(start_lng));
//        toPointExra = new LatLonPoint(Double.parseDouble(end_lat), Double.parseDouble(end_lng));
//        latLonPoint = new LatLonPoint(Double.parseDouble(lat), Double.parseDouble(lng));
//        toSearch();
//        addMarker();
//    }
//
//    private void addMarker(){
//        try {
//            URL url = new URL(avatar);
//            ImageTask task = new ImageTask(imageView, url, this);
//            imageWorker.loadImage(task);
//        } catch (Exception e) {
//            imageView.setImageResource(R.drawable.bg_marker);
//        }
//        drawMarkerOnMap();
//        marker.showInfoWindow();
//    }
//
//    private Marker  drawMarkerOnMap(){
//        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//        if(aMap  != null && latLng != null){
//            marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1)
//                    .position(latLng)
//                    .title(address)
//                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
//            return marker;
//        }
//        return null;
//    }
//
//    private void toSearch(){
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
//                fromPointExra, toPointExra);
//        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
//                RouteSearch.DrivingDefault, null, null, "");
//        routeSearch.calculateDriveRouteAsyn(query);
//    }
//
//    private void startDriverUI(){
//        DrivePath drivePath = carRoutes.get(0).getDrivePath();
//        if (drivePath != null) {
////            aMap.clear();// 清理地图上的所有覆盖物
//            DrivingRouteOverlay  drivingRouteOverlay = new DrivingRouteOverlay(
//                    this, aMap, drivePath, carRoutes.get(0).getFromPoint(),
//                    carRoutes.get(0).getToPoint());
//            drivingRouteOverlay.removeFromMap();
//            drivingRouteOverlay.addToMap();
//            drivingRouteOverlay.zoomToSpan();
//            drivingRouteOverlay.setNodeIconVisibility(false);
//        }
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
//        imageView = (ImageView) findViewById(R.id.markerview);
//    }
//
//    @Override
//    protected void getExras() {
//        start_lat = mIntent.getStringExtra("start_lat");
//        start_lng = mIntent.getStringExtra("start_lng");
//        end_lat = mIntent.getStringExtra("end_lat");
//        end_lng = mIntent.getStringExtra("end_lng");
//        lng = mIntent.getStringExtra("lng");
//        lat = mIntent.getStringExtra("lat");
//        address = mIntent.getStringExtra("address");
//        avatar = mIntent.getStringExtra("avatar");
//    }
//
//    @Override
//    protected void setListener() {
//        title.setText("车主行程详情");
//        right.setVisibility(View.INVISIBLE);
//        left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
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
//    public View getInfoWindow(Marker marker) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_marker, null);
//        TextView textView  = (TextView) view.findViewById(R.id.textview);
//        textView.setText(address);
//        return view;
//    }
//
//    @Override
//    public View getInfoContents(Marker marker) {
//        return null;
//    }
//
//    private class ImageTask extends XtomImageTask {
//
//        DriverMapDetailInforActivity context;
//
//        private ImageTask(ImageView imageView, URL url, DriverMapDetailInforActivity context) {
//            super(imageView, url, context);
//            this.context = context;
//        }
//
//        @Override
//        public void success() {
//            super.success();
//        }
//
//        @Override
//        public void setBitmap(Bitmap bitmap) {
//            bitmap = XtomImageUtil.getRoundedCornerBitmap(bitmap, 1000);
//            super.setBitmap(bitmap);
//            context.bitmap = bitmap;
//        }
//    }
//
//}
