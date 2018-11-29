package com.poloapps.cryptomon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String strCTp1   = "New Alerts: ";

    final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
    final Boolean Dollar = mSettings.getBoolean("Dollar", true);
    final String  Curr   = mSettings.getString("Curr_code","eur");

    NotificationCompat.Builder cmNotification;
    private  static  final int uniqueID = 243823;
    private  static  final int uID      = 527354;

    String idUnique            = Integer.toString(uniqueID);
    Integer overwritten        = 0;
    Integer deleteTimeHrs      = 12;
    private boolean hasStarted = false;
    final Handler   handler    = new Handler();
    Timer           timer      = new Timer();

    dbPriceHandler        dbPHandler;
    dbVolumeHandler       dbVHandler;
    dbCurrentValsHandler  dbCVHandler;
    dbPriceAlertsAchieved dbPAchHandler;
    dbVolAlertsAchieved   dbVAchHandler;

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            handler.post(new Runnable() {
                public void run() {
                    try {
                        hasStarted = true;
                        Log.i("CM22","service running");
                        updateCurrentVals();
                        checkAchieved();

                        int achievedAlerts  = returnNumberAlerts();
                        overwritten = 0;

                        if (achievedAlerts > 0 ){
                            Intent intent =
                                    new Intent(getApplication(), AllAlertsActivity.class);

                            if (Build.VERSION.SDK_INT >= 26) {

                                NotificationChannel channel = new NotificationChannel(idUnique,
                                        strTicker, NotificationManager.IMPORTANCE_HIGH);

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
                                        .setPriority(NotificationManager.IMPORTANCE_MAX)
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
            timer.schedule(task, 15000 , 600000);  // interval of 10 min
            Log.i("CM22","service scheduled");
            hasStarted  = true;
            overwritten = 0;
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
        dbVHandler     = new dbVolumeHandler(this, null);
        dbCVHandler    = new dbCurrentValsHandler(this, null);
        dbPAchHandler  = new dbPriceAlertsAchieved(this, null);
        dbVAchHandler  = new dbVolAlertsAchieved(this, null);

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

    @Override public IBinder onBind(Intent intent) { return null; }

    void updateCurrentVals(){
        if(!Dollar) LC_url = LC_url + "?convert=" + Curr;
        StringRequest crypto100_request = new StringRequest(LC_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {

                            String price_key      = "price_usd";
                            String v24h_key       = "24h_volume_usd";

                            if(!Dollar){
                                price_key      = "price_" + Curr;
                                v24h_key       = "24h_volume_" + Curr;
                            }
                            JSONArray T100_Array = new JSONArray(string);
                            for (int i = 0; i < T100_Array.length(); i++) {

                                JSONObject obj1 = T100_Array.getJSONObject(i);

                                String rate       = obj1.getString(price_key);
                                Double d_rate     = Double.parseDouble(rate);
                                Double curr_vol   = Double.parseDouble(
                                                           obj1.getString(v24h_key));
                                String link_id    = obj1.getString("id");
                                long millis       = System.currentTimeMillis();
                                Integer hours     = (int) (millis/1000/60/60);

                                dbCVHandler.deleteEntry(link_id);
                                dbCVHandler.addCurrentVals(link_id,d_rate,curr_vol,hours);
                            }

                        } catch (JSONException e) {  e.printStackTrace();    }
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
        String priceAchieved   = dbPAchHandler.dbEntries();
        String[] splitPAAlerts = priceAchieved.split("[\n]");
        int len2               = splitPAAlerts.length;
        if (splitPAAlerts[0].equals(""))len2 = 0;

        String volAchieved     = dbVAchHandler.dbEntries();
        String[] splitVAAlerts = volAchieved.split("[\n]");
        int len3               = splitVAAlerts.length;
        if (splitPAAlerts[0].equals(""))len3 = 0;

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        int dispAlerts = mSettings.getInt("disp_alerts",0);
        return len2 + len3 - dispAlerts + overwritten;
    }

    void checkAchieved(){
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1              = splitPAlerts.length;
        if (splitPAlerts[0].equals("")) len1 = 0;

        for (int i = 0; i < len1; i++) {

            double price    = Double.parseDouble(dbCVHandler.currentPrice(splitPAlerts[i]));
            double thPrice  = Double.parseDouble(dbPHandler.getPrice_Val(splitPAlerts[i]));
            int    check    = Integer.parseInt(dbPHandler.getThresh_Check(splitPAlerts[i]));
            int    set_hrs  = Integer.parseInt(dbCVHandler.currentHour(splitPAlerts[i]));
            long   millis   = System.currentTimeMillis();
            int    cur_hrs  = (int) (millis/1000/60/60);
            int    cur_mins = (int) (millis/1000/60);

            if ((thPrice < price && check == 1) || (thPrice > price && check == -1)) {
                dbPHelperMethod(splitPAlerts[i]);
                dbPAchHandler.addPriceAchAlert(splitPAlerts[i],price,thPrice,check,cur_mins);
            } else if (cur_hrs - set_hrs > deleteTimeHrs) {
                dbPHelperMethod(splitPAlerts[i]);
                dbPAchHandler.addPriceAchAlert(splitPAlerts[i],price,thPrice,100,cur_mins);
            }
        }

        String   volAlerts    = dbVHandler.dbToString();
        String[] splitVAlerts = volAlerts.split("[\n]");
        int len3              = numberVAlerts();

        if(len1 + len3 == 0) stopSelf();

        for (int j = 0; j < len3; j++) {

            double vol    = Double.parseDouble(dbCVHandler.currentVol(splitVAlerts[j]));
            double thVol  = Double.parseDouble(dbVHandler.getVol_Val(splitVAlerts[j]));
            int check2    = Integer.parseInt(dbVHandler.getThresh_Check(splitVAlerts[j]));
            int cur_mins2 = (int) ((System.currentTimeMillis())/1000/60);
            int cur_hrs2  = (int) (System.currentTimeMillis()/1000/60/60);
            int set_hrs2  = Integer.parseInt(dbCVHandler.currentHour(splitVAlerts[j]));

            if ((thVol < vol && check2 == 1) || (thVol > vol && check2 == -1)){
                dbVHelperMethod(splitVAlerts[j]);
                dbVAchHandler.addVolAchAlert(splitVAlerts[j], vol, thVol, check2, cur_mins2);
            }else if (cur_hrs2 - set_hrs2 > deleteTimeHrs){
                dbVHelperMethod(splitVAlerts[j]);
                dbVAchHandler.addVolAchAlert(splitVAlerts[j], vol, thVol, 100, cur_mins2);
            }
        }
    }

    int numberVAlerts(){
        String   volAlerts    = dbVHandler.dbToString();
        String[] splitVAlerts = volAlerts.split("[\n]");
        int lenVArray         = splitVAlerts.length;
        if (splitVAlerts[0].equals("")) lenVArray = 0;
        return lenVArray;
    }

    void dbPHelperMethod(String in){
        dbPHandler.deleteAlert(in);
        if(dbPAchHandler.alertExists(in)){
            dbPAchHandler.removePriceAchAlert(in);
            overwritten++;
        }
    }

    void dbVHelperMethod(String in){
        dbVHandler.deleteAlert(in);
        if(dbVAchHandler.alertExists(in)){
            dbVAchHandler.removeVolAchAlert(in);
            overwritten++;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = idUnique;
            String description = strTicker;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(idUnique, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}
