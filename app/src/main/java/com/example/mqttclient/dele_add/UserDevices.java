package com.example.mqttclient.dele_add;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class UserDevices extends DataSupport implements Serializable {

    private int id;
    private String deviceName;
    private int type;
    private int imageId;
    private String theme;

    public UserDevices(){}

    public UserDevices(int id, String deviceName, int type,int imageId,String theme) {
        this.id = id;
        this.deviceName = deviceName;
        this.type = type;
        this.imageId = imageId;
        this.theme = theme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
