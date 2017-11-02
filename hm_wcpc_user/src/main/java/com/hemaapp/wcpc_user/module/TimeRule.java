package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class TimeRule extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String   current_time;//		经度
    private String  time1_begin;//		可下单开始时间（下单时间）
    private String  time1_end;//		可下单结束时间（下单时间）
    private String  time2_begin	;//	可拼车的开始时间（出发时间）
    private String  time2_end;//		可拼车的结束时间（出发时间）
    public TimeRule(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                current_time = get(jsonObject, "current_time");
                time1_begin = get(jsonObject, "time1_begin");
                time1_end = get(jsonObject, "time1_end");
                time2_begin = get(jsonObject, "time2_begin");
                time2_end = get(jsonObject, "time2_end");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }


    @Override
    public String toString() {
        return "TimeRule{" +
                "current_time='" + current_time + '\'' +
                ", time1_begin='" + time1_begin + '\'' +
                ", time1_end='" + time1_end + '\'' +
                ", time2_begin='" + time2_begin + '\'' +
                ", time2_end='" + time2_end + '\'' +
                '}';
    }

    public String getCurrent_time() {
        return current_time;
    }

    public String getTime1_begin() {
        return time1_begin;
    }

    public String getTime1_end() {
        return time1_end;
    }

    public String getTime2_begin() {
        return time2_begin;
    }

    public String getTime2_end() {
        return time2_end;
    }
}

