package com.poloapps.cryptomon;

import android.app.IntentService;
import android.content.Intent;

import android.util.Log;


public class IntentServiceCM extends IntentService {

    private static final String TAG = "com.poloapps.cryptomon";

    public IntentServiceCM() {
        super("IntentServiceCM");
    }

   @Override
    protected void onHandleIntent(Intent intent){
        // service code here
       Log.i(TAG, "CM service has started");

   }
}
