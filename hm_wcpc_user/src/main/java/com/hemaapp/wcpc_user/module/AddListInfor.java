package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/6.
 */
public class AddListInfor extends XtomObject {
    private String id; //主键ID
    private String keytype; //广告类型
    private String content; //内容
    private String imgurl; //图片
    private String imgurlbig; //大图

    public AddListInfor(JSONObject jsonObject) throws DataParseException {
        if(jsonObject != null){
            try {
                id = get(jsonObject, "id");
                keytype = get(jsonObject, "keytype");
                content = get(jsonObject, "content");
                imgurl = get(jsonObject, "imgurl");
                imgurlbig =get(jsonObject, "imgurlbig");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "AddListInfor{" +
                "id='" + id + '\'' +
                ", keytype='" + keytype + '\'' +
                ", content='" + content + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", imgurlbig='" + imgurlbig + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getKeytype() {
        return keytype;
    }

    public String getContent() {
        return content;
    }

    public String getImgurl() {
        return imgurl;
    }

    public String getImgurlbig() {
        return imgurlbig;
    }
}
