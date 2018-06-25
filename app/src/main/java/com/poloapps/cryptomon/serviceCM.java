package com.poloapps.cryptomon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class serviceCM extends Service {

    String LC_url    = "https://api.coinmarketcap.com/v1/ticker/";
    String strTicker = "CM ALERTS:";
    String strCTp1   = "Number of Alerts: ";

    NotificationCompat.Builder cmNotification;
    private  static  final int uniqueID = 243823;
    private  static  final int uID      = 527354;
    String idUnique = Integer.toString(uniqueID);
    String uIDstr   = Integer.toString(uID);

    private boolean hasStarted = false;
    final Handler   handler    = new Handler();
    Timer           timer      = new Timer();

    dbPriceHandler        dbPHandler;
    dbCurrentValsHandler  dbCVHandler;
    dbPriceAlertsAchieved dbPAchHandler;

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        hasStarted = true;

                        Log.i("CM22","service running");
                        updateCurrentVals();
                        checkPriceAchieved();

                        int achievedAlerts  = returnNumberAlerts();
                        if (achievedAlerts > 0 ){
                            Intent intent =
                                    new Intent(getApplication(), All_AlertsActivity.class);

                            if (Build.VERSION.SDK_INT >= 26) {

                                NotificationChannel channel = new NotificationChannel(idUnique,
                                        strTicker, NotificationManager.IMPORTANCE_MIN);

                                ((NotificationManager) Objects.requireNonNull(getSystemService
                                        (Context.NOTIFICATION_SERVICE)))
                                        .createNotificationChannel(channel);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent =
                                        PendingIntent.getActivity(getApplication(),
                                                0, intent, 0);

                                Notification notification = new
                                        NotificationCompat.Builder(getApplication(),idUnique)
                                        .setContentTitle(strTicker)
                                        .setTicker(strTicker)
                                        .setOngoing(false)
                                        .setSmallIcon(R.drawable.ic_action_alert_red)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true)
                                        .setContentText(
                                                strCTp1 + Integer.toString(achievedAlerts)).build();

                                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                createNotificationChannel();
                                NotificationManagerCompat notificationManager
                                        = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(uID, notification);

                            } else {

                                cmNotification.setSmallIcon(R.drawable.ic_action_alert_red);
                                cmNotification.setTicker(strTicker);
                                cmNotification.setWhen(System.currentTimeMillis());
                                cmNotification.setContentTitle(strTicker);
                                cmNotification.setContentText(
                                        strCTp1 + Integer.toString(achievedAlerts));


                                PendingIntent pendingIntent =
                                        PendingIntent.getActivity(
                                                getApplicationContext(), 0,
                                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                cmNotification.setContentIntent(pendingIntent);

                                NotificationManager nm =
                                        (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                assert nm != null;
                                nm.notify(uniqueID, cmNotification.build());

                            }

                            Log.i("CM22","Number of Alerts: " +
                                                                Integer.toString(achievedAlerts));
                        }

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

        Log.i("CM22","service started");
        if (!hasStarted) {
            timer.schedule(task, 15000 , 60000);  // interval of 30 sec
            Log.i("CM22","service scheduled");
            hasStarted = true;
        }
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

        cmNotification = new NotificationCompat.Builder(this, Integer.toString(uniqueID));
        cmNotification.setAutoCancel(true);

        dbPHandler     = new dbPriceHandler(this, null);
        dbCVHandler    = new dbCurrentValsHandler(this, null);
        dbPAchHandler  = new dbPriceAlertsAchieved(this, null);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) Objects.requireNonNull(getSystemService
                    (Context.NOTIFICATION_SERVICE))).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setAutoCancel(true)
                    .setContentText("").build();

            startForeground(1, notification);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void updateCurrentVals(){
        StringRequest crypto100_request = new StringRequest(LC_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONArray T100_Array = new JSONArray(string);
                            for (int i = 0; i < T100_Array.length(); i++) {

                                JSONObject obj1 = T100_Array.getJSONObject(i);

                                String rate       = obj1.getString("price_usd");
                                Double d_rate     = Double.parseDouble(rate);
                                Double curr_vol   = Double.parseDouble(
                                        obj1.getString("24h_volume_usd"));
                                String link_id    = obj1.getString("id");
                                dbCVHandler.deleteEntry(link_id);
                                dbCVHandler.addCurrentVals(link_id,d_rate,curr_vol);

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!",
                        Toast.LENGTH_SHORT).show();

            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(serviceCM.this);
        rQueue.add(crypto100_request);
    }

    int returnNumberAlerts(){
        String priceAchieved   = dbPAchHandler.dbToString();
        String[] splitPAAlerts = priceAchieved.split("[\n]");
        int len2               = splitPAAlerts.length;

        if (splitPAAlerts[0].equals("")){
            len2 = 0;
        }
        return len2;
    }

    void checkPriceAchieved(){
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1              = splitPAlerts.length;

        if (splitPAlerts[0].equals("")){
            len1 = 0;
            stopSelf();
        }

        for (int i = 0; i < len1; i++) {
            double price   = Double.parseDouble(dbCVHandler.currentPrice(splitPAlerts[i]));
            double thPrice = Double.parseDouble(dbPHandler.getPrice_Val(splitPAlerts[i]));
            int    check   = Integer.parseInt(dbPHandler.getThresh_Check(splitPAlerts[i]));

            if ((thPrice < price && check == 1) || (thPrice > price && check == -1)) {

                dbPHandler.deleteAlert(splitPAlerts[i]);
                dbPAchHandler.removePAAlert(splitPAlerts[i]);
                dbPAchHandler.addPriceAchAlert(splitPAlerts[i], price, thPrice, check);
            }
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = idUnique;
            String description = strTicker;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(idUnique, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

}
