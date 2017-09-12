/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.SysInitInfo;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * 系统初始化
 */
public class InitTask extends BaseNetTask {

	public InitTask(BaseHttpInformation information,
			HashMap<String, String> params) {
		super(information, params);
	}

	public InitTask(BaseHttpInformation information,
                    HashMap<String, String> params, HashMap<String, String> files) {
		super(information, params, files);
	}

	@Override
	public Object parse(JSONObject jsonObject) throws DataParseException {
		return new Result(jsonObject);
	}

	private class Result extends HemaArrayResult<SysInitInfo> {

		public Result(JSONObject jsonObject) throws DataParseException {
			super(jsonObject);
		}

		@Override
		public SysInitInfo parse(JSONObject jsonObject)
				throws DataParseException {
			return new SysInitInfo(jsonObject);
		}
	}
}
