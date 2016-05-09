package com.example.parmindr.sunshine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Invoked onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d(LOG_TAG, "Invoked onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOG_TAG, "Invoked onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(LOG_TAG, "Invoked onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LOG_TAG, "Invoked onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "Invoked onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(LOG_TAG, "Invoked onRestart");
        super.onRestart();
    }
}
