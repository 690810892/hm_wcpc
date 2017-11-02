package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.User;
import com.hemaapp.wcpc_driver.module.Workstatus;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 */
public class WorkStatusGetTask extends BaseNetTask {

    public WorkStatusGetTask(BaseHttpInformation information,
                             HashMap<String, String> params) {
        super(information, params);
    }

    public WorkStatusGetTask(BaseHttpInformation information,
                             HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<Workstatus> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public Workstatus parse(JSONObject jsonObject)
                throws DataParseException {
            return new Workstatus(jsonObject);
        }
    }
}



