package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 */
public class FeeAccountInfor extends XtomObject {
    private String id; //主键id
    private String keytype; //记录类型
    private String name; //名称
    private String amount; //数额
    private String regdate; //添加时间

    public FeeAccountInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id =get(jsonObject, "id");
                keytype =get(jsonObject, "keytype");
                name =get(jsonObject, "name");
                amount =get(jsonObject, "amount");
                regdate =get(jsonObject, "regdate");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "FeeAccountInfor{" +
                "amount='" + amount + '\'' +
                ", id='" + id + '\'' +
                ", keytype='" + keytype + '\'' +
                ", name='" + name + '\'' +
                ", regdate='" + regdate + '\'' +
                '}';
    }

    public String getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getKeytype() {
        return keytype;
    }

    public String getName() {
        return name;
    }

    public String getRegdate() {
        return regdate;
    }
}
