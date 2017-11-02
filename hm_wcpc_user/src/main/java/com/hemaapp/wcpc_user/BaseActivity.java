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
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.gyf.barlibrary.ImmersionBar;
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
	private ImmersionBar mImmersionBar;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImmersionBar = ImmersionBar.with(this);
		mImmersionBar.statusBarDarkFont(true, 0.2f); //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
		mImmersionBar.init();   //所有子类都将继承这些相同的属性
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImmersionBar != null)
			mImmersionBar.destroy();  //必须调用该方法，防止内存泄漏，不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
	}
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
