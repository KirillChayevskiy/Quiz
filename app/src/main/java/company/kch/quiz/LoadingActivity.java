package company.kch.quiz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    public static final String SELECTED_REGIONS = "selected_regions";

    String[] fileNamesEurope;
    String[] fileNamesAsia;
    String[] fileNamesAfrica;
    String[] fileNamesNorthAmerica;
    String[] fileNamesSouthAmerica;
    String[] fileNamesOceania;
    int checkTest = 0;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    Intent intent;
    TextView textView;
    boolean[] selectedRegions = new boolean[6];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        intent = new Intent(LoadingActivity.this, MainActivity.class);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);

        fileNamesEurope = getArrayFileNamesOfRegion("Europe", getResources().getStringArray(R.array.europe_flags).length);
        fileNamesAsia = getArrayFileNamesOfRegion("Asia", getResources().getStringArray(R.array.asia_flags).length);
        fileNamesAfrica = getArrayFileNamesOfRegion("Africa", getResources().getStringArray(R.array.africa_flags).length);
        fileNamesNorthAmerica = getArrayFileNamesOfRegion("North America", getResources().getStringArray(R.array.north_america_flags).length);
        fileNamesSouthAmerica = getArrayFileNamesOfRegion("South America", getResources().getStringArray(R.array.south_america_flags).length);
        fileNamesOceania = getArrayFileNamesOfRegion("Oceania", getResources().getStringArray(R.array.oceania_flags).length);

    }


    public String[] getArrayFileNamesOfRegion (final String region, int resourcesStringArrayLength) {
        final String[] fileNames = new String[resourcesStringArrayLength];
        for (int i = 0; i < resourcesStringArrayLength; i++) {
            final int finalI = i;
            databaseReference.child(region).child(String.valueOf(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    fileNames[finalI] = dataSnapshot.getValue(String.class);
                    checkTest++;
                    progressBar.setProgress(checkTest);
                    textView.setText("Загрузка");
                    if (checkTest == 194) {
                        intent.putExtra("europe_array", fileNamesEurope);
                        intent.putExtra("asia_array", fileNamesAsia);
                        intent.putExtra("africa_array", fileNamesAfrica);
                        intent.putExtra("north_america_array", fileNamesNorthAmerica);
                        intent.putExtra("south_america_array", fileNamesSouthAmerica);
                        intent.putExtra("oceania_array", fileNamesOceania);
                        for (int i = 0; i < 6; i++){
                            selectedRegions[i] = true;
                        }
                        intent.putExtra(SELECTED_REGIONS, selectedRegions);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    textView.setText("Нет подключения к интернету");
                }
            });
        }
        return fileNames;
    }
}


