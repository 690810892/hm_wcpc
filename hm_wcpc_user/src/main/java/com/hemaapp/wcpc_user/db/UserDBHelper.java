/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.hemaapp.wcpc_user.module.User;


/**
 * 用户信息数据库帮助类
 */
public class UserDBHelper extends DBHelper {
	String tableName = USER;

	private static UserDBHelper mClient;

	public UserDBHelper(Context context) {
		super(context);
	}

	public static UserDBHelper get(Context context) {
		return mClient == null ? mClient = new UserDBHelper(context) : mClient;
	}

	/**
	 * 判断信息是插入还是更新
	 * */
	public boolean insertOrUpdate(User user) {
		if (isExist(user.getId()))
			return update(user);
		else
			return insert(user);
	}

	/**
	 * 用户不存在的情况下，将数据插入表中
	 * */
	public boolean insert(User user) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		try {
			String sql = ("insert into "
					+ USER
					+ " ( id, username, email, realname, mobile, password, paypassword, sex, avatar, avatarbig, lng, lat, IDtype, "
					+ "IDnumber, regdate, takecount, feeaccount, token, android_must_update, android_last_version, android_update_url, carbrand, carnumber, score, "
					+ "bankuser, bankname, bankcard, bankmobile, alipay_no,invitecode,today_cancel_count ) "
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			Object[] bindArgs = new Object[] { user.getId(),
					user.getUsername(), user.getEmail(), user.getRealname(),
					user.getMobile(),user.getPassword(), user.getPaypassword(),
					user.getSex(), user.getAvatar(), user.getAvatarbig(),
					user.getLng(), user.getLat(), user.getIDtype(), user.getIDnumber(),
					user.getRegdate(),  user.getTakecount(), user.getFeeaccount(),
					user.getToken(), user.getAndroid_must_update(),
					user.getAndroid_last_version(), user.getAndroid_update_url(),
					user.getCarbrand(), user.getCarnumber(), user.getScore(),
			user.getBankuser(), user.getBankname(), user.getBankcard(), user.getBankmobile(),
			user.getAlipay_no(),user.getInvitecode(),user.getToday_cancel_count()};
			db.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			success = false;
		}
		db.close();
		return success;
	}

	/**
	 * 用户已经存在的情况下，更新用户的信息
	 * */
	public boolean update(User user) {
		SQLiteDatabase db = getWritableDatabase();
		boolean success = true;
		try {
			String sql = ("update "
					+ USER
					+ " set id=?, username=?, email=?, realname=?, mobile=?, password=?, paypassword=?, sex=?, avatar=?, avatarbig=?, lng=?, lat=?, IDtype=?, "
					+ "IDnumber=?, regdate=?, takecount=?, feeaccount=?, token=?, android_must_update=?, android_last_version=?, android_update_url=?, carbrand=?, carnumber=?, score=?,"
					+ "bankuser=?, bankname=?, bankcard=?, bankmobile=?, alipay_no=?, invitecode=?,today_cancel_count=?  "
					+ " where id = '" + user.getId() + "'");
			Object[] bindArgs = new Object[] { user.getId(),
					user.getUsername(), user.getEmail(), user.getRealname(),
					user.getMobile(),user.getPassword(), user.getPaypassword(),
					user.getSex(), user.getAvatar(), user.getAvatarbig(),
					user.getLng(), user.getLat(), user.getIDtype(), user.getIDnumber(),
					user.getRegdate(),  user.getTakecount(), user.getFeeaccount(),
					user.getToken(), user.getAndroid_must_update(),
					user.getAndroid_last_version(), user.getAndroid_update_url(),
					user.getCarbrand(), user.getCarnumber(), user.getScore(),
			user.getBankuser(), user.getBankname(), user.getBankcard(), user.getBankmobile(),
			user.getAlipay_no(),user.getInvitecode(),user.getToday_cancel_count()};
			db.execSQL(sql, bindArgs);
		} catch (SQLException e) {
			success = false;
		}
		db.close();
		return success;
	}

	/**
	 * 根据用户的id,来判断这个用户的信息是否存在表中
	 * */
	public boolean isExist(String id) {
		SQLiteDatabase db = getWritableDatabase();
		String sql = ("select * from " + USER + " where id = '" + id + "'");
		Cursor cursor = db.rawQuery(sql, null);
		boolean isExist = cursor != null && cursor.getCount() > 0;
		cursor.close();
		db.close();
		return isExist;
	}

	/**
	 * 删除一条记录
	 * */
	public boolean delete(String uid) {
		boolean success = true;
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL("delete from " + USER + " where id ='" + uid + "'");
		} catch (SQLException e) {
			success = false;
		}
		db.close();
		return success;
	}

	/**
	 * 清空
	 * */
	public void clear() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from " + USER);
		db.close();
	}

	/**
	 * 判断用户表是否为空
	 * */
	public boolean isEmpty() {
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from " + USER, null);
		boolean empty = 0 == cursor.getCount();
		cursor.close();
		db.close();
		return empty;  
	}

	/**
	 * 获取用户信息
	 * 
	 * @param username
	 * */
	public User selectByUsername(String username) {
		User user = null;
		String sql = "select id, username, email, realname, mobile, password, paypassword, sex, avatar, avatarbig, lng, lat, IDtype,"
		+" IDnumber, regdate, takecount, feeaccount, token, android_must_update, android_last_version, android_update_url, carbrand, carnumber, score,"
				+ "bankuser, bankname, bankcard, bankmobile, alipay_no,invitecode,today_cancel_count from "
		+ USER + " where username = '" + username + "'";
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			user = new User(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3),
					cursor.getString(4), cursor.getString(5),
					cursor.getString(6), cursor.getString(7),
					cursor.getString(8), cursor.getString(9),
					cursor.getString(10), cursor.getString(11),
					cursor.getString(12), cursor.getString(13),
					cursor.getString(14), cursor.getString(15),
					cursor.getString(16), cursor.getString(17),
					cursor.getString(18), cursor.getString(19),
					cursor.getString(20), cursor.getString(21),
					cursor.getString(22), cursor.getString(23),
					cursor.getString(24), cursor.getString(25),
					cursor.getString(26), cursor.getString(27),
					cursor.getString(28),cursor.getString(29)
					,cursor.getString(30));
		}
		cursor.close();
		db.close();
		return user;
	}
}
