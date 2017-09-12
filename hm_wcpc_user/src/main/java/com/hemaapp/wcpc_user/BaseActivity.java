/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user;

import android.content.Intent;

import com.hemaapp.hm_FrameWork.HemaActivity;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.activity.LoginActivity;
import com.hemaapp.wcpc_user.module.User;

import xtom.frame.XtomActivityManager;
import xtom.frame.net.XtomNetWorker;

/**
 */
public abstract class BaseActivity extends HemaActivity {

	@Override
	protected HemaNetWorker initNetWorker() {
		return new BaseNetWorker(mContext);
	}

	@Override
	public BaseNetWorker getNetWorker() {
		return (BaseNetWorker) super.getNetWorker();
	}

	@Override
	public hm_WcpcUserApplication getApplicationContext() {
		return (hm_WcpcUserApplication) super.getApplicationContext();
	}

	@Override
	protected void callBackForServerFailed(HemaNetTask netTask,
			HemaBaseResult baseResult) {
		if (baseResult.getError_code() == 404) {
			showTextDialog("您即将访问的页面不存在");
			finish();
		}
	}

	@Override
	public boolean onAutoLoginFailed(HemaNetWorker netWorker,
                                     HemaNetTask netTask, int failedType, HemaBaseResult baseResult) {
		switch (failedType) {
		case 0:// 服务器处理失败
			int error_code = baseResult.getError_code();
			switch (error_code) {
			case 102:// 密码错误
				XtomActivityManager.finishAll();
				Intent it = new Intent(mContext, LoginActivity.class);
				startActivity(it);
				return true;
			default:
				break;
			}
		case XtomNetWorker.FAILED_HTTP:// 网络异常
		case XtomNetWorker.FAILED_DATAPARSE:// 数据异常
		case XtomNetWorker.FAILED_NONETWORK:// 无网络
			break;
		}
		return false;
	}

	// ------------------------下面填充项目自定义方法---------------------------

	/**
	 * 获取用户
	 * */
	public User getUser() {
		User user = hm_WcpcUserApplication.getInstance().getUser();
		if (user == null)
			return null;
		else {
			return user;
		}
	}

	@Override
	protected boolean isNull(String str) {
		if ("".equals(str) || str == null || "null".equals(str))
			return true;
		return false;
	}

}
