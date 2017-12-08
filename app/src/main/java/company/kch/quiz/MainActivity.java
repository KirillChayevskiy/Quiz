package company.kch.quiz;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static final String USER_ID = "userID";
    //Google Registration in Firebase
    SignInButton signInButton;
    Button signOutButton;
    private GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    EditText editTextGoogleName;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "MAIN_ACTIVITY";

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

    String userID = null;

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

        //Google SignIn
        mAuth = FirebaseAuth.getInstance();
        signInButton = findViewById(R.id.signInButton);
        signOutButton = findViewById(R.id.signOutButton);
        editTextGoogleName = findViewById(R.id.editText);
        // Configure Google Sign In
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Rating");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    signInButton.setVisibility(View.INVISIBLE);
                    signOutButton.setVisibility(View.VISIBLE);
                    editTextGoogleName.setVisibility(View.VISIBLE);
                    myRef.child(mAuth.getUid()).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(String.class) == null) myRef.child(mAuth.getUid()).child("name").setValue(mAuth.getCurrentUser().getDisplayName());
                            editTextGoogleName.setHint(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    userID = mAuth.getUid();
                }
            }
        };
        editTextGoogleName.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (editTextGoogleName.getText().length() < 4) {
                        showToast("Слишком короткое имя");
                    } else {
                        myRef.child(mAuth.getUid()).child("name").setValue(editTextGoogleName.getText().toString());
                        editTextGoogleName.setText("");
                    }
                    String strCatName = editTextGoogleName.getText().toString();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException ignored) {

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void onClickExitButton (View view) {
        FirebaseAuth.getInstance().signOut();
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.INVISIBLE);
        userID = null;
        editTextGoogleName.setVisibility(View.INVISIBLE);
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
            intent.putExtra(USER_ID, userID);
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