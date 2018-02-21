package com.poloapps.cryptomon;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CryptoSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_select);


    }
}

//        Uri uri = Uri.parse(getString(
//                R.string.cryptos_display_link)
//                + link_id.getText());
//        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//        startActivity(intent);