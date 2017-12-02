package company.kch.quiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

    public static final String DATA_BASE_ARRAY = "dataBaseArray";
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

    String[] dbArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Реклама
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        dialog = new Dialog(MainActivity.this);

        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference().child("FileNameDB");
        dbArray = new String[196];
        for (int i = 0; i < 196; i++) {
            final int finalI = i;
            myRef2.child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dbArray[finalI] = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                }
            });
        }
        for (int i = 0; i < 6; i++) {
            selectRegion[i] = true;
        }
        //selectRegion = getIntent().getBooleanArrayExtra(SELECTED_REGIONS);



        //tested
        //intent = new Intent(MainActivity.this, TabbedActivity.class);


        //


    }

    public void buttonClick(View view) {
        if (dbArray[195] != null) {
            intent = new Intent(MainActivity.this, TabbedActivity.class);
            intent.putExtra(SAVED_NUM, toIntent);
            intent.putExtra(DATA_BASE_ARRAY, dbArray);
            if (selectRegion[0])  intent.putExtra(EUROPE_ARRAY, getIntent().getStringArrayExtra(EUROPE_ARRAY));
            if (selectRegion[1]) intent.putExtra(ASIA_ARRAY, getIntent().getStringArrayExtra(ASIA_ARRAY));
            if (selectRegion[2]) intent.putExtra(AFRICA_ARRAY, getIntent().getStringArrayExtra(AFRICA_ARRAY));
            if (selectRegion[3]) intent.putExtra(NORTH_AMERICA_ARRAY, getIntent().getStringArrayExtra(NORTH_AMERICA_ARRAY));
            if (selectRegion[4]) intent.putExtra(SOUTH_AMERICA_ARRAY, getIntent().getStringArrayExtra(SOUTH_AMERICA_ARRAY));
            if (selectRegion[5]) intent.putExtra(OCEANIA_ARRAY, getIntent().getStringArrayExtra(OCEANIA_ARRAY));
            intent.putExtra(SELECTED_REGIONS, selectRegion);
            intent.putExtra(AUTO_PAGING, autoPaging);
            startActivity(intent);
        } else {
            showToast("Повторите попытку позже");
        }
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

        final CheckBox checkBoxEurope = (CheckBox) dialog.findViewById(R.id.checkBoxEurope);
        final CheckBox checkBoxAsia = (CheckBox) dialog.findViewById(R.id.checkBoxAsia);
        final CheckBox checkBoxAfrica = (CheckBox) dialog.findViewById(R.id.checkBoxAfrica);
        final CheckBox checkBoxNothrAmerica = (CheckBox) dialog.findViewById(R.id.checkBoxNothrAmerica);
        final CheckBox checkBoxSouthAmerica = (CheckBox) dialog.findViewById(R.id.checkBoxSouthAmerica);
        final CheckBox checkBoxOceania = (CheckBox) dialog.findViewById(R.id.checkBoxOceania);

        checkBoxEurope.setChecked(selectRegion[0]);
        checkBoxAsia.setChecked(selectRegion[1]);
        checkBoxAfrica.setChecked(selectRegion[2]);
        checkBoxNothrAmerica.setChecked(selectRegion[3]);
        checkBoxSouthAmerica.setChecked(selectRegion[4]);
        checkBoxOceania.setChecked(selectRegion[5]);

        final Switch switch1 = (Switch) dialog .findViewById(R.id.switch1);
        switch1.setChecked(autoPaging);


        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.buttonOk) {
                    toIntent = Integer.parseInt(textView.getText().toString());
                    selectRegion[0] = checkBoxEurope.isChecked();
                    selectRegion[1] = checkBoxAsia.isChecked();
                    selectRegion[2] = checkBoxAfrica.isChecked();
                    selectRegion[3] = checkBoxNothrAmerica.isChecked();
                    selectRegion[4] = checkBoxSouthAmerica.isChecked();
                    selectRegion[5] = checkBoxOceania.isChecked();
                    boolean check = false;
                    for (int i = 0; i < 6; i++) {
                        if (selectRegion[i]) {
                            check = true;
                            break;
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