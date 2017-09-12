package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.Driver;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/6/14.
 */
public class DriverGetTask extends BaseNetTask {

    public DriverGetTask(BaseHttpInformation information,
                         HashMap<String, String> params) {
        super(information, params);
    }

    public DriverGetTask(BaseHttpInformation information,
                         HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<Driver> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public Driver parse(JSONObject jsonObject)
                throws DataParseException {
            return new Driver(jsonObject);
        }
    }
}
