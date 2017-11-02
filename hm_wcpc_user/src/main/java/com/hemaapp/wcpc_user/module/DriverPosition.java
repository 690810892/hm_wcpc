package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class DriverPosition extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String lng	;//
    private String lat;//
    public DriverPosition(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                lng = get(jsonObject, "lng");
                lat = get(jsonObject, "lat");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "DriverPosition{" +
                "lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}

