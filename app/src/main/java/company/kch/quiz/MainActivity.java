package company.kch.quiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static final String TO_INTENT = "toIntent";
    public static final String EUROPE_ARRAY = "europe_array";
    public static final String ASIA_ARRAY = "asia_array";
    public static final String AFRICA_ARRAY = "africa_array";
    public static final String NORTH_AMERICA_ARRAY = "north_america_array";
    public static final String SOUTH_AMERICA_ARRAY = "south_america_array";
    public static final String OCEANIA_ARRAY = "oceania_array";
    public static final String SELECTED_REGIONS = "selected_regions";
    public static final String AUTO_PAGING = "autoPaging";
    final String SAVED_NUM = "saved_num";

    boolean[] selectRegion = new boolean[6];

    private AdView mAdView;

    Dialog dialog;
    Intent intent;
    int toIntent = 8;

    boolean autoPaging = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Реклама
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //Setting dialog
        dialog = new Dialog(MainActivity.this);

        for (int i = 0; i < 6; i++) {
            selectRegion[i] = true;
        }
        selectRegion = getIntent().getBooleanArrayExtra(SELECTED_REGIONS);

    }

    public void buttonClick(View view) {
            intent = new Intent(MainActivity.this, TabbedActivity.class);
            intent.putExtra(SAVED_NUM, toIntent);
            intent.putExtra(EUROPE_ARRAY, getIntent().getStringArrayExtra(EUROPE_ARRAY));
            intent.putExtra(ASIA_ARRAY, getIntent().getStringArrayExtra(ASIA_ARRAY));
            intent.putExtra(AFRICA_ARRAY, getIntent().getStringArrayExtra(AFRICA_ARRAY));
            intent.putExtra(NORTH_AMERICA_ARRAY, getIntent().getStringArrayExtra(NORTH_AMERICA_ARRAY));
            intent.putExtra(SOUTH_AMERICA_ARRAY, getIntent().getStringArrayExtra(SOUTH_AMERICA_ARRAY));
            intent.putExtra(OCEANIA_ARRAY, getIntent().getStringArrayExtra(OCEANIA_ARRAY));
            intent.putExtra(SELECTED_REGIONS, selectRegion);
            intent.putExtra(AUTO_PAGING, autoPaging);
            startActivity(intent);
    }

    public void buttonSettingsClick(View view) {
        dialog.setContentView(R.layout.settings_dialog);
        dialog.setCancelable(false);
        final TextView textView = (TextView) dialog.findViewById(R.id.textView);
        textView.setText(String.valueOf(toIntent));
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
        final CheckBox[] checkBoxes = new CheckBox[6];
        int[] checkBoxIDs = new int[] {R.id.checkBoxEurope, R.id.checkBoxAsia, R.id.checkBoxAfrica, R.id.checkBoxNothrAmerica, R.id.checkBoxSouthAmerica, R.id.checkBoxOceania};
        for (int i =0; i < 6; i++) {
            checkBoxes[i] = dialog.findViewById(checkBoxIDs[i]);
            checkBoxes[i].setChecked(selectRegion[i]);
        }

        final Switch switch1 = (Switch) dialog .findViewById(R.id.switch1);
        switch1.setChecked(autoPaging);


        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonOk) {
                    toIntent = Integer.parseInt(textView.getText().toString());
                    boolean check = false;
                    for (int i = 0; i < 6; i++) {
                        selectRegion[i] = checkBoxes[i].isChecked();
                        if (selectRegion[i]) {
                            check = true;
                        }
                    }
                    autoPaging = switch1.isChecked();
                    if (check) dialog.cancel();
                    else showToast("Не выбрано ниодного региона");
                } else dialog.cancel();
            }
        };
        buttonOk.setOnClickListener(onClickListener);
        buttonCancel.setOnClickListener(onClickListener);
        dialog.show();
    }

    public void buttonRaitingClick(View view) {
        intent = new Intent(MainActivity.this, RaitingActivity.class);
        startActivity(intent);
    }

    public void buttonTestClick(View view) {
        intent = new Intent(MainActivity.this, TestActivity.class);
        startActivity(intent);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        toIntent = savedInstanceState.getInt(TO_INTENT);
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TO_INTENT, toIntent);
    }


    //Текстовое уведомление
    public void showToast(String message) {
        //создаем и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.TOP, 0, 80);
        toast.show();
    }
}