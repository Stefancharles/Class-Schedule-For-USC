package com.example.lookup.Retrofit2;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface useApi_Interface {
    /*
    *   改进,post
    * */
    @POST("Login/ReLogin")    //不是@POST("\ReLogin")
    @FormUrlEncoded
    @Headers("Content-Type:application/x-www-form-urlencoded")
    Call<ResponseBody> LoginToUSC(@Field("UserName")String UserName,@Field("Password")String Password);

    @POST("Educational/FreeClassRoom/GetFreeClassRoom")     //教室
    @FormUrlEncoded
    Call<ResponseBody> GetFreeClassRoom(@FieldMap Map<String, String> requestBodyMap);

    @POST("Student/StuTimetable/GetStudentTimetable")   //课表
    @FormUrlEncoded
    Call<ResponseBody> GetStudentTimetable(@FieldMap Map<String,String> requestBodyMap);

    @GET("EducationalCombobox/GetTeachingClassUnits")
    Call<ResponseBody> GetTeachingClassUnits(@Query("teachingClassCode") String teachingClassCode);
}

