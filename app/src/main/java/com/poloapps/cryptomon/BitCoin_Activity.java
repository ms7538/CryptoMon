package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

//https://api.coinmarketcap.com/v1/ticker/   -top 100 crypto api
public class BitCoin_Activity extends AppCompatActivity {


    String BC_url = "https://api.coindesk.com/v1/bpi/currentprice/usd.json";
    //String LC_url = "https://api.coinmarketcap.com/v1/ticker/litecoin/";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bitcoin_layout);
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog = new ProgressDialog(this);
        final DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest bitcoin_usd_request = new StringRequest(BC_url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                TextView BitCoin_Rate = findViewById((R.id.bitcoin_rate_val));
                try {

                    JSONObject object = new JSONObject(string);
                    object = object.getJSONObject("bpi");
                    object = object.getJSONObject("USD");
                    Double bitcoin_rate = object.getDouble("rate_float");
                    String Bitcoin_Rate = "$" + formatter.format(bitcoin_rate);
                    BitCoin_Rate.setText(Bitcoin_Rate);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(BitCoin_Activity.this);
        rQueue.add(bitcoin_usd_request);

    }


}
