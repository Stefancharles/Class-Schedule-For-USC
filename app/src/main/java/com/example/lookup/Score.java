package com.example.lookup;

public class Score {
    private String ClassName;   //班级
    private String CourseName;  //课程名
    private String ExamScore;
    private String FinalScore;
    private String GeneralScore;
    private int Gpa;    //绩点
    private String RTeacherName;    //老师
    private String TermName;
    public Score(String ClassName,String CourseName,String GeneralScore,String ExamScore,String FinalScore){
        this.ClassName = ClassName;
        this.CourseName = CourseName;
        this.GeneralScore = GeneralScore;
        this.ExamScore = ExamScore;
        this.FinalScore = FinalScore;
    }
    public String getClassName(){
        return ClassName;
    }
    public String getCourseName(){
        return CourseName;
    }
    public String getGeneralScore(){
        return GeneralScore;
    }
    public String getExamScore(){
        return ExamScore;
    }
    public String getFinalScore(){
        return FinalScore;
    }
}
