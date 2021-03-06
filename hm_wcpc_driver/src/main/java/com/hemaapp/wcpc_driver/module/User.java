package com.hemaapp.wcpc_driver.module;

import com.hemaapp.hm_FrameWork.HemaUser;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.exception.DataParseException;

/**
 * 用户登录信息
 * */
public class User extends HemaUser {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

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
	private String lng; //经度
	private String lat;  //维度
	private String IDtype; //证件类型
	private String IDnumber; //证件号码
	private String regdate; //用户注册时间
	private String franchisee_id; //加盟商主键id
	private String servicecount; //服务次数
	private String loginflag; //登录状态	0-休车 1-出车
	private String feeaccount; //账户余额
	private String token; //登陆令牌
	private String android_must_update; //安卓强制更新标记 0：不强制 1：强制
	private String android_last_version; //安卓最新版本号
	private String android_update_url; //安卓软件更新地址

	private String bankuser; //用主姓名
	private String bankname; //银行名称
	private String bankcard; //卡号
	private String bankmobile; //银行预留手机号
	private String alipay_no; //支付宝账号
	private String alipay_name;

	private String mylength;
	private String filenumber;
	private String totalpoint; //	总评分	计算平均星级
	private String replycount; //	被评分次数
	private String invitecode;
	private String drivinglicense; //
	private String 	carbrand; //
	private String 	totalworktime; //	总在线时长	单位：秒
	private String 	todayworktime; //	今天在线时长	单位：秒
	public User(JSONObject jsonObject) throws DataParseException {
		super(jsonObject);
		try {
			totalworktime= get(jsonObject, "totalworktime");
			todayworktime= get(jsonObject, "todayworktime");
			drivinglicense= get(jsonObject, "drivinglicense");
			carbrand= get(jsonObject, "carbrand");
			invitecode= get(jsonObject, "invitecode");
			totalpoint= get(jsonObject, "totalpoint");
			replycount= get(jsonObject, "replycount");
			alipay_name= get(jsonObject, "alipay_name");
			filenumber = get(jsonObject, "filenumber");
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
			lng = get(jsonObject, "lng");
			lat = get(jsonObject, "lat");
			IDtype = get(jsonObject, "IDtype");
			IDnumber = get(jsonObject, "IDnumber");
			regdate = get(jsonObject, "regdate");
			franchisee_id = get(jsonObject, "franchisee_id");
			servicecount = get(jsonObject, "servicecount");
			loginflag = get(jsonObject, "loginflag");
			feeaccount = get(jsonObject, "feeaccount");
			token = get(jsonObject, "token");
			android_must_update = get(jsonObject, "android_must_update");
			android_last_version = get(jsonObject, "android_last_version");
			android_update_url = get(jsonObject, "android_update_url");

			bankuser = get(jsonObject, "bankuser");
			bankname = get(jsonObject, "bankname");
			bankcard = get(jsonObject, "bankcard");
			bankmobile = get(jsonObject, "bankmobile");
			alipay_no = get(jsonObject, "alipay_no");
			mylength = get(jsonObject, "mylength");

			log_i(toString());
		} catch (JSONException e) {
			throw new DataParseException(e);
		}
	}

	public User(String id, String username, String email, String realname,
                String mobile, String password, String paypassword, String sex ,
                String avatar, String avatarbig, String lng, String lat, String IDtype,
                String IDnumber, String regdate, String franchisee_id, String servicecount,
                String loginflag, String feeaccount, String token,
                String android_must_update, String android_last_version,
                String android_update_url, String bankuser, String bankname, String bankcard,
                String bankmobile, String alipay_no, String mylength, String filenumber,String alipay_name
			,String totalpoint,String replycount,String invitecode,String drivinglicense,String carbrand
			,String totalworktime,String todayworktime) {
		super(token);
		this.id = id;
		this.totalworktime = totalworktime;
		this.todayworktime = todayworktime;
		this.drivinglicense = drivinglicense;
		this.carbrand = carbrand;
		this.totalpoint = totalpoint;
		this.invitecode = invitecode;
		this.replycount = replycount;
		this.alipay_name = alipay_name;
		this.username = username;
		this.filenumber = filenumber;
		this.email = email;
		this.realname = realname;
		this.mobile = mobile;
		this.password = password;
		this.paypassword = paypassword;
		this.sex = sex;
		this.avatar = avatar;
		this.avatarbig = avatarbig;
		this.lng = lng;
		this.lat = lat;
		this.IDtype = IDtype;
		this.IDnumber = IDnumber;
		this.regdate = regdate;
		this.franchisee_id = franchisee_id;
		this.servicecount = servicecount;
		this.loginflag = loginflag;
		this.feeaccount = feeaccount;
		this.android_must_update = android_must_update;
		this.android_last_version = android_last_version;
		this.android_update_url = android_update_url;
		this.bankuser = bankuser;
		this.bankname = bankname;
		this.bankcard = bankcard;
		this.bankmobile = bankmobile;
		this.alipay_no = alipay_no;
		this.mylength = mylength;
	}

