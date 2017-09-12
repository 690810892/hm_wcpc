package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;
import com.hemaapp.wcpc_user.module.ReplyListInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/13.
 */
public class ReplyListTask extends BaseNetTask {

    public ReplyListTask(BaseHttpInformation information,
                         HashMap<String, String> params) {
        super(information, params);
    }
    public ReplyListTask(BaseHttpInformation information,
                         HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaPageArrayResult<ReplyListInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public ReplyListInfor parse(JSONObject jsonObject)
                throws DataParseException {
            return new ReplyListInfor(jsonObject);
        }
    }
}


