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


        final Button BitCoin_btn = findViewById(R.id.bitcoin_btn);
        final Button T100_btn = findViewById(R.id.top100_btn);

        BitCoin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent myIntent = new Intent(HomeActivity.this,
                        BitCoin_Activity.class);
                HomeActivity.this.startActivity(myIntent);

            }
        });
        T100_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent myIntent = new Intent(HomeActivity.this,
                        Top_100.class);
                HomeActivity.this.startActivity(myIntent);

            }
        });
    }
}
