package com.rohit.codv19admin;

public class AdminMessage {

    private String desc;
    private String image;



    private String title;
    public AdminMessage() {

    }

    public AdminMessage(String desc, String image, String title) {
        this.desc = desc;
        this.image = image;
        this.title=title;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
