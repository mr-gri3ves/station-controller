package com.locator.stationcontroller.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StationDao {
    @Insert
    List<Long> insert(Station... stations);

    @Query("SELECT * FROM Station")
    List<Station> getAll();
}
