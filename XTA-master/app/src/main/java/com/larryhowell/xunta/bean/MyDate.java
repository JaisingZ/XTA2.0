package com.larryhowell.xunta.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

@SuppressWarnings("serial")
public class MyDate implements Serializable, Comparable<MyDate> {
    private int year;
    private int month;
    private int day;

    public MyDate() {
    }

    public MyDate(int year, int month, int day) {
        super();
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
            return year + "/" + month + "/" + day;
    }

    @Override
    public int compareTo(@NonNull MyDate another) {
        Calendar tmpDate1 = Calendar.getInstance();
        tmpDate1.set(this.year, this.month, this.day);

        Calendar tmpDate2 = Calendar.getInstance();
        tmpDate2.set(another.year, another.month, another.day);

        if (tmpDate1.before(tmpDate2)) {
            return -1;
        } else if (tmpDate1.after(tmpDate2)) {
            return 1;
        } else {
            return 0;
        }
    }
}