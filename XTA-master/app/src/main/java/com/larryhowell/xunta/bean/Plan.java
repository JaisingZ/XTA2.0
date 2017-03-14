package com.larryhowell.xunta.bean;

import com.baidu.mapapi.search.core.PoiInfo;

public class Plan {
    private String targetTelephone;
    private String desc;
    private int grade;
    private String startTime;
    private String arrival;
    private PoiInfo departure;
    private PoiInfo terminal;

    public Plan() {
    }

    public Plan(String targetTelephone, String desc, int grade, String startTime,
                String arrival, PoiInfo departure, PoiInfo terminal) {
        this.targetTelephone = targetTelephone;
        this.desc = desc;
        this.grade = grade;
        this.startTime = startTime;
        this.arrival = arrival;
        this.departure = departure;
        this.terminal = terminal;
    }

    public String getTargetTelephone() {
        return targetTelephone;
    }

    public void setTargetTelephone(String targetTelephone) {
        this.targetTelephone = targetTelephone;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public PoiInfo getDeparture() {
        return departure;
    }

    public void setDeparture(PoiInfo departure) {
        this.departure = departure;
    }

    public PoiInfo getTerminal() {
        return terminal;
    }

    public void setTerminal(PoiInfo terminal) {
        this.terminal = terminal;
    }
}
