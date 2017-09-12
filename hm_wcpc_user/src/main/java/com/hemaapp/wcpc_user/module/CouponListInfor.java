package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/17.
 */
public class CouponListInfor extends XtomObject {

    private String id; //主键id
    private String client_id; //用户主键id
    private String value; //面额
    private String dateline; //截止日期
    private String useflag; //是否使用
    private String dateflag; //是否过期
    private String regdate; //添加时间

    public CouponListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id =get(jsonObject, "id");
                client_id =get(jsonObject, "client_id");
                value =get(jsonObject, "value");
                dateline =get(jsonObject, "dateline");
                useflag =get(jsonObject, "useflag");
                dateflag =get(jsonObject, "dateflag");
                regdate =get(jsonObject, "regdate");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "CouponListInfor{" +
                "client_id='" + client_id + '\'' +
                ", id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", dateline='" + dateline + '\'' +
                ", useflag='" + useflag + '\'' +
                ", dateflag='" + dateflag + '\'' +
                ", regdate='" + regdate + '\'' +
                '}';
    }

    public String getClient_id() {
        return client_id;
    }

    public String getDateflag() {
        return dateflag;
    }

    public String getDateline() {
        return dateline;
    }

    public String getId() {
        return id;
    }

    public String getRegdate() {
        return regdate;
    }

    public String getUseflag() {
        return useflag;
    }

    public String getValue() {
        return value;
    }
}
