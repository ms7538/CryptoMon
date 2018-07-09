package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AllAlertsActivity extends BaseActivity {

    ArrayList<HashMap<String, String>> PriceAchievedList;
    ArrayList<HashMap<String, String>> PriceSetList;
    final DecimalFormat frmt  = new DecimalFormat("#,###,###,###,###.##");
    final DecimalFormat frmt0 = new DecimalFormat("#,###,###,###,###");
    final DecimalFormat frmt2 = new DecimalFormat("#.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alerts);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                                                                              R.color.dark_gray)));
        AdView mAdView      = findViewById(R.id.all_alerts_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        updateCurrentVals();
    }

    @Override
    public void onResume() {
        super.onResume();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        getIntent().removeExtra("restart");
        overwritten = 0;
        stopRunningService();
        final int YELLOW  = ContextCompat.getColor(getApplicationContext(),(R.color.bright_yellow));
        final int RED     = ContextCompat.getColor(getApplicationContext(),(R.color.red));
        final int GREEN   = ContextCompat.getColor(getApplicationContext(),(R.color.green2));
        final LayoutInflater li    = LayoutInflater.from(getApplicationContext());

        TextView     achievedTopMsg = findViewById(R.id.achieved_top_msg);
        TextView     setTopMsg      = findViewById(R.id.set_top_msg);

        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int len1              = splitPAlerts.length;
        PriceSetList          = new ArrayList<>();
        ListView priceSet_lv  = findViewById(R.id.priceSet_listView);
        String type           = "Price";
        if(splitPAlerts[0].equals("")) len1 = 0;
        if(len1 == 0) setTopMsg.setText(getString(R.string.no_alerts_set));
        else {
            String msg = String.format(Locale.US, "%d", len1) + " " +
                                                                        getString(R.string.running);
            setTopMsg.setText(msg);
        }
        for (int i = 0;i < len1;i++){
            String id_set    = splitPAlerts[i];
            String cur_val   = dbCVHandler.currentPrice(splitPAlerts[i]);
            String thr_val   = dbPHandler.getPrice_Val(splitPAlerts[i]);

            int    set_hrs   = Integer.parseInt(dbCVHandler.currentHour(splitPAlerts[i]));
            long   millis    = System.currentTimeMillis();
            int    cur_hrs   = (int) (millis/1000/60/60);

            String setHrs  = Integer.toString(set_hrs - cur_hrs);

            HashMap<String, String> s_item = new HashMap<>();
            s_item.put("id_set" , id_set);
            s_item.put("cur_val", cur_val);
            s_item.put("thr_val", thr_val);
            s_item.put("type"   , type);
            s_item.put("set_hrs", setHrs);

            PriceSetList.add(s_item);
        }
        String[] fr1 = {"id_set", "cur_val", "thr_val", "type", "set_hrs" };
        int[]    to1 = {R.id.set_alert_name, R.id.set_current_val, R.id.set_threshold_val,
                R.id.set_alert_type, R.id.set_updated_val};

        ListAdapter setAdapter = new SimpleAdapter(getApplicationContext(), PriceSetList,
                R.layout.set_list_item, fr1, to1) {

            @Override
            public View getView(int position, View cnvrtView, ViewGroup parent){
                DecimalFormat thresh = frmt;
                DecimalFormat curr   = frmt;
                View view = super.getView(position, cnvrtView, parent);

                Button delButton      = view.findViewById(R.id.del_set_btn);
                final Map<String, String> currentRow = PriceSetList.get(position);
                TextView dispStale    = view.findViewById(R.id.set_updated_val);
                TextView txtCurrent   = view.findViewById(R.id.set_current_txt);
                TextView setThreshVal = view.findViewById(R.id.set_threshold_val);
                TextView setCurrVal   = view.findViewById(R.id.set_current_val);

                double valTh   = Double.parseDouble(setThreshVal.getText().toString());
                double valCr   = Double.parseDouble(setCurrVal.getText().toString());

                if      (valTh < .01)  thresh = frmt2;
                else if (valTh >= 100) thresh = frmt0;
                else                   thresh = frmt;

                if      (valCr < .01)  curr = frmt2;
                else if (valCr >= 100) curr = frmt0;
                else                   curr = frmt;

                String dispThr = "$"  + thresh.format(valTh);
                String dispCur = "$"  + curr.format(valCr);
                setThreshVal.setText(dispThr);
                setCurrVal.setText(dispCur);

                String hrsVal = dispStale.getText().toString();

                int hrs = Integer.parseInt(hrsVal);

                if (hrs < 2){
                    dispStale.setTextColor(GREEN);
                    txtCurrent.setTextColor(GREEN);

                }else if (hrs < 6){
                    dispStale.setTextColor(YELLOW);
                    txtCurrent.setTextColor(YELLOW);
                }else{
                    dispStale.setTextColor(RED);
                    txtCurrent.setTextColor(RED);
                }

                dispStale.append( "h");
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbPHandler.deleteAlert(currentRow.get("id_set"));
                        restart();
                    }
                });
                return view;
            }
        };
        priceSet_lv.setAdapter(setAdapter);

        String priceAchAlrts    = dbPAchHandler.dbToString();
        String[] splitPAchAlrts = priceAchAlrts.split("[\n]");
        int len2                = splitPAchAlrts.length;
        if(splitPAchAlrts[0].equals("")) len2 = 0;

        if(len2 == 0) {
            achievedTopMsg.setText(getString(R.string.no_achieved_alerts));
        }else {
            String msg = String.format(Locale.US, "%d", len2) + " "
                                                                     + getString(R.string.achieved);
            achievedTopMsg.setText(msg);
        }
        editor.putInt("disp_price_alerts", len2);
        editor.putBoolean("aa_active"    , true);
        editor.apply();

        PriceAchievedList    = new ArrayList<>();
        ListView priceAch_lv = findViewById(R.id.priceAch_listView);

        for (int j = 0;j < len2;j++){

            String id        = splitPAchAlrts[j];
            String threshVal = dbPAchHandler.getThresh_Val(splitPAchAlrts[j]);
            String threshBrk = dbPAchHandler.getThresh_Brk(splitPAchAlrts[j]);
            String check     = dbPAchHandler.getColumnBreakerChck(splitPAchAlrts[j]);
            String min_ach   = dbPAchHandler.getAchievedTimeStamp(splitPAchAlrts[j]);
            String type1     = "Price";
            String top_msg   = getString(R.string.alert_achieved);

            if (check.equals("100")) {
                type1 = "";
                top_msg = getString(R.string.data_not_available);
                threshBrk = "N/A";
            }

            HashMap<String, String> item = new HashMap<>();
            item.put("id" ,    id);
            item.put("msg",    top_msg);
            item.put("check",  check);
            item.put("type",   type1);
            item.put("thresh", threshVal);
            item.put("min_ach",min_ach);
            item.put("breaker",threshBrk);

            PriceAchievedList.add(item);
        }
        String[] from = {"id", "type", "msg", "thresh", "breaker", "min_ach"};
        int[]    to   = {R.id.ach_alert_name, R.id.ach_alert_type, R.id.price_ach_msg,
                R.id.ach_thresh_val, R.id.ach_threshold_brk, R.id.ach_time_stamp};

        ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(), PriceAchievedList,
                R.layout.achieved_list_item, from, to) {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public View getView(int position, View cnvrtView, ViewGroup parent){

                View view = super.getView(position, cnvrtView, parent);

                TextView Time2        = view.findViewById(R.id.ach_time_stamp);
                TextView valT         = view.findViewById(R.id.ach_thresh_val);
                TextView valB         = view.findViewById(R.id.ach_threshold_brk);
                ImageButton CMC_link  = view.findViewById(R.id.cmc_ach_btn);
                ImageButton CS_sel    = view.findViewById(R.id.csel_ach_btn);
                ImageView checkIcon   = view.findViewById(R.id.ach_icon_specifier);
                ImageButton delButton = view.findViewById(R.id.del_ach_btn);

                final Map<String, String> currentRow = PriceAchievedList.get(position);
                String check     = currentRow.get("check");
                long tStamp      = Long.parseLong(currentRow.get("min_ach"));
                Date date        = new java.util.Date(tStamp*60*1000L);
                String reqTime   = DateFormat.getDateTimeInstance().format(date);
                Time2.setText(reqTime);
                double threshVal        = Double.parseDouble(currentRow.get("thresh"));
                String valThr = "$"  + frmt.format(threshVal);
                valT.setText(valThr);

                if (!check.equals("100")) {
                    double breakVal         = Double.parseDouble(currentRow.get("breaker"));
                    String valBrk = "$"  + frmt.format(breakVal);
                    valB.setText(valBrk);

                    if(check.equals("1")) {
                        checkIcon.setBackground(getDrawable(R.drawable.ic_action_surpass));
                        valB.setTextColor(GREEN);
                    } else {
                        checkIcon.setBackground(getDrawable(R.drawable.ic_action_fall_below));
                        valB.setTextColor(RED);
                    }

                } else checkIcon.setBackground(getDrawable(R.drawable.ic_action_not_available));

                CMC_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        @SuppressLint({"InflateParams", "ViewHolder"})
                        final View CMC_linkMenu = li.inflate(R.layout.cmc_link_menu ,null);
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(
                                AllAlertsActivity.this);
                        builder2.setView(CMC_linkMenu);
                        final AlertDialog dialog2  = builder2.create();
                        dialog2.show();

                        TextView Link = CMC_linkMenu.findViewById(R.id.cmc_link_id);
                        Link.setText(currentRow.get("id"));
                        Button OK      = CMC_linkMenu.findViewById(R.id.cmc_OK_btn);
                        Button NO      = CMC_linkMenu.findViewById(R.id.cmc_NO_btn);
                        final String CMC_url = getString(R.string.cryptos_display_link)
                                                                    + currentRow.get("id")+ "/";
                        OK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri       = Uri.parse(CMC_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                dialog2.dismiss();
                                startActivity(intent);
                            }
                        });
                        NO.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });
                    }
                });
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbPAchHandler.removePAAlert(currentRow.get("id"));
                        restart();
                    }
                });

                CS_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                AllAlertsActivity.this,
                                CryptoSelectActivity.class);
                        intent.putExtra("crypto_id", currentRow.get("id"));
                        intent.putExtra("restart", false);
                        AllAlertsActivity.this.startActivity(intent);
                    }
                });

                return view;
            }
        };
        priceAch_lv.setAdapter(listAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        editor.putBoolean("aa_active", false);
        editor.apply();

        Boolean csActive   = mSettings.getBoolean("cs_active", false);
        Boolean t100Active = mSettings.getBoolean("t100_active", false);
        Boolean restart    = getIntent().getBooleanExtra("restart", false);
        getIntent().removeExtra("restart");

        if(!csActive && !t100Active && !restart ) checkStartService();
    }
    @Override
    protected void onStart() {
        super.onStart();
        stopRunningService();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        editor.putBoolean("aa_active", true);
        editor.apply();
    }

    @Override
    public void onPause(){
        super.onPause();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("aa_active", false);
        editor.apply();
    }
}
