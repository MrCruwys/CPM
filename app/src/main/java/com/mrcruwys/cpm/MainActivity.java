package com.mrcruwys.cpm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private DBEntryAdapter entryAdapter;
    //private TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        entries = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Entries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dsAllEntries) {
                for (DataSnapshot dsSingleEntry : dsAllEntries.getChildren()) {
                    final DBEntry tEntry = new DBEntry(dsSingleEntry.getKey());
                    for (DataSnapshot dsFields : dsSingleEntry.getChildren()) {
                        tEntry.addPair(dsFields.getKey(), (String)dsFields.getValue());
                    }
                    entries.add(tEntry);
                }
                // All entries loaded...go ahead and do your stuff!
                RecyclerView pwordList = (RecyclerView) findViewById(R.id.pwList);
                LinearLayoutManager pwListManager = new LinearLayoutManager(getApplicationContext());
                pwListManager.setOrientation(LinearLayoutManager.VERTICAL);
                pwordList.setLayoutManager(pwListManager);
                pwordList.setItemAnimator(new DefaultItemAnimator());
                entryAdapter = new DBEntryAdapter(entries);
                pwordList.setAdapter(entryAdapter);
                entryAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}