package com.mrcruwys.cpm;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends AppCompatActivity {

    // MEMBER FIELD DECLARATIONS
    private DBEntry mEntry;
    private TextView mEntryHeading;
    private List<TextView> mEntryKeys;
    private List<TextView> mEntryValues;
    private Button mUpdate;
    private Button mDelete;
    private Button mAdd;
    private Bundle extras;
    private String errorMsg = "";
    private Fix fix = new Fix();

    // CONSTANT DECLARATIONS
    public static final String EXTRA_MESSAGE = "com.mrcruwys.cpm.MESSAGE";
    public static final String MODIFY_MESSAGE = "com.mrcruwys.cpm.MODIFY";
    public static final String ADD_MESSAGE = "com.mrcruwys.cpm.ADD";
    private static final int[] KEY_IDS = {
            R.id.txt_fieldk1,
            R.id.txt_fieldk2,
            R.id.txt_fieldk3,
            R.id.txt_fieldk4,
            R.id.txt_fieldk5,
    };
    private static final int[] VALUE_IDS = {
            R.id.txt_fieldv1,
            R.id.txt_fieldv2,
            R.id.txt_fieldv3,
            R.id.txt_fieldv4,
            R.id.txt_fieldv5,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_modify);

        // INITIALISE ALL XML FIELDS
        mEntryHeading = (TextView) findViewById(R.id.txt_entry_title);
        mEntryKeys = new ArrayList<>(KEY_IDS.length);
        for (int id : KEY_IDS) {
            TextView key = (TextView) findViewById(id);
            mEntryKeys.add(key);
        }
        mEntryValues = new ArrayList<>(VALUE_IDS.length);
        for (int id : VALUE_IDS) {
            TextView values = (TextView) findViewById(id);
            mEntryValues.add(values);
        }
        mUpdate = (Button) findViewById(R.id.btn_mod_update);
        mDelete = (Button) findViewById(R.id.btn_mod_delete);
        mAdd = (Button) findViewById(R.id.btn_mod_add);

        // WHEN THE ADD ENTRY BUTTON IS SELECTED...
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDataChange("add");
            }
        });
        extras = getIntent().getExtras();

        // IF AN ENTRY WAS PASSED INTO THIS ACTIVITY
        if (extras != null) {

            // EXTRACT ELEMENTS OF PASSED ENTRY AND DISPLAY THEM
            mEntry = (DBEntry) extras.get(EXTRA_MESSAGE);
            mEntryHeading.setText(mEntry.getName());
            int i = 0;
            for (DBPair pairs : mEntry.getPairs()) {
                mEntryKeys.get(i).setText(pairs.getKey());
                mEntryValues.get(i).setText(pairs.getValue());
                i++;
            }

            // IF THE USER HAS SELECTED THE DELETE BUTTON
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // CHECK THAT THE USER ACTUALLY WANTS TO DELETE THE SELECTED ENTRY
                    AlertDialog.Builder alert = new AlertDialog.Builder(ModifyActivity.this);
                    alert.setTitle("Delete");
                    alert.setMessage("Are you sure you want to delete the \"" + mEntry.getName() + "\" entry ?");

                    // USER HAS CONFIRMED THEY WANT TO DELETE THIS PERSON
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.putExtra(MODIFY_MESSAGE, "delete");
                            setResult(RESULT_OK, intent);
                            finish();
                            dialog.dismiss();
                        }
                    });

                    // USER HAS DECIDED THEY DO NOT WANT TO DELETE THIS PERSON
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }
            });

            // IF THE USER HAS SELECTED THE UPDATE BUTTON
            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptDataChange("update");
                }
            });
            mAdd.setEnabled(false);
        } else {
            mUpdate.setEnabled(false);
            mDelete.setEnabled(false);
        }
    }

    // THIS METHOD CHECKS IF THE DATA ATTEMPTING TO BE INSERTED IS VALID
    private void attemptDataChange(final String validateType) {

        // CHECK IF ENTRY NAME IS UNIQUE
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        DatabaseReference users = root.child("Entries");
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                errorMsg = "";
                if (snapshot.child(fix.en(mEntryHeading.getText().toString(), true)).exists()) { // TODO - sanitize

                    // IF WE ARE ADDING A NEW ENTRY WHO'S NAME CLASHES
                    if (extras == null) {
                        errorMsg = "There is already an entry in the database with the name \"" + mEntryHeading.getText().toString() + "\"";
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        // IF THE USER IS TRYING TO CHANGE AN ENTRIES NAME AND IT CLASHES
                    } else if (!(mEntry.getName().equals(mEntryHeading.getText().toString()))) {
                        errorMsg = "There is already an entry in the database with the name \"" + mEntryHeading.getText().toString() + "\"";
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                if (errorMsg.equals("")) {
                    // CHECK IF ANY PAIR WITH A KEY/VALUE ACTUALLY CONTAINS BOTH PIECES OF DATA
                    for (int i = 0; i < 5; i++) {
                        if (mEntryKeys.get(i).getText().toString().equals("") ^ mEntryValues.get(i).getText().toString().equals("")) {
                            errorMsg = "One or more of your pairs contains an empty key or value.";
                        }
                    }
                    if (!(errorMsg.equals(""))) {
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else {

                        // CHECK IF EACH KEY HAS A UNIQUE NAME
                        for (int i = 0; i < 4; i++) {
                            for (int j = i + 1; j < 5; j++) {
                                if (!(mEntryKeys.get(i).getText().toString().equals(""))) {
                                    if (mEntryKeys.get(i).getText().toString().equals(mEntryKeys.get(j).getText().toString())) {
                                        errorMsg = "You have duplicate row headers, each key within an entry must be unique.";
                                    }
                                }
                            }
                        }
                        if (!(errorMsg.equals(""))) {
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        } else {

                            // EVERYTHING IS FINE, GO AHEAD AND MAKE THE REQUESTED CHANGE
                            Intent intnt = new Intent();
                            Bundle b = new Bundle();
                            mEntry = new DBEntry(mEntryHeading.getText().toString());
                            for (int i = 0; i < 5; i++) {
                                if (!(mEntryKeys.get(i).getText().toString().equals(""))) {
                                    mEntry.addPair(mEntryKeys.get(i).getText().toString(), mEntryValues.get(i).getText().toString());
                                }
                            }
                            b.putParcelable(ADD_MESSAGE, mEntry);
                            if (validateType.equals("update")) {
                                b.putString(MODIFY_MESSAGE, "update");
                            }
                            intnt.putExtras(b);
                            setResult(RESULT_OK, intnt);
                            finish();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}