package com.example.lookup.classroom;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lookup.AutoEvaluation;
import com.example.lookup.GradeManage;
import com.example.lookup.MainActivity;
import com.example.lookup.R;
import com.example.lookup.Retrofit2.retrofit2;
import com.example.lookup.StudentTimeTable;
import com.example.lookup.aboutAuthor;
import com.lemonade.widgets.slidesidemenu.SlideSideMenuTransitionLayout;
import com.example.lookup.staticdata;


import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Call;

import retrofit2.Response;

//空闲教室
public class roommanage extends AppCompatActivity {
    private SlideSideMenuTransitionLayout mSlideSideMenu;
    private Toolbar mToolbar;
    private TextView Mainactivity_Username,QuerygradeAnyear,Index,Querytable,Querygrade,Autoevaluation,Getclassroom,Aboutauthor;

    private Spinner spinner1,spinner2,spinner3,spinner4,spinner5,spinner6,spinner7;
    private ArrayList<FreeClassRoom> freeClassRoomArrayList;
    private Map<String, String> requestDataMap;
    private Button sendData;
    private retrofit2 retrofit;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommanage);
        init();
        Message msg = new Message();
        msg.what = 0x01;
        handler.sendMessage(msg);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideSideMenu.toggle();
            }
        });
        sendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDataMap = new HashMap<>();
                requestDataMap.put("termCode","2019-2020-1");
                requestDataMap.put("campusCode","1");
                requestDataMap.put("buildingCode",spinner3.getSelectedItem().toString().split("-")[0]);
                //Log.e("buildingCode",spinner3.getSelectedItem().toString().split("-")[0]);
                requestDataMap.put("roomKind",spinner4.getSelectedItem().toString().split("-")[0]);
                //Log.e("roomKind",spinner4.getSelectedItem().toString().split("-")[0]);
                requestDataMap.put("timeCodes[]",spinner6.getSelectedItem().toString().split("-")[0]);
                //Log.e("timeCodes[]",spinner6.getSelectedItem().toString().split("-")[0]);
                requestDataMap.put("weekName",spinner7.getSelectedItem().toString());
                //Log.e("weekName",spinner7.getSelectedItem().toString());
                requestDataMap.put("capacity",spinner5.getSelectedItem().toString());
                //Log.e("capacity",spinner5.getSelectedItem().toString());
                requestDataMap.put("page","1");
                requestDataMap.put("rows","50");
                requestDataMap.put("sort","ClassRoomNo");
                requestDataMap.put("order","ASC");
                //调用方法.
                retrofit = new retrofit2();
                retrofit.GetFreeClassRoom(requestDataMap).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //解析数据，进行绑定.
                        freeClassRoomArrayList = new ArrayList<FreeClassRoom>();
                        freeClassRoomArrayList = retrofit.Getfreeclassroom(response);
                        Message m = new Message();
                        m.what = 0x02;
                        handler.sendMessage(m);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("error",call.toString());
                    }
                });
            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                Mainactivity_Username.setText(staticdata.Username);
            }else if (msg.what==0x02){
                SetListview();
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

        sendData = (Button)findViewById(R.id.send);
        spinner1 = (Spinner)findViewById(R.id.roommanage_spinner1);
        spinner2 = (Spinner)findViewById(R.id.roommanage_spinner2);
        spinner3 = (Spinner)findViewById(R.id.roommanage_spinner3);
        spinner4 = (Spinner)findViewById(R.id.roommanage_spinner4);
        spinner5 = (Spinner)findViewById(R.id.roommanage_spinner5);
        spinner6 = (Spinner)findViewById(R.id.roommanage_spinner6);
        spinner7 = (Spinner)findViewById(R.id.roommanage_spinner7);
        listView = (ListView)findViewById(R.id.roommanage_listView);

    }
    private View.OnClickListener listener= new View.OnClickListener(){
        Intent intent;
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.Index:
                    intent = new Intent(roommanage.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.querytable:
                    intent = new Intent(roommanage.this, StudentTimeTable.class);
                    startActivity(intent);
                    break;
                case R.id.querygrade:
                    intent = new Intent(roommanage.this, GradeManage.class);
                    startActivity(intent);
                    break;
                case R.id.querygradeAnyear:
                    intent = new Intent(roommanage.this,com.example.lookup.StuAcademicScore.GetList.class);
                    startActivity(intent);
                    break;
                case R.id.autoevaluation:
                    intent = new Intent(roommanage.this, AutoEvaluation.class);
                    startActivity(intent);
                    break;
                case R.id.getclassroom:
                    break;
                case R.id.aboutAuthor:
                    intent = new Intent(roommanage.this,aboutAuthor.class);
                    startActivity(intent);
                    break;
            }
        }
    };
    private void SetListview(){
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>(200);
        Log.e("容量",String.valueOf(freeClassRoomArrayList.size()));
        for(int i=0;i<freeClassRoomArrayList.size();i++){
            HashMap<String,Object> item = new HashMap<String, Object>();
            item.put("SchoolName","学院:"+freeClassRoomArrayList.get(i).getSchoolName());
            item.put("ClassRoomName","编号:"+ freeClassRoomArrayList.get(i).getClassRoomName());
            item.put("DataSign","所属:"+freeClassRoomArrayList.get(i).getDataSign());
            item.put("ClassRoomSize","容量:"+freeClassRoomArrayList.get(i).getClassRoomSize());
            item.put("ExamSites","考场:"+freeClassRoomArrayList.get(i).getExamSites());
            data.add(item);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(roommanage.this,data,R.layout.item,
                new String[]{"SchoolName","ClassRoomName","DataSign","ClassRoomSize","ExamSites"},new int[]{
                R.id.class_name,R.id.course_name,R.id.general_score,R.id.exam_score,R.id.final_score
        });
        listView.setAdapter(simpleAdapter);
    }
}
