package com.poloapps.cryptomon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;


public class BootUpReceiver extends BroadcastReceiver {
    dbPriceHandler dbPHandler;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action         = intent.getAction();
        dbPHandler            = new dbPriceHandler(context, null);
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");

        assert action != null;
        if(action.contains(".action.BOOT_COMPLETED")) {

            if (!splitPAlerts[0].equals("")) {

                Toast.makeText(context, "boot start service",
                        Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(context, serviceCM.class);
                if (Build.VERSION.SDK_INT < 26) {
                    context.startService(myIntent);
                }else {
                    ContextCompat.startForegroundService(context, myIntent);
                }
            }
        }


    }
}