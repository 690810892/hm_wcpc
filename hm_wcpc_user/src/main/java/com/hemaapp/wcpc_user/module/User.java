package com.hemaapp.wcpc_user.module;

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
	private String takecount; //乘车次数
	private String feeaccount; //账户余额
	private String token; //登陆令牌
	private String android_must_update; //安卓强制更新标记 0：不强制 1：强制
	private String android_last_version; //安卓最新版本号
	private String android_update_url; //安卓软件更新地址

	private String carbrand; //车牌
	private String carnumber; //车牌号
	private String score; //积分

	private String bankuser; //用主姓名
	private String bankname; //银行名称
	private String bankcard; //卡号
	private String bankmobile; //银行预留手机号
	private String alipay_no; //支付宝账号
	private String invitecode; //
	private String today_cancel_count; //

	private String   coupon_count;//		系统赠送的代金券数
	private String   coupon_value;//		代金券每一张的金额
	private String  coupon_dateline	;//	代金券有效期
	private String  is_reg;//	是否为新注册记录	1：是，0：否

	public User(JSONObject jsonObject) throws DataParseException {
		super(jsonObject);
		try {
			today_cancel_count= get(jsonObject, "today_cancel_count");
			invitecode= get(jsonObject, "invitecode");
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
			takecount = get(jsonObject, "takecount");
			feeaccount = get(jsonObject, "feeaccount");
			token = get(jsonObject, "token");
			android_must_update = get(jsonObject, "android_must_update");
			android_last_version = get(jsonObject, "android_last_version");
			android_update_url = get(jsonObject, "android_update_url");

			carbrand = get(jsonObject, "carbrand");
			carnumber = get(jsonObject, "carnumber");
			score = get(jsonObject, "score");

			bankuser = get(jsonObject, "bankuser");
			bankname = get(jsonObject, "bankname");
			bankcard = get(jsonObject, "bankcard");
			bankmobile = get(jsonObject, "bankmobile");
			alipay_no = get(jsonObject, "alipay_no");

			coupon_count = get(jsonObject, "coupon_count");
			coupon_value = get(jsonObject, "coupon_value");
			coupon_dateline = get(jsonObject, "coupon_dateline");
			is_reg = get(jsonObject, "is_reg");
			
			log_i(toString());
		} catch (JSONException e) {
			throw new DataParseException(e);
		}
	}
	
	public User(String id, String username, String email, String realname,
                String mobile, String password, String paypassword, String sex ,
                String avatar, String avatarbig, String lng, String lat, String IDtype,
                String IDnumber, String regdate, String takecount, String feeaccount,
                String token, String android_must_update, String android_last_version,
                String android_update_url, String carbrand, String carnumber, String score,
                String bankuser, String bankname, String bankcard, String bankmobile,
                String alipay_no,String invitecode,String today_cancel_count
			,String coupon_count,String coupon_value,String coupon_dateline,String is_reg) {
		super(token);
		this.id = id;
		this.coupon_count = coupon_count;
		this.coupon_value = coupon_value;
		this.coupon_dateline = coupon_dateline;
		this.is_reg = is_reg;
		this.today_cancel_count = today_cancel_count;
		this.username = username;
		this.invitecode = invitecode;
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
		this.takecount = takecount;
		this.feeaccount = feeaccount;
		this.android_must_update = android_must_update;
		this.android_last_version = android_last_version;
		this.android_update_url = android_update_url;
		this.carbrand = carbrand;
		this.carnumber = carnumber;
		this.score = score;
		this.bankuser = bankuser;
		this.bankname = bankname;
		this.bankcard = bankcard;
		this.bankmobile = bankmobile;
		this.alipay_no = alipay_no;
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
				", takecount='" + takecount + '\'' +
				", feeaccount='" + feeaccount + '\'' +
				", token='" + token + '\'' +
				", android_must_update='" + android_must_update + '\'' +
				", android_last_version='" + android_last_version + '\'' +
				", android_update_url='" + android_update_url + '\'' +
				", carbrand='" + carbrand + '\'' +
				", carnumber='" + carnumber + '\'' +
				", score='" + score + '\'' +
				", bankuser='" + bankuser + '\'' +
				", bankname='" + bankname + '\'' +
				", bankcard='" + bankcard + '\'' +
				", bankmobile='" + bankmobile + '\'' +
				", alipay_no='" + alipay_no + '\'' +
				", invitecode='" + invitecode + '\'' +
				", today_cancel_count='" + today_cancel_count + '\'' +
				", coupon_count='" + coupon_count + '\'' +
				", coupon_value='" + coupon_value + '\'' +
				", coupon_dateline='" + coupon_dateline + '\'' +
				", is_reg='" + is_reg + '\'' +
				'}';
	}

	public String getId() {
		return id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAndroid_must_update() {
		return android_must_update;
	}

	public String getAndroid_last_version() {
		return android_last_version;
	}

	public String getAndroid_update_url() {
		return android_update_url;
	}

	public String getInvitecode() {
		return invitecode;
	}

	public String getPassword() {
		return password;
	}

	public String getCoupon_count() {
		return coupon_count;
	}

	public String getCoupon_value() {
		return coupon_value;
	}

	public String getCoupon_dateline() {
		return coupon_dateline;
	}

	public String getIs_reg() {
		return is_reg;
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

	public void setPaypassword(String paypassword) {
		this.paypassword = paypassword;
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

	public String getCarbrand() {
		return carbrand;
	}

	public String getCarnumber() {
		return carnumber;
	}

	public String getScore() {
		return score;
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

	public void setAlipay_no(String alipay_no) {
		this.alipay_no = alipay_no;
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

	public String getBankuser() {
		return bankuser;
	}

	public String getToday_cancel_count() {
		return today_cancel_count;
	}

	public String getTakecount() {
		return takecount;
	}
}
