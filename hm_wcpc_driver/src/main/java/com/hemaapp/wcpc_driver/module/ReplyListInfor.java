package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/13.
 */
public class ReplyListInfor extends XtomObject {
    private String id; //主键id
    private String content; //内容
    private String point; //评分
    private String regdate; //添加时间

    public ReplyListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                content = get(jsonObject, "content");
                point = get(jsonObject, "point");
                regdate = get(jsonObject, "regdate");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "ReplyListInfor{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", point='" + point + '\'' +
                ", regdate='" + regdate + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getPoint() {
        return point;
    }

    public String getRegdate() {
        return regdate;
    }
}
