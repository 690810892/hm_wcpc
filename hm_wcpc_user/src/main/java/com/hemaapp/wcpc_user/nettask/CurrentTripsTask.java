package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.CurrentTripsInfor;
import com.hemaapp.wcpc_user.module.User;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by wangyuxia on 2017/9/29.
 */

public class CurrentTripsTask extends BaseNetTask {

    public CurrentTripsTask(BaseHttpInformation information,
                         HashMap<String, String> params) {
        super(information, params);
    }

    public CurrentTripsTask(BaseHttpInformation information,
                         HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<CurrentTripsInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public CurrentTripsInfor parse(JSONObject jsonObject)
                throws DataParseException {
            return new CurrentTripsInfor(jsonObject);
        }
    }
}

