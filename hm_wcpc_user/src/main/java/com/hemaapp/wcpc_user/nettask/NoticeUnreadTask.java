package com.hemaapp.wcpc_user.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_user.BaseHttpInformation;
import com.hemaapp.wcpc_user.BaseNetTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/5.
 */
public class NoticeUnreadTask extends BaseNetTask {

    public NoticeUnreadTask(BaseHttpInformation information,
                            HashMap<String, String> params) {
        super(information, params);
    }

    public NoticeUnreadTask(BaseHttpInformation information,
                            HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<String> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public String parse(JSONObject jsonObject) throws DataParseException {
            try {
                return get(jsonObject, "count");
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

}

