package com.example.lookup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;

import org.json.JSONException;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;

public class GradeManage extends AppCompatActivity {
    private control mycontrol;
    private ArrayList<Score> scores;
    private ListView listView;
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_manage);
        mycontrol = new control(staticdata.Cookie);
        listView = (ListView)findViewById(R.id.grade_manage_listView);
        init();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    scores = mycontrol.JsonToScore();
                    Message msg = new Message();
                    msg.what = 0x01;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void SetGradetolist(){
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>(20);
        for(int i=0;i<scores.size();i++){
            HashMap<String,Object> item = new HashMap<String, Object>();
            item.put("ClassName","班级:"+scores.get(i).getClassName());
            item.put("CourseName","课程:"+ Jsoup.parse(scores.get(i).getCourseName()).text());
            item.put("GeneralScore","平时:"+scores.get(i).getGeneralScore());
            item.put("ExamScore","考试:"+scores.get(i).getExamScore());
            item.put("FinalScore","总评:"+scores.get(i).getFinalScore());
            data.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(GradeManage.this,data,R.layout.item,
                new String[]{"ClassName","CourseName","GeneralScore","ExamScore","FinalScore"},new int[]{
                        R.id.class_name,R.id.course_name,R.id.general_score,R.id.exam_score,R.id.final_score
        });
        listView.setAdapter(simpleAdapter);
        //获取到内容
   }
   @SuppressLint("HandlerLeak")
   private Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           super.handleMessage(msg);
           Mainactivity_Username.setText(staticdata.Username);
           if (msg.what == 0x01){
               //调用SetGradetolist
               SetGradetolist();
           }
       }
   };
    private void init(){
        // Grab the widget
        mSlideSideMenu = (SlideSideMenuTransitionLayout)findViewById(R.id.slide_side_menu);

        // Setup the toolbar
        mToolbar = (Toolbar) findViewById(R.id.base_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

        // Wire SideMenu with Toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        Mainactivity_Username = (TextView)findViewById(R.id.mainactivity_Username); //你的姓名
        Index = (TextView)findViewById(R.id.Index); //主页
        Querytable = (TextView)findViewById(R.id.querytable);   //课表
        Querygrade = (TextView)findViewById(R.id.querygrade);   //成绩
        QuerygradeAnyear = (TextView)findViewById(R.id.querygradeAnyear); //学年成绩
        Autoevaluation = (TextView)findViewById(R.id.autoevaluation);   //一键评教
        Getclassroom = (TextView) findViewById(R.id.getclassroom);  //空闲教室
        Aboutauthor = (TextView)findViewById(R.id.aboutAuthor); //关于作者

        Index.setOnClickListener(listener);
        Querytable.setOnClickListener(listener);
        Querygrade.setOnClickListener(listener);
        QuerygradeAnyear.setOnClickListener(listener);
        Autoevaluation.setOnClickListener(listener);
        Getclassroom.setOnClickListener(listener);
        Aboutauthor.setOnClickListener(listener);
    }
    private View.OnClickListener listener= new View.OnClickListener(){
        Intent intent;
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.Index:
                    intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.querytable:
                    intent = new Intent(getApplicationContext(),StudentTimeTable.class);
                    startActivity(intent);
                    break;
                case R.id.querygrade:
                    break;
                case R.id.querygradeAnyear:
                    intent = new Intent(getApplicationContext(),com.example.lookup.StuAcademicScore.GetList.class);
                    startActivity(intent);
                    break;
                case R.id.autoevaluation:
                    intent = new Intent(getApplicationContext(),AutoEvaluation.class);
                    startActivity(intent);
                    break;
                case R.id.getclassroom:
                    intent = new Intent(getApplicationContext(),com.example.lookup.classroom.roommanage.class);
                    startActivity(intent);
                    break;
                case R.id.aboutAuthor:
                    intent = new Intent(getApplicationContext(),aboutAuthor.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
