package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/19.
 */
public class OrderListInfor extends XtomObject {

    private String id; //主键id
    private String order_no;//订单号
    private String client_id; //用户主键id
    private String realname; //用户姓名
    private String sex; //用户性别
    private String mobile; //用户电话
    private String takecount; //乘车次数
    private String avatar; //头像
    private String avatarbig; //头像大图
    private String startaddress; //出发地
    private String endaddress; //目的地
    private String begintime; //出发时间
    private String numbers; //乘车人数
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String is_pool; //拼车成功 0-否 1-是
    private String payflag; //订单状态 0-未支付 1-已支付 2-已评价 3-已取消
    private String grabflag;

    public OrderListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                order_no = get(jsonObject, "order_no");
                client_id = get(jsonObject, "client_id");
                realname = get(jsonObject, "realname");
                sex = get(jsonObject, "sex");
                mobile = get(jsonObject, "mobile");
                takecount = get(jsonObject, "takecount");
                avatar = get(jsonObject, "avatar");
                avatarbig = get(jsonObject, "avatarbig");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                begintime = get(jsonObject, "begintime");
                numbers = get(jsonObject, "numbers");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                is_pool = get(jsonObject, "is_pool");
                payflag = get(jsonObject, "payflag");
                grabflag = get(jsonObject, "grabflag");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "OrderListInfor{" +
                "avatar='" + avatar + '\'' +
                ", id='" + id + '\'' +
                ", order_no='" + order_no + '\'' +
                ", client_id='" + client_id + '\'' +
                ", realname='" + realname + '\'' +
                ", sex='" + sex + '\'' +
                ", mobile='" + mobile + '\'' +
                ", takecount='" + takecount + '\'' +
                ", avatarbig='" + avatarbig + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", begintime='" + begintime + '\'' +
                ", numbers='" + numbers + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", payflag='" + payflag + '\'' +
                ", grabflag='" + grabflag + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarbig() {
        return avatarbig;
    }

    public String getBegintime() {
        return begintime;
    }

    public String getTakecount() {
        return takecount;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getEndaddress() {
        return endaddress;
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

    public String getNumbers() {
        return numbers;
    }

    public String getOrder_no() {
        return order_no;
    }

    public String getPayflag() {
        return payflag;
    }

    public String getRealname() {
        return realname;
    }

    public String getSex() {
        return sex;
    }

    public String getGrabflag() {
        return grabflag;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getSuccessfee() {
        return successfee;
    }
}
