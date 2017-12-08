package company.kch.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class TabbedActivity extends AppCompatActivity {

    public static final String SELECTED_REGIONS = "selected_regions";
    public static final String EUROPE_ARRAY = "europe_array";
    public static final String ASIA_ARRAY = "asia_array";
    public static final String AFRICA_ARRAY = "africa_array";
    public static final String NORTH_AMERICA_ARRAY = "north_america_array";
    public static final String SOUTH_AMERICA_ARRAY = "south_america_array";
    public static final String OCEANIA_ARRAY = "oceania_array";
    public static final String AUTO_PAGING = "autoPaging";
    public static final String USER_ID = "userID";

    String[] allStringsOfRegions = new String[]{EUROPE_ARRAY, ASIA_ARRAY, AFRICA_ARRAY, NORTH_AMERICA_ARRAY, SOUTH_AMERICA_ARRAY, OCEANIA_ARRAY};
    int[] regionStringsIDs = new int[]{R.array.europe_flags, R.array.asia_flags, R.array.africa_flags, R.array.north_america_flags, R.array.south_america_flags, R.array.oceania_flags};

    public static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ViewPager mViewPager;
    static TabLayout tabLayout;

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

    static String userID;

    private static StorageReference mStorageRef;

    boolean[] selectedRegions = new boolean[6];

    static boolean autoPaging;

    static int time;
    static int diffLevel = 0;
    static double hintUse = 1;
    static int previousScore = 0;
    static int countRightAnswer;


    static FloatingActionButton fab50x50;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        autoPaging = getIntent().getBooleanExtra(AUTO_PAGING, true);

        fab50x50 = findViewById(R.id.floatingActionButton);

        //сброс
        for (int i = 0; i < 10; i++) {
            ready[i] = false;
        }
        countRightAnswer = 0;
        time = 0;

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

        userID = getIntent().getStringExtra(USER_ID);


        loadNum = getIntent().getIntExtra(SAVED_NUM, 8);
        selectedRegions = getIntent().getBooleanArrayExtra(SELECTED_REGIONS);


        for (int i = 0; i < 6; i++) {
            if (selectedRegions[i]) {
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
                }
            }
            if (selectedRegions[0] || selectedRegions[1]) {
                allFileNameList.add("russia.png");
                allFileNameList.add("cyprus.png");
                Collections.addAll(allFlagsList, getResources().getStringArray(R.array.other_flags));
            }
            diffLevel = allFlagsList.size();
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
            } else randomAnswerNum[i] = random.nextInt(loadNum);
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.setOffscreenPageLimit(10);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time++;
            }
        }, 0, 1000);
        if (userID != null) {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rating").child(userID).child("score");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue(Integer.class) == null) previousScore = 0;
                     else previousScore = dataSnapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        for (int i = 0; i < 6; i++) {
            intent.putExtra(allStringsOfRegions[i], getIntent().getStringArrayExtra(allStringsOfRegions[i]));
            intent.putExtra(SELECTED_REGIONS, selectedRegions);
        }


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

        Button[] button = new Button[8];
        int[] btn = new int[]{R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7};


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
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                 final Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);


            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

            for (int i = 0; i < 8; i++) {
                button[i] = rootView.findViewById(btn[i]);
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
                    fab50x50.setVisibility(View.INVISIBLE);
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

            final int[] finalBtn = btn;

            fab50x50.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Integer> fiftyFifty = new ArrayList<>();
                    for (int i = 0; i < loadNum; i++) {
                        fiftyFifty.add(i);
                    }
                    fiftyFifty.remove(randomAnswerNum[mViewPager.getCurrentItem()]);
                    Random random = new Random();
                    for (int i = 0; i < (loadNum / 2) - 1; i++) {
                        fiftyFifty.remove(random.nextInt(fiftyFifty.size()));
                    }
                    for (int i = 0; i < loadNum; i++) {
                        button[i] = mViewPager.getChildAt(mViewPager.getCurrentItem()).findViewById(finalBtn[i]);
                    }
                    for (int i = 0; i < fiftyFifty.size(); i++) {
                        button[fiftyFifty.get(i)].setVisibility(View.INVISIBLE);
                    }
                    fab50x50.setVisibility(View.INVISIBLE);
                    hintUse = 0.95;
                }
            });

            ImageView imageView = rootView.findViewById(R.id.imageView);

            for (int i = 0; i < loadNum; i++) {
                button[i] = rootView.findViewById(btn[i]);
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
                            final Handler handler = new Handler();
                            for (int i = getArguments().getInt(ARG_SECTION_NUMBER) - 1; i < 10; i++) {
                                if (!ready[i]) {
                                    final int finalI1 = i;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mViewPager.setCurrentItem(finalI1);
                                        }
                                    }, 1000);
                                    break;
                                } else {
                                    for (int j = getArguments().getInt(ARG_SECTION_NUMBER) - 1; j > 0; j--) {
                                        if (!ready[j]) {
                                            final int finalJ = j;
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mViewPager.setCurrentItem(finalJ);
                                                }
                                            }, 1000);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if (checkReady()) {

                            final int resultScore = (int) ((double) countRightAnswer / time * diffLevel * ((double) loadNum / 10 + 1) * hintUse * 100);
                            if (userID != null && previousScore < resultScore) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Rating").child(userID).child("score");
                                reference.setValue(resultScore);
                                builder.setTitle("Новый рекорд!");
                            } else builder.setTitle("Результат");

                            builder.setMessage("Правильных ответов: " + countRightAnswer + "\n"
                                            + "Очки: " + resultScore +"\n"
                                            + "Время: " + time + "\n");
                            builder.setCancelable(true);
                            builder.setPositiveButton("Главное меню",
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
            mStorageRef = FirebaseStorage.getInstance().getReference();
            for (int i = 0; i < allFlagsListFull.size(); i++) {
                if (allFlagsListFull.get(i).contains((button[randomAnswerNum[getArguments().getInt(ARG_SECTION_NUMBER) - 1]]).getText())) {
                    Glide.with(this)
                            .using(new FirebaseImageLoader())
                            .load(mStorageRef.child(allFileNameListFull.get(i)))
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

    public static boolean checkReady() {
        for (boolean b : ready) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (checkReady()) startActivity(intent);
        else openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                TabbedActivity.this);
        quitDialog.setTitle("Закончить игру?");
        quitDialog.setMessage("Весь прогресс будет утерян!");
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

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

    //Текстовое уведомление
    public void showToast(String message) {
        //создаем и отображаем текстовое уведомление
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.TOP, 0, 80);
        toast.show();
    }
}
