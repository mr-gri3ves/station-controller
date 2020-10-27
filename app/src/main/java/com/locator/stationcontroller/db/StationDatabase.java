package com.locator.stationcontroller.db;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Station.class}, version = 1)
public abstract class StationDatabase extends RoomDatabase {
    private static final String TAG = StationDatabase.class.getSimpleName();
    private static StationDatabase instance;

    public abstract StationDao stationDao();

    public static void init(@NonNull Application application) {
        if (instance != null) {
            Log.e(TAG, "Db is already initialized");
            return;
        }
        synchronized (StationDatabase.class) {
            instance = Room.databaseBuilder(application, StationDatabase.class, "station-database").build();
        }
    }

    public static StationDatabase db() {
        if (instance == null) {
            throw new IllegalStateException(TAG + " is not initialized");
        }
        return instance;
    }
}
