package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class TripListInfor extends XtomObject {
    private String id; //主键id
    private String startaddress; //出发地
    private String endaddress; //目的地
    private String begintime; //出发时间
    private String numbers; //乘车人数
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String client_id; //发布用户id
    private String avatar; //头像
    private String sex; //性别
    private String realname; //姓名
    private String takecount; //乘车次数
    private String driver_id; //司机主键id
    private String remainnum; //剩余座位
    private String carbrand; //车品牌
    private String carnumber; //车牌号
    private String lng_start;
    private String lat_start;
    private String lng_end;
    private String lat_end;

    private String lng, lat, address, maxnumbers, mobile, franchisee_id, keytype, distance;

    public TripListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                begintime = get(jsonObject, "begintime");
                numbers = get(jsonObject, "numbers");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                client_id = get(jsonObject, "client_id");
                avatar = get(jsonObject, "avatar");
                sex = get(jsonObject, "sex");
                realname = get(jsonObject, "realname");
                takecount = get(jsonObject, "takecount");
                driver_id = get(jsonObject, "driver_id");
                remainnum = get(jsonObject, "remainnum");
                carbrand = get(jsonObject, "carbrand");
                carnumber = get(jsonObject, "carnumber");
                lng_start = get(jsonObject, "lng_start");
                lat_start = get(jsonObject, "lat_start");
                lng_end = get(jsonObject, "lng_end");
                lat_end = get(jsonObject, "lat_end");
                lng = get(jsonObject, "lng");
                lat = get(jsonObject, "lat");
                address = get(jsonObject, "address");
                maxnumbers = get(jsonObject, "maxnumbers");
                mobile = get(jsonObject, "mobile");
                franchisee_id = get(jsonObject, "franchisee_id");
                keytype = get(jsonObject, "keytype");
                distance = get(jsonObject, "distance");


                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "TripListInfor{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", begintime='" + begintime + '\'' +
                ", numbers='" + numbers + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", client_id='" + client_id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex='" + sex + '\'' +
                ", realname='" + realname + '\'' +
                ", takecount='" + takecount + '\'' +
                ", driver_id='" + driver_id + '\'' +
                ", remainnum='" + remainnum + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumber='" + carnumber + '\'' +
                ", lng_start='" + lng_start + '\'' +
                ", lat_start='" + lat_start + '\'' +
                ", lng_end='" + lng_end + '\'' +
                ", lat_end='" + lat_end + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", maxnumbers='" + maxnumbers + '\'' +
                ", mobile='" + mobile + '\'' +
                ", franchisee_id='" + franchisee_id + '\'' +
                ", keytype='" + keytype + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public String getKeytype() {
        return keytype;
    }

    public String getFailfee() {
        return failfee;
    }

    public String getId() {
        return id;
    }

    public String getDistance() {
        return distance;
    }

    public String getNumbers() {
        return numbers;
    }

    public String getRealname() {
        return realname;
    }

    public String getRemainnum() {
        return remainnum;
    }

    public String getSex() {
        return sex;
    }

    public String getFranchisee_id() {
        return franchisee_id;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getLat_end() {
        return lat_end;
    }

    public String getLat_start() {
        return lat_start;
    }

    public String getLng_end() {
        return lng_end;
    }

    public String getLng_start() {
        return lng_start;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public String getTakecount() {
        return takecount;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMaxnumbers() {
        return maxnumbers;
    }
}
