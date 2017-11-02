package com.hemaapp.wcpc_driver;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.hemaapp.hm_FrameWork.HemaApplication;
import com.hemaapp.wcpc_driver.db.SysInfoDBHelper;
import com.hemaapp.wcpc_driver.db.UserDBHelper;
import com.hemaapp.wcpc_driver.module.SysInitInfo;
import com.hemaapp.wcpc_driver.module.User;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.L;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
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
		XtomConfig.DATAKEY = "9qk2hKHaRTysJqCS"; //后台配置的加密内容，每个项目都不同
		
		XtomLogger.i(TAG, "onCreate");

		//科大讯飞语音播报需要的内容
		SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=5754d59f");
		initImageLoader();
		super.onCreate();
	}

	public static hm_WcpcDriverApplication getInstance() {
		return application;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);//突破65535限制
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
	/**
	 * 初始化imageLoader
	 */
	@SuppressWarnings("deprecation")
	public void initImageLoader() {
		L.writeLogs(false);
		File cacheDir = StorageUtils.getCacheDirectory(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽

				.threadPoolSize(3)//线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(1 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(256 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100) //缓存的文件数量
				.discCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();//开始构建
		ImageLoader.getInstance().init(config);
	}

	@SuppressWarnings("deprecation")
	public DisplayImageOptions getOptions(int drawableId) {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(drawableId)
				.showImageForEmptyUri(drawableId)
				.showImageOnFail(drawableId)
				.resetViewBeforeLoading(true)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.build();
	}

}
