/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user;

import com.hemaapp.HemaConfig;
import com.hemaapp.hm_FrameWork.HemaHttpInfomation;
import com.hemaapp.wcpc_user.module.SysInitInfo;

/**
 * 网络请求信息枚举类
 */
public enum BaseHttpInformation implements HemaHttpInfomation {
	/**
	 * 后台服务接口根路径
	 */
	SYS_ROOT(0, BaseConfig.SYS_ROOT, "后台服务接口根路径", true),
	/**
	 * 用户登陆接口
	 */
	CLIENT_LOGIN(HemaConfig.ID_LOGIN, "client_login", "登录", false),
	// 注意登录接口id必须为HemaConfig.ID_LOGIN
	/**
	 * 第三方登录
	 */
	THIRD_SAVE(HemaConfig.ID_THIRDSAVE, "third_save", "第三方登录", false),
	// 注意第三方登录接口id必须为HemaConfig.ID_THIRDSAVE

	/**
	 * 系统初始化
	 */
	INIT(1, "index.php/Webservice/Index/init", "系统初始化", false),
	/**
	 * 上传文件（图片，音频，视频）
	 */
	FILE_UPLOAD(2, "file_upload", "上传文件（图片，音频，视频）", false),
	/**
	 * 验证用户名是否合法
	 */
	CLIENT_VERIFY(3, "client_verify", "验证用户名是否合法", false),
	/**
	 * 申请随机验证码
	 */
	CODE_GET(4, "code_get", "申请随机验证码", false),
	/**
	 * 验证随机码
	 */
	CODE_VERIFY(5, "code_verify", "验证随机码", false),
	/**
	 * 硬件注册保存
	 */
	DEVICE_SAVE(6, "device_save", "硬件注册保存", false),
	/**
	 * 意见反馈
	 */
	ADVICE_ADD(7, "advice_add", "意见反馈", false),
	/**
	 * 修改并保存密码
	 */
	PASSWORD_SAVE(8, "password_save", "修改并保存密码", false),
	/**
	 * 退出登录
	 */
	CLIENT_LOGINOUT(9, "client_loginout", "退出登录", false),
	/**
	 * 保存用户资料
	 */
	CLIENT_SAVE(10, "client_save", "保存用户资料", false),
	/**
	 * 添加评论
	 */
	REPLY_ADD(11, "reply_add", "添加评论接口", false),
	/**
	 * 评论列表
	 */
	REPLY_LIST(12, "reply_list", "评论列表", false),
	/**
	 * 获取用户通知列表接口
	 * */
	NOTICE_LIST(13, "notice_list", "获取用户通知列表接口", false),
	/**
	 * 保存用户通知操作接口
	 * */
	NOTICE_SAVEOPERATE(14, "notice_saveoperate", "保存用户通知操作接口", false),
	/**
	 * 用户注册
	 **/
	CLIENT_ADD(15, "client_add", "用户注册", false),
	/**
	 * 重设密码
	 */
	PASSWORD_RESET(16, "password_reset", "重设密码", false),
	/**
	 *  首页广告接口
	 * */
	ADVERTISE_LIST(17, "advertise_list", "首页广告接口", false),
	/**
	 *  计费规则接口
	 * */
	FEE_RULE_LIST(18, "fee_rule_list", "计费规则接口", false),
	/**
	 *  保存当前用户坐标接口
	 * */
	POSITION_SAVE(19, "position_save", "保存当前用户坐标接口", false),
	/**
	 * 费用计算接口
	 * */
	FEE_CALCULATION(20, "fee_calculation", "费用计算接口", false),
	/**
	 * 发布行程接口
	 * */
	TRIPS_ADD(21, "trips_add", "发布行程接口", false),
	/**
	 * 行程列表接口
	 * */
	TRIPS_LIST(22, "trips_list", "行程列表接口", false),
	/**
	 * 车主行程详情接口
	 * */
	DRIVER_TRIPS_GET(23, "driver_trips_get", "车主行程详情接口", false),
	/**
	 * 获取用户个人资料接口
	 * */
	CLIENT_GET(24, "client_get", "获取用户个人资料接口", false),
	/**
	 * 加入行程接口
	 * */
	JOIN_TRIPS(25, "join_trips", "加入行程接口", false),
	/**
	 * 优惠券列表接口
	 * */
	COUPONS_LIST(26, "coupons_list", "优惠券列表接口", false),
	/**
	 * 账户明细接口
	 * */
	ACCOUNT_RECORD_LIST(27, "account_record_list", "账户明细接口", false),
	/**
	 * 获取支付宝交易签名串(内含我方交易单号)接口
	 */
	ALIPAY(28, "OnlinePay/Alipay/alipaysign_get.php", "获取支付宝交易签名串", false),
	/**
	 * 获取银联交易签名串(内含我方交易单号)接口
	 */
	UNIONPAY(29, "OnlinePay/Unionpay/unionpay_get.php", "获取银联交易签名串", false),
	/**
	 * 获取微信预支付交易会话标识(内含我方交易单号)接口
	 * */
	WEI_XIN(30, "OnlinePay/Weixinpay/weixinpay_get.php", "获取微信交易签名串", false),
	/**
	 * 支付宝信息保存接口
	 * */
	ALIPAY_SAVE(31, "alipay_save", "支付宝信息保存接口", false),
	/**
	 * 申请提现接口
	 * */
	CASH_ADD(32, "cash_add", "申请提现接口", false),
	/**
	 * 获取银行列表
	 */
	BANK_LIST(33, "bank_list", "获取银行列表", false),
	/**
	 * 银行卡信息保存接口
	 * */
	BANK_SAVE(34, "bank_save", "银行卡信息保存接口", false),
	/**
	 * 我的行程接口
	 * */
	MY_TRIPS_LIST(35, "my_trips_list", "我的行程接口", false),
	/**
	 * 我的行程操作接口
	 * */
	MY_TRIPS_OPERATE(36, "my_trips_operate", "我的行程操作接口", false),
	/**
	 * 用户端订单列表接口
	 * */
	CLIENT_ORDER_LIST(37, "client_order_list", "用户端订单列表接口", false),
	/**
	 * 订单操作接口
	 * */
	ORDER_OPERATE(38, "order_operate", "订单操作接口", false),
	/**
	 * 数据获取接口
	 * */
	DATA_LIST(39, "data_list", "数据获取接口", false),
	/**
	 * 用户端订单详情接口
	 * */
	CLIENT_ORDER_GET(40, "client_order_get", "用户端订单详情接口", false),
	/**
	 * 通知未读接口
	 * */
	NOTICE_UNREAD(41, "notice_unread", "通知未读接口", false),
	/**
	 * 订单保存(去支付时调用)接口
	 * */
	ORDER_SAVE(42, "order_save", "订单保存(去支付时调用)接口", false),
	/**
	 * 司机详情接口
	 * */
	DRIVER_GET(43, "driver_get", "司机详情接口", false),
	/**
	 * 地区列表接口
	 * */
	DISTRICT_LIST(44, "district_list", "地区列表接口", false),
	/**
	 * 是否可以发布行程接口
	 * */
	CAN_TRIPS(45, "can_trips", "是否可以发布行程接口", false),
	/**
	 * 用户当前行程接口
	 * */
	CURRENT_TRIPS(46, "current_trips", "用户当前行程接口", false),
	;

	private int id;// 对应NetTask的id
	private String urlPath;// 请求地址
	private String description;// 请求描述
	private boolean isRootPath;// 是否是根路径

	private BaseHttpInformation(int id, String urlPath, String description,
                                boolean isRootPath) {
		this.id = id;
		this.urlPath = urlPath;
		this.description = description;
		this.isRootPath = isRootPath;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getUrlPath() {
		if (isRootPath)
			return urlPath;

		String path = SYS_ROOT.urlPath + urlPath;

		if (this.equals(INIT)){
			return path;
		}

		hm_WcpcUserApplication application = hm_WcpcUserApplication.getInstance();
		SysInitInfo info = application.getSysInitInfo();
		path = info.getSys_web_service() + urlPath;

		if (this.equals(ALIPAY))
			path = info.getSys_plugins() + urlPath;

		if (this.equals(UNIONPAY))
			path = info.getSys_plugins() + urlPath;

		if (this.equals(WEI_XIN))
			path = info.getSys_plugins() + urlPath;
		return path;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isRootPath() {
		return isRootPath;
	}

}
