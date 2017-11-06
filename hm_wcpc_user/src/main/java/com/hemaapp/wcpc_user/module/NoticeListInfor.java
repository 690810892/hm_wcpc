package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/16.
 */
public class NoticeListInfor extends XtomObject {

    private String id; //通知主键id
    private String keytype; //上传操作类型 	1：系统消息  2：订单通知
    private String keyid; //关联模块主键id
    private String comtent; //通知内容
    private String client_id; //通知所属用户主键id
    private String from_id; //通知来源用户主键id
    private String looktype; //标记位
    private String regdate; //通知日期
    private String status; //

    public NoticeListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                keytype = get(jsonObject, "keytype");
                keyid = get(jsonObject, "keyid");
                comtent = get(jsonObject, "content");
                client_id = get(jsonObject, "client_id");
                from_id = get(jsonObject, "from_id");
                looktype = get(jsonObject, "looktype");
                regdate = get(jsonObject, "regdate");
                status = get(jsonObject, "status");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "NoticeListInfor{" +
                "id='" + id + '\'' +
                ", keytype='" + keytype + '\'' +
                ", keyid='" + keyid + '\'' +
                ", comtent='" + comtent + '\'' +
                ", client_id='" + client_id + '\'' +
                ", from_id='" + from_id + '\'' +
                ", looktype='" + looktype + '\'' +
                ", regdate='" + regdate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String getClient_id() {
        return client_id;
    }

    public String getComtent() {
        return comtent;
    }

    public String getFrom_id() {
        return from_id;
    }

    public String getId() {
        return id;
    }

    public String getKeyid() {
        return keyid;
    }

    public String getKeytype() {
        return keytype;
    }

    public String getLooktype() {
        return looktype;
    }

    public String getRegdate() {
        return regdate;
    }

    public String getStatus() {
        return status;
    }

    public void setLooktype(String looktype) {
        this.looktype = looktype;
    }
}
