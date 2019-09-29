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
import android.widget.TextView;

import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;

public class aboutAuthor extends AppCompatActivity {
    private TextView AboutauthorText;
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_author);
        init();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
        Message msg = new Message();
        msg.what = 0x01;
        handler.sendMessage(msg);
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                String Text = "作者:Lin\n博客:https://blog.csdn.net/qq_40318498\n未来会更新更多的内容";
                AboutauthorText.setText(Text);
                Mainactivity_Username.setText(staticdata.Username);
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

        AboutauthorText = (TextView)findViewById(R.id.aboutAuthorText);     //文本
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
                    intent = new Intent(aboutAuthor.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.querytable:
                    intent = new Intent(aboutAuthor.this,StudentTimeTable.class);
                    startActivity(intent);
                    break;
                case R.id.querygrade:
                    intent = new Intent(aboutAuthor.this,GradeManage.class);
                    startActivity(intent);
                    break;
                case R.id.querygradeAnyear:
                    intent = new Intent(aboutAuthor.this,com.example.lookup.StuAcademicScore.GetList.class);
                    startActivity(intent);
                    break;
                case R.id.autoevaluation:
                    intent = new Intent(aboutAuthor.this,AutoEvaluation.class);
                    startActivity(intent);
                    break;
                case R.id.getclassroom:
                    intent = new Intent(aboutAuthor.this,com.example.lookup.classroom.roommanage.class);
                    startActivity(intent);
                    break;
                case R.id.aboutAuthor:
                    intent = new Intent(aboutAuthor.this,aboutAuthor.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    public void solveMessage(int type){

    }
}
