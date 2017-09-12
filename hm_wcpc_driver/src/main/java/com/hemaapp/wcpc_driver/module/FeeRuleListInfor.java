package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/9.
 */
public class FeeRuleListInfor extends XtomObject {

    private String id; //主键id
    private String numbers; //乘车人数
    private String mileage; //里程数
    private String successfee; //成功费用
    private String successoverfee; //超过规定里程数成功费用
    private String failfee; //失败费用
    private String failoverfee; //超过规定里程数失败费用

    private boolean isChecked = false;

    public FeeRuleListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                numbers = get(jsonObject, "numbers");
                mileage = get(jsonObject, "mileage");
                successfee = get(jsonObject, "successfee");
                successoverfee = get(jsonObject, "successoverfee");
                failfee = get(jsonObject, "failfee");
                failoverfee = get(jsonObject, "failoverfee");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "FeeRuleListInfor{" +
                "failfee='" + failfee + '\'' +
                ", id='" + id + '\'' +
                ", numbers='" + numbers + '\'' +
                ", mileage='" + mileage + '\'' +
                ", successfee='" + successfee + '\'' +
                ", successoverfee='" + successoverfee + '\'' +
                ", failoverfee='" + failoverfee + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getMileage() {
        return mileage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getNumbers() {
        return numbers;
    }

    public String getFailfee() {
        return failfee;
    }

    public String getFailoverfee() {
        return failoverfee;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public String getSuccessoverfee() {
        return successoverfee;
    }
}
