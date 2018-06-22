package com.poloapps.cryptomon;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class serviceCM extends Service {
    private boolean hasStarted = false;
    private static final String TAG = "com.poloapps.cryptomon";
    final Handler handler = new Handler();
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        hasStarted = true;
                        Log.i(TAG, "Service is running");
                        Toast.makeText(getApplicationContext(), "Service is running",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // error, do something
                    }
                }
            });
        }
    };
    public serviceCM() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service started",
                Toast.LENGTH_SHORT).show();
        if (hasStarted) timer.schedule(task, 0 , 3000);  // interval of 10 sec

        return  Service.START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel();
        hasStarted = false;
        Toast.makeText(getApplicationContext(), "Service has stopped",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
