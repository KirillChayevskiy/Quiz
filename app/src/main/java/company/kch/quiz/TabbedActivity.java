package company.kch.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class TabbedActivity extends AppCompatActivity {

    public static final String ALL_FILE_NAME_LIST = "allFileNameList";
    public static final String DATA_BASE_ARRAY = "dataBaseArray";
    public static final String SELECTED_REGIONS = "selected_regions";

    public static final String EUROPE_ARRAY = "europe_array";
    public static final String ASIA_ARRAY = "asia_array";
    public static final String AFRICA_ARRAY = "africa_array";
    public static final String NORTH_AMERICA_ARRAY = "north_america_array";
    public static final String SOUTH_AMERICA_ARRAY = "south_america_array";
    public static final String OCEANIA_ARRAY = "oceania_array";
    public static final String ALL_FLAGS_LIST = "allFlagsList";
    public static final String RANDOMIZED_FLAGS_LIST = "randomizedFlagsList";
    public static final String RANDOMIZED_FILE_NAME_LIST = "randomizedFileNameList";
    public static final String ALL_FLAGS_LIST_FULL = "allFlagsListFull";
    public static final String ALL_FILE_NAME_LIST_FULL = "allFileNameListFull";
    public static final String READY_BOOLEAN = "readyBoolean";
    public static final String RIGHT_ANSWER_BOOLEAN = "rightAnswerBoolean";
    public static final String RANDOM_ANSWER_NUM = "randomAnswerNum";
    public static final String ANSWER_NUM = "answerNum";
    public static final String AUTO_PAGING = "autoPaging";

    String[] allStringsOfRegions = new String[]{EUROPE_ARRAY, ASIA_ARRAY, AFRICA_ARRAY, NORTH_AMERICA_ARRAY, SOUTH_AMERICA_ARRAY, OCEANIA_ARRAY};
    int[] regionStringsIDs = new int[]{R.array.europe_flags, R.array.asia_flags, R.array.africa_flags, R.array.north_america_flags, R.array.south_america_flags, R.array.oceania_flags};
    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;

    static TabLayout tabLayout;

    static int countRightAnswer;
    static List<String> allFileNameList = new ArrayList<>();
    static List<String> allFlagsList = new ArrayList<>();

    static List<String> allFileNameListFull = new ArrayList<>();
    static List<String> allFlagsListFull = new ArrayList<>();

    static List<String> randomizedFileNameList = new ArrayList<>();
    static List<String> randomizedFlagsList = new ArrayList<>();

    static AlertDialog.Builder builder;
    static boolean[] ready = new boolean[10];
    static boolean[] rightAnswer = new boolean[10];

    static Intent intent;

    static int[] randomAnswerNum = new int[10];
    static int[] answerNum = new int[10];

    static int loadNum;

    final String SAVED_NUM = "saved_num";

    private static StorageReference mStorageRef;

    boolean[] selectedRegions = new boolean[6];

    static boolean autoPaging;

    static int testInt = 0;



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);





        autoPaging = getIntent().getBooleanExtra(AUTO_PAGING, true);

        //сброс
        for (int i = 0; i < 10; i++) {
            ready[i] = false;
        }
        countRightAnswer = 0;

        intent = new Intent(TabbedActivity.this, MainActivity.class);
        builder = new AlertDialog.Builder(TabbedActivity.this);

        for (int i = 0; i < 6; i++) {
            intent.putExtra(allStringsOfRegions[i], getIntent().getStringArrayExtra(allStringsOfRegions[i]));
            intent.putExtra(SELECTED_REGIONS, selectedRegions);
        }


        allFileNameListFull.clear();
        allFlagsListFull.clear();
        randomizedFileNameList.clear();
        randomizedFlagsList.clear();


        loadNum = getIntent().getIntExtra(SAVED_NUM, 8);
        selectedRegions = getIntent().getBooleanArrayExtra(SELECTED_REGIONS);




        for (int i = 0; i < 6; i++) {
            if (selectedRegions[i]){
                addValuesToArrays(regionStringsIDs[i], allStringsOfRegions[i]);
                addValuesToFullArrays(regionStringsIDs[i], allStringsOfRegions[i]);
            }

        }

        if (selectedRegions[0] || selectedRegions[1]) {
            allFileNameListFull.add("russia.png");
            allFileNameListFull.add("cyprus.png");
            Collections.addAll(allFlagsListFull, getResources().getStringArray(R.array.other_flags));
        }

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            allFileNameList.clear();
            allFlagsList.clear();
            for (int k = 0; k < 6; k++) {
                if (selectedRegions[k]) {
                    addValuesToArrays(regionStringsIDs[k], allStringsOfRegions[k]);
                    if (selectedRegions[0] || selectedRegions[1]) {
                        allFileNameList.add("russia.png");
                        allFileNameList.add("cyprus.png");
                        Collections.addAll(allFlagsList, getResources().getStringArray(R.array.other_flags));
                    }
                }
            }
            for (int j = 0; j < loadNum; j++) {
                int rand = random.nextInt(allFileNameList.size());
                randomizedFileNameList.add(allFileNameList.get(rand));
                randomizedFlagsList.add(allFlagsList.get(rand));
                allFlagsList.remove(rand);
                allFileNameList.remove(rand);
            }
            if (i > 0) {
                boolean uniq = false;
                while (!uniq) {
                    randomAnswerNum[i] = random.nextInt(loadNum);
                    for (int k = 0; k < i; k++) {
                        if (randomizedFlagsList.get(k * loadNum + randomAnswerNum[k]).equals(randomizedFlagsList.get(i * loadNum + randomAnswerNum[i]))) {
                            uniq = false;
                            break;
                        } else uniq = true;
                    }
                }
            }
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(10);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                testInt++;
            }
        }, 0, 100);
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


    public void addValuesToArrays(int resourceStringArray, String intentStringArrayExtra) {
        Collections.addAll(allFlagsList, getResources().getStringArray(resourceStringArray));
        Collections.addAll(allFileNameList, getIntent().getStringArrayExtra(intentStringArrayExtra));

    }

    public void addValuesToFullArrays(int resourceStringArray, String intentStringArrayExtra) {
        Collections.addAll(allFileNameListFull, getIntent().getStringArrayExtra(intentStringArrayExtra));
        Collections.addAll(allFlagsListFull, getResources().getStringArray(resourceStringArray));
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

        public boolean checkReady() {
            for (boolean b : ready) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

            final Button[] button = new Button[8];

            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

            int[] btn = new int[]{R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7};
            for (int i = 0; i < 8; i++) {
                button[i] = (Button) rootView.findViewById(btn[i]);
            }
            switch (loadNum) {
                case 6:
                    button[0].setVisibility(View.GONE);
                    button[1].setVisibility(View.GONE);
                    btn = new int[]{R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7};
                    break;
                case 4:
                    button[0].setVisibility(View.GONE);
                    button[2].setVisibility(View.GONE);
                    button[4].setVisibility(View.GONE);
                    button[6].setVisibility(View.GONE);
                    btn = new int[]{R.id.button1, R.id.button3, R.id.button5, R.id.button7};
                    break;
                case 2:
                    button[0].setVisibility(View.GONE);
                    button[1].setVisibility(View.GONE);
                    button[2].setVisibility(View.GONE);
                    button[3].setVisibility(View.GONE);
                    button[4].setVisibility(View.GONE);
                    button[6].setVisibility(View.GONE);
                    button[5].setTextSize(25);
                    button[7].setTextSize(25);
                    btn = new int[]{R.id.button5, R.id.button7};
                    break;
            }
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView);


            AssetManager assets = getActivity().getAssets();

            final Handler handler = new Handler();

            for (int i = 0; i < loadNum; i++) {
                button[i] = (Button) rootView.findViewById(btn[i]);
                button[i].setText(randomizedFlagsList.get((getArguments().getInt(ARG_SECTION_NUMBER) - 1) * loadNum + i));
                button[i].setVisibility(View.INVISIBLE);

                final int finalI = i;


                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finalI == randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]) {
                            colorizeButton(button[finalI], "#4CAF50");
                            rightAnswer[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = true;
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setIcon(R.drawable.ic_tab_yes);
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setText("");
                            countRightAnswer++;
                        } else {
                            colorizeButton(button[finalI], "#F44336");
                            colorizeButton(button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]], "#4CAF50");
                            rightAnswer[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = false;
                            answerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = finalI;
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setIcon(R.drawable.ic_tab_no);
                            tabLayout.getTabAt(getArguments().getInt(ARG_SECTION_NUMBER) - 1).setText("");
                        }
                        ready[getArguments().getInt(ARG_SECTION_NUMBER) - 1] = true;
                        for (int i = 0; i < loadNum; i++) {
                            button[i].setEnabled(false);
                        }
                        if (autoPaging) {
                            for (int i = getArguments().getInt(ARG_SECTION_NUMBER) - 1; i < 10; i++) {
                                if (!ready[i]){
                                    final int finalI1 = i;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mViewPager.setCurrentItem(finalI1);
                                        }
                                    }, 1200);
                                    break;
                                } else {
                                    for (int j = getArguments().getInt(ARG_SECTION_NUMBER) - 1; j > 0 ; j--) {
                                        if (!ready[j]){
                                            final int finalJ = j;
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mViewPager.setCurrentItem(finalJ);
                                                }
                                            }, 1200);
                                            break;
                                        }
                                    }
                                }
                            }

                        }


                        if (checkReady()) {
                            builder.setTitle("Результат")
                                    .setMessage("Правильных ответов: " + countRightAnswer + "\n"
                                            +"Test: " + testInt + "\n")
                                    .setCancelable(false)
                                    .setNegativeButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    startActivity(intent);
                                                }
                                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                };
                button[i].getId();
                button[i].setOnClickListener(onClickListener);
            }
            for (int i = 0; i < allFlagsListFull.size(); i++) {

                if (allFlagsListFull.get(i).contains((button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]]).getText())) {
                    mStorageRef = FirebaseStorage.getInstance().getReference().child(allFileNameListFull.get(i));
                    Glide.with(this)
                            .using(new FirebaseImageLoader())
                            .load(mStorageRef)
                            .listener(new RequestListener<StorageReference, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progressBar.setVisibility(View.GONE);
                                    for (int i = 0; i < loadNum; i++) {
                                        button[i].setVisibility(View.VISIBLE);
                                    }
                                    return false;
                                }
                            })
                            .into(imageView);
                    break;
                }
            }
            return rootView;
        }


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
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                TabbedActivity.this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Таки да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                for (int i = 0; i < 6; i++) {
                    intent.putExtra(allStringsOfRegions[i], getIntent().getStringArrayExtra(allStringsOfRegions[i]));
                    intent.putExtra(SELECTED_REGIONS, selectedRegions);
                }
                startActivity(intent);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }
}