	@Override
	public String toString() {
		return "User{" +
				"id='" + id + '\'' +
				", username='" + username + '\'' +
				", email='" + email + '\'' +
				", realname='" + realname + '\'' +
				", mobile='" + mobile + '\'' +
				", password='" + password + '\'' +
				", paypassword='" + paypassword + '\'' +
				", sex='" + sex + '\'' +
				", avatar='" + avatar + '\'' +
				", avatarbig='" + avatarbig + '\'' +
				", lng='" + lng + '\'' +
				", lat='" + lat + '\'' +
				", IDtype='" + IDtype + '\'' +
				", IDnumber='" + IDnumber + '\'' +
				", regdate='" + regdate + '\'' +
				", franchisee_id='" + franchisee_id + '\'' +
				", servicecount='" + servicecount + '\'' +
				", loginflag='" + loginflag + '\'' +
				", feeaccount='" + feeaccount + '\'' +
				", token='" + token + '\'' +
				", android_must_update='" + android_must_update + '\'' +
				", android_last_version='" + android_last_version + '\'' +
				", android_update_url='" + android_update_url + '\'' +
				", bankuser='" + bankuser + '\'' +
				", bankname='" + bankname + '\'' +
				", bankcard='" + bankcard + '\'' +
				", bankmobile='" + bankmobile + '\'' +
				", alipay_no='" + alipay_no + '\'' +
				", alipay_name='" + alipay_name + '\'' +
				", mylength='" + mylength + '\'' +
				", filenumber='" + filenumber + '\'' +
				", totalpoint='" + totalpoint + '\'' +
				", replycount='" + replycount + '\'' +
				", invitecode='" + invitecode + '\'' +
				", drivinglicense='" + drivinglicense + '\'' +
				", carbrand='" + carbrand + '\'' +
				", totalworktime='" + totalworktime + '\'' +
				", todayworktime='" + todayworktime + '\'' +
				'}';
	}

	public String getId() {
		return id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTotalpoint() {
		if (isNull(totalpoint))
			totalpoint="0";
		return totalpoint;
	}

	public String getReplycount() {
		if (isNull(replycount))
			replycount="0";
		return replycount;
	}

	public String getTotalworktime() {
		if (isNull(totalworktime))
			totalworktime="0";
		return totalworktime;
	}

	public String getTodayworktime() {
		if (isNull(todayworktime))
			todayworktime="0";
		return todayworktime;
	}

	public String getDrivinglicense() {
		return drivinglicense;
	}

	public String getCarbrand() {
		return carbrand;
	}

	public String getAndroid_must_update() {
		return android_must_update;
	}

	public String getAndroid_last_version() {
		return android_last_version;
	}

	public String getInvitecode() {
		return invitecode;
	}

	public String getAndroid_update_url() {
		return android_update_url;
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

	public String getAlipay_name() {
		return alipay_name;
	}

	public void setAlipay_name(String alipay_name) {
		this.alipay_name = alipay_name;
	}

	public void setPaypassword(String paypassword) {
		this.paypassword = paypassword;
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

	public String getMylength() {
		return mylength;
	}

	public void setMylength(String mylength) {
		this.mylength = mylength;
	}

	public String getEmail() {
		return email;
	}

	public String getFeeaccount() {
		return feeaccount;
	}

	public String getFilenumber() {
		return filenumber;
	}

	public String getIDnumber() {
		return IDnumber;
	}

	public String getIDtype() {
		return IDtype;
	}

	public String getLat() {
		return lat;
	}

	public String getLng() {
		return lng;
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
		if (isNull(loginflag)){
			loginflag="0";
		}
		return loginflag;
	}

	public String getServicecount() {
		return servicecount;
	}

	public String getAlipay_no() {
		return alipay_no;
	}

	public String getBankcard() {
		return bankcard;
	}

	public String getBankmobile() {
		return bankmobile;
	}

	public String getBankname() {
		return bankname;
	}

	public String getBankuser() {
		return bankuser;
	}

	public void setBankcard(String bankcard) {
		this.bankcard = bankcard;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public void setBankuser(String bankuser) {
		this.bankuser = bankuser;
	}

	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
	}

	public void setLoginflag(String loginflag) {
		this.loginflag = loginflag;
	}
}
