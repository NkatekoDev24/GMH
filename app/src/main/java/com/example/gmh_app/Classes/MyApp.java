package com.example.gmh_app.Classes;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Firebase offline persistence (called only once)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
