package com.hemaapp.wcpc_driver.module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 */
public class Workstatus extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String current_driver_trip_id;//	司机当前行程id(不同于乘客行程的id系统)	0：没有行程，首页为空
//>0：为当前司机行程id
    private String allgetflag;//	当前行程的乘客是否都已上车	0：否，1：是（都上车之后才能显示接返程单按钮）
    private String  finalworkflag;//	返程接单开关状态	0：未开启（可开启按钮），1：已开启（显示接单地点）
    private String final_address;//	选择的接单地点	finalworkflag=1时开启时有效
    private String current_client_trip_list;//	司机当前行程的乘客列表	子列表
    private ArrayList<TripClient> clients=new ArrayList<>();
    public Workstatus(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                current_driver_trip_id = get(jsonObject, "current_driver_trip_id");
                allgetflag = get(jsonObject, "allgetflag");
                finalworkflag = get(jsonObject, "finalworkflag");
                final_address = get(jsonObject, "final_address");
                current_client_trip_list = get(jsonObject, "current_client_trip_list");
                if (!jsonObject.isNull("current_client_trip_list")
                        && !isNull(jsonObject.getString("current_client_trip_list"))) {
                    JSONArray jsonList = jsonObject.getJSONArray("current_client_trip_list");
                    int size = jsonList.length();
                    for (int i = 0; i < size; i++) {
                        clients.add(new TripClient(jsonList.getJSONObject(i)));
                    }
                }
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "Workstatus{" +
                "current_driver_trip_id='" + current_driver_trip_id + '\'' +
                ", allgetflag='" + allgetflag + '\'' +
                ", finalworkflag='" + finalworkflag + '\'' +
                ", final_address='" + final_address + '\'' +
                ", current_client_trip_list='" + current_client_trip_list + '\'' +
                ", clients=" + clients +
                '}';
    }

    public String getCurrent_driver_trip_id() {
        return current_driver_trip_id;
    }

    public String getAllgetflag() {
        return allgetflag;
    }

    public String getFinalworkflag() {
        return finalworkflag;
    }

    public String getFinal_address() {
        return final_address;
    }

    public String getCurrent_client_trip_list() {
        return current_client_trip_list;
    }

    public ArrayList<TripClient> getClients() {
        return clients;
    }
}

