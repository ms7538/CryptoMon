package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;

public class Top_100 extends AppCompatActivity {
    String LC_url = "https://api.coinmarketcap.com/v1/ticker/";
    ProgressDialog dialog;
    ArrayList<HashMap<String, String>> rankList;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_100);

    }
    @Override
    public void onResume() {
        super.onResume();
        final DecimalFormat formatter = new DecimalFormat("#,###,###,###.##");
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest crypto100_request = new StringRequest(LC_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {

                        try {

                            rankList = new ArrayList<>();
                            ListView lv = findViewById(R.id.list);

                            JSONArray T100_Array = new JSONArray(string);

                            for (int i = 0; i < T100_Array.length(); i++) {

                                JSONObject obj1 = T100_Array.getJSONObject(i);

                                String rate   = obj1.getString("price_usd");
                                rate          = "$" + formatter.format(Double.parseDouble(rate));

                                String name   = obj1.getString("name");
                                String symbol = obj1.getString("symbol");
                                name          = name + " / " + symbol;
                                String rank   = obj1.getString("rank");

                                HashMap<String, String> item = new HashMap<>();
                                item.put("rank",   rank);
                                item.put("name",   name);
                                item.put("rate",   rate);
                                rankList.add(item);
                            }

                            ListAdapter adapter = new SimpleAdapter(
                                    Top_100.this, rankList,
                                    R.layout.list_item, new String[]{"rank","name","rate"},
                                    new int[]{R.id.list_rank, R.id.list_name,
                                            R.id.list_rate});

                            lv.setAdapter(adapter);

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
