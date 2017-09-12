package com.hemaapp.wcpc_user.module;

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
    private String driver_id; //司机主键id
    private String realname; //姓名
    private String sex; //性别
    private String mobile; //电话
    private String carbrand; //车品牌
    private String carnumber; //车牌号
    private String avatar; //头像
    private String avatarbig; //头像大图
    private String startaddress; //出发地
    private String endaddress; //目的地
    private String begintime; //出发时间
    private String numbers; //乘车人数
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String is_pool; //拼车成功 0-否 1-是
    private String tripflag; //出行标志 0-未出行 1-已出行
    private String payflag; //订单状态 0-未支付 1-已支付 2-已评价 3-已取消
    private String reachflag; //送达标志	0-未送达 1-已送达

    public OrderListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                order_no = get(jsonObject, "order_no");
                driver_id = get(jsonObject, "driver_id");
                realname = get(jsonObject, "realname");
                sex = get(jsonObject, "sex");
                mobile = get(jsonObject, "mobile");
                carbrand = get(jsonObject, "carbrand");
                carnumber = get(jsonObject, "carnumber");
                avatar = get(jsonObject, "avatar");
                avatarbig = get(jsonObject, "avatarbig");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");
                begintime = get(jsonObject, "begintime");
                numbers = get(jsonObject, "numbers");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                is_pool = get(jsonObject, "is_pool");
                tripflag = get(jsonObject, "tripflag");
                payflag = get(jsonObject, "payflag");
                reachflag = get(jsonObject, "reachflag");

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
                ", driver_id='" + driver_id + '\'' +
                ", realname='" + realname + '\'' +
                ", sex='" + sex + '\'' +
                ", mobile='" + mobile + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumber='" + carnumber + '\'' +
                ", avatarbig='" + avatarbig + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
                ", begintime='" + begintime + '\'' +
                ", numbers='" + numbers + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", tripflag='" + tripflag + '\'' +
                ", payflag='" + payflag + '\'' +
                ", reachflag='" + reachflag + '\'' +
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

    public String getCarbrand() {
        return carbrand;
    }

    public String getCarnumber() {
        return carnumber;
    }

    public String getDriver_id() {
        return driver_id;
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

    public String getReachflag() {
        return reachflag;
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

    public String getStartaddress() {
        return startaddress;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public String getTripflag() {
        return tripflag;
    }
}
