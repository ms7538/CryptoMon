package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by Marko on 2/25/2018.
 * Updated on 10/22/2018
 * Base Class
 *
 */

public abstract class BaseActivity extends AppCompatActivity {

    String                LC_url         = "https://api.coinmarketcap.com/v1/ticker/";
    ProgressDialog        dialog;
    dbPriceHandler        dbPHandler;
    dbVolumeHandler       dbVHandler;
    dbCurrentValsHandler  dbCVHandler;
    dbPriceAlertsAchieved dbPAchHandler;
    dbVolAlertsAchieved   dbVAchHandler;
    PublisherAdView       mPublisherAdView;
    Integer overwritten   = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbPHandler    = new dbPriceHandler(this, null);
        dbVHandler    = new dbVolumeHandler(this, null);
        dbCVHandler   = new dbCurrentValsHandler(this, null);
        dbPAchHandler = new dbPriceAlertsAchieved(this, null);
        dbVAchHandler = new dbVolAlertsAchieved(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopRunningService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);

        int lenAch             = numberPAchAlerts() + numberVAchAlerts();
        int dispAlerts         = mSettings.getInt("disp_alerts",0);
        int newAlerts          = lenAch - dispAlerts + overwritten;
        MenuItem alertsIcon    = menu.findItem(R.id.action_alerts);
        int setAlerts          = numberPAlerts() + numberVAlerts();

