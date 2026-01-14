package com.sachintha.posapp;

import android.app.Application;

import com.sachintha.posapp.database.POSDatabase;

/**
 * Application class for POS App
 * Initializes the database and provides global access
 */
public class POSApplication extends Application {

    private static POSApplication instance;
    private POSDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = POSDatabase.getInstance(this);
    }

    public static POSApplication getInstance() {
        return instance;
    }

    public POSDatabase getDatabase() {
        return database;
    }
}
