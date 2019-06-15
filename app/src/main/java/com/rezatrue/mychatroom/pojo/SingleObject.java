package com.rezatrue.mychatroom.pojo;

public class SingleObject {

    String name;
    String msg;


    public SingleObject() {
    }

    public SingleObject(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