        if      (newAlerts > 0) alertsIcon.setIcon(R.drawable.ic_action_red2);
        else if (setAlerts > 0 )alertsIcon.setIcon(R.drawable.ic_action_yellow);
        else                    alertsIcon.setIcon(R.drawable.no_alerts_logo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        final Boolean Dollar = mSettings.getBoolean("Dollar", true);
        final String  Curr   = mSettings.getString("Curr_code","eur");

        switch (item.getItemId()) {

            case R.id.action_t100:
                Intent intent2 = new Intent(
                        BaseActivity.this,
                        T100Activity.class);
                intent2.putExtra("restart", true);
                BaseActivity.this.startActivity(intent2);
                return true;

            case R.id.action_alerts:
                Intent intent = new Intent(
                        BaseActivity.this,
                        AllAlertsActivity.class);
                intent.putExtra("restart", true);
                BaseActivity.this.startActivity(intent);
                return true;

            case R.id.action_refresh:
                restart();
                return true;

            case R.id.action_subreddits:

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(BaseActivity.this);
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

                builder = new AlertDialog.Builder(BaseActivity.this);
                @SuppressLint("InflateParams") final View mView2 = getLayoutInflater()
                        .inflate(R.layout.currency_diag, null);
                builder.setView(mView2);
                RadioGroup rg = mView2.findViewById(R.id.curr_rg);
                final Button Unit_OK = mView2.findViewById(R.id.Units_OK_btn);
                Unit_OK.setEnabled(false);

                final AlertDialog dialog2  = builder.create();
                final RadioButton RadioUSD = mView2.findViewById(R.id.radio_currency_usd);
                final RadioButton RadioEUR = mView2.findViewById(R.id.radio_currency_eur);
                final RadioButton RadioJPY = mView2.findViewById(R.id.radio_currency_jpy);
                final RadioButton RadioGBP = mView2.findViewById(R.id.radio_currency_gbp);
                final RadioButton RadioAUD = mView2.findViewById(R.id.radio_currency_aud);
                final RadioButton RadioCAD = mView2.findViewById(R.id.radio_currency_cad);
                final RadioButton RadioCHF = mView2.findViewById(R.id.radio_currency_chf);
                final RadioButton RadioCNY = mView2.findViewById(R.id.radio_currency_cny);
                final RadioButton RadioSEK = mView2.findViewById(R.id.radio_currency_sek);
                final RadioButton RadioNZD = mView2.findViewById(R.id.radio_currency_nzd);
                final RadioButton RadioKRW = mView2.findViewById(R.id.radio_currency_krw);
                final RadioButton RadioTRY = mView2.findViewById(R.id.radio_currency_try);
                final RadioButton RadioRUB = mView2.findViewById(R.id.radio_currency_rub);
                final RadioButton RadioINR = mView2.findViewById(R.id.radio_currency_inr);

                final LinearLayout DelWarn = mView2.findViewById(R.id.curr_diag_delLayout);
                DelWarn.setVisibility(View.GONE);

                if (Dollar) {
                    RadioUSD.getParent().requestChildFocus(RadioUSD, RadioUSD);
                    RadioUSD.setChecked(true);
                    RadioUSD.setEnabled(false);
                }
                else if (Objects.equals(Curr, "eur")) {
                    RadioEUR.getParent().requestChildFocus(RadioEUR, RadioEUR);
                    RadioEUR.setChecked(true);
                    RadioEUR.setEnabled(false);
                }
                else if (Objects.equals(Curr, "jpy")) {
                    RadioJPY.getParent().requestChildFocus(RadioJPY, RadioJPY);
                    RadioJPY.setChecked(true);
                    RadioJPY.setEnabled(false);
                }
                else if (Objects.equals(Curr, "gbp")) {
                    RadioGBP.getParent().requestChildFocus(RadioGBP, RadioGBP);
                    RadioGBP.setChecked(true);
                    RadioGBP.setEnabled(false);
                }
                else if (Objects.equals(Curr, "aud")) {
                    RadioAUD.getParent().requestChildFocus(RadioAUD, RadioAUD);
                    RadioAUD.setChecked(true);
                    RadioAUD.setEnabled(false);
                }
                else if (Objects.equals(Curr, "cad")) {
                    RadioCAD.getParent().requestChildFocus(RadioCAD, RadioCAD);
                    RadioCAD.setChecked(true);
                    RadioCAD.setEnabled(false);
                }
                else if (Objects.equals(Curr, "chf")) {
                    RadioCHF.getParent().requestChildFocus(RadioCHF, RadioCHF);
                    RadioCHF.setChecked(true);
                    RadioCHF.setEnabled(false);
                }
                else if (Objects.equals(Curr, "cny")) {
                    RadioCNY.getParent().requestChildFocus(RadioCNY, RadioCNY);
                    RadioCNY.setChecked(true);
                    RadioCNY.setEnabled(false);
                }
                else if (Objects.equals(Curr, "sek")) {
                    RadioSEK.getParent().requestChildFocus(RadioSEK, RadioSEK);
                    RadioSEK.setChecked(true);
                    RadioSEK.setEnabled(false);
                }
                else if (Objects.equals(Curr, "nzd")) {
                    RadioNZD.getParent().requestChildFocus(RadioNZD, RadioNZD);
                    RadioNZD.setChecked(true);
                    RadioNZD.setEnabled(false);
                }
                else if (Objects.equals(Curr, "krw")) {
                    RadioKRW.getParent().requestChildFocus(RadioKRW, RadioKRW);
                    RadioKRW.setChecked(true);
                    RadioKRW.setEnabled(false);
                }
                else if (Objects.equals(Curr, "try")) {
                    RadioTRY.getParent().requestChildFocus(RadioTRY, RadioTRY);
                    RadioTRY.setChecked(true);
                    RadioTRY.setEnabled(false);
                }
                else if (Objects.equals(Curr, "rub")) {
                    RadioRUB.getParent().requestChildFocus(RadioRUB, RadioRUB);
                    RadioRUB.setChecked(true);
                    RadioRUB.setEnabled(false);
                }
                else if (Objects.equals(Curr, "inr")) {
                    RadioINR.getParent().requestChildFocus(RadioINR, RadioINR);
                    RadioINR.setChecked(true);
                    RadioINR.setEnabled(false);
                }
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if(numberPAlerts() != 0 || numberVAlerts() != 0) {
                            DelWarn.setVisibility(View.VISIBLE);
                        }
                        Unit_OK.setEnabled(true);
                        Boolean Dollar_Sel = false;
                        String nonUSD_code = "eur";
                        String nonUSD_symb = "€";

                        switch (checkedId){
                            case R.id.radio_currency_usd:
                                Dollar_Sel = true;
                                break;
                            case R.id.radio_currency_jpy:
                                nonUSD_code = "jpy";
                                nonUSD_symb = "¥";
                                break;
                            case R.id.radio_currency_gbp:
                                nonUSD_code = "gbp";
                                nonUSD_symb = "£";
                                break;
                            case R.id.radio_currency_aud:
                                nonUSD_code = "aud";
                                nonUSD_symb = "A$";
                                break;
                            case R.id.radio_currency_cad:
                                nonUSD_code = "cad";
                                nonUSD_symb = "C$";
                                break;
                            case R.id.radio_currency_chf:
                                nonUSD_code = "chf";
                                nonUSD_symb = "Fr";
                                break;
                            case R.id.radio_currency_cny:
                                nonUSD_code = "cny";
                                nonUSD_symb = "元";
                                break;
                            case R.id.radio_currency_sek:
                                nonUSD_code = "sek";
                                nonUSD_symb = "kr";
                                break;
                            case R.id.radio_currency_nzd:
                                nonUSD_code = "nzd";
                                nonUSD_symb = "NZ$";
                                break;
                            case R.id.radio_currency_krw:
                                nonUSD_code = "krw";
                                nonUSD_symb = "₩";
                                break;
                            case R.id.radio_currency_try:
                                nonUSD_code = "try";
                                nonUSD_symb = "₺";
                                break;
                            case R.id.radio_currency_rub:
                                nonUSD_code = "rub";
                                nonUSD_symb = "\u20BD";
                                break;
                            case R.id.radio_currency_inr:
                                nonUSD_code = "inr";
                                nonUSD_symb = "₹";
                                break;
                        }
                        editor.putString ("Curr_code_tmp",nonUSD_code);
                        editor.putString ("Curr_symb_tmp",nonUSD_symb);
                        editor.putBoolean("Dollar_tmp"   ,Dollar_Sel );
                        editor.apply();

                    }
                });

                Unit_OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        editor.putString ("Curr_code",
                                mSettings.getString("Curr_code_tmp","eur"));
                        editor.putString ("Curr_symb",
                                mSettings.getString("Curr_symb_tmp","€"));
                        editor.putBoolean("Dollar"   ,
                                mSettings.getBoolean("Dollar_tmp", true));
                        editor.apply();

                        dialog2.dismiss();
                        restart();
                    }});
                final Button CurrSel_Dismiss_btn = mView2.findViewById(R.id.Units_NO_btn);
                CurrSel_Dismiss_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog2.dismiss();
                    }
                });
                dialog2.show();
                return true;

            case R.id.action_about:

                builder = new AlertDialog.Builder(BaseActivity.this);
                @SuppressLint("InflateParams")
                View mView3 = getLayoutInflater().inflate(R.layout.about_diag, null);
                builder.setView(mView3);
                final AlertDialog dialog3  = builder.create();
                dialog3.show();
                Button NO = mView3.findViewById(R.id.about_NO_btn);

                TextView Privacy_Policy_tv = mView3.findViewById(R.id.about_link);
                Privacy_Policy_tv.setPaintFlags(Privacy_Policy_tv.getPaintFlags() |
                                                                         Paint.UNDERLINE_TEXT_FLAG);
                Privacy_Policy_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Uri uri = Uri.parse("http://www.poloapps.com/Crypto_Mon_Privacy_Policy.txt");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    }
                });

                NO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog3.dismiss();
                    }
                });
                return true;

            case R.id.action_disclaimer:

                builder = new AlertDialog.Builder(BaseActivity.this);
                @SuppressLint("InflateParams")
                View mView4 = getLayoutInflater().inflate(R.layout.disclaimer_diag, null);
                builder.setView(mView4);
                final AlertDialog dialog4  = builder.create();
                dialog4.show();
                Button Dismiss = mView4.findViewById(R.id.disc_NO_btn);
                TextView Disc_CMC_tv = mView4.findViewById(R.id.disc_coinmarketcap_link);
                Disc_CMC_tv.setPaintFlags(Disc_CMC_tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                Disc_CMC_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Uri uri = Uri.parse("https://coinmarketcap.com/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    dialog4.dismiss();
                    startActivity(intent);
                    }
                });

                Dismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog4.dismiss();
                }});

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    void restart(){
        Intent intent = getIntent();
        intent.putExtra("restart", true);
        finish();
        startActivity(intent);
    }

    void updateCurrentVals(){
        StringRequest crypto100_request = new StringRequest(LC_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String string) {
                        try {
                            JSONArray T100_Array = new JSONArray(string);
                            for (int i = 0; i < T100_Array.length(); i++) {

                                JSONObject obj1   = T100_Array.getJSONObject(i);

                                String rate       = obj1.getString("price_usd");
                                Double d_rate     = Double.parseDouble(rate);
                                Double curr_vol   = Double.parseDouble(
                                                            obj1.getString("24h_volume_usd"));
                                String link_id    = obj1.getString("id");
                                long millis       = System.currentTimeMillis();
                                Integer hours     = (int)(millis/1000/60/60);

                                dbCVHandler.deleteEntry(link_id);
                                dbCVHandler.addCurrentVals(link_id,d_rate,curr_vol,hours);
                            }

                            String priceAlerts    = dbPHandler.dbToString();
                            String[] splitPAlerts = priceAlerts.split("[\n]");

                            int len1 = numberPAlerts();

                            for (int i = 0; i < len1; i++) {
                                double price   = Double.parseDouble(
                                        dbCVHandler.currentPrice(splitPAlerts[i]));
                                double thPrice = Double.parseDouble(
                                        dbPHandler.getPrice_Val(splitPAlerts[i]));
                                int    check   = Integer.parseInt(
                                        dbPHandler.getThresh_Check(splitPAlerts[i]));

                                if ((thPrice < price && check == 1) ||
                                                                (thPrice > price && check == -1)) {

                                    dbPHandler.deleteAlert(splitPAlerts[i]);
                                    if(dbPAchHandler.alertExists(splitPAlerts[i])){
                                        dbPAchHandler.removePriceAchAlert(splitPAlerts[i]);
                                        overwritten++;
                                    }
                                    int cur_mins  = (int) ((System.currentTimeMillis())/1000/60);
                                    dbPAchHandler.addPriceAchAlert(
                                            splitPAlerts[i], price, thPrice, check, cur_mins);
                                }
                            }

                            String   volAlerts    = dbVHandler.dbToString();
                            String[] splitVAlerts = volAlerts.split("[\n]");
                            int len3              = numberVAlerts();

                            for (int j = 0; j < len3; j++) {
                                double vol    = Double.parseDouble(
                                                        dbCVHandler.currentVol(splitVAlerts[j]));
                                double thVol  = Double.parseDouble(
                                                            dbVHandler.getVol_Val(splitVAlerts[j]));
                                int check2 = Integer.parseInt(
                                                       dbVHandler.getThresh_Check(splitVAlerts[j]));
                                if ((thVol < vol && check2 == 1) ||
                                                                    (thVol > vol && check2 == -1)){

                                    dbVHandler.deleteAlert(splitVAlerts[j]);

                                    if(dbVAchHandler.alertExists(splitVAlerts[j])){
                                        dbVAchHandler.removeVolAchAlert(splitVAlerts[j]);
                                        overwritten++;
                                    }
                                    int cur_mins  = (int) ((System.currentTimeMillis())/1000/60);
                                    dbVAchHandler.addVolAchAlert(
                                            splitVAlerts[j], vol, thVol, check2, cur_mins);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue rQueue = Volley.newRequestQueue(BaseActivity.this);
        rQueue.add(crypto100_request);
    }

    void checkStartService(){
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        String   volAlerts    = dbVHandler.dbToString();
        String[] splitVAlerts = volAlerts.split("[\n]");

        if (!splitPAlerts[0].equals("") || !splitVAlerts[0].equals("")){
            startServiceCM();
        }
    }

    void startServiceCM(){
        Intent intent = new Intent(this,serviceCM.class);
        startService(intent);
    }

    void stopServiceCM(){
        Intent intent = new Intent(this,serviceCM.class);
        stopService(intent);
    }

    int numberPAlerts(){
        String priceAlerts    = dbPHandler.dbToString();
        String[] splitPAlerts = priceAlerts.split("[\n]");
        int lenPArray         = splitPAlerts.length;
        if (splitPAlerts[0].equals("")) lenPArray = 0;
        return lenPArray;
    }

    int numberVAlerts(){
        String   volAlerts    = dbVHandler.dbToString();
        String[] splitVAlerts = volAlerts.split("[\n]");
        int lenVArray         = splitVAlerts.length;
        if (splitVAlerts[0].equals("")) lenVArray = 0;
        return lenVArray;
    }

    int numberPAchAlerts(){
        String priceAchieved   = dbPAchHandler.dbEntries();
        String[] splitPAAlerts = priceAchieved.split("[\n]");
        int lenPAchArray       = splitPAAlerts.length;
        if (splitPAAlerts[0].equals("")) lenPAchArray = 0;
        return lenPAchArray;
    }

    int numberVAchAlerts(){
        String volAchieved     = dbVAchHandler.dbEntries();
        String[] splitVAAlerts = volAchieved.split("[\n]");
        int lenVAchArray       = splitVAAlerts.length;
        if (splitVAAlerts[0].equals("")) lenVAchArray = 0;
        return lenVAchArray;
    }

    void stopRunningService(){
        if( isMyServiceRunning(serviceCM.class)){
            Log.i("CM22","service stopping");
            stopServiceCM();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
