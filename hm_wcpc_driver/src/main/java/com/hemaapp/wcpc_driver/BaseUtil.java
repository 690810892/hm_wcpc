/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.text.SpannableString;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hemaapp.hm_FrameWork.emoji.EmojiParser;
import com.hemaapp.hm_FrameWork.emoji.ParseEmojiMsgUtil;
import com.hemaapp.wcpc_driver.activity.LoginActivity;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import xtom.frame.XtomActivityManager;
import xtom.frame.util.XtomSharedPreferencesUtil;
import xtom.frame.util.XtomTimeUtil;

import static android.content.Context.INPUT_METHOD_SERVICE;

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

    /**
     * 秒转化为天小时分秒字符串
     *
     * @param seconds
     * @return String
     */
    public static String formatSeconds(long seconds) {
        //String timeStr = seconds + "秒";
        String timeStr = "0分";
        if (seconds > 60) {
            long second = seconds % 60;
            long min = seconds / 60;
//			timeStr = min + "分" + second + "秒";
            timeStr = min + "分";
            if (min > 60) {
                min = (seconds / 60) % 60;
                long hour = (seconds / 60) / 60;
                //timeStr = hour + "小时" + min + "分" + second + "秒";
                timeStr = hour + "小时" + min + "分";
                if (hour > 24) {
                    hour = ((seconds / 60) / 60) % 24;
                    long day = (((seconds / 60) / 60) / 24);
                    //timeStr = day + "天" + hour + "小时" + min + "分" + second + "秒";
                    timeStr = day + "天" + hour + "小时" + min + "分";
                }
            }
        }
        return timeStr;
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

    //除
    public static String divide(String v1, String v2, int scale) {
        //如果精确范围小于0，抛出异常信息
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static void hideInput(Context context, View v) {
        ((InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
                (v.getWindowToken(), 0);
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

    public static String getTime2(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 退出登录
     */
    public static void clientLoginout(Context context) {
        XtomSharedPreferencesUtil.save(context, "password", "");
        XtomSharedPreferencesUtil.save(context, "username", "");
        hm_WcpcDriverApplication application = hm_WcpcDriverApplication.getInstance();
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
     * @param time 时间字符串
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

            calendar.add(Calendar.DAY_OF_YEAR, 2);// 日期加2天
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
                d48 = sdf.parse(dian48);
                d72 = sdf.parse(dian72);
                d24 = sdf.parse(dian24);
                d00 = sdf.parse(dian00);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//			long diff = now.getTime() - date.getTime(); // 获取二者之间的时间差值
//			long min = diff / (60 * 1000);
//			if (min <= 5)
//				return "刚刚";
//			if (min < 60)
//				return min + "分钟前";

            if (date.getTime() <= d24.getTime()
                    && date.getTime() >= d00.getTime())
                return "今天" + XtomTimeUtil.TransTime(time, "HH:mm");

            if (date.getTime() >= d24.getTime() && date.getTime() <= d48.getTime())
                return "明天" + XtomTimeUtil.TransTime(time, "HH:mm");

            if (date.getTime() >= d48.getTime() && date.getTime() <= d72.getTime())
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
     * 获取MD5的加密秘钥
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 计算百分率 if(x>y) (x-y)/total else (y-x)/total value 是除数 2 total 是被除数 3
     */
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
     */
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
     */
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
     */
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

    public static String get2double(double data) {

        DecimalFormat df2 = new DecimalFormat("###.00");
        String value = df2.format(data);
        return value;
    }

    /**
     * 传入要显示评分的五个ImageView和分数 根据四舍五入来计算分数
     */
    public static void transScore(ImageView image_0, ImageView image_1,
                                  ImageView image_2, ImageView image_3, ImageView image_4,
                                  String totalcount, String score) {
        int point = 0;
        if ("".equals(totalcount) || null == totalcount || "null".equals(totalcount)
                || "0".equals(totalcount))
            point = 0;
        else if ("".equals(score) || null == score || "null".equals(score)
                || "0".equals(score))
            point = 0;
        else {
            int t = Integer.parseInt(totalcount);
            int s = Integer.parseInt(score);

            double d = s / t;
            DecimalFormat df2 = new DecimalFormat("###.00");
            String value = df2.format(d);
            double dd = Double.parseDouble(value);
            if (dd >= 0 && dd < 0.5)
                point = 0;
            else if (dd < 1.5)
                point = 1;
            else if (dd < 2.5)
                point = 2;
            else if (dd < 3.5)
                point = 3;
            else if (dd < 4.5)
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

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean net = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || net) {
            return true;
        }
        return false;

    }
}
