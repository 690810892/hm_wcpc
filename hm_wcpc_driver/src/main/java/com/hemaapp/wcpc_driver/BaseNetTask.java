/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver;

import com.hemaapp.hm_FrameWork.HemaNetTask;

import java.util.HashMap;

/**
 * 网络请求任务
 */
public abstract class BaseNetTask extends HemaNetTask {
	/**
	 * 实例化网络请求任务
	 * 
	 * @param information
	 *            网络请求信息
	 * @param params
	 *            任务参数集(参数名,参数值)
	 */
	public BaseNetTask(BaseHttpInformation information,
			HashMap<String, String> params) {
		this(information, params, null);
	}

	/**
	 * 实例化网络请求任务
	 * 
	 * @param information
	 *            网络请求信息
	 * @param params
	 *            任务参数集(参数名,参数值)
	 * @param files
	 *            任务文件集(参数名,文件的本地路径)
	 */
	public BaseNetTask(BaseHttpInformation information,
                       HashMap<String, String> params, HashMap<String, String> files) {
		super(information, params, files);
	}

	@Override
	public BaseHttpInformation getHttpInformation() {
		return (BaseHttpInformation) super.getHttpInformation();
	}

}
