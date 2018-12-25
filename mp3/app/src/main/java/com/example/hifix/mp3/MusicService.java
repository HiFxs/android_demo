package com.example.hifix.mp3;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.hifix.mp3.MainActivity.text;

public class MusicService extends Service {
    private static final int DELETE_PENDINGINTENT_REQUESTCODE = 1022;
    private static final int CONTENT_PENDINGINTENT_REQUESTCODE = 1023;
    private static final int PLAY_PENDINGINTENT_REQUESTCODE = 1025;
    private static final int STOP_PENDINGINTENT_REQUESTCODE = 1026;
    private static final int NOTIFICATION_PENDINGINTENT_ID = 1;// 是用来标记Notifaction，可用于更新，删除Notifition
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private BroadcastReceiver playerReceiver;
    private boolean isPause = false;
    public static   MediaPlayer mediaPlayer;
    public boolean tag = false;
    public static RemoteViews views;
    public MusicService() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(MainActivity.path);
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //1.设置前台服务通知
    @Override
    public void onCreate() {
        // 设置点击通知结果
        //意图
        Intent intent = new Intent(this, MainActivity.class);
        //预处理意图
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, CONTENT_PENDINGINTENT_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent delIntent = new Intent(this, MusicService.class);
        PendingIntent delPendingIntent = PendingIntent.getService(this, DELETE_PENDINGINTENT_REQUESTCODE, delIntent, PendingIntent.FLAG_UPDATE_CURRENT);
// 自定义布局、动态加载布局

        views = new RemoteViews(getPackageName(), R.layout.layout_mediaplayer);

        MusicService.views.setTextViewText(R.id.tv_name,MainActivity.text);
        MusicService.views.setTextViewText(R.id.tv_author, MainActivity.singer);
        // 暂停/播放
        Intent intentPlay = new Intent("playMusic");
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, PLAY_PENDINGINTENT_REQUESTCODE, intentPlay, PendingIntent.FLAG_CANCEL_CURRENT);
       //绑定相应事件
        views.setOnClickPendingIntent(R.id.tv_pause, playPendingIntent);

        // 停止
        Intent intentStop = new Intent("stopMusic");
        //获取广播
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, STOP_PENDINGINTENT_REQUESTCODE, intentStop, PendingIntent.FLAG_CANCEL_CURRENT);

        views.setOnClickPendingIntent(R.id.tv_cancel, stopPendingIntent);

        builder = new NotificationCompat.Builder(this)
                // 设置小图标
                .setSmallIcon(R.mipmap.img)
                // 设置标题
                .setContentTitle("HIFIX_Player")
                // 设置内容
                .setContentText("内容")
                // 点击通知后自动清除
                .setAutoCancel(false)
                // 设置点击通知效果
                .setContentIntent(contentPendingIntent)
                // 设置删除时候出发的动作
                .setDeleteIntent(delPendingIntent)
                // 自定义视图
                .setContent(views);

        // 获取NotificationManager实例
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // 发送通知，并设置id
//        notificationManager.notify(NOTIFICATION_PENDINGINTENT_ID, builder.build());

        startForeground(NOTIFICATION_PENDINGINTENT_ID, builder.build());
        // 2.注册广播
        playerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("action", intent.getAction());
                switch (intent.getAction()) {
                    case "playMusic":// 暂停/播放
                        if (mediaPlayer != null) {
                            if (!isPause) {
                                mediaPlayer.pause();
                                //用于控制暂停和播放的监控
                                isPause = true;
                                tag=false;
                                music.musicStatus.setText("暂停~~");
                                music.btn_playorpause.setText("播放");
                                music.animator.pause();//暂停动画
                                //更新通知栏
                                updateNotification();
                            } else {
                                mediaPlayer.start();
                                isPause = false;
                                tag=true;
                                music.musicStatus.setText("播放~~");
                                music.btn_playorpause.setText("暂停");
                                music.animator.resume();//恢复动画
                                updateNotification();
                            }
                        }
                        break;
                    case "stopMusic":// 停止
                        stop();
                        break;
                }
            }
        };

        //意图过滤器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("playMusic");
        intentFilter.addAction("stopMusic");
        registerReceiver(playerReceiver, intentFilter);

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    //  通过 Binder 来保持 Activity 和 Service 的通信
    public MyBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playOrPause() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPause = true;
            music.musicStatus.setText("暂停~~");
            music.btn_playorpause.setText("播放");
            music.animator.pause();
            updateNotification();
        } else {
            mediaPlayer.start();
            isPause = false;
            music.musicStatus.setText("播放~~");
            music.btn_playorpause.setText("暂停");
            music.animator.resume();
            updateNotification();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            music.animator.pause();
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MainActivity.path);
                mediaPlayer.prepare();

                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 取消Notification
        if (notificationManager != null)
            notificationManager.cancel(NOTIFICATION_PENDINGINTENT_ID);
        stopForeground(true);
        // 停止服务
        stopSelf();
    }
    // 3.更新Notification
    private void updateNotification() {
        if (views != null) {

            if (!isPause) {
                views.setTextViewText(R.id.tv_pause, "暂停");
            } else {
                views.setTextViewText(R.id.tv_pause, "播放");
            }
        }

        // 刷新notification
        notificationManager.notify(NOTIFICATION_PENDINGINTENT_ID, builder.build());
    }
    public static void savePNG_After(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
