/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.emoji.EmojiParser;
import com.hemaapp.hm_FrameWork.emoji.ParseEmojiMsgUtil;
import com.hemaapp.wcpc_user.activity.LoginActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import xtom.frame.XtomActivityManager;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomTimeUtil;

/**
 * 工具类
 */
public class BaseUtil {
	private static double EARTH_RADIUS = 6378.137;// 地球半径

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static String transDuration(long duration) {
		String ds = "";
		long min = duration / 60;
		if (min < 60) {
			ds += (min + "分钟");
		} else {
			long hour = min / 60;
			long rm = min % 60;
			if (rm > 0)
				ds += (hour + "小时" + rm + "分钟");
			else
				ds += (hour + "小时");
		}
		return ds;
	}

	public static String transDistance(float distance) {
		String ds = "";
//		if (distance < 1000) {
//			ds += (distance + "m");
//		} else {
			float km = distance / 1000;
			ds += (String.format(Locale.getDefault(), "%.3f", km));
//		}
		return ds;
	}

	/**
	 * 计算两点间的距离
	 * 
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static Double GetDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);

		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**
	 * 退出登录
	 */
	public static void clientLoginout(Context context) {
		XtomSharedPreferencesUtil.save(context, "password", "");
		XtomSharedPreferencesUtil.save(context, "username", "");
		hm_WcpcUserApplication application = hm_WcpcUserApplication.getInstance();
		application.setUser(null);
		XtomActivityManager.finishAll();
		Intent it = new Intent(context, LoginActivity.class);
		context.startActivity(it);
	}

	/**
	 * 隐藏用户名
	 * 
	 * @param nickname
	 * @return
	 */
	public static String hideNickname(String nickname) {
		int length = nickname.length();
		String first = nickname.substring(0, 1);
		String last = nickname.substring(length - 1, length);
		String x = "";
		for (int i = 0; i < length - 2; i++) {
			x += "*";
		}
		return first + x + last;
	}

	/**
	 * 转换时间显示形式(与当前系统时间比较),在显示即时聊天的时间时使用
	 * 
	 * @param time
	 *            时间字符串
	 * @return String
	 */
	public static String transTimeChat(String time) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault());
			String current = XtomTimeUtil.getCurrentTime("yyyy-MM-dd HH:mm:ss");
			String dian24 = XtomTimeUtil.TransTime(current, "yyyy-MM-dd")
					+ " 24:00:00";
			String dian00 = XtomTimeUtil.TransTime(current, "yyyy-MM-dd")
					+ " 00:00:00";

			java.util.Date dt = new java.util.Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			calendar.add(Calendar.DAY_OF_YEAR, 1);// 日期加1天
			java.util.Date dt1 = calendar.getTime();
			String dian48 = XtomTimeUtil.TransTime(sdf.format(dt1), "yyyy-MM-dd")
					+ " 24:00:00";

			calendar.add(Calendar.DAY_OF_YEAR, 1);// 日期加2天
			java.util.Date dt2 = calendar.getTime();
			String dian72 = XtomTimeUtil.TransTime(sdf.format(dt2), "yyyy-MM-dd")
					+ " 24:00:00";

