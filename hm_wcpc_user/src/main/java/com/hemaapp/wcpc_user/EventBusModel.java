package com.hemaapp.wcpc_user;


import java.io.Serializable;

import xtom.frame.XtomObject;

/**
 * EventBus传递数据使用
 * Created by Hufanglin on 2017/3/10.
 */

public class EventBusModel extends XtomObject implements Serializable {
    private static final long serialVersionUID = 2457040098627212163L;
    private EventBusConfig type;
    private String content = "";
    private Object object;
    private int code=0;

    public EventBusModel(EventBusConfig type) {
        this.type = type;
    }

    public EventBusModel(EventBusConfig type, String content) {
        this(type);
        this.content = content;
    }

    public EventBusModel(EventBusConfig type, Object object) {
        this(type);
        this.object = object;
    }
    public EventBusModel(EventBusConfig type, Object object,String content) {
        this(type);
        this.object = object;
        this.content = content;
    }
    public EventBusModel(EventBusConfig type, int code) {
        this(type);
        this.code = code;
    }

    public EventBusConfig getType() {
        return type;
    }

    public void setType(EventBusConfig type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}