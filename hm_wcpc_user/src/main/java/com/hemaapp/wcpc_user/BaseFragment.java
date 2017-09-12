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

import com.hemaapp.hm_FrameWork.HemaFragment;
import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.activity.LoginActivity;

import xtom.frame.XtomActivityManager;
import xtom.frame.net.XtomNetWorker;

public abstract class BaseFragment extends HemaFragment {

	@Override
	protected HemaNetWorker initNetWorker() {
		return new BaseNetWorker(getActivity());
	}

	@Override
	public BaseNetWorker getNetWorker() {
		return (BaseNetWorker) super.getNetWorker();
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
				Intent it = new Intent(getActivity(), LoginActivity.class);
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
}
