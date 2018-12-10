package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

//v1.216    created
public class T100Activity extends BaseActivity {

    long createdTime = System.currentTimeMillis() / 1000L;
    ArrayList<HashMap<String, String>> rankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        setContentView(R.layout.activity_top_100);
        final TextView Time2 = findViewById(R.id.t100_request_time);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                                                                              R.color.dark_gray)));

        String reqCurrentTime = DateFormat.getDateTimeInstance().format(new Date());
        Time2.setText(reqCurrentTime);

        mPublisherAdView = findViewById(R.id.adView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        Boolean Dollar = mSettings.getBoolean("Dollar", true);

        final String  Curr   = mSettings.getString("Curr_code","eur");
        final Integer RED    = ContextCompat.getColor(getApplicationContext(), (R.color.red));

        LC_url = "https://api.coinmarketcap.com/v1/ticker/";
        if(!Dollar) LC_url = LC_url + "?convert=" + Curr;

        final DecimalFormat form  = new DecimalFormat("#,###,###,###.##");
        final DecimalFormat form2 = new DecimalFormat("#.######");
        final DecimalFormat form3 = new DecimalFormat("#,###,###,###");

        StringRequest crypto100_request = new StringRequest(LC_url,
                new Response.Listener<String>() {

            @Override
            public void onResponse(String string) {

                try {

                    String price_key      = "price_usd";
                    String curr_symbol    = "$";
                    String Vol24h_key     = "24h_volume_usd";
                    Boolean Dollar = mSettings.getBoolean("Dollar", true);
                    if(!Dollar){
                        price_key      = "price_" + Curr;
                        curr_symbol    = mSettings.getString("Curr_symb","€");
                        Vol24h_key     = "24h_volume_" + Curr;
                    }
                    rankList = new ArrayList<>();
                    ListView lv = findViewById(R.id.t100_listView);

                    JSONArray T100_Array = new JSONArray(string);

                    for (int i = 0; i < T100_Array.length(); i++) {

                        JSONObject obj1 = T100_Array.getJSONObject(i);

                        String rate       = obj1.getString(price_key);
                        Double d_rate     = Double.parseDouble(rate);
                        Double curr_vol   = Double.parseDouble(obj1.getString(Vol24h_key));
                        String volume_24h = curr_symbol + form3.format(curr_vol);

                        Double PriceVal  = Double.parseDouble(obj1.getString(price_key));
                        Double VolVal    = Double.parseDouble(obj1.getString(Vol24h_key));

                        if (d_rate < .01) rate  = curr_symbol + form2.format(d_rate);
                        else              rate  = curr_symbol + form.format(d_rate);

                        String name       = obj1.getString("name");
                        String symbol     = obj1.getString("symbol");

                        name              = name + " / " + symbol;
                        String rank       = obj1.getString("rank");
                        String delta_1h   = obj1.getString("percent_change_1h");
                        String delta_1d   = obj1.getString("percent_change_24h");
                        String delta_7d   = obj1.getString("percent_change_7d");
                        String link_id    = obj1.getString("id");
                        long millis       = System.currentTimeMillis();
                        Integer hours     = (int) (millis/1000/60/60);

                        dbCVHandler.deleteEntry(link_id);
                        dbCVHandler.addCurrentVals(link_id, PriceVal, VolVal, hours);

                        HashMap<String, String> item = new HashMap<>();
                        item.put("rank",    rank);
                        item.put("name",    name);
                        item.put("rate",    rate);
                        item.put("d1h",     delta_1h);
                        item.put("d1d",     delta_1d);
                        item.put("d7_d",    delta_7d);
                        item.put("24h_vol", volume_24h);
                        item.put("id_link", link_id);
                        rankList.add(item);
                    }

                    String[] from = {"rank","name","rate","d1h",
                                     "d1d","d7_d","24h_vol","id_link"};
                    int[] to = {R.id.list_rank, R.id.list_name, R.id.list_rate,R.id.h1,R.id.d1,
                                R.id.delta7_d,R.id.list_24h_volume,R.id.id_link};

                    ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(), rankList,
                                              R.layout.t100_list_item, from, to) {

                        @Override
                        public View getView(int position, View cnvrtView, ViewGroup parent){

                            View view = super.getView(position, cnvrtView, parent);

                            TextView delta_1h = view.findViewById(R.id.h1);
                            TextView delta_1d = view.findViewById(R.id.d1);
                            TextView delta_7d = view.findViewById(R.id.delta7_d);


                            final TextView link_id = view.findViewById(R.id.id_link);
                            link_id.setPaintFlags(link_id.getPaintFlags()
                                    | Paint.UNDERLINE_TEXT_FLAG);

                            Map<String, String> currentRow = rankList.get(position);

                            double delta1h = 0;
                            if (!Objects.equals(currentRow.get("d1h"), "null")) {
                                delta1h = Double.parseDouble(currentRow.get("d1h"));
                            }
                            if (delta1h < 0) delta_1h.setTextColor(RED);
                            else if (delta1h > 0) delta_1h.setTextColor(Color.GREEN);

                            double delta1d = 0;
                            if (!Objects.equals(currentRow.get("d1d"), "null")) {
                                delta1d = Double.parseDouble(currentRow.get("d1d"));
                            }
                            if (delta1d < 0) delta_1d.setTextColor(RED);
                            else if (delta1d > 0) delta_1d.setTextColor(Color.GREEN);

                            double delta7d = 0;
                            if (!Objects.equals(currentRow.get("d7_d"), "null")) {
                                delta7d = Double.parseDouble(currentRow.get("d7_d"));
                            }
                            if (delta7d < 0) delta_7d.setTextColor(RED);
                            else if (delta7d > 0) delta_7d.setTextColor(Color.GREEN);

                            link_id.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           Intent intent = new Intent(
                                                   T100Activity.this,
                                                   CryptoSelectActivity.class);
                                           intent.putExtra("crypto_id",  link_id.getText());
                                           intent.putExtra("restart", false);
                                           T100Activity.this.startActivity(intent);
                                       }
                                   });

                            return view;
                        }
                    };
                    lv.setAdapter(listAdapter);
                    dialog.dismiss();
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }
            }, new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError volleyError) {
               Toast.makeText(getApplicationContext(), "Some error occurred!!",
                       Toast.LENGTH_SHORT).show();
               dialog.dismiss();
           }
       });
        RequestQueue rQueue = Volley.newRequestQueue(T100Activity.this);
        rQueue.add(crypto100_request);
        createdTime = System.currentTimeMillis() / 1000L;
    }

    @Override
    protected void onStop() {
        super.onStop();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        Boolean csActive   = mSettings.getBoolean("cs_active", false);
        Boolean aaActive   = mSettings.getBoolean("aa_active", false);
        Boolean t100Active = mSettings.getBoolean("t100_active", true);
        Boolean restart    = getIntent().getBooleanExtra("restart", false);
        getIntent().removeExtra("restart");

        if(!csActive && !aaActive && !t100Active && !restart) checkStartService();
        editor.putBoolean("t100_active", false);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("t100_active", true);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences mSettings     = this.getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = mSettings.edit();
        Boolean Dollar = mSettings.getBoolean("Dollar", true);
        String  Curr   = mSettings.getString("Curr_code","eur");

        editor.putBoolean("t100_active", false);
        String T100_currency = "usd";
        if(!Dollar) T100_currency = Curr;
        editor.putString("t100_curr",T100_currency);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences mSettings     = this.getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = mSettings.edit();
        getIntent().removeExtra("restart");
        Boolean Dollar                  = mSettings.getBoolean("Dollar", true);
        String  Curr                    = mSettings.getString("Curr_code","eur");
        String  T100                    = mSettings.getString("t100_curr","usd");
        stopRunningService();
        editor.putBoolean("t100_active", true);
        editor.apply();

        Long resumeTime             = System.currentTimeMillis() / 1000L;
        if (resumeTime - createdTime > 299) restart();
        String currency_check = "usd";
        if(!Dollar) currency_check = Curr;
        if (!Objects.equals(T100, currency_check)) restart();
    }
}
