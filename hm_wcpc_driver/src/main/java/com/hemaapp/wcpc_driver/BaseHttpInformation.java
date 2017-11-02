/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver;

import com.hemaapp.HemaConfig;
import com.hemaapp.hm_FrameWork.HemaHttpInfomation;
import com.hemaapp.wcpc_driver.module.SysInitInfo;

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
	CLIENT_LOGIN(HemaConfig.ID_LOGIN, "driver_login", "登录", false),
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
	CLIENT_SAVE(10, "driver_save", "保存用户资料", false),
	/**
	 * 获取用户通知列表接口
	 * */
	NOTICE_LIST(12, "notice_list", "获取用户通知列表接口", false),
	/**
	 * 保存用户通知操作接口
	 * */
	NOTICE_SAVEOPERATE(13, "notice_saveoperate", "保存用户通知操作接口", false),
	/**
	 * 重设密码
	 */
	PASSWORD_RESET(14, "password_reset", "重设密码", false),
	/**
	 *  计费规则接口
	 * */
	FEE_RULE_LIST(15, "fee_rule_list", "计费规则接口", false),
	/**
	 *  保存当前用户坐标接口
	 * */
	POSITION_SAVE(16, "position_save", "保存当前用户坐标接口", false),
	/**
	 * 费用计算接口
	 * */
	FEE_CALCULATION(17, "fee_calculation", "费用计算接口", false),
	/**
	 * 发布行程接口
	 * */
	TRIPS_ADD(18, "trips_add", "发布行程接口", false),
	/**
	 * 行程列表接口
	 * */
	TRIPS_LIST(19, "trips_list", "行程列表接口", false),
	/**
	 * 获取用户个人资料接口
	 * */
	CLIENT_GET(20, "driver_get", "获取用户个人资料接口", false),
	/**
	 * 我的行程接口
	 * */
	MY_TRIPS_LIST(21, "my_trips_list", "我的行程接口", false),
	/**
	 * 我的行程操作接口
	 * */
	MY_TRIPS_OPERATE(22, "my_trips_operate", "我的行程操作接口", false),
	/**
	 * 订单操作接口
	 * */
	ORDER_OPERATE(23, "order_operate", "订单操作接口", false),
	/**
	 * 数据获取接口
	 * */
	DATA_LIST(24, "data_list", "数据获取接口", false),
	/**
	 * 司机端订单列表接口
	 * */
	DRIVER_ORDER_LIST(25, "driver_order_list", "司机端订单列表接口", false),
	/**
	 * 抢单接口
	 * */
	GRAP_TRIPS(26, "grab_trips", "抢单接口", false),
	/**
	 * 司机端订单详情接口
	 * */
	DRIVER_ORDER_GET(27, "driver_order_get", "司机端订单详情接口", false),
	/**
	 * 用户行程详情(抢单详情)接口
	 * */
	CLIENT_TRIPS_GET(28, "client_trips_get", "用户行程详情(抢单详情)接口", false),
	/**
	 * 登录状态保存接口
	 * */
	LOGINFLAG_SAVE(29, "loginflag_save", "登录状态保存接口", false),
	/**
	 * 我的乘客接口
	 * */
	MY_CLIENT_LIST(30, "my_client_list", "我的乘客接口", false),
	/**
	 * 通知未读接口
	 * */
	NOTICE_UNREAD(31, "notice_unread", "通知未读接口", false),
	/**
	 * 支付宝信息保存接口
	 * */
	ALIPAY_SAVE(32, "alipay_save", "支付宝信息保存接口", false),
	/**
	 * 申请提现接口
	 * */
	CASH_ADD(33, "cash_add", "申请提现接口", false),
	/**
	 * 获取银行列表
	 */
	BANK_LIST(34, "bank_list", "获取银行列表", false),
	/**
	 * 银行卡信息保存接口
	 * */
	BANK_SAVE(35, "bank_save", "银行卡信息保存接口", false),
	/**
	 * 地区列表接口
	 * */
	DISTRICT_LIST(36, "district_list", "地区列表接口", false),
	/**
	 * 设置接单距离接口
	 * */
	MY_LENGTH(37, "my_length", "设置接单距离接口", false),
	TRIPS_SAVEOPERATE(37, "trips_saveoperate", "保存行程操作接口", false),
	WORKSTATUS_GET(37, "driver_workstatus_get", "获取司机首页各种状态接口", false),
	OPEN_WORKSTATUS(37, "driver_open_async_workstatus", "司机开启异地接单接口", false),
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
	COMPLAIN_ADD(44, "complain_add", "投诉", false),
	REPLY_ADD(11, "reply_add", "添加评论接口", false),
	REPLY_LIST(11, "reply_list", "评论列表", false),
	ADVICE_ADD(7, "advice_add", "意见反馈", false),
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

		hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
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
