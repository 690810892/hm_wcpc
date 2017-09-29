package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by wangyuxia on 2017/9/29.
 */
public class CurrentTripsInfor extends XtomObject{

    private String id; //
    private String startaddress; //
    private String endaddress; //
    private String successfee; //
    private String failfee; //
    private String lng_start; //
    private String lat_start; //
    private String lng_end; //
    private String lat_end; //
    private String status; //
    private String order_id; //
    private String is_pool; //
    private String driver_id; //
    private String carbrand; //
    private String carnumber; //
    private String lng; //
    private String lat; //

    public CurrentTripsInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                lng_start = get(jsonObject, "lng_start");
                lat_start = get(jsonObject, "lat_start");
                lng_end = get(jsonObject, "lng_end");
                lat_end = get(jsonObject, "lat_end");
                status = get(jsonObject, "status");
                order_id = get(jsonObject, "order_id");
                is_pool = get(jsonObject, "is_pool");
                driver_id = get(jsonObject, "driver_id");
                carbrand = get(jsonObject, "carbrand");
                carnumber = get(jsonObject, "carnumber");
                lng = get(jsonObject, "lng");
                lat = get(jsonObject, "lat");

                log_e(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "CurrentTripsInfor{" +
                "id='" + id + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", lng_start='" + lng_start + '\'' +
                ", lat_start='" + lat_start + '\'' +
                ", lng_end='" + lng_end + '\'' +
                ", lat_end='" + lat_end + '\'' +
                ", status='" + status + '\'' +
                ", order_id='" + order_id + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", driver_id='" + driver_id + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumber='" + carnumber + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public String getFailfee() {
        return failfee;
    }

    public String getLng_start() {
        return lng_start;
    }

    public String getLat_start() {
        return lat_start;
    }

    public String getLng_end() {
        return lng_end;
    }

    public String getLat_end() {
        return lat_end;
    }

    public String getStatus() {
        return status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getIs_pool() {
        return is_pool;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }
}
