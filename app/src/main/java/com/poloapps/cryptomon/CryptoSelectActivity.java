package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import java.text.DecimalFormat;

public class CryptoSelectActivity extends BaseActivity {

    ProgressDialog  dialog;
    dbPriceHandler  dbPHandler;
    dbVolumeHandler dbVHandler;
    Boolean alertPrice = false;
    Boolean alertVol   = false;

    final DecimalFormat frmt  = new DecimalFormat("#,###,###,###,###.##");
    final DecimalFormat frmt0 = new DecimalFormat("#,###,###,###,###");
    final DecimalFormat frmt2 = new DecimalFormat("#.########");
    final DecimalFormat frmt3 = new DecimalFormat("#,###,###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_select);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                R.color.dark_gray)));

        dbPHandler = new dbPriceHandler(this, null);
        dbVHandler = new dbVolumeHandler(this, null);

        mPublisherAdView = findViewById(R.id.adView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("A530388CACF455CECC92502035BB36DC")
                .build();
        mPublisherAdView.loadAd(adRequest);

    }
    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        editor.putBoolean("cs_active", true);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        stopRunningService();

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        getIntent().removeExtra("restart");
        editor.putBoolean("cs_active", true);
        editor.apply();

        final LayoutInflater li    = LayoutInflater.from(CryptoSelectActivity.this);
        final String Curr          = mSettings.getString("Curr_code","eur");
        String CAP_curr            = Curr.toUpperCase();
        final String Curr_symbol   = mSettings.getString("Curr_symb","€");
        final boolean Dollar       = mSettings.getBoolean("Dollar", true);
        String Select_url1         = "https://api.coinmarketcap.com/v1/ticker/";
        String Select_url2         = "/?convert=" + Curr;

        final String price_key_nonUSD      = "price_"      + Curr;
        final String volume_key_nonUSD     = "24h_volume_" + Curr;
        final String market_cap_key_nonUSD = "market_cap_" + Curr;

        final TextView CMC_link = findViewById(R.id.sel_crypto_coinmarketcap_link);
        CMC_link.setPaintFlags(CMC_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        final String crypto_id    = getIntent().getStringExtra("crypto_id");
        String Select_url         = Select_url1 + crypto_id + Select_url2;
        final String CMC_url      = getString(R.string.cryptos_display_link) + crypto_id + "/";

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

        final Button alertsBtn             = findViewById(R.id.sel_alerts_link);

        String TV_price_text  = getString(R.string.price_)      + " " + CAP_curr  + getString(R.string.colon);
        String TV_volume_text = getString(R.string.vol24h_) + " " + CAP_curr  + getString(R.string.colon);
        String TV_cap_text    = getString(R.string.market_cap_) + " " + CAP_curr  + getString(R.string.colon);

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
                            double not_usdP = Double.parseDouble(
                                                                object.getString(price_key_nonUSD));
                            double btcP = Double.parseDouble(object.getString("price_btc"));

                            double currPrice = usdP;

                            if(!Dollar){
                               currPrice = not_usdP;
                            }

                            if      (usdP     < 0.01) USD_frmt = frmt2;
                            else if (usdP     > 99)   USD_frmt = frmt0;
                            if      (not_usdP < 0.01) EUR_frmt = frmt2;
                            else if (not_usdP > 99)   EUR_frmt = frmt0;
                            if      (btcP     < 0.01) BTC_frmt = frmt2;

                            String Price_USD = "$"         + USD_frmt.format(usdP);
                            String Price_EUR = Curr_symbol + EUR_frmt.format(not_usdP);
                            String Price_BTC = "\u0E3F"    + BTC_frmt.format(btcP);

                            PriceUSD.setText(Price_USD);
                            PriceEUR.setText(Price_EUR);
                            PriceBTC.setText(Price_BTC);

                            String Delta_1h     = getString(R.string.not_avail);
                            String Delta_1h_val = object.getString("percent_change_1h");
                            if (!Objects.equals(Delta_1h_val, "null")) {
                                Delta_1h = Delta_1h_val + "%";
                                if( Double.parseDouble(Delta_1h_val) < 0 ) {
                                    Delta1h.setTextColor(ContextCompat.getColor(getBaseContext(),
                                            R.color.red));
                                }else if ( Double.parseDouble(Delta_1h_val) > 0 ){
                                    Delta_1h = "+" + Delta_1h;
                                    Delta1h.setTextColor(ContextCompat.getColor(getBaseContext(),
                                            R.color.green2));
                                }
                            }
                            Delta1h.setText(Delta_1h);

                            String Delta_1d     = getString(R.string.not_avail);
                            String Delta_1d_val = object.getString("percent_change_24h");
                            if (!Objects.equals(Delta_1d_val, "null")) {
                                Delta_1d = Delta_1d_val + "%";
                                if( Double.parseDouble(Delta_1d_val) < 0 ) {
                                    Delta1d.setTextColor(ContextCompat.getColor(getBaseContext(),
                                            R.color.red));
                                }else if ( Double.parseDouble(Delta_1d_val) > 0 ){
                                    Delta_1d = "+" + Delta_1d;
                                    Delta1d.setTextColor(ContextCompat.getColor(getBaseContext(),
                                            R.color.green2));
                                }
                            }
                            Delta1d.setText(Delta_1d);

                            String Delta_7d     = getString(R.string.not_avail);
                            String Delta_7d_val = object.getString("percent_change_7d");
                            if (!Objects.equals(Delta_7d_val, "null")) {
                                Delta_7d = Delta_7d_val + "%";
                                if( Double.parseDouble(Delta_7d_val) < 0 ) {
                                    Delta7d.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.red));
                                }else if ( Double.parseDouble(Delta_7d_val) > 0 ){
                                    Delta_7d = "+" + Delta_7d;
                                    Delta7d.setTextColor(ContextCompat.getColor(getBaseContext(),
                                            R.color.green2));
                                }
                            }
                            Delta7d.setText(Delta_7d);

