package com.hemaapp.wcpc_driver.module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/23.
 */
public class OrderDetailInfor extends XtomObject {

    private String id; //订单主键id
    private String order_no; //订单号
    private String client_id; //司机主键id
    private String realname; //司机姓名
    private String sex; //司机姓名
    private String avatar; //头像
    private String mobile; //司机电话
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String is_pool; //拼车成功
    private String numbers; //乘车人数
    private String takecount; //乘车次数
    private String payflag; //订单状态
    private String startaddress; //出发地
    private String endaddress; //目的地
    private String begintime; //出发时间
    private String lng_start; //出发经纬度
    private String lat_start;
    private String lng_end; //目的地经纬度
    private String lat_end;
    private String reason_str; //取消原因
    private String content; //其它原因
    private String regdate; //添加时间
    private ArrayList<ReplyItems> replyItems; //订单评论

    private String thankfee; //感谢费
    private String carpoolflag; //是否拼车
    private String remarks; //备注
    private String grabflag; //抢单标志
    private String lng, lat, address;
    private String statusflag; //状态标志  0-未接乘客 1-已接到乘客 2-乘客确认上车

    public OrderDetailInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                order_no = get(jsonObject, "order_no");
                client_id = get(jsonObject, "client_id");
                realname = get(jsonObject, "realname");
                sex = get(jsonObject, "sex");
                avatar = get(jsonObject, "avatar");
                mobile = get(jsonObject, "mobile");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                is_pool = get(jsonObject, "is_pool");
                numbers = get(jsonObject, "numbers");
                takecount = get(jsonObject, "takecount");
                payflag = get(jsonObject, "payflag");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                begintime = get(jsonObject, "begintime");
                lng_start = get(jsonObject, "lng_start");
                lat_start = get(jsonObject, "lat_start");
                lng_end = get(jsonObject, "lng_end");
                lat_end = get(jsonObject, "lat_end");
                reason_str = get(jsonObject, "reason_str");
                content = get(jsonObject, "content");
                regdate = get(jsonObject, "regdate");
                thankfee = get(jsonObject, "thankfee");
                carpoolflag = get(jsonObject, "carpoolflag");
                remarks = get(jsonObject, "remarks");
                grabflag = get(jsonObject, "grabflag");
                lng = get(jsonObject, "lng");
                lat = get(jsonObject, "lat");
                address = get(jsonObject, "address");
                statusflag = get(jsonObject, "statusflag");

                if (!jsonObject.isNull("replyItems")&&
                        !isNull(jsonObject.getString("replyItems"))) {
                    JSONArray jsonList = jsonObject.getJSONArray("replyItems");
                    int size = jsonList.length();
                    replyItems = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        replyItems.add(new ReplyItems(jsonList.getJSONObject(i)));
                    }
                }

            } catch (JSONException e) {
                 throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "OrderDetailInfor{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", order_no='" + order_no + '\'' +
                ", client_id='" + client_id + '\'' +
                ", realname='" + realname + '\'' +
                ", sex='" + sex + '\'' +
                ", avatar='" + avatar + '\'' +
                ", mobile='" + mobile + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", numbers='" + numbers + '\'' +
                ", takecount='" + takecount + '\'' +
                ", payflag='" + payflag + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", begintime='" + begintime + '\'' +
                ", lng_start='" + lng_start + '\'' +
                ", lat_start='" + lat_start + '\'' +
                ", lng_end='" + lng_end + '\'' +
                ", lat_end='" + lat_end + '\'' +
                ", reason_str='" + reason_str + '\'' +
                ", content='" + content + '\'' +
                ", regdate='" + regdate + '\'' +
                ", replyItems=" + replyItems +
                ", thankfee='" + thankfee + '\'' +
                ", carpoolflag='" + carpoolflag + '\'' +
                ", remarks='" + remarks + '\'' +
                ", grabflag='" + grabflag + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", statusflag='" + statusflag + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public String getFailfee() {
        return failfee;
    }

    public String getId() {
        return id;
    }

    public String getIs_pool() {
        return is_pool;
    }

    public String getMobile() {
        return mobile;
    }

    public String getOrder_no() {
        return order_no;
    }

    public String getPayflag() {
        return payflag;
    }

    public String getStatusflag() {
        return statusflag;
    }

    public String getRealname() {
        return realname;
    }

    public String getReason_str() {
        return reason_str;
    }

    public String getRegdate() {
        return regdate;
    }

    public ArrayList<ReplyItems> getReplyItems() {
        return replyItems;
    }

    public String getSex() {
        return sex;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getCarpoolflag() {
        return carpoolflag;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public String getGrabflag() {
        return grabflag;
    }

    public String getLat_end() {
        return lat_end;
    }

    public String getLat_start() {
        return lat_start;
    }

    public String getLng_end() {
        return lng_end;
    }

    public String getLng_start() {
        return lng_start;
    }

    public String getNumbers() {
        return numbers;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getTakecount() {
        return takecount;
    }

    public String getThankfee() {
        return thankfee;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public void setStatusflag(String statusflag) {
        this.statusflag = statusflag;
    }
}
