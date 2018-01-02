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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class Top_100 extends AppCompatActivity {
    String LC_url = "https://api.coinmarketcap.com/v1/ticker/";
    ProgressDialog dialog;
   // final DecimalFormat formatter = new DecimalFormat("#,###,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_100);
    }
    @Override
    public void onResume() {
        super.onResume();
        dialog = new ProgressDialog(this);
        final TextView test_txt = findViewById((R.id.testtv));
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest crypto100_request = new StringRequest(LC_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {

                        try {
                            JSONArray T100_Array = new JSONArray(string);
                            JSONObject obj1 = T100_Array.getJSONObject(0);
                            String rate = obj1.getString("price_usd");
                            test_txt.setText(rate);

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

        RequestQueue rQueue = Volley.newRequestQueue(Top_100.this);
        rQueue.add(crypto100_request);
           }
}
