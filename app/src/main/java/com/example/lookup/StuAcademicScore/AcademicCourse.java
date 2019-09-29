package com.example.lookup.StuAcademicScore;

public class AcademicCourse {
    private String CourseName;
    private String CoursePlatformName;  //学科基础选修课
    private String Credit;  //学分
    private String FinalScore;
    private String TotalPeriod; //总共学时
    public AcademicCourse(String CourseName,String CoursePlatformName,String Credit,String FinalScore,String TotalPeriod){
        this.CourseName =CourseName ;
        this.CoursePlatformName =CoursePlatformName ;
        this.Credit =Credit ;
        this.FinalScore =FinalScore ;
        this.TotalPeriod =TotalPeriod ;
    }
    public String getCourseName(){
        return CourseName;
    }
    public String getCoursePlatformName(){
        return CoursePlatformName;
    }
    public String getCredit(){
        return Credit;
    }
    public String getFinalScore(){
        return FinalScore;
    }
    public String getTotalPeriod(){
        return TotalPeriod;
    }
}
