package com.example.gmh_app.Classes;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Force app to always use light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
