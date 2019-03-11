package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AllAlertsActivity extends BaseActivity {

    ArrayList<HashMap<String, String>> AchievedList;
    ArrayList<HashMap<String, String>> AlertSetList;
    final DecimalFormat frmt  = new DecimalFormat("#,###,###,###,###.##");
    final DecimalFormat frmt0 = new DecimalFormat("#,###,###,###,###,###");
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
        mPublisherAdView = findViewById(R.id.all_alerts_adView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
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

        final Boolean Dollar = mSettings.getBoolean("Dollar", true);
        final String  Symb   = mSettings.getString("Curr_symb","€");

        final LayoutInflater li    = LayoutInflater.from(getApplicationContext());
        ImageButton DelAchAll      = findViewById(R.id.del_ach_all_btn);
        ImageButton DelSetAll      = findViewById(R.id.del_set_all_btn);
        TextView    achievedTopMsg = findViewById(R.id.achieved_top_msg);
        TextView    setTopMsg      = findViewById(R.id.set_top_msg);

        String   priceAlerts  = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int      lenPArray    = splitPAlerts.length;
        String   volAlerts    = dbVHandler.dbToString();
        String[] splitVAlerts = volAlerts.split("[\n]");
        int      lenVArray    = splitVAlerts.length;

        AlertSetList      = new ArrayList<>();
        ListView Set_lv   = findViewById(R.id.alertSet_listView);

        if (splitPAlerts[0].equals("")) lenPArray = 0;
        if (splitVAlerts[0].equals("")) lenVArray = 0;

        if(lenPArray == 0 && lenVArray == 0){
            setTopMsg.setText(getString(R.string.no_alerts_set));
            DelSetAll.setVisibility(View.INVISIBLE);
        }
        else {
            DelSetAll.setVisibility(View.VISIBLE);
            String topMsg = getString(R.string.running);
            if(lenPArray+lenVArray == 1){
                topMsg = getString(R.string.running1);
            }
            String msg = String.format(Locale.US, "%d",
                    lenPArray+lenVArray) + " " +  topMsg;
            setTopMsg.setText(msg);
        }

        for (int i = 0;i < lenPArray;i++){
            String id_set    = splitPAlerts[i];
            String cur_val   = dbCVHandler.currentPrice(splitPAlerts[i]);
            String thr_val   = dbPHandler.getPrice_Val(splitPAlerts[i]);
            String type       = "Price";
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

            AlertSetList.add(s_item);
        }
        for (int j = 0; j < lenVArray; j++) {
            String id_set    = splitVAlerts[j];
            String cur_val   = dbCVHandler.currentVol(splitVAlerts[j]);
            String thr_val   = dbVHandler.getVol_Val(splitVAlerts[j]);
            String type      = "24h Volume";
            int    set_hrs   = Integer.parseInt(dbCVHandler.currentHour(splitVAlerts[j]));
            long   millis    = System.currentTimeMillis();
            int    cur_hrs   = (int) (millis/1000/60/60);
            String setHrs    = Integer.toString(set_hrs - cur_hrs);

            HashMap<String, String> s_item = new HashMap<>();
            s_item.put("id_set" , id_set);
            s_item.put("cur_val", cur_val);
            s_item.put("thr_val", thr_val);
            s_item.put("type"   , type);
            s_item.put("set_hrs", setHrs);
            AlertSetList.add(s_item);
        }

        String[] fr1 = {"id_set", "cur_val", "thr_val", "type", "set_hrs" };
        int[]    to1 = {R.id.set_alert_name, R.id.set_current_val, R.id.set_threshold_val,
                R.id.set_alert_type, R.id.set_updated_val};

        ListAdapter setAdapter = new SimpleAdapter(getApplicationContext(), AlertSetList,
                R.layout.set_list_item, fr1, to1) {

            @Override
            public View getView(int position, View cnvrtView, ViewGroup parent){
                DecimalFormat thresh = frmt;
                DecimalFormat curr   = frmt;
                View view = super.getView(position, cnvrtView, parent);

                ImageButton delButton = view.findViewById(R.id.del_set_btn);
                final Map<String, String> currentRow = AlertSetList.get(position);
                TextView dispStale    = view.findViewById(R.id.set_updated_val);
                TextView txtCurrent   = view.findViewById(R.id.set_current_txt);
                TextView setThreshVal = view.findViewById(R.id.set_threshold_val);
                TextView setCurrVal   = view.findViewById(R.id.set_current_val);
                ImageButton CS_sel    = view.findViewById(R.id.set_cs_btn);
                final String   type   = currentRow.get("type");

                double valTh   = Double.parseDouble(setThreshVal.getText().toString());
                double valCr   = Double.parseDouble(setCurrVal.getText().toString());

                if      (valTh < .01)  thresh = frmt2;
                else if (valTh >= 100) thresh = frmt0;

                if      (valCr < .01)  curr = frmt2;
                else if (valCr >= 100) curr = frmt0;

                String disp_curr_symb = "$";
                if (!Dollar) disp_curr_symb = Symb;
                String dispThr = disp_curr_symb + thresh.format(valTh);
                String dispCur = disp_curr_symb + curr.format(valCr);
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
                CS_sel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startCSActivity(currentRow.get("id_set"));
                    }
                });
                dispStale.append("h");
                delButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(type.equals("Price"))
                            dbPHandler.deleteAlert(currentRow.get("id_set"));
                        else if(type.equals("24h Volume"))
                            dbVHandler.deleteAlert(currentRow.get("id_set"));
                        restart();
                    }
                });
                return view;
            }
        };
        Set_lv.setAdapter(setAdapter);

        String priceAchAlrts      = dbPAchHandler.dbEntries();
        String[] splitPAchAlrts   = priceAchAlrts.split("[\n]");
        int len2                  = splitPAchAlrts.length;
        String   volAchAlerts     = dbVAchHandler.dbEntries();
        String[] splitVAchAlerts  = volAchAlerts.split("[\n]");
        int len3                  = splitVAchAlerts.length;
        String topMsg             = getString(R.string.achieved);

        if(splitPAchAlrts[0].equals("")) len2 = 0;
        if(splitVAchAlerts[0].equals("")) len3 = 0;
        if(len2 == 0 && len3 == 0) {
            achievedTopMsg.setText(getString(R.string.no_achieved_alerts));
            DelAchAll.setVisibility(View.INVISIBLE);
        }else {
            DelAchAll.setVisibility(View.VISIBLE);
            if(len2 + len3 == 1) topMsg = getString(R.string.achieved1);
            String msg = String.format(Locale.US, "%d", len2 + len3) + " " + topMsg;
            achievedTopMsg.setText(msg);
        }
        editor.putInt("disp_alerts",   len2+len3);
        editor.putBoolean("aa_active", true);
        editor.apply();

        AchievedList         = new ArrayList<>();
        ListView achLV       = findViewById(R.id.alertAch_listView);

        for (int i = 0;i < len2;i++) {
            String threshVal = dbPAchHandler.getThresh_Val(splitPAchAlrts[i]);
            String threshBrk = dbPAchHandler.getThresh_Brk(splitPAchAlrts[i]);
            String check     = dbPAchHandler.getColumnBreakerChck(splitPAchAlrts[i]);
            String min_ach   = dbPAchHandler.getAchievedTimeStamp(splitPAchAlrts[i]);
            String ach_symb  = dbPAchHandler.getAchievedMonCurrency(splitPAchAlrts[i]);
            String type1     = "Price";
            String top_msg   = getString(R.string.alert_achieved);

            if (check.equals("100")) {
                type1 = "";
                top_msg = getString(R.string.data_not_available);
                threshBrk = "N/A";
            }

            HashMap<String, String> item = new HashMap<>();
            item.put("id",       splitPAchAlrts[i]);
            item.put("msg",      top_msg);
            item.put("check",    check);
            item.put("type",     type1);
            item.put("thresh",   threshVal);
            item.put("min_ach",  min_ach);
            item.put("breaker",  threshBrk);
            item.put("mon_curr", ach_symb);

            AchievedList.add(item);
        }
        for (int ij = 0;ij < len3;ij++){

            String id        = splitVAchAlerts[ij];
            String threshVal = dbVAchHandler.getThreshVal(splitVAchAlerts[ij]);
            String threshBrk = dbVAchHandler.getThreshBrk(splitVAchAlerts[ij]);
            String check     = dbVAchHandler.getColumnBreakerChck(splitVAchAlerts[ij]);
            String min_ach   = dbVAchHandler.getAchievedTimeStamp(splitVAchAlerts[ij]);
            String ach_symb  = dbVAchHandler.getAchievedMonCurrency(splitVAchAlerts[ij]);
            String type1     = "24h Volume";
            String top_msg   = getString(R.string.alert_achieved);

            if (check.equals("100")) {
                type1 = "";
                top_msg = getString(R.string.data_not_available);
                threshBrk = "N/A";
            }
            HashMap<String, String> item = new HashMap<>();
            item.put("id" ,      id);
            item.put("msg",      top_msg);
            item.put("check",    check);
            item.put("type",     type1);
            item.put("thresh",   threshVal);
            item.put("min_ach",  min_ach);
            item.put("breaker",  threshBrk);
            item.put("mon_curr", ach_symb);

            AchievedList.add(item);
        }

        String[] from = {"id", "type", "msg", "thresh", "breaker", "min_ach"};
        int[]    to   = {R.id.ach_alert_name, R.id.ach_alert_type, R.id.price_ach_msg,
                R.id.ach_thresh_val, R.id.ach_threshold_brk, R.id.ach_time_stamp};

            ListAdapter listAdapter2 = new SimpleAdapter(getApplicationContext(), AchievedList,
                    R.layout.achieved_list_item, from, to) {

                @Override
                public View getView(int position, View cnvrtView, ViewGroup parent) {

                    View view = super.getView(position, cnvrtView, parent);

                    TextView Time2        = view.findViewById(R.id.ach_time_stamp);
                    TextView valT         = view.findViewById(R.id.ach_thresh_val);
                    TextView valB         = view.findViewById(R.id.ach_threshold_brk);
                    ImageButton CMC_link  = view.findViewById(R.id.cmc_ach_btn);
                    ImageButton CS_sel    = view.findViewById(R.id.csel_ach_btn);
                    ImageView checkIcon   = view.findViewById(R.id.ach_icon_specifier);
                    ImageButton delButton = view.findViewById(R.id.del_ach_btn);

                    final Map<String, String> currentRow = AchievedList.get(position);
                    String check      = currentRow.get("check");
                    final String type = currentRow.get("type");
                    long tStamp       = Long.parseLong(currentRow.get("min_ach"));
                    Date date         = new java.util.Date(tStamp * 60 * 1000L);

                    String reqTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
                    String reqDay  = DateFormat.getDateInstance().format(date);
                    String achTime = reqDay + " " + reqTime;
                    Time2.setText(achTime);

                    double threshVal = Double.parseDouble(currentRow.get("thresh"));
                    String valThr = currentRow.get("mon_curr") + frmt.format(threshVal);
                    valT.setText(valThr);

                    if (!check.equals("100")) {
                        double breakVal = Double.parseDouble(currentRow.get("breaker"));
                        String valBrk = currentRow.get("mon_curr") + frmt.format(breakVal);
                        valB.setText(valBrk);

                        if (check.equals("1")) {
                            checkIcon.setBackground(getDrawable(R.drawable.ic_action_surpass));
                            valB.setTextColor(GREEN);
                        } else {
                            checkIcon.setBackground(getDrawable(R.drawable.ic_action_fall_below));
                            valB.setTextColor(RED);
                        }
                    } else {
                        checkIcon.setBackground(getDrawable(R.drawable.ic_action_not_available));
                        CS_sel.setEnabled(false);
                    }

                    CMC_link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            @SuppressLint({"InflateParams", "ViewHolder"})
                            final View CMC_linkMenu = li.inflate(R.layout.cmc_link_menu, null);
                            final AlertDialog.Builder builder2 = new AlertDialog.Builder(
                                    AllAlertsActivity.this);
                            builder2.setView(CMC_linkMenu);
                            final AlertDialog dialog2 = builder2.create();
                            dialog2.show();

                            TextView Link = CMC_linkMenu.findViewById(R.id.cmc_link_id);
                            Link.setText(currentRow.get("id"));
                            Button OK = CMC_linkMenu.findViewById(R.id.cmc_OK_btn);
                            Button NO = CMC_linkMenu.findViewById(R.id.cmc_NO_btn);
                            final String CMC_url = getString(R.string.cryptos_display_link)
                                    + currentRow.get("id") + "/";
                            OK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(CMC_url);
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
                            if (type.equals("Price"))
                                dbPAchHandler.removePriceAchAlert(currentRow.get("id"));
                            else if (type.equals("24h Volume"))
                                dbVAchHandler.removeVolAchAlert(currentRow.get("id"));
                            restart();
                        }
                    });
                    CS_sel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startCSActivity(currentRow.get("id"));
                        }
                    });
                    return view;
                }
            };
            achLV.setAdapter(listAdapter2);

        DelSetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(AllAlertsActivity.this);
                @SuppressLint("InflateParams")
                View mView = getLayoutInflater()
                        .inflate(R.layout.confirm_del_set_alerts_menu,null);
                builder.setView(mView);

                final AlertDialog dialog = builder.create();
                final Button cancel_btn = mView.findViewById(R.id.del_set_alerts_NO_btn);
                final Button ok_btn = mView.findViewById(R.id.del_set_alerts_OK_btn);

                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }});
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbPHandler.deleteAll();
                        dbVHandler.deleteAll();
                        restart();
                    }});
                dialog.show();
            }
        });

        DelAchAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(AllAlertsActivity.this);
                @SuppressLint("InflateParams")
                View mView = getLayoutInflater()
                        .inflate(R.layout.confirm_del_ach_alerts_menu,null);
                builder.setView(mView);

                final AlertDialog dialog = builder.create();
                final Button cancel_btn = mView.findViewById(R.id.del_ach_alerts_NO_btn);
                final Button ok_btn = mView.findViewById(R.id.del_ach_alerts_OK_btn);

                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }});
                ok_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbPAchHandler.deleteAll();
                        dbVAchHandler.deleteAll();
                        restart();
                    }});
                dialog.show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        editor.putBoolean("aa_active", false);
        editor.apply();

        boolean csActive   = mSettings.getBoolean("cs_active", false);
        boolean t100Active = mSettings.getBoolean("t100_active", false);
        boolean restart    = getIntent().getBooleanExtra("restart", false);
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
    public void startCSActivity(String in){
        Intent intent = new Intent(AllAlertsActivity.this,CryptoSelectActivity.class);
        intent.putExtra("crypto_id", in);
        intent.putExtra("restart", false);
        AllAlertsActivity.this.startActivity(intent);
    }
}
