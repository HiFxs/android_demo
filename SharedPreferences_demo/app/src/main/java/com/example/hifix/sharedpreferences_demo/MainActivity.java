package com.example.hifix.sharedpreferences_demo;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/** 不同于文件的存储方式，SharedPreferences是使用键值对的方式来存储数据的。
        也就是说当保存一条数据的时候，需要给这条数据提供一个对应的键，
        这样在读取数据的时候就可以通过这个键把相应的值取出来。
        而且SharedPreferences还支持多种不同的数据类型存储，
        如果存储的数据类型是整型，
        那么读取出来的数据也是整型的，
        存储的数据是一个字符串，读取出来的数据仍然是字符串。
        */
public class MainActivity extends AppCompatActivity {
private Button button;
private  Button restorebutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.save_data);
        restorebutton=(Button)findViewById(R.id.restore_data);
       restorebutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SharedPreferences sharedPreferences=getSharedPreferences("data",MODE_PRIVATE);
               String name=sharedPreferences.getString("name","");
               int age=sharedPreferences.getInt("age",0);
               boolean married=sharedPreferences.getBoolean("married",false);
               Log.d("MainActivity", "name is " + name);
               Log.d("MainActivity", "age is " + age);
               Log.d("MainActivity", "married is " + married);
           }
       });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
editor.putString("name","Tom");
editor.putInt("age",18);
editor.putBoolean("married",false);
editor.commit();
                Toast.makeText(MainActivity.this,"sharaed store successful!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
