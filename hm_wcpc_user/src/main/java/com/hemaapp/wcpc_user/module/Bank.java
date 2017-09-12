package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/18.
 */
public class Bank extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String id;// 主键id
    private String name;// 银行名称

    public Bank(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                id = get(jsonObject, "id");
                name = get(jsonObject, "name");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "Bank [id=" + id + ", name=" + name + "]";
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}

