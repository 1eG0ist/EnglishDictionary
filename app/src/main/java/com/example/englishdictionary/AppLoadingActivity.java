package com.example.englishdictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class AppLoadingActivity extends AppCompatActivity {

    private int isRegistered;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_loading);

        WorkWithDB db = new WorkWithDB(this);
        isRegistered = db.getOnlineUser();

        if (isRegistered == 0) {
            intent = new Intent(this, WelcomeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}