package company.kch.quiz;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ImageView imageView = (ImageView) findViewById(R.id.imageViewTest);


        // Изображение в imageView
        mStorageRef = FirebaseStorage.getInstance().getReference().child("belarus.png");
        Glide.with(TestActivity.this).using(new FirebaseImageLoader()).load(mStorageRef).into(imageView);


        AssetManager assetManager = getApplicationContext().getAssets();
        List<String> createStringList = new ArrayList<>();
        String createStringString = null;
        try {
            createStringList.addAll(Arrays.asList(assetManager.list("Europe")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Добавить инфу в бд
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (int i = 0; i < createStringList.size(); i++) {
            DatabaseReference myRef = database.getReference("Europe/" + i);
            myRef.setValue(createStringList.get(i));
        }


        // Чтение
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference().child("FileNameDB/1");
        final TextView textView = (TextView) findViewById(R.id.textView2);
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                textView.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });












    }
}
