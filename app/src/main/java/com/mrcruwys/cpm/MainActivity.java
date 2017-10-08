package com.mrcruwys.cpm;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private ArrayList<DBEntry> entries;
    private List<TextView> mEntryKeys;
    private List<TextView> mEntryValues;
    private DBEntryAdapter entryAdapter;
    private TextView mEntryHeading;
    private Button mModify;
    private Button mAdd;
    private int selectedPos = -1;

    private static final int[] KEY_IDS = {
            R.id.txt_pairh1,
            R.id.txt_pairh2,
            R.id.txt_pairh3,
            R.id.txt_pairh4,
            R.id.txt_pairh5,
    };

    private static final int[] VALUE_IDS = {
            R.id.txt_pairv1,
            R.id.txt_pairv2,
            R.id.txt_pairv3,
            R.id.txt_pairv4,
            R.id.txt_pairv5,
    };
    public static final String EXTRA_MESSAGE = "com.mrcruwys.cpm.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entries = new ArrayList<>();

        mEntryKeys = new ArrayList<>(KEY_IDS.length);
        for(int id : KEY_IDS) {
            TextView key = (TextView)findViewById(id);
            mEntryKeys.add(key);
        }

        mEntryValues = new ArrayList<>(VALUE_IDS.length);
        for(int id : VALUE_IDS) {
            TextView values = (TextView)findViewById(id);
            mEntryValues.add(values);
        }
        final TextView mEntryHeading = (TextView)findViewById(R.id.txt_entry_heading);
        mEntryHeading.setPaintFlags(mEntryHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mModify = (Button) findViewById(R.id.btn_modify);
        mModify.setVisibility(View.INVISIBLE);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ModifyActivity.class);
                Bundle b = new Bundle();
                b.putParcelable(EXTRA_MESSAGE, entries.get(selectedPos));
                i.putExtras(b);
                startActivity(i);
            }
        });
        mAdd = (Button) findViewById(R.id.btn_addnew);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ModifyActivity.class);
                startActivity(i);
            }
        });

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
                RecyclerView pwordList = (RecyclerView) findViewById(R.id.pwList);
                LinearLayoutManager pwListManager = new LinearLayoutManager(getApplicationContext());
                pwListManager.setOrientation(LinearLayoutManager.VERTICAL);
                pwordList.setLayoutManager(pwListManager);
                entryAdapter = new DBEntryAdapter(entries);
                pwordList.addItemDecoration(new Divider(getApplicationContext(), LinearLayoutManager.VERTICAL));
                pwordList.setAdapter(entryAdapter);
                entryAdapter.notifyDataSetChanged();
                entryAdapter.setOnDataChangeListener(new DBEntryAdapter.OnDataChangeListener(){
                    public void onDataChanged(int position){
                        mEntryHeading.setText(entries.get(position).getName());
                        mModify.setVisibility(View.VISIBLE);
                        selectedPos = position;
                        for (int i = 0; i < 5; i++) {
                            if (i < entries.get(position).size()) {
                                mEntryKeys.get(i).setText(entries.get(position).getPairKey(i));
                                mEntryValues.get(i).setText(entries.get(position).getPairValue(i));
                            } else {
                                mEntryKeys.get(i).setText("");
                                mEntryValues.get(i).setText("");
                            }
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}