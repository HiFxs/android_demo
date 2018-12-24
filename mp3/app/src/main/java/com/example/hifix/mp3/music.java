package com.example.hifix.mp3;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;

public class music extends AppCompatActivity{
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private MusicService musicService;
    public static TextView musicStatus, musicTime, musicTotal;
    private SeekBar seekBar;
    public static  ImageView imageView;
    public static boolean tag1 = false;
    public static boolean tag2 = false;
   public  static Bitmap b1;
   public static   ObjectAnimator animator;
    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            musicTime.setText(time.format(musicService.mediaPlayer.getCurrentPosition()));
            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
            seekBar.setMax(musicService.mediaPlayer.getDuration());
            musicTotal.setText(time.format(musicService.mediaPlayer.getDuration()));
            handler.postDelayed(runnable, 200);

        }
    };
    //  回调onServiceConnected 函数，通过IBinder 获取 Service对象，实现Activity与 Service的绑定
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) (service)).getService();
            Log.i("musicService", musicService + "");
            musicTotal.setText(time.format(musicService.mediaPlayer.getDuration()));
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };


    //  在Activity中调用 bindService 保持与 Service 的通信
    private void bindServiceConnection() {
        Intent intent = new Intent(music.this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, this.BIND_AUTO_CREATE);
    }
    public static Button btn_playorpause,btn_quit,btn_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        btn_playorpause = (Button) findViewById(R.id.btn_playorpause);
        btn_quit = (Button) findViewById(R.id.btn_quit);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        imageView=(ImageView)findViewById(R.id.Image);
        b1=MusicUtils.createAlbumArt(MainActivity.path);
        music.imageView.setImageBitmap(MusicUtils.toRoundBitmap(b1));
        musicTime = (TextView) findViewById(R.id.MusicTime);
        musicTotal = (TextView) findViewById(R.id.MusicTotal);
        seekBar = (SeekBar) findViewById(R.id.MusicSeekBar);
        musicStatus = (TextView) findViewById(R.id.MusicStatus);
        myListener();
        bindServiceConnection();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    musicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    private void myListener() {
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);
        btn_playorpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService.mediaPlayer != null) {
                    seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
                    seekBar.setMax(musicService.mediaPlayer.getDuration());
                }
                //  由tag的变换来控制事件的调用
                if (musicService.tag != true) {
                    btn_playorpause.setText("暂停");
                    musicStatus.setText("播放~~");
                    musicService.playOrPause();
                    musicService.tag = true;

                    if (tag1 == false) {
                        animator.start();
                        tag1 = true;
                    } else {
                        animator.resume();
                    }
                } else {
                    btn_playorpause.setText("播放");
                    musicStatus.setText("暂停~~");
                    musicService.playOrPause();
                    animator.pause();
                    musicService.tag = false;
                }
                if (tag2 == false) {
                    handler.post(runnable);
                    tag2 = true;
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicStatus.setText("停止~~");
                btn_playorpause.setText("播放");
                musicService.stop();
                animator.pause();
                musicService.tag = false;
            }
        });
        //  停止服务时，必须解除绑定，写入btnQuit按钮中
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                unbindService(serviceConnection);
                Intent intent = new Intent(music.this, MusicService.class);
                stopService(intent);

                try {
                    music.this.finish();
                } catch (Exception e) {

                }
            }
        });




    }
    //  获取并设置返回键的点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy(){
        if(musicService.mediaPlayer!=null&&musicService.mediaPlayer.isPlaying()){
            musicService.mediaPlayer.stop();
            musicService.mediaPlayer.release();
            musicService.mediaPlayer=null;
        }

        super.onDestroy();
    }
}
