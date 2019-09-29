package com.example.lookup;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.lookup.Retrofit2.retrofit2;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
public class Login extends AppCompatActivity {
    private TextInputEditText usernameView;      //用户名
    private TextInputEditText passwordView;      //密码
    private String un="",pw="";             //文本
    private RadioButton Rememberme;         //记住密码
    private retrofit2 retrofit;
    private Message message;
    private Button Autowrite,Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        Autowrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                SharedPreferences sharedPreferences = getSharedPreferences("im", 0);
                String sharedPreferences_username = sharedPreferences.getString("username","default");
                String sharedPreferences_password= sharedPreferences.getString("password","default");
                if (Objects.equals(sharedPreferences_username, "default")){
                    TastyToast.makeText(Login.this,"请先点击记住密码后再登录",TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                }else{
                    usernameView.setText(sharedPreferences_username);
                    passwordView.setText(sharedPreferences_password);
                }
            }
        });
        Login.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                //登录.
                un = Objects.requireNonNull(usernameView.getText()).toString();
                pw = Objects.requireNonNull(passwordView.getText()).toString();
                if (un.equals("") || pw.equals("")){
                    TastyToast.makeText(Login.this,"用户名或密码不为空",TastyToast.LENGTH_SHORT,TastyToast.ERROR);
                }else{
                    retrofit = new retrofit2();
                    retrofit.LoginToUSC(un,pw).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            int type = retrofit.Getloginresponse(response);
                            if (type==1){
                                message = handler.obtainMessage(0x01);
                                message.what = 0x01;
                                handler.sendMessage(message);
                            }else{
                                message = handler.obtainMessage(0x02);
                                message.what = 0x02;
                                handler.sendMessage(message);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.theme) {
            if (AppCompatDelegate.getDefaultNightMode() != MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
            recreate();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if (msg.what==0x01){
                if (Rememberme.isChecked()){
                    SharedPreferences sharedPreferences = getSharedPreferences("im",0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username",un);
                    editor.putString("password",pw);
                    editor.apply(); //提交
                }
                TastyToast.makeText(Login.this,"登录成功",TastyToast.LENGTH_SHORT,TastyToast.SUCCESS);
                //等待两秒，跳转
                Message m = new Message();
                m.what = 0x03;
                handler.sendMessageDelayed(m,500);
            }else if (msg.what == 0x02){
                TastyToast.makeText(Login.this,"用户名或密码错误",TastyToast.LENGTH_SHORT,TastyToast.ERROR);

            }
            else if (msg.what==0x03){
                Intent intent = new Intent(com.example.lookup.Login.this, com.example.lookup.MainActivity.class);
                startActivity(intent);  //启动活动
            }
        }
    };
    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameView = findViewById(R.id.Username);
        TextInputLayout passwordViewTIL = findViewById(R.id.password_til);

        passwordView = findViewById(R.id.Password);
        TextInputLayout passwordViewWithStrikeThroughTIL = findViewById(R.id.password_strike_til);

        Typeface roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        usernameView.setTypeface(roboto);
        usernameView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  //显示
        passwordViewTIL.setTypeface(roboto);
        passwordView.setTypeface(roboto);
        passwordViewWithStrikeThroughTIL.setTypeface(roboto);
        Login = findViewById(R.id.login);
        Autowrite = findViewById(R.id.AutoWrite);
        Rememberme = findViewById(R.id.rememberme);

        message = new Message();
    }
}
