package com.example.thomas.mywines.informationclasses;


import java.util.Date;

public class Wine {

    public static final String NO_IMAGE = "NO_IMAGE";
    public static final int RED = 1;
    public static final int ROSE = 2;
    public static final int WHITE = 3;

    private long id;
    private int type;
    private String name;
    private String appellation;
    private int year;
    private String note;
    private Date date;
    private String thumbnail;
    private String image;
    private String comment;
    private String seller;
    private float price;

    public Wine(int type, String name, String appellation, int year, String note, Date date, String comment, String seller, float price){
        this.type = type;
        this.name = name;
        this.appellation = appellation;
        this.year = year;
        this.note = note;
        this.date = date;
        this.comment = comment;
        this.seller = seller;
        this.price = price;

        this.image = NO_IMAGE;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getAppellation() {
        return appellation;
    }

    public void setAppellation(String appellation) {
        this.appellation = appellation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
