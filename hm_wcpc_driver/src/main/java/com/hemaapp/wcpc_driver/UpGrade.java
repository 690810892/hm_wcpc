/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.hemaapp.hm_FrameWork.HemaNetTask;
import com.hemaapp.hm_FrameWork.HemaNetWorker;
import com.hemaapp.hm_FrameWork.HemaUtil;
import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaBaseResult;
import com.hemaapp.wcpc_driver.module.SysInitInfo;

import java.io.File;

import xtom.frame.XtomObject;
import xtom.frame.fileload.FileInfo;
import xtom.frame.fileload.XtomFileDownLoader;
import xtom.frame.fileload.XtomFileDownLoader.XtomDownLoadListener;
import xtom.frame.util.XtomFileUtil;
import xtom.frame.util.XtomToastUtil;

/**
 * 软件升级
 */
public abstract class UpGrade extends XtomObject {
	private long checkTime = 0;
	private Context mContext;
	private String savePath;
	private BaseNetWorker netWorker;
	private SysInitInfo sysInitInfo;

	public UpGrade(Context mContext) {
		this.mContext = mContext;
		this.netWorker = new BaseNetWorker(mContext);
		this.netWorker.setOnTaskExecuteListener(new TaskExecuteListener(
				mContext));
	}

	/**
	 * 检查升级
	 */
	public void check() {
		long currentTime = System.currentTimeMillis();
		boolean isCanCheck = checkTime == 0
				|| currentTime - checkTime > 1000 * 60 * 60 * 24;
		if (isCanCheck) {
			netWorker.init();
		}
	}

	// 是否强制升级
	private boolean isMust() {
		SysInitInfo sysInfo = hm_WcpcDriverApplication.getInstance().getSysInitInfo();
		boolean must = "1".equals(sysInfo.getAndroid_must_update());
		return must;
	}

	private class TaskExecuteListener extends BaseNetTaskExecuteListener {

		/**
		 * @param context
		 */
		public TaskExecuteListener(Context context) {
			super(context);
		}

		@Override
		public void onPreExecute(HemaNetWorker netWorker, HemaNetTask netTask) {
		}

		@Override
		public void onPostExecute(HemaNetWorker netWorker, HemaNetTask netTask) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onServerSuccess(HemaNetWorker netWorker,
                                    HemaNetTask netTask, HemaBaseResult baseResult) {
			checkTime = System.currentTimeMillis();
			HemaArrayResult<SysInitInfo> sResult = (HemaArrayResult<SysInitInfo>) baseResult;
			sysInitInfo = sResult.getObjects().get(0);
			String sysVersion = sysInitInfo.getDriver_android_last_version_mer();
			String version = HemaUtil.getAppVersionForSever(mContext);
			if (HemaUtil.isNeedUpDate(version, sysVersion)) {
				alert();
			}
		}

		@Override
		public void onServerFailed(HemaNetWorker netWorker,
                                   HemaNetTask netTask, HemaBaseResult baseResult) {
		}

		@Override
		public void onExecuteFailed(HemaNetWorker netWorker,
                                    HemaNetTask netTask, int failedType) {
		}

	}

	public void alert() {
		Builder ab = new Builder(mContext);
		ab.setTitle("软件更新");
		ab.setMessage("有最新的软件版本，是否升级？");
		ab.setPositiveButton("升级", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(sysInitInfo == null)
					sysInitInfo = hm_WcpcDriverApplication.getInstance().getSysInitInfo();
				upGrade(sysInitInfo);
			}
		});
		ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				if (isMust())
					BaseUtil.clientLoginout(mContext);
				else
					NoNeedUpdate();
			}
		});
		ab.setCancelable(false);
		ab.show();
	}

	public void upGrade(SysInitInfo sysInitInfo) {
		String downPath = sysInitInfo.getDriver_android_update_url();
		savePath = XtomFileUtil.getFileDir(mContext) + "/apps/driver_"
				+ sysInitInfo.getDriver_android_last_version_mer() + ".apk";
		XtomFileDownLoader downLoader = new XtomFileDownLoader(mContext,
				downPath, savePath);
		downLoader.setThreadCount(3);
		downLoader.setXtomDownLoadListener(new DownLoadListener());
		downLoader.start();
	}

	private class DownLoadListener implements XtomDownLoadListener {
		private ProgressDialog pBar;

		@Override
		public void onStart(final XtomFileDownLoader loader) {
			pBar = new ProgressDialog(mContext) {
				@Override
				public void onBackPressed() {
					loader.stop();
				}
			};
			pBar.setTitle("正在下载");
			pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pBar.setMax(100);
			pBar.setCancelable(false);
			pBar.show();
		}

		@Override
		public void onSuccess(XtomFileDownLoader loader) {
			if (pBar != null) {
				pBar.cancel();
			}
			install();
		}

		void install() {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(savePath)),
					"application/vnd.android.package-archive");
			mContext.startActivity(intent);
		}

		@Override
		public void onFailed(XtomFileDownLoader loader) {
			if (pBar != null) {
				pBar.cancel();
			}
			XtomToastUtil.showShortToast(mContext, "下载失败了");
		}

		@Override
		public void onLoading(XtomFileDownLoader loader) {
			FileInfo fileInfo = loader.getFileInfo();
			int curr = fileInfo.getCurrentLength();
			int cont = fileInfo.getContentLength();
			int per = (int) ((float) curr / (float) cont * 100);
			if (pBar != null) {
				pBar.setProgress(per);
			}
		}

		@Override
		public void onStop(XtomFileDownLoader loader) {
			if (pBar != null) {
				pBar.cancel();
			}
			XtomToastUtil.showShortToast(mContext, "下载停止");
			if (isMust())
				BaseUtil.clientLoginout(mContext);
		}
	}

	public abstract void NoNeedUpdate();
}
