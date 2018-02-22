package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

//v0.85  created
public class Top_100_Activity extends AppCompatActivity {

    String LC_url = "https://api.coinmarketcap.com/v1/ticker/?convert=EUR";
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

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final Boolean Dollar = mSettings.getBoolean("Dollar", true);
        final Integer RED = ContextCompat.getColor(getApplicationContext(),(R.color.red));

        final DecimalFormat form  = new DecimalFormat("#,###,###,###.##");
        final DecimalFormat form2 = new DecimalFormat("#.######");
        final DecimalFormat form3  = new DecimalFormat("#,###,###,###");
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        StringRequest crypto100_request = new StringRequest(LC_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {

                        try {
                            String price_key      = "price_usd";
                            String curr_symbol    = "$";
                            String volume_24h_key = "24h_volume_usd";

                            if(!Dollar){
                                price_key      = "price_eur";
                                curr_symbol    = "\u20AC";
                                volume_24h_key = "24h_volume_eur";
                            }
                            rankList = new ArrayList<>();
                            ListView lv = findViewById(R.id.list);

                            JSONArray T100_Array = new JSONArray(string);

                            for (int i = 0; i < T100_Array.length(); i++) {

                                JSONObject obj1 = T100_Array.getJSONObject(i);

                                String rate       = obj1.getString(price_key);
                                Double d_rate     = Double.parseDouble(rate);
                                //
                                String volume_24h = curr_symbol + form3.format(Double.parseDouble
                                                    (obj1.getString(volume_24h_key)));

                                if (d_rate < .01) rate  = curr_symbol + form2.format(d_rate);
                                else              rate  = curr_symbol + form.format(d_rate);

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
                                    final TextView link_id = view.findViewById(R.id.id_link);

                                    Map<String, String> currentRow = rankList.get(position);

                                    double delta1h = 0;
                                    if (!Objects.equals(currentRow.get("d1h"), "null")) {
                                        delta1h = Double.parseDouble(currentRow.get("d1h"));
                                    }
                                    if (delta1h < 0) delta_1h.setTextColor(RED);
                                    else if (delta1h > 0) delta_1h.setTextColor(Color.GREEN);

                                    double delta1d = 0;
                                    if (!Objects.equals(currentRow.get("d1d"), "null")) {
                                        delta1d = Double.parseDouble(currentRow.get("d1d"));
                                    }
                                    if (delta1d < 0) delta_1d.setTextColor(RED);
                                    else if (delta1d > 0) delta_1d.setTextColor(Color.GREEN);

                                    double delta7d = 0;
                                    if (!Objects.equals(currentRow.get("d7_d"), "null")) {
                                        delta7d = Double.parseDouble(currentRow.get("d7_d"));
                                    }
                                    if (delta7d < 0) delta_7d.setTextColor(RED);
                                    else if (delta7d > 0) delta_7d.setTextColor(Color.GREEN);

                                    link_id.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(
                                                    Top_100_Activity.this,
                                                    CryptoSelectActivity.class);
                                            intent.putExtra("crypto_id", link_id.getText());
                                            Top_100_Activity.this.startActivity(intent);
                                        }
                                    });
                                    dialog.dismiss();
                                    return view;
                                }
                            };
                            lv.setAdapter(listAdapter);
                        } catch (JSONException e) {
                            dialog.dismiss();
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
        RequestQueue rQueue = Volley.newRequestQueue(Top_100_Activity.this);
        rQueue.add(crypto100_request);

   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top100_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        final Boolean Dollar = mSettings.getBoolean("Dollar", true);

        switch (item.getItemId()) {

            case R.id.action_subreddits:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(Top_100_Activity.this);
                @SuppressLint("InflateParams")
                View mView = getLayoutInflater().inflate(R.layout.reddit_links_diag, null);
                builder.setView(mView);

                final AlertDialog dialog = builder.create();
                final Button Dismiss_btn = mView.findViewById(R.id.dismiss_btn_menu);

                int[] TV_IDs = new int[]{R.id.r_crypto_link,R.id.r_cryptomarkets_link,
                        R.id.r_bitplaceex_link,R.id.r_cryptocurrencies_link,
                        R.id.r_bitcoin_link,R.id.r_ethereum_link,R.id.r_ripple_link,
                        R.id.r_bitcoincash_link,R.id.r_litecoin_link,R.id.r_cardano_link,
                        R.id.r_stellar_link,R.id.r_neo_link,R.id.r_eos_link,R.id.r_iota_link,
                        R.id.r_dashpay_link,R.id.r_nem_link,R.id.r_monero_link,R.id.r_lisk_link,
                        R.id.r_ethclassic_link,R.id.r_tron_link,R.id.r_vechain_link,
                        R.id.r_qtum_link,R.id.r_bitcoin_gold_link,R.id.r_tether_link,
                        R.id.r_helloicon_link,R.id.r_omise_go_link,R.id.r_zcash_link,
                        R.id.r_verge_link,R.id.r_nano_link,R.id.r_binance_link,R.id.r_steem_link,
                        R.id.r_populous_link,R.id.r_bytecoin_link,R.id.r_rchain_link,
                        R.id.r_statusim_link,R.id.r_dogecoin_link,R.id.r_waves_link,
                        R.id.r_bitshares_link,R.id.r_maker_link,R.id.r_digix_link,
                        R.id.r_walton_link,R.id.r_zerox_link,R.id.r_augur_link,
                        R.id.r_aeternity_link,R.id.r_zclassic_link,R.id.r_decred_link,
                        R.id.r_veritaseum_link,R.id.r_hshare_link,R.id.r_electroneum_link,
                        R.id.r_komodo_link,R.id.r_kucoin_link,R.id.r_ardor_link,R.id.r_arkeco_link,
                        R.id.r_arkcoin_link,R.id.r_neotrader_link,R.id.r_dragonchain_link,
                        R.id.r_digibyte_link,R.id.r_loopring_link,R.id.r_zilliqa_link,
                        R.id.r_basicatttoken_link,R.id.r_byteball_link,R.id.r_aelf_link,
                        R.id.r_aelfofficial_link,R.id.r_aelftrader_link,R.id.r_polymath_link,
                        R.id.r_bytomblock_link,R.id.r_bytomchain_link,R.id.r_monacoin_link,
                        R.id.r_qash_link,R.id.r_redcoin_link,R.id.r_aionnetwork_link,
                        R.id.r_aiontrader_link,R.id.r_golemproject_link,R.id.r_golemtrader_link};

                final String[] Link_Strings = new String[] {"cryptocurrency","CryptoMarkets",
                        "BitplaceExchange","CryptoCurrencies",
                        "bitcoin","ethereum","ripple","bitcoincash" ,"litecoin","cardano","stellar",
                        "neo","eos","iota","dashpay","nem","monero","lisk","ethereumclassic",
                        "tronix","vechain","qtum","BitcoinGoldHQ","Tether","helloicon","omise_go",
                        "zcash","verge","nanocurrency","BinanceExchange","steem","populous",
                        "bytecoin","RChain","statusim","dogecoin","Wavesplatform","BitShares",
                        "MakerDAO","digix","Waltonchain","Zerox","Augur","Aeternity","ZClassic",
                        "Decred","Veritaseum","hshare","Electroneum","komodoplatform","kucoin",
                        "Ardor","ArkEcosystem","Arkcoin","Neotrader","Dragonchain","Digibyte",
                        "loopring","zilliqa","BATProject","ByteBall","aelf","aelfofficial",
                        "AElfTrader","PolymathNetwork","BytomBlockchain","Bytomchain","monacoin",
                        "QASH","ReddCoin","AionNetwork","AionTrader","GolemProject","GolemTrader"};


                for(int i = 0; i < TV_IDs.length ;i++) {
                    final int j = i;
                    TextView TV_name = mView.findViewById(TV_IDs[i]);
                    TV_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Uri uri = Uri.parse(getString(
                                    R.string.subreddit_link_text)
                                    + Link_Strings[j]+"/");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);

                        }});
                }

                Dismiss_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }});

                dialog.show();
                return true;

            case R.id.action_currency_sel:

                builder = new AlertDialog.Builder(Top_100_Activity.this);

                @SuppressLint("InflateParams")
                View mView2 = getLayoutInflater().inflate(R.layout.currency_diag, null);
                builder.setView(mView2);

                final AlertDialog dialog2 = builder.create();
                final RadioButton Currency_Dollar = mView2.findViewById(R.id.radio_currency_dollar);
                final RadioButton Currency_Euro = mView2.findViewById(R.id.radio_currency_euro);
                if (Dollar) Currency_Dollar.setChecked(true);
                else Currency_Euro.setChecked(true);

                Button Unit_OK = mView2.findViewById(R.id.Units_OK_btn);
                Unit_OK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Boolean Dollar_Sel = false;
                        if (Currency_Dollar.isChecked()) Dollar_Sel = true;
                        editor.putBoolean("Dollar", Dollar_Sel);
                        editor.apply();
                        dialog2.dismiss();
                        restart();
                    }});
                dialog2.show();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    void restart(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
}
