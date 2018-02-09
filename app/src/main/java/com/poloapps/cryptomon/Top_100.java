package com.poloapps.cryptomon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import java.util.Objects;

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

                                String rate       = obj1.getString("price_usd");
                                Double d_rate     = Double.parseDouble(rate);
                                //
                                String volume_24h = "$" + formatter.format(Double.parseDouble(
                                                          obj1.getString("24h_volume_usd")));
                                if (d_rate < .01) rate  = "$" + formatter2.format(d_rate);
                                else              rate  = "$" + formatter.format(d_rate);

                                String name       = obj1.getString("name");
                                String symbol     = obj1.getString("symbol");
                                name              = name + " / " + symbol;
                                String rank       = obj1.getString("rank");
                                String delta_1h   = obj1.getString("percent_change_1h");
                                String delta_1d   = obj1.getString("percent_change_24h");
                                String delta_7d   = obj1.getString("percent_change_7d");
                                String link_id    = obj1.getString("id");

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
                            int[] to = {R.id.list_rank, R.id.list_name, R.id.list_rate,
                                    R.id.h1,R.id.d1,R.id.delta7_d,R.id.list_24h_volume,
                                    R.id.id_link};

                            ListAdapter listAdapter = new SimpleAdapter(getApplicationContext(),
                                    rankList, R.layout.list_item, from, to)
                            {
                                @Override
                                public View getView(int position, View cnvrtView, ViewGroup parent){

                                    View view = super.getView(position, cnvrtView, parent);

                                    TextView delta_1h = view.findViewById(R.id.h1);
                                    TextView delta_1d = view.findViewById(R.id.d1);
                                    TextView delta_7d = view.findViewById(R.id.delta7_d);
                                    final TextView link_id  = view.findViewById(R.id.id_link);

                                    Map<String, String> currentRow = rankList.get(position);

                                    double    delta1h = 0;
                                    if (!Objects.equals(currentRow.get("d1h"), "null")){
                                        delta1h = Double.parseDouble(currentRow.get("d1h"));
                                    }
                                    if      ( delta1h < 0 ) delta_1h.setTextColor(RED);
                                    else if ( delta1h > 0 ) delta_1h.setTextColor(Color.GREEN);

                                    double    delta1d = 0;
                                    if (!Objects.equals(currentRow.get("d1d"), "null")){
                                        delta1d = Double.parseDouble(currentRow.get("d1d"));
                                    }
                                    if      ( delta1d < 0 ) delta_1d.setTextColor(RED);
                                    else if ( delta1d > 0 ) delta_1d.setTextColor(Color.GREEN);

                                    double delta7d = 0;
                                    if (!Objects.equals(currentRow.get("d7_d"), "null")){
                                        delta7d = Double.parseDouble(currentRow.get("d7_d"));
                                    }
                                    if      ( delta7d < 0 ) delta_7d.setTextColor(RED);
                                    else if ( delta7d > 0 ) delta_7d.setTextColor(Color.GREEN);

                                    link_id.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Uri uri = Uri.parse(getString(
                                                    R.string.cryptos_display_link)
                                                    + link_id.getText());
                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                            startActivity(intent);

                                        }
                                    });

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
