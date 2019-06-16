package com.rezatrue.mychatroom.pojo;

public class Message {

    private String image;
    private String name;
    private String msg;
    private String time;
    private String status;


    public Message() {
    }

    public Message(String image, String name, String msg, String time, String status) {
        this.image = image;
        this.name = name;
        this.msg = msg;
        this.time = time;
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
