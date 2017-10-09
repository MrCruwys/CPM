package com.mrcruwys.cpm;

import android.app.Activity;
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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // MEMBER FIELD DECLARATIONS
    private DatabaseReference mDatabase;
    private ArrayList<DBEntry> entries;
    private List<TextView> mEntryKeys;
    private List<TextView> mEntryValues;
    private RecyclerView pwordList;
    private DBEntryAdapter entryAdapter;
    private TextView mEntryHeading;
    private Button mModify;
    private Button mAdd;
    private int selectedPos = -1;

    // MEMBER CONSTANT DECLARATIONS
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
    public static final String ADD_MESSAGE = "com.mrcruwys.cpm.ADD";
    public static final String MODIFY_MESSAGE = "com.mrcruwys.cpm.MODIFY";
    private static final int ADD_REQUEST = 1;
    private static final int EDIT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // CREATE THE ENTRY HEADING LINK AND ENSURE IT'S UNDERLINED
        mEntryHeading = (TextView)findViewById(R.id.txt_entry_heading);
        mEntryHeading.setPaintFlags(mEntryHeading.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // CREATE AN ARRAY REFERENCING THE KEY AND VALUE TEXTBOXES
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

        // WHEN THE USER CHOOSES TO EDIT THE SELECTED ENTRY...
        mModify = (Button) findViewById(R.id.btn_modify);
        mModify.setVisibility(View.INVISIBLE);
        mModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ModifyActivity.class);
                Bundle b = new Bundle();

                // PASS THE MODIFY ACTIVITY THE SELECTED ENTRY
                b.putParcelable(EXTRA_MESSAGE, entries.get(selectedPos));
                i.putExtras(b);
                startActivityForResult(i, EDIT_REQUEST);
            }
        });

        // WHEN THE USER CHOOSES TO ADD A NEW ENTRY...
        mAdd = (Button) findViewById(R.id.btn_addnew);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ModifyActivity.class);
                startActivityForResult(i, ADD_REQUEST);
            }
        });

        // CREATE A LINK BETWEEN THIS ANDROID APP AND THE FIREBASE DATABASE ONLINE
        entries = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Entries").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dsAllEntries) {

                // GO THROUGH EACH DB ENTRY AND PLACE IT IN OUR MAIN DATA STRUCTURE "entries"
                for (DataSnapshot dsSingleEntry : dsAllEntries.getChildren()) {
                    final DBEntry tEntry = new DBEntry(dsSingleEntry.getKey());
                    for (DataSnapshot dsFields : dsSingleEntry.getChildren()) {
                        tEntry.addPair(dsFields.getKey(), (String)dsFields.getValue());
                    }
                    entries.add(tEntry);
                }

                // NOW PLACE OUR PARSED ENTRIES INTO THE RECYCLERVIEW WITH AN ADAPTER
                pwordList = (RecyclerView) findViewById(R.id.pwList);
                LinearLayoutManager pwListManager = new LinearLayoutManager(getApplicationContext());
                pwListManager.setOrientation(LinearLayoutManager.VERTICAL);
                pwordList.setLayoutManager(pwListManager);
                entryAdapter = new DBEntryAdapter(entries);
                pwordList.addItemDecoration(new Divider(getApplicationContext(), LinearLayoutManager.VERTICAL));
                pwordList.setAdapter(entryAdapter);
                entryAdapter.notifyDataSetChanged();

                // CREATE LISTENER FOR WHEN A RECYCLERVIEW ITEM IS CLICKED
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // IF USER IS LOOKING TO ADD A NEW ENTRY
        if (requestCode == ADD_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                AddEntry((DBEntry)data.getExtras().get(ADD_MESSAGE));
            }
        } else if(requestCode == EDIT_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                String tempStr = (String)data.getExtras().get(MODIFY_MESSAGE);

                // IF USER IS LOOKING TO DELETE AN ENTRY
                if (tempStr.equals("delete")) {
                    if (selectedPos != -1) { // Is there any way this could actually be -1???
                        DeleteEntry(selectedPos);
                    }

                    // IF USER IS LOOKING TO UPDATE AN ENTRY
                } else {
                    if (selectedPos != -1) { // Is there any way this could actually be -1???
                        DeleteEntry(selectedPos);
                        AddEntry((DBEntry)data.getExtras().get(ADD_MESSAGE));
                    }
                }
            }
        }
    }

    private void AddEntry(DBEntry entryToAdd) {
        mDatabase.child("Entries").child(entryToAdd.getName()).setValue("");
        for (DBPair pairs : entryToAdd.getPairs()) {
            mDatabase.child("Entries").child(entryToAdd.getName()).child(pairs.getKey()).setValue(pairs.getValue());
        }
        int i = 0;
        boolean found = false;
        while ((!found) && (i < entries.size())){
            if (entries.get(i).getName().compareToIgnoreCase(entryToAdd.getName()) > 0) {
                found = true;
            } else {
                i++;
            }
        }
        entries.add(i, entryToAdd);
        entryAdapter.notifyItemInserted(i);
        pwordList.scrollToPosition(0);
        ClearSelected();
    }

    private void DeleteEntry(int position) {
        mDatabase.child("Entries").child(entries.get(position).getName()).setValue(null);
        entries.remove(position);
        pwordList.removeViewAt(position);
        entryAdapter.notifyItemRemoved(position);
        entryAdapter.notifyItemRangeChanged(position, entries.size());
        pwordList.scrollToPosition(0);
        ClearSelected();
    }

    private void ClearSelected(){
        mEntryHeading.setText(R.string.entry_heading);
        mModify.setVisibility(View.GONE);
        selectedPos = -1;
        for (int i = 0; i < 5; i++) {
            mEntryKeys.get(i).setText("");
            mEntryValues.get(i).setText("");
        }
    }
}