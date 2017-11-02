package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class TripClient extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String avatar;//		乘客头像
    private String  nickname;//		乘客姓名
    private String  sex	;//	乘客性别
    private String  username;//		乘客电话
    private String remarks;//		备注
    private String startaddress;//		出发地
    private String endaddress;//		目的地
    private String lng_start;//		出发地经度
    private String  lat_start;//		出发地纬度
    private String lng_end;//		目的地经度
    private String lat_end	;//	目的地纬度
    private boolean isSelect=false;
    public TripClient(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                avatar = get(jsonObject, "avatar");
                nickname = get(jsonObject, "nickname");
                sex = get(jsonObject, "sex");
                username = get(jsonObject, "username");
                remarks = get(jsonObject, "remarks");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                lng_start = get(jsonObject, "lng_start");
                lat_start = get(jsonObject, "lat_start");
                lng_end = get(jsonObject, "lng_end");
                lat_end = get(jsonObject, "lat_end");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    public TripClient(String avatar, String nickname, String sex, String username, String remarks,
                      String startaddress, String endaddress, String lng_start, String lat_start, String lng_end, String lat_end, boolean isSelect) {
        this.avatar = avatar;
        this.nickname = nickname;
        this.sex = sex;
        this.username = username;
        this.remarks = remarks;
        this.startaddress = startaddress;
        this.endaddress = endaddress;
        this.lng_start = lng_start;
        this.lat_start = lat_start;
        this.lng_end = lng_end;
        this.lat_end = lat_end;
        this.isSelect = isSelect;
    }

    @Override
    public String toString() {
        return "TripClient{" +
                "avatar='" + avatar + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", username='" + username + '\'' +
                ", remarks='" + remarks + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", lng_start='" + lng_start + '\'' +
                ", lat_start='" + lat_start + '\'' +
                ", lng_end='" + lng_end + '\'' +
                ", lat_end='" + lat_end + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSex() {
        return sex;
    }

    public String getUsername() {
        return username;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getEndaddress() {
        return endaddress;
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

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getLat_end() {
        return lat_end;
    }
}

