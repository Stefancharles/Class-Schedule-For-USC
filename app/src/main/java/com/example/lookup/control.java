package com.example.lookup;

import android.content.Intent;
import android.icu.util.RangeValueIterator;
import android.provider.DocumentsContract;
import android.text.Html;
import android.util.Log;

import com.example.lookup.StuAcademicScore.AcademicCourse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class control {
    private String target = "",result="";
    private URL url;
    private HttpURLConnection httpURLConnection;
    private String Cookie,Username;
    private boolean evaluationOk = false;
    public control(String Cookie){
        this.Cookie = Cookie;
        Log.e("Control-Cookie",Cookie);
    }
    private void GetStuEvaluation(){
        //GetCourseList是获取课程列表,2019春季学期本部课务评教(评教课程)
        //GetResultList是获取评价指标,10条
        //UpdataEvaluationResult是评教后的操作
        target = "http://jwzx.usc.edu.cn/Student/StuEvaluation/GetCourseList";
        try{
            url = new URL(target);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "batchId=20190530095305274886878ad9552af&sort=CourseName&order=ASC";  //暂时先2019-2020-1
            out.writeBytes(param);
            out.flush();
            out.close();
            if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //响应成功
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                //获取输入流
                while((inputLine=buffer.readLine())!=null){
                    result = result + inputLine ;
                }
                in.close();
            }
            httpURLConnection.disconnect(); //断开连接
            Log.e("table",result);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<ArrayList<String>> GetResultList() throws JSONException {
        GetStuEvaluation();
        ArrayList<String> resultId = new ArrayList<>();
        ArrayList<ArrayList<String>> evaluationValuatorId = new ArrayList<>();
        resultId = EvaJsonToString();  //获取到了id.
        //获取到一个全部的id.
        target = "http://jwzx.usc.edu.cn/Student/StuEvaluation/GetResultList";
        try{
            //内容类型
            //连接要提交的数据
            for (int i=0;i<resultId.size();i++){
                result = "";
                url = new URL(target);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(false);  //禁止缓存
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
                httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
                String param = "evaluationValuatorId="+ resultId.get(i) + "&sort=SerialNo&order=ASC";
                out.writeBytes(param);
                out.flush();
                out.close();
                if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    //响应成功
                    InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inputLine = null;
                    //获取输入流
                    while((inputLine=buffer.readLine())!=null){
                        result = result + inputLine ;   //调用方法，解析result.
                    }
                    in.close();
                    //解析数据
                    evaluationValuatorId.add(EvaJsonToString());   //total * 10个id
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return evaluationValuatorId;
    }
    public void UpdateEvaluationResult() throws JSONException, IOException {
        //打印一下evaluationValuatorId.二维数组
        ArrayList<ArrayList<String>> resultId = new ArrayList<>(); //课程列表
        evaluationOk = false;
        String[] rankCode = {"4","3","3","4","4","4","4","4","4","4","4"};
        resultId = GetResultList();
        Log.e("length",String.valueOf(resultId.size()));
        for (int i=0;i<resultId.size();i++){
            for(int j=0;j<resultId.get(i).size();j++){
                //进行post
                target = "http://jwzx.usc.edu.cn/Student/StuEvaluation/UpdateEvaluationResult";
                url = new URL(target);
                httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(false);  //禁止缓存
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
                httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
                Random random = new Random();
                int nextInt = random.nextInt(10);   //1-10之间
                String param = "resultId="+ resultId.get(i).get(j) + "&rankCode=" + rankCode[nextInt];
                out.writeBytes(param);
                out.flush();
                out.close();
                if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    //响应成功
                    evaluationOk = true;
                }
            }
        }
        Log.e("成功","成功");
    }
    private ArrayList<String> EvaJsonToString() throws JSONException {
        ArrayList<String> resultId = new ArrayList<>(); //课程列表
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("rows");
        int total = Integer.parseInt(jsonObject.getString("total"));
        //每个老师都有自己的id,每个Id下面的评教id不同
        //先找规律.
        for(int i=0;i<total;i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            resultId.add(jsonObject1.getString("Id"));  //获取到了id,然后通过id继续获取评教的id.
            //Log.e("resultId",jsonObject1.getString("Id") + "\n");
        }
        return resultId;
    }
    public void GetStudentTimetable(){
        //获取课表的信息.
        result = "";
        target = "http://jwzx.usc.edu.cn/Student/StuTimetable/GetStudentTimetable";
        try{
            url = new URL(target);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "termCode=2019-2020-1&sort=TimeName&order=ASC";  //暂时先2019-2020-1
            out.writeBytes(param);
            out.flush();
            out.close();
            if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //响应成功
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                //获取输入流
                while((inputLine=buffer.readLine())!=null){
                    result = result + inputLine ;
                }
                in.close();
            }
            httpURLConnection.disconnect(); //断开连接
            Log.e("table",result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Course> JsonToCourse() throws JSONException {
        //把课表String转换成Course
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("rows");
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        int testLength =0;
        for(int i=0;i<5;i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String []day = new String[8];
            /*
            *   i=0 1-2
            *   i=1 3-4
            *   i=2 5-6
            * */
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
                    //String courseName, String teacher, String classRoom, int day, int classStart, int classEnd
                    coursesList.add(new Course(temp[0],temp[1], temp[2], temp[3],temp[4], k, 2 * i + 1, 2 * i + 2));
                    Log.e("day", day[k]);
                    ++testLength;
                }
        }
        Log.e("testLength",String.valueOf(testLength));
        return coursesList;
    }
    //获取成绩
    private void GetScoreList(){
        result = "";
        target = "http://jwzx.usc.edu.cn/Student/ScoreReview/GetScoreList";
        try{
            url = new URL(target);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "batchId=201906301512359790568e5b5022507&sort=CourseName&order=ASC";  //暂时先2019-2020-1
            out.writeBytes(param);
            out.flush();
            out.close();
            if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //响应成功
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                //获取输入流
                while((inputLine=buffer.readLine())!=null){
                    result = result + inputLine ;
                }
                in.close();
            }
            httpURLConnection.disconnect(); //断开连接
            Log.e("table",result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Score> JsonToScore() throws JSONException {
        GetScoreList();
        ArrayList<Score> ScoreReview = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("rows");
        int total = Integer.parseInt(jsonObject.getString("total"));
        for(int i=0;i<total;i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String ClassName = jsonObject1.getString("ClassName");
            String CourseName = jsonObject1.getString("CourseName");
            String GeneralScore = jsonObject1.getString("GeneralScore");
            String ExamScore = jsonObject1.getString("ExamScore");
            String FinalScore = jsonObject1.getString("FinalScore");
            ScoreReview.add(new Score(ClassName,CourseName,GeneralScore,ExamScore,FinalScore));
        }
       return ScoreReview;
    }
    public void GetHtmlUsername() throws IOException {
        //获取你的姓名.
        //Document doc = Jsoup.connect("http://jwzx.usc.edu.cn/Home/Index").cookie(".AspNet.ApplicationCookie",Cookie).post();
        Connection con = Jsoup.connect("http://jwzx.usc.edu.cn/Home/Index");
        con.header("Content-Type","application/x-www-form-urlencoded");
        con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))");
        con.header("Cookie", Cookie);   //必须这个，不然不能请求到
        Document doc = con.get();
        //带cookie的get请求
        Username= doc.select("strong").first().text();
        //Username = doc.outerHtml();
        //Username = link.html();
        Username = Username.split("【")[0];
        Log.e("Username",Username);
        //Username = ".";
    }
    public String Getusername(){
        return Username;
    }
    public boolean GetEvaluationOkFlag(){
        return evaluationOk;
    }
    private void StuAcademicScore(){
        target = "http://jwzx.usc.edu.cn/Student/StuAcademicScore/GetList";
        try{
            url = new URL(target);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);  //从登录那里获取到.
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "grade=2017&sort=Id&order=ASC";  //暂时先2019-2020-1
            out.writeBytes(param);
            out.flush();
            out.close();
            if (httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                //响应成功
                InputStreamReader in = new InputStreamReader(httpURLConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                //获取输入流
                while((inputLine=buffer.readLine())!=null){
                    result = result + inputLine ;
                }
                in.close();
            }
            httpURLConnection.disconnect(); //断开连接
            Log.e("table",result);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<AcademicCourse> JsonToAcademicCourse() throws JSONException {
        StuAcademicScore();
        ArrayList<AcademicCourse> ScoreReview = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(result);
        JSONArray jsonArray = jsonObject.getJSONArray("rows");
        int total = Integer.parseInt(jsonObject.getString("total"));
        for(int i=0;i<total;i++){
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String CoursePlatformName = jsonObject1.getString("CoursePlatformName");
            String CourseName = jsonObject1.getString("CourseName");
            String Credit = jsonObject1.getString("Credit");
            String FinalScore = jsonObject1.getString("FinalScore");
            String TotalPeriod = jsonObject1.getString("TotalPeriod");
            ScoreReview.add(new AcademicCourse(CourseName,CoursePlatformName,Credit,FinalScore,TotalPeriod));
        }
        return ScoreReview;
    }
    private void GetFreeClassRoom(){
        /*
        *       termCode: 2019-2020-1
        *       campusCode: 1   本部
        *       buildingCode: 01    一教
        *       roomKind: 01    多媒体
        *       timeCodes[]
        *       weekName: 9
        *       capacity: 20
        *       page: 1
        *       rows: 50
        *       sort: ClassRoomNo
        *       order: ASC
        * */
    }
}
