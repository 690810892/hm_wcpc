/*
 * Copyright (C) 2014 The Android Client Of Demo Project
 * 
 *     The BeiJing PingChuanJiaHeng Technology Co., Ltd.
 * 
 * Author:Yang ZiTian
 * You Can Contact QQ:646172820 Or Email:mail_yzt@163.com
 */
package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * 文件上传结果
 */
public class FileUploadResult extends XtomObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String item1;// 请查看标准化api文档
	private String item2;// 请查看标准化api文档

	public FileUploadResult(JSONObject jsonObject) throws DataParseException {
		if (jsonObject != null) {
			try {
				item1 = get(jsonObject, "item1");
				item2 = get(jsonObject, "item2");

				log_i(toString());
			} catch (JSONException e) {
				throw new DataParseException(e);
			}
		}
	}

	@Override
	public String toString() {
		return "FileUploadResult [item1=" + item1 + ", item2=" + item2 + "]";
	}

	/**
	 * @return the item1
	 */
	public String getItem1() {
		return item1;
	}

	/**
	 * @return the item2
	 */
	public String getItem2() {
		return item2;
	}

}
