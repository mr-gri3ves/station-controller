package com.locator.stationcontroller;

import android.app.Application;

import com.locator.stationcontroller.db.StationDatabase;
import com.locator.stationcontroller.db.StationRepository;

public class StationController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StationDatabase.init(this);
        StationRepository.init(StationDatabase.db());
    }
}
