/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user;

/**
 * 该项目配置信息
 */
public class BaseConfig {

	/**
	 * 是否打印信息开关
	 */
	public static final boolean DEBUG = true;
	/**
	 * 后台服务接口根路径
	 */
	public static final String SYS_ROOT = "http://124.128.23.74:8008/group3/hm_sfzc/";
//	public static final String SYS_ROOT = "http://101.200.191.117/hm_sfzc/";

	/**
	 * 图片压缩的最大宽度
	 */
	public static final int IMAGE_WIDTH = 640;
	
	/**
	 * 图片压缩的失真率
	 */
	public static final int IMAGE_QUALITY = 100;

	/**
	 * 微信appid
	 */
	public static final String APPID_WEIXIN = "wx08b49dcf9ea3c412";
	/**
	 * 银联支付环境--"00"生产环境,"01"测试环境
	 */
	public static final String UNIONPAY_TESTMODE = "00";

}
