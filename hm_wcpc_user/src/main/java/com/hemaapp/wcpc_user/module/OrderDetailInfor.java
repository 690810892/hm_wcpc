package com.hemaapp.wcpc_user.module;

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
    private String driver_id; //司机主键id
    private String realname; //司机姓名
    private String sex; //司机姓名
    private String avatar; //头像
    private String mobile; //司机电话
    private String successfee; //拼车成功费用
    private String failfee; //拼车失败费用
    private String is_pool; //拼车成功	0
    private String totalpoint; //总评分
    private String replycount; //评价数
    private String payflag; //订单状态
    private String tripflag; //出行标志	0-未出行 1-已出行
    private String reason_str; //取消原因
    private String content; //其它原因
    private String regdate; //添加时间
    private String carbrand; //车品牌
    private String carnumbers; //车牌号
    private ArrayList<ReplyItems> replyItems; //订单评论
    private String reachflag; //到达标志
    private String statusflag; //0-未接乘客 1-已接到乘客 2-乘客确认上车

    public OrderDetailInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                order_no = get(jsonObject, "order_no");
                driver_id = get(jsonObject, "driver_id");
                realname = get(jsonObject, "realname");
                sex = get(jsonObject, "sex");
                avatar = get(jsonObject, "avatar");
                mobile = get(jsonObject, "mobile");
                successfee = get(jsonObject, "successfee");
                failfee = get(jsonObject, "failfee");
                is_pool = get(jsonObject, "is_pool");
                totalpoint = get(jsonObject, "totalpoint");
                replycount = get(jsonObject, "replycount");
                payflag = get(jsonObject, "payflag");
                tripflag = get(jsonObject, "tripflag");
                reason_str = get(jsonObject, "reason_str");
                content = get(jsonObject, "content");
                regdate = get(jsonObject, "regdate");
                carbrand = get(jsonObject, "carbrand");
                carnumbers = get(jsonObject, "carnumbers");
                reachflag = get(jsonObject, "reachflag");
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
                "avatar='" + avatar + '\'' +
                ", id='" + id + '\'' +
                ", order_no='" + order_no + '\'' +
                ", driver_id='" + driver_id + '\'' +
                ", realname='" + realname + '\'' +
                ", sex='" + sex + '\'' +
                ", mobile='" + mobile + '\'' +
                ", successfee='" + successfee + '\'' +
                ", failfee='" + failfee + '\'' +
                ", is_pool='" + is_pool + '\'' +
                ", totalpoint='" + totalpoint + '\'' +
                ", replycount='" + replycount + '\'' +
                ", payflag='" + payflag + '\'' +
                ", tripflag='" + tripflag + '\'' +
                ", reason_str='" + reason_str + '\'' +
                ", content='" + content + '\'' +
                ", regdate='" + regdate + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumbers='" + carnumbers + '\'' +
                ", replyItems=" + replyItems +
                ", reachflag='" + reachflag + '\'' +
                ", statusflag='" + statusflag + '\'' +
                '}';
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public String getDriver_id() {
        return driver_id;
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

    public String getRealname() {
        return realname;
    }

    public String getReason_str() {
        return reason_str;
    }

    public String getStatusflag() {
        return statusflag;
    }

    public String getRegdate() {
        return regdate;
    }

    public String getReplycount() {
        return replycount;
    }

    public ArrayList<ReplyItems> getReplyItems() {
        return replyItems;
    }

    public String getSex() {
        return sex;
    }

    public String getSuccessfee() {
        return successfee;
    }

    public String getTotalpoint() {
        return totalpoint;
    }

    public String getTripflag() {
        return tripflag;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getReachflag() {
        return reachflag;
    }

    public String getCarnumbers() {
        return carnumbers;
    }
}
