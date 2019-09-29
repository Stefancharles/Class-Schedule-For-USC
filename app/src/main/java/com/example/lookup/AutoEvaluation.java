package com.example.lookup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONException;

import java.io.IOException;

public class AutoEvaluation extends AppCompatActivity {
    private control mycontrol;
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_evaluation);
        init();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
        Message msg = new Message();
        msg.what = 0x02;
        handler.sendMessage(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //调用
                try {
                    mycontrol.UpdateEvaluationResult();
                    //mycontrol.GetHtmlUsername();
                }  catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if (mycontrol.GetEvaluationOkFlag()){
                        //开始弹出对话框.
                        Message msg = new Message();
                        msg.what = 0x01;    //成功
                        handler.sendMessage(msg);
                        break;
                    }
                }
            }
        }).start();

    }
    public void init(){
        mycontrol = new control(staticdata.Cookie);    //绑定对象
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
                    intent = new Intent(getApplicationContext(),GradeManage.class);
                    startActivity(intent);
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
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                TastyToast.makeText(AutoEvaluation.this,"评教已完成,请前往官网查看",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
            }else if (msg.what == 0x02){
                Mainactivity_Username.setText(staticdata.Username);
                TastyToast.makeText(AutoEvaluation.this,"正在评教,请等待",TastyToast.LENGTH_SHORT,TastyToast.INFO);
            }
        }
    };
}
