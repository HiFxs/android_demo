package com.example.hifix.mp3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MusicService extends Service {
public  Mybinder binder=new Mybinder();
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public  class Mybinder extends Binder{

        MusicService getService(){
            return MusicService.this;
        }

    }
}
