package com.hemaapp.wcpc_driver;

import com.hemaapp.hm_FrameWork.HemaApplication;
import com.hemaapp.wcpc_driver.db.SysInfoDBHelper;
import com.hemaapp.wcpc_driver.db.UserDBHelper;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import java.util.Locale;

import xtom.frame.XtomConfig;
import xtom.frame.util.XtomLogger;
import xtom.frame.util.XtomSharedPreferencesUtil;

public class hm_WcpcDriverApplication extends HemaApplication {

	private static final String TAG = hm_WcpcDriverApplication.class.getSimpleName();
	private static hm_WcpcDriverApplication application;

	private SysInitInfo sysInitInfo;// 系统初始化信息
	private User user;

	@Override
	public void onCreate() {
		application = this;
		XtomConfig.LOG = BaseConfig.DEBUG;
		
		//是否只在wifi情况下加载图片
		String iow = XtomSharedPreferencesUtil.get(this, "imageload_onlywifi");
		XtomConfig.IMAGELOAD_ONLYWIFI = "true".equals(iow);
		
		//压缩图片的最大大小
		XtomConfig.MAX_IMAGE_SIZE = 400;
		Locale.setDefault(Locale.CHINESE);
		
		//是否进行数字加密
		XtomConfig.DIGITAL_CHECK = true;
		XtomConfig.DATAKEY = "hYtd0QRR4aituQs"; //后台配置的加密内容，每个项目都不同
		
		XtomLogger.i(TAG, "onCreate");

		//科大讯飞语音播报需要的内容
		SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5754d59f");
		super.onCreate();
	}

	public static hm_WcpcDriverApplication getInstance() {
		return application;
	}
	

	/**
	 * @return 当前用户
	 */
	public User getUser() {
		if (user == null) {
			UserDBHelper helper = new UserDBHelper(this);
			String username = XtomSharedPreferencesUtil.get(this, "username");
			if (username == null || "".equals(username)
					|| "null".equals(username))
				return null;
			else {
				if ("false".equals(XtomSharedPreferencesUtil.get(application,
						"isAutoLogin")))
					return null;
				else {
					user = helper.selectByUsername(username);
					helper.close();
				}
			}
		}
		return user;
	}

	/**
	 * 设置保存当前用户
	 * 
	 * @param user
	 *            当前用户
	 */
	public void setUser(User user) {
		this.user = user;
		if (user != null) {
			UserDBHelper helper = new UserDBHelper(this);
			helper.insertOrUpdate(user);
			helper.close();
		}
	}

	/**
	 * @return 系统初始化信息
	 */
	public SysInitInfo getSysInitInfo() {
		if (sysInitInfo == null) {
			SysInfoDBHelper helper = new SysInfoDBHelper(this);
			sysInitInfo = helper.select();
			helper.close();
		}
		return sysInitInfo;
	}

	/**
	 * 设置保存系统初始化信息
	 * 
	 * @param sysInitInfo
	 *            系统初始化信息
	 */
	public void setSysInitInfo(SysInitInfo sysInitInfo) {
		this.sysInitInfo = sysInitInfo;
		if (sysInitInfo != null) {
			SysInfoDBHelper helper = new SysInfoDBHelper(this);
			helper.insertOrUpdate(sysInitInfo);
			helper.close();
		}
	}

}
