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
    private String  is_reg;//	是否为新注册记录	1：是，0：否
    private String  password;//	密码
    public ClientAdd(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                token = get(jsonObject, "token");
                coupon_count = get(jsonObject, "coupon_count");
                coupon_value = get(jsonObject, "coupon_value");
                coupon_dateline = get(jsonObject, "coupon_dateline");
                is_reg = get(jsonObject, "is_reg");
                password = get(jsonObject, "password");
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
                ", is_reg='" + is_reg + '\'' +
                ", password='" + password + '\'' +
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

    public String getIs_reg() {
        return is_reg;
    }

    public String getPassword() {
        return password;
    }

    public String getCoupon_dateline() {
        return coupon_dateline;
    }
}

