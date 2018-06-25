package com.example.ilya2.rob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onClickStart(View view) {
        startActivity(new Intent(StartActivity.this,GameActivity.class));
    }

    public void onClickSettings(View view) {
        startActivity(new Intent(StartActivity.this,SettingsActivity.class));
    }
}
