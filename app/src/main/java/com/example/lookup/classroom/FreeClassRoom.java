package com.example.lookup.classroom;

public class FreeClassRoom {
    public String ClassRoomName;    //103
    public String ClassRoomSize;    //120
    public String SchoolName;   //所属学院
    public String DataSign; //本部，船山
    public String ExamSites;    //考场数.
    public FreeClassRoom(String ClassRoomName,String ClassRoomSize,String SchoolName,String DataSign,String ExamSites){
        this.ClassRoomName =ClassRoomName ;
        this.ClassRoomSize = ClassRoomSize;
        this.SchoolName = SchoolName;
        this.DataSign = DataSign;
        this.ExamSites = ExamSites;
    }
    public String getClassRoomName(){return ClassRoomName;}
    public String getClassRoomSize(){return ClassRoomSize;}
    public String getSchoolName(){return SchoolName;}
    public String getDataSign(){return DataSign;}
    public String getExamSites(){return ExamSites;}
}
