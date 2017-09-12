/*
 * Copyright (C) 2015 The Android Client Of QK Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user;

import android.content.Context;
import android.content.Intent;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaNetTaskExecuteListener;
import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_user.activity.LoginActivity;

import xtom.frame.XtomActivityManager;
import xtom.frame.net.XtomNetWorker;

/**
 * 网络任务执行监听
 */
public abstract class BaseNetTaskExecuteListener extends
        HemaNetTaskExecuteListener {

	public BaseNetTaskExecuteListener(Context context) {
		super(context);
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
				mContext.startActivity(it);
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

}
