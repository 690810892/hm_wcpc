package com.hemaapp.wcpc_driver.nettask;

import com.hemaapp.hm_FrameWork.result.HemaPageArrayResult;
import com.hemaapp.wcpc_driver.BaseHttpInformation;
import com.hemaapp.wcpc_driver.BaseNetTask;
import com.hemaapp.wcpc_driver.module.OrderListInfor;

import org.json.JSONObject;

import java.util.HashMap;

import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/19.
 */
public class DriverOrderListTask extends BaseNetTask {

    public DriverOrderListTask(BaseHttpInformation information,
                               HashMap<String, String> params) {
        super(information, params);
    }
    public DriverOrderListTask(BaseHttpInformation information,
                               HashMap<String, String> params, HashMap<String, String> files) {
        super(information, params, files);
    }

    @Override
    public Object parse(JSONObject jsonObject) throws DataParseException {
        return new Result(jsonObject);
    }

    private class Result extends HemaPageArrayResult<OrderListInfor> {

        public Result(JSONObject jsonObject) throws DataParseException {
            super(jsonObject);
        }

        @Override
        public OrderListInfor parse(JSONObject jsonObject)
                throws DataParseException {
            return new OrderListInfor(jsonObject);
        }

    }
}



