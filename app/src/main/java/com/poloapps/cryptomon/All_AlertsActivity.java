package com.poloapps.cryptomon;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class All_AlertsActivity extends BaseActivity {
    dbPriceHandler  dbPHandler;
    dbVolumeHandler dbVHandler;
    dbCurrentValsHandler dbCVHandler;
    StringBuilder PAlertArray = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__alerts);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                R.color.dark_gray)));
        PAlertArray.setLength(0);
        dbPHandler = new dbPriceHandler(this, null);
        dbVHandler = new dbVolumeHandler(this, null);
        dbCVHandler = new dbCurrentValsHandler(this, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        PAlertArray.setLength(0);
        final TextView tv1 = findViewById(R.id.tv1);
        final TextView tv2 = findViewById(R.id.tv2);
        final TextView tv3 = findViewById(R.id.tv3);
        String priceAlerts = dbPHandler.dbToString();
        //tv1.setText(priceAlerts);
        //tv2.setText(dbVHandler.databaseToString());

        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1 = splitPAlerts.length;

        int i = 0;
        for (i = 0;i<len1;i++){
     //for reach line: query dbP for check, Thresh Val; query dbCV for current price.
            // if th > curr then check = 1 | if th < curr then check = -1
            // alarm if th < curr && check = 1 | th > curr && check = -1
            PAlertArray.setLength(0);
            PAlertArray.append(splitPAlerts[i]);
            PAlertArray.append(":");
            PAlertArray.append(dbPHandler.getPrice_Val(splitPAlerts[i]));
            PAlertArray.append(":");
            PAlertArray.append(dbPHandler.getThresh_Check(splitPAlerts[i]));
            PAlertArray.append("-c->");
            PAlertArray.append(dbCVHandler.currentPrice(splitPAlerts[i]));
            PAlertArray.append("Alert:");

            double price = Double.parseDouble(dbCVHandler.currentPrice(splitPAlerts[i]));
            double thPrice = Double.parseDouble(dbPHandler.getPrice_Val(splitPAlerts[i]));
            int check = Integer.parseInt(dbPHandler.getThresh_Check(splitPAlerts[i]));

            if((thPrice < price && check == 1) || (thPrice > price && check == -1)){
                PAlertArray.append("true");
            }else     PAlertArray.append("false");

            tv3.append(PAlertArray);
        }

        if(splitPAlerts[0].equals("")) len1 = 0;
        Toast.makeText(getApplicationContext(), Integer.toString(len1),
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onPause(){
        final TextView tv3 = findViewById(R.id.tv3);
        super.onPause();
        PAlertArray.setLength(0);
        tv3.setText("");
    }
}
