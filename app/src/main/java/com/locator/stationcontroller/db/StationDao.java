package com.locator.stationcontroller.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StationDao {
    @Query("SELECT * FROM Station")
    List<Station> getAll();

    @Query("SELECT * FROM Station WHERE id IN (:ids)")
    List<Station> getAllByIds(Long... ids);

    @Query("SELECT * FROM STATION WHERE phone1=:phone OR phone2=:phone")
    List<Station> getAllByPhone(String phone);

    @Insert
    List<Long> insert(Station... stations);

    @Delete
    int delete(Station... stations);

    @Update
    int update(Station... stations);
}
