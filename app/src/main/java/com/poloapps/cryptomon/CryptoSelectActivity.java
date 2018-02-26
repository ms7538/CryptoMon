package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
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

public class CryptoSelectActivity extends BaseActivity {

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

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final String Curr          = mSettings.getString("Curr_code","eur");
        String CAP_curr            = Curr.toUpperCase();
        final String Curr_symbol   = mSettings.getString("Curr_symb","€");
        String Select_url1         = "https://api.coinmarketcap.com/v1/ticker/";
        String Select_url2         = "/?convert=" + Curr;

        final String price_key_nonUSD      = "price_" + Curr;
        final String volume_key_nonUSD     = "24h_volume_" + Curr;
        final String market_cap_key_nonUSD = "market_cap_" + Curr;

        final TextView CMC_link = findViewById(R.id.sel_crypto_coinmarketcap_link);
        CMC_link.setPaintFlags(CMC_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        String crypto_id     = getIntent().getStringExtra("crypto_id");
        String Select_url    = Select_url1 + crypto_id + Select_url2;
        final String CMC_url = getString(R.string.cryptos_display_link) + crypto_id + "/";

        final DecimalFormat frmt  = new DecimalFormat("#,###,###,###,###.##");
        final DecimalFormat frmt0 = new DecimalFormat("#,###,###,###,###");
        final DecimalFormat frmt2 = new DecimalFormat("#.########");
        final DecimalFormat frmt3 = new DecimalFormat("#,###,###,###,###");

        final TextView Time                = findViewById(R.id.select_update_time);
        final TextView Name                = findViewById(R.id.select_name);
        final TextView Rank                = findViewById(R.id.select_rank);
        final TextView Symbol              = findViewById(R.id.select_symbol);

        final TextView PriceUSD            = findViewById(R.id.select_price_usd);
        final TextView PriceEUR            = findViewById(R.id.select_price_eur);
        final TextView PriceBTC            = findViewById(R.id.select_price_btc);

        final TextView AvailSupply         = findViewById(R.id.select_avail_supply);
        final TextView TotalSupply         = findViewById(R.id.select_total_supply);
        final TextView MaxSupply           = findViewById(R.id.select_max_supply);

        final TextView Delta1h             = findViewById(R.id.select_delta_1h);
        final TextView Delta1d             = findViewById(R.id.select_delta_24h);
        final TextView Delta7d             = findViewById(R.id.select_delta_7d);

        final TextView VolumeUSD           = findViewById(R.id.select_24h_vol_usd);
        final TextView VolumeEUR           = findViewById(R.id.select_24h_vol_eur);

        final TextView MarketCapUSD        = findViewById(R.id.select_market_cap_usd);
        final TextView MarketCapEUR        = findViewById(R.id.select_market_cap_eur);

        final TextView price_NUSD_TV       = findViewById(R.id.textView_price_eur);
        final TextView volume_NUSD_TV      = findViewById(R.id.textView_24h_vol_eur);
        final TextView market_cap_NUSD_TV  = findViewById(R.id.textView_market_cap_eur);

        String TV_price_text  = getString(R.string.price_) + " " +
                                CAP_curr + getString(R.string.colon);
        String TV_volume_text = getString(R.string.vol24h_) + " " + CAP_curr  +
                                getString(R.string.colon);
        String TV_cap_text    = getString(R.string.market_cap_) + " " + CAP_curr  +
                                getString(R.string.colon);

        price_NUSD_TV.setText(TV_price_text);
        volume_NUSD_TV.setText(TV_volume_text);
        market_cap_NUSD_TV.setText(TV_cap_text);

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
                            double eurP = Double.parseDouble(object.getString(price_key_nonUSD));
                            double btcP = Double.parseDouble(object.getString("price_btc"));

                            if      (usdP < 0.01) USD_frmt = frmt2;
                            else if (usdP > 99)   USD_frmt = frmt0;
                            if      (eurP < 0.01) EUR_frmt = frmt2;
                            else if (eurP > 99)   EUR_frmt = frmt0;
                            if      (btcP < 0.01) BTC_frmt = frmt2;


                            String Price_USD = "$"      + USD_frmt.format(usdP);
                            String Price_EUR = Curr_symbol + EUR_frmt.format(eurP);
                            String Price_BTC = "\u0E3F" + BTC_frmt.format(btcP);

                            PriceUSD.setText(Price_USD);
                            PriceEUR.setText(Price_EUR);
                            PriceBTC.setText(Price_BTC);


                            String Delta_1h = getString(R.string.not_avail);
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

                            String Delta_1d = getString(R.string.not_avail);
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

                            String Delta_7d = getString(R.string.not_avail);
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

                            String Av_Supply = getString(R.string.not_avail);
                            String Av_Supply_val = object.getString("available_supply");
                            if (!Objects.equals(Av_Supply_val, "null")) {
                                Av_Supply = frmt.format(Double.parseDouble(Av_Supply_val));
                            }
                            AvailSupply.setText(Av_Supply);

                            String T_Supply  = getString(R.string.not_avail);
                            String T_Supply_val = object.getString("total_supply");
                            if (!Objects.equals(T_Supply_val, "null")) {
                                T_Supply = frmt.format(Double.parseDouble(T_Supply_val));
                            }
                            TotalSupply.setText(T_Supply);

                            String Max_Supply  = getString(R.string.not_avail);
                            String Max_Supply_val = object.getString("max_supply");
                            if (!Objects.equals(Max_Supply_val, "null")) {
                                Max_Supply = frmt.format(Double.parseDouble(Max_Supply_val));
                            }
                            MaxSupply.setText(Max_Supply);


                            String USD_Volume = getString(R.string.not_avail);
                            String USD_Volume_val = object.getString("24h_volume_usd");
                            if (!Objects.equals(USD_Volume_val, "null")) {
                                USD_Volume = "$" + frmt3.format(Double.parseDouble(USD_Volume_val));
                            }
                            VolumeUSD.setText(USD_Volume);

                            String EUR_Volume = getString(R.string.not_avail);
                            String EUR_Volume_val = object.getString(volume_key_nonUSD);
                            if (!Objects.equals(EUR_Volume_val, "null")) {
                                EUR_Volume = Curr_symbol +
                                             frmt3.format(Double.parseDouble(EUR_Volume_val));
                            }
                            VolumeEUR.setText(EUR_Volume);

                            String USD_MarketCap = getString(R.string.not_avail);
                            String USD_MarketCap_val = object.getString("market_cap_usd");
                            if (!Objects.equals(USD_MarketCap_val, "null")) {
                                USD_MarketCap = "$" +
                                                frmt3.format(Double.parseDouble(USD_MarketCap_val));
                            }
                            MarketCapUSD.setText(USD_MarketCap);

                            String EUR_MarketCap = getString(R.string.not_avail);
                            String EUR_MarketCap_val = object.getString(market_cap_key_nonUSD);
                            if (!Objects.equals(EUR_MarketCap_val, "null")) {
                                EUR_MarketCap = Curr_symbol +
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

