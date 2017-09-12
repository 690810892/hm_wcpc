package com.hemaapp.wcpc_user.view;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.hemaapp.wcpc_user.R;

import java.util.List;

/**
 * Created by WangYuxia on 2016/5/11.
 */
public class DrivingRoute extends DrivingRouteOverlay {

    private Context context;

    public DrivingRoute(Context context, AMap aMap, DrivePath drivePath, LatLonPoint latLonPoint, LatLonPoint latLonPoint1) {
        super(context, aMap, drivePath, latLonPoint, latLonPoint1);
        this.context = context;
    }

    public DrivingRoute(Context context, AMap aMap, DrivePath drivePath, LatLonPoint latLonPoint, LatLonPoint latLonPoint1, List<LatLonPoint> list) {
        super(context, aMap, drivePath, latLonPoint, latLonPoint1, list);
        this.context = context;
    }

    @Override
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.img_startposition_logo);
    }

    @Override
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.img_endposition_logo);
    }

    @Override
    protected int getDriveColor() {
        return context.getResources().getColor(R.color.yellow);
    }
}
