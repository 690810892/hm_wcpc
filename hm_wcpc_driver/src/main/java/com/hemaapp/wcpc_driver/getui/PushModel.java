package com.hemaapp.wcpc_driver.getui;

import java.io.Serializable;

import xtom.frame.XtomObject;

/**
 * 消息推送
 * Created by HuHu on 2016/4/12.
 */
public class PushModel extends XtomObject implements Serializable {

    private static final long serialVersionUID = -2059126470520707606L;
    private String keyType;
    private String keyId;
    private String msg;
    private String msg_nickname;
    private String msg_avatar;

    public PushModel(String keyType, String keyId, String msg, String msg_nickname, String msg_avatar) {
        this.keyType = keyType;
        this.keyId = keyId;
        this.msg = msg;
        this.msg_nickname = msg_nickname;
        this.msg_avatar = msg_avatar;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg_nickname() {
        return msg_nickname;
    }

    public void setMsg_nickname(String msg_nickname) {
        this.msg_nickname = msg_nickname;
    }

    public String getMsg_avatar() {
        return msg_avatar;
    }

    public void setMsg_avatar(String msg_avatar) {
        this.msg_avatar = msg_avatar;
    }
}
