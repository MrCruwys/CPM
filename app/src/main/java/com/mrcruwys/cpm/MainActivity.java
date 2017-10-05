package com.mrcruwys.cpm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<DBEntry> entries;
    private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mText = (TextView) findViewById(R.id.text_temp);

        entries = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Entries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snpSht : dataSnapshot.getChildren()) {
                    final DBEntry tempEntry = new DBEntry(snpSht.getKey());
                    for (DataSnapshot entry : snpSht.getChildren()) {
                        tempEntry.addPair(entry.getKey(), (String)entry.getValue());
                    }
                    entries.add(tempEntry);
                }
                // All entries loaded...go ahead and do your stuff!
                //mText.setText(entries.get(0).getName());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}