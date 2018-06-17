package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class All_AlertsActivity extends BaseActivity {

    ArrayList<HashMap<String, String>> PriceAchievedList;

    StringBuilder PAlertArray    = new StringBuilder();
    StringBuilder PAchAlertArray = new StringBuilder();

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
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCurrentVals();
        PAlertArray.setLength(0);
        PAchAlertArray.setLength(0);
        final TextView tv1    = findViewById(R.id.tv1);
        String checkDescript  = " surpassed ";
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1              = splitPAlerts.length;

        if(splitPAlerts[0].equals("")) len1 = 0;
        for (int i = 0;i < len1;i++){
            PAlertArray.setLength(0);
            PAlertArray.append(splitPAlerts[i]);
            PAlertArray.append(":");
            PAlertArray.append(dbPHandler.getPrice_Val(splitPAlerts[i]));
            PAlertArray.append(":");
            PAlertArray.append(dbPHandler.getThresh_Check(splitPAlerts[i]));
            PAlertArray.append("-c->");
            PAlertArray.append(dbCVHandler.currentPrice(splitPAlerts[i]));
            PAlertArray.append("\n");
            tv1.append(PAlertArray);
        }

        String priceAchAlrts    = dbPAchHandler.dbToString();
        String[] splitPAchAlrts = priceAchAlrts.split("[\n]");

        int len2                = splitPAchAlrts.length;
        if(splitPAchAlrts[0].equals("")) len2 = 0;

        PAchAlertArray.setLength(0);
        PriceAchievedList    = new ArrayList<>();
        ListView priceAch_lv = findViewById(R.id.priceAchievedAlert_listView);

        for (int j = 0;j < len2;j++){

            String id        = splitPAchAlrts[j];
            String threshVal = dbPAchHandler.getThresh_Val(splitPAchAlrts[j]);
            String threshBrk = dbPAchHandler.getThresh_Brk(splitPAchAlrts[j]);

            if(dbPAchHandler.getColumnBreakerChck(splitPAchAlrts[j]).equals("-1")) {
                checkDescript = " fell below ";
            }else
                checkDescript = " surpassed ";

            String disp_msg = id + checkDescript + threshVal + " at " + threshBrk;
            HashMap<String, String> item = new HashMap<>();

            item.put("id"    ,id);
            item.put("msg"   ,disp_msg);

            PriceAchievedList.add(item);
        }
        String[] from = {"msg","id"};
        int[] to = {R.id.price_ach_msg,R.id.dismiss_price_ach_alert};

        ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(), PriceAchievedList,
                R.layout.price_achieved_list_item, from, to) {

            @Override
            public View getView(int position, View cnvrtView, ViewGroup parent){

                View view = super.getView(position, cnvrtView, parent);

                Button linkButton   = view.findViewById(R.id.dismiss_price_ach_alert);
                final Map<String, String> currentRow = PriceAchievedList.get(position);

                linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbPAchHandler.removePAAlert(currentRow.get("id"));
                        restart();
                    }
                });

                dialog.dismiss();
                return view;
            }
        };
        priceAch_lv.setAdapter(listAdapter);

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