//			Date now = null;
			Date date = null;
			Date d24 = null;
			Date d00 = null;
			Date d48 = null;
			Date d72 = null;
			try {
//				now = sdf.parse(current); // 将当前时间转化为日期
				date = sdf.parse(time); // 将传入的时间参数转化为日期
				d00 = sdf.parse(dian00);
				d24 = sdf.parse(dian24);
				d48 = sdf.parse(dian48);
				d72 = sdf.parse(dian72);
			} catch (ParseException e) {
				e.printStackTrace();
			}


			if (date.getTime() <= d24.getTime()
					&& date.getTime() >= d00.getTime())
				return "今天" + XtomTimeUtil.TransTime(time, "HH:mm");

			if(date.getTime() >= d24.getTime() && date.getTime() <= d48.getTime() )
				return "明天" + XtomTimeUtil.TransTime(time, "HH:mm");

			if(date.getTime() >= d48.getTime() && date.getTime() <= d72.getTime() )
				return "后天" + XtomTimeUtil.TransTime(time, "HH:mm");

			int sendYear = Integer
					.valueOf(XtomTimeUtil.TransTime(time, "yyyy"));
			int nowYear = Integer.valueOf(XtomTimeUtil.TransTime(current,
					"yyyy"));
			if (sendYear < nowYear)
				return XtomTimeUtil.TransTime(time, "yyyy-MM-dd HH:mm");
			else
				return XtomTimeUtil.TransTime(time, "MM-dd HH:mm");

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 计算百分率 if(x>y) (x-y)/total else (y-x)/total value 是除数 2 total 是被除数 3
	 * */
	public static String getValue(double x, double y, double total) {
		String result = "";// 接受百分比的值
		String flag = "+"; // 数据的正负值
		DecimalFormat df1 = new DecimalFormat("0.00%"); // ##.00%
														// 百分比格式，后面不足2位的用0补齐
		if (x >= y) {
			flag = "+";
			double tempresult = (x - y) / total;
			result = df1.format(tempresult);
		} else {
			flag = "-";
			double tempresult = (y - x) / total;
			result = df1.format(tempresult);
		}
		return flag + result;
	}

	/**
	 * 计算好评率
	 * */
	public static String getRate(int x, int total) {
		String result = "";// 接受百分比的值
		String flag = "+"; // 数据的正负值
		DecimalFormat df1 = new DecimalFormat("0.0%"); // ##.00%
														// 百分比格式，后面不足2位的用0补齐
		double tempresult = x / total;
		result = df1.format(tempresult);
		return flag + result;
	}

	/**
	 * 获利
	 * */
	public static String income(String old, String now, String count) {
		String result = "";
		String flag = "+";
		Double x = Double.parseDouble(old);
		Double y = Double.parseDouble(now);
		int cou = Integer.parseInt(count);
		if (x >= y) {
			double t = x - y;
			double c = t * cou;
			flag = "-";
			result = String.valueOf(c);
		} else {
			double t = y - x;
			double c = t * cou;
			flag = "+";
			result = String.valueOf(c);
		}
		return flag + result;
	}

	// 聊天中的表情
	public static void SetMessageTextView(Context mContext, TextView mtextview,
                                          String mcontent) {
		if (mcontent == null || "".equals(mcontent)) {
			mtextview.setText("");
			return;
		}

		String unicode = EmojiParser.getInstance(mContext).parseEmoji(mcontent);
		SpannableString spannableString = ParseEmojiMsgUtil
				.getExpressionString(mContext, unicode);
		mtextview.setText(spannableString);
	}

	/**
	 * 计算缓存大小的表现形式
	 * */
	public static String getSize(long size) {

		/** size 如果 小于1024 * 1024,以KB单位返回,反则以MB单位返回 */

		DecimalFormat df = new DecimalFormat("###.##");
		float f;
		if (size < 1024 * 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "KB");
		} else {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "MB");
		}
	}

	
	public static String transString(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd",
				Locale.getDefault());
		String str = sdf.format(d);
		return str;
	}
	
	public static String transString1(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		String str = sdf.format(d);
		return str;
	}
	
	public static String get2double(double data){
		DecimalFormat df2 = new DecimalFormat("###.00");
		String value = df2.format(data);
		if(data == 0)
			value = "0.00";
		return value;
	}

	/**
	 * 传入要显示评分的五个ImageView和分数 根据四舍五入来计算分数
	 * */
	public static void transScore(ImageView image_0, ImageView image_1,
                                  ImageView image_2, ImageView image_3, ImageView image_4,
                                  String totalcount, String score) {
		int point = 0;
		if("".equals(totalcount) || null == totalcount || "null".equals(totalcount)
				|| "0".equals(totalcount))
			point = 0;
		else if("".equals(score) || null == score || "null".equals(score)
				|| "0".equals(score))
			point = 0;
		else {
			int t = Integer.parseInt(totalcount);
			int s = Integer.parseInt(score);

			double d = s/t;
			DecimalFormat df2 = new DecimalFormat("###.00");
			String value = df2.format(d);
			double dd = Double.parseDouble(value);
			if(dd >= 0 && dd < 0.5)
				point = 0;
			else if( dd < 1.5)
				point = 1;
			else if(dd < 2.5)
				point = 2;
			else if(dd < 3.5)
				point = 3;
			else if(dd < 4.5)
				point = 4;
			else {
				point = 5;
			}
		}

		if (point == 0) {
			image_0.setImageResource(R.mipmap.img_star_n);
			image_1.setImageResource(R.mipmap.img_star_n);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 1) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_n);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 2) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 3) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 4) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_s);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 5) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_s);
			image_4.setImageResource(R.mipmap.img_star_s);
		}
	}

	public static void transScoreByPoint(ImageView image_0, ImageView image_1,
                                         ImageView image_2, ImageView image_3, ImageView image_4,
                                         String count) {
		int point = Integer.parseInt(count);

		if (point == 0) {
			image_0.setImageResource(R.mipmap.img_star_n);
			image_1.setImageResource(R.mipmap.img_star_n);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 1) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_n);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 2) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_n);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 3) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_n);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 4) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_s);
			image_4.setImageResource(R.mipmap.img_star_n);
		} else if (point == 5) {
			image_0.setImageResource(R.mipmap.img_star_s);
			image_1.setImageResource(R.mipmap.img_star_s);
			image_2.setImageResource(R.mipmap.img_star_s);
			image_3.setImageResource(R.mipmap.img_star_s);
			image_4.setImageResource(R.mipmap.img_star_s);
		}
	}

	public static void transScoreByPoint1(ImageView image_0, ImageView image_1,
                                          ImageView image_2, ImageView image_3, ImageView image_4,
                                          String count) {
		int point = Integer.parseInt(count);

		if (point == 0) {
			image_0.setImageResource(R.mipmap.img_pingjia_n);
			image_1.setImageResource(R.mipmap.img_pingjia_n);
			image_2.setImageResource(R.mipmap.img_pingjia_n);
			image_3.setImageResource(R.mipmap.img_pingjia_n);
			image_4.setImageResource(R.mipmap.img_pingjia_n);
		} else if (point == 1) {
			image_0.setImageResource(R.mipmap.img_pingjia_s);
			image_1.setImageResource(R.mipmap.img_pingjia_n);
			image_2.setImageResource(R.mipmap.img_pingjia_n);
			image_3.setImageResource(R.mipmap.img_pingjia_n);
			image_4.setImageResource(R.mipmap.img_pingjia_n);
		} else if (point == 2) {
			image_0.setImageResource(R.mipmap.img_pingjia_s);
			image_1.setImageResource(R.mipmap.img_pingjia_s);
			image_2.setImageResource(R.mipmap.img_pingjia_n);
			image_3.setImageResource(R.mipmap.img_pingjia_n);
			image_4.setImageResource(R.mipmap.img_pingjia_n);
		} else if (point == 3) {
			image_0.setImageResource(R.mipmap.img_pingjia_s);
			image_1.setImageResource(R.mipmap.img_pingjia_s);
			image_2.setImageResource(R.mipmap.img_pingjia_s);
			image_3.setImageResource(R.mipmap.img_pingjia_n);
			image_4.setImageResource(R.mipmap.img_pingjia_n);
		} else if (point == 4) {
			image_0.setImageResource(R.mipmap.img_pingjia_s);
			image_1.setImageResource(R.mipmap.img_pingjia_s);
			image_2.setImageResource(R.mipmap.img_pingjia_s);
			image_3.setImageResource(R.mipmap.img_pingjia_s);
			image_4.setImageResource(R.mipmap.img_pingjia_n);
		} else if (point == 5) {
			image_0.setImageResource(R.mipmap.img_pingjia_s);
			image_1.setImageResource(R.mipmap.img_pingjia_s);
			image_2.setImageResource(R.mipmap.img_pingjia_s);
			image_3.setImageResource(R.mipmap.img_pingjia_s);
			image_4.setImageResource(R.mipmap.img_pingjia_s);
		}
	}
}
