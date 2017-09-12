package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.OrderDetailInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/23.
 */
public class DriverOrderGetTask extends BaseNetTask {

    public DriverOrderGetTask(BaseHttpInformation information,
                              HashMap<String, String> params) {
        super(information, params);
    }

    public DriverOrderGetTask(BaseHttpInformation information,
                              HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaArrayResult<OrderDetailInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public OrderDetailInfor parse(JSONObject jsonObject)
                throws DataParseException {
            return new OrderDetailInfor(jsonObject);
        }
    }
}


