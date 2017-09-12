package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/17.
 */
public class WeiXinPay extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String appid; //公众账号ID
    private String partnerid; //商户号
    private String prepayid; //预支付交易会话ID
    private String ppackage; //扩展字段
    private String noncestr; //随机字符串
    private String timestamp; //时间戳
    private String sign; //签名

    public WeiXinPay(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                appid = get(jsonObject, "appid");
                partnerid = get(jsonObject, "partnerid");
                prepayid = get(jsonObject, "prepayid");
                ppackage = get(jsonObject, "package");
                noncestr = get(jsonObject, "noncestr");
                timestamp = get(jsonObject, "timestamp");
                sign = get(jsonObject, "sign");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "WeiXinPay{" +
                "appid='" + appid + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", prepayid='" + prepayid + '\'' +
                ", ppackage='" + ppackage + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }

    public String getAppid() {
        return appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPpackage() {
        return ppackage;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSign() {
        return sign;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
