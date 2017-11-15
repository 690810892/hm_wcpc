/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 */
	public class DBHelper extends SQLiteOpenHelper {
		private static final String DBNAME = "hm_wcpc_drivier02.db";
		/**
		 * 系统初始化信息
		 */
		protected static final String SYSINITINFO = "sysinfor";
		/**
		 * 当前登录用户信息
		 */
		protected static final String USER = "user";
		/**
		 * 搜索词缓存
		 */
		protected static final String SYS_CASCADE_SEARCH = "sys_cascade_search";


		public DBHelper(Context context) {
			super(context, DBNAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			String sys = "sys_web_service text,sys_plugins text,sys_show_iospay text, android_must_update text,"
					+ "android_last_version text, iphone_must_update text, iphone_last_version text,"
					+ "sys_chat_ip text, sys_chat_port text,sys_pagesize text,"
					+ "sys_service_phone text,android_update_url text,"
					+ "iphone_update_url text, iphone_comment_url text,msg_invite text, "
					+ "start_img text, driver_android_must_update text, driver_android_last_version_mer text, "
					+ "driver_iphone_must_update_mer text, driver_iphone_last_version_mer text, driver_android_update_url text, "
					+ "driver_iphone_update_url text ";
			String sysSQL = "create table " + SYSINITINFO
					+ " (id integer primary key," + sys + ")";
			// 创建系统初始化信息缓存表
			db.execSQL(sysSQL);

			// 创建当前登录用户信息缓存表
			String user = "id text primary key, username text, email text, realname text,mobile text, password text, paypassword text, "
					+ " sex text, avatar text, avatarbig text, lng text, lat text, IDtype text, IDnumber text, regdate text, franchisee_id text, "
					+ "servicecount text, loginflag text, feeaccount text, token text, android_must_update text, android_last_version text, android_update_url text,"
					+ "bankuser text, bankname text, bankcard text, bankmobile text, alipay_no text, mylength text," +
					" filenumber text ,alipay_name text, totalpoint text, replycount text, invitecode text, drivinglicense text, carbrand text, totalworktime text, todayworktime text ";
			String userSQL = "create table " + USER + " (" + user + ")";
			db.execSQL(userSQL);

			// 创建搜索词缓存表
			String search = "searchname text";
			String searchSQL = "create table " + SYS_CASCADE_SEARCH + " (" + search
					+ ")";
			db.execSQL(searchSQL);

		}

		@Override
		public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {

		}

	}
