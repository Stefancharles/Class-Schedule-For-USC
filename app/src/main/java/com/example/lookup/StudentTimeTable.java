package com.example.lookup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lookup.Retrofit2.retrofit2;
import com.example.lookup.TimeTable.staticTimeTable;
import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;

import static android.view.View.GONE;
import retrofit2.Callback;
import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.Response;
public class StudentTimeTable extends AppCompatActivity {
    //星期几
    private RelativeLayout day;

    private ArrayList<Course> mycoursesList;
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;
    //SQLite Helper类
    private DatabaseHelper databaseHelper = new DatabaseHelper
            (this, "database.db", null, 1);

    private int currentCoursesNumber = 0;
    private int maxCoursesNumber = 0;
    private retrofit2 retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_time_table);
        init();
        retrofit = new retrofit2();
        setSupportActionBar(mToolbar);
        loadData();

        loadDataFromURL();

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                Mainactivity_Username.setText(staticdata.Username); //设置姓名
                for (Course course : mycoursesList) {
                    createLeftView(course);         //一开始的时候不会创建左侧试图
                    createItemCourseView(course);   //课程列表
                }
            }
        }
    };
    //从数据库加载数据
    private void loadData() {
        ArrayList<Course> coursesList = new ArrayList<>(); //课程列表
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from courses", null);
        if (cursor.moveToFirst()) {
            do {
                coursesList.add(new Course(
                        cursor.getString(cursor.getColumnIndex("course_name")),
                        cursor.getString(cursor.getColumnIndex("teacher")),
                        cursor.getString(cursor.getColumnIndex("cno")),
                        cursor.getString(cursor.getColumnIndex("week_name")),
                        cursor.getString(cursor.getColumnIndex("class_room")),
                        cursor.getInt(cursor.getColumnIndex("day")),
                        cursor.getInt(cursor.getColumnIndex("class_start")),
                        cursor.getInt(cursor.getColumnIndex("class_end"))));
            } while(cursor.moveToNext());
        }
        cursor.close();
        //这里可以从网页加载数据.
        //使用从数据库读取出来的课程信息来加载课程表视图
        for (Course course : coursesList) {
            createLeftView(course);         //一开始的时候不会创建左侧试图
            createItemCourseView(course);   //课程列表
        }
    }
    public void loadDataFromURL()  {
        Map<String,String> map = new HashMap<>();
        map.put("termCode","2019-2020-1");
        map.put("sort","TimeName");
        map.put("order","ASC");
        new retrofit2().GetStudentTimetable(map).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mycoursesList = retrofit.Getstudenttimetable(response);
                Log.e("lDFU_onResponse","sucessful");
                Log.e("mycoursesList_size",String.valueOf(mycoursesList.size()));
                Message msg = new Message();
                msg.what = 0x01;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        Message m = handler.obtainMessage();
        handler.sendMessage(m); //发送消息
    }
    //创建"第几节数"视图
    private void createLeftView(Course course) {
        int endNumber = course.getEnd();
        //maxCoursesNumber = 0,一般只有10节课
        if (endNumber > maxCoursesNumber) {
            for (int i = 0; i < endNumber-maxCoursesNumber; i++) {
                //对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater.inflate()来载入
                //对于一个已经载入的界面，就可以使用Activiyt.findViewById()方法来获得其中的界面元素。
                View view = LayoutInflater.from(this).inflate(R.layout.left_view, null);    //载入left_view布局
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(110,190);
                view.setLayoutParams(params);   //设置布局参数

                TextView text = view.findViewById(R.id.class_number_text);
                text.setText(String.valueOf(++currentCoursesNumber));   //设置文本

                LinearLayout leftViewLayout = findViewById(R.id.left_view_layout);
                leftViewLayout.addView(view);
            }
            maxCoursesNumber = endNumber;
        }
    }

    //创建单个课程视图
    private void createItemCourseView(final Course course) {
        int getDay = course.getDay();
        if ((getDay < 1 || getDay > 7) || course.getStart() > course.getEnd())
            Toast.makeText(this, "星期几没写对,或课程结束时间比开始时间还早~~", Toast.LENGTH_LONG).show();
        else {
            int dayId = 0;
            switch (getDay) {
                case 1: dayId = R.id.monday; break;
                case 2: dayId = R.id.tuesday; break;
                case 3: dayId = R.id.wednesday; break;
                case 4: dayId = R.id.thursday; break;
                case 5: dayId = R.id.friday; break;
                case 6: dayId = R.id.saturday; break;
                case 7: dayId = R.id.weekday; break;
            }
            day = findViewById(dayId);
            //width = 110,height=210
            int height = 190;
            final View v = LayoutInflater.from(this).inflate(R.layout.course_card, null); //加载单个课程布局
            //start=1,setY=0.end=2,
            v.setY(height * (course.getStart()-1)); //设置开始高度,即第几节课开始
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT,(course.getEnd()-course.getStart()+1)*height - 8); //设置布局高度,即跨多少节课
            v.setLayoutParams(params);
            TextView text = v.findViewById(R.id.text_view);
            text.setText(course.getCourseName() + "\n" + course.getTeacher() + "\n" + course.getClassRoom()); //显示课程名
            day.addView(v);
            //长按删除课程
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    v.setVisibility(GONE);//先隐藏
                    day.removeView(v);//再移除课程视图
                    SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
                    sqLiteDatabase.execSQL("delete from courses where course_name = ?", new String[] {course.getCourseName()});
                    return true;
                }
            });
            //长按查看课程
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(StudentTimeTable.this,com.example.lookup.TimeTable.tabledetail.class);
                    staticTimeTable.class_room = course.getClassRoom();
                    staticTimeTable.course_name = course.getCourseName();
                    staticTimeTable.teacher_name = course.getTeacher();
                    staticTimeTable.week_name = course.getWeekTime();
                    staticTimeTable.cno = course.getCno();
                    //使用Serializable 和 Parceable 传递对象，使用Bundle的话
                    Log.e("timetable",staticTimeTable.week_name);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }
    //保存数据到数据库
    private void saveData(Course course) {
        SQLiteDatabase sqLiteDatabase =  databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL
                ("insert into courses(course_name, teacher, class_room, day, class_start, class_end) " + "values(?, ?, ?, ?, ?, ?)",
                        new String[] {course.getCourseName(),
                                course.getTeacher(),
                                course.getClassRoom(),
                                course.getDay()+"",
                                course.getStart()+"",
                                course.getEnd()+""}
                );
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //这个方法负责生成menu,它是一个回调方法,当按下手机设备上的menu按键的时候,android系统才会生成一个包含两个子项的菜单.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_courses:
                Intent intent = new Intent(StudentTimeTable.this, AddCourseActivity.class);
                //布局显示一半
                startActivityForResult(intent, 0);
                break;
            case R.id.menu_about:
                //Intent intent1 = new Intent(this, AboutActivity.class);
                //startActivity(intent1);
                break;
        }
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Course course = (Course) data.getSerializableExtra("course");
            //创建课程表左边视图(节数)
            createLeftView(course);
            //创建课程表视图
            createItemCourseView(course);
            //存储数据到数据库
            saveData(course);
        }
    }
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
                    intent = new Intent(getApplicationContext(),aboutAuthor.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}