                            String Av_Supply     = getString(R.string.not_avail);
                            String Av_Supply_val = object.getString("available_supply");
                            if (!Objects.equals(Av_Supply_val, "null")) {
                                Av_Supply = frmt.format(Double.parseDouble(Av_Supply_val));
                            }
                            AvailSupply.setText(Av_Supply);

                            String T_Supply     = getString(R.string.not_avail);
                            String T_Supply_val = object.getString("total_supply");
                            if (!Objects.equals(T_Supply_val, "null")) {
                                T_Supply = frmt.format(Double.parseDouble(T_Supply_val));
                            }
                            TotalSupply.setText(T_Supply);

                            String Max_Supply     = getString(R.string.not_avail);
                            String Max_Supply_val = object.getString("max_supply");
                            if (!Objects.equals(Max_Supply_val, "null")) {
                                Max_Supply = frmt.format(Double.parseDouble(Max_Supply_val));
                            }
                            MaxSupply.setText(Max_Supply);

                            String USD_Volume     = getString(R.string.not_avail);
                            String USD_Volume_val = object.getString("24h_volume_usd");
                            if (!Objects.equals(USD_Volume_val, "null")) {
                                USD_Volume = "$" + frmt3.format(Double.parseDouble(USD_Volume_val));
                            }
                            VolumeUSD.setText(USD_Volume);

                            editor.putFloat ("price_init_f",  (float) currPrice);

                            String priceInit = frmt.format((currPrice));
                            if (currPrice >= 100)
                                   priceInit = frmt0.format((currPrice));
                            editor.putString("price_initial", priceInit);

                            String EUR_Volume     = getString(R.string.not_avail);
                            String EUR_Volume_val = object.getString(volume_key_nonUSD);
                            if (!Objects.equals(EUR_Volume_val, "null")) {
                                EUR_Volume = Curr_symbol +
                                             frmt3.format(Double.parseDouble(EUR_Volume_val));
                            }
                            VolumeEUR.setText(EUR_Volume);

                            String CurrVol = USD_Volume_val;
                            if(!Dollar) CurrVol = EUR_Volume_val;

                            editor.putFloat("vol_init_i", Float.parseFloat(CurrVol));
                            editor.putString("vol_initial",frmt3.format(Float.parseFloat(CurrVol)));
                            editor.apply();

                            String USD_MarketCap     = getString(R.string.not_avail);
                            String USD_MarketCap_val = object.getString("market_cap_usd");
                            if (!Objects.equals(USD_MarketCap_val, "null")) {
                                USD_MarketCap = "$" +
                                                frmt3.format(Double.parseDouble(USD_MarketCap_val));
                            }
                            MarketCapUSD.setText(USD_MarketCap);

                            String EUR_MarketCap     = getString(R.string.not_avail);
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

                @SuppressLint("InflateParams")
                final View CMC_linkMenu = li.inflate(R.layout.cmc_link_menu, null);
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(
                        CryptoSelectActivity.this);
                builder2.setView(CMC_linkMenu);
                final AlertDialog dialog2  = builder2.create();
                dialog2.show();

                TextView Link = CMC_linkMenu.findViewById(R.id.cmc_link_id);
                Link.setText(crypto_id);
                Button OK = CMC_linkMenu.findViewById(R.id.cmc_OK_btn);
                Button NO = CMC_linkMenu.findViewById(R.id.cmc_NO_btn);

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
        alertsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("InflateParams")
                final View alertsMenu = li.inflate(R.layout.alerts_select_menu, null);
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(
                                                                 CryptoSelectActivity.this);
                builder3.setView(alertsMenu);
                final String  Symbol      = getIntent().getStringExtra("crypto_id");
                TextView alertName        = alertsMenu.findViewById(R.id.alerts_crypto_name);
                TextView alertsSym        = alertsMenu.findViewById(R.id.alerts_price_currency);
                TextView alertsVSym       = alertsMenu.findViewById(R.id.alerts_volume_currency);
                final EditText priceInput = alertsMenu.findViewById(R.id.price_input);
                final EditText volInput   = alertsMenu.findViewById(R.id.volume_input);
                final Button setPriceBtn  = alertsMenu.findViewById(R.id.price_setBtn);
                final Button setVolBtn    = alertsMenu.findViewById(R.id.vol_setBtn);

