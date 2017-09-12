package com.hemaapp.wcpc_user.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.WalkPath;

import xtom.frame.XtomObject;

/**
 * Created by WangYuxia on 2016/5/10.
 */
public class Route extends XtomObject implements Parcelable {
    private String name;
    private String time;
    private String distance;
    private BusPath busPath;
    private DrivePath drivePath;
    private WalkPath walkPath;
    private LatLonPoint fromPoint;
    private LatLonPoint toPoint;

    public Route(String name, String time, String distance, BusPath busPath,
                 DrivePath drivePath, WalkPath walkPath, LatLonPoint fromPoint,
                 LatLonPoint toPoint) {
        super();
        this.name = name;
        this.time = time;
        this.distance = distance;
        this.busPath = busPath;
        this.drivePath = drivePath;
        this.walkPath = walkPath;
        this.fromPoint = fromPoint;
        this.toPoint = toPoint;
    }

    @Override
    public String toString() {
        return "Route [name=" + name + ", time=" + time + ", distance="
                + distance + ", busPath=" + busPath + ", drivePath="
                + drivePath + ", walkPath=" + walkPath + ", fromPoint="
                + fromPoint + ", toPoint=" + toPoint + "]";
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * @return the distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     * @return the busPath
     */
    public BusPath getBusPath() {
        return busPath;
    }

    /**
     * @return the drivePath
     */
    public DrivePath getDrivePath() {
        return drivePath;
    }

    /**
     * @return the walkPath
     */
    public WalkPath getWalkPath() {
        return walkPath;
    }

    /**
     * @return the fromPoint
     */
    public LatLonPoint getFromPoint() {
        return fromPoint;
    }

    /**
     * @return the toPoint
     */
    public LatLonPoint getToPoint() {
        return toPoint;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(time);
        dest.writeString(distance);
        dest.writeParcelable(busPath, flags);
        dest.writeParcelable(drivePath, flags);
        dest.writeParcelable(walkPath, flags);
        dest.writeParcelable(fromPoint, flags);
        dest.writeParcelable(toPoint, flags);
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    private Route(Parcel in) {
        name = in.readString();
        time = in.readString();
        distance = in.readString();
        busPath = in.readParcelable(BusPath.class.getClassLoader());
        drivePath = in.readParcelable(DrivePath.class.getClassLoader());
        walkPath = in.readParcelable(WalkPath.class.getClassLoader());
        fromPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
        toPoint = in.readParcelable(LatLonPoint.class.getClassLoader());
    }

}

