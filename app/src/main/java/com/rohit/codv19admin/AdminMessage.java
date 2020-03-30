package com.rohit.codv19admin;

public class AdminMessage {

    private String desc;
    private String image;

    public AdminMessage() {

    }

    public AdminMessage(String desc, String image) {
        this.desc = desc;
        this.image = image;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
