package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.DistrictInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * 获取地区（城市）列表信息
 * */
public class DistrictListTask extends BaseNetTask {

	public DistrictListTask(BaseHttpInformation information,
			HashMap<String, String> params) {
		super(information, params);
	}
	
	public DistrictListTask(BaseHttpInformation information,
                            HashMap<String, String> params, HashMap<String, String> files) {
		super(information, params, files);
	}

	@Override
	public Object parse(JSONObject jsonObject) throws DataParseException {
		return new Result(jsonObject);
	}

	private class Result extends HemaPageArrayResult<DistrictInfor> {

		public Result(JSONObject jsonObject) throws DataParseException {
			super(jsonObject);
		}

		@Override
		public DistrictInfor parse(JSONObject jsonObject) throws DataParseException {
			return new DistrictInfor(jsonObject);
		}

	}
}
