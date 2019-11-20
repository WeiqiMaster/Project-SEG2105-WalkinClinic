package com.example.seg2105_project;

public class MyTime {
    int month;
    int day;
    int hour;
    int minute;
    String timeInterval;

//    public MyTime(int month, int day,
//                  int hour,
//                  int minute){
//        this.month = month;
//        this.day = day;
//        this.hour = hour;
//        this.minute = minute;
//    }

    public MyTime(int month, int day,
                  String timeInterval){
        this.month = month;
        this.day = day;
        this.timeInterval = timeInterval;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
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
}