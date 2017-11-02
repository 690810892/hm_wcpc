package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class Client extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String   avatar;//	服务区域主键id
    private String  numbers;//	加减价格	正为加，负为减
    public Client(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                avatar = get(jsonObject, "avatar");
                numbers = get(jsonObject, "numbers");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    public Client(String avatar, String numbers) {
        this.avatar = avatar;
        this.numbers = numbers;
    }

    @Override
    public String toString() {
        return "Client{" +
                "avatar='" + avatar + '\'' +
                ", numbers='" + numbers + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNumbers() {
        return numbers;
    }
}

