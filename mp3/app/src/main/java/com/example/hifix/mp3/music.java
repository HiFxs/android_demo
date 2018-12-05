package com.example.hifix.mp3;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;

public class music extends AppCompatActivity implements View.OnClickListener {
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private MediaPlayer mediaPlayer;
    private MusicService musicService;
    private TextView musicStatus, musicTime, musicTotal;
    private SeekBar seekBar;
    private  ObjectAnimator animator;
    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            musicTime.setText(time.format(mediaPlayer.getCurrentPosition()));
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekBar.setMax(mediaPlayer.getDuration());
            musicTotal.setText(time.format(mediaPlayer.getDuration()));
            handler.postDelayed(runnable, 200);

        }
    };
    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.Mybinder) (service)).getService();
            Log.i("musicService", musicService + "");
            // musicTotal.setText(time.format(mediaPlayer.getDuration()));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    private Button btn_play,btn_pause,btn_replay,btn_stop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        myListener();
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_replay = (Button) findViewById(R.id.btn_replay);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_replay.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        musicTime = (TextView) findViewById(R.id.MusicTime);
        musicTotal = (TextView) findViewById(R.id.MusicTotal);
        seekBar = (SeekBar) findViewById(R.id.MusicSeekBar);
        musicStatus = (TextView) findViewById(R.id.MusicStatus);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser == true) {
                    mediaPlayer.seekTo(progress);
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

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_pause:
                pause();
                break;
            case R.id.btn_replay:
                replay();
                break;
            case R.id.btn_stop:
                stop();
                break;

            default:
                break;
        }
    }
    /**
     play  music
     */
    protected void play(){
        File file=new File(MainActivity.path);
        if(file.exists()&&file.length()>0){
            try{
                ImageView imageView = (ImageView) findViewById(R.id.Image);
                animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
                animator.setDuration(10000);
                animator.setInterpolator(new LinearInterpolator());
                animator.setRepeatCount(-1);
                mediaPlayer=new MediaPlayer();
                mediaPlayer.setDataSource(MainActivity.path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                seekBar.setMax(mediaPlayer.getDuration());
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                       musicStatus.setText("Playing~~~~");
//                        mediaPlayer.start();
//                        Toast.makeText(music.this,"开始播放",Toast.LENGTH_SHORT).show();
//                        animator.start();
//                        btn_play.setEnabled(false);
//                    }
//                });
                musicStatus.setText("Playing~~~~");
                       mediaPlayer.start();
                      Toast.makeText(music.this,"开始播放",Toast.LENGTH_SHORT).show();
                      animator.start();
                       btn_play.setEnabled(false);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btn_play.setEnabled(true);
                    }
                });
                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        replay();
                        return false;
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(music.this,"播放失败",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(music.this,"文件不存在",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * pause music
     */
    protected void pause(){
        if(btn_pause.getText().toString().trim().equals("继续")){
            btn_pause.setText("暂停");
            musicStatus.setText("Playing~~~~~");
            mediaPlayer.start();
            animator.start();
            handler.post(runnable);
            Toast.makeText(music.this,"继续播放",Toast.LENGTH_LONG).show();
            return;
        }
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            btn_pause.setText("继续");
            musicStatus.setText("Puse~~~~~");
            mediaPlayer.pause();
            animator.pause();
            handler.post(runnable);
            Toast.makeText(music.this,"暂停播放",Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * replay music
     */
    protected void replay(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
            Toast.makeText(music.this,"重新播放",Toast.LENGTH_LONG).show();
            btn_pause.setText("暂停");
            return;
        }
        play();
    }
    /**
     * stop music
     */
    protected void stop(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            animator.pause();
            mediaPlayer.release();
            mediaPlayer=null;
            btn_play.setEnabled(true);
            Toast.makeText(music.this,"停止播放",Toast.LENGTH_SHORT).show();
        }
        Intent intent=new Intent(music.this,MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onDestroy(){
        if(mediaPlayer!=null&&mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        super.onDestroy();
    }
}
