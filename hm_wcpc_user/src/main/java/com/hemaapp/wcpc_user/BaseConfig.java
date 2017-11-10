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
	public static final String SYS_ROOT = "http://app.qwicar.com/";
//	public static final String SYS_ROOT = "http://101.200.191.117/hm_sfzc/";

	/**
	 * 图片压缩的最大宽度
	 */
	public static final int IMAGE_WIDTH = 640;
	public static final int IMAGE_HEIGHT = 3000;
	/**
	 * 图片压缩的失真率
	 */
	public static final int IMAGE_QUALITY = 100;

	/**
	 * 微信appid
	 */
	public static final String APPID_WEIXIN = "wx5bff865f7d0ef0ad";
	/**
	 * 银联支付环境--"00"生产环境,"01"测试环境
	 */
	public static final String UNIONPAY_TESTMODE = "00";
/*
  MD5: D0:9A:4D:C3:1A:10:D8:33:FC:39:D1:D6:C9:8B:44:8C
         SHA1: 71:DA:36:21:AE:F9:A6:31:CC:2D:EA:AF:96:0C:B0:64:D6:0C:A0:69
         SHA256: 74:00:BD:81:35:8B:AA:25:C0:F3:62:17:97:D4:05:68:CF:D3:B4:AD:68:E8:7E:BC:8F:D8:4E:93:13:A7:BC:A8
         签名算法名称: SHA256withRSA
         版本: 3

 */
}
