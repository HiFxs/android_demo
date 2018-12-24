package com.example.hifix.mp3;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener ,AdapterView.OnItemClickListener{
    private long firstime = 0;
    private LinearLayout linearLayout;
    private Button scan_button;
    public static final int update_list = 1;
    public static String text="",singer="";
    private MyAdapter adapter;
    public  static  String path="";
    private ListView music_listView;
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case update_list:
                    //获取到音乐列表
                    music_list();
                    break;
                default:
                    break;
            }
        }
    };
    private List<Song> list;
    private List<String> DATA;
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        scan_button=(Button)findViewById(R.id.button);
        scan_button.setOnClickListener(this);
        linearLayout=(LinearLayout)findViewById(R.id.music_layout);
    }

    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.button:
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=update_list;
                handler.sendMessage(message);
            }
        }).start();
        break;
    default:
        break;
}
    }

    /**
     * 获取音乐列表
     */
protected  void music_list(){

    LayoutInflater layoutInflater=getLayoutInflater();
    linearLayout.removeView(scan_button);
    linearLayout.addView(music_listView);

}
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      text=(String)adapter.getItem(position).song;
      path=(String)adapter.getItem(position).path;
      singer=(String)adapter.getItem(position).singer;
        //MusicService.views.setTextViewText(R.id.tv_name, text);
        //MusicService.views.setTextViewText(R.id.tv_author,(String)adapter.getItem(position).singer );
        Intent intent=new Intent(MainActivity.this,music.class);
        startActivity(intent);

      //et_path.setText(text);

        //大多数情况下，position和id相同，并且都从0开始
      //  Toast.makeText(this, "listview的"+text+"被点击了！，点击的位置是-->" + position,Toast.LENGTH_SHORT).show();
    }
    private void init(){
       // listView=(ListView)findViewById(R.id.music_list);
        list=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },1);
        }else{
            list=MusicUtils.getMusicData(this);
        }
        music_listView=new ListView(this);
        music_listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        adapter=new MyAdapter(this,list);
        music_listView.setAdapter(adapter);
        music_listView.setOnItemClickListener(this);
    }

    //运行时权限处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode){
        case 1:
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                list=MusicUtils.getMusicData(this);
            }else{
                Toast.makeText(this,"You denoed the permission",Toast.LENGTH_LONG).show();
            }
        break;
            default:
    }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
// TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondtime = System.currentTimeMillis();
            if (secondtime - firstime > 3000) {
                Toast.makeText(MainActivity.this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstime = System.currentTimeMillis();
                return true;
            } else {
                finish();
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
