package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.TimeRule;
import com.hemaapp.wcpc_user.module.User;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 */
public class TimeRuleGetTask extends BaseNetTask {

    public TimeRuleGetTask(BaseHttpInformation information,
                           HashMap<String, String> params) {
        super(information, params);
    }

    public TimeRuleGetTask(BaseHttpInformation information,
                           HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<TimeRule> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public TimeRule parse(JSONObject jsonObject)
                throws DataParseException {
            return new TimeRule(jsonObject);
        }
    }
}



