package com.example.eets_nostredame.getit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Eets_Nostredame on 28/03/2018.
 */


public class FiltersActivity extends AppCompatActivity {

    public static final String TAG = "FiltersActivity";

    //widgets
    private Button mSave;
    private EditText mCity, mState;
    private ImageView mBackArrow;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        mBackArrow = findViewById(R.id.backArrow);
        mCity = findViewById(R.id.input_city);
        mState = findViewById(R.id.input_state_province);
        mSave = findViewById(R.id.btnSave);

        init();



    }

    private void init() {
        getFilterPreferences();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Saving");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FiltersActivity.this);
                SharedPreferences.Editor editor = preferences.edit();

                Log.d(TAG, "onClick: city " + mCity.getText().toString());
                // KEY, VALUE
                editor.putString(getString(R.string.preferences_city),mCity.getText().toString());
                editor.apply();

                Log.d(TAG, "onClick: state " + mState.getText().toString());
                // KEY, VALUE
                editor.putString(getString(R.string.preferences_state),mState.getText().toString());
                editor.apply();

                Toast.makeText(FiltersActivity.this, "Saving complete", Toast.LENGTH_SHORT).show();
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Navigating back");
                finish();
            }
        });
    }

    private void getFilterPreferences() {
        Log.d(TAG, "getFilterPreferences: retreiving saved preferences");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String state = preferences.getString(getString(R.string.preferences_state),"");
        String city = preferences.getString(getString(R.string.preferences_city),"");

        mState.setText(state);
        mCity.setText(city);

    }
}
