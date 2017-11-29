package company.kch.quiz;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    final String SAVED_NUM = "saved_num";

    private static final String TAG = "MainActivity";
    private AdView mAdView;

    Dialog dialog;
    Intent intent;
    int toIntent = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Реклама
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dialog = new Dialog(MainActivity.this);

    }

    public void buttonClick(View view) {
        intent = new Intent(MainActivity.this, TabbedActivity.class);
        intent.putExtra(SAVED_NUM, toIntent);
        startActivity(intent);
    }

    public void buttonSettingsClick(View view) {
        dialog.setContentView(R.layout.settings_dialog);
        dialog.setCancelable(false);
        final TextView textView = (TextView) dialog.findViewById(R.id.textView);
        textView.setText(String.format("%d", toIntent));
        SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
        seekBar.setMax(3);
        seekBar.setProgress((toIntent - 2)/2);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                textView.setText(String.valueOf(2 + progress * 2));
            }
        });
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonOk) {
                    toIntent = Integer.parseInt(textView.getText().toString());
                }
                dialog.cancel();
            }
        };
        buttonOk.setOnClickListener(onClickListener);
        buttonCancel.setOnClickListener(onClickListener);
        dialog.show();
    }

    public void buttonRaitingClick(View view) {

    }

    public void buttonTestClick(View view) {

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        toIntent = savedInstanceState.getInt("toIntent");
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("toIntent", toIntent);
    }
}