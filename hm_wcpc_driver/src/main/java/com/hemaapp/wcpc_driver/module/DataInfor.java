package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/20.
 */
public class DataInfor extends XtomObject {

    private String id; //主键ID
    private String name; //名称
    private boolean isChecked =false;

    public DataInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                name = get(jsonObject, "name");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    public DataInfor(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataInfor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    public String getId() {
        return id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getName() {
        return name;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
