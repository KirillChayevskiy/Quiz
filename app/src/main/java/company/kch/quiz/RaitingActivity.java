package company.kch.quiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RaitingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<UserModel> result;
    private UserAdapter adapter;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raiting);

        reference = FirebaseDatabase.getInstance().getReference("Rating");

        result = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);


        recyclerView.setLayoutManager(lim);



        adapter = new UserAdapter(result);
        recyclerView.setAdapter(adapter);

        updateList();

    }


    public void updateList(){

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                int i = dataSnapshot.getValue(UserModel.class).score;
                int j = 0;
                boolean k = true;
                if (result.size() == 0) {
                    result.add(dataSnapshot.getValue(UserModel.class));
                } else if (i < result.get(result.size() - 1).score) {
                    result.add(dataSnapshot.getValue(UserModel.class));
                } else {
                    while (k) {
                        if (i < result.get(j).score) {
                            j++;
                        } else {
                            k = false;
                        }
                    }
                    result.add(j, dataSnapshot.getValue(UserModel.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });
    }


}


