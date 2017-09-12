/*
 * Copyright (C) 2014 The Android Client Of QK Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * 
 */
public class PopItem extends XtomObject {
	public String name;
	public String id; //获取二级分类时用
	public boolean checked;

	/**
	 * @param name
	 * @param checked
	 */
	public PopItem(String name, String id, boolean checked) {
		super();
		this.name = name;
		this.id = id;
		this.checked = checked;
	}
	
	public PopItem(JSONObject jsonObject) throws DataParseException {
		if(jsonObject != null){
			try {
				id = get(jsonObject, "id");
				name = get(jsonObject, "name");
				
				log_i(toString());
			} catch (JSONException e) {
				throw new DataParseException(e);
			}
		}
	}

	@Override
	public String toString() {
		return "PopItem [name=" + name + ", id=" + id + ", checked=" + checked
				+ "]";
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
	
	
}
