package com.hemaapp.wcpc_user.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 * Created by WangYuxia on 2016/5/23.
 */
public class ReplyItems extends XtomObject {
    private String id; //主键ID
    private String reply_str;//评论选项主键id
    private String content; //评论内容
    private String point; //评分

    public ReplyItems(JSONObject jsonObject) throws DataParseException {
        if(jsonObject!=null){
            try {
                id = get(jsonObject, "id");
                reply_str = get(jsonObject, "reply_str");
                content = get(jsonObject, "content");
                point = get(jsonObject, "point");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "ReplyItems{" +
                "content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", reply_str='" + reply_str + '\'' +
                ", point='" + point + '\'' +
                '}';
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getPoint() {
        return point;
    }

    public String getReply_str() {
        return reply_str;
    }
}
