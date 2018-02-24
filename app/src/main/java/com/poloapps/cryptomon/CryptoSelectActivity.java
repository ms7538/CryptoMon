package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import java.text.DecimalFormat;

public class CryptoSelectActivity extends AppCompatActivity {
    String Select_url1 = "https://api.coinmarketcap.com/v1/ticker/";
    String Select_url2 = "/?convert=EUR";
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_select);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    @Override
    public void onResume() {
        super.onResume();

        final TextView CMC_link = findViewById(R.id.sel_crypto_coinmarketcap_link);
        CMC_link.setPaintFlags(CMC_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String crypto_id = getIntent().getStringExtra("crypto_id");
        String Select_url = Select_url1 + crypto_id + Select_url2;
        final String CMC_url = getString(R.string.cryptos_display_link) + crypto_id + "/";

        final DecimalFormat frmt  = new DecimalFormat("#,###,###,###,###.##");
        final DecimalFormat frmt2 = new DecimalFormat("#.########");
        final DecimalFormat frmt3  = new DecimalFormat("#,###,###,###,###");

        final TextView Time          = findViewById(R.id.select_update_time);

        final TextView Name          = findViewById(R.id.select_name);
        final TextView Rank          = findViewById(R.id.select_rank);
        final TextView Symbol        = findViewById(R.id.select_symbol);

        final TextView PriceUSD      = findViewById(R.id.select_price_usd);
        final TextView PriceEUR      = findViewById(R.id.select_price_eur);
        final TextView PriceBTC      = findViewById(R.id.select_price_btc);

        final TextView AvailSupply   = findViewById(R.id.select_avail_supply);
        final TextView TotalSupply   = findViewById(R.id.select_total_supply);
        final TextView MaxSupply     = findViewById(R.id.select_max_supply);

        final TextView Delta1h       = findViewById(R.id.select_delta_1h);
        final TextView Delta1d       = findViewById(R.id.select_delta_24h);
        final TextView Delta7d       = findViewById(R.id.select_delta_7d);

        final TextView VolumeUSD     = findViewById(R.id.select_24h_vol_usd);
        final TextView VolumeEUR     = findViewById(R.id.select_24h_vol_eur);

        final TextView MarketCapUSD     = findViewById(R.id.select_market_cap_usd);
        final TextView MarketCapEUR     = findViewById(R.id.select_market_cap_eur);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest cryptoSel_request = new StringRequest(Select_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {

                        try {
                            JSONArray Selected = new JSONArray(string);
                            JSONObject object = Selected.getJSONObject(0);

                            String Sel_Name  = object.getString("name");
                            String Sel_Rank  = object.getString("rank");
                            String Sel_Symb  = object.getString("symbol");

                            Name.setText(Sel_Name);
                            Rank.setText(Sel_Rank);
                            Symbol.setText(Sel_Symb);

                            DecimalFormat USD_frmt = frmt;
                            DecimalFormat EUR_frmt = frmt;
                            DecimalFormat BTC_frmt = frmt;

                            double usdP = Double.parseDouble(object.getString("price_usd"));
                            double eurP = Double.parseDouble(object.getString("price_eur"));
                            double btcP = Double.parseDouble(object.getString("price_btc"));

                            if(usdP < 0.01) USD_frmt = frmt2;
                            if(eurP < 0.01) EUR_frmt = frmt2;
                            if(btcP < 0.01) BTC_frmt = frmt2;

                            String Price_USD = "$"      + USD_frmt.format(usdP);
                            String Price_EUR = "\u20AC" + EUR_frmt.format(eurP);
                            String Price_BTC = "\u0E3F" + BTC_frmt.format(btcP);

                            PriceUSD.setText(Price_USD);
                            PriceEUR.setText(Price_EUR);
                            PriceBTC.setText(Price_BTC);


                            String Delta_1h = "Not Available";
                            String Delta_1h_val = object.getString("percent_change_1h");
                            if (!Objects.equals(Delta_1h_val, "null")) {
                                Delta_1h = Delta_1h_val + "%";
                                if( Double.parseDouble(Delta_1h_val) < 0 ) {
                                    Delta1h.setTextColor(getResources().getColor(R.color.red));
                                }else if ( Double.parseDouble(Delta_1h_val) > 0 ){
                                    Delta_1h = "+" + Delta_1h;
                                    Delta1h.setTextColor(getResources().getColor(R.color.green2));
                                }
                            }
                            Delta1h.setText(Delta_1h);

                            String Delta_1d = "Not Available";
                            String Delta_1d_val = object.getString("percent_change_24h");
                            if (!Objects.equals(Delta_1d_val, "null")) {
                                Delta_1d = Delta_1d_val + "%";
                                if( Double.parseDouble(Delta_1d_val) < 0 ) {
                                    Delta1d.setTextColor(getResources().getColor(R.color.red));
                                }else if ( Double.parseDouble(Delta_1d_val) > 0 ){
                                    Delta_1d = "+" + Delta_1d;
                                    Delta1d.setTextColor(getResources().getColor(R.color.green2));
                                }
                            }
                            Delta1d.setText(Delta_1d);

                            String Delta_7d = "Not Available";
                            String Delta_7d_val = object.getString("percent_change_7d");
                            if (!Objects.equals(Delta_7d_val, "null")) {
                                Delta_7d = Delta_7d_val + "%";
                                if( Double.parseDouble(Delta_7d_val) < 0 ) {
                                    Delta7d.setTextColor(getResources().getColor(R.color.red));
                                }else if ( Double.parseDouble(Delta_7d_val) > 0 ){
                                    Delta_7d = "+" + Delta_7d;
                                    Delta7d.setTextColor(getResources().getColor(R.color.green2));
                                }
                            }
                            Delta7d.setText(Delta_7d);

                            String Av_Supply = "Not Available";
                            String Av_Supply_val = object.getString("available_supply");
                            if (!Objects.equals(Av_Supply_val, "null")) {
                                Av_Supply = frmt.format(Double.parseDouble(Av_Supply_val));
                            }
                            AvailSupply.setText(Av_Supply);

                            String T_Supply  = "Not Available";
                            String T_Supply_val = object.getString("total_supply");
                            if (!Objects.equals(T_Supply_val, "null")) {
                                T_Supply = frmt.format(Double.parseDouble(T_Supply_val));
                            }
                            TotalSupply.setText(T_Supply);

                            String Max_Supply  = "Not Available";
                            String Max_Supply_val = object.getString("max_supply");
                            if (!Objects.equals(Max_Supply_val, "null")) {
                                Max_Supply = frmt.format(Double.parseDouble(Max_Supply_val));
                            }
                            MaxSupply.setText(Max_Supply);


                            String USD_Volume = "Not Available";
                            String USD_Volume_val = object.getString("24h_volume_usd");
                            if (!Objects.equals(USD_Volume_val, "null")) {
                                USD_Volume = "$" + frmt3.format(Double.parseDouble(USD_Volume_val));
                            }
                            VolumeUSD.setText(USD_Volume);

                            String EUR_Volume = "Not Available";
                            String EUR_Volume_val = object.getString("24h_volume_eur");
                            if (!Objects.equals(EUR_Volume_val, "null")) {
                                EUR_Volume = "\u20AC" +
                                             frmt3.format(Double.parseDouble(EUR_Volume_val));
                            }
                            VolumeEUR.setText(EUR_Volume);

                            String USD_MarketCap = "Not Available";
                            String USD_MarketCap_val = object.getString("market_cap_usd");
                            if (!Objects.equals(USD_MarketCap_val, "null")) {
                                USD_MarketCap = "$" +
                                                frmt3.format(Double.parseDouble(USD_MarketCap_val));
                            }
                            MarketCapUSD.setText(USD_MarketCap);

                            String EUR_MarketCap = "Not Available";
                            String EUR_MarketCap_val = object.getString("market_cap_eur");
                            if (!Objects.equals(EUR_MarketCap_val, "null")) {
                                EUR_MarketCap = "\u20AC" +
                                        frmt3.format(Double.parseDouble(EUR_MarketCap_val));
                            }
                            MarketCapEUR.setText(EUR_MarketCap);



                            long last_update = Long.parseLong(
                                                object.getString("last_updated")) *1000L;

                            String lastUpdateString =
                                    DateFormat.getDateTimeInstance().format( new Date(last_update));

                            Time.setText(lastUpdateString);



                            dialog.dismiss();

                        } catch (JSONException e) {
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
        RequestQueue rQueue = Volley.newRequestQueue(CryptoSelectActivity.this);
        rQueue.add(cryptoSel_request);
        CMC_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(CMC_url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}


