package com.example.hifix.sql_android_demo;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
//文件存储的关键技术就是contend的openfileinput和openfileoutput,再结合java的字节流和字符流操作
//进行文件存储
public class MainActivity extends AppCompatActivity {
private EditText editText;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String inputtext=editText.getText().toString();

        save(inputtext);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=(EditText)findViewById(R.id.edit);
        String inputText=load();
        if (!TextUtils.isEmpty(inputText)) {
            editText.setText(inputText);
            editText.setSelection(inputText.length());
            Toast.makeText(this, "Restoring succeeded", Toast.LENGTH_SHORT).show();
        }
    }
    public  String load(){
        FileInputStream in=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            in=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(in));
            String line="";
            while((line=reader.readLine())!=null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
return content.toString();
    }

public void  save(String inputtext){
    FileOutputStream out=null;
    BufferedWriter writer=null;

try{

    out=openFileOutput("data", Context.MODE_PRIVATE);
writer=new BufferedWriter(new OutputStreamWriter(out));
writer.write(inputtext);
}catch (IOException e){
    e.printStackTrace();
}finally {
    try{
if(writer!=null){

    writer.close();
}
    }catch (IOException e){
e.printStackTrace();
    }
}



}


}
