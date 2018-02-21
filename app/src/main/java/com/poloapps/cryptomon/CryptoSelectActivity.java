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

import java.text.DecimalFormat;

public class CryptoSelectActivity extends AppCompatActivity {
    String Select_url1 = "https://api.coinmarketcap.com/v1/ticker/";
    String Select_url2 = "/?convert=EUR";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_select);

        AdView mAdView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        String crypto_id = getIntent().getStringExtra("crypto_id");
        String Select_url = Select_url1 + crypto_id + Select_url2;
        final String CMC_url = getString(R.string.cryptos_display_link) + crypto_id + "/";

        final DecimalFormat frmt  = new DecimalFormat("#,###,###,###.##");
        final DecimalFormat frmt2 = new DecimalFormat("#.######");
        final DecimalFormat frmt3 = new DecimalFormat("#,###,###,###");

        final TextView Name     = findViewById(R.id.select_name);
        final TextView Rank     = findViewById(R.id.select_rank);
        final TextView Symbol   = findViewById(R.id.select_symbol);
        final TextView CMC_link = findViewById(R.id.sel_crypto_coinmarketcap_link);
        CMC_link.setPaintFlags(CMC_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

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
                            String Sel_Name = object.getString("name");
                            String Sel_Rank = object.getString("rank");
                            String Sel_Symb = object.getString("symbol");

                            Name.setText(Sel_Name);
                            Rank.setText(Sel_Rank);
                            Symbol.setText(Sel_Symb);
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


