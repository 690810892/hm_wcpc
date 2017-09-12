package com.hemaapp.wcpc_user.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * 搜索历史DB
 */
public class SearchDBClient extends DBHelper {
	private static SearchDBClient mClient;
	@SuppressWarnings("unused")
	private static Context mContext;

	private String tableName = SYS_CASCADE_SEARCH;

	private SearchDBClient(Context context) {
		super(context);
	}

	public static SearchDBClient get(Context context) {
		mContext = context;
		return mClient == null ? mClient = new SearchDBClient(context)
				: mClient;
	}

	/**
	 * 插入一条记录
	 * 
	 * @return
	 */
	public boolean insert(String search) {
		SQLiteDatabase db = getWritableDatabase();
		boolean success = true;
		try {
			db.execSQL(
					("insert into " + tableName + "(searchname) " + "values (?)"),
					new Object[] { search });

		} catch (SQLException e) {
			success = false;
			Log.w("insert", "insert e=" + e);
		}
		db.close();
		Log.w("insert", "insert searchname success");
		return success;
	}
	
	/**
	 * 删除一条记录
	 * */
	public void delete(String value){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("delete from " + tableName+" where searchname = '"+ value+"';");
		db.close();
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
		int num = cursor.getCount();
		cursor.close();
		db.close();
		return 0 == num;
	}

	/**
	 * 获取
	 * 
	 * @return
	 */
	public ArrayList<String> select() {
		ArrayList<String> counts = null;
		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select * from " + tableName, null);
			if (cursor != null && cursor.getCount() > 0) {
				counts = new ArrayList<String>();
				cursor.moveToFirst();
				String count;
				for (int i = 0; i < cursor.getCount(); i++) {
					count = cursor.getString(0);
					counts.add(0, count);
					cursor.moveToNext();
				}
			}

		} catch (Exception e) {
			Log.w("select", "select e=" + e);
		}
		if (cursor != null)
			cursor.close();
		db.close();
		return counts;
	}
}
