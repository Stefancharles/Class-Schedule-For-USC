package com.example.lookup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.sdsmdg.tastytoast.TastyToast;

import us.feras.mdv.MarkdownView;


public class MainActivity extends AppCompatActivity {
    private control mycontrol;
    private TextView UpdataText;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
        Message msg = new Message();
        msg = new Message();
        msg.what = 0x01;    //更新文本
        handler.sendMessage(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mycontrol.GetHtmlUsername();
                    Message message = new Message();
                    message.what = 0x02;
                    handler.sendMessageDelayed(message,500);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public void init(){
        mycontrol = new control(staticdata.Cookie);
        Mainactivity_Username = findViewById(R.id.mainactivity_Username); //你的姓名
        //UpdataText = (TextView)findViewById(R.id.updataText) ;  //更新的文本
        Index = findViewById(R.id.Index); //主页
        Querytable = findViewById(R.id.querytable);   //课表
        Querygrade = findViewById(R.id.querygrade);   //成绩
        QuerygradeAnyear = findViewById(R.id.querygradeAnyear); //学年成绩
        Autoevaluation = findViewById(R.id.autoevaluation);   //一键评教
        Getclassroom = findViewById(R.id.getclassroom);  //空闲教室
        Aboutauthor = findViewById(R.id.aboutAuthor); //关于作者

        // Grab the widget
        mSlideSideMenu = findViewById(R.id.slide_side_menu);
        // Setup the toolbar
        mToolbar = findViewById(R.id.base_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        // Wire SideMenu with Toolbar
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        Index.setOnClickListener(listener);
        Querytable.setOnClickListener(listener);
        Querygrade.setOnClickListener(listener);
        QuerygradeAnyear.setOnClickListener(listener);
        Autoevaluation.setOnClickListener(listener);
        Getclassroom.setOnClickListener(listener);
        Aboutauthor.setOnClickListener(listener);
    }
    @Override
    public void onBackPressed() {
        if (mSlideSideMenu != null && mSlideSideMenu.closeSideMenu()) {
            // Closed the side menu, override the default back pressed behavior
            return;
        }
        super.onBackPressed();
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){

                String Text = "";
                Text +="# <font color=\"blue\">教务在线正式版v1.3\n</font>" +
                        "更新如下\n<br>" +
                        "   1.导航菜单现在全面支持\n<br>" +
                        "   2.课表允许查看兄弟班级\n<br>" +
                        "   3.部分代码采用新框架，效率提高\n<br>" +
                        "   4.修复一键评教闪退BUG\n<br><hr>\n";
                Text +="# 教务在线正式版v1.2\n" +
                        "更新如下\n<br>" +
                        "   1.新增登录界面背景\n<br>" +
                        "   2.新增课表点击查看更多课程信息功能\n<br>" +
                        "   3.优化登录代码，登录更快\n<br>" +
                        "   4.修复闪退问题\n<br><hr>\n";
                Text +="# 教务在线正式版v1.1\n" +
                        "更新如下\n<br>" +
                        "   1.新增记住密码按钮\n<br>" +
                        "   2.更新部分UI，如右滑菜单也支持在关于作者，空闲教室，一键评教显示\n<br>" +
                        "   3.修复若干BUG\n<br><hr>\n";
                Text += "# 教务在线正式版v1.0\n更新如下\n" +
                        "   1.查询当前学期课表\n<br>    " +
                        "   2.查询当前成绩\n<br>  " +
                        "   3.支持一键评教\n<br><hr>\n";
                MarkdownView markdownView = findViewById(R.id.markdownView);
                markdownView.loadMarkdown(Text);
                //UpdataText.setText(Text);
            }else if (msg.what == 0x02){
                staticdata.Username = mycontrol.Getusername();
                Mainactivity_Username.setText(staticdata.Username);
            }
        }
    };
    private View.OnClickListener listener= new View.OnClickListener(){
        Intent intent;
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.Index:
                    break;
                case R.id.querytable:
                    intent = new Intent(MainActivity.this,StudentTimeTable.class);
                    startActivity(intent);
                    break;
                case R.id.querygrade:
                    intent = new Intent(MainActivity.this,GradeManage.class);
                    startActivity(intent);
                    break;
                case R.id.querygradeAnyear:
                    intent = new Intent(MainActivity.this,com.example.lookup.StuAcademicScore.GetList.class);
                    startActivity(intent);
                    break;
                case R.id.autoevaluation:
                    intent = new Intent(MainActivity.this,AutoEvaluation.class);
                    startActivity(intent);
                    break;
                case R.id.getclassroom:
                    intent = new Intent(MainActivity.this,com.example.lookup.classroom.roommanage.class);
                    startActivity(intent);
                    break;
                case R.id.aboutAuthor:
                    intent = new Intent(MainActivity.this,aboutAuthor.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
