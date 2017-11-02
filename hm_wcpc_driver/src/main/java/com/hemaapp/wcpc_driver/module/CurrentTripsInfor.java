package com.hemaapp.wcpc_driver.module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by wangyuxia on 2017/9/29.
 */
public class CurrentTripsInfor extends XtomObject{

    private String  id	; //主键id
    private String  startaddress; //	出发地
    private String  endaddress	; //目的地
    private String  startcity; //	出发城市名称
    private String  startcity_id; //	出发城市id
    private String endcity	; //目的地城市名称
    private String  endcity_id	; //目的地城市id
    private String  lng_start; //	出发地经度
    private String  lat_start	; //出发地纬度
    private String  lng_end	; //目的地经度
    private String  lat_end	; //目的地纬度
    private String begintime; //	出发时间
    private String  numbers; //	乘车人数
    private String  client_id	; //发布用户id
    private String  driver_id; //	接单司机id	0表示未接单
    private String carpoolflag	; //是否拼车	0-否 1-是
    private String  avatar; //	乘客头像
    private String  sex	; //乘客性别
    private String  nickname; //	乘客昵称
    private String takecount; //	乘客乘坐次数
    private String  username; //	乘客电话
    private String   realname; //	司机姓名
    private String   driver_avatar; //	司机头像
    private String  servicecount	; //司机服务次数
    private String driver_username	; //司机电话
    private String carbrand	; //车品牌
    private String carnumber	; //车牌号
    private String  status; //	行程状态	0：待派单
    private String cancel_reason; //	乘客取消原因
    private String total_fee;
    private String coupon_fee;
    private String allfee;
    private String together_client_arr; //	同行乘客列表	子列表
    private String replyflag1;//乘客是否评价	1：是，0：否
    private String remarks;
    private String  driver_fee;//	司机总费用
    private String conpon_fee;//	优惠券抵扣金额
    private String  replyflag2;//	司机是否评价	0：否，1：是
    private ArrayList<Client> clients=new ArrayList<>();
    public CurrentTripsInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                driver_fee = get(jsonObject, "driver_fee");
                conpon_fee = get(jsonObject, "conpon_fee");
                replyflag2 = get(jsonObject, "replyflag2");
                remarks = get(jsonObject, "remarks");
                replyflag1 = get(jsonObject, "replyflag1");
                total_fee = get(jsonObject, "total_fee");
                coupon_fee = get(jsonObject, "coupon_fee");
                allfee = get(jsonObject, "allfee");
                id = get(jsonObject, "id");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                startcity = get(jsonObject, "startcity");
                startcity_id = get(jsonObject, "startcity_id");
                endcity = get(jsonObject, "endcity");
                endcity_id = get(jsonObject, "endcity_id");
                lng_start = get(jsonObject, "lng_start");
                lat_start = get(jsonObject, "lat_start");
                lng_end = get(jsonObject, "lng_end");
                lat_end = get(jsonObject, "lat_end");
                status = get(jsonObject, "status");
                begintime = get(jsonObject, "begintime");
                numbers = get(jsonObject, "numbers");
                driver_id = get(jsonObject, "driver_id");
                client_id = get(jsonObject, "client_id");
                carpoolflag = get(jsonObject, "carpoolflag");
                nickname = get(jsonObject, "nickname");
                takecount = get(jsonObject, "takecount");
                carbrand = get(jsonObject, "carbrand");
                carnumber = get(jsonObject, "carnumber");
                username = get(jsonObject, "username");
                driver_avatar = get(jsonObject, "driver_avatar");
                realname = get(jsonObject, "realname");
                sex = get(jsonObject, "sex");
                avatar = get(jsonObject, "avatar");
                servicecount = get(jsonObject, "servicecount");
                driver_username = get(jsonObject, "driver_username");
                cancel_reason = get(jsonObject, "cancel_reason");
                together_client_arr = get(jsonObject, "together_client_arr");
                if (!jsonObject.isNull("together_client_arr")
                        && !isNull(jsonObject.getString("together_client_arr"))) {
                    JSONArray jsonList = jsonObject.getJSONArray("together_client_arr");
                    int size = jsonList.length();
                    for (int i = 0; i < size; i++) {
                        clients.add(new Client(jsonList.getJSONObject(i)));
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
        return "CurrentTripsInfor{" +
                "id='" + id + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", startcity='" + startcity + '\'' +
                ", startcity_id='" + startcity_id + '\'' +
                ", endcity='" + endcity + '\'' +
                ", endcity_id='" + endcity_id + '\'' +
                ", lng_start='" + lng_start + '\'' +
                ", lat_start='" + lat_start + '\'' +
                ", lng_end='" + lng_end + '\'' +
                ", lat_end='" + lat_end + '\'' +
                ", begintime='" + begintime + '\'' +
                ", numbers='" + numbers + '\'' +
                ", client_id='" + client_id + '\'' +
                ", driver_id='" + driver_id + '\'' +
                ", carpoolflag='" + carpoolflag + '\'' +
                ", avatar='" + avatar + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickname + '\'' +
                ", takecount='" + takecount + '\'' +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", driver_avatar='" + driver_avatar + '\'' +
                ", servicecount='" + servicecount + '\'' +
                ", driver_username='" + driver_username + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumber='" + carnumber + '\'' +
                ", status='" + status + '\'' +
                ", cancel_reason='" + cancel_reason + '\'' +
                ", total_fee='" + total_fee + '\'' +
                ", coupon_fee='" + coupon_fee + '\'' +
                ", allfee='" + allfee + '\'' +
                ", together_client_arr='" + together_client_arr + '\'' +
                ", replyflag1='" + replyflag1 + '\'' +
                ", remarks='" + remarks + '\'' +
                ", driver_fee='" + driver_fee + '\'' +
                ", conpon_fee='" + conpon_fee + '\'' +
                ", replyflag2='" + replyflag2 + '\'' +
                ", clients=" + clients +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getEndaddress() {
        return endaddress;
    }


    public String getLng_start() {
        return lng_start;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getDriver_fee() {
        return driver_fee;
    }

    public String getConpon_fee() {
        return conpon_fee;
    }

    public String getReplyflag2() {
        return replyflag2;
    }

    public String getReplyflag1() {
        return replyflag1;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public String getCoupon_fee() {
        return coupon_fee;
    }

    public String getAllfee() {
        return allfee;
    }

    public String getLat_start() {
        return lat_start;
    }

    public String getLng_end() {
        return lng_end;
    }


    public String getRealname() {
        return realname;
    }

    public String getSex() {
        return sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getLat_end() {
        return lat_end;
    }

    public String getStatus() {
        return status;
    }


    public String getDriver_id() {
        return driver_id;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public String getStartcity() {
        return startcity;
    }

    public String getStartcity_id() {
        return startcity_id;
    }

    public String getEndcity() {
        return endcity;
    }

    public String getEndcity_id() {
        return endcity_id;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getNumbers() {
        return numbers;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getCarpoolflag() {
        return carpoolflag;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTakecount() {
        return takecount;
    }

    public String getUsername() {
        return username;
    }

    public String getDriver_avatar() {
        return driver_avatar;
    }

    public String getServicecount() {
        return servicecount;
    }

    public String getDriver_username() {
        return driver_username;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public String getTogether_client_arr() {
        return together_client_arr;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