                alertName.setText(Symbol);
                final String initPrice    = mSettings.getString("price_initial","0");
                final double currentPrice = mSettings.getFloat("price_init_f",0);

                final String initVolume   = mSettings.getString("vol_initial","0");
                final double currentVol   = mSettings.getFloat("vol_init_i",0);

                final String priceTH      = dbPHandler.getPrice_Val(Symbol);
                final String volTH        = dbVHandler.getVol_Val(Symbol);

                if (!priceTH.equals("")){
                    double formatPTH = Double.parseDouble(priceTH);
                    String fmtTH     = frmt.format(formatPTH);
                    priceInput.setText(fmtTH);
                    alertPrice       = true;
                }else {
                    priceInput.setHint(initPrice);
                    alertPrice       = false;
                }
                if (!volTH.equals("")){
                    double formatVTH = Double.valueOf(volTH).longValue();
                    String fmtVTH    = frmt3.format(formatVTH);
                    volInput.setText(fmtVTH);
                    alertVol         = true;
                }else {
                    volInput.setHint(initVolume);
                    alertVol         = false;
                }
                String symbolCurrent = "$";
                if(!Dollar){
                    symbolCurrent = mSettings.getString("Curr_symb","€");
                    alertsSym.setText(symbolCurrent);
                    alertsVSym.setText(symbolCurrent);
                }

                setPriceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!alertPrice) {
                            if(!priceInput.getText().toString().equals("")) {
                                alertPrice = true;
                                String fmtRemoved = priceInput.getText().toString().replace(
                                                                        ",", "");
                                double thPrice = Double.parseDouble(fmtRemoved);
                                int tc = 0;
                                if (thPrice > currentPrice)      tc = 1;
                                else if (thPrice < currentPrice) tc = -1;

                                dbPHandler.addPriceAlert(Symbol, tc, thPrice);

                                setPriceBtn.setVisibility(View.GONE);
                                setPriceBtn.setText(R.string.clear);
                                setPriceBtn.setVisibility(View.VISIBLE);
                                priceInput.setText(frmt.format(Double.parseDouble(
                                                               priceInput.getText().toString())));
                            }else
                                Toast.makeText(getApplicationContext(),
                                        "PRICE THRESHOLD MUST BE SET",
                                                                         Toast.LENGTH_SHORT).show();
                        }else {
                            setPriceBtn.setVisibility(View.GONE);
                            setPriceBtn.setText(R.string.set);
                            setPriceBtn.setVisibility(View.VISIBLE);
                            priceInput.setText("");
                            priceInput.setHint(initPrice);
                            alertPrice = false;
                            dbPHandler.deleteAlert(Symbol);
                        }

                    }
                });
                setVolBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!alertVol) {
                            if (!volInput.getText().toString().equals("")) {

                                alertVol = true;
                                String fmtRemoved = volInput.getText().toString().replace(
                                                                        ",", "");
                                double thVol = Double.parseDouble(fmtRemoved);
                                int tc = 0;
                                if (thVol > currentVol) tc = 1;
                                else if (thVol < currentVol) tc = -1;
                                dbVHandler.addVolAlert(Symbol, tc, thVol);
                                setVolBtn.setVisibility(View.GONE);
                                setVolBtn.setText(R.string.clear);
                                setVolBtn.setVisibility(View.VISIBLE);
                                volInput.setText(frmt3.format(Double.parseDouble(
                                                                  volInput.getText().toString())));
                            } else
                                Toast.makeText(getApplicationContext(),
                                        "VOLUME THRESHOLD MUST BE SET",
                                                                         Toast.LENGTH_SHORT).show();
                        }else {
                            setVolBtn.setVisibility(View.GONE);
                            setVolBtn.setText(R.string.set);
                            setVolBtn.setVisibility(View.VISIBLE);
                            volInput.setText("");
                            volInput.setHint(initVolume);
                            alertVol = false;
                            dbVHandler.deleteAlert(Symbol);
                        }

                    }
                });

                final AlertDialog dialog3 = builder3.create();
                dialog3.show();

                if( alertPrice ) setPriceBtn.setText(R.string.clear);
                else             setPriceBtn.setText(R.string.set);
                if ( alertVol )  setVolBtn.setText(R.string.clear);
                else             setVolBtn.setText(R.string.set);

                Button Dismiss = alertsMenu.findViewById(R.id.alerts_NO_btn);
                Dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();

        editor.putBoolean("cs_active", false);
        editor.apply();

        boolean aaActive   = mSettings.getBoolean("aa_active", false);
        boolean t100Active = mSettings.getBoolean("t100_active", false);
        boolean restart    = getIntent().getBooleanExtra("restart", false);
        getIntent().removeExtra("restart");

        if(!aaActive && !t100Active && !restart) checkStartService();
    }
    @Override
    public void onPause(){
        super.onPause();
        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean("cs_active", false);
        editor.apply();
    }
}


