package company.kch.quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    Intent intent;
    RadioGroup radioGroup;
    RadioButton radioButton;
    SharedPreferences sharedPreferences;
    final String SAVED_NUM = "saved_num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        intent = new Intent(SettingsActivity.this, MainActivity.class);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);


    }

    public void onClickOk (View view) {
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        int num = Integer.parseInt(radioButton.getText().toString());
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(SAVED_NUM, string);
//        editor.apply();
        intent.putExtra(SAVED_NUM, num);
        startActivity(intent);
    }

    public void onClickCancel (View view) {
        startActivity(intent);
    }
}
