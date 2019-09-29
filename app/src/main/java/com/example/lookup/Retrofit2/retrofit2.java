package com.example.lookup.Retrofit2;


import android.util.Log;

import com.example.lookup.Course;
import com.example.lookup.classroom.FreeClassRoom;
import com.example.lookup.staticdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.*;

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
public class retrofit2 {
    private static OkHttpClient genericClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("Cookie", staticdata.Cookie)
                                .build();
                        return chain.proceed(request);
                    }
                }).build();
    }
    private useApi_Interface Getretrofit(){
        String baseUrl = "http://jwzx.usc.edu.cn/";
        return new Retrofit.Builder()
                .baseUrl(baseUrl)   //设置网络请求的Url地址
                .client(genericClient())
                .addConverterFactory(GsonConverterFactory.create())// 设置数据解析器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 支持RxJava平台
                .build()
                .create(useApi_Interface.class);
    }
    public Call<ResponseBody> LoginToUSC(final String UserName, final String Password){
        //对发送请求进行封装
        //发送异步请求.
        return Getretrofit().LoginToUSC(UserName,Password);
    }
    public Call<ResponseBody> GetFreeClassRoom(Map<String, String> requestDataMap){
        return Getretrofit().GetFreeClassRoom(requestDataMap);
    }
    public Call<ResponseBody> GetStudentTimetable(Map<String,String> requestDataMap){
        return Getretrofit().GetStudentTimetable(requestDataMap);
    }
    public Call<ResponseBody> GetTeachingClassUnits(String ClassCode){
        return Getretrofit().GetTeachingClassUnits(ClassCode);
    }
    public int Getloginresponse(Response<ResponseBody> response)  {
        ResponseBody responseBody = response.body();
        if (responseBody!=null){
            try {
                Log.e("Headers",response.headers().get("Set-Cookie"));
                staticdata.Cookie = response.headers().get("Set-Cookie");
                JSONObject jsonObject = new JSONObject(responseBody.string());

                return Integer.parseInt(jsonObject.getString("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public ArrayList<FreeClassRoom> Getfreeclassroom(Response<ResponseBody> response){
        ResponseBody responseBody = response.body();    //为空.
        ArrayList<FreeClassRoom> freeClassRoomArrayList = new ArrayList<>();
        if (responseBody!=null){
            try {
                String msg = responseBody.string();
                Log.e("body",msg);    //如果是只能读一次的话，诶
                JSONObject jsonObject = new JSONObject(msg);
                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                int total = Integer.parseInt(jsonObject.getString("total"));
                Log.e("total",String.valueOf(total));
                for(int i=0;i<total;i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    freeClassRoomArrayList.add(new FreeClassRoom(jsonObject1.getString("ClassRoomName"),
                            jsonObject1.getString("ClassRoomSize"),
                            jsonObject1.getString("SchoolName"),jsonObject1.getString("DataSign"),
                            jsonObject1.getString("ExamSites")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("retrofit2容量",String.valueOf(freeClassRoomArrayList.size()));
        return freeClassRoomArrayList;
    }
    public ArrayList<Course> Getstudenttimetable(Response<ResponseBody> response){
        //把课表String转换成Course
        ResponseBody responseBody = response.body();    //为空.
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        if (responseBody!=null){
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(responseBody.string());
                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                int testLength =0;
                for(int i=0;i<5;i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String []day = new String[8];
                    String TimeName = jsonObject1.getString("TimeName");    //1-2节,需要分割
                    day[1] = jsonObject1.getString("Monday");        //1
                    day[2] = jsonObject1.getString("Tuesday");      //2
                    day[3] = jsonObject1.getString("Wednesday");  //3
                    day[4] = jsonObject1.getString("Thursday");    //4
                    day[5] = jsonObject1.getString("Friday");         //5
                    day[6] = jsonObject1.getString("Saturday");    //6
                    day[7] = jsonObject1.getString("Sunday");        //7
                    //解析string.
                    for(int k=1;k<8;k++)
                        if (!day[k].equals("")) {
                            //先这样添加把.
                            Document doc = Jsoup.parse(day[k]); //解析html标签.
                            Element content = doc.getElementsByTag("ul").first();
                            Elements links = content.getElementsByTag("li");
                            String [] temp = new String[6];
                            int length=0;
                            for(Element link : links){
                                temp[length++] = link.text();
                            }
                            //课程名,编码,老师，周次，教室.
                            //String courseName, String cno,String teacher, String classRoom, int day, int classStart, int classEnd
                            temp[1] = temp[1].split("\\(")[0];
                            //reg,\\.=.   \\(=(
                            coursesList.add(new Course(temp[0], temp[1],temp[2], temp[3],temp[4], k, 2 * i + 1, 2 * i + 2));
                            ++testLength;
                        }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("Getstudenttimetable","null");
        }
        return coursesList;
    }
    public String Getteachingclassunits(Response<ResponseBody> response){
        ResponseBody responseBody = response.body();
        StringBuilder result = new StringBuilder();
        if (responseBody!=null){
            //使用Jsoup解析.
            try {
                Document doc = Jsoup.parse(responseBody.string());
                Elements links = doc.select("td");
                int i=0;
                for (Element link:links){
                    i++;
                    result.append(link.text());
                    if (i%2==0){
                        result.append("\r\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Document doc = Jsoup.parse()
        }
        return result.toString();
    }
}

