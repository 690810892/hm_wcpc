package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/12.
 */
public class FeeCalculationInfor extends XtomObject {
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用

    public FeeCalculationInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "FeeCalculationInfor{" +
                "failfee='" + failfee + '\'' +
                ", successfee='" + successfee + '\'' +
                '}';
    }

    public String getFailfee() {
        return failfee;
    }

    public String getSuccessfee() {
        return successfee;
    }
}
