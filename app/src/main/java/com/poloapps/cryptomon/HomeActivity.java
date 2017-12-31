package com.poloapps.cryptomon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        final Button Agree_btn = findViewById(R.id.bitcoin_btn);


        Agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent myIntent = new Intent(HomeActivity.this,
                        BitCoin_Activity.class);
                HomeActivity.this.startActivity(myIntent);

            }
        });

    }
}
