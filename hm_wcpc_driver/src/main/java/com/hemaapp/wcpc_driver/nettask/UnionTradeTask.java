package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.UnionTrade;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/17.
 */
public class UnionTradeTask extends BaseNetTask {

    public UnionTradeTask(BaseHttpInformation information,
                          HashMap<String, String> params) {
        super(information, params);
    }

    public UnionTradeTask(BaseHttpInformation information,
                          HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<UnionTrade> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public UnionTrade parse(JSONObject jsonObject)
                throws DataParseException {
            return new UnionTrade(jsonObject);
        }

    }
}
