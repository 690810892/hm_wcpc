package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/17.
 */
public class UnionTrade extends XtomObject implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String respCode;// 响应码 成功时为“00”
    private String respMsg;// 具体错误信息描述 当respCode<>“00”时才会返回此字段
    private String tn;// 银联交易流水号 非常重要，客户端需要用到
    private String reqReserved;// 我方服务器交易单号

    public UnionTrade(JSONObject jsonObject) throws DataParseException {
        if (jsonObject != null) {
            try {
                respCode = get(jsonObject, "respCode");
                respMsg = get(jsonObject, "respMsg");
                tn = get(jsonObject, "tn");
                reqReserved = get(jsonObject, "reqReserved");
                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "UnionTrade [respCode=" + respCode + ", respMsg=" + respMsg
                + ", tn=" + tn + ", reqReserved=" + reqReserved + "]";
    }

    /**
     * @return the respCode
     */
    public String getRespCode() {
        return respCode;
    }

    /**
     * @return the respMsg
     */
    public String getRespMsg() {
        return respMsg;
    }

    /**
     * @return the tn
     */
    public String getTn() {
        return tn;
    }

    /**
     * @return the reqReserved
     */
    public String getReqReserved() {
        return reqReserved;
    }

}

