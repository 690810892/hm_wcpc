package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/6/14.
 */
public class Driver extends XtomObject {

    private String id; //用户主键
    private String username; //登录名
    private String email; //用户邮箱
    private String realname; //用户昵称
    private String mobile; //用户手机
    private String password; //登陆密码
    private String paypassword; //支付密码
    private String sex; //性别
    private String avatar; //个人主页头像图片（小）
    private String avatarbig; //个人主页头像图片（大）
    private String replycount; //经度
    private String totalpoint;  //维度
    private String IDtype; //证件类型
    private String IDnumber; //证件号码
    private String regdate; //用户注册时间
    private String franchisee_id; //加盟商主键id
    private String servicecount; //服务次数
    private String loginflag; //登录状态	0-休车 1-出车
    private String feeaccount; //账户余额
    private String carbrand;
    private String carnumbers;

    public Driver(JSONObject jsonObject) throws DataParseException {
        try {
            id = get(jsonObject, "id");
            username = get(jsonObject, "username");
            email = get(jsonObject, "email");
            realname = get(jsonObject, "realname");
            mobile = get(jsonObject, "mobile");
            password = get(jsonObject, "password");
            paypassword = get(jsonObject, "paypassword");
            sex = get(jsonObject, "sex");
            avatar = get(jsonObject, "avatar");
            avatarbig = get(jsonObject, "avatarbig");
            replycount = get(jsonObject, "replycount");
            totalpoint = get(jsonObject, "totalpoint");
            IDtype = get(jsonObject, "IDtype");
            IDnumber = get(jsonObject, "IDnumber");
            regdate = get(jsonObject, "regdate");
            franchisee_id = get(jsonObject, "franchisee_id");
            servicecount = get(jsonObject, "servicecount");
            loginflag = get(jsonObject, "loginflag");
            feeaccount = get(jsonObject, "feeaccount");
            carbrand = get(jsonObject, "carbrand");
            carnumbers = get(jsonObject, "carnumber");

            log_i(toString());
        } catch (JSONException e) {
            throw new DataParseException(e);
        }
    }

    @Override
    public String toString() {
        return "Driver{" +
                "avatar='" + avatar + '\'' +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realname='" + realname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", paypassword='" + paypassword + '\'' +
                ", sex='" + sex + '\'' +
                ", avatarbig='" + avatarbig + '\'' +
                ", replycount='" + replycount + '\'' +
                ", totalpoint='" + totalpoint + '\'' +
                ", IDtype='" + IDtype + '\'' +
                ", IDnumber='" + IDnumber + '\'' +
                ", regdate='" + regdate + '\'' +
                ", franchisee_id='" + franchisee_id + '\'' +
                ", servicecount='" + servicecount + '\'' +
                ", loginflag='" + loginflag + '\'' +
                ", feeaccount='" + feeaccount + '\'' +
                ", carbrand='" + carbrand + '\'' +
                ", carnumbers='" + carnumbers + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarbig() {
        return avatarbig;
    }

    public String getRegdate() {
        return regdate;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFeeaccount() {
        return feeaccount;
    }

    public String getIDnumber() {
        return IDnumber;
    }

    public String getIDtype() {
        return IDtype;
    }

    public String getPaypassword() {
        return paypassword;
    }

    public String getRealname() {
        return realname;
    }

    public String getSex() {
        return sex;
    }

    public String getFranchisee_id() {
        return franchisee_id;
    }

    public String getLoginflag() {
        return loginflag;
    }

    public String getServicecount() {
        return servicecount;
    }

    public void setLoginflag(String loginflag) {
        this.loginflag = loginflag;
    }

    public String getCarbrand() {
        return carbrand;
    }

    public String getCarnumbers() {
        return carnumbers;
    }

    public String getReplycount() {
        return replycount;
    }

    public String getTotalpoint() {
        return totalpoint;
    }
}
