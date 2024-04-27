package com.example.weather.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import com.example.weather.model.City;

import java.util.List;

import androidx.room.Query;

@Dao
public interface CityDao {
    @Query("Select * from city order by cid desc")
    List<City> getAllCity();

    @Insert
    long insert(City city);

    @Delete
    void delete(City city);
}
