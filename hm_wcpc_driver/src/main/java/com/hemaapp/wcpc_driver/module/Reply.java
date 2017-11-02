package com.hemaapp.wcpc_driver.module;

import org.json.JSONException;
import org.json.JSONObject;

import xtom.frame.XtomObject;
import xtom.frame.exception.DataParseException;

/**
 */
public class Reply extends XtomObject {
    private String  id	; //主键id
    private String  realname; //	评价人姓名
    private String avatar; //	评价人头像
    private String content; //	内容
    private String point; //	评分
    private String replystr1; //	所选评价选项id串	英文逗号分隔，如：1,2,3
    private String reply_str_text1; //	所选评价选项文字内容串	英文逗号分隔，如：不干净,开车慢,不安全
    private String replycon1; //	内容
    private String  regdate	; //添加时间
    private String   startaddress; //
    private String   endaddress; //

    public Reply(JSONObject jsonObject) throws DataParseException {
        if(jsonObject!=null){
            try {
                id = get(jsonObject, "id");
                realname = get(jsonObject, "realname");
                content = get(jsonObject, "content");
                point = get(jsonObject, "point");
                avatar = get(jsonObject, "avatar");
                replystr1 = get(jsonObject, "replystr1");
                reply_str_text1 = get(jsonObject, "reply_str_text1");
                replycon1 = get(jsonObject, "replycon1");
                regdate = get(jsonObject, "regdate");
                startaddress = get(jsonObject, "startaddress");
                endaddress = get(jsonObject, "endaddress");

                log_i(toString());
            } catch (JSONException e) {
                throw new DataParseException(e);
            }
        }
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id='" + id + '\'' +
                ", realname='" + realname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                ", point='" + point + '\'' +
                ", replystr1='" + replystr1 + '\'' +
                ", reply_str_text1='" + reply_str_text1 + '\'' +
                ", replycon1='" + replycon1 + '\'' +
                ", regdate='" + regdate + '\'' +
                ", startaddress='" + startaddress + '\'' +
                ", endaddress='" + endaddress + '\'' +
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

    public String getRealname() {
        return realname;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getStartaddress() {
        return startaddress;
    }

    public String getReplystr1() {
        return replystr1;
    }

    public String getReply_str_text1() {
        return reply_str_text1;
    }

    public String getReplycon1() {
        return replycon1;
    }

    public String getEndaddress() {
        return endaddress;
    }

    public String getRegdate() {
        return regdate;
    }
}
