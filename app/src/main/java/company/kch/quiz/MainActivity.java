package company.kch.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    final String SAVED_NUM = "saved_num";

    private static final String TAG = "MainActivity";
    private AdView mAdView;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Реклама
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        builder = new AlertDialog.Builder(MainActivity.this);

    }

    public void buttonClick(View view) {
        Intent intent = new Intent(MainActivity.this, TabbedActivity.class);
        intent.putExtra(SAVED_NUM, getIntent().getIntExtra(SAVED_NUM, 8));
        startActivity(intent);
    }

    public void buttonSettingsClick(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void buttonRaitingClick(View view) {

    }
}