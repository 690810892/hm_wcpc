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
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.hemaapp.wcpc_driver.module.SysInitInfo;


/**
 * 系统初始化信息数据库帮助类
 */
public class SysInfoDBHelper extends DBHelper {
	private static SysInfoDBHelper mClient;
	String tableName = SYSINITINFO;
	String columns = "sys_web_service,sys_plugins,sys_show_iospay, android_must_update,android_last_version,"
			+"iphone_must_update, iphone_last_version, sys_chat_ip,sys_chat_port,sys_pagesize,"
			+"sys_service_phone,android_update_url,iphone_update_url,"
			+ "iphone_comment_url,msg_invite, start_img, driver_android_must_update, driver_android_last_version_mer, "
			+ "driver_iphone_must_update_mer, driver_iphone_last_version_mer, driver_android_update_url, driver_iphone_update_url  ";

	String updateColumns = "sys_web_service=?, sys_plugins=?, sys_show_iospay=?, android_must_update=?, android_last_version=?, "
			+"iphone_must_update=?, iphone_last_version=?, sys_chat_ip=?,sys_chat_port=?,sys_pagesize=?,"
			+"sys_service_phone=?,android_update_url=?,iphone_update_url=?,"
			+ "iphone_comment_url=?,msg_invite=?, start_img=?, driver_android_must_update=?, driver_android_last_version_mer=?, "
			+ "driver_iphone_must_update_mer=?, driver_iphone_last_version_mer=?, driver_android_update_url=?, driver_iphone_update_url=?  ";

	/**
	 * 实例化系统初始化信息数据库帮助类
	 *
	 * @param context
	 */
	public SysInfoDBHelper(Context context) {
		super(context);
	}

	public static SysInfoDBHelper get(Context context){
		return mClient == null? mClient = new SysInfoDBHelper(context) : mClient;
	}

	public boolean insertOrUpdate(SysInitInfo info) {
		if (isExist()) {
			return update(info);
		} else {
			return insert(info);
		}
	}

	/**
	 * 插入一条记录
	 *
	 * @return 是否成功
	 */
	public boolean insert(SysInitInfo infor) {
		String sql = "insert into " + tableName + " (" + columns
				+ ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		Object[] bindArgs = new Object[] {
				infor.getSys_web_service(), infor.getSys_plugins(),
				infor.getSys_show_iospay(), infor.getAndroid_must_update(),
				infor.getAndroid_last_version(), infor.getIphone_must_update(),
				infor.getIphone_last_version(), infor.getSys_chat_ip(),
				infor.getSys_chat_port(), infor.getSys_pagesize(), infor.getSys_service_phone(),
				infor.getAndroid_update_url(), infor.getIphone_update_url(),
				infor.getIphone_comment_url(), infor.getMsg_invite(), infor.getStart_img(),
				infor.getDriver_android_must_update(), infor.getDriver_android_last_version_mer(),
				infor.getDriver_iphone_must_update_mer(), infor.getDriver_iphone_last_version_mer(),
				infor.getDriver_android_update_url(), infor.getDriver_iphone_update_url()};
		SQLiteDatabase db = getWritableDatabase();
		boolean success = true;
		try {
			db.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			success = false;
		}
		db.close();
		return success;
	}

	/**
	 * 更新
	 *
	 * @return 是否成功
	 */
	public boolean update(SysInitInfo infor) {
		int id = 1;
		String conditions = " where id=" + id;
		String sql = "update " + tableName + " set " + updateColumns
				+ conditions;
		Object[] bindArgs = new Object[] {
				infor.getSys_web_service(), infor.getSys_plugins(),
				infor.getSys_show_iospay(), infor.getAndroid_must_update(),
				infor.getAndroid_last_version(), infor.getIphone_must_update(),
				infor.getIphone_last_version(), infor.getSys_chat_ip(),
				infor.getSys_chat_port(), infor.getSys_pagesize(), infor.getSys_service_phone(),
				infor.getAndroid_update_url(), infor.getIphone_update_url(),
				infor.getIphone_comment_url(), infor.getMsg_invite(), infor.getStart_img(),
				infor.getDriver_android_must_update(), infor.getDriver_android_last_version_mer(),
				infor.getDriver_iphone_must_update_mer(), infor.getDriver_iphone_last_version_mer(),
				infor.getDriver_android_update_url(), infor.getDriver_iphone_update_url()};
		SQLiteDatabase db = getWritableDatabase();
		boolean success = true;
		try {
			db.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			success = false;
		}
		db.close();
		return success;
	}

	public boolean isExist() {
		int id = 1;
		String sql = ("select * from " + tableName + " where id=" + id);
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		boolean exist = cursor.getCount() > 0;
		cursor.close();
		db.close();
		return exist;
	}

	/**
	 * 清空
	 */
	public void clear() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from " + tableName);
		db.close();
	}

	/**
	 * 判断表是否为空
	 *
	 * @return
	 */
	public boolean isEmpty() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from " + tableName, null);
		boolean empty = 0 == cursor.getCount();
		cursor.close();
		db.close();
		return empty;
	}

	/**
	 * @return 系统初始化信息
	 */
	public SysInitInfo select() {
		int id = 1;
		String conditions = " where id=" + id;
		String sql = "select " + columns + " from " + tableName + conditions;

		SQLiteDatabase db = getWritableDatabase();
		SysInitInfo info = null;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			info = new SysInitInfo(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),cursor.getString(4),
					cursor.getString(5), cursor.getString(6),
					cursor.getString(7), cursor.getString(8),
					cursor.getInt(9), cursor.getString(10),
					cursor.getString(11), cursor.getString(12),
					cursor.getString(13), cursor.getString(14),
					cursor.getString(15), cursor.getString(16),
					cursor.getString(17), cursor.getString(18),
					cursor.getString(19), cursor.getString(20),
					cursor.getString(21));
		}
		cursor.close();
		db.close();
		return info;
	}
}
