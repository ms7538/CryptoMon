package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        final Integer RED = ContextCompat.getColor(getApplicationContext(),(R.color.red));

        final DecimalFormat formatter  = new DecimalFormat("#,###,###,###.##");
        final DecimalFormat formatter2 = new DecimalFormat("#.######");
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
                                Double d_rate = Double.parseDouble(rate);

                                if (d_rate < .01) rate  = "$" + formatter2.format(d_rate);
                                else              rate  = "$" + formatter.format(d_rate);

                                String name   = obj1.getString("name");
                                String symbol = obj1.getString("symbol");
                                name          = name + " / " + symbol;
                                String rank   = obj1.getString("rank");
                                String D_1h   = obj1.getString("percent_change_1h");
                                String D_1d   = obj1.getString("percent_change_24h");
                                String D_7d   = obj1.getString("percent_change_7d");

                                HashMap<String, String> item = new HashMap<>();
                                item.put("rank",   rank);
                                item.put("name",   name);
                                item.put("rate",   rate);
                                item.put("d1h",    D_1h);
                                item.put("d1d",    D_1d);
                                item.put("d7d",    D_7d);
                                rankList.add(item);
                            }

                            String[] from = {"rank","name","rate","d1h","d1d","d7d"};
                            int[] to = {R.id.list_rank, R.id.list_name, R.id.list_rate,
                                    R.id.h1,R.id.d1,R.id.d7};

                            ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(),
                                    rankList, R.layout.list_item, from, to)
                            {
                                @Override
                                public View getView(int position, View cnvrtView, ViewGroup parent){
                                    View view = super.getView(position, cnvrtView, parent);
                                    TextView delta_1h = view.findViewById(R.id.h1);
                                    TextView delta_1d = view.findViewById(R.id.d1);
                                    TextView delta_7d = view.findViewById(R.id.d7);

                                    Map<String, String> currentRow = rankList.get(position);

                                    double    delta1h = Double.parseDouble(currentRow.get("d1h"));
                                    if      ( delta1h < 0 ) delta_1h.setTextColor(RED);
                                    else if ( delta1h > 0 ) delta_1h.setTextColor(Color.GREEN);

                                    double    delta1d = Double.parseDouble(currentRow.get("d1d"));
                                    if      ( delta1d < 0 ) delta_1d.setTextColor(RED);
                                    else if ( delta1d > 0 ) delta_1d.setTextColor(Color.GREEN);

                                    double    delta7d = Double.parseDouble(currentRow.get("d7d"));
                                    if      ( delta7d < 0 ) delta_7d.setTextColor(RED);
                                    else if ( delta7d > 0 ) delta_7d.setTextColor(Color.GREEN);
                                    return view;
                                }
                            };

                            lv.setAdapter(listAdapter);

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
