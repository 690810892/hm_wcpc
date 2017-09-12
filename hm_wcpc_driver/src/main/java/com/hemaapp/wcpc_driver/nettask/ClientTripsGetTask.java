package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.TripListInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/13.
 */
public class ClientTripsGetTask extends BaseNetTask {

    public ClientTripsGetTask(BaseHttpInformation information,
                              HashMap<String, String> params) {
        super(information, params);
    }

    public ClientTripsGetTask(BaseHttpInformation information,
                              HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<TripListInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public TripListInfor parse(JSONObject jsonObject)
                throws DataParseException {
            return new TripListInfor(jsonObject);
        }
    }
}


