package com.example.lookup;

import java.io.Serializable;

public class Course implements Serializable {
    private String courseName;
    private String teacher;
    private String classRoom;
    private String cno;
    private String weekTime;
    private int day;
    private int classStart;
    private int classEnd;
    public Course(){

    }
    public Course(String courseName, String cno,String teacher, String weekTime,String classRoom, int day, int classStart, int classEnd) {
        this.courseName = courseName;
        this.teacher = teacher;
        this.cno = cno;
        this.classRoom = classRoom;
        this.weekTime = weekTime;
        this.day = day;
        this.classStart = classStart;
        this.classEnd = classEnd;
    }
    public String getWeekTime(){
        return weekTime;
    }
    public String getCourseName() {
        return courseName;
    }
    public String getCno(){return cno;}
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStart() {
        return classStart;
    }

    public void setStart(int classStart) {
        this.classEnd = classStart;
    }

    public int getEnd() {
        return classEnd;
    }

    public void setEnd(int classEnd) {
        this.classEnd = classEnd;
    }
}

