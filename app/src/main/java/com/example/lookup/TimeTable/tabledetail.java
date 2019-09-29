package com.example.lookup.TimeTable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.lookup.R;
import com.example.lookup.Retrofit2.retrofit2;
import retrofit2.Callback;
import retrofit2.Call;
import okhttp3.ResponseBody;
import retrofit2.Response;
/*
*       长按课表，显示更多信息。
*
* */
public class tabledetail extends AppCompatActivity {
    private TextView course_name,teacher_name,week_name,class_room,class_number;
    private String Classname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabledetail);
        init();
    }
    private void init(){
        course_name = (TextView)findViewById(R.id.course_name);
        teacher_name = (TextView)findViewById(R.id.teacher_name);
        week_name = (TextView)findViewById(R.id.week_name);
        class_room = (TextView)findViewById(R.id.class_name);
        class_number = (TextView)findViewById(R.id.class_number);
        final retrofit2 retrofit = new retrofit2();
        Log.e("cno",staticTimeTable.cno);
        retrofit.GetTeachingClassUnits(staticTimeTable.cno).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Classname = retrofit.Getteachingclassunits(response);
                Message message = new Message();
                message.what = 0x01;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01){
                course_name.setText(staticTimeTable.course_name);
                teacher_name.setText(staticTimeTable.teacher_name);
                week_name.setText(staticTimeTable.week_name);
                class_number.setText(staticTimeTable.class_room);
                class_room.setText(Classname );
            }
        }
    };
}
