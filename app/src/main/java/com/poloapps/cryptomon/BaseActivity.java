package com.poloapps.cryptomon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Objects;

/**
 * Created by Marko on 2/25/2018.
 * Base Class
 *
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        final SharedPreferences.Editor editor = mSettings.edit();
        final Boolean Dollar = mSettings.getBoolean("Dollar", true);
        final String  Curr   = mSettings.getString("Curr_code","eur");

        switch (item.getItemId()) {

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

                @SuppressLint("InflateParams")
                View mView2 = getLayoutInflater().inflate(R.layout.currency_diag, null);
                builder.setView(mView2);

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

//                ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
//                sv.scrollTo(0, 100);

                if (Dollar) RadioUSD.setChecked(true);
                else if (Objects.equals(Curr, "eur")) RadioEUR.setChecked(true);
                else if (Objects.equals(Curr, "jpy")) RadioJPY.setChecked(true);
                else if (Objects.equals(Curr, "gbp")) RadioGBP.setChecked(true);
                else if (Objects.equals(Curr, "aud")) RadioAUD.setChecked(true);
                else if (Objects.equals(Curr, "cad")) RadioCAD.setChecked(true);
                else if (Objects.equals(Curr, "chf")) RadioCHF.setChecked(true);
                else if (Objects.equals(Curr, "cny")) RadioCNY.setChecked(true);
                else if (Objects.equals(Curr, "sek")) RadioSEK.setChecked(true);
                else if (Objects.equals(Curr, "nzd")) RadioNZD.setChecked(true);
                else if (Objects.equals(Curr, "krw")) RadioKRW.setChecked(true);
                else if (Objects.equals(Curr, "try")) RadioTRY.setChecked(true);
                else if (Objects.equals(Curr, "rub")) RadioRUB.setChecked(true);
                else if (Objects.equals(Curr, "inr")) RadioINR.setChecked(true);

                Button Unit_OK = mView2.findViewById(R.id.Units_OK_btn);
                Unit_OK.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        Boolean Dollar_Sel = false;
                        String nonUSD_code = "eur";
                        String nonUSD_symb = "€";

                        if (RadioUSD.isChecked()) Dollar_Sel = true;

                        else if (RadioJPY.isChecked()) {
                            nonUSD_code = "jpy";
                            nonUSD_symb = "¥";
                        }
                        else if (RadioGBP.isChecked()) {
                            nonUSD_code = "gbp";
                            nonUSD_symb = "£";
                        }
                        else if (RadioAUD.isChecked()) {
                            nonUSD_code = "aud";
                            nonUSD_symb = "A$";
                        }
                        else if (RadioCAD.isChecked()) {
                            nonUSD_code = "cad";
                            nonUSD_symb = "C$";
                        }
                        else if (RadioCHF.isChecked()) {
                            nonUSD_code = "chf";
                            nonUSD_symb = "Fr";
                        }
                        else if (RadioCNY.isChecked()) {
                            nonUSD_code = "cny";
                            nonUSD_symb = "元";
                        }
                        else if (RadioSEK.isChecked()) {
                            nonUSD_code = "sek";
                            nonUSD_symb = "kr";
                        }
                        else if (RadioNZD.isChecked()) {
                            nonUSD_code = "nzd";
                            nonUSD_symb = "NZ$";
                        }
                        else if (RadioKRW.isChecked()) {
                            nonUSD_code = "krw";
                            nonUSD_symb = "₩";
                        }
                        else if (RadioTRY.isChecked()) {
                            nonUSD_code = "try";
                            nonUSD_symb = "₺";
                        }
                        else if (RadioRUB.isChecked()) {
                            nonUSD_code = "rub";
                            nonUSD_symb = "\u20BD";
                        }
                        else if (RadioINR.isChecked()) {
                            nonUSD_code = "inr";
                            nonUSD_symb = "₹";
                        }

                        editor.putString("Curr_code",nonUSD_code);
                        editor.putString("Curr_symb",nonUSD_symb);
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
