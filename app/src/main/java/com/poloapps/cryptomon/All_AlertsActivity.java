package com.poloapps.cryptomon;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class All_AlertsActivity extends BaseActivity {

    dbPriceHandler  dbPHandler;
    dbVolumeHandler dbVHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__alerts);
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                R.color.dark_gray)));

        dbPHandler = new dbPriceHandler(this, null);
        dbVHandler = new dbVolumeHandler(this, null);
    }

    @Override
    public void onResume() {
        super.onResume();

        final TextView tv1 = findViewById(R.id.tv1);
        final TextView tv2 = findViewById(R.id.tv2);

        tv1.setText(dbPHandler.dbToString());
        tv2.setText(dbVHandler.dbToString());
    }
}
