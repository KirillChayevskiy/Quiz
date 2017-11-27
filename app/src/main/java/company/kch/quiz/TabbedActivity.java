package company.kch.quiz;

import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity {

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    static TabLayout tabLayout;


    static List<String> allFileNameList = new ArrayList<>();
    static List<String> allFlagsList = new ArrayList<>();

    static List<String> allFileNameListFull = new ArrayList<>();
    static List<String> allFlagsListFull = new ArrayList<>();

    static List<String> randomizedFileNameList = new ArrayList<>();
    static List<String> randomizedFlagsList = new ArrayList<>();


    static boolean[] ready = new boolean[10];
    static boolean[] rightAnswer = new boolean[10];


    static int[] randomAnswerNum = new int[10];
    static int[] answerNum = new int[10];


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);



        allFileNameList.clear();

        AssetManager assetManager = getApplicationContext().getAssets();

        try {
            allFileNameList.addAll(Arrays.asList(assetManager.list("AllNewFlags")));
            allFileNameListFull.addAll(Arrays.asList(assetManager.list("AllNewFlags")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.addAll(allFlagsList, getResources().getStringArray(R.array.flag_names));
        Collections.addAll(allFlagsListFull, getResources().getStringArray(R.array.flag_names));


        randomizedFileNameList.clear();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 8; j++){
                int rand = random.nextInt(allFileNameList.size());
                randomizedFileNameList.add(allFileNameList.get(rand));
                randomizedFlagsList.add(allFlagsList.get(rand));
                allFileNameList.remove(rand);
                allFlagsList.remove(rand);
            }
            randomAnswerNum[i] = random.nextInt(8);
        }



        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




















    public static class PlaceholderFragment extends Fragment {


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }




        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public void colorizeButton(Button button, String color) {
            button.getBackground().setColorFilter(Color.parseColor(color), PorterDuff.Mode.MULTIPLY);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

            final Button[] button = new Button[8];

            int[] btn = new int[]{R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7};

            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);


            AssetManager assets = getActivity().getAssets();

            final Handler handler = new Handler();

            for (int i = 0; i < 8; i++){
                button[i] = (Button) rootView.findViewById(btn[i]);
                button[i].setText(randomizedFlagsList.get((getArguments().getInt(ARG_SECTION_NUMBER) - 1) * 8 + i));

                final int finalI = i;


                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalI == randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]){
                            colorizeButton(button[finalI], "#4CAF50");
                            rightAnswer[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = true;
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setIcon(R.drawable.ic_tab_yes);
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setText("");
                        } else {
                            colorizeButton(button[finalI], "#F44336");
                            colorizeButton(button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]], "#4CAF50");
                            rightAnswer[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = false;
                            answerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = finalI;
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setIcon(R.drawable.ic_tab_no);
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setText("");
                        }
                        ready[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = true;
                        for (int i = 0; i < 8; i++) {
                            button[i].setEnabled(false);
                        }
                        handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        mViewPager.setCurrentItem(getArguments().getInt(ARG_SECTION_NUMBER));
                                    }
                        }, 1500);
                    }
                };
                button[i].getId();
                button[i].setOnClickListener(onClickListener);
            }

            if (ready[getArguments().getInt(ARG_SECTION_NUMBER) - 1]) {
                for (int i = 0; i < 8; i++) {
                    button[i].setEnabled(false);
                }

                if (!rightAnswer[getArguments().getInt(ARG_SECTION_NUMBER) - 1]) {
                    int numnum = answerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1];
                    colorizeButton(button[numnum], "#F44336");
                }
                button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]].getBackground().setColorFilter(Color.parseColor("#4CAF50"), PorterDuff.Mode.MULTIPLY);
            }
            for (int i = 0; i < allFlagsListFull.size(); i++) {
                if (allFlagsListFull.get(i).contains((button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]]).getText())) {
                    String answerStringOfPath = "AllNewFlags/" + allFileNameListFull.get(i);
                    try {
                        imageView.setImageDrawable(Drawable.createFromStream(assets.open(answerStringOfPath), ""));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            TextView textView = (TextView) rootView.findViewById(R.id.textView);
            textView.setText((button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]]).getText());
            textView.setVisibility(View.INVISIBLE);
            return rootView;
        }



    }


    public void clickButton (View view) {
    }










    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 10 total pages.
            return 10;
        }
    }
}
