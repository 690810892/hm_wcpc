package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/19.
 */
public class MyTripsInfor extends XtomObject {
    private String id; //主键id
    private String startaddress; //出发地
    private String endaddress; //目的地
    private String begintime; //出发时间
    private String is_pool; //拼车成功	0-否 1-是
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String reachflag; //送达状态	0-否 1-是
    private String status; //状态 0-未完成 1-已完成
    private String driver_id; //司机主键id 	根据此字段判断是否被抢单
    private String tripflag; //出行标志	0-未出行 1-已出行

    public MyTripsInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id =get(jsonObject, "id");
                startaddress =get(jsonObject, "startaddress");
                endaddress =get(jsonObject, "endaddress");
                begintime =get(jsonObject, "begintime");
                is_pool =get(jsonObject, "is_pool");
                successfee =get(jsonObject, "successfee");
                failfee =get(jsonObject, "failfee");
                reachflag =get(jsonObject, "reachflag");
                status = get(jsonObject, "status");
                driver_id = get(jsonObject, "driver_id");
                tripflag = get(jsonObject, "tripflag");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "MyTripsInfor{" +
                "begintime='" + begintime + '\'' +
                ", id='" + id + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", reachflag='" + reachflag + '\'' +
                ", status='" + status + '\'' +
                ", driver_id='" + driver_id + '\'' +
                ", tripflag='" + tripflag + '\'' +
                '}';
    }

    public String getTripflag() {
        return tripflag;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public String getFailfee() {
        return failfee;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getIs_pool() {
        return is_pool;
    }

    public String getReachflag() {
        return reachflag;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getSuccessfee() {
        return successfee;
    }
}
