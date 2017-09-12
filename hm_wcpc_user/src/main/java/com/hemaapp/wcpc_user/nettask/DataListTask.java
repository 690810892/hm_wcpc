package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.DataInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/20.
 */
public class DataListTask extends BaseNetTask {

    public DataListTask(BaseHttpInformation information,
                        HashMap<String, String> params) {
        super(information, params);
    }

    public DataListTask(BaseHttpInformation information,
                        HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaPageArrayResult<DataInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public DataInfor parse(JSONObject jsonObject) throws DataParseException {
            return new DataInfor(jsonObject);
        }

    }
}

