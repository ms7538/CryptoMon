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

//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class Current_Rates_Activity extends AppCompatActivity {


    String url = "https://api.coindesk.com/v1/bpi/currentprice/usd.json";
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current__rates_);



        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                parseJsonData(string);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!",
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(Current_Rates_Activity.this);
        rQueue.add(request);
    }

    void parseJsonData(String jsonString) {
        TextView BitCoin_Rate = findViewById((R.id.bitcoin_rate_val));
        try {
            JSONObject object = new JSONObject(jsonString);
            object=object.getJSONObject("bpi");
            object=object.getJSONObject("USD");
            Double bitcoin_rate = object.getDouble("rate_float");

            BitCoin_Rate.setText(String.format(Locale.US,"%f",bitcoin_rate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialog.dismiss();
    }
}
