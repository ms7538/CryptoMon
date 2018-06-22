package com.poloapps.cryptomon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class serviceCM extends Service {
    private boolean hasStarted = false;
    final Handler handler      = new Handler();
    Timer timer                = new Timer();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        hasStarted = true;
                        Toast.makeText(getApplicationContext(), "Service is running",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // error, do something
                    }
                }
            });
        }
    };
    public serviceCM() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service started",
                Toast.LENGTH_SHORT).show();
        if (!hasStarted) timer.schedule(task, 0 , 10000);  // interval of 10 sec

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
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) Objects.requireNonNull(getSystemService
                    (Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
