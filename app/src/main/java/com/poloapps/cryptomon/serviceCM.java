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
    String LC_url   = "https://api.coinmarketcap.com/v1/ticker/";
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
                        Toast.makeText(getApplicationContext(), "Service is running",
                                Toast.LENGTH_SHORT).show();
                        updateCurrentVals();
                        checkPriceAchieved();
                       //TODO returnNumberAlerts();

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

        dbPHandler    = new dbPriceHandler(this, null);
        dbCVHandler   = new dbCurrentValsHandler(this, null);
        dbPAchHandler = new dbPriceAlertsAchieved(this, null);

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

}
