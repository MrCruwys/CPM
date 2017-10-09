package com.mrcruwys.cpm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ModifyActivity extends AppCompatActivity {

    private DBEntry mEntry;
    private TextView mEntryHeading;
    private List<TextView> mEntryKeys;
    private List<TextView> mEntryValues;
    private Button mUpdate;
    private Button mDelete;
    private Button mAdd;
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
        setContentView(R.layout.activity_modify);
        mEntryHeading = (TextView)findViewById(R.id.txt_entry_title);
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
        mUpdate = (Button) findViewById(R.id.btn_mod_update);
        mDelete = (Button) findViewById(R.id.btn_mod_delete);
        mAdd = (Button) findViewById(R.id.btn_mod_add);
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - Check the user has added valid data (including unique entry name)
                Intent intnt = new Intent();
                Bundle b = new Bundle();
                mEntry = new DBEntry(mEntryHeading.getText().toString());
                for (int i = 0; i < 5; i++){
                    if (!((mEntryKeys.get(i).getText().toString().equals("") || mEntryValues.get(i).getText().toString().equals("")))) {
                        mEntry.addPair(mEntryKeys.get(i).getText().toString(), mEntryValues.get(i).getText().toString());
                    }
                }
                b.putParcelable(ADD_MESSAGE, mEntry);
                intnt.putExtras(b);
                setResult(RESULT_OK, intnt);
                finish();
            }
        });
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mEntry = (DBEntry)extras.get(EXTRA_MESSAGE);
            mEntryHeading.setText(mEntry.getName());
            int i = 0;
            for (DBPair pairs : mEntry.getPairs()) {
                mEntryKeys.get(i).setText(pairs.getKey());
                mEntryValues.get(i).setText(pairs.getValue());
                i++;
            }
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - Check the user actually wants to delete the entry
                    Intent intent = new Intent();
                    intent.putExtra(MODIFY_MESSAGE, "delete");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            mUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO - Check the user has added valid data
                    Intent intnt = new Intent();
                    Bundle b = new Bundle();
                    mEntry = new DBEntry(mEntryHeading.getText().toString());
                    for (int i = 0; i < 5; i++){
                        if (!((mEntryKeys.get(i).getText().toString().equals("") || mEntryValues.get(i).getText().toString().equals("")))) {
                            mEntry.addPair(mEntryKeys.get(i).getText().toString(), mEntryValues.get(i).getText().toString());
                        }
                    }
                    b.putParcelable(ADD_MESSAGE, mEntry);
                    b.putString(MODIFY_MESSAGE, "update");
                    intnt.putExtras(b);
                    setResult(RESULT_OK, intnt);
                    finish();
                }
            });
            mAdd.setEnabled(false);
        }else{
            mUpdate.setEnabled(false);
            mDelete.setEnabled(false);
        }
    }
}
