package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 *
 */
public class FeeRule extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String  id;//	主键id
    private String   city1;//	城市1	即客户端传来的city
    private String  city2;//	城市2
    private String  price;//	价格
    public FeeRule(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                id = get(jsonObject, "id");
                city1 = get(jsonObject, "city1");
                city2 = get(jsonObject, "city2");
                price = get(jsonObject, "price");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }


    @Override
    public String toString() {
        return "FeeRule{" +
                "id='" + id + '\'' +
                ", city1='" + city1 + '\'' +
                ", city2='" + city2 + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getCity1() {
        return city1;
    }

    public String getCity2() {
        return city2;
    }

    public String getPrice() {
        return price;
    }
}

