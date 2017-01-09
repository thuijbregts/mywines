package com.example.thomas.mywines.informationclasses;

import java.util.Date;

public class Event {

    public static final int EXPO = 0;
    public static final int TASTING = 1;

    private long id;
    private String name;
    private String country;
    private String city;
    private String address;
    private String site;
    private Date dateStart;
    private int duration;
    private int type;

    public Event(String name, String country,  String city, String address, String site, Date dateStart, int duration, int type){
        this.name = name;
        this.country = country;
        this.city = city;
        this.address = address;
        this.site = site;
        this.dateStart = dateStart;
        this.duration = duration;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
