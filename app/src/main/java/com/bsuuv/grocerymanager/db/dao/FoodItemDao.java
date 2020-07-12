package com.bsuuv.grocerymanager.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bsuuv.grocerymanager.db.entity.FoodItemEntity;

import java.util.List;

@Dao
public interface FoodItemDao {

    @Insert
    void insert(FoodItemEntity foodItem);

    @Delete
    void delete(FoodItemEntity foodItem);

    @Query("SELECT * FROM FoodItemEntity")
    LiveData<List<FoodItemEntity>> getAllFoodItems();

    @Update
    void update(FoodItemEntity foodItem);

    @Query("DELETE FROM FoodItemEntity")
    void deleteAll();
}
