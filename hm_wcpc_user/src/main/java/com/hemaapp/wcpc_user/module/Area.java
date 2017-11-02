package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class Area extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String   id;//	服务区域主键id
    private String  addprice;//	加减价格	正为加，负为减
    private String name	;//区域名称	如：济南机场
    private String lnglat;//	范围经纬度串	格式：lng,lat;lng1,lat1;lng2,lat2
    public Area(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                id = get(jsonObject, "id");
                name = get(jsonObject, "name");
                addprice = get(jsonObject, "addprice");
                lnglat = get(jsonObject, "lnglat");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "Area{" +
                "id='" + id + '\'' +
                ", addprice='" + addprice + '\'' +
                ", name='" + name + '\'' +
                ", lnglat='" + lnglat + '\'' +
                '}';
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getAddprice() {
        return addprice;
    }

    public String getLnglat() {
        return lnglat;
    }
}

