package com.mrcruwys.cpm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

@SuppressWarnings("ALL")
public class LoginActivity extends AppCompatActivity {

    // DECLARE ALL MEMBER FIELDS THAT MATCH XML ELEMENTS
    private TextView mPasscode;             // the password field
    private TextView mMessage;              // the error message field
    private List<Button> mKeys;             // all the keys the user can press
    private Button mClear;                  // the clear button
    private Button mEnter;                  // the enter button

    // DECLARE ALL OTHER MEMBER FIELDS
    private DatabaseReference mDatabase;    // the link to our Firebase database

    // ALL OTHER DECLARATIONS GO HERE
    public static final String EXTRA_MESSAGE = "com.mrcruwys.cpm.MESSAGE";
    private static final int[] KEY_IDS = {
            R.id.btn_e,
            R.id.btn_t,
            R.id.btn_a,
            R.id.btn_o,
            R.id.btn_n,
            R.id.btn_r,
            R.id.btn_i,
            R.id.btn_s,
            R.id.btn_h,
            R.id.btn_zero,
            R.id.btn_one,
            R.id.btn_two,
            R.id.btn_hash,
            R.id.btn_star,
            R.id.btn_at,
            R.id.btn_percent,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // INITIALISE ALL FIELDS
        mPasscode = (TextView) findViewById(R.id.txt_passcode);
        mMessage = (TextView) findViewById(R.id.txt_message);
        mKeys = new ArrayList<>(KEY_IDS.length);
        for(int id : KEY_IDS) {
            Button button = (Button)findViewById(id);
            button.setOnClickListener(myListener);
            mKeys.add(button);
        }
        mClear = (Button) findViewById(R.id.btn_clear);
        mEnter = (Button) findViewById(R.id.btn_enter);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // CREATE CLICKLISTENERS
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPasscode();
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasscode.setText(getResources().getString(R.string.empty));
            }
        });
    }

    private View.OnClickListener myListener = new View.OnClickListener() {
        public void onClick(View v) {
            Button b = (Button)v;
            String x = b.getText().toString();
            mPasscode.append(x);
        }
    };

    private void submitPasscode(){

        // read the passcode from the database
        mDatabase.child("Login").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String passcode = (String)dataSnapshot.getValue();
                String attemptedCode = mPasscode.getText().toString();
                if (passcode.equals(attemptedCode)) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, attemptedCode);
                    startActivity(intent);
                } else {
                    mPasscode.setText(getResources().getString(R.string.empty));
                    mMessage.setText(getResources().getString(R.string.incorrect));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
