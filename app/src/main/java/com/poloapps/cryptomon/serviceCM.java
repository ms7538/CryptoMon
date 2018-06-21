package com.poloapps.cryptomon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class serviceCM extends Service {

    private static final String TAG = "com.poloapps.cryptomon";

    public serviceCM() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"on Start called");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //TODO Implement Timed task and stop at onDestroy
                //                for(int i = 0; i < 500;i++){
//                    long futureTime = System.currentTimeMillis() + 2000;
//                    while (System.currentTimeMillis()< futureTime){
//                        synchronized (this){
//                            try{
//                                wait(futureTime-System.currentTimeMillis());
//                                Log.i(TAG, "Service is doing something");
//                            }catch (Exception e){}
//                        }
//                    }
//                }
            }
        };
        Thread cmThread = new Thread(r);
        cmThread.start();
        return  Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
