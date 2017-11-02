package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.DistrictInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * 获取已开通（城市）列表信息
 * */
public class CityListTask extends BaseNetTask {

	public CityListTask(BaseHttpInformation information,
                        HashMap<String, String> params) {
		super(information, params);
	}

	public CityListTask(BaseHttpInformation information,
                        HashMap<String, String> params, HashMap<String, String> files) {
		super(information, params, files);
	}

	@Override
	public Object parse(JSONObject jsonObject) throws DataParseException {
		return new Result(jsonObject);
	}

	private class Result extends HemaArrayResult<DistrictInfor> {

		public Result(JSONObject jsonObject) throws DataParseException {
			super(jsonObject);
		}

		@Override
		public DistrictInfor parse(JSONObject jsonObject) throws DataParseException {
			return new DistrictInfor(jsonObject);
		}

	}
}
