package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class All_AlertsActivity extends BaseActivity {
    dbPriceHandler  dbPHandler;
    dbVolumeHandler dbVHandler;
    dbCurrentValsHandler dbCVHandler;
    dbPriceAlertsAchieved dbPAchHandler;
    StringBuilder PAlertArray   = new StringBuilder();
    StringBuilder PAchAlertArray = new StringBuilder();
    String LC_url       = "https://api.coinmarketcap.com/v1/ticker/";
    ProgressDialog dialog;


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
        PAchAlertArray.setLength(0);
        dbPHandler    = new dbPriceHandler(this, null);
        dbVHandler    = new dbVolumeHandler(this, null);
        dbCVHandler   = new dbCurrentValsHandler(this, null);
        dbPAchHandler = new dbPriceAlertsAchieved(this, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        PAlertArray.setLength(0);
        PAchAlertArray.setLength(0);
        final TextView tv1    = findViewById(R.id.tv1);
        final TextView tv2    = findViewById(R.id.tv2);
        final TextView tv3    = findViewById(R.id.tv3);

        //TODO implement dbCurrentVals refresh


        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1 = splitPAlerts.length;
        if(splitPAlerts[0].equals("")) len1 = 0;
        int i = 0;
        for (i = 0;i < len1;i++){
            //for reach line: query dbP for check, Thresh Val; query dbCV for current price.
            // if th > curr then check = 1 | if th < curr then check = -1
            // alarm if th < curr && check = 1 | th > curr && check = -1
            PAlertArray.setLength(0);

            double price   = Double.parseDouble(dbCVHandler.currentPrice(splitPAlerts[i]));
            double thPrice = Double.parseDouble(dbPHandler.getPrice_Val(splitPAlerts[i]));
            int check      = Integer.parseInt(dbPHandler.getThresh_Check(splitPAlerts[i]));

            if((thPrice < price && check == 1) || (thPrice > price && check == -1)){
                dbPHandler.deleteAlert(splitPAlerts[i]);
                dbPAchHandler.removePAAlert(splitPAlerts[i]);
                dbPAchHandler.addPriceAchAlert(splitPAlerts[i],price,thPrice);
                //TODO add check description
            } else {
                PAlertArray.append(splitPAlerts[i]);
                PAlertArray.append(":");
                PAlertArray.append(dbPHandler.getPrice_Val(splitPAlerts[i]));
                PAlertArray.append(":");
                PAlertArray.append(dbPHandler.getThresh_Check(splitPAlerts[i]));
                PAlertArray.append("-c->");
                PAlertArray.append(dbCVHandler.currentPrice(splitPAlerts[i]));
                PAlertArray.append("\n");
            }
            tv1.append(PAlertArray);
        }

        String priceAchAlrts    = dbPAchHandler.dbToString();
        String[] splitPAchAlrts = priceAchAlrts.split("[\n]");
        int len2                = splitPAchAlrts.length;

        if(splitPAchAlrts[0].equals("")) len2 = 0;
        PAchAlertArray.setLength(0);

        for (int j = 0;j < len2;j++){

            PAchAlertArray.append(splitPAchAlrts[j]);
            PAchAlertArray.append(" passed set threshold of ");
            PAchAlertArray.append(dbPAchHandler.getThresh_Val(splitPAchAlrts[j]));
            PAchAlertArray.append(" at ");
            PAchAlertArray.append(dbPAchHandler.getThresh_Brk(splitPAchAlrts[j]));
            PAchAlertArray.append("\n");
        }
        tv2.append(PAchAlertArray);

    }

    @Override
    public void onPause(){
        final TextView tv3 = findViewById(R.id.tv3);
        final TextView tv2 = findViewById(R.id.tv2);
        final TextView tv1 = findViewById(R.id.tv1);
        super.onPause();
        PAlertArray.setLength(0);
        PAchAlertArray.setLength(0);
        tv1.setText("");
        tv2.setText("");
        tv3.setText("");
    }
}
