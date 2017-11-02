package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class ClientAdd extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String  token;//		正式token	注册成功后，服务器返回一个正式token
    private String   coupon_count;//		系统赠送的代金券数
    private String   coupon_value;//		代金券每一张的金额
    private String  coupon_dateline	;//	代金券有效期
    public ClientAdd(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                token = get(jsonObject, "token");
                coupon_count = get(jsonObject, "coupon_count");
                coupon_value = get(jsonObject, "coupon_value");
                coupon_dateline = get(jsonObject, "coupon_dateline");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }


    @Override
    public String toString() {
        return "ClientAdd{" +
                "token='" + token + '\'' +
                ", coupon_count='" + coupon_count + '\'' +
                ", coupon_value='" + coupon_value + '\'' +
                ", coupon_dateline='" + coupon_dateline + '\'' +
                '}';
    }

    public String getToken() {
        return token;
    }

    public String getCoupon_count() {
        return coupon_count;
    }

    public String getCoupon_value() {
        return coupon_value;
    }

    public String getCoupon_dateline() {
        return coupon_dateline;
    }
}

