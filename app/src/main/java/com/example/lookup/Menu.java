package com.example.lookup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class Menu extends AppCompatActivity {
    private TextView testText;
    private String Cookie;
    private String result = "";
    private String testResult = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        testText = (TextView)findViewById(R.id.TestText);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Cookie =bundle.getString("Cookie");
        Log.e("Cookie",Cookie);
        //进行账号的获取.
        new Thread(new Runnable() {
            @Override
            public void run() {
                //GetGrade();
                GetStudentTimeetable();
            }
        }).start();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0x01){
                testText.setText(testResult);
            }
        }
    };

    public void GetGrade(){
        String target = "http://jwzx.usc.edu.cn/Student/ScoreReview/GetScoreList";
        URL url ;
        try{
            url = new URL(target);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "batchId=201906301512359790568e5b5022507&sort=CourseName&order=ASC";
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
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("rows");
            int total = Integer.parseInt(jsonObject.getString("total"));
            testResult = "课程 平时 考试 总评\r\n";
            for(int i=0;i<total;i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String CourseName = jsonObject1.getString("CourseName");
                String GeneralScore = jsonObject1.getString("GeneralScore");
                String ExamScore = jsonObject1.getString("ExamScore");
                String FinalScore = jsonObject1.getString("FinalScore");
                testResult  = testResult + CourseName+"   "+  GeneralScore + "   " +ExamScore +"  "+ FinalScore + "\r\n";
            }
            Log.e("testResult",testResult);
            //发送消息
            Message m = new Message();
            m.what = 0x01;
            handler.sendMessage(m);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void GetStudentTimeetable(){
        String target = "http://jwzx.usc.edu.cn/Student/StuTimetable/GetStudentTimetable";
        URL url ;
        result = "";
        try{
            url = new URL(target);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);  //禁止缓存
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setRequestProperty("Cookie",Cookie);
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");   //内容类型
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            //连接要提交的数据
            String param = "termCode=2019-2020-1&sort=TimeName&order=ASC";
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
}
