package com.larryhowell.xunta.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 自定义时间类
 */
public class MyTime implements Serializable, Comparable<MyTime> {
    private int hour;
    private int minute;

    public MyTime() {
    }

    public MyTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }

    @Override
    public int compareTo(@NonNull MyTime another) {
        if(this.hour * 60 + this.minute > another.hour * 60 + another.minute){
            return 1;
        } else if (this.hour * 60 + this.minute < another.hour * 60 + another.minute){
            return  -1;
        } else {
            return 0;
        }
    }
}