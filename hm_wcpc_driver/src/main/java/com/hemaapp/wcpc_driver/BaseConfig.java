/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver;

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
//	public static final String SYS_ROOT = "http://192.168.1.126/yscx_xjc/";

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
	public static final String APPID_WEIXIN = "wx5c3400f5fe253fb4";
	/**
	 * 银联支付环境--"00"生产环境,"01"测试环境
	 */
	public static final String UNIONPAY_TESTMODE = "00";
	/*
	证书指纹:
         MD5: 93:F3:AD:64:DE:5E:76:18:71:39:AB:70:06:64:0E:A1
         SHA1: CF:39:55:CD:7D:13:07:0E:A4:49:7E:E4:13:4B:E5:85:A2:F1:2D:40
         SHA256: C2:E5:62:44:AE:0A:66:88:E8:6D:85:AF:69:8A:DA:9A:46:C6:AB:CD:E7:4E:09:E5:62:F7:7D:BB:FF:9D:DD:E6
         签名算法名称: SHA256withRSA
         版本: 3

	 */

}
