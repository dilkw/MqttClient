package com.example.mqttclient.dele_add;

import org.litepal.crud.DataSupport;

public class Device extends DataSupport {

    private int id;
    private String deviceName;
    private int type;
    private String theme;


    public Device(){}

    public Device(int id, String deviceName, int type,String theme) {
        this.id = id;
        this.deviceName = deviceName;
        this.type = type;
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

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
